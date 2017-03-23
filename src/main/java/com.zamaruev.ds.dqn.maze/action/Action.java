package com.zamaruev.ds.dqn.maze.action;

public enum Action {

    LEFT, UP, RIGHT, DOWN;

    public static int indexOf(Action action) {
        int i = 0;
        for (Action value : Action.values()) {
            if (value == action) {
                return i;
            }
            i++;
        }
        return i;
    }

}
