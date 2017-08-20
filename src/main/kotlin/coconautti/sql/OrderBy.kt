package coconautti.sql

private enum class Direction(val value: String) {
    ASC("ASC"), DESC("DESC")
}

data class OrderBy(private val column: String) {
    private var direction: Direction? = null

    fun asc(): OrderBy {
        direction = Direction.ASC
        return this
    }

    fun desc(): OrderBy {
        direction = Direction.DESC
        return this
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("ORDER BY ")
        sb.append(column)

        direction?.let { dir ->
            sb.append(" $dir")
        }

        return sb.toString()
    }
}
