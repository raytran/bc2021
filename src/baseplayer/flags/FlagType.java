package baseplayer.flags;

public enum FlagType {
    INVALID, // not used, must be here so default flag works
    ENEMY_SPOTTED,
    NEUTRAL_EC_SPOTTED,
    BOUNDARY_SPOTTED,
    GOING_TO_NEUTRAL_EC;
    public static final FlagType[] values = values();
}
