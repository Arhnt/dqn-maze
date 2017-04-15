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
        return this.getExit() == null || this.getAgent().getX() == this.getExit().getX() && this.getAgent().getY() == this.getExit().getY();
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

    public double[][][] toDoubleMatrix() {
        double[][][] matrix = new double[x][y][3];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                if (tiles[i][j] instanceof Agent) {
                    matrix[i][j][0] = 1;
                } else if (tiles[i][j] instanceof Exit) {
                    matrix[i][j][1] = 1;
                } else if (tiles[i][j] instanceof Brick) {
                    matrix[i][j][2] = 1;
                }
            }
        }
        return matrix;
    }

    public int[][][] toMatrix() {
        int[][][] matrix = new int[x][y][3];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                if (tiles[i][j] instanceof Agent) {
                    matrix[i][j][0] = 1;
                } else if (tiles[i][j] instanceof Exit) {
                    matrix[i][j][1] = 1;
                } else if (tiles[i][j] instanceof Brick) {
                    matrix[i][j][2] = 1;
                }
            }
        }
        return matrix;
    }

    public static Maze fromMatrix(int[][][] matrix) {
        Maze maze = new Maze(matrix.length, matrix.length);
        for(int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j][0] == 1) {
                    maze.setAgent(new Agent(i, j));
                } else if (matrix[i][j][1] == 1) {
                    maze.setExit(new Exit(i, j));
                } else if (matrix[i][j][2] == 1) {
                    maze.getTiles()[i][j] = new Brick(i, j);
                }
            }
        }
        return maze;
    }

}
