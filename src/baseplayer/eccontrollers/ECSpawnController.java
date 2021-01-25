package baseplayer.eccontrollers;

import baseplayer.Utilities;
import battlecode.common.*;
import baseplayer.BotEnlightenment;

public class ECSpawnController implements ECController {
    private final int[] SLANDERER_VALUES = Utilities.SLANDERER_VALUES;
    private final RobotType[] EARLY_BUILD_Q = {RobotType.SLANDERER, RobotType.MUCKRAKER, RobotType.MUCKRAKER, RobotType.MUCKRAKER, RobotType.POLITICIAN};
    private final RobotType[] NORMAL_BUILD_Q = {RobotType.POLITICIAN, RobotType.SLANDERER, RobotType.MUCKRAKER, RobotType.SLANDERER, RobotType.POLITICIAN, RobotType.SLANDERER, RobotType.POLITICIAN};
    private final RobotType[] SAFE_BUILD_Q = {RobotType.SLANDERER, RobotType.MUCKRAKER, RobotType.SLANDERER, RobotType.POLITICIAN, RobotType.POLITICIAN, RobotType.SLANDERER};
    private final int MIN_BUILD_AMOUNT = 21;
    private int MIN_DEFENDER_COUNT;
    private final int MIN_SLANDERER_AMOUNT = 85;
    private final RobotController rc;
    private final BotEnlightenment ec;
    private Direction nextSpawnDirection = Direction.NORTH;
    private Direction nextDefenderSpawnDirection;
    private Direction lastMuckrakerSpawnDirection;
    private int[] defenderDirections = null; //index will correspond to direction in array, stores robot ids
    private Direction[] outwardsDirections = null;
    private int numSpawned = 0;
    public ECSpawnController(RobotController rc, BotEnlightenment ec) {
        this.rc = rc;
        this.ec = ec;
    }

