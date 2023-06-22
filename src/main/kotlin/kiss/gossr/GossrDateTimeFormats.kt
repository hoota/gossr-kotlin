package kiss.gossr

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface GossrDateTimeFormatter {
    fun formatDateTime(t: LocalDateTime?): String?
    fun formatDateTimeSec(t: LocalDateTime?): String?
    fun formatDateTimeMillis(t: LocalDateTime?): String?

    fun formatDate(d: LocalDate?): String?

    fun formatTime(t: LocalDateTime?): String?
    fun formatTimeSec(t: LocalDateTime?): String?

    fun formatDate(d: LocalDateTime?): String? = formatDate(d?.toLocalDate())
}

abstract class GossrDateTimeFormats : GossrDateTimeFormatter {
    abstract val dateTimeFormat: DateTimeFormatter
    abstract val dateTimeFormatSec: DateTimeFormatter
    abstract val dateTimeFormatMillis: DateTimeFormatter
    abstract val dateFormat: DateTimeFormatter
    abstract val timeFormat: DateTimeFormatter
    abstract val timeFormatSec: DateTimeFormatter

    override fun formatDateTime(t: LocalDateTime?): String? = t?.let {
        dateTimeFormat.format(t)?.lowercase()
    }

    override fun formatDateTimeSec(t: LocalDateTime?): String? = t?.let {
        dateTimeFormatSec.format(t)?.lowercase()
    }

    override fun formatDateTimeMillis(t: LocalDateTime?): String? = t?.let {
        dateTimeFormatMillis.format(t)?.lowercase()
    }

    override fun formatDate(d: LocalDate?): String? = d?.let {
        dateFormat.format(d)
    }

    override fun formatTime(t: LocalDateTime?): String? = t?.let {
        timeFormat.format(t)?.lowercase()
    }

    override fun formatTimeSec(t: LocalDateTime?): String? = t?.let {
        timeFormatSec.format(t)?.lowercase()
    }
}

object DateTimeFormatEurope : GossrDateTimeFormats() {

    override val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    override val dateTimeFormatSec = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    override val dateTimeFormatMillis = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    override val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    override val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
    override val timeFormatSec = DateTimeFormatter.ofPattern("HH:mm:ss")
}

object DateTimeFormatUSA : GossrDateTimeFormats() {

    override val dateTimeFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mma")
    override val dateTimeFormatSec = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm:ssa")
    override val dateTimeFormatMillis = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm:ssa.SSS")
    override val dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    override val timeFormat = DateTimeFormatter.ofPattern("h:mma")
    override val timeFormatSec = DateTimeFormatter.ofPattern("h:mm:ssa")
}
