class Maze(
    private val x: Int,
    private val y: Int
) {
    private val maze = Array(x) { IntArray(y) }
    private enum class Direction(val bit: Int, val dx: Int, val dy: Int) {
        N(1, 0, -1), S(2, 0, 1), E(4, 1, 0),W(8, -1, 0);
        var opposite: Direction? = null
        companion object {
            init {
                N.opposite = S
                S.opposite = N
                E.opposite = W
                W.opposite = E
            }
        }
    }

    init {
        maze[0][0] = 3 // Start point
    }

    fun generate(cx: Int = 0, cy: Int = 0) {
        Direction.entries.toTypedArray().shuffle().forEach {
            val nx = cx + it.dx
            val ny = cy + it.dy
            if (between(nx, x) && between(ny, y) && maze[nx][ny] == 0) {
                maze[cx][cy] = maze[cx][cy] or it.bit
                maze[nx][ny] = maze[nx][ny] or it.opposite!!.bit
                generate(nx, ny)
            }
        }
    }

    fun display() {
        for (i in 0..<y) {
            for (j in 0..<x)
                if (maze[j][i] and 1 == 0) {
                    print("+---")
                } else {
                    print("+   ")
                }
//                print(maze[j][i])
            println('+')

            for (j in 0..<x)
//                print(maze[j][i])
                print(if (maze[j][i] and 8 == 0) "|   " else "    ")
            println('|')
        }

        for (j in 0..<x) {
            if (j == x - 1) {
                print("+   ")
            } else {
                print("+---")
            }
        }
        println('+')
    }

    private inline fun <reified T> Array<T>.shuffle(): Array<T> {
        val list = toMutableList()
        list.shuffle()
        return list.toTypedArray()
    }

    private fun between(v: Int, upper: Int) = v in 0..<upper
}