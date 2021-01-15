package baseplayer;
import baseplayer.flags.*;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import org.junit.Test;

import static org.junit.Assert.*;

public class FlagEncodeDecodeTest {
    @Test
    public void testEncodeDecodeEnemySpotted() {
        int flag = Flags.encodeEnemySpotted(200, new MapLocation(29, 23), RobotType.MUCKRAKER, false);
        assertEquals(FlagType.ENEMY_SPOTTED, Flags.decodeFlagType(flag));
        EnemySpottedInfo info = Flags.decodeEnemySpotted(new MapLocation(0, 0), flag);
        assertEquals(200, info.timestamp);
        assertEquals(info.enemyType, RobotType.MUCKRAKER);
        assertEquals(info.location, new MapLocation(29, 23));
        assertFalse(info.isGuess);


        int flag2 = Flags.encodeEnemySpotted(800, new MapLocation(63, 63), RobotType.ENLIGHTENMENT_CENTER, true);
        EnemySpottedInfo info2 = Flags.decodeEnemySpotted(new MapLocation(0,0), flag2);
        assertEquals(800, info2.timestamp);
        assertEquals(info2.enemyType, RobotType.ENLIGHTENMENT_CENTER);
        assertEquals(info2.location, new MapLocation(63, 63));
        assertTrue(info2.isGuess);

        int flag3 = Flags.encodeEnemySpotted(130, new MapLocation(-29, -23), RobotType.SLANDERER, false);
        EnemySpottedInfo info3 = Flags.decodeEnemySpotted(new MapLocation(0, 0), flag3);
        assertEquals(0, info3.timestamp);
        assertEquals(info3.enemyType, RobotType.SLANDERER);
        assertEquals(info3.location, new MapLocation(-29, -23));
        assertFalse(info3.isGuess);


        int flag4 = Flags.encodeEnemySpotted(2600, new MapLocation(-63, -63), RobotType.POLITICIAN, true);
        EnemySpottedInfo info4 = Flags.decodeEnemySpotted(new MapLocation(-14, -14), flag4);
        assertEquals(2600, info4.timestamp);
        assertEquals(info4.enemyType, RobotType.POLITICIAN);
        assertEquals(info4.location, new MapLocation(-63, -63));
        assertTrue(info4.isGuess);



        int flag5 = Flags.encodeEnemySpotted(1600, new MapLocation(28756, 17732), RobotType.POLITICIAN, false);
        EnemySpottedInfo info5 = Flags.decodeEnemySpotted(new MapLocation(28746, 17722), flag5);
        assertEquals(1600, info5.timestamp);
        assertEquals(info5.enemyType, RobotType.POLITICIAN);
        assertEquals(info5.location, new MapLocation(28756, 17732));
        assertFalse(info5.isGuess);


        int flag6 = Flags.encodeEnemySpotted(2800, new MapLocation(28751, 17713), RobotType.POLITICIAN, false);
        EnemySpottedInfo info6 = Flags.decodeEnemySpotted(new MapLocation(28746, 17722), flag6);
        assertEquals(2800, info6.timestamp);
        assertEquals(info6.enemyType, RobotType.POLITICIAN);
        assertEquals(info6.location, new MapLocation(28751, 17713));
        assertFalse(info6.isGuess);
    }


    @Test
    public void testEncodeDecodeBoundarySpotted() {
        int flag = Flags.encodeBoundarySpotted(692, 30063, BoundaryType.EAST);
        BoundarySpottedInfo info = Flags.decodeBoundarySpotted(flag);
        assertEquals(BoundaryType.EAST, info.boundaryType);
        assertEquals(30063, info.exactBoundaryLocation);
        assertEquals(600, info.timestamp);


        int flag2 = Flags.encodeBoundarySpotted(892, 0, BoundaryType.SOUTH);
        BoundarySpottedInfo info2 = Flags.decodeBoundarySpotted(flag2);
        assertEquals(BoundaryType.SOUTH, info2.boundaryType);
        assertEquals(800, info2.timestamp);
        assertEquals(0, info2.exactBoundaryLocation);

        int flag3 = Flags.encodeBoundarySpotted(1720, 28410, BoundaryType.WEST);
        BoundarySpottedInfo info3 = Flags.decodeBoundarySpotted(flag3);
        assertEquals(BoundaryType.WEST, info3.boundaryType);
        assertEquals(28410, info3.exactBoundaryLocation);
        assertEquals(1600, info3.timestamp);


        int flag4 = Flags.encodeBoundarySpotted(2309, 2910, BoundaryType.NORTH);
        BoundarySpottedInfo info4 = Flags.decodeBoundarySpotted(flag4);
        assertEquals(BoundaryType.NORTH, info4.boundaryType);
        assertEquals(2910, info4.exactBoundaryLocation);
        assertEquals(2200, info4.timestamp);
    }

