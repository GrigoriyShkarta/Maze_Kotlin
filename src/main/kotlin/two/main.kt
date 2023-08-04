package two

fun main() {
    val x = 15 // Задайте значение размера x
    val y = 35 // Задайте значение размера y

    // Создание двумерного массива
    val array = Array(x) { Array(y) { " " } }

    // Заполнение массива данными (для примера, заполним его числами от 1 до x*y)
    var count = 1
    for (i in 0..<x) {
        for (j in 0..<y) {
            array[i][j] = count.toString()
            count++
        }
    }

    // Укажите координаты точки входа и точки выхода
    val entryPointX = 0 // Координата x точки входа (на левой стенке)
    val entryPointY = 0 // Координата y точки входа (на верхней стенке)

    val exitPointX = x - 1 // Координата x точки выхода (на правой стенке)
    val exitPointY = y - 1 // Координата y точки выхода (на верхней стенке)

    // Печать массива с точками входа и выхода
    printWallWithEntryExit(array, entryPointX, entryPointY, exitPointX, exitPointY)
}

fun printWallWithEntryExit(array: Array<Array<String>>, entryX: Int, entryY: Int, exitX: Int, exitY: Int) {
    val x = array.size
    val y = array[0].size

    // Верхняя стенка
    repeat(y + 2) { print("+---") }
    println()

    // Печать данных с боковыми стенками
    for (i in 0..<x) {
        print("////")
        for (j in 0..<y) {
            if (i == entryX && j == entryY) {
                print("  A ") // Обозначение точки входа
            } else if (i == exitX && j == exitY) {
                print(" B  ") // Обозначение точки выхода
            } else {
//                print("    ")
//                print("${array[i][j]} ")
            }
        }
        println("////")
    }

    // Нижняя стенка
    repeat(y + 2) { print("+---") }
    println()
}

fun generate (array: Array<Array<String>>) {
    val x = array.size
    val y = array[0].size
    var currentX = 0
    var currentY = 0
    for (i in 0..<x) {
        for (j in 0..<y) {
//            if
        }
    }
}
