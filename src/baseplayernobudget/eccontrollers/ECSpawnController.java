package baseplayernobudget.eccontrollers;

import baseplayernobudget.BotEnlightenment;
import baseplayernobudget.Utilities;
import battlecode.common.*;

public class ECSpawnController implements ECController {
    private final RobotController rc;
    private final BotEnlightenment ec;

    private static final int[] SLANDERER_VALUES = {21, 42, 63, 85, 107, 130, 154, 178, 203, 228, 255, 282, 310, 339, 368, 399, 431, 463, 497, 532, 568, 605, 644, 683, 724, 766, 810, 855, 902, 949};
    public static final RobotType[] buildQ = {RobotType.SLANDERER, /*RobotType.MUCKRAKER, RobotType.MUCKRAKER,*/ RobotType.POLITICIAN};
    public static int buildQIndex = 0;

    public ECSpawnController(RobotController rc, BotEnlightenment ec) {
        this.rc = rc;
        this.ec = ec;
    }

    public void run() throws GameActionException {
        RobotType toBuild;
        int inf = 0;
        if(ec.getSafetyEval() >= 0 && !ec.isEnemyMuckraker()) {
            toBuild = buildQ[buildQIndex];
            switch (toBuild) {
                case SLANDERER:
                    inf = getSlandererBuildInfluence();
                    break;
                case MUCKRAKER:
                    inf = getMuckrakerBuildInfluence();
                    break;
                case POLITICIAN:
                    inf = getPoliticianBuildInfluence();
                    break;
            }
        } else {
            toBuild = RobotType.POLITICIAN;
            inf = getPoliticianBuildInfluence();
        }

        System.out.println("BUILDING " + toBuild + " WITH " + inf);
        for (Direction dir : Utilities.directions) {
            if (rc.canBuildRobot(toBuild, dir, inf)) {
                rc.buildRobot(toBuild, dir, inf);
                buildQIndex = (buildQIndex + 1) % buildQ.length;
                RobotInfo ri = rc.senseRobotAtLocation(rc.getLocation().add(dir));
                ec.recordSpawn(ri.ID, ri.type);
                break;
            }
        }
    }

    private int getSlandererBuildInfluence(){
        int inf;
        int i = SLANDERER_VALUES.length - 1;
        while (SLANDERER_VALUES[i] > rc.getInfluence()){
            i -= 1;
            if (i < 0) break;
        }
        if (i < 0) inf = 0;
        else inf = SLANDERER_VALUES[i];
        return inf;
    }

    private int getMuckrakerBuildInfluence(){
        return Math.max(1, (int) (rc.getInfluence() * 0.01));
    }

    private int getPoliticianBuildInfluence(){
        if (ec.isEnemyMuckraker()) return Math.max(21, (int) (rc.getInfluence() * 0.1));
        else return Math.max(21, (int) (rc.getInfluence() * 0.2));
    }
}
