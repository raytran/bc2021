package dlmoreram012521_01;

import battlecode.common.MapLocation;

public class MapLocPair implements Comparable<MapLocPair> {
    public MapLocation location;
    public double value;
    public MapLocPair(MapLocation location, double val){
        this.location = location;
        this.value = val;
    }

    @Override
    public int compareTo(MapLocPair mapLocPair) {
        return Double.compare(value, mapLocPair.value);
    }
}
