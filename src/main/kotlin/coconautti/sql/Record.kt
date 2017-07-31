package coconautti.sql

abstract class Record {
    fun size(): Int {
        when (this) {
            is Record1 -> return 1
            is Record2 -> return 2
            is Record3 -> return 3
            is Record4 -> return 4
            is Record5 -> return 5
            is Record6 -> return 6
            is Record7 -> return 7
            else -> return -1
        }
    }
}

data class Record1(val first: Any?) : Record()
data class Record2(val first: Any?, val second: Any?) : Record()
data class Record3(val first: Any?, val second: Any?, val third: Any?) : Record()
data class Record4(val first: Any?, val second: Any?, val third: Any?, val fourth: Any?) : Record()
data class Record5(val first: Any?, val second: Any?, val third: Any?, val fourth: Any?, val fifth: Any?) : Record()
data class Record6(val first: Any?, val second: Any?, val third: Any?, val fourth: Any?, val fifth: Any?, val sixth: Any?) : Record()
data class Record7(val first: Any?, val second: Any?, val third: Any?, val fourth: Any?, val fifth: Any?, val sixth: Any?, val seventh: Any?) : Record()
