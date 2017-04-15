package com.zamaruev.ds.dqn.maze.dl4j;

import com.zamaruev.ds.dqn.maze.objects.Agent;
import com.zamaruev.ds.dqn.maze.objects.Brick;
import com.zamaruev.ds.dqn.maze.objects.Exit;
import com.zamaruev.ds.dqn.maze.objects.Maze;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.util.ArrayUtil;

import java.util.ArrayList;
import java.util.List;

public class Dl4jModel {


    public MultiLayerNetwork buildModel() {
        double learningRate = 0.0001;
        int numInputs = 75;
        int numOutputs = 4;
        int numHiddenNodes = 64;

        //Initialize the user interface backend
        UIServer uiServer = UIServer.getInstance();
        //Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
        StatsStorage statsStorage = new InMemoryStatsStorage();         //Alternative: new FileStatsStorage(File), for saving and loading later
        //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
        uiServer.attach(statsStorage);

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .iterations(1)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .learningRate(learningRate)
                .updater(Updater.ADAM)
                .momentum(0.9)
                .seed(123123)
//                .l2(0.001)
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(numInputs)
                        .nOut(numHiddenNodes)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.LEAKYRELU)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.IDENTITY)
                        .nIn(numHiddenNodes )
                        .nOut(numOutputs)
                        .build())
                .pretrain(false)
                .backprop(true)
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();

        model.setListeners(new StatsListener(statsStorage));
        model.setListeners(new ScoreIterationListener(1000));

        return model;
    }

    public double trainModel(MultiLayerNetwork model, List<Maze> batch, float[][] correctedPredictions) {
        List<DataSet> samples = new ArrayList<>();
        for (int i = 0; i < batch.size(); i++) {
            DataSet sample = new DataSet(flatten(batch.get(i)), Nd4j.create(correctedPredictions[i]));
            samples.add(sample);
        }
        model.fit(new ListDataSetIterator(samples));
        return model.score();
    }

    public float[] runModel(MultiLayerNetwork model, Maze maze) {
        INDArray flatten = flatten(maze);
        INDArray output = model.output(flatten, false);

        float[] scores = new float[output.columns()];
        for (int i = 0; i < output.columns(); i++) {
            scores[i] = output.getFloat(i);
        }
        return scores;
    }

    private INDArray flatten(Maze maze) {

        double[] flat = ArrayUtil.flattenDoubleArray(maze.toDoubleMatrix());
        return Nd4j.create(flat);
    }

}

