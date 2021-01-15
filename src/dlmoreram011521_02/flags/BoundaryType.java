package dlmoreram011521_02.flags;

import battlecode.common.Direction;

public enum BoundaryType {
    NORTH,
    EAST,
    SOUTH,
    WEST;
    public static final BoundaryType[] values = values();

    public static Direction toDirection(BoundaryType boundaryType) {
        switch (boundaryType){
            case NORTH:
                return Direction.NORTH;
            case SOUTH:
                return Direction.SOUTH;
            case EAST:
                return Direction.EAST;
            case WEST:
                return Direction.WEST;
        }
        throw new RuntimeException("Shouldn't be here.");
    }
}
