data class Position2D(var x:Long, var y: Long){

    fun withOffset(offset: Position2D): Position2D{
        return Position2D(x + offset.x, y + offset.y)
    }

    fun isAdjacent(other: Position2D): Boolean{
        return (other.y == y && (other.x == x - 1 || other.x == x + 1))
                || (other.x == x && (other.y == y - 1 || other.y == y + 1))
    }

    override fun equals(other: Any?): Boolean {
        return other is Position2D && other.x == x && other.y == y
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }
}
fun Position2D(x: Int, y: Int) = Position2D(x.toLong(), y.toLong())