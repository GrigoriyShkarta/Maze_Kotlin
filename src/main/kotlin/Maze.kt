import java.util.*
import kotlin.random.Random

data class Cell(val row: Int, val col: Int)

class Maze (
    private  val width: Int,
    private val height: Int
) {
    private companion object {
        private const val NOTHING = 0
        private const val ROAD = 1
        private const val WALL = 2
        private const val USER_PATH = 3
        private const val USER_LOCATION = 5
        private const val PROBABILITY = 0.5
    }

    private val maze = Array(height) { Array(width) { NOTHING } }
    private var currentCell = Cell(0, 0)
    private var prevCell: Cell? = null
    private var neighborsWithDirections = getNeighborsWithDirections(currentCell.row, currentCell.col, false)
    private var checkZero = false
    private var currentUserCell = Cell(0, 0)
    private var score = 1

    private fun generate() {
        build(0, 0, ROAD)

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
                    val isHorizontalNeighborRoad = horizontalNeighbor?.let { (row, col) -> maze[row][col] == ROAD } ?: false
                    val isVerticalNeighborRoad = verticalNeighbor?.let { (row, col) -> maze[row][col] == ROAD } ?: false

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
                        if (maze[i][j] == ROAD) {
                            waysList.add(Pair(i, j))
                        }
                    }
                }

                val filteredWays = waysList.filter { (i, j) ->
                    listOf(-1, 1).any { di ->
                        val ni = i + di
                        ni in 0..<height && maze[ni][j] == NOTHING
                    } || listOf(-1, 1).any { dj ->
                        val nj = j + dj
                        nj in 0..<width && maze[i][nj] == NOTHING
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
        build(0, 0, USER_LOCATION)
        build(height - 1, width - 1, ROAD)
    }

    private fun checkZeroCell() {
        val emptyCells = mutableListOf<Cell>()
        checkZero = true

        for (i in maze.indices) {
            for (j in maze[i].indices) {
                if (maze[i][j] == NOTHING) {
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

    private fun isValidCell(row: Int, col: Int): Boolean {
        return row in 0..<height && col in 0..<width
    }

    private fun getNeighborsWithDirections(x: Int, y: Int, includeWalls: Boolean): Map<String, Cell> {
        val neighbors = mutableMapOf<String, Cell>()
        if (x - 1 >= 0 && (includeWalls || maze[x - 1][y] == NOTHING)) neighbors["Up"] = Cell(x - 1, y)
        if (x + 1 < maze.size && (includeWalls || maze[x + 1][y] == NOTHING)) neighbors["Down"] = Cell(x + 1, y)
        if (y - 1 >= 0 && (includeWalls || maze[x][y - 1] == NOTHING)) neighbors["Left"] = Cell(x, y - 1)
        if (y + 1 < maze[0].size && (includeWalls || maze[x][y + 1] == NOTHING)) neighbors["Right"] = Cell(x, y + 1)
        return neighbors
    }

    private fun printMaze () {
        for (i in -1..<height + 1) {
            for (j in -1..<width + 1) {
                val cellSymbol = when {
                    i == 0 && j == -1 -> "\u001B[31m →\u001B[0m" // Start point
                    i == height && j == width - 1 -> "\u001B[31m ↓\u001B[0m" // Finish point
                    i in 0..<height && j in 0..<width -> when (maze[i][j]) {
                        ROAD -> "  "
                        USER_PATH -> "\u001B[33m *\u001B[0m"
                        USER_LOCATION -> "\u001B[34m *\u001B[0m"
                        else -> "\u001B[35m░░\u001B[0m"
                    }
                    else -> "\u001B[35m░░\u001B[0m"
                }
                print(cellSymbol)
            }
            println()
        }
    }

    private fun findShortestPath(): List<Cell>? {
        for (i in 0..<height) {
            for (j in 0..<width) {
                if (maze[i][j] == USER_PATH || maze[i][j] == USER_LOCATION) {
                    build(i, j, ROAD)
                }
            }
        }

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
                    build(cell.row, cell.col, USER_PATH)
                }
                score = path.size
                build(height - 1, width - 1, USER_LOCATION)
                println()
                print("WELL DONE!!! YOU FINISHED!!! YOUR SCORE ${score + 1}")
                println()

                return path + current
            }

            val neighbors = listOf(Cell(1, 0), Cell(-1, 0), Cell(0, 1), Cell(0, -1))
            for (neighbor in neighbors) {
                val newRow = current.row + neighbor.row
                val newCol = current.col + neighbor.col
                val newCell = Cell(newRow, newCol)

                if (newRow in 0..<rows && newCol in 0..<cols
                    && (maze[newRow][newCol] == ROAD || maze[newRow][newCol] == USER_PATH || maze[newRow][newCol] == USER_LOCATION)
                    && newCell !in visited)
                {
                    queue.add(Pair(newCell, path + current))
                    visited.add(newCell)
                }
            }
        }

        return null
    }

    private fun move(x: Int, y: Int) {
        if (isValidCell(currentUserCell.row + x, currentUserCell.col + y)) {
            build(currentUserCell.row, currentUserCell.col, USER_PATH)
            if (maze[currentUserCell.row + x][currentUserCell.col + y] == ROAD ||
                maze[currentUserCell.row + x][currentUserCell.col + y] == USER_PATH)
            {
                val newRow = currentUserCell.row + x
                val newCol = currentUserCell.col + y
                currentUserCell = Cell(newRow, newCol)
                build(currentUserCell.row, currentUserCell.col, USER_LOCATION)
                score++
                println()
                print("SCORE $score")
                println()
            }
        }

    }

    fun play() {
        val endMaze = Cell(height - 1, width - 1)
        generate()
        printMaze()

        while (true) {
            if (currentUserCell == endMaze) {
                println()
                print("WELL DONE!!! YOU FINISHED!!! YOUR SCORE $score")
                println()
                break
            }

            println("Enter 'w' to move up, 's' to move down, 'a' to move left, 'd' to move right, or 'q' to quit:")

            when (readlnOrNull()) {
                "w" -> move(-1, 0)
                "s" -> move(1, 0)
                "a" -> move(0, -1)
                "d" -> move(0, 1)
                "cheat" -> {
                    findShortestPath()
                    printMaze()
                    break
                }
                "q" -> break
                else -> println("Invalid input. Please enter 'w', 's', 'a', 'd', or 'q'.")
            }

            printMaze()
        }
    }
}



