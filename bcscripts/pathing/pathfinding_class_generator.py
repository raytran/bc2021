# ========================================================================== BEGIN SETTINGS
import settings as s
#======================================================================= BEGIN CLASS
def make_class(target):
    f = open(target['CLASSNAME'] + ".java", "w")

    def write_w_newline(string):
        f.write(string + "\n")


    i_to_xy = {i:xy for xy, i in target['XY_TO_I'].items()}

    write_w_newline("import battlecode.common.*;")
    write_w_newline("public class " + target['CLASSNAME'] + "{")
    write_w_newline ("    public static RobotController rc;")
    i_to_xy_arrays = {}
    for i, delta in i_to_xy.items():
        i_to_xy_arrays[i] = target['VAR_INDEX_TO_DELTA_NAME'] + str(i)
        write_w_newline("    public static final int[] " + target['VAR_INDEX_TO_DELTA_NAME'] + str(i) + " = new int[]{" + str(delta[0]) +","+ str(delta[1]) + "};")


    statics_arrays = {}
    for index, neighbors in target['INDEX_TO_NEIGHOR'].items():
        java_style_neighbors = "{"
        java_style_neighbors += ", ".join(str(x) for x in neighbors)
        java_style_neighbors += "}"
        statics_arrays[index] = (target['VAR_NEIGHBOR_NAME'] + str(index))
        write_w_newline("    public static final int[] " + target['VAR_NEIGHBOR_NAME'] + str(index) + " = "+ java_style_neighbors + ";")






    # BEGIN CODE 
    write_w_newline("    static void "+  target['BELLMAN_FORD_METHOD_NAME'] + "(MapLocation targetLoc) throws GameActionException {")
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
    write_w_newline("        MapLocation currentLoc = rc.getLocation();")
    write_w_newline("        int minDist = Integer.MAX_VALUE;")
    write_w_newline("        MapLocation replacement = null;")
    write_w_newline("        MapLocation candidate = null;")
    
    for i in range(len(target['INDEX_TO_NEIGHOR'])):
        write_w_newline("        candidate = new MapLocation(currentLoc.x"  + (" + " + str(i_to_xy[i][0]) if i_to_xy[i][0] != 0 else  "") +", currentLoc.y"+ (" + " + str(i_to_xy[i][1]) if i_to_xy[i][1] != 0   else "") + ");")
        write_w_newline("        if (rc.onTheMap(candidate) && !rc.isLocationOccupied(candidate) && candidate.distanceSquaredTo(targetLoc) < minDist){")
        write_w_newline("            minDist = candidate.distanceSquaredTo(targetLoc);")
        write_w_newline("            replacement = candidate;")
        write_w_newline("        }")
    
    write_w_newline("        targetLoc = replacement;")
    write_w_newline("        if (targetLoc != null){")
    # Prepare Bellman Ford
    write_w_newline("            int[] parents = new int[" + str(len(target['INDEX_TO_NEIGHOR'])) + "];")
    write_w_newline("            for (int i = 0; i < " + str(len(target['INDEX_TO_NEIGHOR'])) + "; i++) {")
    write_w_newline("                parents[i] = -1;" )
    write_w_newline("            }")
    for i in range(len(target['INDEX_TO_NEIGHOR'])):
        write_w_newline("            int distance" + str(i) + " = 99999;")
        write_w_newline("            int weight" + str(i) + ";")
    
    
    write_w_newline("            MapLocation pos = null;")
    for i in range(len(target['INDEX_TO_NEIGHOR'])): 
        write_w_newline("            pos = rc.getLocation().translate(" + str(i_to_xy[i][0]) + ", " + str(i_to_xy[i][1]) + ");")
        write_w_newline("            if (!rc.onTheMap(pos) || (!rc.getLocation().equals(pos) && rc.isLocationOccupied(pos))) {")
        write_w_newline("                weight" + str(i) +" = 999999;")
        write_w_newline("            } else {")
        write_w_newline("                weight" + str(i) + " = (int) (100 * ((1 - rc.sensePassability(pos))));")
        write_w_newline("            }")
    write_w_newline("            distance" + str(target['XY_TO_I'][(0, 0)]) + " = 0;")
    
    
    
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
    
    
    write_w_newline("            int newDist;")
    for i in range(target['BELLMAN_ITERATIONS']):
        for u in range(len(target['INDEX_TO_NEIGHOR'])):
            for v in target['INDEX_TO_NEIGHOR'][u]:
                write_w_newline("            newDist = distance" + str(u) + " + weight" + str(v) + ";")
                write_w_newline("            if (newDist < distance" + str(v) + ") {")
                write_w_newline("                distance"+ str(v) + " = newDist;")
                write_w_newline("                parents[" + str(v) + "] = " + str(u) + ";")
                write_w_newline("            }")
    
    
    # Find path (traverse parent pointers from target)
    write_w_newline("            int current = " + target['DELTA_TO_INDEX_METHOD_NAME'] + "(targetLoc.x - currentLoc.x, targetLoc.y - currentLoc.y);")
    write_w_newline("            int last = -1;")
    write_w_newline("            while (parents[current] != -1) {")
    write_w_newline("                last = current;")
    write_w_newline("                current = parents[current];")
    write_w_newline("            }")
    write_w_newline("            if (last != -1){")
    write_w_newline("                int[] delta = " + target['INDEX_TO_DELTA_METHOD_NAME'] + "(last);")
    write_w_newline("                MapLocation target = new MapLocation(rc.getLocation().x + delta[0],  rc.getLocation().y + delta[1]);")
    write_w_newline("                if (rc.canMove(rc.getLocation().directionTo(target))){")
    write_w_newline("                    rc.move(rc.getLocation().directionTo(target));")
    write_w_newline("                }")
    write_w_newline("            }")
    write_w_newline("        }")
    write_w_newline("    }")
    
    
    
    
    write_w_newline("    static int " + target['DELTA_TO_INDEX_METHOD_NAME'] + "(int x, int y) {")
    write_w_newline("        String delta = x + \"|\" + y;")
    write_w_newline("        switch (delta) {")
    for delta, i in target['XY_TO_I'].items():
        write_w_newline("            case \"" + str(delta[0]) +"|"+ str(delta[1]) + "\":")
        write_w_newline("                return " + str(i) + ";")
    write_w_newline("            default: break;")
    write_w_newline("         }")
    write_w_newline("        throw new RuntimeException(\"Bad delta\");")
    write_w_newline("    }")
    
    
    
    
    
    write_w_newline("    static int[] " + target['INDEX_TO_DELTA_METHOD_NAME'] + "(int index) {")
    write_w_newline("        switch (index) {")
    for i, delta in i_to_xy.items():
        write_w_newline("        case "+ str(i) + ":")
        write_w_newline("            return " + i_to_xy_arrays[i] + ";")
    write_w_newline("        default: break;")
    write_w_newline("        }")
    write_w_newline("        throw new RuntimeException(\"Bad index\");")
    write_w_newline("    }")
    
    
    
    
    
    
    write_w_newline("    static int[] " + target['INDEX_TO_NEIGHBOR_METHOD_NAME'] + "(int index) {")
    write_w_newline("        switch (index) {")
    for index, neighbors in target['INDEX_TO_NEIGHOR'].items():
        write_w_newline("            case " + str(index) + ":")
        write_w_newline("                return "+ statics_arrays[index] + ";")
    write_w_newline("            default: break;")
    write_w_newline("        }")
    write_w_newline("        throw new RuntimeException(\"Bad index\");")
    write_w_newline("     }")
    
    
    write_w_newline("    public static void init(RobotController rc) {")
    write_w_newline("        " +target['CLASSNAME']+".rc = rc;")
    write_w_newline("    }")
    
    
    
    write_w_newline("    static void testMap() {")
    write_w_newline("        for (int i=0;i<" + str(len(target['INDEX_TO_NEIGHOR'])) + ";i++) {")
    write_w_newline("            for (int j=0;j<" +str(len(target['INDEX_TO_NEIGHOR']))+ " ;j++) {")
    write_w_newline("                int[] delta1 = "+target['INDEX_TO_DELTA_METHOD_NAME']+"(j);")
    write_w_newline("                rc.setIndicatorDot(new MapLocation(rc.getLocation().x + delta1[0], rc.getLocation().y + delta1[1]), 255, 0, 0);")
    write_w_newline("            }")
    write_w_newline("        for (int neighborIndex : " + target['INDEX_TO_NEIGHBOR_METHOD_NAME'] + "(i)) {")
    write_w_newline("            int[] delta = "+target['INDEX_TO_DELTA_METHOD_NAME']+"(neighborIndex);")
    write_w_newline("            rc.setIndicatorDot(new MapLocation(rc.getLocation().x + delta[0], rc.getLocation().y + delta[1]), 0, 0, 255);")
    write_w_newline("        }")
    write_w_newline("        Clock.yield();")
    write_w_newline("        }")
    write_w_newline("    }")
    
    write_w_newline("}")
    
    f.close()


for target in [s.north, s.east, s.south, s.west, s.northeast, s.northwest, s.southeast, s.southwest]:
    make_class(target)
