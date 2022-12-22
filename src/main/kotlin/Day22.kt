fun main() {
    Day22.run(getInput())
}

object Day22 {
    fun run(input: String) {
        part1(input)
        part2(input)
    }

    fun part1(input: String) {
        val (mapRaw, instrRaw) = input.split("\n\n")
        val map = mapRaw.toGrid { c, _ ->
            when(c) {
                ' ' -> Tile.VOID
                '.' -> Tile.OPEN
                '#' -> Tile.WALL
                else -> error(c)
            }
        }

        val instructions = parseInstructions(instrRaw)

        val startX = map.data[0].indexOfFirst { it != Tile.VOID }

        val agent = Agent(map, Point(startX, 0), Facing.RIGHT)

        for (instr in instructions) {
            agent.move(instr)
        }

        println((agent.pos.y + 1) * 1000 + (agent.pos.x + 1) * 4 + agent.facing.ordinal)
    }

    fun part2(input: String) {
        val (mapRaw, instrRaw) = input.split("\n\n")

        val instructions = parseInstructions(instrRaw)

        val mapRawLines = mapRaw.lines()

        fun gridMapper(c: Char): Tile {
            return when (c) {
                '.' -> Tile.OPEN
                '#' -> Tile.WALL
                else -> error(c)
            }
        }

        val frontGrid = mapRawLines.subList(0, 50).joinToString(separator = "\n") { it.substring(50, 100) }
            .toGrid { c, _ -> gridMapper(c) }
        val rightGrid = mapRawLines.subList(0, 50).joinToString(separator = "\n") { it.substring(100, 150) }
            .toGrid { c, _ -> gridMapper(c) }
        val downGrid = mapRawLines.subList(50, 100).joinToString(separator = "\n") { it.substring(50, 100) }
            .toGrid { c, _ -> gridMapper(c) }
        val leftGrid = mapRawLines.subList(100, 150).joinToString(separator = "\n") { it.substring(0, 50) }
            .toGrid { c, _ -> gridMapper(c) }
        val backgrid = mapRawLines.subList(100, 150).joinToString(separator = "\n") { it.substring(50, 100) }
            .toGrid { c, _ -> gridMapper(c) }
        val upGrid = mapRawLines.subList(150, 200).joinToString(separator = "\n") { it.substring(0, 50) }
            .toGrid { c, _ -> gridMapper(c) }

        val cube = Cube(
            front = frontGrid,
            right = rightGrid,
            down = downGrid,
            left = leftGrid,
            up = upGrid,
            back = backgrid,
        )

        var pos = CubePoint(Point(0, 0), CubeFace.FRONT, Facing.RIGHT)
        for (instr in instructions) {
            when (instr) {
                is Forward -> {
                    for (i in 1..instr.n) {
                        val newPos = pos.go()
                        if (cube[newPos] == Tile.OPEN) {
                            pos = newPos
                        } else {
                            break
                        }
                    }
                }
                Left -> {
                    pos = pos.turnLeft()
                }
                Right -> pos = pos.turnRight()
            }
        }

        val absPoint = when (pos.face) {
            CubeFace.FRONT -> pos.pos + Point(50, 0)
            CubeFace.RIGHT -> pos.pos + Point(100, 0)
            CubeFace.DOWN -> pos.pos + Point(50, 50)
            CubeFace.LEFT -> pos.pos + Point(0, 100)
            CubeFace.UP -> pos.pos + Point(0, 150)
            CubeFace.BACK -> pos.pos + Point(50, 100)
        }

        println((absPoint.y + 1) * 1000 + (absPoint.x + 1) * 4 + pos.facing.ordinal)
    }

    fun parseInstructions(instrRaw: String): List<Instruction> {
        val pattern = Regex("(\\d+)|([LR])")
        return pattern.findAll(instrRaw).map {
            val (n, lr) = it.destructured
            if (n.isNotEmpty()) {
                Forward(n.toInt())
            } else if (lr == "L") {
                Left
            } else if (lr == "R") {
                Right
            } else {
                error(it)
            }
        }.toList()
    }

    enum class Tile { VOID, OPEN, WALL }

    sealed interface Instruction
    object Left : Instruction
    object Right : Instruction
    data class Forward(val n: Int) : Instruction

    enum class Facing {
        RIGHT, DOWN, LEFT, UP;

        fun diff(): Point {
            return when (this) {
                RIGHT -> Point(1, 0)
                DOWN -> Point(0, 1)
                LEFT -> Point(-1, 0)
                UP -> Point(0, -1)
            }
        }
    }

