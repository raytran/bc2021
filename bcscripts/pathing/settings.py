
northwest = dict(
        BELLMAN_ITERATIONS = 3,
        VAR_INDEX_TO_DELTA_NAME = "NORTHWEST_I_TO_DELTA",
        VAR_NEIGHBOR_NAME = "NORTHWEST_NEIGHBORS",

        BELLMAN_FORD_METHOD_NAME = "pathTo",
        DELTA_TO_INDEX_METHOD_NAME = "northwestDeltaToIndex",
        INDEX_TO_DELTA_METHOD_NAME = "northwestIndexToDelta",
        INDEX_TO_NEIGHBOR_METHOD_NAME = "getNorthwestSemicircleNeighbors",

        CLASSNAME = "NorthwestPather",

        # Indicies on the edge
        EDGE_TILES = [5,9,10,22,21,20,19,18,17,16,8,6],
        # tuple -> index
        XY_TO_I = {
            (-3,0):22,
            (-3,1):21,
            (-3,2):20,
            (-2,-1):10,
            (-2,0):11,
            (-2,1):12,
            (-2,2):13,
            (-2,3):19,
            (-1,-2):9,
            (-1,-1):4,
            (-1,0):3,
            (-1,1):2,
            (-1,2):14,
            (-1,3):18,
            (2,1):8,
            (1,0):6,
            (1,1):7,
            (1,2):16,
            (0,-1):5,
            (0,0):0,
            (0,1):1,
            (0,2):15,
            (0,3):17
            },

        # int -> list[int]

        INDEX_TO_NEIGHOR = {
            0:[2,1,7,3,6,4,5],
            1:[14,15,16,2,7,3,0,6],
            2:[13,14,15,12,1,11,3,0],
            3:[12,2,1,11,0,10,4,5],
            4:[11,3,0,10,5,9],
            5:[3,0,6,4,9],
            6:[1,7,8,0,5],
            7:[15,16,1,8,0,6],
            8:[16,7,6],
            9:[10,4,5],
            10:[22,11,3,4,9],
            11:[21,12,2,22,3,10,4],
            12:[22,11,3,21,2,20,13,14],
            13:[21,12,2,20,14,19,18],
            14:[12,2,1,13,15,19,18,17],
            15:[2,1,7,14,16,18,17],
            16:[1,7,8,15,17],
            17:[14,15,16,18],
            18:[13,14,15,19,17],
            19:[20,13,14,18],
            20:[21,12,13,19],
            21:[22,11,12,13,20],
            22:[10,11,12,21]
            }
        )

west = dict(
        BELLMAN_ITERATIONS = 3,
        VAR_INDEX_TO_DELTA_NAME = "WEST_I_TO_DELTA",
        VAR_NEIGHBOR_NAME = "WEST_NEIGHBORS",

        BELLMAN_FORD_METHOD_NAME = "pathTo",
        DELTA_TO_INDEX_METHOD_NAME = "westDeltaToIndex",
        INDEX_TO_DELTA_METHOD_NAME = "westIndexToDelta",
        INDEX_TO_NEIGHBOR_METHOD_NAME = "getWestSemicircleNeighbors",

        CLASSNAME = "WestPather",

        # Indicies on the edge
        EDGE_TILES = [6,8,9,10,17,18,19,20,21,14,15,16,5],
        # tuple -> index
        XY_TO_I = {
            (-3,-2):21,
            (-3,-1):20,
            (-3,0):19,
            (-3,1):18,
            (-3,2):17,
            (-2,-2):14,
            (-2,-1):13,
            (-2,0):12,
            (-2,1):11,
            (-2,2):10,
            (-1,-2):15,
            (-1,-1):3,
            (-1,0):2,
            (-1,1):1,
            (-1,2):9,
            (1,-1):5,
            (1,1):6,
            (0,-2):16,
            (0,-1):4,
            (0,0):0,
            (0,1):7,
            (0,2):8
            },

        # int -> list[int]

        INDEX_TO_NEIGHOR = {
            0:[1,7,6,2,3,4,5],
            1:[10,9,8,11,7,12,2,0],
            2:[11,1,7,12,0,13,3,4],
            3:[12,2,0,13,4,14,15,16],
            4:[0,2,3,5,15,16],
            5:[0,4,16],
            6:[8,7,0],
            7:[8,9,1,6,2,0],
            8:[9,1,7,6],
            9:[10,11,1,7,8],
            10:[17,18,11,1,9],
            11:[17,10,9,18,1,19,12,2],
            12:[18,11,1,19,2,20,13,3],
            13:[21,14,15,20,3,19,12,2],
            14:[21,20,13,3,15],
            15:[14,13,3,4,16],
            16:[15,3,4,5],
            17:[10,18,11],
            18:[17,10,11,12,19],
            19:[18,11,12,13,20],
            20:[19,12,13,14,21],
            21:[20,13,14]

            }
        )

