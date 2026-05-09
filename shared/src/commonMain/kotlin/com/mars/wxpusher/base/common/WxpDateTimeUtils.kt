package com.mars.wxpusher.base.common

expect fun Double.toDateTimeString(): String

expect fun Long.toDate(): String

fun Long.toDateTimeString(): String {
    return this.toDouble().toDateTimeString()
}

object WxpDateTimeUtils {
    fun getRelativeDateTime(timeStamp: Double): String {
        val nowInSeconds = System.currentTimeMillis()
        val duration = kotlin.math.abs((nowInSeconds - timeStamp) / 1000).toInt()
        return when {
            duration < 60 -> "刚刚"
            duration < 3600 -> "${duration / 60}分钟前"
            duration < 86400 -> "${duration / 3600}小时前"
            duration < 604800 -> "${duration / 86400}天前"
            else -> {
                timeStamp.toDateTimeString()
            }
        }
    }

    fun formatDateTime(timeStamp: Long): String {
        return timeStamp.toDateTimeString()
    }

    fun getDate(): String {
        val nowInSeconds = System.currentTimeMillis()
        return nowInSeconds.toDate()
    }

    fun getDateTime(): String {
        val nowInSeconds = System.currentTimeMillis()
        return nowInSeconds.toDateTimeString()
    }

    fun getTimestamp(): Long = System.currentTimeMillis()

}
