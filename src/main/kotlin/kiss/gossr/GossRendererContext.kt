package kiss.gossr

data class GossRendererContext(
    val out: Appendable,
    val dateTimeFormats: GossrDateTimeFormatter,
    val moneyFormats: GossrMoneyFormatter,
    val throwOnAttributeNotInTag: Boolean = true
) {
    val namePrefixes = ArrayList<String>()
    var tagOpening = StringBuilder()
    val tagClasses = ArrayList<String>()
}