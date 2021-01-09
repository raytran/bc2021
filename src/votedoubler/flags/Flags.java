package votedoubler.flags;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
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
    // [23:21]                    [20:18]                  [20:0]
    // flagType                   address                  flag-specific info
    // FlagType.ordinal()         FlagAddress.ordinal()


    // How far to shift to get to flag bits
    private static final int FLAGTYPE_SHIFT = 21;
    private static final int FLAGADDR_SHIFT = 18;

    // ENEMY_SPOTTED
    // Deltas are stored in 7 bit twos-complement encoding
    // [23:21]     [20:18]    [17:11]       [10:4]        [3:2]             [1:0]
    // flagType    address    enemyDeltaX   enemyDeltaY   enemyRobotType    unused

    /**
     * @param location of the enemy
     * @return flag for enemy spotted at location
     */
    public static int encodeEnemySpotted(FlagAddress address, MapLocation location, RobotType enemyType) {
        int flag = encodeFlagBase(FlagType.ENEMY_SPOTTED, address);
        flag ^= (location.x & 0b1111111) << 11;
        flag ^= (location.y & 0b1111111) << 4;
        flag ^= enemyType.ordinal() << 2;
        return flag;
    }

    /**
     * @param flag to be decoded
     * @return information for enemy spotted
     */
    public static EnemySpottedInfo decodeEnemySpotted(int flag) {
        MapLocation location = new MapLocation (
                signExtend((flag >>> 11) & 0b1111111, 7),
                signExtend((flag >>> 4) & 0b1111111, 7)
        );
        RobotType enemyType = RobotType.values()[(flag >>> 2) & 0b11];
        return new EnemySpottedInfo(location, enemyType);
    }

    // BOUNDARY_SPOTTED
    // Boundary spotted uses exact coordinate
    // [23:21]      [20:18]      [17:3]                [2:1]
    // flagType     address      boundaryExactCoord    boundaryType
    /**
     * @param
     * @return flag for boundary at location
     */
    public static int encodeBoundarySpotted(FlagAddress address, int exactBoundaryLocation, BoundaryType boundaryType) {
        int flag = encodeFlagBase(FlagType.BOUNDARY_SPOTTED, address);
        flag ^= exactBoundaryLocation << 3;
        flag ^= boundaryType.ordinal() << 1;
        return flag;
    }

    /**
     * @param flag to be decoded
     * @return information for boundary spotted
     */
    public static BoundarySpottedInfo decodeBoundarySpotted(int flag) {
        int exactBoundaryLocation = (flag >>> 3) & 0b111111111111111;
        BoundaryType boundaryType = BoundaryType.values[(flag >> 1) & 0b11];
        return new BoundarySpottedInfo(exactBoundaryLocation, boundaryType);
    }


    // BOUNDARY_REQUIRED
    // Deltas are stored in 7 bit twos-complement encoding
    // [23:21]      [20:18]     [17]        [16]       [15]       [14]
    // flagType     address     N_found     E_found    S_found    W_found
    /**
     * @param north true if north boundary found
     * @param east true if east boundary found
     * @param south true if south boundary found
     * @param west true if west boundary found
     * @return flag for boundary at location
     */
    public static int encodeBoundaryRequired(FlagAddress address, boolean north, boolean east, boolean south, boolean west) {
        int flag = encodeFlagBase(FlagType.BOUNDARY_REQUIRED, address);
        flag ^= (north ? 1 : 0) << 17;
        flag ^= (east ? 1 : 0) << 16;
        flag ^= (south ? 1 : 0) << 15;
        flag ^= (west ? 1 : 0) << 14;
        return flag;
    }

    /**
     * @param flag to be decoded
     * @return information for boundary required
     */
    public static BoundaryRequiredInfo decodeBoundaryRequired(int flag) {
        boolean north = ((flag >> 17) & 1) == 1;
        boolean east = ((flag >> 16) & 1) == 1;
        boolean south = ((flag >> 15) & 1) == 1;
        boolean west = ((flag >> 14) & 1) == 1;
        return new BoundaryRequiredInfo(north, east, south, west);
    }


    /**
     * @param flag to be decoded
     * @return the type of the flag
     */
    public static FlagType decodeFlagType(int flag) {
        return FlagType.values[(flag >> FLAGTYPE_SHIFT) & 0b111];
    }

    /**
     * @param flag to be decoded
     * @return the type of the flag
     */
    public static FlagAddress decodeFlagAddress(int flag) {
        return FlagAddress.values[(flag >> FLAGADDR_SHIFT) & 0b111];
    }

    /**
     *
     * @param rc robot controller
     * @param flag flag to check address
     * @param isParent whether or not the bot reading is also the parent of the bot
     * @return
     */
    public static boolean addressedForCurrentBot(RobotController rc, int flag, boolean isParent) {
        switch (decodeFlagAddress(flag)) {
            case ANY:
                return true;
            case MUCKRAKER:
                return rc.getType() == RobotType.MUCKRAKER;
            case SLANDERER:
                return rc.getType() == RobotType.SLANDERER;
            case POLITICIAN:
                return rc.getType() == RobotType.POLITICIAN;
            case ENLIGHTENMENT_CENTER:
                return rc.getType() == RobotType.ENLIGHTENMENT_CENTER;
            case PARENT_ENLIGHTENMENT_CENTER:
                return isParent && rc.getType() == RobotType.ENLIGHTENMENT_CENTER;
        }
        throw new RuntimeException("Unhandled address");
    }


    private static int encodeFlagBase(FlagType type, FlagAddress address) {
        return (type.ordinal() << FLAGTYPE_SHIFT) ^ (address.ordinal() << FLAGADDR_SHIFT);
    }

    private static int signExtend(int num, int bitWidth) {
        // Shift bits to the front, then shift back with signed right shift
        return (num << (32 - bitWidth)) >> (32 - bitWidth);
    }
}
