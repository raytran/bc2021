package baseplayer.eccontrollers;
import baseplayer.BotEnlightenment;
import battlecode.common.*;
import battlecode.common.GameActionException;

public class ECSpawnController implements ECController{
    private static final int POLITICIAN_RATE = 10;
    private static final int MUCKRAKER_RATE = 1;
    private static final int SLANDERER_RATE = 20;
    private final RobotController rc;
    private final BotEnlightenment ec;
    private final ECBudgetController bc;
    private Direction nextSpawnDirection = Direction.NORTH;
    public ECSpawnController(RobotController rc, BotEnlightenment ec, ECBudgetController bc) {
        this.rc = rc;
        this.ec = ec;
        this.bc = bc;
    }
    @Override
    public void run() throws GameActionException {
        RobotType toBuild = robotToSpawn();
        MapLocation myLoc = rc.getLocation();
        int budget = bc.getBotBudget();
        int influence = 200;

        for (int i=0; i<8;i++) {
            if (rc.canBuildRobot(toBuild, nextSpawnDirection, influence)) {
                //Built the robot, add id to total
                rc.buildRobot(toBuild, nextSpawnDirection, influence);
                ec.recordSpawn(rc.senseRobotAtLocation(rc.getLocation().add(nextSpawnDirection)).ID, robotToSpawn());
                nextSpawnDirection = nextSpawnDirection.rotateRight();
                break;
            }
        }
    }
    private RobotType robotToSpawn() {
        int round = rc.getRoundNum();
        if(ec.getMuckrakerCount() <= 8 ){
            System.out.print("COUNT: " + ec.getMuckrakerCount());
            return RobotType.MUCKRAKER;
        }
        if(round % POLITICIAN_RATE == 0 || ec.getPoliticianCount() < 1){
            return RobotType.POLITICIAN;
        }
        if(round % SLANDERER_RATE == 0 || ec.getSlandererCount() < 1){
            return RobotType.SLANDERER;
        }
        else return RobotType.SLANDERER;
    }
}
