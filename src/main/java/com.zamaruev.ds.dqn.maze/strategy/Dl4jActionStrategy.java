package com.zamaruev.ds.dqn.maze.strategy;

import com.zamaruev.ds.commons.CircularObservations;
import com.zamaruev.ds.dqn.maze.action.Action;
import com.zamaruev.ds.dqn.maze.action.ActionMaker;
import com.zamaruev.ds.dqn.maze.dl4j.Dl4jModel;
import com.zamaruev.ds.dqn.maze.objects.Maze;
import com.zamaruev.ds.dqn.maze.tensorflow.Step;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import java.util.*;

import static com.zamaruev.ds.dqn.maze.utils.ArrayUtils.maxIndex;
import static com.zamaruev.ds.dqn.maze.utils.ArrayUtils.maxValue;
import static java.util.stream.Collectors.toList;

@Slf4j
@Setter
@Getter
public class Dl4jActionStrategy implements ActionStrategy {

    public static final int BATCH = 32;
    private final Random random = new Random();

    private final ActionMaker actionMaker = new ActionMaker();
    private final RandomActionStrategy randomStrategy = new RandomActionStrategy();

    private final Dl4jModel dl4j = new Dl4jModel();
    private final CircularObservations<Step> steps = new CircularObservations<>(BATCH * 5000);
    private final CircularObservations<Step> finishSteps = new CircularObservations<>(BATCH * 1000);

    private MultiLayerNetwork model = dl4j.buildModel();
    private double randomThreshold = 2.0d;
    private double minRandThreshold = 0.1d;

    private Double loss;

    @Override
    public Action next(Maze maze) {
        Action action = null;
        if (randomThreshold > random.nextDouble()) {
            action = randomStrategy.next(maze);
        } else {
            float[] rewards = dl4j.runModel(model, maze);
            int actionIndex = maxIndex(rewards);
            action = Action.values()[actionIndex];
        }

        saveStep(maze, action);
        trainModel();
        adjustRandomThreshold();

        return action;
    }

    private void adjustRandomThreshold() {
        if (randomThreshold > minRandThreshold) {
            randomThreshold -= 0.00001;
        }
    }

    /**
     * Save step for training purposes.
     * Step includes original state, action, reward for the action and new state.
     */
    private void saveStep(Maze maze, Action action) {
        Maze initialState = maze.clone();
        Maze finalState = maze.clone();

        float reward = -0.5f;
        if (actionMaker.makeAction(finalState, action)) {
            reward = finalState.isSolved() ? 1 : -0.1f;
        };

        if(finalState.isSolved()) {
            finishSteps.add(new Step(initialState, action, reward, finalState));
        } else {
            steps.add(new Step(initialState, action, reward, finalState));
        }

    }

    /**
     * Train model using recorded steps.
     */
    private void trainModel() {
        float learningRate = 0.8f;
        float discount = 0.95f;

        if (finishSteps.size() < BATCH * 50) {
            return;
        }

        List<Step> sample = steps.takeSample(3 * BATCH / 4);
        sample.addAll(finishSteps.takeSample(BATCH / 4));
        float[][] correctedPredictions = new float[BATCH][Action.values().length];
        for (int i = 0; i < BATCH; i++) {
            // SARSA formula
            // Q = Q + learning rate * (action reward + discount * maxQ' - Q)

            // predict Q for the initial state
            correctedPredictions[i] = dl4j.runModel(model, sample.get(i).getInitialState());

            // Action index
            int action = Action.indexOf(sample.get(i).getAction());

            // Q(s, a)
            float q = correctedPredictions[i][action];

            // calculate max Q for next state, if we are not in the terminal state
            float maxQ = 0;
            // is terminal action
            boolean isTerminalAction = sample.get(i).getNewState().isSolved();
            if (!isTerminalAction) {
                // predict possible future awards
                float[] futureAwards = dl4j.runModel(model, sample.get(i).getNewState());
                maxQ = maxValue(futureAwards);
            }

            // known award from the step
            float actionAward = sample.get(i).getAward();

            correctedPredictions[i][action] = q + learningRate * (actionAward + discount * maxQ - q);
        }

        loss = dl4j.trainModel(model, sample.stream().map(Step::getInitialState).collect(toList()), correctedPredictions);
    }

}
