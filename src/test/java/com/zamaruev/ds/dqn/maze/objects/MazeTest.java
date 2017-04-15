package com.zamaruev.ds.dqn.maze.objects;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class MazeTest {

    @Test
    public void testClone() {
        Maze maze = new Maze(3, 3);
        maze.setExit(new Exit(0, 1));
        maze.setAgent(new Agent(2, 2));

        assertThat(maze.getTiles()[0][1], is(instanceOf(Exit.class)));
        assertThat(maze.getTiles()[2][2], is(instanceOf(Agent.class)));

        Maze clone = maze.clone();

        assertThat(clone.getTiles()[0][1], is(instanceOf(Exit.class)));
        assertThat(clone.getTiles()[2][2], is(instanceOf(Agent.class)));

        clone.setAgent(new Agent(1, 1));

        assertThat(clone.getTiles()[1][1], is(instanceOf(Agent.class)));
        assertThat(maze.getTiles()[1][1], is(instanceOf(EmptyTile.class)));
    }

    @Test
    public void testSerialize() {
        Maze maze = new Maze(3, 3);
        maze.setExit(new Exit(0, 1));
        maze.setAgent(new Agent(2, 2));
        maze.getTiles()[0][2] = new Brick(0, 2);
        maze.getTiles()[1][2] = new Brick(1, 2);

        int[][][] matrix = maze.toMatrix();
        assertEquals(maze, Maze.fromMatrix(matrix));
    }


}