    class Agent(val map: Grid<Tile>, var pos: Point, var facing: Facing) {
        fun move(instruction: Instruction) {
            when (instruction) {
                is Forward -> {
                    val diff = facing.diff()
                    for (i in 0 until instruction.n) {
                        when (map.getOrNull(pos + diff) ?: Tile.VOID) {
                            Tile.OPEN -> {
                                pos += diff
                            }
                            Tile.WALL -> { break }
                            Tile.VOID -> {
                                val realDest = when (facing) {
                                    Facing.RIGHT -> {
                                        val x = (0 until map.width())
                                            .first { x -> (map.getOrNull(Point(x, pos.y)) ?: Tile.VOID) != Tile.VOID }
                                        Point(x, pos.y)
                                    }
                                    Facing.DOWN -> {
                                        val y = (0 until map.height())
                                            .first { y -> (map.getOrNull(Point(pos.x, y)) ?: Tile.VOID) != Tile.VOID }
                                        Point(pos.x, y)
                                    }
                                    Facing.LEFT -> {
                                        val x = ((map.width() - 1) downTo 0)
                                            .first { x -> (map.getOrNull(Point(x, pos.y)) ?: Tile.VOID) != Tile.VOID }
                                        Point(x, pos.y)
                                    }
                                    Facing.UP -> {
                                        val y = ((map.height() - 1) downTo 0)
                                            .first { y -> (map.getOrNull(Point(pos.x, y)) ?: Tile.VOID) != Tile.VOID }
                                        Point(pos.x, y)
                                    }
                                }
                                if (map[realDest] != Tile.WALL) {
                                    pos = realDest
                                } else {
                                    break
                                }
                            }
                        }
                    }
                }
                Left -> {
                    facing = Facing.values()[Math.floorMod(facing.ordinal - 1, 4)]
                }
                Right -> {
                    facing = Facing.values()[Math.floorMod(facing.ordinal + 1, 4)]
                }
            }
        }
    }

    /*
    my input:
     FR
     D
    LB
    U
     */
    class Cube(
        front: Grid<Tile>, // +x -> right, +y -> down
        right: Grid<Tile>, // +x -> back, +y -> down
        down: Grid<Tile>, // +x -> right, +y -> back
        left: Grid<Tile>, // +x -> back, +y -> up
        up: Grid<Tile>, // +x -> back, +y -> right
        back: Grid<Tile>, // +x -> right, +y -> up
    ) {
        private val faces = mapOf(
            CubeFace.FRONT to front,
            CubeFace.RIGHT to right,
            CubeFace.DOWN to down,
            CubeFace.LEFT to left,
            CubeFace.UP to up,
            CubeFace.BACK to back,
        )

        operator fun get(p: CubePoint): Tile {
            return faces.getValue(p.face)[p.pos]
        }
    }
    enum class CubeFace { FRONT, RIGHT, DOWN, LEFT, UP, BACK }

