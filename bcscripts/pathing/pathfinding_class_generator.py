# ========================================================================== BEGIN SETTINGS
import settings as s
#======================================================================= BEGIN CLASS
i_to_xy = {i:xy for xy, i in s.target['XY_TO_I'].items()}

print("import battlecode.common.*;")
print("public class " + s.target['CLASSNAME'] + "{")
print ("    public static RobotController rc;")
i_to_xy_arrays = {}
for i, delta in i_to_xy.items():
    i_to_xy_arrays[i] = s.target['VAR_INDEX_TO_DELTA_NAME'] + str(i)
    print("    public static final int[] " + s.target['VAR_INDEX_TO_DELTA_NAME'] + str(i) + " = new int[]{" + str(delta[0]) +","+ str(delta[1]) + "};")


statics_arrays = {}
for index, neighbors in s.target['INDEX_TO_NEIGHOR'].items():
    java_style_neighbors = "{"
    java_style_neighbors += ", ".join(str(x) for x in neighbors)
    java_style_neighbors += "}"
    statics_arrays[index] = (s.target['VAR_NEIGHBOR_NAME'] + str(index))
    print("    public static final int[] " + s.target['VAR_NEIGHBOR_NAME'] + str(index) + " = "+ java_style_neighbors + ";")






# BEGIN CODE 
print("    static void "+  s.target['BELLMAN_FORD_METHOD_NAME'] + "(MapLocation targetLoc) throws GameActionException {")
# Start by getting the closest location on the edge from target
#for (int i = 0; i < outside.length; i++) {
#            int idx = outside[i];
#            int[] deltas = northIndexToDelta(idx);
#            MapLocation candidate = new MapLocation(currentLoc.x + deltas[0], currentLoc.y + deltas[1]);
#            if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){
#                minDist = candidate.distanceSquaredTo(targetLoc);
#                replacement = candidate;
#            }
#        }
#
#        targetLoc = replacement;
print("        MapLocation currentLoc = rc.getLocation();")
print("        int minDist = Integer.MAX_VALUE;")
print("        MapLocation replacement = null;")
print("        MapLocation candidate = null;")

for i in s.target['EDGE_TILES']:
    print("        candidate = new MapLocation(currentLoc.x"  + (" + " + str(i_to_xy[i][0]) if i_to_xy[i][0] != 0 else  "") +", currentLoc.y"+ (" + " + str(i_to_xy[i][1]) if i_to_xy[i][1] != 0 else "") + ");")
    print("        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){")
    print("            minDist = candidate.distanceSquaredTo(targetLoc);")
    print("            replacement = candidate;")
    print("        }")

print("        targetLoc = replacement;")
print("        if (targetLoc != null){")
# Prepare Bellman Ford
print("            int[] parents = new int[" + str(len(s.target['INDEX_TO_NEIGHOR'])) + "];")
print("            for (int i = 0; i < " + str(len(s.target['INDEX_TO_NEIGHOR'])) + "; i++) {")
print("                parents[i] = -1;" )
print("            }")
for i in range(len(s.target['INDEX_TO_NEIGHOR'])):
    print("            int distance" + str(i) + " = 99999;")
    print("            int weight" + str(i) + ";")


print("            MapLocation pos = null;")
for i in range(len(s.target['INDEX_TO_NEIGHOR'])): 
    print("            pos = rc.getLocation().translate(" + str(i_to_xy[i][0]) + ", " + str(i_to_xy[i][1]) + ");")
    print("            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {")
    print("                weight" + str(i) +" = 999999;")
    print("            } else {")
    print("                weight" + str(i) + " = (int) (100 * ((1 - rc.sensePassability(pos))));")
    print("            }")
print("            distance" + str(s.target['XY_TO_I'][(0, 0)]) + " = 0;")



#    // Relax edges u -> v

#   for (int i=0;i < 3;i++) {
#       for (int u=0; u < 22; u++) {
#           for (int v : getNorthSemicircleNeighbors(u)) {
#               double newDist = distances[u] + weights[v];
#               if (newDist < distances[v]) {
#                   distances[v] = newDist;
#                   parents[v] = u;
#               }
#           }
#       }
#   }


