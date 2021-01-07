package voteflat.flags;

public enum FlagType {
    INVALID, // not used, must be here so default flag works
    ENEMY_SPOTTED,
    BOUNDARY_SPOTTED,
    BOUNDARY_REQUIRED;
    public static final FlagType[] values = values();
}
