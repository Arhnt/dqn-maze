package com.zamaruev.ds.dqn.maze;

import com.zamaruev.ds.dqn.maze.action.ActionMaker;
import com.zamaruev.ds.dqn.maze.generator.MazeGenerator;
import com.zamaruev.ds.dqn.maze.objects.Maze;
import com.zamaruev.ds.dqn.maze.strategy.ActionStrategy;
import com.zamaruev.ds.dqn.maze.strategy.BackTrackActionStrategy;
import com.zamaruev.ds.dqn.maze.strategy.Dl4jActionStrategy;
import com.zamaruev.ds.dqn.maze.strategy.RandomActionStrategy;
import com.zamaruev.ds.dqn.maze.ui.MainFrame;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.util.ModelSerializer;

import javax.swing.*;
import java.io.IOException;
import java.util.IntSummaryStatistics;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
public class MazeRunner extends SwingWorker<Void, Integer> {

    private final MazeGenerator generator = new MazeGenerator(5, 5, true);
    private final ActionMaker actionMaker = new ActionMaker();
    private final ActionStrategy randomStrategy = new RandomActionStrategy();
    private final Dl4jActionStrategy dl4jStrategy = new Dl4jActionStrategy();
    private final BackTrackActionStrategy optimalStrategy = new BackTrackActionStrategy();

    private final MainFrame frame;

    private int iterationCounter = 0;
    private IntSummaryStatistics avg = new IntSummaryStatistics();

    private boolean isPaused;

    @Override
    protected Void doInBackground() throws Exception {
        while (!isCancelled()) {
            if (isPaused()) {
                frame.getPause().setText("Resume");
                Thread.sleep(200);
            } else {
                if (!frame.getRandom().getText().isEmpty()) {
                    dl4jStrategy.setRandomThreshold(Double.valueOf(frame.getRandom().getText()));
                }
                solveMaze(generator.generate());
            }
        }
        return null;
    }

    private int solveMaze(Maze maze) {
        iterationCounter++;
        int steps = 0;
        int best = optimalStrategy.calcSteps(maze);
        frame.getRandom().setText(String.format("%1.5f", dl4jStrategy.getRandomThreshold()));
        updateOutput(maze, steps, dl4jStrategy.getRandomThreshold());
        while (!maze.isSolved() && steps < 1000) {
            frame.getRandom().setText(String.format("%1.5f", dl4jStrategy.getRandomThreshold()));

            updateOutput(maze, steps, dl4jStrategy.getRandomThreshold());
            actionMaker.makeAction(maze, dl4jStrategy.next(maze));
            steps++;
        }

        updateChart(steps - best);

        Maze sample = generator.generate();
        float mse = mse(optimalStrategy.calcScores(sample), dl4jStrategy.getDl4j().runModel(dl4jStrategy.getModel(), sample));
        log.info("Steps: {}, Optimal: {}, Diff: {}, Loss: {}, Random: {}, Custom MSE: {}", steps, best, steps - best, dl4jStrategy.getLoss(), dl4jStrategy.getRandomThreshold(), mse);
        return steps;
    }

    private float mse(float[] target, float[] predicted) {
        Double squareError = IntStream.range(0, 4)
                .boxed()
                .map(i -> Math.pow(target[i] - predicted[i], 2))
                .collect(Collectors.summingDouble(Double::doubleValue));
        return squareError.floatValue() / 4;
    }

    private void updateOutput(Maze maze, int steps, double random) {
        frame.getConsole().setText("Iteration: " + iterationCounter);
        frame.getConsole().setText(String.format("Iteration: %d\nSteps: %d\nRandom: %1.5f", iterationCounter, steps, random));
        for (int x = 0; x < maze.getX(); x++) {
            for (int y = 0; y < maze.getY(); y++) {
                frame.getGrid().setValueAt(maze.getTiles()[x][y], x, y);
            }
        }
        if (frame.getSlider().getValue() > 0) {
            try {
                Thread.sleep(frame.getSlider().getValue());
            } catch (InterruptedException e) {
                // DO NOTHING
            }
        }
    }

    private void updateChart(int steps) {
        frame.getHistory().setMaximumItemCount(100);
        frame.getHistory().add(iterationCounter, steps);

        if (iterationCounter % 100 == 0) {
            avg = new IntSummaryStatistics();
        }
        avg.accept(steps);
        frame.getAvgHistory().addOrUpdate(iterationCounter / 100, avg.getAverage());
    }

    public void loadModel() {
        try {
            if (isPaused()) {
                getDl4jStrategy().setModel(ModelSerializer.restoreMultiLayerNetwork("model.zip"));
                JOptionPane.showMessageDialog(null, "Model loaded");
            } else {
                JOptionPane.showMessageDialog(null, "Please pause first");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        getDl4jStrategy().getModel().getLayer(0).paramTable().get("W").data();
        System.out.println(getDl4jStrategy().getModel().getLayer(0).params());
        System.out.println(getDl4jStrategy().getModel().getLayer(1).params());
        System.out.println(getDl4jStrategy().getModel().getLayer(2).params());
    }

    public void saveModel() {
        try {
            if (isPaused()) {
                ModelSerializer.writeModel(getDl4jStrategy().getModel(), "model.zip", false);
                JOptionPane.showMessageDialog(null, "Model saved");
            } else {
                JOptionPane.showMessageDialog(null, "Please pause first");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
