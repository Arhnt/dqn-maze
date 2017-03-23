package com.zamaruev.ds.dqn.maze.strategy;

import com.zamaruev.ds.dqn.maze.objects.Agent;
import com.zamaruev.ds.dqn.maze.objects.Exit;
import com.zamaruev.ds.dqn.maze.objects.Maze;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class OptimalActionStrategyTest {

    private BackTrackActionStrategy strategy = new BackTrackActionStrategy();

    @Test
    public void testCalcScores_Next() {
        float[] scores = strategy.calcScores(maze(4, 2, 3, 2));
        assertThat(scores, is(new float[]{0.8f, 1f, 0.8f, 0.5f}));
    }

    @Test
    public void testCalcScores_NextDiag() {
        float[] scores = strategy.calcScores(maze(3, 2, 2, 1));
        assertThat(scores, is(new float[]{0.9f, 0.9f, 0.7f, 0.7f}));
    }

    @Test
    public void testCalcScores_StraightLine() {
        float[] scores = strategy.calcScores(maze(4, 2, 0, 2));
        assertThat(scores, is(new float[]{0.5f, 0.7f, 0.5f, 0.2f}));
    }

    private Maze maze(int agentX, int agentY, int exitX, int exitY) {
        Maze maze = new Maze(5, 5);
        maze.setAgent(new Agent(agentX, agentY));
        maze.setExit(new Exit(exitX, exitY));
        return maze;
    }

}
