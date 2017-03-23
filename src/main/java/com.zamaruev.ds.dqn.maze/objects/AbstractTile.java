package com.zamaruev.ds.dqn.maze.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AbstractTile implements Tile {

    private int x;
    private int y;

}
