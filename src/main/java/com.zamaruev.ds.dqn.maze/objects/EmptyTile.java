package com.zamaruev.ds.dqn.maze.objects;

public class EmptyTile extends AbstractTile {

    public EmptyTile(final int x, final int y) {
        super(x, y);
    }

    @Override
    public String toString() {
        return " ";
    }
}
