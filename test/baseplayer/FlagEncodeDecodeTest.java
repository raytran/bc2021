package baseplayer;
import baseplayer.flags.*;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import org.junit.Test;

import static org.junit.Assert.*;

public class FlagEncodeDecodeTest {
    @Test
    public void testEncodeDecodeEnemySpotted() {
        int flag = Flags.encodeEnemySpotted(FlagAddress.PARENT_ENLIGHTENMENT_CENTER, new MapLocation(29, 23), RobotType.MUCKRAKER, false);
        assertEquals(FlagType.ENEMY_SPOTTED, Flags.decodeFlagType(flag));
        EnemySpottedInfo info = Flags.decodeEnemySpotted(new MapLocation(0, 0), flag);
        assertEquals(FlagAddress.PARENT_ENLIGHTENMENT_CENTER, Flags.decodeFlagAddress(flag));
        assertEquals(info.enemyType, RobotType.MUCKRAKER);
        assertEquals(info.location, new MapLocation(29, 23));
        assertFalse(info.isGuess);


        int flag2 = Flags.encodeEnemySpotted(FlagAddress.PARENT_ENLIGHTENMENT_CENTER, new MapLocation(63, 63), RobotType.ENLIGHTENMENT_CENTER, true);
        assertEquals(FlagAddress.PARENT_ENLIGHTENMENT_CENTER, Flags.decodeFlagAddress(flag2));
        EnemySpottedInfo info2 = Flags.decodeEnemySpotted(new MapLocation(0,0), flag2);
        assertEquals(info2.enemyType, RobotType.ENLIGHTENMENT_CENTER);
        assertEquals(info2.location, new MapLocation(63, 63));
        assertTrue(info2.isGuess);

        int flag3 = Flags.encodeEnemySpotted(FlagAddress.PARENT_ENLIGHTENMENT_CENTER, new MapLocation(-29, -23), RobotType.SLANDERER, false);
        EnemySpottedInfo info3 = Flags.decodeEnemySpotted(new MapLocation(0, 0), flag3);
        assertEquals(info3.enemyType, RobotType.SLANDERER);
        assertEquals(info3.location, new MapLocation(-29, -23));
        assertFalse(info3.isGuess);


        int flag4 = Flags.encodeEnemySpotted(FlagAddress.ANY, new MapLocation(-63, -63), RobotType.POLITICIAN, true);
        EnemySpottedInfo info4 = Flags.decodeEnemySpotted(new MapLocation(-14, -14), flag4);
        assertEquals(info4.enemyType, RobotType.POLITICIAN);
        assertEquals(info4.location, new MapLocation(-63, -63));
        assertTrue(info4.isGuess);



        int flag5 = Flags.encodeEnemySpotted(FlagAddress.ANY, new MapLocation(28756, 17732), RobotType.POLITICIAN, false);
        EnemySpottedInfo info5 = Flags.decodeEnemySpotted(new MapLocation(28746, 17722), flag5);
        assertEquals(info5.enemyType, RobotType.POLITICIAN);
        assertEquals(info5.location, new MapLocation(28756, 17732));
        assertFalse(info5.isGuess);


        int flag6 = Flags.encodeEnemySpotted(FlagAddress.ANY, new MapLocation(28751, 17713), RobotType.POLITICIAN, false);
        EnemySpottedInfo info6 = Flags.decodeEnemySpotted(new MapLocation(28746, 17722), flag6);
        assertEquals(info6.enemyType, RobotType.POLITICIAN);
        assertEquals(info6.location, new MapLocation(28751, 17713));
        assertFalse(info6.isGuess);
    }


    @Test
    public void testEncodeDecodeBoundarySpotted() {
        int flag = Flags.encodeBoundarySpotted(FlagAddress.ANY, 30063, BoundaryType.EAST);
        BoundarySpottedInfo info = Flags.decodeBoundarySpotted(flag);
        assertEquals(BoundaryType.EAST, info.boundaryType);
        assertEquals(30063, info.exactBoundaryLocation);


        int flag2 = Flags.encodeBoundarySpotted(FlagAddress.ANY, 0, BoundaryType.SOUTH);
        BoundarySpottedInfo info2 = Flags.decodeBoundarySpotted(flag2);
        assertEquals(BoundaryType.SOUTH, info2.boundaryType);
        assertEquals(0, info2.exactBoundaryLocation);

        int flag3 = Flags.encodeBoundarySpotted(FlagAddress.ANY, 28410, BoundaryType.WEST);
        BoundarySpottedInfo info3 = Flags.decodeBoundarySpotted(flag3);
        assertEquals(BoundaryType.WEST, info3.boundaryType);
        assertEquals(28410, info3.exactBoundaryLocation);


        int flag4 = Flags.encodeBoundarySpotted(FlagAddress.ANY, 2910, BoundaryType.NORTH);
        BoundarySpottedInfo info4 = Flags.decodeBoundarySpotted(flag4);
        assertEquals(BoundaryType.NORTH, info4.boundaryType);
        assertEquals(2910, info4.exactBoundaryLocation);
    }

    @Test
    public void testEncodeDecodeNeutralEC(){
        int flag = Flags.encodeNeutralEcSpotted(FlagAddress.ANY, new MapLocation(29, 23), 480);
        assertEquals(FlagType.NEUTRAL_EC_SPOTTED, Flags.decodeFlagType(flag));
        NeutralEcSpottedInfo info = Flags.decodeNeutralEcSpotted(new MapLocation(0, 0), flag);
        assertEquals(FlagAddress.ANY, Flags.decodeFlagAddress(flag));
        assertEquals(480, info.conviction);
        assertEquals(info.location, new MapLocation(29, 23));
    }
}
