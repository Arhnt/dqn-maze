package com.zamaruev.ds.dqn.maze;

import com.zamaruev.ds.dqn.maze.ui.MainFrame;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Slf4j
public class MazeApp {

    public static void main(String[] args) throws IOException, InvocationTargetException, InterruptedException {
        EventQueue.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);

            MazeRunner runner = new MazeRunner(frame);
            runner.execute();

            frame.getPause().addActionListener(e -> {
                ((JButton) e.getSource()).setText(runner.isPaused() ? "Pause" : "Pausing...");
                runner.setPaused(!runner.isPaused());
            });

            frame.getSave().addActionListener(e -> runner.saveModel());
            frame.getLoad().addActionListener(e -> runner.loadModel());
        });
    }

}
