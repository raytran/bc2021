package dlmoreram012521_01.flags;

public enum FlagType {
    INVALID, // not used, must be here so default flag works
    ENEMY_SPOTTED,
    NEUTRAL_EC_SPOTTED,
    AREA_CLEAR,
    BOUNDARY_SPOTTED,
    OP_SPAWNED,
    ASSISTANCE_REQUIRED;
    public static final FlagType[] values = values();
}