package baseplayertargeting;

import baseplayertargeting.flags.EnemySpottedInfo;
import baseplayertargeting.flags.FlagType;
import baseplayertargeting.flags.Flags;
import baseplayertargeting.nav.BoundingBox;
import battlecode.common.*;

public class BotSlanderer extends BotController {
    Direction awayFromEnemy;
    Direction prevRun;
    MapLocation targetLoc;
    BoundingBox bindingArea;
    int prevEnemySighted = 0;
    int closest = Integer.MAX_VALUE;
    int[] defaultValues = Utilities.SLANDERER_VALUES;
    boolean flagSet = false;
    boolean isLattice = false;
    public BotSlanderer(RobotController rc) throws GameActionException {
        super(rc);
        for (int value : defaultValues) {
            if (rc.getInfluence() == value && parentLoc.isPresent()) {
                isLattice = true;
            }
        }
        if (parentLoc.isPresent() && isLattice) {
            bindingArea = new BoundingBox(parentLoc.get().translate(5, 5), parentLoc.get().translate(-5, -5));
        }
        targetLoc = parentLoc.get().add(parentLoc.get().directionTo(rc.getLocation()));
    }

    @Override
    public BotController run() throws GameActionException {
        if (rc.getType() == RobotType.POLITICIAN){
            // Just converted; return a politician controller instead
            return BotPolitician.fromSlanderer(this);
        }
        if(isLattice) {
            awayFromEnemy = null;
            senseNearbyRobots(this::onNearbyEnemy, this::onNearbyFriendly, this::onNearbyNeutral);
            if (awayFromEnemy != null) {
                nav.moveTo(targetLoc);
                inGrid = false;
                latticeLoc = null;
            } else if (rc.getRoundNum() - prevEnemySighted < 3 && rc.getRoundNum() > 3) {
              nav.moveTo(rc.getLocation().add(prevRun));
            } else {
                latticePosition();
            }
        } else {
            senseNearbyRobots(this::onNearbyEnemy, this::onNearbyFriendly, this::onNearbyNeutral);
            if (rc.getRoundNum() - prevEnemySighted < 3) {
                nav.moveTo(rc.getLocation().add(prevRun));
            } else {
                nav.moveTo(targetLoc);
            }
            if (rc.getType() == RobotType.POLITICIAN){
                // Just converted; return a politician controller instead
                return BotPolitician.fromSlanderer(this);
            }

            // Search for boundary if we can
            if (Clock.getBytecodesLeft() > 1000) {
                searchForNearbyBoundaries();
                flagBoundaries();
            }
            flagSet = false;
        }
        closest = Integer.MAX_VALUE;
        return this;
    }

    private void onNearbyEnemy(RobotInfo robotInfo) throws GameActionException {
        if (rc.getLocation().distanceSquaredTo(robotInfo.location) < closest) {
            awayFromEnemy = robotInfo.location.directionTo(rc.getLocation());
            closest = rc.getLocation().distanceSquaredTo(robotInfo.location);
            targetLoc = rc.getLocation().add(robotInfo.location.directionTo(rc.getLocation()));
            //if(isLattice) bindingArea.translate(awayFromEnemy);
            prevEnemySighted = rc.getRoundNum();
            prevRun = awayFromEnemy;
        }
        if (!flagSet || inGrid) {
            rc.setFlag(Flags.encodeEnemySpotted(rc.getRoundNum(), robotInfo.location, robotInfo.getType(), false));
            flagSet = true;
        }
    }

    private void onNearbyFriendly(RobotInfo robotInfo) throws GameActionException {
        if(!isLattice) {
            if (rc.canGetFlag(robotInfo.ID)) {
                if (rc.getFlag(robotInfo.ID) == 1) {
                    MapLocation allyLoc = robotInfo.location;
                    if (parentLoc.isPresent()) targetLoc = allyLoc.add(allyLoc.directionTo(parentLoc.get()));
                    else targetLoc = allyLoc;
                } else if (Flags.decodeFlagType(rc.getFlag(robotInfo.ID)) == FlagType.ENEMY_SPOTTED) {
                    EnemySpottedInfo nearbyEnemy = Flags.decodeEnemySpotted(rc.getLocation(), rc.getFlag(robotInfo.ID));
                    if (!nearbyEnemy.isGuess) {
                        awayFromEnemy = nearbyEnemy.location.directionTo(rc.getLocation());
                        targetLoc = rc.getLocation().add(awayFromEnemy);
                    }
                }
            }
        }
    }

