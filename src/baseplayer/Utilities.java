package baseplayer;

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

    /**
     * Offsets a given map location in a direction by variable # of units
     * @param dir direction to offset
     * @param units number of units to offset by
     * @return offset maplocation
     */
    static MapLocation offsetLocation(MapLocation loc, Direction dir, int units) {
        switch (dir) {
            case NORTH:
                return loc.translate(0, units);
            case EAST:
                return loc.translate(units, 0);
            case SOUTH:
                return loc.translate(0, -units);
            case WEST:
                return loc.translate(-units, 0);
            case NORTHEAST:
                return loc.translate((int) (units * Math.sqrt(2)/2), (int) (units * Math.sqrt(2)/2));
            case NORTHWEST:
                return loc.translate(-(int) (units * Math.sqrt(2)/2), (int) (units * Math.sqrt(2)/2));
            case SOUTHEAST:
                return loc.translate((int) (units * Math.sqrt(2)/2), -(int) (units * Math.sqrt(2)/2));
            case SOUTHWEST:
                return loc.translate(-(int) (units * Math.sqrt(2)/2), -(int) (units * Math.sqrt(2)/2));
        }
        throw new RuntimeException("Shouldn't be here...");
    }
}
