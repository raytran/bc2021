package dlmoreram012521_01;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import battlecode.common.RobotController;
import dlmoreram012521_01.flags.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Static utility methods
 */
public class Utilities {
    public static final int MAX_ROUND = 1500;
    public static final int VOTE_WIN = 751;
    public static final double EPSILON = 0.001;
    public static final int[] SLANDERER_VALUES = {21, 42, 63, 85, 107, 130, 154, 178, 203, 228, 255, 282, 310, 339, 368, 399, 431, 463, 497, 532, 568, 605, 644, 683, 724, 766, 810, 855, 902, 949};
    public static final Direction[] CARDINAL_DIRECTIONS = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    public static final Direction[] OFF_DIAGONALS = {Direction.NORTHEAST, Direction.NORTHWEST, Direction.SOUTHEAST, Direction.SOUTHWEST};
    public static final RobotType[] spawnableRobot = {
            RobotType.POLITICIAN,
            RobotType.SLANDERER,
            RobotType.MUCKRAKER,
    };

    public static final Direction[] directions = {
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
    public static Direction randomDirection() {
        return directions[(int) (Math.random() * directions.length)];
    }

    /**
     * Returns a random spawnable RobotType
     *
     * @return a random RobotType
     */
    public static RobotType randomSpawnableRobotType() {
        return spawnableRobot[(int) (Math.random() * spawnableRobot.length)];
    }

    /**
     * Returns possible neighboring tiles, not taking into account whether or not the neighbors are out of bounds
     * @param center center tile
     * @return array of 8 neighbors
     */
    public static MapLocation[] getPossibleNeighbors(MapLocation center){
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


    public static Direction toNearestBoundary(RobotController rc, BoundarySpottedInfo[] boundaries) {
        MapLocation currentLoc = rc.getLocation();
        Direction out = null;
        int currentMin = 100;
        for (BoundarySpottedInfo boundary : boundaries) {
            if (boundary != null) {
                int diff;
                switch(boundary.boundaryType) {
                    case NORTH:
                        diff = Math.abs(currentLoc.y - boundary.exactBoundaryLocation);
                        if (diff < currentMin) {
                            currentMin = diff;
                            out = Direction.NORTH;
                        }
                        break;
                    case EAST:
                        diff = Math.abs(currentLoc.x - boundary.exactBoundaryLocation);
                        if (diff < currentMin) {
                            currentMin = diff;
                            out = Direction.EAST;
                        }
                        break;
                    case SOUTH:
                        diff = Math.abs(currentLoc.y - boundary.exactBoundaryLocation);
                        if (diff < currentMin) {
                            currentMin = diff;
                            out = Direction.SOUTH;
                        }
                        break;
                    case WEST:
                        diff = Math.abs(currentLoc.x - boundary.exactBoundaryLocation);
                        if (diff < currentMin) {
                            currentMin = diff;
                            out = Direction.WEST;
                        }
                        break;
                    default: throw new RuntimeException("Shouldn't be here");
                }
            }
        }
        if(currentMin == 0) return Direction.CENTER;
        else return out;
    }

    /**
     * Returns neighbors of center that are +/- 2 turns from dir
     * Not taking into account whether or not the neighbors are out of bounds
     * @param center center loc
     * @param dir direction to turn from
     * @return array of possible directed neighbors
     */
    public static MapLocation[] getPossibleDirectedNeighbors(MapLocation center, Direction dir){
        return new MapLocation[]{
                center.add(dir),
                center.add(dir.rotateRight()),
                center.add(dir.rotateRight().rotateRight()),
                center.add(dir.rotateLeft()),
                center.add(dir.rotateLeft().rotateLeft())
        };
    }

    /**
     * Offsets a given map location in a direction by variable # of units
     * @param dir direction to offset
     * @param units number of units to offset by
     * @return offset maplocation
     */
    public static MapLocation offsetLocation(MapLocation loc, Direction dir, int units) {
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

    /**
     * @param loc1 first loc
     * @param loc2 second loc
     * @return slope between points
     */
    public static double getSlope(MapLocation loc1, MapLocation loc2) {
        return ((double) loc2.y - loc1.y)/((double) (loc2.x - loc1.x)  + EPSILON);
    }

    public static boolean isClose(double d1, double d2) {
        return Math.abs(d1 - d2) < 5;
    }

    /**
     *
     * @param dir
     * @return true if direction is a diagonal
     */
    public static boolean isDiagonal(Direction dir){
        return (dir == Direction.NORTHEAST
                || dir == Direction.NORTHWEST
                || dir == Direction.SOUTHEAST
                || dir == Direction.SOUTHWEST);
    }


    /**
     * Returns map locations that form a circle around center and radius
     * except where loc abs(circleX - restrictedX) > threshold (same for Y)
     * @param restrictedX x loc restricted
     * @param restrictedY y loc restricted
     * @param center of the circle
     * @param radius of the circle
     * @return filtered map locations of circle
     */
    public static List<MapLocation> getFilteredCircleLocs(int threshold, int restrictedX, int restrictedY, MapLocation center, int radius) {
        List<MapLocation> circleLocs = getAllCircleLocs(center, radius);
        for (int i = circleLocs.size() - 1; i >= 0; i--) {
            if (Math.abs(circleLocs.get(i).x - restrictedX) <= threshold
                    || Math.abs(circleLocs.get(i).y - restrictedY) <= threshold) {
                circleLocs.remove(i);
            }
        }
        return circleLocs;
    }
    /**
     * Returns all maplocations in that form a circle of radius around center
     * @param center center of circle
     * @param radius radius of the circle
     * @return map locs of circle
     */
    // See https://rosettacode.org/wiki/Bitmap/Midpoint_circle_algorithm#Java
    public static List<MapLocation> getAllCircleLocs(MapLocation center, int radius) {
        List<MapLocation> circleLocs = new LinkedList<>();
        int d = (5 - radius * 4)/4;
        int x = 0;
        int y = radius;

        do {
            circleLocs.add(new MapLocation(center.x + x, center.y + y));
            circleLocs.add(new MapLocation(center.x + x, center.y - y));
            circleLocs.add(new MapLocation(center.x - x, center.y + y));
            circleLocs.add(new MapLocation(center.x - x, center.y - y));
            circleLocs.add(new MapLocation(center.x + y, center.y + x));
            circleLocs.add(new MapLocation(center.x + y, center.y - x));
            circleLocs.add(new MapLocation(center.x - y, center.y + x));
            circleLocs.add(new MapLocation(center.x - y, center.y - x));
            if (d < 0) {
                d += 2 * x + 1;
            } else {
                d += 2 * (x - y) + 1;
                y--;
            }
            x++;
        } while (x <= y);
        return circleLocs;
    }
}
