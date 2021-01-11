package baseplayer;

import baseplayer.ds.MapLocationArray;
import battlecode.common.MapLocation;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;
public class MapLocationArrayTest {
    @Test
    public void testSetGetEasy() {
        MapLocation center = new MapLocation(2000, 2000);
        MapLocationArray<Double> testArr = new MapLocationArray<>(center, 36);
        testArr.put(center, 50.0);
        double output1 = testArr.get(center);
        assertEquals(50.0, output1, 0.001);
        testArr.put(center.translate(0, 1), 1.0);
        double output2 = testArr.get(center.translate(0, 1));
        assertEquals(1.0, output2, 0.001);
    }

    @Test
    public void testSetGetBounds() {
        MapLocation center = new MapLocation(2000, 2000);
        MapLocationArray<Double> testArr = new MapLocationArray<>(center, 36);
        for (int i = -6; i <= 6; i++) {
            MapLocation next = center.translate(0, i);
            testArr.put(next, (double) i);
        }

        for (int i = -6; i <= 6; i++) {
            MapLocation next = center.translate(0, i);
            assertEquals(testArr.get(next), i, 0.001);
        }

        for (int i = -6; i <= 6; i++) {
            MapLocation next = center.translate(i, i);
            testArr.put(next, (double) i);
        }

        for (int i = -6; i <= 6; i++) {
            MapLocation next = center.translate(i, i);
            assertEquals(testArr.get(next), i, 0.001);
        }
    }

    @Test
    public void testContainsKey() {
        MapLocation center = new MapLocation(2000, 2000);
        MapLocationArray<Double> testArr = new MapLocationArray<>(center, 36);
        assertFalse(testArr.containsKey(center));
        testArr.put(center, 1.0);
        assertTrue(testArr.containsKey(center));
    }
}
