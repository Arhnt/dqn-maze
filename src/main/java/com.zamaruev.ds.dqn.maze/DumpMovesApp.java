package com.zamaruev.ds.dqn.maze;

import com.zamaruev.ds.dqn.maze.action.Action;
import com.zamaruev.ds.dqn.maze.action.ActionMaker;
import com.zamaruev.ds.dqn.maze.generator.MazeGenerator;
import com.zamaruev.ds.dqn.maze.objects.Maze;
import com.zamaruev.ds.dqn.maze.strategy.ActionStrategy;
import com.zamaruev.ds.dqn.maze.strategy.RandomActionStrategy;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

@Slf4j
public class DumpMovesApp {

    private static MazeGenerator generator = new MazeGenerator(5, 5, false);
    private static ActionMaker actionMaker = new ActionMaker();
    private static ActionStrategy strategy = new RandomActionStrategy();

    public static void main(String[] args) throws IOException {
        int stepsMade = 0;
        int stepsToGenerate = 100000;

        try(FileOutputStream dump = new FileOutputStream("maze-runner.ser")) {
            try (ObjectOutputStream dumpStream = new ObjectOutputStream(dump)) {
                while (stepsMade < stepsToGenerate) {
                    int steps = 0;
                    Maze maze = generator.generate();
                    while (!maze.isSolved() && steps++ < 1000) {

                        dumpStream.writeObject(maze.toMatrix());

                        Action action = strategy.next(maze);
                        dumpStream.writeObject(action);

                        actionMaker.makeAction(maze, action);
                        dumpStream.writeObject(maze.toMatrix());
                    }
                    stepsMade += steps;
                }
            }
        }

    }

}
