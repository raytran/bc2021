package baseplayer;
import baseplayer.flags.BoundarySpottedInfo;
import baseplayer.flags.EnemySpottedInfo;
import baseplayer.flags.Flags;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class FlagEncodeDecodeTest {
    @Test
    public void testEncodeDecodeEnemySpotted() {
        int flag = Flags.encodeEnemySpotted(new MapLocation(29, 23), RobotType.MUCKRAKER);
        EnemySpottedInfo info = Flags.decodeEnemySpotted(flag);
        assertEquals(info.enemyType, RobotType.MUCKRAKER);
        assertEquals(info.delta, new MapLocation(29, 23));


        int flag2 = Flags.encodeEnemySpotted(new MapLocation(63, 63), RobotType.ENLIGHTENMENT_CENTER);
        EnemySpottedInfo info2 = Flags.decodeEnemySpotted(flag2);
        assertEquals(info2.enemyType, RobotType.ENLIGHTENMENT_CENTER);
        assertEquals(info2.delta, new MapLocation(63, 63));

        int flag3 = Flags.encodeEnemySpotted(new MapLocation(-29, -23), RobotType.SLANDERER);
        EnemySpottedInfo info3 = Flags.decodeEnemySpotted(flag3);
        assertEquals(info3.enemyType, RobotType.SLANDERER);
        assertEquals(info3.delta, new MapLocation(-29, -23));


        int flag4 = Flags.encodeEnemySpotted(new MapLocation(-63, -63), RobotType.POLITICIAN);
        EnemySpottedInfo info4 = Flags.decodeEnemySpotted(flag4);
        assertEquals(info4.enemyType, RobotType.POLITICIAN);
        assertEquals(info4.delta, new MapLocation(-63, -63));
    }


    @Test
    public void testEncodeDecodeBoundarySpotted() {
        int flag = Flags.encodeBoundarySpotted(new MapLocation(29, 23), Direction.NORTH);
        BoundarySpottedInfo info = Flags.decodeBoundarySpotted(flag);
        assertEquals(info.boundaryDirection, Direction.NORTH);
        assertEquals(info.delta, new MapLocation(29, 23));


        int flag2 = Flags.encodeBoundarySpotted(new MapLocation(63, 63), Direction.NORTHEAST);
        BoundarySpottedInfo info2 = Flags.decodeBoundarySpotted(flag2);
        assertEquals(info2.boundaryDirection, Direction.NORTHEAST);
        assertEquals(info2.delta, new MapLocation(63, 63));

        int flag3 = Flags.encodeBoundarySpotted(new MapLocation(-29, -23), Direction.SOUTH);
        BoundarySpottedInfo info3 = Flags.decodeBoundarySpotted(flag3);
        assertEquals(info3.boundaryDirection, Direction.SOUTH);
        assertEquals(info3.delta, new MapLocation(-29, -23));


        int flag4 = Flags.encodeBoundarySpotted(new MapLocation(-63, -63), Direction.SOUTHWEST);
        BoundarySpottedInfo info4 = Flags.decodeBoundarySpotted(flag4);
        assertEquals(info4.boundaryDirection, Direction.SOUTHWEST);
        assertEquals(info4.delta, new MapLocation(-63, -63));
    }
}
