package placeholderplayer.flags;

public enum FlagType {
    INVALID, // not used, must be here so default flag works
    ENEMY_SPOTTED,
    NEUTRAL_EC_SPOTTED,
    AREA_CLEAR,
    BOUNDARY_SPOTTED,
    OP_SPAWNED,
    ASSISTANCE_REQUIRED,
    ROBOT_ROLE;
    public static final FlagType[] values = values();
}