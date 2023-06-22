package kiss.gossr.tests

import kiss.gossr.CommaSpaceMoneyFormat
import kiss.gossr.DateTimeFormatEurope
import kiss.gossr.DateTimeFormatUSA
import kiss.gossr.DotCommaMoneyFormat
import kiss.gossr.GossRenderer
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicLong
import kotlin.test.Test
import kotlin.test.assertEquals

class Tests {
    @Test
    fun basicTest() {
        val now = LocalDate.now()
        val out = StringBuilder()
        val renderer = object : GossRenderer() {}
        GossRenderer.use(out) {
            renderer.apply {
                DIV("testClass") {
                    classes("oneMoreClass")
                    +"Hello"
                    +formatMoney2(100.0)
                    +formatDate(now)
                }
            }
        }

        assertEquals(
            """<DIV class="testClass oneMoreClass">Hello100.00${now}</DIV>""",
            out.toString().trim()
        )
    }

    fun String?.replaceNbsps(): String? = this?.replace(' ', ' ')?.replace(' ', ' ')

    @Test
    fun testDotCommaMoneyFormat() {
        assertEquals("1,000,000", DotCommaMoneyFormat.formatMoney(1_000_000))
        assertEquals("1,000,000", DotCommaMoneyFormat.formatMoney(AtomicLong(1_000_000)))
        assertEquals("-1,000,000", DotCommaMoneyFormat.formatMoney((-1_000_000).toBigDecimal()))
        assertEquals("1,000,000", DotCommaMoneyFormat.formatMoney(1_000_000.00))
        assertEquals("1,000,000.12", DotCommaMoneyFormat.formatMoney(1_000_000.12))
        assertEquals("0", DotCommaMoneyFormat.formatMoney(-0.0001))
        assertEquals("1000000.00", DotCommaMoneyFormat.formatMoney2(1_000_000))
        assertEquals("1000000.00", DotCommaMoneyFormat.formatMoney2(1_000_000.00))
        assertEquals("0.00", DotCommaMoneyFormat.formatMoney2(-0.0001))
        assertEquals("1234.56789", DotCommaMoneyFormat.formatDouble(1234.56789, 8, true))
    }

    @Test
    fun testCommaSpaceMoneyFormat() {
        assertEquals("1 000 000", CommaSpaceMoneyFormat.formatMoney(1_000_000).replaceNbsps())
        assertEquals("1 000 000", CommaSpaceMoneyFormat.formatMoney(AtomicLong(1_000_000)).replaceNbsps())
        assertEquals("-1 000 000", CommaSpaceMoneyFormat.formatMoney((-1_000_000).toBigDecimal()).replaceNbsps())
        assertEquals("1 000 000", CommaSpaceMoneyFormat.formatMoney(1_000_000.00).replaceNbsps())
        assertEquals("1 000 000,12", CommaSpaceMoneyFormat.formatMoney(1_000_000.12).replaceNbsps())
        assertEquals("0", CommaSpaceMoneyFormat.formatMoney(-0.0001))
        assertEquals("1000000,00", CommaSpaceMoneyFormat.formatMoney2(1_000_000))
        assertEquals("1000000,00", CommaSpaceMoneyFormat.formatMoney2(1_000_000.00))
        assertEquals("0,00", CommaSpaceMoneyFormat.formatMoney2(-0.0001))
        assertEquals("1234,56789", CommaSpaceMoneyFormat.formatDouble(1234.56789, 8, true))
    }

    @Test
    fun testDateTimeFormatEurope() {
        val dateTime = LocalDateTime.from(
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").parse("2020-01-02T22:33:44.555")
        )
        val date = dateTime.toLocalDate()

        assertEquals("2020-01-02 22:33", DateTimeFormatEurope.formatDateTime(dateTime).replaceNbsps())
        assertEquals("2020-01-02 22:33:44", DateTimeFormatEurope.formatDateTimeSec(dateTime).replaceNbsps())
        assertEquals("2020-01-02 22:33:44.555", DateTimeFormatEurope.formatDateTimeMillis(dateTime).replaceNbsps())

        assertEquals("2020-01-02", DateTimeFormatEurope.formatDate(date))

        assertEquals("22:33", DateTimeFormatEurope.formatTime(dateTime))
        assertEquals("22:33:44", DateTimeFormatEurope.formatTimeSec(dateTime))

    }

    @Test
    fun testDateTimeFormatUSA() {
        val dateTime = LocalDateTime.from(
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").parse("2020-01-02T22:33:44.555")
        )
        val date = dateTime.toLocalDate()

        assertEquals("01/02/2020 10:33pm", DateTimeFormatUSA.formatDateTime(dateTime).replaceNbsps())
        assertEquals("01/02/2020 10:33:44pm", DateTimeFormatUSA.formatDateTimeSec(dateTime).replaceNbsps())
        assertEquals("01/02/2020 10:33:44pm.555", DateTimeFormatUSA.formatDateTimeMillis(dateTime).replaceNbsps())

        assertEquals("01/02/2020", DateTimeFormatUSA.formatDate(date))

        assertEquals("10:33pm", DateTimeFormatUSA.formatTime(dateTime))
        assertEquals("10:33:44pm", DateTimeFormatUSA.formatTimeSec(dateTime))

    }

}