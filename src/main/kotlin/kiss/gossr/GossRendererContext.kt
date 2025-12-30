package kiss.gossr

data class GossRendererContext(
    val out: Appendable,
    val dateTimeFormats: GossrDateTimeFormatter,
    val moneyFormats: GossrMoneyFormatter,
    val throwOnAttributeNotInTag: Boolean = true,
    var newLineAfterTagClose: Boolean = true,
    var formFieldNamesCollectionEnabled: Boolean = false,
) {
    var namePrefix: String? = null
    var nameAsMapKey: Boolean = false
    var tagOpening = StringBuilder()
    val tagClasses = ArrayList<String>()
    val formFieldNames = HashSet<String>()
}