    private const val faceSize = 50
    data class CubePoint(val pos: Point, val face: CubeFace, val facing: Facing) {
        fun go(): CubePoint {
            val innerPos = pos + facing.diff()
            if (innerPos.x in (0 until faceSize) && innerPos.y in (0 until faceSize)) {
                return CubePoint(innerPos, face, facing)
            }

            return when (face) {
                CubeFace.FRONT -> {
                    when (facing) {
                        Facing.RIGHT -> {
                            val newPos = Point(0, pos.y)
                            val newFace = CubeFace.RIGHT
                            val newFacing = Facing.RIGHT
                            CubePoint(newPos, newFace, newFacing)
                        }
                        Facing.DOWN -> {
                            val newPos = Point(pos.x, 0)
                            val newFace = CubeFace.DOWN
                            val newFacing = Facing.DOWN
                            CubePoint(newPos, newFace, newFacing)
                        }
                        Facing.LEFT -> {
                            val newPos = Point(0, faceSize - pos.y - 1)
                            val newFace = CubeFace.LEFT
                            val newFacing = Facing.RIGHT
                            CubePoint(newPos, newFace, newFacing)
                        }
                        Facing.UP -> {
                            val newPos = Point(0, pos.x)
                            val newFace = CubeFace.UP
                            val newFacing = Facing.RIGHT
                            CubePoint(newPos, newFace, newFacing)
                        }
                    }
                }
                CubeFace.RIGHT -> {
                    when (facing) {
                        Facing.RIGHT -> {
                            val newPos = Point(faceSize - 1, faceSize - pos.y - 1)
                            val newFace = CubeFace.BACK
                            val newFacing = Facing.LEFT
                            CubePoint(newPos, newFace, newFacing)
                        }
                        Facing.DOWN -> {
                            val newPos = Point(faceSize - 1, pos.x)
                            val newFace = CubeFace.DOWN
                            val newFacing = Facing.LEFT
                            CubePoint(newPos, newFace, newFacing)
                        }
                        Facing.LEFT -> {
                            val newPos = Point(faceSize - 1, pos.y)
                            val newFace = CubeFace.FRONT
                            val newFacing = Facing.LEFT
                            CubePoint(newPos, newFace, newFacing)
                        }
                        Facing.UP -> {
                            val newPos = Point(pos.x, faceSize - 1)
                            val newFace = CubeFace.UP
                            val newFacing = Facing.UP
                            CubePoint(newPos, newFace, newFacing)
                        }
                    }
                }
                CubeFace.DOWN -> {
                    when (facing) {
                        Facing.RIGHT -> {
                            val newPos = Point(pos.y, faceSize - 1)
                            val newFace = CubeFace.RIGHT
                            val newFacing = Facing.UP
                            CubePoint(newPos, newFace, newFacing)
                        }
                        Facing.DOWN -> {
                            val newPos = Point(pos.x, 0)
                            val newFace = CubeFace.BACK
                            val newFacing = Facing.DOWN
                            CubePoint(newPos, newFace, newFacing)
                        }
                        Facing.LEFT -> {
                            val newPos = Point(pos.y, 0)
                            val newFace = CubeFace.LEFT
                            val newFacing = Facing.DOWN
                            CubePoint(newPos, newFace, newFacing)
                        }
                        Facing.UP -> {
                            val newPos = Point(pos.x, faceSize - 1)
                            val newFace = CubeFace.FRONT
                            val newFacing = Facing.UP
                            CubePoint(newPos, newFace, newFacing)
                        }
                    }
                }
                CubeFace.LEFT -> {
                    when (facing) {
                        Facing.RIGHT -> {
                            val newPos = Point(0, pos.y)
                            val newFace = CubeFace.BACK
                            val newFacing = Facing.RIGHT
                            CubePoint(newPos, newFace, newFacing)
                        }
                        Facing.DOWN -> {
                            val newPos = Point(pos.x, 0)
                            val newFace = CubeFace.UP
                            val newFacing = Facing.DOWN
                            CubePoint(newPos, newFace, newFacing)
                        }
                        Facing.LEFT -> {
                            val newPos = Point(0, faceSize - pos.y - 1)
                            val newFace = CubeFace.FRONT
                            val newFacing = Facing.RIGHT
                            CubePoint(newPos, newFace, newFacing)
                        }
                        Facing.UP -> {
                            val newPos = Point(0, pos.x)
                            val newFace = CubeFace.DOWN
                            val newFacing = Facing.RIGHT
                            CubePoint(newPos, newFace, newFacing)
                        }
                    }
                }
                CubeFace.UP -> {
                    when (facing) {
                        Facing.RIGHT -> {
                            val newPos = Point(pos.y, faceSize - 1)
                            val newFace = CubeFace.BACK
                            val newFacing = Facing.UP
                            CubePoint(newPos, newFace, newFacing)
                        }
                        Facing.DOWN -> {
                            val newPos = Point(pos.x, 0)
                            val newFace = CubeFace.RIGHT
                            val newFacing = Facing.DOWN
                            CubePoint(newPos, newFace, newFacing)
                        }
                        Facing.LEFT -> {
                            val newPos = Point(pos.y, 0)
                            val newFace = CubeFace.FRONT
                            val newFacing = Facing.DOWN
                            CubePoint(newPos, newFace, newFacing)
                        }
                        Facing.UP -> {
                            val newPos = Point(pos.x, faceSize - 1)
                            val newFace = CubeFace.LEFT
                            val newFacing = Facing.UP
                            CubePoint(newPos, newFace, newFacing)
                        }
                    }
                }
                CubeFace.BACK -> {
                    when (facing) {
                        Facing.RIGHT -> {
                            val newPos = Point(faceSize - 1, faceSize - pos.y - 1)
                            val newFace = CubeFace.RIGHT
                            val newFacing = Facing.LEFT
                            CubePoint(newPos, newFace, newFacing)
                        }
                        Facing.DOWN -> {
                            val newPos = Point(faceSize - 1, pos.x)
                            val newFace = CubeFace.UP
                            val newFacing = Facing.LEFT
                            CubePoint(newPos, newFace, newFacing)
                        }
                        Facing.LEFT -> {
                            val newPos = Point(faceSize - 1, pos.y)
                            val newFace = CubeFace.LEFT
                            val newFacing = Facing.LEFT
                            CubePoint(newPos, newFace, newFacing)
                        }
                        Facing.UP -> {
                            val newPos = Point(pos.x, faceSize - 1)
                            val newFace = CubeFace.DOWN
                            val newFacing = Facing.UP
                            CubePoint(newPos, newFace, newFacing)
                        }
                    }
                }
            }
        }

        fun turnLeft(): CubePoint {
            return copy(facing = Facing.values()[Math.floorMod(facing.ordinal - 1, 4)])
        }

        fun turnRight(): CubePoint {
            return copy(facing = Facing.values()[Math.floorMod(facing.ordinal + 1, 4)])
        }
    }
}
