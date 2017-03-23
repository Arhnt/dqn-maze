package com.zamaruev.ds.dqn.maze.strategy;

import com.zamaruev.ds.dqn.maze.objects.Brick;
import com.zamaruev.ds.dqn.maze.objects.Maze;

/**
 * Finds path in maze using back track algorithm
 */
public class BackTrackPathFinder {

    private int exitX;
    private int exitY;
    private int[][] maze;
    private int[][] solution;
    private int mazeX;
    private int mazeY;
    private int agentX;
    private int agentY;

    public BackTrackPathFinder(Maze maze) {
        this.mazeX = maze.getX();
        this.mazeY = maze.getY();
        this.exitX = maze.getExit().getX();
        this.exitY = maze.getExit().getY();
        this.maze = new int[mazeX][mazeY];
        this.solution = new int[mazeX][mazeY];
        this.agentX = maze.getAgent().getX();
        this.agentY = maze.getAgent().getY();

        for (int x = 0; x < mazeX; x++) {
            for (int y = 0; y < mazeY; y++) {
                this.maze[x][y] = (maze.getTiles()[x][y] instanceof Brick) ? 0 : 1;
            }
        }
    }

    public int[][] getSolution() {
        findPath(agentX, agentY);
        return solution;
    }

    public boolean isPathExists() {
        return findPath(agentX, agentY);
    }

    /**
     * Finds path in maze in recursion way
     *
     * @param x - agent x
     * @param y - agent y
     * @return true if path exists, false otherwise
     */
    private boolean findPath(int x, int y) {
        if (canMoveTo(x, y)) {
            solution[x][y] = 1;

            if ((x == exitX) && (y == exitY)) {
                return true;
            }

            if (exitX > x) {
                if (findPath(x + 1, y)) return true;
            } else if (exitX < x) {
                if (findPath(x - 1, y)) return true;
            }

            if (exitY > y) {
                if (findPath(x, y + 1)) return true;
            } else if (exitY < y) {
                if (findPath(x, y - 1)) return true;
            }

            solution[x][y] = 0; // dead end
            return false;
        }

        return false;
    }


    private boolean canMoveTo(int x, int y) {
        if ((x >= 0) && (x < mazeX) && (y >= 0) && (y < mazeY)) {
            return maze[x][y] == 1;
        }
        return false;
    }

}
