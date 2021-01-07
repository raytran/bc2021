package baseplayer;
import baseplayer.flags.*;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import org.junit.Test;

import static org.junit.Assert.*;

public class FlagEncodeDecodeTest {
    @Test
    public void testEncodeDecodeEnemySpotted() {
        int flag = Flags.encodeEnemySpotted(FlagAddress.PARENT_ENLIGHTENMENT_CENTER, new MapLocation(29, 23), RobotType.MUCKRAKER);
        assertEquals(FlagType.ENEMY_SPOTTED, Flags.decodeFlagType(flag));
        EnemySpottedInfo info = Flags.decodeEnemySpotted(flag);
        assertEquals(FlagAddress.PARENT_ENLIGHTENMENT_CENTER, Flags.decodeFlagAddress(flag));
        assertEquals(info.enemyType, RobotType.MUCKRAKER);
        assertEquals(info.delta, new MapLocation(29, 23));


        int flag2 = Flags.encodeEnemySpotted(FlagAddress.PARENT_ENLIGHTENMENT_CENTER, new MapLocation(63, 63), RobotType.ENLIGHTENMENT_CENTER);
        assertEquals(FlagAddress.PARENT_ENLIGHTENMENT_CENTER, Flags.decodeFlagAddress(flag2));
        EnemySpottedInfo info2 = Flags.decodeEnemySpotted(flag2);
        assertEquals(info2.enemyType, RobotType.ENLIGHTENMENT_CENTER);
        assertEquals(info2.delta, new MapLocation(63, 63));

        int flag3 = Flags.encodeEnemySpotted(FlagAddress.PARENT_ENLIGHTENMENT_CENTER, new MapLocation(-29, -23), RobotType.SLANDERER);
        EnemySpottedInfo info3 = Flags.decodeEnemySpotted(flag3);
        assertEquals(info3.enemyType, RobotType.SLANDERER);
        assertEquals(info3.delta, new MapLocation(-29, -23));


        int flag4 = Flags.encodeEnemySpotted(FlagAddress.ANY, new MapLocation(-63, -63), RobotType.POLITICIAN);
        EnemySpottedInfo info4 = Flags.decodeEnemySpotted(flag4);
        assertEquals(info4.enemyType, RobotType.POLITICIAN);
        assertEquals(info4.delta, new MapLocation(-63, -63));
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
    public void testEncodeDecodeBoundaryRequired() {
        int flag = Flags.encodeBoundaryRequired(FlagAddress.MUCKRAKER, true, false, false, true);
        BoundaryRequiredInfo info = Flags.decodeBoundaryRequired(flag);
        assertTrue(info.northFound);
        assertFalse(info.eastFound);
        assertFalse(info.southFound);
        assertTrue(info.westFound);

        int flag2 = Flags.encodeBoundaryRequired(FlagAddress.POLITICIAN, false, false, false, false);
        BoundaryRequiredInfo info2 = Flags.decodeBoundaryRequired(flag2);
        assertFalse(info2.northFound);
        assertFalse(info2.eastFound);
        assertFalse(info2.southFound);
        assertFalse(info2.westFound);

        int flag3 = Flags.encodeBoundaryRequired(FlagAddress.SLANDERER, true, true, true, true);
        BoundaryRequiredInfo info3 = Flags.decodeBoundaryRequired(flag3);
        assertTrue(info3.northFound);
        assertTrue(info3.eastFound);
        assertTrue(info3.southFound);
        assertTrue(info3.westFound);
    }
}
