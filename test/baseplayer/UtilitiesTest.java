package baseplayer;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import org.junit.Test;
import static org.junit.Assert.assertEquals;


public class UtilitiesTest {
    @Test
    public void testOffsetLocation() {
        MapLocation original = new MapLocation(0, 0);
        //North
        assertEquals(new MapLocation(0, 5), Utilities.offsetLocation(original, Direction.NORTH, 5));

        //East
        assertEquals(new MapLocation(5, 0), Utilities.offsetLocation(original, Direction.EAST, 5));

        //South
        assertEquals(new MapLocation(0, -5), Utilities.offsetLocation(original, Direction.SOUTH, 5));

        //West
        assertEquals(new MapLocation(-5, 0), Utilities.offsetLocation(original, Direction.WEST, 5));

        //Northeast
        assertEquals(new MapLocation(1, 1), Utilities.offsetLocation(original, Direction.NORTHEAST, 2));

        //Northwest
        assertEquals(new MapLocation(-1, 1), Utilities.offsetLocation(original, Direction.NORTHWEST, 2));

        //Southeast
        assertEquals(new MapLocation(1, -1), Utilities.offsetLocation(original, Direction.SOUTHEAST, 2));

        //Southwest
        assertEquals(new MapLocation(-1, -1), Utilities.offsetLocation(original, Direction.SOUTHWEST, 2));
    }
}
