package betterpathing;
import battlecode.common.*;
public class WestPather{
    public static RobotController rc;
    public static final int[] WEST_I_TO_DELTA21 = new int[]{-3,-2};
    public static final int[] WEST_I_TO_DELTA20 = new int[]{-3,-1};
    public static final int[] WEST_I_TO_DELTA19 = new int[]{-3,0};
    public static final int[] WEST_I_TO_DELTA18 = new int[]{-3,1};
    public static final int[] WEST_I_TO_DELTA17 = new int[]{-3,2};
    public static final int[] WEST_I_TO_DELTA14 = new int[]{-2,-2};
    public static final int[] WEST_I_TO_DELTA13 = new int[]{-2,-1};
    public static final int[] WEST_I_TO_DELTA12 = new int[]{-2,0};
    public static final int[] WEST_I_TO_DELTA11 = new int[]{-2,1};
    public static final int[] WEST_I_TO_DELTA10 = new int[]{-2,2};
    public static final int[] WEST_I_TO_DELTA15 = new int[]{-1,-2};
    public static final int[] WEST_I_TO_DELTA3 = new int[]{-1,-1};
    public static final int[] WEST_I_TO_DELTA2 = new int[]{-1,0};
    public static final int[] WEST_I_TO_DELTA1 = new int[]{-1,1};
    public static final int[] WEST_I_TO_DELTA9 = new int[]{-1,2};
    public static final int[] WEST_I_TO_DELTA5 = new int[]{1,-1};
    public static final int[] WEST_I_TO_DELTA6 = new int[]{1,1};
    public static final int[] WEST_I_TO_DELTA16 = new int[]{0,-2};
    public static final int[] WEST_I_TO_DELTA4 = new int[]{0,-1};
    public static final int[] WEST_I_TO_DELTA0 = new int[]{0,0};
    public static final int[] WEST_I_TO_DELTA7 = new int[]{0,1};
    public static final int[] WEST_I_TO_DELTA8 = new int[]{0,2};
    public static final int[] WEST_NEIGHBORS0 = {1, 7, 6, 2, 3, 4, 5};
    public static final int[] WEST_NEIGHBORS1 = {10, 9, 8, 11, 7, 12, 2, 0};
    public static final int[] WEST_NEIGHBORS2 = {11, 1, 7, 12, 0, 13, 3, 4};
    public static final int[] WEST_NEIGHBORS3 = {12, 2, 0, 13, 4, 14, 15, 16};
    public static final int[] WEST_NEIGHBORS4 = {0, 2, 3, 5, 15, 16};
    public static final int[] WEST_NEIGHBORS5 = {0, 4, 16};
    public static final int[] WEST_NEIGHBORS6 = {8, 7, 0};
    public static final int[] WEST_NEIGHBORS7 = {8, 9, 1, 6, 2, 0};
    public static final int[] WEST_NEIGHBORS8 = {9, 1, 7, 6};
    public static final int[] WEST_NEIGHBORS9 = {10, 11, 1, 7, 8};
    public static final int[] WEST_NEIGHBORS10 = {17, 18, 11, 1, 9};
    public static final int[] WEST_NEIGHBORS11 = {17, 10, 9, 18, 1, 19, 12, 2};
    public static final int[] WEST_NEIGHBORS12 = {18, 11, 1, 19, 2, 20, 13, 3};
    public static final int[] WEST_NEIGHBORS13 = {21, 14, 15, 20, 3, 19, 12, 2};
    public static final int[] WEST_NEIGHBORS14 = {21, 20, 13, 3, 15};
    public static final int[] WEST_NEIGHBORS15 = {14, 13, 3, 4, 16};
    public static final int[] WEST_NEIGHBORS16 = {15, 3, 4, 5};
    public static final int[] WEST_NEIGHBORS17 = {10, 18, 11};
    public static final int[] WEST_NEIGHBORS18 = {17, 10, 11, 12, 19};
    public static final int[] WEST_NEIGHBORS19 = {18, 11, 12, 13, 20};
    public static final int[] WEST_NEIGHBORS20 = {19, 12, 13, 14, 21};
    public static final int[] WEST_NEIGHBORS21 = {20, 13, 14};
    static void pathTo(MapLocation targetLoc) throws GameActionException {
        MapLocation currentLoc = rc.getLocation();
        int minDist = Integer.MAX_VALUE;
        MapLocation replacement = null;
        MapLocation candidate = null;
        candidate = new MapLocation(currentLoc.x + 1, currentLoc.y + 1);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x, currentLoc.y + 2);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x + -1, currentLoc.y + 2);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x + -2, currentLoc.y + 2);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x + -3, currentLoc.y + 2);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x + -3, currentLoc.y + 1);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x + -3, currentLoc.y);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x + -3, currentLoc.y + -1);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x + -3, currentLoc.y + -2);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x + -2, currentLoc.y + -2);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x + -1, currentLoc.y + -2);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x, currentLoc.y + -2);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x + 1, currentLoc.y + -1);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        targetLoc = replacement;
        if (targetLoc != null){
            int[] parents = new int[22];
            for (int i = 0; i < 22; i++) {
                parents[i] = -1;
            }
            int distance0 = 99999;
            int weight0;
            int distance1 = 99999;
            int weight1;
            int distance2 = 99999;
            int weight2;
            int distance3 = 99999;
            int weight3;
            int distance4 = 99999;
            int weight4;
            int distance5 = 99999;
            int weight5;
            int distance6 = 99999;
            int weight6;
            int distance7 = 99999;
            int weight7;
            int distance8 = 99999;
            int weight8;
            int distance9 = 99999;
            int weight9;
            int distance10 = 99999;
            int weight10;
            int distance11 = 99999;
            int weight11;
            int distance12 = 99999;
            int weight12;
            int distance13 = 99999;
            int weight13;
            int distance14 = 99999;
            int weight14;
            int distance15 = 99999;
            int weight15;
            int distance16 = 99999;
            int weight16;
            int distance17 = 99999;
            int weight17;
            int distance18 = 99999;
            int weight18;
            int distance19 = 99999;
            int weight19;
            int distance20 = 99999;
            int weight20;
            int distance21 = 99999;
            int weight21;
            MapLocation pos = null;
            pos = rc.getLocation().translate(0, 0);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight0 = 999999;
            } else {
                weight0 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(-1, 1);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight1 = 999999;
            } else {
                weight1 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(-1, 0);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight2 = 999999;
            } else {
                weight2 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(-1, -1);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight3 = 999999;
            } else {
                weight3 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(0, -1);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight4 = 999999;
            } else {
                weight4 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(1, -1);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight5 = 999999;
            } else {
                weight5 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(1, 1);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight6 = 999999;
            } else {
                weight6 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(0, 1);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight7 = 999999;
            } else {
                weight7 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(0, 2);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight8 = 999999;
            } else {
                weight8 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(-1, 2);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight9 = 999999;
            } else {
                weight9 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(-2, 2);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight10 = 999999;
            } else {
                weight10 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(-2, 1);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight11 = 999999;
            } else {
                weight11 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(-2, 0);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight12 = 999999;
            } else {
                weight12 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(-2, -1);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight13 = 999999;
            } else {
                weight13 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(-2, -2);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight14 = 999999;
            } else {
                weight14 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(-1, -2);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight15 = 999999;
            } else {
                weight15 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(0, -2);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight16 = 999999;
            } else {
                weight16 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(-3, 2);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight17 = 999999;
            } else {
                weight17 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(-3, 1);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight18 = 999999;
            } else {
                weight18 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(-3, 0);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight19 = 999999;
            } else {
                weight19 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(-3, -1);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight20 = 999999;
            } else {
                weight20 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(-3, -2);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight21 = 999999;
            } else {
                weight21 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            distance0 = 0;
            int newDist;
            newDist = distance0 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 0;
            }
            newDist = distance0 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 0;
            }
            newDist = distance0 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 0;
            }
            newDist = distance0 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 0;
            }
            newDist = distance0 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 0;
            }
            newDist = distance0 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 0;
            }
            newDist = distance0 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 0;
            }
            newDist = distance1 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 1;
            }
            newDist = distance1 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 1;
            }
            newDist = distance1 + weight8;
            if (newDist < distance8) {
                distance8 = newDist;
                parents[8] = 1;
            }
            newDist = distance1 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 1;
            }
            newDist = distance1 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 1;
            }
            newDist = distance1 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 1;
            }
            newDist = distance1 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 1;
            }
            newDist = distance1 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 1;
            }
            newDist = distance2 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 2;
            }
            newDist = distance2 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 2;
            }
            newDist = distance2 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 2;
            }
            newDist = distance2 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 2;
            }
            newDist = distance2 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 2;
            }
            newDist = distance2 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 2;
            }
            newDist = distance2 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 2;
            }
            newDist = distance2 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 2;
            }
            newDist = distance3 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 3;
            }
            newDist = distance3 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 3;
            }
            newDist = distance3 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 3;
            }
            newDist = distance3 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 3;
            }
            newDist = distance3 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 3;
            }
            newDist = distance3 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 3;
            }
            newDist = distance3 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 3;
            }
            newDist = distance3 + weight16;
            if (newDist < distance16) {
                distance16 = newDist;
                parents[16] = 3;
            }
            newDist = distance4 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 4;
            }
            newDist = distance4 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 4;
            }
            newDist = distance4 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 4;
            }
            newDist = distance4 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 4;
            }
            newDist = distance4 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 4;
            }
            newDist = distance4 + weight16;
            if (newDist < distance16) {
                distance16 = newDist;
                parents[16] = 4;
            }
            newDist = distance5 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 5;
            }
            newDist = distance5 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 5;
            }
            newDist = distance5 + weight16;
            if (newDist < distance16) {
                distance16 = newDist;
                parents[16] = 5;
            }
            newDist = distance6 + weight8;
            if (newDist < distance8) {
                distance8 = newDist;
                parents[8] = 6;
            }
            newDist = distance6 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 6;
            }
            newDist = distance6 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 6;
            }
            newDist = distance7 + weight8;
            if (newDist < distance8) {
                distance8 = newDist;
                parents[8] = 7;
            }
            newDist = distance7 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 7;
            }
            newDist = distance7 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 7;
            }
            newDist = distance7 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 7;
            }
            newDist = distance7 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 7;
            }
            newDist = distance7 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 7;
            }
            newDist = distance8 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 8;
            }
            newDist = distance8 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 8;
            }
            newDist = distance8 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 8;
            }
            newDist = distance8 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 8;
            }
            newDist = distance9 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 9;
            }
            newDist = distance9 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 9;
            }
            newDist = distance9 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 9;
            }
            newDist = distance9 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 9;
            }
            newDist = distance9 + weight8;
            if (newDist < distance8) {
                distance8 = newDist;
                parents[8] = 9;
            }
            newDist = distance10 + weight17;
            if (newDist < distance17) {
                distance17 = newDist;
                parents[17] = 10;
            }
            newDist = distance10 + weight18;
            if (newDist < distance18) {
                distance18 = newDist;
                parents[18] = 10;
            }
            newDist = distance10 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 10;
            }
            newDist = distance10 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 10;
            }
            newDist = distance10 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 10;
            }
            newDist = distance11 + weight17;
            if (newDist < distance17) {
                distance17 = newDist;
                parents[17] = 11;
            }
            newDist = distance11 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 11;
            }
            newDist = distance11 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 11;
            }
            newDist = distance11 + weight18;
            if (newDist < distance18) {
                distance18 = newDist;
                parents[18] = 11;
            }
            newDist = distance11 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 11;
            }
            newDist = distance11 + weight19;
            if (newDist < distance19) {
                distance19 = newDist;
                parents[19] = 11;
            }
            newDist = distance11 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 11;
            }
            newDist = distance11 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 11;
            }
            newDist = distance12 + weight18;
            if (newDist < distance18) {
                distance18 = newDist;
                parents[18] = 12;
            }
            newDist = distance12 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 12;
            }
            newDist = distance12 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 12;
            }
            newDist = distance12 + weight19;
            if (newDist < distance19) {
                distance19 = newDist;
                parents[19] = 12;
            }
            newDist = distance12 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 12;
            }
            newDist = distance12 + weight20;
            if (newDist < distance20) {
                distance20 = newDist;
                parents[20] = 12;
            }
            newDist = distance12 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 12;
            }
            newDist = distance12 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 12;
            }
            newDist = distance13 + weight21;
            if (newDist < distance21) {
                distance21 = newDist;
                parents[21] = 13;
            }
            newDist = distance13 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 13;
            }
            newDist = distance13 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 13;
            }
            newDist = distance13 + weight20;
            if (newDist < distance20) {
                distance20 = newDist;
                parents[20] = 13;
            }
            newDist = distance13 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 13;
            }
            newDist = distance13 + weight19;
            if (newDist < distance19) {
                distance19 = newDist;
                parents[19] = 13;
            }
            newDist = distance13 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 13;
            }
            newDist = distance13 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 13;
            }
            newDist = distance14 + weight21;
            if (newDist < distance21) {
                distance21 = newDist;
                parents[21] = 14;
            }
            newDist = distance14 + weight20;
            if (newDist < distance20) {
                distance20 = newDist;
                parents[20] = 14;
            }
            newDist = distance14 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 14;
            }
            newDist = distance14 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 14;
            }
            newDist = distance14 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 14;
            }
            newDist = distance15 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 15;
            }
            newDist = distance15 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 15;
            }
            newDist = distance15 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 15;
            }
            newDist = distance15 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 15;
            }
            newDist = distance15 + weight16;
            if (newDist < distance16) {
                distance16 = newDist;
                parents[16] = 15;
            }
            newDist = distance16 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 16;
            }
            newDist = distance16 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 16;
            }
            newDist = distance16 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 16;
            }
            newDist = distance16 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 16;
            }
            newDist = distance17 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 17;
            }
            newDist = distance17 + weight18;
            if (newDist < distance18) {
                distance18 = newDist;
                parents[18] = 17;
            }
            newDist = distance17 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 17;
            }
            newDist = distance18 + weight17;
            if (newDist < distance17) {
                distance17 = newDist;
                parents[17] = 18;
            }
            newDist = distance18 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 18;
            }
            newDist = distance18 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 18;
            }
            newDist = distance18 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 18;
            }
            newDist = distance18 + weight19;
            if (newDist < distance19) {
                distance19 = newDist;
                parents[19] = 18;
            }
            newDist = distance19 + weight18;
            if (newDist < distance18) {
                distance18 = newDist;
                parents[18] = 19;
            }
            newDist = distance19 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 19;
            }
            newDist = distance19 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 19;
            }
            newDist = distance19 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 19;
            }
            newDist = distance19 + weight20;
            if (newDist < distance20) {
                distance20 = newDist;
                parents[20] = 19;
            }
            newDist = distance20 + weight19;
            if (newDist < distance19) {
                distance19 = newDist;
                parents[19] = 20;
            }
            newDist = distance20 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 20;
            }
            newDist = distance20 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 20;
            }
            newDist = distance20 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 20;
            }
            newDist = distance20 + weight21;
            if (newDist < distance21) {
                distance21 = newDist;
                parents[21] = 20;
            }
            newDist = distance21 + weight20;
            if (newDist < distance20) {
                distance20 = newDist;
                parents[20] = 21;
            }
            newDist = distance21 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 21;
            }
            newDist = distance21 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 21;
            }
            newDist = distance0 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 0;
            }
            newDist = distance0 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 0;
            }
            newDist = distance0 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 0;
            }
            newDist = distance0 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 0;
            }
            newDist = distance0 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 0;
            }
            newDist = distance0 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 0;
            }
            newDist = distance0 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 0;
            }
            newDist = distance1 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 1;
            }
            newDist = distance1 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 1;
            }
            newDist = distance1 + weight8;
            if (newDist < distance8) {
                distance8 = newDist;
                parents[8] = 1;
            }
            newDist = distance1 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 1;
            }
            newDist = distance1 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 1;
            }
            newDist = distance1 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 1;
            }
            newDist = distance1 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 1;
            }
            newDist = distance1 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 1;
            }
            newDist = distance2 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 2;
            }
            newDist = distance2 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 2;
            }
            newDist = distance2 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 2;
            }
            newDist = distance2 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 2;
            }
            newDist = distance2 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 2;
            }
            newDist = distance2 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 2;
            }
            newDist = distance2 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 2;
            }
            newDist = distance2 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 2;
            }
            newDist = distance3 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 3;
            }
            newDist = distance3 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 3;
            }
            newDist = distance3 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 3;
            }
            newDist = distance3 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 3;
            }
            newDist = distance3 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 3;
            }
            newDist = distance3 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 3;
            }
            newDist = distance3 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 3;
            }
            newDist = distance3 + weight16;
            if (newDist < distance16) {
                distance16 = newDist;
                parents[16] = 3;
            }
            newDist = distance4 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 4;
            }
            newDist = distance4 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 4;
            }
            newDist = distance4 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 4;
            }
            newDist = distance4 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 4;
            }
            newDist = distance4 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 4;
            }
            newDist = distance4 + weight16;
            if (newDist < distance16) {
                distance16 = newDist;
                parents[16] = 4;
            }
            newDist = distance5 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 5;
            }
            newDist = distance5 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 5;
            }
            newDist = distance5 + weight16;
            if (newDist < distance16) {
                distance16 = newDist;
                parents[16] = 5;
            }
            newDist = distance6 + weight8;
            if (newDist < distance8) {
                distance8 = newDist;
                parents[8] = 6;
            }
            newDist = distance6 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 6;
            }
            newDist = distance6 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 6;
            }
            newDist = distance7 + weight8;
            if (newDist < distance8) {
                distance8 = newDist;
                parents[8] = 7;
            }
            newDist = distance7 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 7;
            }
            newDist = distance7 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 7;
            }
            newDist = distance7 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 7;
            }
            newDist = distance7 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 7;
            }
            newDist = distance7 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 7;
            }
            newDist = distance8 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 8;
            }
            newDist = distance8 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 8;
            }
            newDist = distance8 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 8;
            }
            newDist = distance8 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 8;
            }
            newDist = distance9 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 9;
            }
            newDist = distance9 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 9;
            }
            newDist = distance9 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 9;
            }
            newDist = distance9 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 9;
            }
            newDist = distance9 + weight8;
            if (newDist < distance8) {
                distance8 = newDist;
                parents[8] = 9;
            }
            newDist = distance10 + weight17;
            if (newDist < distance17) {
                distance17 = newDist;
                parents[17] = 10;
            }
            newDist = distance10 + weight18;
            if (newDist < distance18) {
                distance18 = newDist;
                parents[18] = 10;
            }
            newDist = distance10 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 10;
            }
            newDist = distance10 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 10;
            }
            newDist = distance10 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 10;
            }
            newDist = distance11 + weight17;
            if (newDist < distance17) {
                distance17 = newDist;
                parents[17] = 11;
            }
            newDist = distance11 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 11;
            }
            newDist = distance11 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 11;
            }
            newDist = distance11 + weight18;
            if (newDist < distance18) {
                distance18 = newDist;
                parents[18] = 11;
            }
            newDist = distance11 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 11;
            }
            newDist = distance11 + weight19;
            if (newDist < distance19) {
                distance19 = newDist;
                parents[19] = 11;
            }
            newDist = distance11 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 11;
            }
            newDist = distance11 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 11;
            }
            newDist = distance12 + weight18;
            if (newDist < distance18) {
                distance18 = newDist;
                parents[18] = 12;
            }
            newDist = distance12 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 12;
            }
            newDist = distance12 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 12;
            }
            newDist = distance12 + weight19;
            if (newDist < distance19) {
                distance19 = newDist;
                parents[19] = 12;
            }
            newDist = distance12 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 12;
            }
            newDist = distance12 + weight20;
            if (newDist < distance20) {
                distance20 = newDist;
                parents[20] = 12;
            }
            newDist = distance12 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 12;
            }
            newDist = distance12 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 12;
            }
            newDist = distance13 + weight21;
            if (newDist < distance21) {
                distance21 = newDist;
                parents[21] = 13;
            }
            newDist = distance13 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 13;
            }
            newDist = distance13 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 13;
            }
            newDist = distance13 + weight20;
            if (newDist < distance20) {
                distance20 = newDist;
                parents[20] = 13;
            }
            newDist = distance13 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 13;
            }
            newDist = distance13 + weight19;
            if (newDist < distance19) {
                distance19 = newDist;
                parents[19] = 13;
            }
            newDist = distance13 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 13;
            }
            newDist = distance13 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 13;
            }
            newDist = distance14 + weight21;
            if (newDist < distance21) {
                distance21 = newDist;
                parents[21] = 14;
            }
            newDist = distance14 + weight20;
            if (newDist < distance20) {
                distance20 = newDist;
                parents[20] = 14;
            }
            newDist = distance14 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 14;
            }
            newDist = distance14 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 14;
            }
            newDist = distance14 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 14;
            }
            newDist = distance15 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 15;
            }
            newDist = distance15 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 15;
            }
            newDist = distance15 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 15;
            }
            newDist = distance15 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 15;
            }
            newDist = distance15 + weight16;
            if (newDist < distance16) {
                distance16 = newDist;
                parents[16] = 15;
            }
            newDist = distance16 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 16;
            }
            newDist = distance16 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 16;
            }
            newDist = distance16 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 16;
            }
            newDist = distance16 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 16;
            }
            newDist = distance17 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 17;
            }
            newDist = distance17 + weight18;
            if (newDist < distance18) {
                distance18 = newDist;
                parents[18] = 17;
            }
            newDist = distance17 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 17;
            }
            newDist = distance18 + weight17;
            if (newDist < distance17) {
                distance17 = newDist;
                parents[17] = 18;
            }
            newDist = distance18 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 18;
            }
            newDist = distance18 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 18;
            }
            newDist = distance18 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 18;
            }
            newDist = distance18 + weight19;
            if (newDist < distance19) {
                distance19 = newDist;
                parents[19] = 18;
            }
            newDist = distance19 + weight18;
            if (newDist < distance18) {
                distance18 = newDist;
                parents[18] = 19;
            }
            newDist = distance19 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 19;
            }
            newDist = distance19 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 19;
            }
            newDist = distance19 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 19;
            }
            newDist = distance19 + weight20;
            if (newDist < distance20) {
                distance20 = newDist;
                parents[20] = 19;
            }
            newDist = distance20 + weight19;
            if (newDist < distance19) {
                distance19 = newDist;
                parents[19] = 20;
            }
            newDist = distance20 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 20;
            }
            newDist = distance20 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 20;
            }
            newDist = distance20 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 20;
            }
            newDist = distance20 + weight21;
            if (newDist < distance21) {
                distance21 = newDist;
                parents[21] = 20;
            }
            newDist = distance21 + weight20;
            if (newDist < distance20) {
                distance20 = newDist;
                parents[20] = 21;
            }
            newDist = distance21 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 21;
            }
            newDist = distance21 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 21;
            }
            newDist = distance0 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 0;
            }
            newDist = distance0 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 0;
            }
            newDist = distance0 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 0;
            }
            newDist = distance0 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 0;
            }
            newDist = distance0 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 0;
            }
            newDist = distance0 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 0;
            }
            newDist = distance0 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 0;
            }
            newDist = distance1 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 1;
            }
            newDist = distance1 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 1;
            }
            newDist = distance1 + weight8;
            if (newDist < distance8) {
                distance8 = newDist;
                parents[8] = 1;
            }
            newDist = distance1 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 1;
            }
            newDist = distance1 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 1;
            }
            newDist = distance1 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 1;
            }
            newDist = distance1 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 1;
            }
            newDist = distance1 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 1;
            }
            newDist = distance2 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 2;
            }
            newDist = distance2 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 2;
            }
            newDist = distance2 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 2;
            }
            newDist = distance2 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 2;
            }
            newDist = distance2 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 2;
            }
            newDist = distance2 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 2;
            }
            newDist = distance2 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 2;
            }
            newDist = distance2 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 2;
            }
            newDist = distance3 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 3;
            }
            newDist = distance3 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 3;
            }
            newDist = distance3 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 3;
            }
            newDist = distance3 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 3;
            }
            newDist = distance3 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 3;
            }
            newDist = distance3 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 3;
            }
            newDist = distance3 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 3;
            }
            newDist = distance3 + weight16;
            if (newDist < distance16) {
                distance16 = newDist;
                parents[16] = 3;
            }
            newDist = distance4 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 4;
            }
            newDist = distance4 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 4;
            }
            newDist = distance4 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 4;
            }
            newDist = distance4 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 4;
            }
            newDist = distance4 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 4;
            }
            newDist = distance4 + weight16;
            if (newDist < distance16) {
                distance16 = newDist;
                parents[16] = 4;
            }
            newDist = distance5 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 5;
            }
            newDist = distance5 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 5;
            }
            newDist = distance5 + weight16;
            if (newDist < distance16) {
                distance16 = newDist;
                parents[16] = 5;
            }
            newDist = distance6 + weight8;
            if (newDist < distance8) {
                distance8 = newDist;
                parents[8] = 6;
            }
            newDist = distance6 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 6;
            }
            newDist = distance6 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 6;
            }
            newDist = distance7 + weight8;
            if (newDist < distance8) {
                distance8 = newDist;
                parents[8] = 7;
            }
            newDist = distance7 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 7;
            }
            newDist = distance7 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 7;
            }
            newDist = distance7 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 7;
            }
            newDist = distance7 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 7;
            }
            newDist = distance7 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 7;
            }
            newDist = distance8 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 8;
            }
            newDist = distance8 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 8;
            }
            newDist = distance8 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 8;
            }
            newDist = distance8 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 8;
            }
            newDist = distance9 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 9;
            }
            newDist = distance9 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 9;
            }
            newDist = distance9 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 9;
            }
            newDist = distance9 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 9;
            }
            newDist = distance9 + weight8;
            if (newDist < distance8) {
                distance8 = newDist;
                parents[8] = 9;
            }
            newDist = distance10 + weight17;
            if (newDist < distance17) {
                distance17 = newDist;
                parents[17] = 10;
            }
            newDist = distance10 + weight18;
            if (newDist < distance18) {
                distance18 = newDist;
                parents[18] = 10;
            }
            newDist = distance10 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 10;
            }
            newDist = distance10 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 10;
            }
            newDist = distance10 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 10;
            }
            newDist = distance11 + weight17;
            if (newDist < distance17) {
                distance17 = newDist;
                parents[17] = 11;
            }
            newDist = distance11 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 11;
            }
            newDist = distance11 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 11;
            }
            newDist = distance11 + weight18;
            if (newDist < distance18) {
                distance18 = newDist;
                parents[18] = 11;
            }
            newDist = distance11 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 11;
            }
            newDist = distance11 + weight19;
            if (newDist < distance19) {
                distance19 = newDist;
                parents[19] = 11;
            }
            newDist = distance11 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 11;
            }
            newDist = distance11 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 11;
            }
            newDist = distance12 + weight18;
            if (newDist < distance18) {
                distance18 = newDist;
                parents[18] = 12;
            }
            newDist = distance12 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 12;
            }
            newDist = distance12 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 12;
            }
            newDist = distance12 + weight19;
            if (newDist < distance19) {
                distance19 = newDist;
                parents[19] = 12;
            }
            newDist = distance12 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 12;
            }
            newDist = distance12 + weight20;
            if (newDist < distance20) {
                distance20 = newDist;
                parents[20] = 12;
            }
            newDist = distance12 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 12;
            }
            newDist = distance12 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 12;
            }
            newDist = distance13 + weight21;
            if (newDist < distance21) {
                distance21 = newDist;
                parents[21] = 13;
            }
            newDist = distance13 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 13;
            }
            newDist = distance13 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 13;
            }
            newDist = distance13 + weight20;
            if (newDist < distance20) {
                distance20 = newDist;
                parents[20] = 13;
            }
            newDist = distance13 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 13;
            }
            newDist = distance13 + weight19;
            if (newDist < distance19) {
                distance19 = newDist;
                parents[19] = 13;
            }
            newDist = distance13 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 13;
            }
            newDist = distance13 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 13;
            }
            newDist = distance14 + weight21;
            if (newDist < distance21) {
                distance21 = newDist;
                parents[21] = 14;
            }
            newDist = distance14 + weight20;
            if (newDist < distance20) {
                distance20 = newDist;
                parents[20] = 14;
            }
            newDist = distance14 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 14;
            }
            newDist = distance14 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 14;
            }
            newDist = distance14 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 14;
            }
            newDist = distance15 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 15;
            }
            newDist = distance15 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 15;
            }
            newDist = distance15 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 15;
            }
            newDist = distance15 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 15;
            }
            newDist = distance15 + weight16;
            if (newDist < distance16) {
                distance16 = newDist;
                parents[16] = 15;
            }
            newDist = distance16 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 16;
            }
            newDist = distance16 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 16;
            }
            newDist = distance16 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 16;
            }
            newDist = distance16 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 16;
            }
            newDist = distance17 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 17;
            }
            newDist = distance17 + weight18;
            if (newDist < distance18) {
                distance18 = newDist;
                parents[18] = 17;
            }
            newDist = distance17 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 17;
            }
            newDist = distance18 + weight17;
            if (newDist < distance17) {
                distance17 = newDist;
                parents[17] = 18;
            }
            newDist = distance18 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 18;
            }
            newDist = distance18 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 18;
            }
            newDist = distance18 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 18;
            }
            newDist = distance18 + weight19;
            if (newDist < distance19) {
                distance19 = newDist;
                parents[19] = 18;
            }
            newDist = distance19 + weight18;
            if (newDist < distance18) {
                distance18 = newDist;
                parents[18] = 19;
            }
            newDist = distance19 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 19;
            }
            newDist = distance19 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 19;
            }
            newDist = distance19 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 19;
            }
            newDist = distance19 + weight20;
            if (newDist < distance20) {
                distance20 = newDist;
                parents[20] = 19;
            }
            newDist = distance20 + weight19;
            if (newDist < distance19) {
                distance19 = newDist;
                parents[19] = 20;
            }
            newDist = distance20 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 20;
            }
            newDist = distance20 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 20;
            }
            newDist = distance20 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 20;
            }
            newDist = distance20 + weight21;
            if (newDist < distance21) {
                distance21 = newDist;
                parents[21] = 20;
            }
            newDist = distance21 + weight20;
            if (newDist < distance20) {
                distance20 = newDist;
                parents[20] = 21;
            }
            newDist = distance21 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 21;
            }
            newDist = distance21 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 21;
            }
            int current = westDeltaToIndex(targetLoc.x - currentLoc.x, targetLoc.y - currentLoc.y);
            int last = -1;
            while (parents[current] != -1) {
                last = current;
                current = parents[current];
            }
            if (last != -1){
                int[] delta = westIndexToDelta(last);
                MapLocation target = new MapLocation(rc.getLocation().x + delta[0],  rc.getLocation().y + delta[1]);
                if (rc.canMove(rc.getLocation().directionTo(target))){
                    rc.move(rc.getLocation().directionTo(target));
                }
            }
        }
    }
    static int westDeltaToIndex(int x, int y) {
        String delta = x + "|" + y;
        switch (delta) {
            case "-3|-2":
                return 21;
            case "-3|-1":
                return 20;
            case "-3|0":
                return 19;
            case "-3|1":
                return 18;
            case "-3|2":
                return 17;
            case "-2|-2":
                return 14;
            case "-2|-1":
                return 13;
            case "-2|0":
                return 12;
            case "-2|1":
                return 11;
            case "-2|2":
                return 10;
            case "-1|-2":
                return 15;
            case "-1|-1":
                return 3;
            case "-1|0":
                return 2;
            case "-1|1":
                return 1;
            case "-1|2":
                return 9;
            case "1|-1":
                return 5;
            case "1|1":
                return 6;
            case "0|-2":
                return 16;
            case "0|-1":
                return 4;
            case "0|0":
                return 0;
            case "0|1":
                return 7;
            case "0|2":
                return 8;
            default: break;
        }
        throw new RuntimeException("Bad delta");
    }
    static int[] westIndexToDelta(int index) {
        switch (index) {
            case 21:
                return WEST_I_TO_DELTA21;
            case 20:
                return WEST_I_TO_DELTA20;
            case 19:
                return WEST_I_TO_DELTA19;
            case 18:
                return WEST_I_TO_DELTA18;
            case 17:
                return WEST_I_TO_DELTA17;
            case 14:
                return WEST_I_TO_DELTA14;
            case 13:
                return WEST_I_TO_DELTA13;
            case 12:
                return WEST_I_TO_DELTA12;
            case 11:
                return WEST_I_TO_DELTA11;
            case 10:
                return WEST_I_TO_DELTA10;
            case 15:
                return WEST_I_TO_DELTA15;
            case 3:
                return WEST_I_TO_DELTA3;
            case 2:
                return WEST_I_TO_DELTA2;
            case 1:
                return WEST_I_TO_DELTA1;
            case 9:
                return WEST_I_TO_DELTA9;
            case 5:
                return WEST_I_TO_DELTA5;
            case 6:
                return WEST_I_TO_DELTA6;
            case 16:
                return WEST_I_TO_DELTA16;
            case 4:
                return WEST_I_TO_DELTA4;
            case 0:
                return WEST_I_TO_DELTA0;
            case 7:
                return WEST_I_TO_DELTA7;
            case 8:
                return WEST_I_TO_DELTA8;
            default: break;
        }
        throw new RuntimeException("Bad index");
    }
    static int[] getWestSemicircleNeighbors(int index) {
        switch (index) {
            case 0:
                return WEST_NEIGHBORS0;
            case 1:
                return WEST_NEIGHBORS1;
            case 2:
                return WEST_NEIGHBORS2;
            case 3:
                return WEST_NEIGHBORS3;
            case 4:
                return WEST_NEIGHBORS4;
            case 5:
                return WEST_NEIGHBORS5;
            case 6:
                return WEST_NEIGHBORS6;
            case 7:
                return WEST_NEIGHBORS7;
            case 8:
                return WEST_NEIGHBORS8;
            case 9:
                return WEST_NEIGHBORS9;
            case 10:
                return WEST_NEIGHBORS10;
            case 11:
                return WEST_NEIGHBORS11;
            case 12:
                return WEST_NEIGHBORS12;
            case 13:
                return WEST_NEIGHBORS13;
            case 14:
                return WEST_NEIGHBORS14;
            case 15:
                return WEST_NEIGHBORS15;
            case 16:
                return WEST_NEIGHBORS16;
            case 17:
                return WEST_NEIGHBORS17;
            case 18:
                return WEST_NEIGHBORS18;
            case 19:
                return WEST_NEIGHBORS19;
            case 20:
                return WEST_NEIGHBORS20;
            case 21:
                return WEST_NEIGHBORS21;
            default: break;
        }
        throw new RuntimeException("Bad index");
    }
    public static void init(RobotController rc) {
        WestPather.rc = rc;
    }
    static void testMap() {
        for (int i=0;i<22;i++) {
            for (int j=0;j<22 ;j++) {
                int[] delta1 = westIndexToDelta(j);
                rc.setIndicatorDot(new MapLocation(rc.getLocation().x + delta1[0], rc.getLocation().y + delta1[1]), 255, 0, 0);
            }
            for (int neighborIndex : getWestSemicircleNeighbors(i)) {
                int[] delta = westIndexToDelta(neighborIndex);
                rc.setIndicatorDot(new MapLocation(rc.getLocation().x + delta[0], rc.getLocation().y + delta[1]), 0, 0, 255);
            }
            Clock.yield();
        }
    }
}
