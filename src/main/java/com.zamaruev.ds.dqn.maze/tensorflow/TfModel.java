package com.zamaruev.ds.dqn.maze.tensorflow;

import com.zamaruev.ds.dqn.maze.objects.Agent;
import com.zamaruev.ds.dqn.maze.objects.Exit;
import com.zamaruev.ds.dqn.maze.objects.Maze;
import org.tensorflow.Graph;
import org.tensorflow.Output;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.util.List;

/**
 * Created by vzamaruiev on 1/24/17.
 */
public class TfModel {

    public Graph buildModel() {
        Graph g = new Graph();
        GraphBuilder b = new GraphBuilder(g);

        Output visible = b.placeholder("maze", new float[5][5][2]);
        Output flat = b.reshape(visible, b.constant("flatten", new int[]{-1, 50}));
        Output weights = b.truncatedNormal(
                b.constant("weights_hidden1", new int[]{50, 8})
        );

        Output hidden1 = b.add("hidden1",
                b.matmul(flat, weights),
                b.constant("bias", new float[8])
        );

        Output activation1 = b.relu("activation1", hidden1);

        Output hidden2 = b.add("y",
                b.matmul(activation1, b.truncatedNormal(b.constant("weights_action", new int[]{8, 4}))),
                b.constant("action_bias", new float[4])
        );

        return g;
    }

    /**
     * Run model and return predicted awards for each action index.
     */
    public float[] runModel(Graph g, Maze maze) {
        try (Session s = new Session(g)) {
            Tensor result = s.runner().feed("maze", toTensor(maze)).fetch("y").run().get(0);
            float[][] actions = new float[1][4];
            result.copyTo(actions);
            return actions[0];
        }
    }

    public Tensor toTensor(Maze maze) {
        float[][][] matrix = new float[maze.getX()][maze.getY()][2];
        for (int x = 0; x < maze.getX(); x++) {
            for (int y = 0; y < maze.getY(); y++) {
                if (maze.getTiles()[x][y] instanceof Agent) {
                    matrix[x][y][0] = 1;
                } else if (maze.getTiles()[x][y] instanceof Exit) {
                    matrix[x][y][1] = 1;
                }
            }
        }
        return Tensor.create(matrix);
    }

    public void trainModel(Graph g, List<Maze> states, float[][] correctedPredictions) {
        GraphBuilder b = new GraphBuilder(g);

        Output y = g.operation("y").output(0);
        Output y_ = b.constant("y_", correctedPredictions);
        Output loss = b.reduce_mean(b.square(b.sub(y, y_)));

        Output train_step = b.adam(loss);

        try (Session s = new Session(g)) {
            Tensor result = s.runner().fetch("y").run().get(0);

        }

    }

}

