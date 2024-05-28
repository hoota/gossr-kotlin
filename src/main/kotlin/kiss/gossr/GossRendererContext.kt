package kiss.gossr

data class GossRendererContext(
    val out: Appendable,
    val dateTimeFormats: GossrDateTimeFormatter,
    val moneyFormats: GossrMoneyFormatter,
    val throwOnAttributeNotInTag: Boolean = true
) {
    var namePrefix: String? = null
    var tagOpening = StringBuilder()
    val tagClasses = ArrayList<String>()
}