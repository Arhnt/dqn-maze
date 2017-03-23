package com.zamaruev.ds.dqn.maze.strategy;

import com.zamaruev.ds.dqn.maze.action.Action;
import com.zamaruev.ds.dqn.maze.objects.Maze;

import java.util.Random;

public class RandomActionStrategy implements ActionStrategy {

    private Random random = new Random();

    public Action next(Maze maze) {
        int i = random.nextInt(Action.values().length);
        return Action.values()[i];
    }

}
