package com.zamaruev.ds.dqn.maze.tensorflow;

import com.zamaruev.ds.dqn.maze.action.Action;
import com.zamaruev.ds.dqn.maze.objects.Maze;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;

@Data
@AllArgsConstructor
public class Step {

    public Maze initialState;
    public Action action;
    public float award;
    public Maze newState;

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Arrays.deepHashCode(initialState.getTiles());
        result = 31 * result + action.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(obj instanceof Step) {
            Step that = (Step) obj;
            if (this.action == that.action) {
              return  Arrays.deepEquals(this.getInitialState().getTiles(), that.getInitialState().getTiles());
            }
        }
        return false;
    }

}
