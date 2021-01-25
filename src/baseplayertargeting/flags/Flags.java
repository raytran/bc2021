package baseplayertargeting.flags;

import battlecode.common.MapLocation;
import battlecode.common.RobotType;

/**
 * Abstracts the flag system for the game
 * Generate baseplayer.flags with the generate* methods
 * Decode baseplayer.flags with decodeFlag()
 */
public class Flags {
    public static final int REBROADCAST_ROUND_LIMIT = 400;
    // All baseplayer.flags are 24 bit integers
    // This documentation uses bit-slices in bluespec notation; i.e. [20:0] are bits 20 through 0 inclusive

    // Flags will use the following format
    // NOTE: flag-type is currently 3 bits; 8 baseplayer.flags max
    // [23:21]                    [20:17]                    [16:0]
    // flagType                   timestamp                  flag-specific info
    // FlagType.ordinal()         floor(round_num/200)


    // How far to shift to get to flag bits
    private static final int FLAGTYPE_SHIFT = 21;
    private static final int TIMESTAMP_SHIFT = 17;

    // NEUTRAL_EC_SPOTTED
    // [23:21]     [20:17]      [16:10]          [9:3]             [2:0]
    // flagType    timestamp    neutralX % 128   neutralY % 128    floor(neutralConviction/64)
    public static int encodeNeutralEcSpotted(int roundNum, MapLocation location, int convictionLeft){
        int flag = encodeFlagBase(FlagType.NEUTRAL_EC_SPOTTED, roundNum);
        flag ^= ((location.x % 128) & 0b1111111) << 10;
        flag ^= ((location.y % 128) & 0b1111111) << 3;
        flag ^= (convictionLeft / 64) & 0b111;
        return flag;
    }
    // NEUTRAL_EC_SPOTTED And OP Successfully Spawned
    // [23:21]     [20:17]      [16:10]          [9:3]             [2:0]
    // flagType    timestamp    neutralX % 128   neutralY % 128    floor(neutralConviction/64)
    public static int encodeOpSpawned(int roundNum, MapLocation location, int convictionLeft){
        int flag = encodeFlagBase(FlagType.OP_SPAWNED, roundNum);
        flag ^= ((location.x % 128) & 0b1111111) << 10;
        flag ^= ((location.y % 128) & 0b1111111) << 3;
        flag ^= (convictionLeft / 64) & 0b111;
        return flag;
    }
    public static NeutralEcSpottedInfo decodeNeutralEcSpotted(MapLocation currentLoc, int flag) {
        int xMod128 = (flag >>> 10) & 0b1111111;
        int yMod128 = (flag >>> 3) & 0b1111111;
        int xOffset = (currentLoc.x / 128) * 128;
        int yOffset = (currentLoc.y / 128) * 128;

        MapLocation actualLocation = new MapLocation(xMod128 + xOffset, yMod128 + yOffset);
        MapLocation alternative = actualLocation.translate(-128, 0);
        if (alternative.distanceSquaredTo(currentLoc) < currentLoc.distanceSquaredTo(actualLocation)){
            actualLocation = alternative;
        }
        alternative = actualLocation.translate(128, 0);
        if (alternative.distanceSquaredTo(currentLoc) < currentLoc.distanceSquaredTo(actualLocation)){
            actualLocation = alternative;
        }
        alternative = actualLocation.translate(0, -128);
        if (alternative.distanceSquaredTo(currentLoc) < currentLoc.distanceSquaredTo(actualLocation)){
            actualLocation = alternative;
        }
        alternative = actualLocation.translate(0, 128);
        if (alternative.distanceSquaredTo(currentLoc) < currentLoc.distanceSquaredTo(actualLocation)){
            actualLocation = alternative;
        }
        return new NeutralEcSpottedInfo(decodeTimestamp(flag), actualLocation, (flag & 0b111) * 64);
    }

    // ENEMY_SPOTTED
    // [23:21]     [20:17]      [16:10]        [9:3]         [2:1]              [0]
    // flagType    timestamp    enemyX % 128   enemyY % 128   enemyRobotType    isGuess

    /**
     * @param location of the enemy
     * @return flag for enemy spotted at location
     */
    public static int encodeEnemySpotted(int roundNum, MapLocation location, RobotType enemyType, boolean isGuess) {
        int flag = encodeFlagBase(FlagType.ENEMY_SPOTTED, roundNum);
        flag ^= ((location.x % 128) & 0b1111111) << 10;
        flag ^= ((location.y % 128) & 0b1111111) << 3;
        flag ^= enemyType.ordinal() << 1;
        flag ^= (isGuess ? 1 : 0);
        return flag;
    }