    private void onNearbyNeutral(RobotInfo robotInfo) throws GameActionException {
        targetLoc = rc.getLocation().add(rc.getLocation().directionTo(parentLoc.get())); //if a slanderer spots a neutral, we are too far from home lol
        nav.moveTo(targetLoc);
        if (!flagSet) {
            rc.setFlag(Flags.encodeNeutralEcSpotted(rc.getRoundNum(), robotInfo.location, robotInfo.conviction));
            flagSet = true;
        }
    }

    final static int[][] offsets = {{-1,-1},{1,-1},{-1,1},{1,1}};
    final static int[][] ecOffsets = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {-1, 2},  {1, -2}, {-1, -2}};
    static boolean inGrid = false;
    static MapLocation latticeLoc;
    private void latticePosition() throws GameActionException {
        if (!inGrid) {
            if (latticeLoc == null) {
                MapLocation home = parentLoc.get();
                int closestDist = Integer.MAX_VALUE;
                for (int[] offset : ecOffsets) {
                    MapLocation possible = home.translate(offset[0], offset[1]);
                    if (rc.canSenseLocation(possible) && (!rc.isLocationOccupied(possible)
                            || rc.getFlag(rc.senseRobotAtLocation(possible).ID) != 2) && rc.getLocation().distanceSquaredTo(possible) < closestDist) {
                        latticeLoc = possible;
                        closestDist = rc.getLocation().distanceSquaredTo(possible);
                    }
                }
            }
            if (latticeLoc == null) {
                int closestDist = Integer.MAX_VALUE;
                RobotInfo closestBot = null;
                for (RobotInfo robotInfo : rc.senseNearbyRobots()) {
                    if (robotInfo.getTeam().equals(rc.getTeam())) {
                        if (rc.getFlag(robotInfo.ID) == 2) {
                            if (robotInfo.location.distanceSquaredTo(rc.getLocation()) < closestDist) {
                                closestDist = robotInfo.location.distanceSquaredTo(rc.getLocation());
                                closestBot = robotInfo;
                            }
                        }
                    }
                }
                if (closestBot != null) {
                    closestDist = Integer.MAX_VALUE;
                    for (int[] offset : offsets) {
                        MapLocation possible = closestBot.location.translate(offset[0], offset[1]);
                        if (rc.canSenseLocation(possible) && rc.onTheMap(possible) &&
                                !rc.isLocationOccupied(possible) && bindingArea.isContained(possible)
                                && possible.distanceSquaredTo(parentLoc.get()) >= 4 && possible.distanceSquaredTo(rc.getLocation()) < closestDist) {
                            latticeLoc = possible;
                            closestDist = possible.distanceSquaredTo(rc.getLocation());
                        }
                    }
                }
            }
            if (latticeLoc == null){
                boolean nearbyAlly = false;
                for (RobotInfo robotInfo : rc.senseNearbyRobots()) {
                    if (robotInfo.getTeam().equals(rc.getTeam())) {
                        if(robotInfo.getType().equals(RobotType.ENLIGHTENMENT_CENTER) || (rc.canGetFlag(robotInfo.ID) && rc.getFlag(robotInfo.ID) == 2)){
                            nearbyAlly = true;
                        }
                    }
                }
                if(nearbyAlly) {
                    if (parentLoc.isPresent()) {
                        nav.fuzzyMove(parentLoc.get().directionTo(rc.getLocation()));
                    } else {
                        nav.fuzzyMove(Utilities.randomDirection());
                    }
                    if (!bindingArea.isContained(rc.getLocation())) {
                        bindingArea.expand(1);
                    }
                } else {
                    //too far from home
                    nav.moveTo(parentLoc.get());
                }
            } else {
                nav.moveTo(latticeLoc);
                if (rc.getLocation().equals(latticeLoc)){
                    rc.setFlag(2);
                    flagSet = true;
                    inGrid = true;
                }else if (rc.canSenseLocation(latticeLoc)
                        && rc.isLocationOccupied(latticeLoc)
                        && rc.getFlag(rc.senseRobotAtLocation(latticeLoc).ID) == 2){
                    MapLocation original = rc.senseRobotAtLocation(latticeLoc).location;
                    int closestDist = Integer.MAX_VALUE;
                    for (int[] offset : offsets){
                        MapLocation possible = original.translate(offset[0], offset[1]);
                        if (rc.canSenseLocation(possible) && rc.onTheMap(possible) && !rc.isLocationOccupied(possible) && bindingArea.isContained(possible) && possible.distanceSquaredTo(parentLoc.get()) >= 4 && possible.distanceSquaredTo(rc.getLocation()) < closestDist){
                            latticeLoc = possible;
                            closestDist = possible.distanceSquaredTo(rc.getLocation());
                        }
                    }
                }
            }
        }
    }
}
