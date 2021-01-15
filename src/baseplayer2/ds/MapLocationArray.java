package baseplayer2.ds;

import battlecode.common.MapLocation;

import java.util.Arrays;

/**
 * An array centered around/indexed using MapLocations
 */
public class MapLocationArray<T> {
    private final T[][] items;
    private final int xMin;
    private final int yMin;

    @SuppressWarnings("unchecked")
    public MapLocationArray(MapLocation center, int radiusSquared) {
        int radius = (int) Math.ceil(Math.sqrt(radiusSquared));
        xMin = Math.max(0, center.x - radius);
        yMin = Math.max(0, center.y - radius);
        //System.out.println(xMin);
        //System.out.println(yMin);
        items = (T[][]) new Object[2 * radius + 1][2 * radius + 1];
    }

    public T get(MapLocation location) {
        return items[getXindex(location)][getYindex(location)];
    }

    public void put(MapLocation location, T item) {
        items[getXindex(location)][getYindex(location)] = item;
    }

    public boolean containsKey(MapLocation location) {
        return items[getXindex(location)][getYindex(location)] != null;
    }

    private int getXindex(MapLocation location) {
        return Math.max(0, location.x - xMin);
    }

    private int getYindex(MapLocation location) {
        return Math.max(0, location.y - yMin);
    }

}
