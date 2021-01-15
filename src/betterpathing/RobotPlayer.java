
package betterpathing;
import baseplayer.nav.NavigationController;
import battlecode.common.*;

import java.util.List;


public strictfp class RobotPlayer {


    static MapLocation parentLoc;
    static MapLocation circleTargetLoc;
    static List<MapLocation> circleLocs;
    static int currentRadius = 3;
    static RobotController rc;
    static NavigationController nav;
    static MapLocation goal;
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

    public static final int[] NORTH_I_TO_DELTA2 = new int[]{-2,0};
    public static final int[] NORTH_I_TO_DELTA7 = new int[]{-2,1};
    public static final int[] NORTH_I_TO_DELTA12 = new int[]{-2,2};
    public static final int[] NORTH_I_TO_DELTA17 = new int[]{-2,3};
    public static final int[] NORTH_I_TO_DELTA0 = new int[]{-1,-1};
    public static final int[] NORTH_I_TO_DELTA3 = new int[]{-1,0};
    public static final int[] NORTH_I_TO_DELTA8 = new int[]{-1,1};
    public static final int[] NORTH_I_TO_DELTA13 = new int[]{-1,2};
    public static final int[] NORTH_I_TO_DELTA18 = new int[]{-1,3};
    public static final int[] NORTH_I_TO_DELTA4 = new int[]{0,0};
    public static final int[] NORTH_I_TO_DELTA9 = new int[]{0,1};
    public static final int[] NORTH_I_TO_DELTA14 = new int[]{0,2};
    public static final int[] NORTH_I_TO_DELTA19 = new int[]{0,3};
    public static final int[] NORTH_I_TO_DELTA1 = new int[]{1,-1};
    public static final int[] NORTH_I_TO_DELTA5 = new int[]{1,0};
    public static final int[] NORTH_I_TO_DELTA10 = new int[]{1,1};
    public static final int[] NORTH_I_TO_DELTA15 = new int[]{1,2};
    public static final int[] NORTH_I_TO_DELTA20 = new int[]{1,3};
    public static final int[] NORTH_I_TO_DELTA6 = new int[]{2,0};
    public static final int[] NORTH_I_TO_DELTA11 = new int[]{2,1};
    public static final int[] NORTH_I_TO_DELTA16 = new int[]{2,2};
    public static final int[] NORTH_I_TO_DELTA21 = new int[]{2,3};
    public static final int[] NORTH_NEIGHBORS0 = {2, 3, 4};
    public static final int[] NORTH_NEIGHBORS1 = {4, 5, 6};
    public static final int[] NORTH_NEIGHBORS2 = {0, 7, 8, 3};
    public static final int[] NORTH_NEIGHBORS3 = {0, 2, 7, 8, 9, 4};
    public static final int[] NORTH_NEIGHBORS4 = {0, 3, 8, 9, 10, 5, 1};
    public static final int[] NORTH_NEIGHBORS5 = {1, 4, 6, 8, 10, 11};
    public static final int[] NORTH_NEIGHBORS6 = {1, 5, 10, 11};
    public static final int[] NORTH_NEIGHBORS7 = {2, 3, 8, 12, 13};
    public static final int[] NORTH_NEIGHBORS8 = {2, 3, 4, 7, 9, 12, 13, 14};
    public static final int[] NORTH_NEIGHBORS9 = {3, 4, 5, 8, 10, 13, 14, 15};
    public static final int[] NORTH_NEIGHBORS10 = {4, 5, 6, 9, 11, 14, 15, 16};
    public static final int[] NORTH_NEIGHBORS11 = {15, 16, 10, 5, 6};
    public static final int[] NORTH_NEIGHBORS12 = {17, 18, 13, 7, 8};
    public static final int[] NORTH_NEIGHBORS13 = {17, 18, 19, 12, 14, 7, 8, 9};
    public static final int[] NORTH_NEIGHBORS14 = {18, 19, 20, 13, 15, 8, 9, 10};
    public static final int[] NORTH_NEIGHBORS15 = {19, 20, 21, 14, 16, 9, 10, 11};
    public static final int[] NORTH_NEIGHBORS16 = {20, 21, 15, 10, 11};
    public static final int[] NORTH_NEIGHBORS17 = {18, 12, 13};
    public static final int[] NORTH_NEIGHBORS18 = {17, 19, 12, 13, 14};
    public static final int[] NORTH_NEIGHBORS19 = {18, 20, 13, 14, 15};
    public static final int[] NORTH_NEIGHBORS20 = {19, 21, 14, 15, 16};
    public static final int[] NORTH_NEIGHBORS21 = {20, 15, 16};



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
        Pather.init(rc);
        nav = new NavigationController(rc);
        goal = rc.getLocation().translate(30, 30);

        turnCount = 0;

        parentLoc = new MapLocation(16389, 22821);
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
        //if (rc.getRoundNum() == 1){
            for (Direction dir : directions) {
                if (tryCount == 8) break;
                if (rc.canBuildRobot(toBuild, dir, influence)) {
                    rc.buildRobot(toBuild, dir, influence);
                }
                tryCount += 1;
            }
        //}
    }

    static void runPolitician() throws GameActionException {
        Pather.pathTo(goal);
        if (rc.getRoundNum() == 500){
            goal = rc.getLocation().translate(-20, -20);
        }

    }
    static void runSlanderer() throws GameActionException {



        for (int i=0;i<30;i++) {
            for (int j=0;j<30;j++) {
                int[] delta1 = northIndexToDelta(j);
                rc.setIndicatorDot(new MapLocation(rc.getLocation().x + delta1[0], rc.getLocation().y + delta1[1]), 255, 0, 0);

            }

            for (int neighborIndex : getNorthSemicircleNeighbors(i)){
                int[] delta = northIndexToDelta(neighborIndex);
                rc.setIndicatorDot(new MapLocation(rc.getLocation().x + delta[0], rc.getLocation().y + delta[1]), 0, 0, 255);
            }
            Clock.yield();

        }

    }

    static void runMuckraker() throws GameActionException {

    }

    /*
    static double[] distances = new double[22];
    static double[] weights = new double[22];
    static int[] parents = new int[22];

    final static int[] outside = {0,2,7,12,17,18,19,20,21,16,11,6,1,3,8,13,14,15,10,5,9};
    static void betterDFS(MapLocation targetLoc) throws GameActionException {
        MapLocation currentLoc = rc.getLocation();
        int minDist = Integer.MAX_VALUE;
        MapLocation replacement = null;
        for (int i = 0; i < outside.length; i++) {
            int idx = outside[i];
            int[] deltas = northIndexToDelta(idx);
            MapLocation candidate = new MapLocation(currentLoc.x + deltas[0], currentLoc.y + deltas[1]);
            if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
                minDist = candidate.distanceSquaredTo(targetLoc);
                replacement = candidate;
            }
        }

        targetLoc = replacement;

        //System.out.println("NEW TARGET" + targetLoc);

        if (targetLoc != null) {
            //rc.setIndicatorDot(targetLoc, 0, 0, 255);
            // Init weights and distances
            for (int i=0;i< 22;i++) {
                parents[i] = -1;
                distances[i] = 99999999;
                int[] deltas = northIndexToDelta(i);
                MapLocation pos = rc.getLocation().translate(deltas[0], deltas[1]);
                if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                    weights[i] = 999;
                }else{
                    weights[i] = (1 - rc.sensePassability(pos));
                }
            }
            distances[northDeltaToIndex(0, 0)] = 0;

            // Relax edges u -> v
            int start = Clock.getBytecodeNum();
            for (int i=0;i < 3;i++) {
                for (int u=0; u < 22; u++) {
                    for (int v : getNorthSemicircleNeighbors(u)) {
                        double newDist = distances[u] + weights[v];
                        if (newDist < distances[v]) {
                            distances[v] = newDist;
                            parents[v] = u;
                        }
                    }
                }
            }
            System.out.println("LOOP TOOK " + (Clock.getBytecodeNum() - start));


            int current = northDeltaToIndex(targetLoc.x - rc.getLocation().x, targetLoc.y - rc.getLocation().y);
            int last = -1;
            while (parents[current] != -1) {
                last = current;
                //int[] delta = northIndexToDelta(current);
                //MapLocation path = new MapLocation(rc.getLocation().x + delta[0],  rc.getLocation().y + delta[1]);
                //rc.setIndicatorDot(path, 255, 0, 0);
                current = parents[current];
            }

            if (last != -1){
                int[] delta = northIndexToDelta(last);
                MapLocation target = new MapLocation(rc.getLocation().x + delta[0],  rc.getLocation().y + delta[1]);
                if (rc.canMove(rc.getLocation().directionTo(target))){
                    rc.move(rc.getLocation().directionTo(target));
                }
            }
        }
    }
     */



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
        return RobotType.POLITICIAN;
    }

    static int northDeltaToIndex(int x, int y) {
        String delta = x +"|"+y;

        switch (delta) {
            case "-2|0":
                return 2;
            case "-2|1":
                return 7;
            case "-2|2":
                return 12;
            case "-2|3":
                return 17;
            case "-1|-1":
                return 0;
            case "-1|0":
                return 3;
            case "-1|1":
                return 8;
            case "-1|2":
                return 13;
            case "-1|3":
                return 18;
            case "0|0":
                return 4;
            case "0|1":
                return 9;
            case "0|2":
                return 14;
            case "0|3":
                return 19;
            case "1|-1":
                return 1;
            case "1|0":
                return 5;
            case "1|1":
                return 10;
            case "1|2":
                return 15;
            case "1|3":
                return 20;
            case "2|0":
                return 6;
            case "2|1":
                return 11;
            case "2|2":
                return 16;
            case "2|3":
                return 21;
            default: break;
        }
        throw new RuntimeException("Bad delta" + delta);
    }


    static int[] northIndexToDelta(int index){
        switch (index) {
            case 2:
                return NORTH_I_TO_DELTA2;
            case 7:
                return NORTH_I_TO_DELTA7;
            case 12:
                return NORTH_I_TO_DELTA12;
            case 17:
                return NORTH_I_TO_DELTA17;
            case 0:
                return NORTH_I_TO_DELTA0;
            case 3:
                return NORTH_I_TO_DELTA3;
            case 8:
                return NORTH_I_TO_DELTA8;
            case 13:
                return NORTH_I_TO_DELTA13;
            case 18:
                return NORTH_I_TO_DELTA18;
            case 4:
                return NORTH_I_TO_DELTA4;
            case 9:
                return NORTH_I_TO_DELTA9;
            case 14:
                return NORTH_I_TO_DELTA14;
            case 19:
                return NORTH_I_TO_DELTA19;
            case 1:
                return NORTH_I_TO_DELTA1;
            case 5:
                return NORTH_I_TO_DELTA5;
            case 10:
                return NORTH_I_TO_DELTA10;
            case 15:
                return NORTH_I_TO_DELTA15;
            case 20:
                return NORTH_I_TO_DELTA20;
            case 6:
                return NORTH_I_TO_DELTA6;
            case 11:
                return NORTH_I_TO_DELTA11;
            case 16:
                return NORTH_I_TO_DELTA16;
            case 21:
                return NORTH_I_TO_DELTA21;
            default: break;
        }

        throw new RuntimeException("bad index" + index);
    }

    public static int[] getNorthSemicircleNeighbors(int index) {
        switch (index) {
            case 0:
                return NORTH_NEIGHBORS0;
            case 1:
                return NORTH_NEIGHBORS1;
            case 2:
                return NORTH_NEIGHBORS2;
            case 3:
                return NORTH_NEIGHBORS3;
            case 4:
                return NORTH_NEIGHBORS4;
            case 5:
                return NORTH_NEIGHBORS5;
            case 6:
                return NORTH_NEIGHBORS6;
            case 7:
                return NORTH_NEIGHBORS7;
            case 8:
                return NORTH_NEIGHBORS8;
            case 9:
                return NORTH_NEIGHBORS9;
            case 10:
                return NORTH_NEIGHBORS10;
            case 11:
                return NORTH_NEIGHBORS11;
            case 12:
                return NORTH_NEIGHBORS12;
            case 13:
                return NORTH_NEIGHBORS13;
            case 14:
                return NORTH_NEIGHBORS14;
            case 15:
                return NORTH_NEIGHBORS15;
            case 16:
                return NORTH_NEIGHBORS16;
            case 17:
                return NORTH_NEIGHBORS17;
            case 18:
                return NORTH_NEIGHBORS18;
            case 19:
                return NORTH_NEIGHBORS19;
            case 20:
                return NORTH_NEIGHBORS20;
            case 21:
                return NORTH_NEIGHBORS21;
            default: break;
        }
        throw new RuntimeException("Bad index");
    }


}
