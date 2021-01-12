package baseplayer;

import battlecode.common.*;

public class MapLocationPair implements Comparable<MapLocationPair>{
    private final double key;
    private final MapLocation loc;
    public MapLocationPair(double key, MapLocation loc){
        this.key = key;
        this.loc = loc;
    }

    @Override
    public int compareTo(MapLocationPair o) {
        return 0;
    }
}
