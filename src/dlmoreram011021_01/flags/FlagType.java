package dlmoreram011021_01.flags;

public enum FlagType {
    INVALID, // not used, must be here so default flag works
    ENEMY_SPOTTED,
    BOUNDARY_SPOTTED;
    public static final FlagType[] values = values();
}