    /**
     * @param flag to be decoded
     * @return information for enemy spotted
     */
    public static EnemySpottedInfo decodeEnemySpotted(MapLocation currentLoc, int flag) {
        int xMod128 = (flag >>> 10) & 0b1111111;
        int yMod128 = (flag >>> 3) & 0b1111111;
        int xOffset = (currentLoc.x / 128) * 128;
        int yOffset = (currentLoc.y / 128) * 128;

        MapLocation actualLocation = new MapLocation(xMod128 + xOffset, yMod128 + yOffset);
        MapLocation alternative = actualLocation.translate(-128, 0);
        if (alternative.distanceSquaredTo(currentLoc) < currentLoc.distanceSquaredTo(actualLocation)){
            actualLocation = alternative;
        }
        alternative = actualLocation.translate(128, 0);
        if (alternative.distanceSquaredTo(currentLoc) < currentLoc.distanceSquaredTo(actualLocation)){
            actualLocation = alternative;
        }
        alternative = actualLocation.translate(0, -128);
        if (alternative.distanceSquaredTo(currentLoc) < currentLoc.distanceSquaredTo(actualLocation)){
            actualLocation = alternative;
        }
        alternative = actualLocation.translate(0, 128);
        if (alternative.distanceSquaredTo(currentLoc) < currentLoc.distanceSquaredTo(actualLocation)){
            actualLocation = alternative;
        }
        RobotType enemyType = RobotType.values()[(flag >>> 1) & 0b11];
        return new EnemySpottedInfo(decodeTimestamp(flag), actualLocation, enemyType, ((flag) & 1) == 1);
    }


    // AREA_CLEAR
    // [23:21]     [20:17]      [16:10]        [9:3]       [2:0]
    // flagType    timestamp    X % 128        Y % 128     unused

    /**
     * @param location of the clear area
     * @return flag for area clear
     */
    public static int encodeAreaClear(int roundNum, MapLocation location) {
        int flag = encodeFlagBase(FlagType.AREA_CLEAR, roundNum);
        flag ^= ((location.x % 128) & 0b1111111) << 10;
        flag ^= ((location.y % 128) & 0b1111111) << 3;
        return flag;
    }

    /**
     * @param flag to be decoded
     * @return information for area clear
     */
    public static AreaClearInfo decodeAreaClear(MapLocation currentLoc, int flag) {
        int xMod128 = (flag >>> 10) & 0b1111111;
        int yMod128 = (flag >>> 3) & 0b1111111;
        int xOffset = (currentLoc.x / 128) * 128;
        int yOffset = (currentLoc.y / 128) * 128;

        MapLocation actualLocation = new MapLocation(xMod128 + xOffset, yMod128 + yOffset);
        MapLocation alternative = actualLocation.translate(-128, 0);
        if (alternative.distanceSquaredTo(currentLoc) < currentLoc.distanceSquaredTo(actualLocation)){
            actualLocation = alternative;
        }
        alternative = actualLocation.translate(128, 0);
        if (alternative.distanceSquaredTo(currentLoc) < currentLoc.distanceSquaredTo(actualLocation)){
            actualLocation = alternative;
        }
        alternative = actualLocation.translate(0, -128);
        if (alternative.distanceSquaredTo(currentLoc) < currentLoc.distanceSquaredTo(actualLocation)){
            actualLocation = alternative;
        }
        alternative = actualLocation.translate(0, 128);
        if (alternative.distanceSquaredTo(currentLoc) < currentLoc.distanceSquaredTo(actualLocation)){
            actualLocation = alternative;
        }
        return new AreaClearInfo(decodeTimestamp(flag), actualLocation);
    }

    // BOUNDARY_SPOTTED
    // Boundary spotted uses exact coordinate
    // [23:21]      [20:17]        [16:2]                [1:0]
    // flagType     timestamp      boundaryExactCoord    boundaryType
    /**
     * @param
     * @return flag for boundary at location
     */
    public static int encodeBoundarySpotted(int roundNum, int exactBoundaryLocation, BoundaryType boundaryType) {
        int flag = encodeFlagBase(FlagType.BOUNDARY_SPOTTED, roundNum);
        flag ^= exactBoundaryLocation << 2;
        flag ^= boundaryType.ordinal();
        return flag;
    }

    /**
     * @param flag to be decoded
     * @return information for boundary spotted
     */
    public static BoundarySpottedInfo decodeBoundarySpotted(int flag) {
        int exactBoundaryLocation = (flag >>> 2) & 0b111111111111111;
        BoundaryType boundaryType = BoundaryType.values[flag & 0b11];
        return new BoundarySpottedInfo(decodeTimestamp(flag), exactBoundaryLocation, boundaryType);
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
     * @return timestamp of the flag
     */
    public static int decodeTimestamp(int flag) {
        return ((flag >> TIMESTAMP_SHIFT) & 0b1111) * 200;
    }

    private static int encodeFlagBase(FlagType type, int roundNum) {
        return (type.ordinal() << FLAGTYPE_SHIFT) ^ (((roundNum/200) & 0b1111)  << TIMESTAMP_SHIFT);
    }

    private static int signExtend(int num, int bitWidth) {
        // Shift bits to the front, then shift back with signed right shift
        return (num << (32 - bitWidth)) >> (32 - bitWidth);
    }
}
