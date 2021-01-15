package baseplayer.nav.bellmanford;
import battlecode.common.*;
public class SoutheastPather{
    public static RobotController rc;
    public static final int[] SOUTHEAST_I_TO_DELTA8 = new int[]{-2,-1};
    public static final int[] SOUTHEAST_I_TO_DELTA9 = new int[]{-1,-2};
    public static final int[] SOUTHEAST_I_TO_DELTA2 = new int[]{-1,-1};
    public static final int[] SOUTHEAST_I_TO_DELTA1 = new int[]{-1,0};
    public static final int[] SOUTHEAST_I_TO_DELTA17 = new int[]{0,-3};
    public static final int[] SOUTHEAST_I_TO_DELTA10 = new int[]{0,-2};
    public static final int[] SOUTHEAST_I_TO_DELTA3 = new int[]{0,-1};
    public static final int[] SOUTHEAST_I_TO_DELTA0 = new int[]{0,0};
    public static final int[] SOUTHEAST_I_TO_DELTA7 = new int[]{0,1};
    public static final int[] SOUTHEAST_I_TO_DELTA18 = new int[]{1,-3};
    public static final int[] SOUTHEAST_I_TO_DELTA11 = new int[]{1,-2};
    public static final int[] SOUTHEAST_I_TO_DELTA4 = new int[]{1,-1};
    public static final int[] SOUTHEAST_I_TO_DELTA5 = new int[]{1,0};
    public static final int[] SOUTHEAST_I_TO_DELTA6 = new int[]{1,1};
    public static final int[] SOUTHEAST_I_TO_DELTA16 = new int[]{1,2};
    public static final int[] SOUTHEAST_I_TO_DELTA19 = new int[]{2,-3};
    public static final int[] SOUTHEAST_I_TO_DELTA12 = new int[]{2,-2};
    public static final int[] SOUTHEAST_I_TO_DELTA13 = new int[]{2,-1};
    public static final int[] SOUTHEAST_I_TO_DELTA14 = new int[]{2,0};
    public static final int[] SOUTHEAST_I_TO_DELTA15 = new int[]{2,1};
    public static final int[] SOUTHEAST_I_TO_DELTA20 = new int[]{3,-2};
    public static final int[] SOUTHEAST_I_TO_DELTA21 = new int[]{3,-1};
    public static final int[] SOUTHEAST_I_TO_DELTA22 = new int[]{3,0};
    public static final int[] SOUTHEAST_NEIGHBORS0 = {1, 2, 3, 4, 5, 6, 7};
    public static final int[] SOUTHEAST_NEIGHBORS1 = {8, 2, 3, 0, 7};
    public static final int[] SOUTHEAST_NEIGHBORS2 = {9, 10, 8, 3, 1, 0};
    public static final int[] SOUTHEAST_NEIGHBORS3 = {9, 10, 11, 2, 4, 1, 0, 5};
    public static final int[] SOUTHEAST_NEIGHBORS4 = {10, 11, 12, 3, 13, 0, 5, 14};
    public static final int[] SOUTHEAST_NEIGHBORS5 = {3, 4, 13, 0, 14, 7, 6, 15};
    public static final int[] SOUTHEAST_NEIGHBORS6 = {0, 5, 14, 7, 15, 16};
    public static final int[] SOUTHEAST_NEIGHBORS7 = {1, 0, 5, 6, 16};
    public static final int[] SOUTHEAST_NEIGHBORS8 = {9, 2, 1};
    public static final int[] SOUTHEAST_NEIGHBORS9 = {17, 10, 8, 2, 3};
    public static final int[] SOUTHEAST_NEIGHBORS10 = {17, 18, 9, 11, 2, 3, 4};
    public static final int[] SOUTHEAST_NEIGHBORS11 = {17, 18, 19, 10, 12, 3, 4, 13};
    public static final int[] SOUTHEAST_NEIGHBORS12 = {18, 19, 11, 20, 4, 13, 21};
    public static final int[] SOUTHEAST_NEIGHBORS13 = {11, 12, 20, 4, 21, 5, 14, 22};
    public static final int[] SOUTHEAST_NEIGHBORS14 = {4, 13, 21, 5, 22, 6, 15};
    public static final int[] SOUTHEAST_NEIGHBORS15 = {5, 14, 22, 6, 16};
    public static final int[] SOUTHEAST_NEIGHBORS16 = {7, 6, 15};
    public static final int[] SOUTHEAST_NEIGHBORS17 = {9, 10, 11, 18};
    public static final int[] SOUTHEAST_NEIGHBORS18 = {17, 10, 11, 12, 19};
    public static final int[] SOUTHEAST_NEIGHBORS19 = {18, 11, 12, 20};
    public static final int[] SOUTHEAST_NEIGHBORS20 = {19, 12, 13, 21};
    public static final int[] SOUTHEAST_NEIGHBORS21 = {12, 20, 13, 14, 22};
    public static final int[] SOUTHEAST_NEIGHBORS22 = {13, 21, 14, 15};
    static void pathTo(MapLocation targetLoc) throws GameActionException {
        MapLocation currentLoc = rc.getLocation();
        int minDist = Integer.MAX_VALUE;
        MapLocation replacement = null;
        MapLocation candidate = null;
        candidate = new MapLocation(currentLoc.x, currentLoc.y);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x + -1, currentLoc.y);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x + -1, currentLoc.y + -1);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x, currentLoc.y + -1);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x + 1, currentLoc.y + -1);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x + 1, currentLoc.y);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x + 1, currentLoc.y + 1);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x, currentLoc.y + 1);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x + -2, currentLoc.y + -1);
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
        candidate = new MapLocation(currentLoc.x + 1, currentLoc.y + -2);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x + 2, currentLoc.y + -2);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x + 2, currentLoc.y + -1);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x + 2, currentLoc.y);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x + 2, currentLoc.y + 1);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x + 1, currentLoc.y + 2);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x, currentLoc.y + -3);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x + 1, currentLoc.y + -3);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x + 2, currentLoc.y + -3);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x + 3, currentLoc.y + -2);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x + 3, currentLoc.y + -1);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        candidate = new MapLocation(currentLoc.x + 3, currentLoc.y);
        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
            minDist = candidate.distanceSquaredTo(targetLoc);
            replacement = candidate;
        }
        targetLoc = replacement;
        if (targetLoc != null){
            int[] parents = new int[23];
            for (int i = 0; i < 23; i++) {
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
            int distance22 = 99999;
            int weight22;
            MapLocation pos = null;
            pos = rc.getLocation().translate(0, 0);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight0 = 999999;
            } else {
                weight0 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(-1, 0);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight1 = 999999;
            } else {
                weight1 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(-1, -1);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight2 = 999999;
            } else {
                weight2 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(0, -1);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight3 = 999999;
            } else {
                weight3 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(1, -1);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight4 = 999999;
            } else {
                weight4 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(1, 0);
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
            pos = rc.getLocation().translate(-2, -1);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight8 = 999999;
            } else {
                weight8 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(-1, -2);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight9 = 999999;
            } else {
                weight9 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(0, -2);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight10 = 999999;
            } else {
                weight10 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(1, -2);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight11 = 999999;
            } else {
                weight11 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(2, -2);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight12 = 999999;
            } else {
                weight12 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(2, -1);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight13 = 999999;
            } else {
                weight13 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(2, 0);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight14 = 999999;
            } else {
                weight14 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(2, 1);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight15 = 999999;
            } else {
                weight15 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(1, 2);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight16 = 999999;
            } else {
                weight16 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(0, -3);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight17 = 999999;
            } else {
                weight17 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(1, -3);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight18 = 999999;
            } else {
                weight18 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(2, -3);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight19 = 999999;
            } else {
                weight19 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(3, -2);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight20 = 999999;
            } else {
                weight20 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(3, -1);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight21 = 999999;
            } else {
                weight21 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            pos = rc.getLocation().translate(3, 0);
            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {
                weight22 = 999999;
            } else {
                weight22 = (int) (100 * ((1 - rc.sensePassability(pos))));
            }
            distance0 = 0;
            int newDist;
            newDist = distance0 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 0;
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
            newDist = distance0 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 0;
            }
            newDist = distance0 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 0;
            }
            newDist = distance1 + weight8;
            if (newDist < distance8) {
                distance8 = newDist;
                parents[8] = 1;
            }
            newDist = distance1 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 1;
            }
            newDist = distance1 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 1;
            }
            newDist = distance1 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 1;
            }
            newDist = distance1 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 1;
            }
            newDist = distance2 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 2;
            }
            newDist = distance2 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 2;
            }
            newDist = distance2 + weight8;
            if (newDist < distance8) {
                distance8 = newDist;
                parents[8] = 2;
            }
            newDist = distance2 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 2;
            }
            newDist = distance2 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 2;
            }
            newDist = distance2 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 2;
            }
            newDist = distance3 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 3;
            }
            newDist = distance3 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 3;
            }
            newDist = distance3 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 3;
            }
            newDist = distance3 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 3;
            }
            newDist = distance3 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 3;
            }
            newDist = distance3 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 3;
            }
            newDist = distance3 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 3;
            }
            newDist = distance3 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 3;
            }
            newDist = distance4 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 4;
            }
            newDist = distance4 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 4;
            }
            newDist = distance4 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 4;
            }
            newDist = distance4 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 4;
            }
            newDist = distance4 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 4;
            }
            newDist = distance4 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 4;
            }
            newDist = distance4 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 4;
            }
            newDist = distance4 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 4;
            }
            newDist = distance5 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 5;
            }
            newDist = distance5 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 5;
            }
            newDist = distance5 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 5;
            }
            newDist = distance5 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 5;
            }
            newDist = distance5 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 5;
            }
            newDist = distance5 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 5;
            }
            newDist = distance5 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 5;
            }
            newDist = distance5 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 5;
            }
            newDist = distance6 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 6;
            }
            newDist = distance6 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 6;
            }
            newDist = distance6 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 6;
            }
            newDist = distance6 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 6;
            }
            newDist = distance6 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 6;
            }
            newDist = distance6 + weight16;
            if (newDist < distance16) {
                distance16 = newDist;
                parents[16] = 6;
            }
            newDist = distance7 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 7;
            }
            newDist = distance7 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 7;
            }
            newDist = distance7 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 7;
            }
            newDist = distance7 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 7;
            }
            newDist = distance7 + weight16;
            if (newDist < distance16) {
                distance16 = newDist;
                parents[16] = 7;
            }
            newDist = distance8 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 8;
            }
            newDist = distance8 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 8;
            }
            newDist = distance8 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 8;
            }
            newDist = distance9 + weight17;
            if (newDist < distance17) {
                distance17 = newDist;
                parents[17] = 9;
            }
            newDist = distance9 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 9;
            }
            newDist = distance9 + weight8;
            if (newDist < distance8) {
                distance8 = newDist;
                parents[8] = 9;
            }
            newDist = distance9 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 9;
            }
            newDist = distance9 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 9;
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
            newDist = distance10 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 10;
            }
            newDist = distance10 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 10;
            }
            newDist = distance10 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 10;
            }
            newDist = distance10 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 10;
            }
            newDist = distance10 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 10;
            }
            newDist = distance11 + weight17;
            if (newDist < distance17) {
                distance17 = newDist;
                parents[17] = 11;
            }
            newDist = distance11 + weight18;
            if (newDist < distance18) {
                distance18 = newDist;
                parents[18] = 11;
            }
            newDist = distance11 + weight19;
            if (newDist < distance19) {
                distance19 = newDist;
                parents[19] = 11;
            }
            newDist = distance11 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 11;
            }
            newDist = distance11 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 11;
            }
            newDist = distance11 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 11;
            }
            newDist = distance11 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 11;
            }
            newDist = distance11 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 11;
            }
            newDist = distance12 + weight18;
            if (newDist < distance18) {
                distance18 = newDist;
                parents[18] = 12;
            }
            newDist = distance12 + weight19;
            if (newDist < distance19) {
                distance19 = newDist;
                parents[19] = 12;
            }
            newDist = distance12 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 12;
            }
            newDist = distance12 + weight20;
            if (newDist < distance20) {
                distance20 = newDist;
                parents[20] = 12;
            }
            newDist = distance12 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 12;
            }
            newDist = distance12 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 12;
            }
            newDist = distance12 + weight21;
            if (newDist < distance21) {
                distance21 = newDist;
                parents[21] = 12;
            }
            newDist = distance13 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 13;
            }
            newDist = distance13 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 13;
            }
            newDist = distance13 + weight20;
            if (newDist < distance20) {
                distance20 = newDist;
                parents[20] = 13;
            }
            newDist = distance13 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 13;
            }
            newDist = distance13 + weight21;
            if (newDist < distance21) {
                distance21 = newDist;
                parents[21] = 13;
            }
            newDist = distance13 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 13;
            }
            newDist = distance13 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 13;
            }
            newDist = distance13 + weight22;
            if (newDist < distance22) {
                distance22 = newDist;
                parents[22] = 13;
            }
            newDist = distance14 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 14;
            }
            newDist = distance14 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 14;
            }
            newDist = distance14 + weight21;
            if (newDist < distance21) {
                distance21 = newDist;
                parents[21] = 14;
            }
            newDist = distance14 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 14;
            }
            newDist = distance14 + weight22;
            if (newDist < distance22) {
                distance22 = newDist;
                parents[22] = 14;
            }
            newDist = distance14 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 14;
            }
            newDist = distance14 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 14;
            }
            newDist = distance15 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 15;
            }
            newDist = distance15 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 15;
            }
            newDist = distance15 + weight22;
            if (newDist < distance22) {
                distance22 = newDist;
                parents[22] = 15;
            }
            newDist = distance15 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 15;
            }
            newDist = distance15 + weight16;
            if (newDist < distance16) {
                distance16 = newDist;
                parents[16] = 15;
            }
            newDist = distance16 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 16;
            }
            newDist = distance16 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 16;
            }
            newDist = distance16 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 16;
            }
            newDist = distance17 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 17;
            }
            newDist = distance17 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 17;
            }
            newDist = distance17 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 17;
            }
            newDist = distance17 + weight18;
            if (newDist < distance18) {
                distance18 = newDist;
                parents[18] = 17;
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
            newDist = distance20 + weight21;
            if (newDist < distance21) {
                distance21 = newDist;
                parents[21] = 20;
            }
            newDist = distance21 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 21;
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
            newDist = distance21 + weight22;
            if (newDist < distance22) {
                distance22 = newDist;
                parents[22] = 21;
            }
            newDist = distance22 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 22;
            }
            newDist = distance22 + weight21;
            if (newDist < distance21) {
                distance21 = newDist;
                parents[21] = 22;
            }
            newDist = distance22 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 22;
            }
            newDist = distance22 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 22;
            }
            newDist = distance0 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 0;
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
            newDist = distance0 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 0;
            }
            newDist = distance0 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 0;
            }
            newDist = distance1 + weight8;
            if (newDist < distance8) {
                distance8 = newDist;
                parents[8] = 1;
            }
            newDist = distance1 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 1;
            }
            newDist = distance1 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 1;
            }
            newDist = distance1 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 1;
            }
            newDist = distance1 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 1;
            }
            newDist = distance2 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 2;
            }
            newDist = distance2 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 2;
            }
            newDist = distance2 + weight8;
            if (newDist < distance8) {
                distance8 = newDist;
                parents[8] = 2;
            }
            newDist = distance2 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 2;
            }
            newDist = distance2 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 2;
            }
            newDist = distance2 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 2;
            }
            newDist = distance3 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 3;
            }
            newDist = distance3 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 3;
            }
            newDist = distance3 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 3;
            }
            newDist = distance3 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 3;
            }
            newDist = distance3 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 3;
            }
            newDist = distance3 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 3;
            }
            newDist = distance3 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 3;
            }
            newDist = distance3 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 3;
            }
            newDist = distance4 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 4;
            }
            newDist = distance4 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 4;
            }
            newDist = distance4 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 4;
            }
            newDist = distance4 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 4;
            }
            newDist = distance4 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 4;
            }
            newDist = distance4 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 4;
            }
            newDist = distance4 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 4;
            }
            newDist = distance4 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 4;
            }
            newDist = distance5 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 5;
            }
            newDist = distance5 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 5;
            }
            newDist = distance5 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 5;
            }
            newDist = distance5 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 5;
            }
            newDist = distance5 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 5;
            }
            newDist = distance5 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 5;
            }
            newDist = distance5 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 5;
            }
            newDist = distance5 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 5;
            }
            newDist = distance6 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 6;
            }
            newDist = distance6 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 6;
            }
            newDist = distance6 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 6;
            }
            newDist = distance6 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 6;
            }
            newDist = distance6 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 6;
            }
            newDist = distance6 + weight16;
            if (newDist < distance16) {
                distance16 = newDist;
                parents[16] = 6;
            }
            newDist = distance7 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 7;
            }
            newDist = distance7 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 7;
            }
            newDist = distance7 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 7;
            }
            newDist = distance7 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 7;
            }
            newDist = distance7 + weight16;
            if (newDist < distance16) {
                distance16 = newDist;
                parents[16] = 7;
            }
            newDist = distance8 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 8;
            }
            newDist = distance8 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 8;
            }
            newDist = distance8 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 8;
            }
            newDist = distance9 + weight17;
            if (newDist < distance17) {
                distance17 = newDist;
                parents[17] = 9;
            }
            newDist = distance9 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 9;
            }
            newDist = distance9 + weight8;
            if (newDist < distance8) {
                distance8 = newDist;
                parents[8] = 9;
            }
            newDist = distance9 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 9;
            }
            newDist = distance9 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 9;
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
            newDist = distance10 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 10;
            }
            newDist = distance10 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 10;
            }
            newDist = distance10 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 10;
            }
            newDist = distance10 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 10;
            }
            newDist = distance10 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 10;
            }
            newDist = distance11 + weight17;
            if (newDist < distance17) {
                distance17 = newDist;
                parents[17] = 11;
            }
            newDist = distance11 + weight18;
            if (newDist < distance18) {
                distance18 = newDist;
                parents[18] = 11;
            }
            newDist = distance11 + weight19;
            if (newDist < distance19) {
                distance19 = newDist;
                parents[19] = 11;
            }
            newDist = distance11 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 11;
            }
            newDist = distance11 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 11;
            }
            newDist = distance11 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 11;
            }
            newDist = distance11 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 11;
            }
            newDist = distance11 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 11;
            }
            newDist = distance12 + weight18;
            if (newDist < distance18) {
                distance18 = newDist;
                parents[18] = 12;
            }
            newDist = distance12 + weight19;
            if (newDist < distance19) {
                distance19 = newDist;
                parents[19] = 12;
            }
            newDist = distance12 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 12;
            }
            newDist = distance12 + weight20;
            if (newDist < distance20) {
                distance20 = newDist;
                parents[20] = 12;
            }
            newDist = distance12 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 12;
            }
            newDist = distance12 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 12;
            }
            newDist = distance12 + weight21;
            if (newDist < distance21) {
                distance21 = newDist;
                parents[21] = 12;
            }
            newDist = distance13 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 13;
            }
            newDist = distance13 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 13;
            }
            newDist = distance13 + weight20;
            if (newDist < distance20) {
                distance20 = newDist;
                parents[20] = 13;
            }
            newDist = distance13 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 13;
            }
            newDist = distance13 + weight21;
            if (newDist < distance21) {
                distance21 = newDist;
                parents[21] = 13;
            }
            newDist = distance13 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 13;
            }
            newDist = distance13 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 13;
            }
            newDist = distance13 + weight22;
            if (newDist < distance22) {
                distance22 = newDist;
                parents[22] = 13;
            }
            newDist = distance14 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 14;
            }
            newDist = distance14 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 14;
            }
            newDist = distance14 + weight21;
            if (newDist < distance21) {
                distance21 = newDist;
                parents[21] = 14;
            }
            newDist = distance14 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 14;
            }
            newDist = distance14 + weight22;
            if (newDist < distance22) {
                distance22 = newDist;
                parents[22] = 14;
            }
            newDist = distance14 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 14;
            }
            newDist = distance14 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 14;
            }
            newDist = distance15 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 15;
            }
            newDist = distance15 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 15;
            }
            newDist = distance15 + weight22;
            if (newDist < distance22) {
                distance22 = newDist;
                parents[22] = 15;
            }
            newDist = distance15 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 15;
            }
            newDist = distance15 + weight16;
            if (newDist < distance16) {
                distance16 = newDist;
                parents[16] = 15;
            }
            newDist = distance16 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 16;
            }
            newDist = distance16 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 16;
            }
            newDist = distance16 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 16;
            }
            newDist = distance17 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 17;
            }
            newDist = distance17 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 17;
            }
            newDist = distance17 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 17;
            }
            newDist = distance17 + weight18;
            if (newDist < distance18) {
                distance18 = newDist;
                parents[18] = 17;
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
            newDist = distance20 + weight21;
            if (newDist < distance21) {
                distance21 = newDist;
                parents[21] = 20;
            }
            newDist = distance21 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 21;
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
            newDist = distance21 + weight22;
            if (newDist < distance22) {
                distance22 = newDist;
                parents[22] = 21;
            }
            newDist = distance22 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 22;
            }
            newDist = distance22 + weight21;
            if (newDist < distance21) {
                distance21 = newDist;
                parents[21] = 22;
            }
            newDist = distance22 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 22;
            }
            newDist = distance22 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 22;
            }
            newDist = distance0 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 0;
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
            newDist = distance0 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 0;
            }
            newDist = distance0 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 0;
            }
            newDist = distance1 + weight8;
            if (newDist < distance8) {
                distance8 = newDist;
                parents[8] = 1;
            }
            newDist = distance1 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 1;
            }
            newDist = distance1 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 1;
            }
            newDist = distance1 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 1;
            }
            newDist = distance1 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 1;
            }
            newDist = distance2 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 2;
            }
            newDist = distance2 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 2;
            }
            newDist = distance2 + weight8;
            if (newDist < distance8) {
                distance8 = newDist;
                parents[8] = 2;
            }
            newDist = distance2 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 2;
            }
            newDist = distance2 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 2;
            }
            newDist = distance2 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 2;
            }
            newDist = distance3 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 3;
            }
            newDist = distance3 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 3;
            }
            newDist = distance3 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 3;
            }
            newDist = distance3 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 3;
            }
            newDist = distance3 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 3;
            }
            newDist = distance3 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 3;
            }
            newDist = distance3 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 3;
            }
            newDist = distance3 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 3;
            }
            newDist = distance4 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 4;
            }
            newDist = distance4 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 4;
            }
            newDist = distance4 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 4;
            }
            newDist = distance4 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 4;
            }
            newDist = distance4 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 4;
            }
            newDist = distance4 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 4;
            }
            newDist = distance4 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 4;
            }
            newDist = distance4 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 4;
            }
            newDist = distance5 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 5;
            }
            newDist = distance5 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 5;
            }
            newDist = distance5 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 5;
            }
            newDist = distance5 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 5;
            }
            newDist = distance5 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 5;
            }
            newDist = distance5 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 5;
            }
            newDist = distance5 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 5;
            }
            newDist = distance5 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 5;
            }
            newDist = distance6 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 6;
            }
            newDist = distance6 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 6;
            }
            newDist = distance6 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 6;
            }
            newDist = distance6 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 6;
            }
            newDist = distance6 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 6;
            }
            newDist = distance6 + weight16;
            if (newDist < distance16) {
                distance16 = newDist;
                parents[16] = 6;
            }
            newDist = distance7 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 7;
            }
            newDist = distance7 + weight0;
            if (newDist < distance0) {
                distance0 = newDist;
                parents[0] = 7;
            }
            newDist = distance7 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 7;
            }
            newDist = distance7 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 7;
            }
            newDist = distance7 + weight16;
            if (newDist < distance16) {
                distance16 = newDist;
                parents[16] = 7;
            }
            newDist = distance8 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 8;
            }
            newDist = distance8 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 8;
            }
            newDist = distance8 + weight1;
            if (newDist < distance1) {
                distance1 = newDist;
                parents[1] = 8;
            }
            newDist = distance9 + weight17;
            if (newDist < distance17) {
                distance17 = newDist;
                parents[17] = 9;
            }
            newDist = distance9 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 9;
            }
            newDist = distance9 + weight8;
            if (newDist < distance8) {
                distance8 = newDist;
                parents[8] = 9;
            }
            newDist = distance9 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 9;
            }
            newDist = distance9 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 9;
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
            newDist = distance10 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 10;
            }
            newDist = distance10 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 10;
            }
            newDist = distance10 + weight2;
            if (newDist < distance2) {
                distance2 = newDist;
                parents[2] = 10;
            }
            newDist = distance10 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 10;
            }
            newDist = distance10 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 10;
            }
            newDist = distance11 + weight17;
            if (newDist < distance17) {
                distance17 = newDist;
                parents[17] = 11;
            }
            newDist = distance11 + weight18;
            if (newDist < distance18) {
                distance18 = newDist;
                parents[18] = 11;
            }
            newDist = distance11 + weight19;
            if (newDist < distance19) {
                distance19 = newDist;
                parents[19] = 11;
            }
            newDist = distance11 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 11;
            }
            newDist = distance11 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 11;
            }
            newDist = distance11 + weight3;
            if (newDist < distance3) {
                distance3 = newDist;
                parents[3] = 11;
            }
            newDist = distance11 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 11;
            }
            newDist = distance11 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 11;
            }
            newDist = distance12 + weight18;
            if (newDist < distance18) {
                distance18 = newDist;
                parents[18] = 12;
            }
            newDist = distance12 + weight19;
            if (newDist < distance19) {
                distance19 = newDist;
                parents[19] = 12;
            }
            newDist = distance12 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 12;
            }
            newDist = distance12 + weight20;
            if (newDist < distance20) {
                distance20 = newDist;
                parents[20] = 12;
            }
            newDist = distance12 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 12;
            }
            newDist = distance12 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 12;
            }
            newDist = distance12 + weight21;
            if (newDist < distance21) {
                distance21 = newDist;
                parents[21] = 12;
            }
            newDist = distance13 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 13;
            }
            newDist = distance13 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 13;
            }
            newDist = distance13 + weight20;
            if (newDist < distance20) {
                distance20 = newDist;
                parents[20] = 13;
            }
            newDist = distance13 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 13;
            }
            newDist = distance13 + weight21;
            if (newDist < distance21) {
                distance21 = newDist;
                parents[21] = 13;
            }
            newDist = distance13 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 13;
            }
            newDist = distance13 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 13;
            }
            newDist = distance13 + weight22;
            if (newDist < distance22) {
                distance22 = newDist;
                parents[22] = 13;
            }
            newDist = distance14 + weight4;
            if (newDist < distance4) {
                distance4 = newDist;
                parents[4] = 14;
            }
            newDist = distance14 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 14;
            }
            newDist = distance14 + weight21;
            if (newDist < distance21) {
                distance21 = newDist;
                parents[21] = 14;
            }
            newDist = distance14 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 14;
            }
            newDist = distance14 + weight22;
            if (newDist < distance22) {
                distance22 = newDist;
                parents[22] = 14;
            }
            newDist = distance14 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 14;
            }
            newDist = distance14 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 14;
            }
            newDist = distance15 + weight5;
            if (newDist < distance5) {
                distance5 = newDist;
                parents[5] = 15;
            }
            newDist = distance15 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 15;
            }
            newDist = distance15 + weight22;
            if (newDist < distance22) {
                distance22 = newDist;
                parents[22] = 15;
            }
            newDist = distance15 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 15;
            }
            newDist = distance15 + weight16;
            if (newDist < distance16) {
                distance16 = newDist;
                parents[16] = 15;
            }
            newDist = distance16 + weight7;
            if (newDist < distance7) {
                distance7 = newDist;
                parents[7] = 16;
            }
            newDist = distance16 + weight6;
            if (newDist < distance6) {
                distance6 = newDist;
                parents[6] = 16;
            }
            newDist = distance16 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 16;
            }
            newDist = distance17 + weight9;
            if (newDist < distance9) {
                distance9 = newDist;
                parents[9] = 17;
            }
            newDist = distance17 + weight10;
            if (newDist < distance10) {
                distance10 = newDist;
                parents[10] = 17;
            }
            newDist = distance17 + weight11;
            if (newDist < distance11) {
                distance11 = newDist;
                parents[11] = 17;
            }
            newDist = distance17 + weight18;
            if (newDist < distance18) {
                distance18 = newDist;
                parents[18] = 17;
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
            newDist = distance20 + weight21;
            if (newDist < distance21) {
                distance21 = newDist;
                parents[21] = 20;
            }
            newDist = distance21 + weight12;
            if (newDist < distance12) {
                distance12 = newDist;
                parents[12] = 21;
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
            newDist = distance21 + weight22;
            if (newDist < distance22) {
                distance22 = newDist;
                parents[22] = 21;
            }
            newDist = distance22 + weight13;
            if (newDist < distance13) {
                distance13 = newDist;
                parents[13] = 22;
            }
            newDist = distance22 + weight21;
            if (newDist < distance21) {
                distance21 = newDist;
                parents[21] = 22;
            }
            newDist = distance22 + weight14;
            if (newDist < distance14) {
                distance14 = newDist;
                parents[14] = 22;
            }
            newDist = distance22 + weight15;
            if (newDist < distance15) {
                distance15 = newDist;
                parents[15] = 22;
            }
            int current = southeastDeltaToIndex(targetLoc.x - currentLoc.x, targetLoc.y - currentLoc.y);
            int last = -1;
            while (parents[current] != -1) {
                last = current;
                current = parents[current];
            }
            if (last != -1){
                int[] delta = southeastIndexToDelta(last);
                MapLocation target = new MapLocation(rc.getLocation().x + delta[0],  rc.getLocation().y + delta[1]);
                if (rc.canMove(rc.getLocation().directionTo(target))){
                    rc.move(rc.getLocation().directionTo(target));
                }
            }
        }
    }
    static int southeastDeltaToIndex(int x, int y) {
        String delta = x + "|" + y;
        switch (delta) {
            case "-2|-1":
                return 8;
            case "-1|-2":
                return 9;
            case "-1|-1":
                return 2;
            case "-1|0":
                return 1;
            case "0|-3":
                return 17;
            case "0|-2":
                return 10;
            case "0|-1":
                return 3;
            case "0|0":
                return 0;
            case "0|1":
                return 7;
            case "1|-3":
                return 18;
            case "1|-2":
                return 11;
            case "1|-1":
                return 4;
            case "1|0":
                return 5;
            case "1|1":
                return 6;
            case "1|2":
                return 16;
            case "2|-3":
                return 19;
            case "2|-2":
                return 12;
            case "2|-1":
                return 13;
            case "2|0":
                return 14;
            case "2|1":
                return 15;
            case "3|-2":
                return 20;
            case "3|-1":
                return 21;
            case "3|0":
                return 22;
            default: break;
         }
        throw new RuntimeException("Bad delta");
    }
    static int[] southeastIndexToDelta(int index) {
        switch (index) {
        case 8:
            return SOUTHEAST_I_TO_DELTA8;
        case 9:
            return SOUTHEAST_I_TO_DELTA9;
        case 2:
            return SOUTHEAST_I_TO_DELTA2;
        case 1:
            return SOUTHEAST_I_TO_DELTA1;
        case 17:
            return SOUTHEAST_I_TO_DELTA17;
        case 10:
            return SOUTHEAST_I_TO_DELTA10;
        case 3:
            return SOUTHEAST_I_TO_DELTA3;
        case 0:
            return SOUTHEAST_I_TO_DELTA0;
        case 7:
            return SOUTHEAST_I_TO_DELTA7;
        case 18:
            return SOUTHEAST_I_TO_DELTA18;
        case 11:
            return SOUTHEAST_I_TO_DELTA11;
        case 4:
            return SOUTHEAST_I_TO_DELTA4;
        case 5:
            return SOUTHEAST_I_TO_DELTA5;
        case 6:
            return SOUTHEAST_I_TO_DELTA6;
        case 16:
            return SOUTHEAST_I_TO_DELTA16;
        case 19:
            return SOUTHEAST_I_TO_DELTA19;
        case 12:
            return SOUTHEAST_I_TO_DELTA12;
        case 13:
            return SOUTHEAST_I_TO_DELTA13;
        case 14:
            return SOUTHEAST_I_TO_DELTA14;
        case 15:
            return SOUTHEAST_I_TO_DELTA15;
        case 20:
            return SOUTHEAST_I_TO_DELTA20;
        case 21:
            return SOUTHEAST_I_TO_DELTA21;
        case 22:
            return SOUTHEAST_I_TO_DELTA22;
        default: break;
        }
        throw new RuntimeException("Bad index");
    }
    static int[] getSoutheastSemicircleNeighbors(int index) {
        switch (index) {
            case 0:
                return SOUTHEAST_NEIGHBORS0;
            case 1:
                return SOUTHEAST_NEIGHBORS1;
            case 2:
                return SOUTHEAST_NEIGHBORS2;
            case 3:
                return SOUTHEAST_NEIGHBORS3;
            case 4:
                return SOUTHEAST_NEIGHBORS4;
            case 5:
                return SOUTHEAST_NEIGHBORS5;
            case 6:
                return SOUTHEAST_NEIGHBORS6;
            case 7:
                return SOUTHEAST_NEIGHBORS7;
            case 8:
                return SOUTHEAST_NEIGHBORS8;
            case 9:
                return SOUTHEAST_NEIGHBORS9;
            case 10:
                return SOUTHEAST_NEIGHBORS10;
            case 11:
                return SOUTHEAST_NEIGHBORS11;
            case 12:
                return SOUTHEAST_NEIGHBORS12;
            case 13:
                return SOUTHEAST_NEIGHBORS13;
            case 14:
                return SOUTHEAST_NEIGHBORS14;
            case 15:
                return SOUTHEAST_NEIGHBORS15;
            case 16:
                return SOUTHEAST_NEIGHBORS16;
            case 17:
                return SOUTHEAST_NEIGHBORS17;
            case 18:
                return SOUTHEAST_NEIGHBORS18;
            case 19:
                return SOUTHEAST_NEIGHBORS19;
            case 20:
                return SOUTHEAST_NEIGHBORS20;
            case 21:
                return SOUTHEAST_NEIGHBORS21;
            case 22:
                return SOUTHEAST_NEIGHBORS22;
            default: break;
        }
        throw new RuntimeException("Bad index");
     }
    public static void init(RobotController rc) {
        SoutheastPather.rc = rc;
    }
    static void testMap() {
        for (int i=0;i<23;i++) {
            for (int j=0;j<23 ;j++) {
                int[] delta1 = southeastIndexToDelta(j);
                rc.setIndicatorDot(new MapLocation(rc.getLocation().x + delta1[0], rc.getLocation().y + delta1[1]), 255, 0, 0);
            }
        for (int neighborIndex : getSoutheastSemicircleNeighbors(i)) {
            int[] delta = southeastIndexToDelta(neighborIndex);
            rc.setIndicatorDot(new MapLocation(rc.getLocation().x + delta[0], rc.getLocation().y + delta[1]), 0, 0, 255);
        }
        Clock.yield();
        }
    }
}
