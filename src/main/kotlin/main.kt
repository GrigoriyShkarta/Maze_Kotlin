fun main() {
    fun printHello() {
        print("""
            |||||| || ||      || ||||||      ||||||   ||        ||||    ||        ||    ||  ||        ||         |||||||    ||    ||      ||  ||||||
              ||      ||||  |||| ||            ||   ||  ||      ||  ||  ||      ||  ||  ||  ||      ||  ||      ||        ||  ||  ||||  ||||  ||
              ||   || ||  ||  || ||||||        ||   ||  ||      ||||    ||      ||||||    ||        ||||||      |||||||   ||||||  ||  ||  ||  ||||||
              ||   || ||      || ||            ||   ||  ||      ||      ||      ||  ||    ||        ||  ||      ||    ||  ||  ||  ||      ||  ||
              ||   || ||      || ||||||        ||     ||        ||      ||||||  ||  ||    ||        ||  ||       ||||||   ||  ||  ||      ||  ||||||
            """)

    }
    printHello()
    println()
    print("ENTER WIDTH MAZE: ")
    val width = readlnOrNull()
    print("ENTER HEIGHT MAZE: ")
    val height = readlnOrNull()
    val maze = Maze(width!!.toInt(), height!!.toInt())
    maze.generate()
    maze.printMaze()

    while (true) {
        println("Enter 'w' to move up, 's' to move down, 'a' to move left, 'd' to move right, or 'q' to quit:")

        when (readlnOrNull()) {
            "w" -> {
                maze.move(-1, 0)
            }
            "s" -> {
                maze.move(1, 0)
            }
            "a" -> {
                maze.move(0, -1)
            }
            "d" -> {
                maze.move(0, 1)
            }
            "cheat" -> {
                maze.findShortestPath()
                maze.printMaze()
                break
            }
            "q" -> break
            else -> println("Invalid input. Please enter 'w', 's', 'a', 'd', or 'q'.")
        }

        maze.printMaze()
    }
}