southwest = dict(
        BELLMAN_ITERATIONS = 3,
        VAR_INDEX_TO_DELTA_NAME = "SOUTHWEST_I_TO_DELTA",
        VAR_NEIGHBOR_NAME = "SOUTHWEST_NEIGHBORS",

        BELLMAN_FORD_METHOD_NAME = "pathTo",
        DELTA_TO_INDEX_METHOD_NAME = "southwestDeltaToIndex",
        INDEX_TO_DELTA_METHOD_NAME = "southwestIndexToDelta",
        INDEX_TO_NEIGHBOR_METHOD_NAME = "getSouthwestSemicircleNeighbors",

        CLASSNAME = "SouthwestPather",

        # Indicies on the edge
        EDGE_TILES = [8,15,16,17,18,20,21,22,10,9,7,1],
        # tuple -> index
        XY_TO_I = {
            (-3,-2):18,
            (-3,-1):17,
            (-3,0):16,
            (-2,-3):20,
            (-2,-2):19,
            (-2,-1):13,
            (-2,0):14,
            (-2,1):15,
            (-1,-3):21,
            (-1,-2):12,
            (-1,-1):4,
            (-1,0):3,
            (-1,1):2,
            (-1,2):8,
            (2,-1):9,
            (1,-2):10,
            (1,-1):6,
            (1,0):7,
            (0,-3):22,
            (0,-2):11,
            (0,-1):5,
            (0,0):0,
            (0,1):1
            },

        # int -> list[int]

        INDEX_TO_NEIGHOR = {
            0:[2,1,3,7,4,5,6],
            1:[8,2,3,0,7],
            2:[8,15,1,14,3,0],
            3:[15,2,1,14,0,13,4,5],
            4:[14,3,0,13,5,19,12,11],
            5:[3,0,7,4,6,12,11,10],
            6:[0,7,5,9,11,10],
            7:[1,0,5,6,9],
            8:[15,2,1],
            9:[7,6,10],
            10:[5,6,9,11,22],
            11:[21,22,12,10,4,5,6],
            12:[20,21,22,19,11,13,4,5],
            13:[18,19,12,17,4,16,14,3],
            14:[17,13,4,16,3,15,2],
            15:[16,14,3,2,8],
            16:[17,13,14,15],
            17:[18,19,13,14,16],
            18:[20,19,13,17],
            19:[20,21,18,12,17,13,4],
            20:[21,12,18,19],
            21:[20,19,12,11,22],
            22:[21,12,11,10]
            }
        )


