package scoutingtest;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;

import java.util.ArrayList;
import java.util.List;

/**
 * Static utility methods
 */
public class Utilities {
    static final RobotType[] spawnableRobot = {
            RobotType.POLITICIAN,
            RobotType.SLANDERER,
            RobotType.MUCKRAKER,
    };

    static final Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST,
    };

    /**
     * Returns a random Direction.
     *
     * @return a random Direction
     */
    static Direction randomDirection() {
        return directions[(int) (Math.random() * directions.length)];
    }

    /**
     * Returns a random spawnable RobotType
     *
     * @return a random RobotType
     */
    static RobotType randomSpawnableRobotType() {
        return spawnableRobot[(int) (Math.random() * spawnableRobot.length)];
    }

    /**
     * Returns possible neighboring tiles, not taking into account whether or not the neighbors are out of bounds
     * @param center center tile
     * @return array of 8 neighbors
     */
    static MapLocation[] getPossibleNeighbors(MapLocation center){
        return new MapLocation[] {
                center.translate(0, 1),
                center.translate(0, -1),
                center.translate(1, 0),
                center.translate(-1, 0),
                center.translate(1, 1),
                center.translate(-1, 1),
                center.translate(1, -1),
                center.translate(-1, -1),
        };
    }
}
