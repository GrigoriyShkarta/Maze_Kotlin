

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
    maze.play()
}
