package com.zamaruev.ds.dqn.maze.objects;

import lombok.Data;

@Data
public class Maze {

    private final int x;
    private final int y;
    private final Tile[][] tiles;
    private Agent agent;
    private Exit exit;

    public Maze(int x, int y) {
        this.x = x;
        this.y = y;
        this.tiles = new Tile[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                tiles[i][j] = new EmptyTile(i, j);
            }
        }
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
        tiles[agent.getX()][agent.getY()] = agent;
    }

    public void setExit(Exit exit) {
        this.exit = exit;
        tiles[exit.getX()][exit.getY()] = exit;
    }

    public boolean isSolved() {
        return this.getAgent().getX() == this.getExit().getX() && this.getAgent().getY() == this.getExit().getY();
    }

    @Override
    public String toString() {
        String result = horizontalLine();

        for (int i = 0; i < x; i++) {
            result += "|";
            for (int j = 0; j < y; j++) {
                result += tiles[i][j];
            }
            result += "|\n";
        }

        return result + horizontalLine();
    }

    @Override
    public Maze clone() {
        Maze maze = new Maze(x, y);
        maze.setAgent(this.getAgent().clone());
        maze.setExit(this.getExit().clone());
        return maze;
    }

    private String horizontalLine() {
        String line = "+";
        for (int i = 0; i < x; i++) {
            line += "-";
        }
        line += "+\n";
        return line;
    }

}
