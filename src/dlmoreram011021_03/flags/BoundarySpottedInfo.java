package dlmoreram011021_03.flags;

public class BoundarySpottedInfo {
    public final BoundaryType boundaryType;
    public final int exactBoundaryLocation;
    public BoundarySpottedInfo(int exactBoundaryLocation, BoundaryType boundaryType) {
        this.boundaryType = boundaryType;
        this.exactBoundaryLocation = exactBoundaryLocation;
    }
}
