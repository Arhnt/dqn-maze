package com.zamaruev.ds.dqn.maze.action;

import com.zamaruev.ds.dqn.maze.objects.Brick;
import com.zamaruev.ds.dqn.maze.objects.EmptyTile;
import com.zamaruev.ds.dqn.maze.objects.Maze;

public class ActionMaker {

    /**
     * X - rows, Y - columns
     */
    public boolean makeAction(Maze maze, Action action) {
        switch (action) {
            case LEFT:
                return moveAgent(maze, maze.getAgent().getX(), maze.getAgent().getY() - 1);
            case UP:
                return moveAgent(maze, maze.getAgent().getX() - 1, maze.getAgent().getY());
            case RIGHT:
                return moveAgent(maze, maze.getAgent().getX(), maze.getAgent().getY() + 1);
            case DOWN:
                return moveAgent(maze, maze.getAgent().getX() + 1, maze.getAgent().getY());
        }
        return false;
    }

    private boolean moveAgent(Maze maze, int x, int y) {
        if ((x >= 0) && (x < maze.getX()) && (y >= 0) && (y < maze.getY()) && !(maze.getTiles()[x][y] instanceof Brick)) {
            int oldX = maze.getAgent().getX();
            int oldY = maze.getAgent().getY();

            maze.getAgent().setX(x);
            maze.getAgent().setY(y);

            maze.getTiles()[oldX][oldY] = new EmptyTile(oldX, oldY);
            maze.getTiles()[x][y] = maze.getAgent();

            return true;
        }
        return false;
    }

}