    @Override
    public void run() throws GameActionException {
        if (numSpawned <= 1) {
            MapLocation cornerLocation = null;
            if (ec.inCorner() != null) cornerLocation = ec.inCorner();
            if (cornerLocation != null) {
                Direction toCorner = rc.getLocation().directionTo(cornerLocation);
                switch(toCorner) {
                    case NORTHEAST:
                        outwardsDirections = new Direction[]{Direction.NORTHWEST, Direction.WEST, Direction.SOUTHWEST, Direction.SOUTH, Direction.SOUTHEAST};
                        break;
                    case NORTHWEST:
                        outwardsDirections = new Direction[]{Direction.NORTHEAST, Direction.EAST, Direction.SOUTHEAST, Direction.SOUTH, Direction.SOUTHWEST};
                        break;
                    case SOUTHEAST:
                        outwardsDirections = new Direction[]{Direction.SOUTHWEST, Direction.WEST, Direction.NORTHWEST, Direction.NORTH, Direction.NORTHEAST};
                        break;
                    case SOUTHWEST:
                        outwardsDirections = new Direction[]{Direction.NORTHEAST, Direction.EAST, Direction.NORTHWEST, Direction.NORTH, Direction.SOUTHEAST};
                        break;
                    case NORTH:
                        outwardsDirections = new Direction[]{Direction.NORTHWEST, Direction.WEST, Direction.SOUTHWEST, Direction.SOUTH, Direction.SOUTHEAST, Direction.EAST, Direction.NORTHEAST};
                        break;
                    case SOUTH:
                        outwardsDirections = new Direction[]{Direction.NORTHWEST, Direction.WEST, Direction.SOUTHWEST, Direction.NORTH, Direction.SOUTHEAST, Direction.EAST, Direction.NORTHEAST};
                        break;
                    case EAST:
                        outwardsDirections = new Direction[]{Direction.NORTHWEST, Direction.NORTH, Direction.SOUTHWEST, Direction.SOUTH, Direction.SOUTHEAST, Direction.WEST, Direction.NORTHEAST};
                        break;
                    case WEST:
                        outwardsDirections = new Direction[]{Direction.NORTHWEST, Direction.NORTH, Direction.SOUTHWEST, Direction.SOUTH, Direction.SOUTHEAST, Direction.EAST, Direction.NORTHEAST};
                        break;
                    case CENTER:
                        break;
                    default: break;
                }
            } else if (ec.getNorthBoundary().isPresent()) {
                outwardsDirections = new Direction[]{Direction.NORTHWEST, Direction.WEST, Direction.SOUTHWEST, Direction.SOUTH, Direction.SOUTHEAST, Direction.EAST, Direction.NORTHEAST};
            } else if (ec.getEastBoundary().isPresent()){
                outwardsDirections = new Direction[]{Direction.NORTHWEST, Direction.NORTH, Direction.SOUTHWEST, Direction.SOUTH, Direction.SOUTHEAST, Direction.WEST, Direction.NORTHEAST};
            } else if (ec.getSouthBoundary().isPresent()) {
                outwardsDirections = new Direction[]{Direction.NORTHWEST, Direction.WEST, Direction.SOUTHWEST, Direction.NORTH, Direction.SOUTHEAST, Direction.EAST, Direction.NORTHEAST};
            } else if (ec.getWestBoundary().isPresent()) {
                outwardsDirections = new Direction[]{Direction.NORTHWEST, Direction.NORTH, Direction.SOUTHWEST, Direction.SOUTH, Direction.SOUTHEAST, Direction.EAST, Direction.NORTHEAST};
            }

            if(outwardsDirections != null) {
                for (Direction dir : outwardsDirections) {
                    System.out.println(dir);
                }
            }
            MIN_DEFENDER_COUNT = outwardsDirections != null ? outwardsDirections.length : 8;
            defenderDirections = new int[8];
        }
        updateDefenders();
        RobotType toBuild = robotToSpawn();
        int inf;

        switch(toBuild) {
            case SLANDERER:
                inf = getSlandererBuildInfluence();
                break;
            case POLITICIAN:
                inf = getPoliticianBuildInfluence();
                break;
            case MUCKRAKER:
                inf = getMuckrakerBuildInfluence();
                break;
            default: throw new RuntimeException("Type error in spawn controller");
        }

        //System.out.println("BUILDING " + toBuild + " WITH " + inf);
        for (int i = 0;i<8;i++) {
            switch(toBuild) {
                case POLITICIAN:
                    if (outwardsDirections != null) {
                        for(Direction dir : outwardsDirections) {
                            if(rc.canBuildRobot(toBuild, dir, inf)) {
                                nextSpawnDirection = dir;
                                break;
                            }
                        }
                    }
                    break;
                case MUCKRAKER:
                    if (outwardsDirections != null) {
                        for(Direction dir : outwardsDirections) {
                            if(rc.canBuildRobot(toBuild, dir, inf) && dir != lastMuckrakerSpawnDirection) {
                                nextSpawnDirection = dir;
                                lastMuckrakerSpawnDirection = dir;
                                break;
                            }
                        }
                    }
                case SLANDERER:
                    break;
                default: throw new RuntimeException();
            } if (rc.canBuildRobot(toBuild, nextSpawnDirection, inf)) {
                if((toBuild.equals(RobotType.POLITICIAN) && inf < MIN_BUILD_AMOUNT)) {
                    if (rc.canBuildRobot(toBuild, nextDefenderSpawnDirection, inf)) {
                        nextSpawnDirection = nextDefenderSpawnDirection;
                    } else {
                        inf++;
                    }
                }
                rc.buildRobot(toBuild, nextSpawnDirection, inf);
                if (toBuild.equals(RobotType.POLITICIAN) && inf < MIN_BUILD_AMOUNT) {
                    ec.recordDefender(rc.senseRobotAtLocation(rc.getLocation().add(nextSpawnDirection)).ID);
                    switch(nextSpawnDirection) {
                        case NORTH:
                            if(defenderDirections[0] == 0) defenderDirections[0] = rc.senseRobotAtLocation(rc.getLocation().add(nextSpawnDirection)).ID;
                            break;
                        case NORTHEAST:
                            if(defenderDirections[1] == 0) defenderDirections[1] = rc.senseRobotAtLocation(rc.getLocation().add(nextSpawnDirection)).ID;
                            break;
                        case EAST:
                            if(defenderDirections[2] == 0) defenderDirections[2] = rc.senseRobotAtLocation(rc.getLocation().add(nextSpawnDirection)).ID;
                            break;
                        case SOUTHEAST:
                            if(defenderDirections[3] == 0) defenderDirections[3] = rc.senseRobotAtLocation(rc.getLocation().add(nextSpawnDirection)).ID;
                            break;
                        case SOUTH:
                            if(defenderDirections[4] == 0) defenderDirections[4] = rc.senseRobotAtLocation(rc.getLocation().add(nextSpawnDirection)).ID;
                            break;
                        case SOUTHWEST:
                            if(defenderDirections[5] == 0) defenderDirections[5] = rc.senseRobotAtLocation(rc.getLocation().add(nextSpawnDirection)).ID;
                            break;
                        case WEST:
                            if(defenderDirections[6] == 0) defenderDirections[6] = rc.senseRobotAtLocation(rc.getLocation().add(nextSpawnDirection)).ID;
                            break;
                        case NORTHWEST:
                            if(defenderDirections[7] == 0) defenderDirections[7] = rc.senseRobotAtLocation(rc.getLocation().add(nextSpawnDirection)).ID;
                            break;
                        case CENTER:
                            break;
                        default: throw new RuntimeException();
                    }
                } else {
                    ec.recordSpawn(rc.senseRobotAtLocation(rc.getLocation().add(nextSpawnDirection)).ID, toBuild);
                }
                numSpawned++;
                break;
            } else if (rc.canBuildRobot(RobotType.MUCKRAKER, nextSpawnDirection, 1) && rc.getRoundNum() % 5 == 0) {
                rc.buildRobot(RobotType.MUCKRAKER, nextSpawnDirection, 1);
                ec.recordSpawn(rc.senseRobotAtLocation(rc.getLocation().add(nextSpawnDirection)).ID, RobotType.MUCKRAKER);
            } else {
                nextSpawnDirection = nextSpawnDirection.rotateRight();
            }
        }
    }

