package com.zamaruev.ds.dqn.maze.tensorflow;

import com.zamaruev.ds.dqn.maze.action.Action;
import com.zamaruev.ds.dqn.maze.objects.Maze;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Step {

    public Maze initialState;
    public Action action;
    public float award;
    public Maze newState;

}
