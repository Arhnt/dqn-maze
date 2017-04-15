package com.zamaruev.ds.dqn.maze.objects;

import com.zamaruev.ds.dqn.maze.strategy.ActionStrategy;
import lombok.Data;

@Data
public class Agent extends AbstractTile {

    public Agent(int x, int y) {
        super(x, y);
    }

    @Override
    public String toString() {
        return "A";
    }

    @Override
    public Agent clone() {
        return new Agent(getX(), getY());
    }

}