south = dict(
        BELLMAN_ITERATIONS = 3,
        VAR_INDEX_TO_DELTA_NAME = "SOUTH_I_TO_DELTA",
        VAR_NEIGHBOR_NAME = "SOUTH_NEIGHBORS",

        BELLMAN_FORD_METHOD_NAME = "pathTo",
        DELTA_TO_INDEX_METHOD_NAME = "southDeltaToIndex",
        INDEX_TO_DELTA_METHOD_NAME = "southIndexToDelta",
        INDEX_TO_NEIGHBOR_METHOD_NAME = "getSouthSemicircleNeighbors",

        CLASSNAME = "SouthPather",

        # Indicies on the edge
        EDGE_TILES = [1,8,9,10,17,18,19,20,21,14,15,16,7],
        # tuple -> index
        XY_TO_I = {
            (-2,-3):17,
            (-2,-2):10,
            (-2,-1):9,
            (-2,0):8,
            (-1,-3):18,
            (-1,-2):11,
            (-1,-1):3,
            (-1,0):2,
            (-1,1):1,
            (0,-3):19,
            (0,-2):12,
            (0,-1):4,
            (0,0):0,
            (2,-3):21,
            (2,-2):14,
            (2,-1):15,
            (2,0):16,
            (1,-3):20,
            (1,-2):13,
            (1,-1):5,
            (1,0):6,
            (1,1):7
            },

        # int -> list[int]

        INDEX_TO_NEIGHOR = {
            0:[1,2,3,4,5,6,7],
            1:[8,2,0],
            2:[1,8,0,9,3,4],
            3:[8,2,0,9,4,10,11,12],
            4:[2,0,6,3,5,11,12,13],
            5:[0,6,16,4,15,12,13,14],
            6:[7,0,16,4,5,15],
            7:[0,6,16],
            8:[1,2,3,9],
            9:[8,2,3,10,11],
            10:[9,3,11,18,17],
            11:[9,3,4,10,12,17,18,19],
            12:[18,19,20,11,13,3,4,5],
            13:[19,20,21,12,14,4,5,15],
            14:[20,21,13,5,15],
            15:[13,14,5,6,16],
            16:[5,15,6,7],
            17:[18,11,10],
            18:[17,10,11,12,19],
            19:[18,11,12,13,20],
            20:[19,12,13,14,21],
            21:[20,13,14]
            }
        )


northeast = dict(
        BELLMAN_ITERATIONS = 3,
        VAR_INDEX_TO_DELTA_NAME = "NORTHEAST_I_TO_DELTA",
        VAR_NEIGHBOR_NAME = "NORTHEAST_NEIGHBORS",

        BELLMAN_FORD_METHOD_NAME = "pathTo",
        DELTA_TO_INDEX_METHOD_NAME = "northeastDeltaToIndex",
        INDEX_TO_DELTA_METHOD_NAME = "northeastIndexToDelta",
        INDEX_TO_NEIGHBOR_METHOD_NAME = "getNortheastSemicircleNeighbors",

        CLASSNAME = "NortheastPather",

        # Indicies on the edge
        EDGE_TILES = [7, 16, 15,22,21,20,19,18,17,9,8,1],
        # tuple -> index
        XY_TO_I = {
            (-2,1):16,
            (-1,0):7,
            (-1,1):6,
            (-1,2):15,
            (0,-1):1,
            (0,0):0,
            (0,1):5,
            (0,2):14,
            (0,3):22,
            (1,-2):8,
            (1,-1):2,
            (1,0):3,
            (1,1):4,
            (1,2):13,
            (1,3):21,
            (2,-1):9,
            (2,0):10,
            (2,1):11,
            (2,2):12,
            (2,3):20,
            (3,0):17,
            (3,1):18,
            (3,2):19

            },

        # int -> list[int]
        INDEX_TO_NEIGHOR = {
            0:[1,2,3,4,5,6,7],
            1:[7,0,3,2,8],
            2:[1,0,3,10,9,8],
            3:[1,2,9,0,10,5,4,11],
            4:[0,3,10,5,11,14,13,12],
            5:[7,0,3,6,4,15,14,13],
            6:[7,0,16,5,15,14],
            7:[1,0,16,6,5],
            8:[1,2,9],
            9:[8,2,3,10,17],
            10:[2,9,3,17,4,11,18,],
            11:[3,10,17,4,18,13,12,19],
            12:[4,11,18,13,19,21,20,],
            13:[5,4,11,14,12,22,21,20],
            14:[6,5,4,15,13,22,21,],
            15:[16,6,5,14,22],
            16:[7,6,15],
            17:[9,10,11,18],
            18:[10,17,11,12,19],
            19:[11,18,12,20],
            20:[13,12,19,21],
            21:[14,13,12,22,20],
            22:[15,14,13,21]
            }
        )