print("            int newDist;")
for i in range(s.target['BELLMAN_ITERATIONS']):
    for u in range(len(s.target['INDEX_TO_NEIGHOR'])):
        for v in s.target['INDEX_TO_NEIGHOR'][u]:
            print("            newDist = distance" + str(u) + " + weight" + str(v) + ";")
            print("            if (newDist < distance" + str(v) + ") {")
            print("                distance"+ str(v) + " = newDist;")
            print("                parents[" + str(v) + "] = " + str(u) + ";")
            print("            }")


# Find path (traverse parent pointers from target)
print("            int current = " + s.target['DELTA_TO_INDEX_METHOD_NAME'] + "(targetLoc.x - currentLoc.x, targetLoc.y - currentLoc.y);")
print("            int last = -1;")
print("            while (parents[current] != -1) {")
print("                last = current;")
print("                current = parents[current];")
print("            }")
print("            if (last != -1){")
print("                int[] delta = " + s.target['INDEX_TO_DELTA_METHOD_NAME'] + "(last);")
print("                MapLocation target = new MapLocation(rc.getLocation().x + delta[0],  rc.getLocation().y + delta[1]);")
print("                if (rc.canMove(rc.getLocation().directionTo(target))){")
print("                    rc.move(rc.getLocation().directionTo(target));")
print("                }")
print("            }")
print("        }")
print("    }")




print("    static int " + s.target['DELTA_TO_INDEX_METHOD_NAME'] + "(int x, int y) {")
print("        String delta = x + \"|\" + y;")
print("        switch (delta) {")
for delta, i in s.target['XY_TO_I'].items():
    print("            case \"" + str(delta[0]) +"|"+ str(delta[1]) + "\":")
    print("                return " + str(i) + ";")
print("            default: break;")
print("         }")
print("        throw new RuntimeException(\"Bad delta\");")
print("    }")





print("    static int[] " + s.target['INDEX_TO_DELTA_METHOD_NAME'] + "(int index) {")
print("        switch (index) {")
for i, delta in i_to_xy.items():
    print("        case "+ str(i) + ":")
    print("            return " + i_to_xy_arrays[i] + ";")
print("        default: break;")
print("        }")
print("        throw new RuntimeException(\"Bad index\");")
print("    }")






print("    static int[] " + s.target['INDEX_TO_NEIGHBOR_METHOD_NAME'] + "(int index) {")
print("        switch (index) {")
for index, neighbors in s.target['INDEX_TO_NEIGHOR'].items():
    print("            case " + str(index) + ":")
    print("                return "+ statics_arrays[index] + ";")
print("            default: break;")
print("        }")
print("        throw new RuntimeException(\"Bad index\");")
print("     }")


print("    public static void init(RobotController rc) {")
print("        " +s.target['CLASSNAME']+".rc = rc;")
print("    }")



print("    static void testMap() {")
print("        for (int i=0;i<" + str(len(s.target['INDEX_TO_NEIGHOR'])) + ";i++) {")
print("            for (int j=0;j<" +str(len(s.target['INDEX_TO_NEIGHOR']))+ " ;j++) {")
print("                int[] delta1 = "+s.target['INDEX_TO_DELTA_METHOD_NAME']+"(j);")
print("                rc.setIndicatorDot(new MapLocation(rc.getLocation().x + delta1[0], rc.getLocation().y + delta1[1]), 255, 0, 0);")
print("            }")
print("        for (int neighborIndex : " + s.target['INDEX_TO_NEIGHBOR_METHOD_NAME'] + "(i)) {")
print("            int[] delta = "+s.target['INDEX_TO_DELTA_METHOD_NAME']+"(neighborIndex);")
print("            rc.setIndicatorDot(new MapLocation(rc.getLocation().x + delta[0], rc.getLocation().y + delta[1]), 0, 0, 255);")
print("        }")
print("        Clock.yield();")
print("        }")
print("    }")

print("}")
