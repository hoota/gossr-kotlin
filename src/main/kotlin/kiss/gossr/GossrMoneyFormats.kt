package kiss.gossr

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.roundToLong

interface GossrMoneyFormatter {
    fun formatMoney(n: Number?): String?
    fun formatMoney2(n: Number?): String?
    fun formatDouble(n: Number?, digits: Int, trimZeros: Boolean = false): String?
}

abstract class GossrMoneyFormats : GossrMoneyFormatter {
    abstract val separator: Char
    abstract val format: DecimalFormat
    abstract val format0: DecimalFormat
    abstract val trailingZerosRegex: Regex

    private fun isInteger(n: Number): Boolean =
        n !is BigDecimal && n !is Double && n !is Float

    override fun formatMoney(n: Number?): String? = n?.let {
        if(isInteger(n)) {
            format.format(n)
        } else if((n.toDouble() * 100).roundToLong() % 100 == 0L) {
            // to avoid "-0.00" we have to compare with 0.01
            format.format(if(n.toDouble().absoluteValue >= ONE_CENT) n else 0.0)
        } else {
            format0.format(if(n.toDouble().absoluteValue >= ONE_CENT) n else 0.0)
        }
    }

    override fun formatMoney2(n: Number?): String? = n?.let {
        if(isInteger(n)) {
            "$n${separator}00"
        } else {
            // to avoid "-0.00" we have to compare with 0.01
            "%.02f".format(if(n.toDouble().absoluteValue >= ONE_CENT) n else 0.0)
                .replace('.', separator)
        }
    }

    override fun formatDouble(n: Number?, digits: Int, trimZeros: Boolean): String? = n?.let {
        if(isInteger(n)) {
            n.toString()
        } else {
            val s = "%.${digits}f".format(n).replace('.', separator)
            if(!trimZeros) s else {
                s.replace(trailingZerosRegex, "$1")
            }
        }
    }

    companion object {
        const val ONE_CENT = 0.01
    }
}

object DotCommaMoneyFormat : GossrMoneyFormats() {

    override val separator = '.'
    override val format = DecimalFormat("#,##0.##", DecimalFormatSymbols(Locale.US))
    override val format0 = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.US))
    override val trailingZerosRegex = Regex("(\\.[0-9]+?)0*$")

}

object CommaSpaceMoneyFormat : GossrMoneyFormats() {

    override val separator = ','
    override val format = DecimalFormat("#,##0.##", DecimalFormatSymbols(Locale.FRANCE))
    override val format0 = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.FRANCE))
    override val trailingZerosRegex = Regex("(,[0-9]+?)0*$")
}