    private RobotType robotToSpawn() throws GameActionException{
        int roundNum = rc.getRoundNum();
        RobotType toBuild;

        //pick from build queues most of the time
        if (numSpawned < 9 && roundNum < 50) {
            toBuild = EARLY_BUILD_Q[numSpawned % EARLY_BUILD_Q.length];
        } else if (ec.getAvgSafetyEval() > 150 && ec.getSafetyEval() > 100 && roundNum - ec.getLastEnemySeen() > 25) {
            toBuild = SAFE_BUILD_Q[numSpawned % SAFE_BUILD_Q.length];
        } else {
            toBuild = NORMAL_BUILD_Q[numSpawned % NORMAL_BUILD_Q.length];
        }

        if(ec.getNearbyEnemyMuckrakers() > 3 * ec.getNearbyAllyPoliticians()){
            return RobotType.POLITICIAN;
        }

        if (ec.getSafetyEval() < 0 && rc.getInfluence() < Math.abs(ec.getSafetyEval())) {
            return RobotType.MUCKRAKER;
        }

        // curb spawning too many individual slanderers in the midgame
        if(ec.getSlandererCount() > 2 * (ec.getPoliticianCount() + ec.getDefenderCount()) && roundNum > 150) {
            return RobotType.POLITICIAN;
        }

        return toBuild;
    }

