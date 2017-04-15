package com.zamaruev.ds.dqn.maze;

import com.zamaruev.ds.dqn.maze.action.Action;
import com.zamaruev.ds.dqn.maze.action.ActionMaker;
import com.zamaruev.ds.dqn.maze.dl4j.Dl4jModel;
import com.zamaruev.ds.dqn.maze.generator.MazeGenerator;
import com.zamaruev.ds.dqn.maze.objects.Maze;
import com.zamaruev.ds.dqn.maze.objects.Tile;
import com.zamaruev.ds.dqn.maze.strategy.ActionStrategy;
import com.zamaruev.ds.dqn.maze.strategy.RandomActionStrategy;
import com.zamaruev.ds.dqn.maze.tensorflow.Step;
import com.zamaruev.ds.dqn.maze.utils.ArrayUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.*;
import java.util.*;

import static com.zamaruev.ds.dqn.maze.utils.ArrayUtils.maxValue;
import static java.util.stream.Collectors.toList;

@Slf4j
public class ReadMovesApp {

    private static final int BATCH = 32;
    private static final int SAMPLES = 100000;

    private static final List<Step> steps = new ArrayList<>(SAMPLES);
    private static final List<Step> finishedSteps = new ArrayList<>(SAMPLES);
    private static final Random random = new Random(123123);
    private static final Dl4jModel dl4j = new Dl4jModel();
    private static final MultiLayerNetwork model = dl4j.buildModel();
    private static double lastScore = -1;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int stepsRead = 0;

        try(FileInputStream dump = new FileInputStream("maze-runner.ser")) {
            try (ObjectInputStream dumpStream = new ObjectInputStream(dump)) {
                while(stepsRead++ < SAMPLES) {
                    int[][][] initialState = (int[][][]) dumpStream.readObject();
                    Maze initialMaze = Maze.fromMatrix(initialState);

                    Action action = (Action) dumpStream.readObject();

                    int[][][] finalState = (int[][][]) dumpStream.readObject();
                    Maze finalMaze = Maze.fromMatrix(finalState);

                    float reward;

                    if (Arrays.deepEquals(initialState, finalState)) {
                        reward = -0.1f;
                    } else if (finalMaze.getExit() == null) {
                        reward = 1;
                    } else {
                        reward = -0.01f;
                    }

                    if(finalMaze.getExit() == null ) {
                        finishedSteps.add(new Step(initialMaze, action, reward, finalMaze));
                    } else {
                        steps.add(new Step(initialMaze, action, reward, finalMaze));
                    }
                }
            }
        }

        log.info("Start training, ongoing steps: {}, finished steps: {}", steps.size(), finishedSteps.size());

        for(int i = 0; i < 10*SAMPLES; i++) {
            trainModel();
        }

        log.info("Stop training: " + model.score());
    }



    /**
     * Train model using recorded steps.
     */
    private static void trainModel() {
        float learningRate = 0.8f;
        float discount = 0.95f;

        List<Step> sample = takeSample(3 * BATCH / 4, steps);
        sample.addAll(takeSample(BATCH / 4, finishedSteps));

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
            boolean isTerminalAction = steps.get(i).getNewState().isSolved();
            if (!isTerminalAction) {
                // predict possible future awards
                float[] futureAwards = dl4j.runModel(model, sample.get(i).getNewState());
                maxQ = maxValue(futureAwards);
            }

            // known award from the step
            float actionAward = sample.get(i).getAward();

            correctedPredictions[i][action] = q + learningRate * (actionAward + discount * maxQ - q);
        }
        dl4j.trainModel(model, sample.stream().map(Step::getInitialState).collect(toList()), correctedPredictions);
    }


    /**
     * Take random sample from steps.
     */
    private static List<Step> takeSample(int i, List<Step> source) {
        Set<Integer> indexes = new HashSet<>();
        while (indexes.size() < i) {
            indexes.add(random.nextInt(source.size()));
        }
        return indexes.stream().map(source::get).collect(toList());
    }

}
