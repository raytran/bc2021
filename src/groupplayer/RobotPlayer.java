package groupplayer;
import baseplayer.nav.NavigationController;
import battlecode.common.*;

import java.security.acl.Group;

public strictfp class RobotPlayer {
    static RobotController rc;

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

    static NavigationController nav;
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

        System.out.println("I'm a " + rc.getType() + " and I just got created!");
        while (true) {
            turnCount += 1;
            // Try/catch blocks stop unhandled exceptions, which cause your robot to freeze
            try {
                // Here, we've separated the controls into a different method for each RobotType.
                // You may rewrite this into your own control structure if you wish.
                System.out.println("I'm a " + rc.getType() + "! Location " + rc.getLocation());
                switch (rc.getType()) {
                    case ENLIGHTENMENT_CENTER: runEnlightenmentCenter(); break;
                    case POLITICIAN:           runPolitician();          break;
                    case SLANDERER:            runSlanderer();           break;
                    case MUCKRAKER:            runMuckraker();           break;
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            }
        }
    }

    static RobotType[] buildQ = new RobotType[]{RobotType.SLANDERER, RobotType.POLITICIAN, RobotType.MUCKRAKER};
    static int nextToBuild = 0;
    static int spawnCount = 0;
    static void runEnlightenmentCenter() throws GameActionException {
        RobotType toBuild = buildQ[nextToBuild];

        int influence = toBuild == RobotType.SLANDERER ? rc.getInfluence()/2 : (toBuild == RobotType.POLITICIAN ? 11 : 1);
        for (Direction dir : directions) {
            if (rc.canBuildRobot(toBuild, dir, influence)) {
                nextToBuild = ((nextToBuild + 1) % buildQ.length);
                rc.buildRobot(toBuild, dir, influence);
                spawnCount += 1;
                break;
            }
        }

        rc.setFlag(spawnCount/3 + (spawnCount % 3));
    }

    static int group = -1;
    static int age = 0;
    static int parentId = 0;
    static void runPolitician() throws GameActionException {
        runFollower();
    }

    static MapLocation parentLoc;
    static Direction targetDir = Direction.CENTER;
    static void runSlanderer() throws GameActionException {
        if (age == 0) {
            for (RobotInfo robotInfo : rc.senseNearbyRobots(2)){
                if (robotInfo.type == RobotType.ENLIGHTENMENT_CENTER) {
                    parentId = robotInfo.ID;
                    parentLoc = robotInfo.location;
                    if (rc.canGetFlag(robotInfo.ID)){
                        group = rc.getFlag(parentId);
                    }
                }
            }
        }
        int groupMembers = 0;
        int groupDist = 0;
        for (RobotInfo robotInfo : rc.senseNearbyRobots()){
            if (robotInfo.team.equals(rc.getTeam().opponent())){
            } else {
                GroupInfo groupInfo = decodeGroupFlag(rc.getFlag(robotInfo.ID));
                if (groupInfo.group == group){
                    groupDist = Math.max(rc.getLocation().distanceSquaredTo(robotInfo.location), groupDist);
                    groupMembers += 1;

                    if (groupInfo.dir != Direction.CENTER) targetDir = groupInfo.dir;
                }
            }
        }

        if (groupMembers == 2 && groupDist < 7)
            if (targetDir != Direction.CENTER)
                nav.fuzzyMove(targetDir);
            else
                nav.fuzzyMove(parentLoc.directionTo(rc.getLocation()));

        System.out.println("I AM IN GROUP " + group + " WITH " + groupMembers);
        age += 1;
        rc.setFlag(group);
    }

    static void runMuckraker() throws GameActionException {
        runFollower();
    }

    static void runFollower() throws GameActionException {
        if (age == 0) {
            for (RobotInfo robotInfo : rc.senseNearbyRobots(2)){
                if (robotInfo.type == RobotType.ENLIGHTENMENT_CENTER) {
                    parentId = robotInfo.ID;
                    if (rc.canGetFlag(robotInfo.ID)){
                        group = rc.getFlag(parentId);
                    }
                }
            }
        }
        int closetEnemyDist = Integer.MAX_VALUE;
        Direction targetDir = Direction.CENTER;
        MapLocation targetLoc = null;
        for (RobotInfo robotInfo : rc.senseNearbyRobots()){
            if (robotInfo.team.equals(rc.getTeam())){
                if (rc.canGetFlag(robotInfo.ID)) {
                    GroupInfo groupInfo = decodeGroupFlag(rc.getFlag(robotInfo.ID));
                    if (groupInfo.group == group){
                        if (targetLoc == null || robotInfo.type == RobotType.SLANDERER)
                            targetLoc = robotInfo.location;
                    }
                }
            } else if (robotInfo.location.distanceSquaredTo(rc.getLocation()) < closetEnemyDist) {
                closetEnemyDist = robotInfo.location.distanceSquaredTo(rc.getLocation());
                targetDir = robotInfo.location.directionTo(rc.getLocation());
            }
        }
        if (targetLoc != null)
            nav.moveTo(targetLoc);
        System.out.println("I AM IN GROUP " + group + " MOVING TO " + targetLoc);
        age += 1;
        rc.setFlag(encodeGroupFlag(group, targetDir));

    }

    static int encodeGroupFlag(int groupNum, Direction targetDir) {
        return (targetDir.ordinal() << 10) ^ (groupNum & 0b111111);
    }

    static GroupInfo decodeGroupFlag(int flag) {
        return new GroupInfo(flag & 0b111111, Direction.values()[flag >> 10]);
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
        return spawnableRobot[(int) (Math.random() * spawnableRobot.length)];
    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        } else return false;
    }
}

class GroupInfo{
    int group;
    Direction dir;
    public GroupInfo(int group, Direction dir){
        this.group = group;
        this.dir = dir;
    }
}
