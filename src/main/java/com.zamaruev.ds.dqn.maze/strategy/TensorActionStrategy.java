package com.zamaruev.ds.dqn.maze.strategy;

import com.zamaruev.ds.dqn.maze.action.Action;
import com.zamaruev.ds.dqn.maze.action.ActionMaker;
import com.zamaruev.ds.dqn.maze.objects.Maze;
import com.zamaruev.ds.dqn.maze.tensorflow.Step;
import com.zamaruev.ds.dqn.maze.tensorflow.TfModel;
import org.tensorflow.Graph;

import java.util.*;

import static com.zamaruev.ds.dqn.maze.utils.ArrayUtils.maxIndex;
import static com.zamaruev.ds.dqn.maze.utils.ArrayUtils.maxValue;
import static java.util.stream.Collectors.toList;

public class TensorActionStrategy implements ActionStrategy {

    public static final int BATCH = 32;
    private final Random random = new Random();

    private final ActionMaker actionMaker = new ActionMaker();
    private final RandomActionStrategy randomStrategy = new RandomActionStrategy();

    private final TfModel tf = new TfModel();
    private final Graph model = tf.buildModel();
    private final List<Step> steps = new ArrayList<>();

    private double randomThreshold = 1.0d;
    private int totalSteps;

    @Override
    public Action next(Maze maze) {
        Action action = null;
        if (randomThreshold > random.nextDouble()) {
            action = randomStrategy.next(maze);
        } else {
            float[] rewards = tf.runModel(model, maze);
            int actionIndex = maxIndex(rewards);
            action = Action.values()[actionIndex];
        }

        saveStep(maze, action);
        trainModel();
        adjustRandomThreshold();

        return action;
    }

    private void adjustRandomThreshold() {
        if (randomThreshold <= 0.05) {
            randomThreshold = 0.05;
        } else {
            randomThreshold = 1 - totalSteps * 0.0001; // fully switch on 10 000 steps
        }
    }

    /**
     * Save step for training purposes.
     * Step includes original state, action, reward for the action and new state.
     */
    private void saveStep(Maze maze, Action action) {
        totalSteps++;
        // TODO: clean up steps

        Maze initialState = maze.clone();
        Maze clone = maze.clone();

        int reward = -10;
        if (actionMaker.makeAction(clone, action)) {
            reward = clone.isSolved() ? 100 : -1;
        }

        steps.add(new Step(initialState, action, reward, clone));
    }

    /**
     * Take random sample from steps.
     */
    private List<Step> takeSample(int i) {
        Set<Integer> indexes = new HashSet<>();
        while (indexes.size() < i) {
            indexes.add(random.nextInt(steps.size()));
        }

        return indexes.stream().map(steps::get).collect(toList());
    }

    /**
     * Train model using recorded steps.
     */
    private void trainModel() {
        if (steps.size() < BATCH * 10) {
            return;
        }
        List<Step> sample = takeSample(BATCH);
        float[][] correctedPredictions = new float[BATCH][4];
        for (int i = 0; i < BATCH; i++) {
            // predict awards for the initial state to fill matrix
            correctedPredictions[i] = tf.runModel(model, sample.get(i).getInitialState());

            // known award from the step
            int action = Action.indexOf(sample.get(i).getAction());
            float actionAward = sample.get(i).getAward();

            if (!steps.get(i).getNewState().isSolved()) {
                // predict possible future awards
                float[] futureAwards = tf.runModel(model, sample.get(i).getNewState());
                actionAward += maxValue(futureAwards);
            }

            correctedPredictions[i][action] = actionAward;
        }


        tf.trainModel(model, sample.stream().map(Step::getInitialState).collect(toList()), correctedPredictions);
    }


}
