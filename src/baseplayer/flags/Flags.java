package baseplayer.flags;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;

/**
 * Abstracts the flag system for the game
 * Generate baseplayer.flags with the generate* methods
 * Decode baseplayer.flags with decodeFlag()
 */
public class Flags {
    // All baseplayer.flags are 24 bit integers
    // This documentation uses bit-slices in bluespec notation; i.e. [20:0] are bits 20 through 0 inclusive

    // Flags will use the following format
    // NOTE: flag-type is currently 3 bits; 8 baseplayer.flags max
    // [23:21]                        [20:0]
    // flagType (FlagType.ordinal())  flag-specific info



    // How far to shift to get to flag bits
    private static int FLAG_START_SHIFT = 21;

    // ENEMY_SPOTTED
    // Deltas are stored in 7 bit twos-complement encoding
    // [23:21]     [20:14]       [13:7]        [6:5]             [4:0]
    // flagType    enemyDeltaX   enemyDeltaY   enemyRobotType    unused

    /**
     * @param location of the enemy
     * @return flag for enemy spotted at location
     */
    public static int encodeEnemySpotted(MapLocation location, RobotType enemyType) {
        int flag = encodeFlagType(FlagType.ENEMY_SPOTTED);
        flag ^= (location.x & 0b1111111) << 14;
        flag ^= (location.y & 0b1111111) << 7;
        flag ^= enemyType.ordinal() << 5;
        return flag;
    }

    /**
     * @param flag to be decoded
     * @return information for enemy spotted
     */
    public static EnemySpottedInfo decodeEnemySpotted(int flag) {
        MapLocation location = new MapLocation (
                signExtend((flag >>> 14) & 0b1111111, 7),
                signExtend((flag >>> 7) & 0b1111111, 7)
        );
        RobotType enemyType = RobotType.values()[(flag >>> 5) & 0b11];
        return new EnemySpottedInfo(location, enemyType);
    }

    // BOUNDARY_SPOTTED
    // Deltas are stored in 7 bit twos-complement encoding
    // [23:21]     [20:14]          [13:7]           [6:3]                                 [2:0]
    // flagType    boundaryDeltaX   boundaryDeltaY   boundaryType (Direction.ordinal())    unused
    /**
     * @param boundaryDelta of the boundary
     * @return flag for boundary at location
     */
    public static int encodeBoundarySpotted(MapLocation boundaryDelta, Direction boundaryDir) {
        int flag = encodeFlagType(FlagType.BOUNDARY_SPOTTED);
        flag ^= (boundaryDelta.x & 0b1111111) << 14;
        flag ^= (boundaryDelta.y & 0b1111111) << 7;
        flag ^= boundaryDir.ordinal() << 3;
        return flag;
    }

    /**
     * @param flag to be decoded
     * @return information for boundary spotted
     */
    public static BoundarySpottedInfo decodeBoundarySpotted(int flag) {
        MapLocation location = new MapLocation (
                signExtend((flag >>> 14) & 0b1111111, 7),
                signExtend((flag >>> 7) & 0b1111111, 7)
        );
        Direction boundaryDir = Direction.values()[(flag >>> 3) & 0b1111];
        return new BoundarySpottedInfo(location, boundaryDir);
    }


    // BOUNDARY_REQUIRED
    // Deltas are stored in 7 bit twos-complement encoding
    // [23:21]     [20]        [19]       [18]       [17]
    // flagType    N_found     E_found    S_found    W_found
    /**
     * @param north true if north boundary found
     * @param east true if east boundary found
     * @param south true if south boundary found
     * @param west true if west boundary found
     * @return flag for boundary at location
     */
    public static int encodeBoundaryRequired(boolean north, boolean east, boolean south, boolean west) {
        int flag = encodeFlagType(FlagType.BOUNDARY_REQUIRED);
        flag ^= (north ? 1 : 0) << 20;
        flag ^= (east ? 1 : 0) << 19;
        flag ^= (south ? 1 : 0) << 18;
        flag ^= (west ? 1 : 0) << 17;
        return flag;
    }

    /**
     * @param flag to be decoded
     * @return information for boundary required
     */
    public static BoundaryRequiredInfo decodeBoundaryRequired(int flag) {
        boolean north = ((flag >> 20) & 1) == 1;
        boolean east = ((flag >> 19) & 1) == 1;
        boolean south = ((flag >> 18) & 1) == 1;
        boolean west = ((flag >> 17) & 1) == 1;
        return new BoundaryRequiredInfo(north, east, south, west);
    }


    /**
     * @param flag to be decoded
     * @return the type of the flag
     */
    public static FlagType decodeFlagType(int flag) {
        return FlagType.values[flag >> FLAG_START_SHIFT];
    }

    private static int encodeFlagType(FlagType type) {
        return type.ordinal() << FLAG_START_SHIFT;
    }

    private static int signExtend(int num, int bitWidth) {
        // Shift bits to the front, then shift back with signed right shift
        return (num << (32 - bitWidth)) >> (32 - bitWidth);
    }
}
