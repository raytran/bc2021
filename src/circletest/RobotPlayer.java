package circletest;
import battlecode.common.*;
import circletest.nav.NavigationController;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public strictfp class RobotPlayer {
    static List<MapLocation> circleLocs;
    static int currentRadius = 3;
    static RobotController rc;
    static NavigationController nav;
    static MapLocation targetLoc;
    static Direction lastSpawnDir = Direction.NORTH;

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

    static int turnCount;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;
        nav = new NavigationController(rc);

        turnCount = 0;

        circleLocs = getFilteredCircleLocs(16389,22821, new MapLocation(16389, 22821), currentRadius);
        while (true) {
            turnCount += 1;
            // Try/catch blocks stop unhandled exceptions, which cause your robot to freeze
            try {
                // Here, we've separated the controls into a different method for each RobotType.
                // You may rewrite this into your own control structure if you wish.
                switch (rc.getType()) {
                    case ENLIGHTENMENT_CENTER: runEnlightenmentCenter(); break;
                    case POLITICIAN:           runPolitician();          break;
                    case SLANDERER:            runSlanderer();           break;
                    case MUCKRAKER:            runMuckraker();           break;
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static void runEnlightenmentCenter() throws GameActionException {
        RobotType toBuild = randomSpawnableRobotType();
        int influence = 1;
        int tryCount = 0;
        for (Direction dir : directions) {
            if (tryCount == 8) break;
            if (rc.canBuildRobot(toBuild, dir, influence)) {
                rc.buildRobot(toBuild, dir, influence);
            }
            tryCount += 1;
        }
    }

    static void runPolitician() throws GameActionException {
    }

    static boolean fuzzyMove(Direction dir) throws GameActionException {
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        } else {
            if (rc.canMove(dir.rotateLeft())){
                rc.move(dir.rotateLeft());
                return true;
            }
            if (rc.canMove(dir.rotateRight())){
                rc.move(dir.rotateRight());
                return true;
            }
            if (rc.canMove(dir.rotateLeft().rotateLeft())){
                rc.move(dir.rotateLeft().rotateLeft());
                return true;
            }
            if (rc.canMove(dir.rotateRight().rotateRight())){
                rc.move(dir.rotateRight().rotateRight());
                return true;
            }
        }
        return false;
    }
    static void runSlanderer() throws GameActionException {
    }

    static void runMuckraker() throws GameActionException {
        if (circleLocs.size() == 0){
            //System.out.println("CIRCLE DONE");
            currentRadius = currentRadius +4;
            circleLocs = getFilteredCircleLocs(16389, 22821, new MapLocation(16389, 22821), currentRadius);
        }
        if (targetLoc == null
                || (rc.getLocation().distanceSquaredTo(targetLoc) < rc.getType().sensorRadiusSquared
                && (!rc.onTheMap(targetLoc) || rc.isLocationOccupied(targetLoc) && !rc.getLocation().equals(targetLoc)))
        ){
            circleLocs.remove(targetLoc);
            int closest = Integer.MAX_VALUE;
            for (MapLocation loc : circleLocs) {
                if (loc.distanceSquaredTo(rc.getLocation()) < closest){
                    closest = loc.distanceSquaredTo(rc.getLocation());
                    targetLoc = loc;
                }
            }
        }else{
            int closest = Integer.MAX_VALUE;
            for (MapLocation loc : circleLocs) {
                if (loc.distanceSquaredTo(rc.getLocation()) < closest){
                    closest = loc.distanceSquaredTo(rc.getLocation());
                    targetLoc = loc;
                }
            }
            nav.bugAndDijkstraTo(targetLoc);
            //nav.bugTo(targetLoc);
        }
    }

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
        return RobotType.MUCKRAKER;
    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        } else return false;
    }


    // See https://rosettacode.org/wiki/Bitmap/Midpoint_circle_algorithm#Java
    private static List<MapLocation> getAllCircleLocs(MapLocation center, int radius) {
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

    private static List<MapLocation> getFilteredCircleLocs(int restrictedX, int restrictedY, MapLocation center, int radius) {
        List<MapLocation> circleLocs = getAllCircleLocs(center, radius);
        for (int i = circleLocs.size() - 1; i >= 0; i--){
            if (Math.abs(circleLocs.get(i).x - restrictedX) <= 1 || Math.abs(circleLocs.get(i).y - restrictedY) <= 1){
                // do nothing
                circleLocs.remove(i);
            } else if (circleLocs.get(i).x == restrictedX){
               //circleLocs.remove(i);
            } else if (circleLocs.get(i).y == restrictedY){
               //circleLocs.remove(i);
            }
        }
        return circleLocs;
    }
}
