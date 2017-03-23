package com.zamaruev.ds.dqn.maze.strategy;

import com.zamaruev.ds.dqn.maze.action.Action;
import com.zamaruev.ds.dqn.maze.objects.Maze;

public interface ActionStrategy {

    Action next(Maze maze);

}