north = dict(
        BELLMAN_ITERATIONS = 3,
        VAR_INDEX_TO_DELTA_NAME = "NORTH_I_TO_DELTA",
        VAR_NEIGHBOR_NAME = "NORTH_NEIGHBORS",

        BELLMAN_FORD_METHOD_NAME = "pathTo",
        DELTA_TO_INDEX_METHOD_NAME = "northDeltaToIndex",
        INDEX_TO_DELTA_METHOD_NAME = "northIndexToDelta",
        INDEX_TO_NEIGHBOR_METHOD_NAME = "getNorthSemicircleNeighbors",

        CLASSNAME = "NorthPather",

        # Indicies on the edge
        EDGE_TILES = [6,16,15,14,17,18,19,20,21,10,9,8,7],
        # tuple -> index
        XY_TO_I = {
            (-2,0):16,
            (-2,1):15,
            (-2,2):14,
            (-2,3):17,
            (-1,-1):6,
            (-1,0):5,
            (-1,1):4,
            (-1,2):13,
            (-1,3):18,
            (0,0):0,
            (0,1):3,
            (0,2):12,
            (0,3):19,
            (1,-1):7,
            (1,0):1,
            (1,1):2,
            (1,2):11,
            (1,3):20,
            (2,0):8,
            (2,1):9,
            (2,2):10,
            (2,3):21
            },

        # int -> list[int]
        INDEX_TO_NEIGHOR = {
            0:[4,3,2,5,1,6,7],
            1:[3,2,9,0,8,7],
            2:[12,11,10,3,9,0,1,8],
            3:[13,12,11,4,2,5,0,1],
            4:[14,13,12,15,3,16,5,0],
            5:[15,4,3,16,0,6],
            6:[16,5,0],
            7:[0,1,8],
            8:[2,9,1,7],
            9:[10,11,2,1,8],
            10:[21,20,11,2,9],
            11:[19,20,21,12,3,10,2,9],
            12:[18,19,20,13,11,4,3,2],
            13:[17,18,19,14,12,15,4,3],
            14:[17,18,13,4,15],
            15:[14,13,4,5,16],
            16:[15,4,5,6],
            17:[18,14,13],
            18:[17,14,13,12,19],
            19:[18,13,12,11,20],
            20:[19,12,11,10,21],
            21:[20,11,10]
            }
        )



