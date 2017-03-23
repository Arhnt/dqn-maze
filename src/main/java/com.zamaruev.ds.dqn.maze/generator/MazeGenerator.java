package com.zamaruev.ds.dqn.maze.generator;


import com.zamaruev.ds.dqn.maze.objects.*;
import com.zamaruev.ds.dqn.maze.strategy.BackTrackPathFinder;
import lombok.RequiredArgsConstructor;

import java.util.Random;

@RequiredArgsConstructor
public class MazeGenerator {

    private final int x;
    private final int y;
    private final boolean fixed;
    private Random random = new Random();

    public Maze generate() {
        Maze maze = new Maze(x, y);

        addBrick(maze);
        addBrick(maze);

        addAgent(maze, fixed);
        addExit(maze, fixed);

        checkPath(maze);

        return maze;
    }

    private void addAgent(Maze maze, boolean fixed) {
        int x = fixed ? (maze.getX() - 1) : random.nextInt(maze.getX());
        int y = fixed ? maze.getY() / 2 : random.nextInt(maze.getY());
        maze.setAgent(new Agent(x, y));
    }

    private void addExit(Maze maze, boolean fixed) {
        int x = fixed ? 0 : random.nextInt(maze.getX());
        int y = fixed ? maze.getY() / 2 : random.nextInt(maze.getY());

        if ((maze.getAgent().getX() == x) && (maze.getAgent().getY() == y)) {
            x = Math.abs(x - 1);
        }

        maze.setExit(new Exit(x, y));
    }

    private void addBrick(final Maze maze) {
        int x = random.nextInt(maze.getX());
        int y = random.nextInt(maze.getY());
        maze.getTiles()[x][y] = new Brick(x, y);
    }

    private void checkPath(Maze maze) {
        BackTrackPathFinder finder = new BackTrackPathFinder(maze);
        while (!finder.isPathExists()) {
            removeBrick(maze);
            finder = new BackTrackPathFinder(maze);
        }
    }

    private void removeBrick(Maze maze) {
        for (int x = 0; x < maze.getX(); x++) {
            for (int y = 0; y < maze.getY(); y++) {
                if (maze.getTiles()[x][y] instanceof Brick) {
                    maze.getTiles()[x][y] = new EmptyTile(x, y);
                    return;
                }
            }
        }
    }

}