    @Test
    public void testEncodeDecodeNeutralEC(){
        int flag = Flags.encodeNeutralEcSpotted(2789, new MapLocation(29, 23), 480);
        assertEquals(FlagType.NEUTRAL_EC_SPOTTED, Flags.decodeFlagType(flag));
        NeutralEcSpottedInfo info = Flags.decodeNeutralEcSpotted(new MapLocation(0, 0), flag);
        assertEquals(2600, info.timestamp);
        assertEquals(448, info.conviction);
        assertEquals(info.location, new MapLocation(29, 23));
    }


    @Test
    public void testEncodeDecodeEnemyClear() {
        int flag = Flags.encodeEnemyClear(200, new MapLocation(29, 23), RobotType.MUCKRAKER);
        assertEquals(FlagType.ENEMY_CLEAR, Flags.decodeFlagType(flag));
        EnemyClearInfo info = Flags.decodeEnemyClear(new MapLocation(0, 0), flag);
        assertEquals(200, info.timestamp);
        assertEquals(info.enemyType, RobotType.MUCKRAKER);
        assertEquals(info.location, new MapLocation(29, 23));


        int flag2 = Flags.encodeEnemyClear(800, new MapLocation(63, 63), RobotType.ENLIGHTENMENT_CENTER);
        EnemyClearInfo info2 = Flags.decodeEnemyClear(new MapLocation(0,0), flag2);
        assertEquals(800, info2.timestamp);
        assertEquals(info2.enemyType, RobotType.ENLIGHTENMENT_CENTER);
        assertEquals(info2.location, new MapLocation(63, 63));

        int flag3 = Flags.encodeEnemyClear(130, new MapLocation(-29, -23), RobotType.SLANDERER);
        EnemyClearInfo info3 = Flags.decodeEnemyClear(new MapLocation(0, 0), flag3);
        assertEquals(0, info3.timestamp);
        assertEquals(info3.enemyType, RobotType.SLANDERER);
        assertEquals(info3.location, new MapLocation(-29, -23));


        int flag4 = Flags.encodeEnemyClear(2600, new MapLocation(-63, -63), RobotType.POLITICIAN);
        EnemyClearInfo info4 = Flags.decodeEnemyClear(new MapLocation(-14, -14), flag4);
        assertEquals(2600, info4.timestamp);
        assertEquals(info4.enemyType, RobotType.POLITICIAN);
        assertEquals(info4.location, new MapLocation(-63, -63));


        int flag5 = Flags.encodeEnemyClear(1600, new MapLocation(28756, 17732), RobotType.POLITICIAN);
        EnemyClearInfo info5 = Flags.decodeEnemyClear(new MapLocation(28746, 17722), flag5);
        assertEquals(1600, info5.timestamp);
        assertEquals(info5.enemyType, RobotType.POLITICIAN);
        assertEquals(info5.location, new MapLocation(28756, 17732));

        int flag6 = Flags.encodeEnemyClear(2800, new MapLocation(28751, 17713), RobotType.POLITICIAN);
        EnemyClearInfo info6 = Flags.decodeEnemyClear(new MapLocation(28746, 17722), flag6);
        assertEquals(2800, info6.timestamp);
        assertEquals(info6.enemyType, RobotType.POLITICIAN);
        assertEquals(info6.location, new MapLocation(28751, 17713));
    }


    @Test
    public void testEncodeDecodeNeutralECClear() {
        int flag = Flags.encodeNeutralEcClear(2789, new MapLocation(29, 23), 480);
        assertEquals(FlagType.NEUTRAL_EC_CLEAR, Flags.decodeFlagType(flag));
        NeutralEcClearInfo info = Flags.decodeNeutralClear(new MapLocation(0, 0), flag);
        assertEquals(2600, info.timestamp);
        assertEquals(448, info.conviction);
        assertEquals(info.location, new MapLocation(29, 23));
    }

}
