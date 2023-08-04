package two

import kotlin.random.Random

class Maze(
    private val x: Int,
    private val y: Int
) {
    private val maze = Array(x) { IntArray(y) }

    init {
        maze[0][0] = 2
        removeRandomWallsAndUnderlines()
        printMaze(x, y)
    }

    private fun printMaze(x: Int, y: Int) {
        for (i in 0..<x) {
            // Верхняя граница клетки
            for (j in 0..<y) {
                print("+")
                if (maze[i][j] == 0) {
                    print("---")
                } else if (maze[i][j] == 2) { // Точка входа
                    print("   ")
                } else {
                    print("   ")
                }
            }
            println("+")

            // Вертикальные стены
            for (j in 0..<y) {
                if (maze[i][j] == -1) {
                    print("   ")
                } else if (maze[i][j] == 0) {
                    print("|   ")
                } else if (maze[i][j] == 2) {
                    print("|   ")
                }
                else {
                    print("    ")
                }
            }
            println("|")
        }

        for (j in 0..<y) {
            if (maze[x - 1][j] != -1) {
                if (j == y - 1) {
                    print("+   ")
                } else {
                    print("+---")
                }
            }
        }
        println("+")
    }

    private fun removeRandomWallsAndUnderlines() {
        // Step 1: Select the initial row
        for (i in 2 until x) {
            // Step 2: Select the initial cell in the row and make it the current cell
            var currentCell = Random.nextInt(1, y)

            // Step 3: Initialize an empty set
            val visited = mutableSetOf<Pair<Int, Int>>()

            // Step 4: Add the current cell to the set
            visited.add(i to currentCell)

            // Step 5: Decide whether to move right
            while (Random.nextBoolean()) {
                // Step 6: If moving right, go to the new cell and make it the current cell
                currentCell++
                if (currentCell < y) {
                    visited.add(i to currentCell)
                    maze[i][currentCell] = 0 // Remove horizontal wall
                } else {
                    break
                }
            }

            // Step 7: If not moving right, select a random cell from the set and move upward
            if (visited.isNotEmpty()) {
                val randomCell = visited.random()
                var currentRow = randomCell.first
                currentCell = randomCell.second

                // Step 8: Check if it's the first row
                if (currentRow == 0) {
                    // Only remove the horizontal wall for the left corner (entry point)
                    maze[0][currentCell] = 0
                } else {
                    while (currentRow > 0) {
                        currentRow--
                        visited.add(currentRow to currentCell)
                        maze[currentRow][currentCell] = 1 // Remove vertical wall
                    }
                }
            }
        }
    }


}