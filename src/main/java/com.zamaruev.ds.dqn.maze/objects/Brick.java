package com.zamaruev.ds.dqn.maze.objects;

public class Brick extends AbstractTile {
    public Brick(final int x, final int y) {
        super(x, y);
    }

    @Override
    public String toString() {
        return "*";
    }

    @Override
    public Brick clone() {
        return new Brick(getX(), getY());
    }

}
