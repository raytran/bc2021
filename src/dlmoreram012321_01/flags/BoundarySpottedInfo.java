package dlmoreram012321_01.flags;

public class BoundarySpottedInfo {
    public final int timestamp;
    public final BoundaryType boundaryType;
    public final int exactBoundaryLocation;
    public BoundarySpottedInfo(int timestamp, int exactBoundaryLocation, BoundaryType boundaryType) {
        this.timestamp = timestamp;
        this.boundaryType = boundaryType;
        this.exactBoundaryLocation = exactBoundaryLocation;
    }
}
