package pathingprofiler;
import battlecode.common.*;
import baseplayer.nav.NavigationController;

import java.util.Arrays;
import java.util.Map;
import java.util.Queue;

public strictfp class RobotPlayer {
    static RobotController rc;
    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController theRc) throws GameActionException {
        rc = theRc;
        int spawnCount = 0;
        int age = 0;
        NavigationController nav = new NavigationController(rc);
        int[][] deltas = {{0,0},{1,0},{0,-1},{-1,0},{0,1},{2,0},{1,-1},{0,-2},{-1,-1},{-2,0},{-1,1},{0,2},{1,1},{3,0},{2,-1},{1,-2},{0,-3},{-1,-2},{-2,-1},{-3,0},{-2,1},{-1,2},{0,3},{1,2},{2,1},{4,0},{3,-1},{2,-2},{1,-3},{0,-4},{-1,-3},{-2,-2},{-3,-1},{-4,0},{-3,1},{-2,2},{-1,3},{0,4},{1,3},{2,2},{3,1},{4,-1},{3,-2},{2,-3},{1,-4},{-1,-4},{-2,-3},{-3,-2},{-4,-1},{-4,1},{-3,2},{-2,3},{-1,4},{1,4},{2,3},{3,2},{4,1},{4,-2},{3,-3},{2,-4},{-2,-4},{-3,-3},{-4,-2},{-4,2},{-3,3},{-2,4},{2,4},{3,3},{4,2}};
        MapLocation target = rc.getLocation().translate(10, 40);
        while(true){

            if (spawnCount < 1 && rc.canBuildRobot(RobotType.POLITICIAN, Direction.NORTH, 1)){
                rc.buildRobot(RobotType.POLITICIAN, Direction.NORTH, 1);
                spawnCount += 1;
            };

            if (rc.getType() == RobotType.POLITICIAN){

                System.out.println("STARTING ON TURN " + rc.getRoundNum());
                int byteCodeStart = Clock.getBytecodeNum();
                matrixNav(rc, target, 3);
                System.out.println("TOOK " + (Clock.getBytecodeNum() - byteCodeStart) + " ON " + rc.getRoundNum());

            }


            Clock.yield();
            age += 1;
        }
    }


    // radius = Matrix radius size
    static void matrixNav(RobotController rc, MapLocation target, int tilesOut) throws GameActionException {
        MapLocation currentLoc = rc.getLocation();
        int xMin = currentLoc.x - tilesOut;
        int yMin = currentLoc.y - tilesOut;

        int xMax = currentLoc.x + tilesOut;
        int yMax = currentLoc.y + tilesOut;
        int[] scores = new int[4 * tilesOut * tilesOut + 4 * tilesOut + 1];
        int width = 2 * tilesOut + 1;
        for (int i = 0; i < scores.length; i++) {
            int x = i % width + xMin;
            int y = i / width + yMin;
            MapLocation current = new MapLocation(x, y);
            if (rc.onTheMap(current) && !rc.isLocationOccupied(current)){
                scores[i] = current.distanceSquaredTo(target) + (int) ((1 - rc.sensePassability(current)) * 500);
            } else {
                scores[i] = Integer.MAX_VALUE;
            }
        }

        /*
        System.out.println(Arrays.toString(scores));
        for (int iters = 0 ; iters < 3; iters ++){
            for (int i = 0; i < scores.length;i++) {
                int x = i % width + xMin;
                int y = i / width + yMin;
                for (int dx = -1; dx <= 1; dx++){
                    for (int dy=-1; dy<=1; dy++){
                        int neighborX = x + dx;
                        int neighborY = y + dy;

                        int neighborIndex = (neighborY-yMin) * width + (neighborX-xMin);
                        if (neighborX >= xMin && neighborY >= yMin && neighborX <= xMax && neighborY <= yMax && neighborIndex >= 0 && neighborIndex < scores.length){
                            if (scores[i] != Integer.MAX_VALUE && scores[neighborIndex] != Integer.MAX_VALUE) {
                                scores[i] = Math.min(scores[i], scores[neighborIndex] + 1);
                            }
                        }

                    }
                }
            }
        }

         */

        int x = currentLoc.x;
        int y = currentLoc.y;

        MapLocation bestTile = null;
        int bestNearbyScore = Integer.MAX_VALUE;
        for (int dx = -1; dx <= 1; dx++){
            for (int dy=-1; dy<=1; dy++){
                int neighborX = x + dx;
                int neighborY = y + dy;
                if (scores[(neighborY-yMin) * width + (neighborX-xMin)] < bestNearbyScore){
                    bestNearbyScore = scores[(neighborY-yMin) * width + (neighborX-xMin)];
                    bestTile = new MapLocation(neighborX, neighborY);
                }
            }
        }
        System.out.println(Arrays.toString(scores));

        Direction bestDir = currentLoc.directionTo(bestTile);
        if (rc.canMove(bestDir)){
            rc.move(bestDir);
        }
    }
}