southeast = dict(
        BELLMAN_ITERATIONS = 3,
        VAR_INDEX_TO_DELTA_NAME = "SOUTHEAST_I_TO_DELTA",
        VAR_NEIGHBOR_NAME = "SOUTHEAST_NEIGHBORS",

        BELLMAN_FORD_METHOD_NAME = "pathTo",
        DELTA_TO_INDEX_METHOD_NAME = "southeastDeltaToIndex",
        INDEX_TO_DELTA_METHOD_NAME = "southeastIndexToDelta",
        INDEX_TO_NEIGHBOR_METHOD_NAME = "getSoutheastSemicircleNeighbors",

        CLASSNAME = "SoutheastPather",

        # Indicies on the edge
        EDGE_TILES = [8,9,17,18,19,20,21,22,15,16,7,1],
        # tuple -> index
        XY_TO_I = {
            (-2,-1):8,
            (-1,-2):9,
            (-1,-1):2,
            (-1,0):1,
            (0,-3):17,
            (0,-2):10,
            (0,-1):3,
            (0,0):0,
            (0,1):7,
            (1,-3):18,
            (1,-2):11,
            (1,-1):4,
            (1,0):5,
            (1,1):6,
            (1,2):16,
            (2,-3):19,
            (2,-2):12,
            (2,-1):13,
            (2,0):14,
            (2,1):15,
            (3,-2):20,
            (3,-1):21,
            (3,0):22
            },

        # int -> list[int]
        INDEX_TO_NEIGHOR = {
            0:[1,2,3,4,5,6,7],
            1:[8,2,3,0,7],
            2:[9,10,8,3,1,0],
            3:[9,10,11,2,4,1,0,5],
            4:[10,11,12,3,13,0,5,14],
            5:[3,4,13,0,14,7,6,15],
            6:[0,5,14,7,15,16],
            7:[1,0,5,6,16],
            8:[9,2,1],
            9:[17,10,8,2,3],
            10:[17,18,9,11,2,3,4,],
            11:[17,18,19,10,12,3,4,13],
            12:[18,19,11,20,4,13,21,],
            13:[11,12,20,4,21,5,14,22],
            14:[4,13,21,5,22,6,15],
            15:[5,14,22,6,16],
            16:[7,6,15],
            17:[9,10,11,18],
            18:[17,10,11,12,19],
            19:[18,11,12,20],
            20:[19,12,13,21],
            21:[12,20,13,14,22],
            22:[13,21,14,15]
            }
        )


east = dict(
        BELLMAN_ITERATIONS = 3,
        VAR_INDEX_TO_DELTA_NAME = "EAST_I_TO_DELTA",
        VAR_NEIGHBOR_NAME = "EAST_NEIGHBORS",

        BELLMAN_FORD_METHOD_NAME = "pathTo",
        DELTA_TO_INDEX_METHOD_NAME = "eastDeltaToIndex",
        INDEX_TO_DELTA_METHOD_NAME = "eastIndexToDelta",
        INDEX_TO_NEIGHBOR_METHOD_NAME = "getEastSemicircleNeighbors",

        CLASSNAME = "EastPather",

        # Indicies on the edge
        EDGE_TILES = [1,7,16,15,14,21,20,19,18,17,10,9,8],
        # tuple -> index
        XY_TO_I = {
            (-1,-1):1,
            (-1,1):7,
            (0,-2):8,
            (0,-1):2,
            (0,0):0,
            (0,1):6,
            (0,2):16,
            (1,-2):9,
            (1,-1):3,
            (1,0):4,
            (1,1):5,
            (1,2):15,
            (2,-2):10,
            (2,-1):11,
            (2,0):12,
            (2,1):13,
            (2,2):14,
            (3,-2):17,
            (3,-1):18,
            (3,0):19,
            (3,1):20,
            (3,2):21
            },

        # int -> list[int]
        INDEX_TO_NEIGHOR = {
            0:[1,2,3,4,5,6,7],
            1:[8,2,0],
            2:[0,4,1,3,8,9],
            3:[8,9,10,2,11,0,4,12],
            4:[2,3,11,0,12,6,5,13],
            5:[0,4,12,6,13,14,15,16],
            6:[0,4,7,5,15,16],
            7:[0,6,16],
            8:[1,2,3,9],
            9:[8,10,2,3,11],
            10:[9,17,3,11,18],
            11:[9,10,17,3,18,4,12,19],
            12:[3,11,18,4,19,5,13,20],
            13:[4,12,19,5,20,15,14,21],
            14:[5,13,20,15,21],
            15:[6,5,13,16,14],
            16:[7,6,5,15],
            17:[10,11,18],
            18:[10,17,11,12,19],
            19:[11,18,12,13,20],
            20:[12,19,13,14,21],
            21:[13,14,20]
            }
        )





target = northeast

