package d11

import LongCodeProgram
import readInputCommaSeparatedFileAsLong
import kotlin.math.abs

enum class Color(val value: Int, val symbol:Char) {
    BLACK(0, ' '), WHITE(1, '▓'), UNKNOWN(-1, '?');

    companion object {
        fun from(value: Int): Color = values().find { it.value == value } ?: UNKNOWN
    }
}

enum class Rotation(val orientation: Int) {
    ROTATE_LEFT(0), ROTATE_RIGHT(1), UNKNOWN(-1);
    companion object {
        fun from(value: Int): Rotation = values().find { it.orientation == value } ?: UNKNOWN
    }
}

enum class Direction() {
    UP, DOWN, LEFT, RIGHT
}

data class Position(val x: Int, val y: Int){
    override fun equals(other: Any?): Boolean {
        return other is Position && other.x == this.x && other.y == this.y
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }
}

data class PositionAndDirection(val position: Position, val direction: Direction)

data class Panel(val position: Position, var color: Color)

class PartOneAndTwo(val input: LongArray) {

    private val panels = mutableListOf<Panel>()
    private var panelIndex = 0
    private var finished = false
    private val program = LongCodeProgram(input)
    private var currentDirection = Direction.UP

    private fun moveRobot(p: Position, r: Rotation, d: Direction): PositionAndDirection {
        return when {
            d == Direction.UP && r == Rotation.ROTATE_LEFT -> PositionAndDirection(Position(p.x - 1, p.y), Direction.LEFT)
            d == Direction.UP && r == Rotation.ROTATE_RIGHT -> PositionAndDirection(Position(p.x + 1, p.y), Direction.RIGHT)
            d == Direction.DOWN && r == Rotation.ROTATE_LEFT -> PositionAndDirection(Position(p.x + 1, p.y), Direction.RIGHT)
            d == Direction.DOWN && r == Rotation.ROTATE_RIGHT -> PositionAndDirection(Position(p.x - 1, p.y), Direction.LEFT)
            d == Direction.RIGHT && r == Rotation.ROTATE_LEFT -> PositionAndDirection(Position(p.x, p.y - 1), Direction.UP)
            d == Direction.RIGHT && r == Rotation.ROTATE_RIGHT -> PositionAndDirection(Position(p.x, p.y + 1), Direction.DOWN)
            d == Direction.LEFT && r == Rotation.ROTATE_LEFT -> PositionAndDirection(Position(p.x, p.y + 1), Direction.DOWN)
            d == Direction.LEFT && r == Rotation.ROTATE_RIGHT -> PositionAndDirection(Position(p.x, p.y - 1), Direction.UP)
            else -> throw RuntimeException("Pas possible")
        }
    }

    private fun addPanel(position: Position, color: Color = Color.BLACK): Panel{
        val newPanel = Panel(position, color)
        panels.add(newPanel)
        return newPanel
    }

    private fun drawRegistrationId(){
        val topLeft = panels.fold(Position(0,0)) {
            position, panel -> Position(
                if(position.x < panel.position.x) position.x else panel.position.x,
                if(position.y < panel.position.y) position.y else panel.position.y
            )
        }
        val bottomRight = panels.fold(Position(0,0)) {
            position, panel -> Position(
                if(position.x > panel.position.x) position.x else panel.position.x,
                if(position.y > panel.position.y) position.y else panel.position.y
        )
        }
        val drawingWidth = bottomRight.x - topLeft.x;
        val drawingHeight = bottomRight.y - topLeft.y
        val drawing = Array(drawingHeight + 1) {
            Array(drawingWidth + 1) {
                Color.BLACK.symbol
            }
        }
        for(panel in panels){
            drawing[panel.position.y + abs(topLeft.y)][panel.position.x + abs(topLeft.x)] = panel.color.symbol
        }
        for(line in drawing){
            println(line.toCharArray().concatToString())
        }
    }

    fun run(firstPanelColor: Color) {
        program.debug = false
        program.clearOutputAfterRun = true
        addPanel(Position(0, 0), firstPanelColor)
        while (!finished) {
            //println(panels.toTypedArray().contentToString())
            val currrentPanel = panels[panelIndex]
            program.addInput(currrentPanel.color.value.toLong())
            val programOutput = program.run()
            if(!program.endReached){
                currrentPanel.color = Color.from(programOutput.output[0].toInt())
                val rotation = Rotation.from(programOutput.output[1].toInt())
                val nextPositionAndDirection = moveRobot(currrentPanel.position, rotation, currentDirection)
                //println("Output=$programOutput, color=${currrentPanel.color.name}, rotation=$rotation, direction=$currentDirection, nextPosition=$nextPositionAndDirection")
                panelIndex = panels.indexOfFirst { panel -> panel.position == nextPositionAndDirection.position }
                if (panelIndex == -1) {
                    addPanel(nextPositionAndDirection.position)
                    panelIndex = panels.size - 1
                }
                currentDirection = nextPositionAndDirection.direction
            } else {
                finished = true
            }
        }
        println("${panels.size} panels painted at least once")
        drawRegistrationId()
    }

}

fun main() {
    val input = readInputCommaSeparatedFileAsLong("d11/input.txt")
    PartOneAndTwo(input).run(Color.BLACK)
    PartOneAndTwo(input).run(Color.WHITE)
}