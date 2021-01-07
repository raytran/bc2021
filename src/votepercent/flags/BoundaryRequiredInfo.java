package votepercent.flags;

public class BoundaryRequiredInfo {
    public final boolean northFound;
    public final boolean eastFound;
    public final boolean southFound;
    public final boolean westFound;
    public BoundaryRequiredInfo(boolean north, boolean east, boolean south, boolean west) {
        this.northFound = north;
        this.eastFound = east;
        this.southFound = south;
        this.westFound = west;
    }
}
