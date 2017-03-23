package com.zamaruev.ds.dqn.maze.strategy;

import com.zamaruev.ds.dqn.maze.objects.Agent;
import com.zamaruev.ds.dqn.maze.objects.Brick;
import com.zamaruev.ds.dqn.maze.objects.Exit;
import com.zamaruev.ds.dqn.maze.objects.Maze;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BackTrackPathFinderTest {

    @Test
    public void testIsPathExists() {
        Maze maze = new Maze(5, 5);
        maze.setAgent(new Agent(0, 0));
        maze.setExit(new Exit(4, 4));

        BackTrackPathFinder finder = new BackTrackPathFinder(maze);

        assertThat(finder.isPathExists(), is(true));
    }

    @Test
    public void testIsPathExists_Backward() {
        Maze maze = new Maze(5, 5);
        maze.setAgent(new Agent(4, 4));
        maze.setExit(new Exit(0, 0));

        BackTrackPathFinder finder = new BackTrackPathFinder(maze);

        assertThat(finder.isPathExists(), is(true));
    }

    @Test
    public void testIsPathExists_Vertical() {
        Maze maze = new Maze(5, 5);
        maze.setAgent(new Agent(0, 0));
        maze.setExit(new Exit(4, 0));

        BackTrackPathFinder finder = new BackTrackPathFinder(maze);

        assertThat(finder.isPathExists(), is(true));
    }

    @Test
    public void testIsPathExists_BrickAngle() {
        Maze maze = new Maze(5, 5);
        maze.setAgent(new Agent(0, 0));
        maze.setExit(new Exit(4, 4));
        maze.getTiles()[4][3] = new Brick(4, 3);
        maze.getTiles()[3][4] = new Brick(3, 4);

        BackTrackPathFinder finder = new BackTrackPathFinder(maze);

        assertThat(finder.isPathExists(), is(false));
    }

}