    private int getSlandererBuildInfluence() {
        int inf;
        if (numSpawned == 0) {
            inf = 130;
        } else {
            int budget = ec.getAvgSafetyEval() > 50 ? (int) (rc.getInfluence() * 0.9) : (int) (rc.getInfluence() * 0.7);
            int i = SLANDERER_VALUES.length - 1;
            while (SLANDERER_VALUES[i] > budget) {
                i -= 1;
                if (i < 0) break;
            }
            if (i < 0) inf = 0;
            else inf = SLANDERER_VALUES[i];
        }
        if (inf < MIN_SLANDERER_AMOUNT) inf = 0;
        return inf;
    }

    private int getMuckrakerBuildInfluence(){
        if (rc.getRoundNum() < 50 || (ec.getSafetyEval() < 0 && rc.getInfluence() < Math.abs(ec.getSafetyEval()))) {
            return 1;
        } else {
            return Math.max(1, (int) (0.1 * rc.getInfluence()));
        }
    }

    private int getPoliticianBuildInfluence() {
        // normal amount
        int inf = Math.max((int) (0.25 * rc.getInfluence()), MIN_BUILD_AMOUNT);

        // build a defender if needed
        if (ec.getDefenderCount() < MIN_DEFENDER_COUNT) {
            inf = MIN_BUILD_AMOUNT - 1;
        }

        if (ec.getThisRoundNeutralEcSpottedInfo().isPresent() && ec.getSafetyEval() > 0) {
            inf = Math.max((int) (0.7 * rc.getInfluence()), MIN_BUILD_AMOUNT);
        }

        // proceed with big politician strat if on map like CrossStitch
        if (ec.isSpawnNextToEnemyEC()) {
            inf = 131;
        } else if (inf == 131) {
            inf = 130;
        }

        return inf;
    }

