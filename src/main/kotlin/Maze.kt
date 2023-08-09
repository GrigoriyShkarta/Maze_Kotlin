import java.util.*
import kotlin.random.Random

class Maze (
    private  val width: Int,
    private val height: Int
) {
    private companion object {
        private const val ROAD = 1
        private const val WALL = 2
        private const val SHORT_PATH = 3
        private const val PROBABILITY = 0.6
    }

    private val maze = Array(height) { Array(width) { 0 } }
    private var currentCell = Cell(0, 0)
    private var prevCell: Cell? = null
    private var neighborsWithDirections = getNeighborsWithDirections(currentCell.row, currentCell.col, false)
    private var checkZero = false

    fun generate() {
        build(0, 0, ROAD)
        build(height - 1, width - 1, ROAD)

        while (neighborsWithDirections.isNotEmpty()) {
            prevCell = currentCell
            val randomDirection = neighborsWithDirections.keys.toList().random()
            val selectedCell = neighborsWithDirections[randomDirection]

            if (selectedCell != null) {
                currentCell = selectedCell

                val allNeighborsWithDirections = getNeighborsWithDirections(currentCell.row, currentCell.col, true)
                val checkNeighborsWithDirections = getNeighborsWithDirections(prevCell!!.row, prevCell!!.col, false)
                val emptyNeighbors = checkNeighborsWithDirections.size

                if (currentCell != Cell(0, 0) && Random.nextDouble() < PROBABILITY && emptyNeighbors > 1 && !checkZero) {
                    build(currentCell.row, currentCell.col, WALL)
                    currentCell = prevCell as Cell
                    neighborsWithDirections = getNeighborsWithDirections(currentCell.row, currentCell.col, false)
                } else{
                    val isHorizontalDirection = randomDirection == "Right" || randomDirection == "Left"
                    val horizontalNeighbor = if (isHorizontalDirection) allNeighborsWithDirections["Up"] else allNeighborsWithDirections["Left"]
                    val verticalNeighbor = if (isHorizontalDirection) allNeighborsWithDirections["Down"] else allNeighborsWithDirections["Right"]
                    val isHorizontalNeighborRoad = horizontalNeighbor?.let { (row, col) -> maze[row][col] == 1 } ?: false
                    val isVerticalNeighborRoad = verticalNeighbor?.let { (row, col) -> maze[row][col] == 1 } ?: false

                    if (isHorizontalNeighborRoad || isVerticalNeighborRoad || allNeighborsWithDirections.isEmpty()) {
                        build(currentCell.row, currentCell.col, WALL)
                        currentCell = prevCell as Cell
                    } else {
                        build(currentCell.row, currentCell.col, ROAD)
                    }
                    neighborsWithDirections = getNeighborsWithDirections(currentCell.row, currentCell.col, false)
                }
            }

            if (neighborsWithDirections.isEmpty()) {
                val waysList = mutableListOf<Pair<Int, Int>>()
                for (i in 0..<height) {
                    for (j in 0..<width) {
                        if (maze[i][j] == 1) {
                            waysList.add(Pair(i, j))
                        }
                    }
                }

                val filteredWays = waysList.filter { (i, j) ->
                    listOf(-1, 1).any { di ->
                        val ni = i + di
                        ni in 0..<height && maze[ni][j] == 0
                    } || listOf(-1, 1).any { dj ->
                        val nj = j + dj
                        nj in 0..<width && maze[i][nj] == 0
                    }
                }

                if (filteredWays.isNotEmpty()) {
                    val randomCell = filteredWays.random()
                    currentCell = Cell(randomCell.first, randomCell.second)
                    neighborsWithDirections = getNeighborsWithDirections(currentCell.row, currentCell.col, false)
                }
            }
        }
        checkZeroCell()
    }

    private fun checkZeroCell() {
        val emptyCells = mutableListOf<Cell>()
        checkZero = true

        for (i in maze.indices) {
            for (j in maze[i].indices) {
                if (maze[i][j] == 0) {
                    emptyCells.add(Cell(i, j))
                }
            }
        }

        if (emptyCells.isNotEmpty()) {
            val randomIndex = Random.nextInt(0, emptyCells.size)
            val randomCell = emptyCells[randomIndex]
            currentCell = randomCell
            build(currentCell.row, currentCell.col, ROAD)
            neighborsWithDirections = getNeighborsWithDirections(currentCell.row, currentCell.col, false)
            generate()
        } else {
            return
        }
    }

    private fun build(x: Int, y: Int, action: Int) {
        maze[x][y] = action
    }

    private fun getNeighborsWithDirections(x: Int, y: Int, includeWalls: Boolean): Map<String, Cell> {
        val neighbors = mutableMapOf<String, Cell>()
        if (x - 1 >= 0 && (includeWalls || maze[x - 1][y] == 0)) neighbors["Up"] = Cell(x - 1, y)
        if (x + 1 < maze.size && (includeWalls || maze[x + 1][y] == 0)) neighbors["Down"] = Cell(x + 1, y)
        if (y - 1 >= 0 && (includeWalls || maze[x][y - 1] == 0)) neighbors["Left"] = Cell(x, y - 1)
        if (y + 1 < maze[0].size && (includeWalls || maze[x][y + 1] == 0)) neighbors["Right"] = Cell(x, y + 1)
        return neighbors
    }

    fun printMaze () {
        for (i in -1..<height + 1) {
            for (j in -1..<width + 1) {
                val cellSymbol = when {
                    i == 0 && j == -1 -> " *" // Start point
                    i == height && j == width - 1 -> " *" // Finish point
                    i in 0..<height && j in 0..<width -> when (maze[i][j]) {
                        1 -> "  "
                        3 -> " *"
                        else -> "░░"
                    }
                    else -> "░░"
                }
                print(cellSymbol)
            }
            println()
        }
    }

    fun shortestPath(): List<Cell>? {
        val rows = maze.size
        val cols = maze[0].size

        val start = Cell(0, 0)
        val end = Cell(rows - 1, cols - 1)

        val queue: Queue<Pair<Cell, List<Cell>>> = LinkedList()
        queue.add(Pair(start, emptyList()))
        val visited = mutableSetOf(start)

        while (queue.isNotEmpty()) {
            val (current, path) = queue.poll()

            if (current == end) {
                for (cell in path + current) {
                    build(cell.row, cell.col, SHORT_PATH)
                }
                return path + current
            }

            val neighbors = listOf(Cell(1, 0), Cell(-1, 0), Cell(0, 1), Cell(0, -1))
            for (neighbor in neighbors) {
                val newRow = current.row + neighbor.row
                val newCol = current.col + neighbor.col
                val newCell = Cell(newRow, newCol)

                if (newRow in 0..<rows && newCol in 0..<cols && maze[newRow][newCol] == 1 && newCell !in visited) {
                    queue.add(Pair(newCell, path + current))
                    visited.add(newCell)
                }
            }
        }

        return null
    }

    fun printHello() {
        print("""
            |||||| () ||      || ||||||      ||||||   ||        ||||    ||        ||    ||  ||        ||         |||||||    ||    ||      ||  ||||||
              ||      ||||  |||| ||            ||   ||  ||      ||  ||  ||      ||  ||  ||  ||      ||  ||      ||        ||  ||  ||||  ||||  ||
              ||   || ||  ||  || ||||||        ||   ||  ||      ||||    ||      ||||||    ||        ||||||      |||||||   ||||||  ||  ||  ||  ||||||
              ||   || ||      || ||            ||   ||  ||      ||      ||      ||  ||    ||        ||  ||      ||    ||  ||  ||  ||      ||  ||
              ||   || ||      || ||||||        ||     ||        ||      ||||||  ||  ||    ||        ||  ||       ||||||   ||  ||  ||      ||  ||||||
            """)

    }
}

data class Cell(val row: Int, val col: Int)

