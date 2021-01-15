package baseplayer.flags;

public enum FlagType {
    INVALID, // not used, must be here so default flag works
    ENEMY_SPOTTED,
    NEUTRAL_EC_SPOTTED,
    ENEMY_CLEAR,
    NEUTRAL_EC_CLEAR,
    BOUNDARY_SPOTTED,
    ASSISTANCE_REQUIRED;
    public static final FlagType[] values = values();
}