    private void updateDefenders() {
        if (outwardsDirections != null) {
            for (Direction outDir : outwardsDirections) {
                switch(outDir) {
                    case NORTH:
                        if (defenderDirections[0] == 0){
                            nextDefenderSpawnDirection = Direction.NORTH;
                        } else if (!rc.canGetFlag(defenderDirections[0])) {
                            defenderDirections[0] = 0;
                            nextDefenderSpawnDirection = Direction.NORTH;
                        }
                        break;
                    case NORTHEAST:
                        if (defenderDirections[1] == 0){
                            nextDefenderSpawnDirection = Direction.NORTHEAST;
                        } else if (!rc.canGetFlag(defenderDirections[1])) {
                            defenderDirections[1] = 0;
                            nextDefenderSpawnDirection = Direction.NORTHEAST;
                        }
                        break;
                    case EAST:
                        if (defenderDirections[2] == 0){
                            nextDefenderSpawnDirection = Direction.EAST;
                        } else if (!rc.canGetFlag(defenderDirections[2])) {
                            defenderDirections[2] = 0;
                            nextDefenderSpawnDirection = Direction.EAST;
                        }
                        break;
                    case SOUTHEAST:
                        if (defenderDirections[3] == 0){
                            nextDefenderSpawnDirection = Direction.SOUTHEAST;
                        } else if (!rc.canGetFlag(defenderDirections[3])) {
                            defenderDirections[3] = 0;
                            nextDefenderSpawnDirection = Direction.SOUTHEAST;
                        }
                        break;
                    case SOUTH:
                        if (defenderDirections[4] == 0){
                            nextDefenderSpawnDirection = Direction.SOUTH;
                        } else if (!rc.canGetFlag(defenderDirections[4])) {
                            defenderDirections[4] = 0;
                            nextDefenderSpawnDirection = Direction.SOUTH;
                        }
                        break;
                    case SOUTHWEST:
                        if (defenderDirections[5] == 0){
                            nextDefenderSpawnDirection = Direction.SOUTHWEST;
                        } else if (!rc.canGetFlag(defenderDirections[5])) {
                            defenderDirections[5] = 0;
                            nextDefenderSpawnDirection = Direction.SOUTHWEST;
                        }
                        break;
                    case WEST:
                        if (defenderDirections[6] == 0){
                            nextDefenderSpawnDirection = Direction.WEST;
                        } else if (!rc.canGetFlag(defenderDirections[6])) {
                            defenderDirections[6] = 0;
                            nextDefenderSpawnDirection = Direction.WEST;
                        }
                        break;
                    case NORTHWEST:
                        if (defenderDirections[7] == 0){
                            nextDefenderSpawnDirection = Direction.NORTHWEST;
                        } else if (!rc.canGetFlag(defenderDirections[7])) {
                            defenderDirections[7] = 0;
                            nextDefenderSpawnDirection = Direction.NORTHWEST;
                        }
                        break;
                    case CENTER:
                        break;
                    default: throw new RuntimeException();
                }
            }
        } else {
            for (Direction outDir : Direction.allDirections()) {
                switch(outDir) {
                    case NORTH:
                        if (defenderDirections[0] == 0){
                            nextDefenderSpawnDirection = Direction.NORTH;
                        } else if (!rc.canGetFlag(defenderDirections[0])) {
                            defenderDirections[0] = 0;
                            nextDefenderSpawnDirection = Direction.NORTH;
                        }
                        break;
                    case NORTHEAST:
                        if (defenderDirections[1] == 0){
                            nextDefenderSpawnDirection = Direction.NORTHEAST;
                        } else if (!rc.canGetFlag(defenderDirections[1])) {
                            defenderDirections[1] = 0;
                            nextDefenderSpawnDirection = Direction.NORTHEAST;
                        }
                        break;
                    case EAST:
                        if (defenderDirections[2] == 0){
                            nextDefenderSpawnDirection = Direction.EAST;
                        } else if (!rc.canGetFlag(defenderDirections[2])) {
                            defenderDirections[2] = 0;
                            nextDefenderSpawnDirection = Direction.EAST;
                        }
                        break;
                    case SOUTHEAST:
                        if (defenderDirections[3] == 0){
                            nextDefenderSpawnDirection = Direction.SOUTHEAST;
                        } else if (!rc.canGetFlag(defenderDirections[3])) {
                            defenderDirections[3] = 0;
                            nextDefenderSpawnDirection = Direction.SOUTHEAST;
                        }
                        break;
                    case SOUTH:
                        if (defenderDirections[4] == 0){
                            nextDefenderSpawnDirection = Direction.SOUTH;
                        } else if (!rc.canGetFlag(defenderDirections[4])) {
                            defenderDirections[4] = 0;
                            nextDefenderSpawnDirection = Direction.SOUTH;
                        }
                        break;
                    case SOUTHWEST:
                        if (defenderDirections[5] == 0){
                            nextDefenderSpawnDirection = Direction.SOUTHWEST;
                        } else if (!rc.canGetFlag(defenderDirections[5])) {
                            defenderDirections[5] = 0;
                            nextDefenderSpawnDirection = Direction.SOUTHWEST;
                        }
                        break;
                    case WEST:
                        if (defenderDirections[6] == 0){
                            nextDefenderSpawnDirection = Direction.WEST;
                        } else if (!rc.canGetFlag(defenderDirections[6])) {
                            defenderDirections[6] = 0;
                            nextDefenderSpawnDirection = Direction.WEST;
                        }
                        break;
                    case NORTHWEST:
                        if (defenderDirections[7] == 0){
                            nextDefenderSpawnDirection = Direction.NORTHWEST;
                        } else if (!rc.canGetFlag(defenderDirections[7])) {
                            defenderDirections[7] = 0;
                            nextDefenderSpawnDirection = Direction.NORTHWEST;
                        }
                        break;
                    case CENTER:
                        break;
                    default: throw new RuntimeException();
                }
            }
        }
    }
}
