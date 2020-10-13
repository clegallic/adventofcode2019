enum class ParameterMode(val mode: Int) {
    UNKNOWN(-1), POSITION_MODE(0), IMMEDIATE_MODE(1), RELATIVE_MODE(2);
    companion object {
        fun from(i: Int): ParameterMode = values().find { it.mode == i } ?: UNKNOWN
    }
}

enum class OperationType(val type: Int) {
    UNKNOWN(-1), ADD(1), MULTIPLY(2), INPUT(3), OUTPUT(4), JUMP_IF_TRUE(5), JUMP_IF_FALSE(6), LESS_THAN(7), EQUALS(8), ADJUST_RELATIVE_BASE(9);
    companion object {
        fun from(i: Int): OperationType = values().find { it.type == i } ?: UNKNOWN
    }
}