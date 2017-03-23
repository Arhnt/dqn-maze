package com.zamaruev.ds.dqn.maze.strategy;

import com.zamaruev.ds.dqn.maze.action.Action;
import com.zamaruev.ds.dqn.maze.objects.Maze;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Solves Maze using Back Track strategy
 */
public class BackTrackActionStrategy implements ActionStrategy {

    /**
     * Score for win action that solves maze.
     */
    private final static float WIN_SCORE = 1f;
    /**
     * Score for dead action that does not change state (i.e. wall)
     */
    private final static float DEAD_SCORE = -0.1f;
    /**
     * Score for move action that does change state but does not solve the maze.
     */
    private final static float MOVE_SCORE = -0.1f;


    public Action next(Maze maze) {
        BackTrackPathFinder finder = new BackTrackPathFinder(maze);
        int[][] solution = finder.getSolution();

        if (maze.getAgent().getX() > maze.getExit().getX()) {
            return Action.UP;
        } else if (maze.getAgent().getX() < maze.getExit().getX()) {
            return Action.DOWN;
        } else if (maze.getAgent().getY() > maze.getExit().getY()) {
            return Action.LEFT;
        }
        return Action.RIGHT;
    }


    /**
     * Calculates minimum number of steps to solve maze.
     */
    public int calcSteps(Maze maze) {
        BackTrackPathFinder finder = new BackTrackPathFinder(maze);
        int[][] solution = finder.getSolution();
        return (int) Arrays.stream(solution).map(row -> Arrays.stream(row).sum()).collect(Collectors.summarizingInt(Integer::valueOf)).getSum() - 1;

//        return Math.abs(maze.getExit().getX() - maze.getAgent().getX())
//                + Math.abs(maze.getExit().getY() - maze.getAgent().getY());
    }

    /**
     * Calculates scores for each action based on optimal solution
     *
     * @return scores for left \ up \ right \ down actions
     */
    public float[] calcScores(Maze maze) {
        float[] scores = new float[4];

        int stepsToWin = calcSteps(maze) - 1;
        // Left

        if (maze.getAgent().getY() == 0) {
            // can't move
            scores[Action.indexOf(Action.LEFT)] = WIN_SCORE + DEAD_SCORE + stepsToWin * MOVE_SCORE;
        } else if (maze.getAgent().getY() > maze.getExit().getY()) {
            // should to that direction
            scores[Action.indexOf(Action.LEFT)] = WIN_SCORE + stepsToWin * MOVE_SCORE;
        } else {
            // wrong way
            scores[Action.indexOf(Action.LEFT)] = WIN_SCORE + (stepsToWin + 2) * MOVE_SCORE;
        }

        // Up
        if (maze.getAgent().getX() == 0) {
            scores[Action.indexOf(Action.UP)] = WIN_SCORE + DEAD_SCORE + stepsToWin * MOVE_SCORE;
        } else if (maze.getAgent().getX() > maze.getExit().getX()) {
            scores[Action.indexOf(Action.UP)] = WIN_SCORE + stepsToWin * MOVE_SCORE;
        } else {
            scores[Action.indexOf(Action.UP)] = WIN_SCORE + (stepsToWin + 2) * MOVE_SCORE;
        }

        // Right
        if (maze.getAgent().getY() == maze.getY() - 1) {
            scores[Action.indexOf(Action.RIGHT)] = WIN_SCORE + DEAD_SCORE + stepsToWin * MOVE_SCORE;
        } else if (maze.getAgent().getY() < maze.getExit().getY()) {
            scores[Action.indexOf(Action.RIGHT)] = WIN_SCORE + stepsToWin * MOVE_SCORE;
        } else {
            scores[Action.indexOf(Action.RIGHT)] = WIN_SCORE + (stepsToWin + 2) * MOVE_SCORE;
        }

        // Down
        if (maze.getAgent().getX() == maze.getX() - 1) {
            scores[Action.indexOf(Action.DOWN)] = WIN_SCORE + DEAD_SCORE + stepsToWin * MOVE_SCORE;
        } else if (maze.getAgent().getX() < maze.getExit().getX()) {
            scores[Action.indexOf(Action.DOWN)] = WIN_SCORE + stepsToWin * MOVE_SCORE;
        } else {
            scores[Action.indexOf(Action.DOWN)] = WIN_SCORE + (stepsToWin + 2) * MOVE_SCORE;
        }

        return scores;
    }

}
