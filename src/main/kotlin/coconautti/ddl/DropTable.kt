package coconautti.ddl

data class DropTable(private val name: String, private val force: Boolean = true) {
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("DROP TABLE")

        if (!force) {
            sb.append(" IF EXISTS")
        }

        sb.append(" $name")

        return sb.toString()
    }
}
