package com.zamaruev.ds.dqn.maze.objects;

public class Exit extends AbstractTile {
    public Exit(final int x, final int y) {
        super(x, y);
    }

    @Override
    public String toString() {
        return "E";
    }

    @Override
    public Exit clone() {
        return new Exit(getX(), getY());
    }

}
