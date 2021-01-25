package baseplayer.nav;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import java.util.Optional;

public class BoundingBox {
    private MapLocation pointOne;
    private MapLocation pointTwo;
    private int northBoundary;
    private int eastBoundary;
    private int southBoundary;
    private int westBoundary;

    public BoundingBox(MapLocation one, MapLocation two) {
        pointOne = one;
        pointTwo = two;
        if (pointOne.x > pointTwo.x) {
            eastBoundary = pointOne.x;
            westBoundary = pointTwo.x;
        } else {
            eastBoundary = pointTwo.x;
            westBoundary = pointOne.x;
        }
        if (pointOne.y > pointTwo.y) {
            northBoundary = pointOne.y;
            southBoundary = pointTwo.y;
        } else {
            northBoundary = pointTwo.y;
            southBoundary = pointOne.y;
        }
    }

    public MapLocation[] getCoords() {
        return new MapLocation[] {pointOne, pointTwo};
    }

    public int getNorthBoundary() {
        return northBoundary;
    }

    public int getEastBoundary() {
        return eastBoundary;
    }

    public int getSouthBoundary() {
        return southBoundary;
    }

    public int getWestBoundary() {
        return westBoundary;
    }

    public void expand(int i) {
        northBoundary++;
        eastBoundary++;
        southBoundary--;
        westBoundary--;
    }

    public boolean isContained(MapLocation loc) {
        assert loc != null;
        if (westBoundary <= loc.x && loc.x <= eastBoundary) {
            return southBoundary <= loc.y && loc.y <= northBoundary;
        } else {
            return false;
        }
    }

    public void translate(Direction dir) {
        pointOne = pointOne.add(dir);
        pointTwo = pointTwo.add(dir);
        if (pointOne.x > pointTwo.x) {
            eastBoundary = pointOne.x;
            westBoundary = pointTwo.x;
        } else {
            eastBoundary = pointTwo.x;
            westBoundary = pointOne.x;
        }
        if (pointOne.y > pointTwo.y) {
            northBoundary = pointOne.y;
            southBoundary = pointTwo.y;
        } else {
            northBoundary = pointTwo.y;
            southBoundary = pointOne.y;
        }
    }
}
