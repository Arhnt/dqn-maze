package com.zamaruev.ds.dqn.maze.utils;

public class ArrayUtils {

    public static float maxValue(float[] values) {
        float value = values[0];
        for (int i = 1; i < values.length; i++) {
            if (values[i] > value) {
                value = values[i];
            }
        }
        return value;
    }

    public static int maxIndex(float[] values) {
        int index = 0;
        float value = values[index];
        for (int i = 1; i < values.length; i++) {
            if (values[i] > value) {
                index = i;
                value = values[index];
            }
        }
        return index;
    }

}
