package kiss.gossr

import org.intellij.lang.annotations.Language
import java.net.URLEncoder
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

@Suppress("FunctionNaming", "TooManyFunctions", "MethodOverloading")
abstract class GossRenderer : GossrDateTimeFormatter, GossrMoneyFormatter {
    private var localContext: GossRendererContext? = null

    val context: GossRendererContext get() = localContext
        ?: threadLocalContext.get()?.also {
            localContext = it
        }
        ?: throw GossRendererException("GossRendererContext.threadLocal is empty")

    fun closeTagOpening() {
        val context = context
        if(context.tagOpening.isNotEmpty()) {
            context.out.append(context.tagOpening)
            if(context.tagClasses.isNotEmpty()) {
                context.out.append(" class=\"")
                context.tagClasses.forEachIndexed { index, c ->
                    if(index > 0) context.out.append(' ')
                    htmlEncode(c, context.out)
                }
                context.out.append('"')
            }
            context.tagClasses.clear()
            context.out.append('>')
            context.tagOpening.clear()
        }
    }

    /** general HTML element */
    inline fun EL(tag: String, noBody: Boolean = false, body: () -> Unit) {
        closeTagOpening()
        context.tagOpening.append('<').append(tag)

        try {
            body()
        } finally {

            if(noBody && context.tagOpening.isNotEmpty()) {
                context.tagOpening.append('/')
                closeTagOpening()
                context.out.append('\n')
            } else {
                closeTagOpening()
                context.out.append("</").append(tag).append(">\n")
            }
        }
    }

    /** shorthand for DIV align=center */
    inline fun CENTER(body: () -> Unit) = EL("CENTER", body = body)

    inline fun DIV(body: () -> Unit) = EL("DIV", body = body)
    inline fun DIV(classes: String, body: () -> Unit) = DIV {
        classes(classes)
        body()
    }

    /** small text style */
    inline fun SMALL(body: () -> Unit) = EL("SMALL", body = body)
    inline fun SMALL(classes: String, body: () -> Unit) = SMALL {
        classes(classes)
        body()
    }

    /** strike-through text */
    inline fun STRIKE(body: () -> Unit) = EL("STRIKE", body = body)
    inline fun STRIKE(classes: String, body: () -> Unit) = STRIKE {
        classes(classes)
        body()
    }

    /** strong emphasis */
    inline fun STRONG(body: () -> Unit) = EL("STRONG", body = body)
    inline fun STRONG(classes: String, body: () -> Unit) = STRONG {
        classes(classes)
        body()
    }

    /** subscript */
    inline fun SUB(body: () -> Unit) = EL("SUB", body = body)
    inline fun SUB(classes: String, body: () -> Unit) = SUB {
        classes(classes)
        body()
    }

    /** superscript */
    inline fun SUP(body: () -> Unit) = EL("SUP", body = body)
    inline fun SUP(classes: String, body: () -> Unit) = SUP {
        classes(classes)
        body()
    }

    inline fun SPAN(body: () -> Unit) = EL("SPAN", body = body)
    inline fun SPAN(classes: String, body: () -> Unit) = SPAN {
        classes(classes)
        body()
    }

    inline fun LI(body: () -> Unit) = EL("LI", body = body)
    inline fun LI(classes: String, body: () -> Unit) = LI {
        classes(classes)
        body()
    }

    inline fun OL(body: () -> Unit) = EL("OL", body = body)
    inline fun OL(classes: String, body: () -> Unit) = OL {
        classes(classes)
        body()
    }

    inline fun TABLE(body: () -> Unit) = EL("TABLE", body = body)
    inline fun TABLE(classes: String, body: () -> Unit) = TABLE {
        classes(classes)
        body()
    }

    inline fun TBODY(body: () -> Unit) = EL("TBODY", body = body)
    inline fun THEAD(body: () -> Unit) = EL("THEAD", body = body)
    inline fun TFOOT(body: () -> Unit) = EL("TFOOT", body = body)

    inline fun TH(body: () -> Unit) = EL("TH", body = body)
    inline fun TR(body: () -> Unit) = EL("TR", body = body)
    inline fun TD(body: () -> Unit) = EL("TD", body = body)

    inline fun PRE(body: () -> Unit) = EL("PRE", body = body)
    inline fun PRE(classes: String, body: () -> Unit) = PRE {
        classes(classes)
        body()
    }

    inline fun TT(body: () -> Unit) = EL("TT", body = body)
    inline fun TT(classes: String, body: () -> Unit) = TT {
        classes(classes)
        body()
    }

    /** anchor */
    inline fun A(body: () -> Unit) = EL("A", body = body)
    inline fun B(body: () -> Unit) = EL("B", body = body)

    inline fun P(body: () -> Unit) = EL("P", body = body)
    inline fun P(classes: String, body: () -> Unit) = P {
        classes(classes)
        body()
    }

    inline fun S(body: () -> Unit) = EL("S", body = body)
    inline fun I(body: () -> Unit) = EL("I", body = body)

    /** underlined text style */
    inline fun U(body: () -> Unit) = EL("U", body = body)

    /** short inline quotation */
    inline fun Q(body: () -> Unit) = EL("Q", body = body)

    /** unordered list */
    inline fun UL(body: () -> Unit) = EL("UL", body = body)
    inline fun UL(classes: String, body: () -> Unit) = UL {
        classes(classes)
        body()
    }

    inline fun H1(body: () -> Unit) = EL("H1", body = body)
    inline fun H2(body: () -> Unit) = EL("H2", body = body)
    inline fun H3(body: () -> Unit) = EL("H3", body = body)
    inline fun H4(body: () -> Unit) = EL("H4", body = body)
    inline fun H5(body: () -> Unit) = EL("H5", body = body)
    inline fun H6(body: () -> Unit) = EL("H6", body = body)
    inline fun NOBR(body: () -> Unit) = EL("NOBR", body = body)

    fun BR() = EL("BR", noBody = true) {}

    inline fun LABEL(body: () -> Unit) = EL("LABEL", body = body)
    inline fun LABEL(classes: String, body: () -> Unit) = LABEL {
        classes(classes)
        body()
    }

    inline fun SELECT(body: GossRendererCommonSelect.() -> Unit) = EL("SELECT") {
        GossRendererCommonSelect().body()
    }

    inline fun <T> SELECT(property: KProperty0<T?>, body: GossRendererTypedSelect<T>.() -> Unit) = EL("SELECT") {
        name(property)
        GossRendererTypedSelect(property.get()).body()
    }

    inline fun <E, C : Collection<E>> MULTISELECT(property: KProperty0<C?>, body: GossRendererTypedMultiSelect<E>.() -> Unit) = EL("SELECT") {
        name(property)
        multiple(true)
        GossRendererTypedMultiSelect(property.get()).body()
    }

    fun OPTION(empty: String?) = EL("OPTION") {
        value("")
        +empty
    }

    inline fun TEXTAREA(body: () -> Unit) = EL("TEXTAREA", body = body)
    inline fun HTML(body: () -> Unit) = EL("HTML", body = body)
    inline fun HEAD(body: () -> Unit) = EL("HEAD", body = body)
    inline fun BODY(body: () -> Unit) = EL("BODY", body = body)

    inline fun BUTTON(body: () -> Unit) = EL("BUTTON", body = body)
    inline fun BUTTON(classes: String, body: () -> Unit) = BUTTON {
        classes(classes)
        body()
    }

    inline fun LINK(body: () -> Unit) = EL("LINK", noBody = true, body = body)
    fun LINK(rel: String, href: String) = EL("LINK", noBody = true) {
        rel(rel)
        href(href)
    }

    fun SCRIPT(src: String) = EL("SCRIPT") {
        src(src)
    }

    fun SCRIPT(@Language("js") code: String, type: String? = null) = EL("SCRIPT") {
        if(type != null) type(type)
        noEscape(code.replace("</script>", "</scr\"+\"ipt>", ignoreCase = true))
    }

    fun STYLE(@Language("css") style: String) = EL("STYLE") {
        noEscape(style)
    }

    fun noEscape(@Language("html") html: String?) {
        closeTagOpening()
        context.out.append(html)
    }

    inline fun INPUT(body: () -> Unit) = EL("INPUT", noBody = true, body = body)

    fun SUBMIT(classes: String? = null, text: String? = null) = INPUT {
        typeSubmit(classes)
        value(text)
    }

    inline fun SUBMIT(classes: String? = null, text: String? = null, body: () -> Unit) = INPUT {
        typeSubmit(classes)
        value(text)
        body()
    }

    fun SUBMIT(classes: String?, property: KProperty<String?>, text: String?) = INPUT {
        typeSubmit(classes)
        nameValue(property, text)
    }

    fun CHECKBOX(
        classes: String?,
        property: KProperty0<Boolean?>,
        withId: Boolean = false,
        disabled: Boolean = false,
    ) = INPUT {
        classes(classes)
        type("checkbox")
        if(withId) id(property.name)
        name(property)
        value("true")
        checked(property.get())
        disabled(disabled)
    }

    inline fun INPUT(
        type: String,
        name: String? = null,
        value: String? = null,
        classes: String? = null,
        required: Boolean = false,
        checked: Boolean? = null,
        body: () -> Unit
    ) = EL("INPUT", noBody = true) {
        type(type)
        if(name != null) name(name)
        value(value)
        classes(classes)
        required(required)
        checked?.let { checked(it) }
        body()
    }

    fun INPUT(
        type: String,
        name: String? = null,
        value: String? = null,
        classes: String? = null,
        required: Boolean = false,
        checked: Boolean? = null
    ) = EL("INPUT", noBody = true) {
        type(type)
        if(name != null) name(name)
        value(value)
        classes(classes)
        required(required)
        checked?.let { checked(it) }
    }

    fun HIDDEN(name: String, value: Int?) = INPUT {
        type("hidden")
        name(name)
        value(value)
    }

    fun HIDDEN(name: String, value: Long?) = INPUT {
        type("hidden")
        name(name)
        value(value)
    }

    fun HIDDEN(name: String, value: String?) = INPUT {
        type("hidden")
        name(name)
        value(value)
    }

    fun HIDDEN(name: String, value: UUID?) = INPUT {
        type("hidden")
        name(name)
        value(value)
    }

    fun HIDDEN(name: String, value: Enum<*>?) = INPUT {
        type("hidden")
        name(name)
        value(value)
    }

    fun HIDDEN(name: String, value: LocalDate?) = INPUT {
        type("hidden")
        name(name)
        value(formatDateISO(value))
    }

    fun HIDDEN(property: KProperty<UUID>, value: UUID?) = INPUT {
        type("hidden")
        name(property)
        value(value)
    }

    fun HIDDEN(property: KProperty<Iterable<UUID?>>, vararg values: UUID?) {
        values.forEach { value ->
            value?.let {
                HIDDEN(property.name, it)
            }
        }
    }

    fun HIDDEN(property: KProperty<Iterable<Long?>>, vararg values: Long?) {
        values.forEach { value ->
            value?.let {
                HIDDEN(property.name, it)
            }
        }
    }

    fun HIDDEN(property: KProperty<String?>, value: String?) = INPUT {
        type("hidden")
        name(property)
        value(value)
    }

    fun HIDDEN_BOOLEAN(property: KProperty0<Boolean?>) = HIDDEN(property.name, property.get()?.toString())
    fun HIDDEN_INT(property: KProperty0<Int?>) = HIDDEN(property.name, property.get())
    fun HIDDEN_LONG(property: KProperty0<Long?>) = HIDDEN(property.name, property.get())
    fun HIDDEN_UUID(property: KProperty0<UUID?>) = HIDDEN(property.name, property.get())
    fun HIDDEN_STRING(property: KProperty0<String?>) = HIDDEN(property.name, property.get())
    fun HIDDEN_ENUM(property: KProperty0<Enum<*>?>) = HIDDEN(property.name, property.get())
    fun HIDDEN_DATE(property: KProperty0<LocalDate?>) = HIDDEN(property.name, property.get())

    fun TEXTAREA(name: String, value: String? = null, required: Boolean = false, classes: String? = null, style: String? = null) =
        EL("TEXTAREA") {
            classes(classes)
            name(name)
            if(required) required()
            style(style)
            +value
        }

    fun TEXTAREA(property: KProperty0<String?>, required: Boolean = false, classes: String? = null, style: String? = null) =
        TEXTAREA(name = property.name, value = property.get(), required = required, classes = classes, style = style)

    open fun csrf(): Pair<String, String>? = null

    inline fun FORM(action: String? = null, method: String? = null, body: () -> Unit) = EL("FORM") {
        action(action)
        method(method)
        body()
        if(method == null
            || method.equals("post", ignoreCase = true)
            || method.equals("delete", ignoreCase = true)) {
            csrf()?.let {
                HIDDEN(it.first, it.second)
            }
        }
    }

    inline fun IMG(src: String, width: Int? = null, height: Int? = null, body: () -> Unit) = EL("IMG", noBody = true) {
        src(src)
        if(width != null) width(width)
        if(height != null) height(height)
        body()
    }

    fun IMG(src: String, width: Int? = null, height: Int? = null) = EL("IMG", noBody = true) {
        src(src)
        if(width != null) width(width)
        if(height != null) height(height)
    }

    inline fun SVG(body: () -> Unit) = EL("SVG") {
        attr("xmlns", "http://www.w3.org/2000/svg")
        body()
    }

    fun PATH(d: String) {
        EL("path") { attr("d", d) }
    }

    fun t(t: String?) {
        if(t != null) {
            closeTagOpening()
            htmlEncode(t, context.out)
        }
    }

    fun t(t: Int?) = t(t?.toString())
    fun t(t: Long?) = t(t?.toString())
    fun t(t: Enum<*>?) = t(t?.name)

    operator fun String?.unaryPlus() = t(this)
    operator fun UUID?.unaryPlus() = t(this?.toString())
    operator fun LocalDate?.unaryPlus() = t(formatDate(this))
    operator fun Enum<*>?.unaryPlus() = t(this)

    fun attr(name: String, value: String?) {
        if(context.tagOpening.isNotEmpty()) {
            context.tagOpening.append(' ').append(name).append('=').append('"')
            if(value != null) htmlEncode(value, context.tagOpening)
            context.tagOpening.append('"')
        } else if(context.throwOnAttributeNotInTag) {
            throw GossRendererException("Attribute $name is not in tag brackets")
        }
    }

    fun attr(name: String) {
        if(context.tagOpening.isNotEmpty()) {
            context.tagOpening.append(' ').append(name)
        } else if(context.throwOnAttributeNotInTag) {
            throw GossRendererException("Attribute $name is not in tag brackets")
        }
    }

    fun attr(name: String, value: Int?) = attr(name, value?.toString())
    fun attr(name: String, value: Long?) = attr(name, value?.toString())
    fun attr(name: String, value: Boolean?) = attr(name, value?.toString())
    fun attr(name: String, value: UUID?) = attr(name, value?.toString())
    fun attr(name: String, value: Enum<*>?) = attr(name, value?.name)

    fun classes(value: String?) = value?.let { context.tagClasses.add(it) }
    fun id(value: String?) = value?.let { attr("id", value) }
    fun id(value: UUID?) = value?.let { attr("id", value) }
    fun rel(value: String?) = attr("rel", value)
    fun src(value: String?) = attr("src", value)
    fun width(value: Int?) = attr("width", value)
    fun height(value: Int?) = attr("height", value)
    fun href(value: String?) = attr("href", value)
    fun type(value: String?) = attr("type", value)
    fun colspan(value: Int?) = attr("colspan", value)
    fun rowspan(value: Int?) = attr("rowspan", value)

    fun typeSubmit(classes: String?) {
        type("submit")
        classes(classes)
    }

    fun typeNumber(name: String?, value: Double? = null) {
        type("text")
        name(name)
        attr("pattern", COMMON_NUMBER_PATTERN)
        value(value)
    }

    fun typeMoney(name: String?, value: Double? = null) {
        type("number")
        step("0.01")
        name(name)
        value(value?.let { "%.02f".format(it).replace(".00", "") })
    }

    fun nameValueMoney(property: KProperty<Double?>, value: Double? = null) =
        typeMoney(property.name, value)

    fun nameValueMoney(property: KProperty0<Double?>) =
        typeMoney(property.name, property.get())

    fun nameValueLong(property: KProperty0<Long?>, inputType: String = "number") {
        type(inputType)
        name(property)
        value(property.get())
    }

    fun nameValueDouble(property: KProperty0<Double?>, inputType: String = "text") {
        attr("pattern", "^\\s*${NUMBER_PATTERN_SIMPLE}\\s*$")
        type(inputType)
        name(property)
        value(property.get())
    }

    fun nameValueBool(property: KProperty0<Boolean?>, inputType: String = "text") {
        type(inputType)
        name(property)
        value(property.get())
    }

    fun nameValueInt(property: KProperty0<Int?>, inputType: String = "number") {
        type(inputType)
        name(property)
        value(property.get())
    }

    fun nameValueString(property: KProperty0<String?>, inputType: String = "text") {
        type(inputType)
        name(property)
        value(property.get())
    }

    fun nameValueEnum(property: KProperty0<Enum<*>?>, inputType: String = "text") {
        type(inputType)
        name(property)
        value(property.get())
    }

    fun typeInt(name: String?, value: Int? = null) {
        type("number")
        name(name)
        value(value)
    }

    fun typeLong(name: String?, value: Long? = null) {
        type("number")
        name(name)
        value(value)
    }

    fun typeDate(name: String?, value: LocalDate? = null, required: Boolean = false) {
        type("date")
        name(name)
        required(required)
        value(value)
    }

    fun name(value: String?) = attr(
        "name",
        context.namePrefixes.lastOrNull()?.let { it + value } ?: value
    )

    fun name(property: KProperty<*>) = name(property.name)
    fun name(e: Enum<*>) = name(e.name)

    inline fun namePrefix(prefix: String, body: () -> Unit) {
        context.namePrefixes.add(context.namePrefixes.lastOrNull()?.let { it + prefix } ?: prefix)
        try {
            body()
        }finally {
            context.namePrefixes.removeLast()
        }
    }

    inline fun namePrefix(property: KProperty<*>, body: () -> Unit) =
        namePrefix("${property.name}.", body)

    inline fun namePrefix(property: KProperty0<Map<UUID, *>>, value: UUID, body: () -> Unit) =
        namePrefix("${property.name}[$value].", body)

    fun nameValue(property: KProperty<String?>, value: String?) {
        name(property)
        value(value)
    }

    fun nameValue(property: KProperty0<String?>) {
        name(property)
        value(property.get())
    }

    fun nameValueUUID(property: KProperty<UUID?>, value: UUID?) {
        name(property)
        value(value)
    }

    fun nameValueUUID(property: KProperty0<UUID?>, classes: String = "text") {
        classes(classes)
        name(property)
        value(property.get())
    }

    fun nameValueDate(property: KProperty0<LocalDate?>, type: String = "date") {
        type(type)
        name(property)
        value(property.get())
    }

    fun nameValue(property: KProperty<Iterable<UUID>>, vararg values: UUID?) {
        values.firstOrNull()?.let { value ->
            name(property)
            value(value)
        }
    }

    fun method(value: String?) = value?.let { attr("method", value) }
    fun value(value: String?) = value?.let { attr("value", value) }
    fun value(value: Boolean?) = value?.let { attr("value", it.toString()) }
    fun value(value: Int?) = value?.let { attr("value", value) }
    fun value(value: Long?) = value?.let { attr("value", value) }
    fun enctype(value: String?) = value?.let { attr("enctype", value) }
    fun target(value: String?) = value?.let { attr("target", value) }
    fun value(value: Double?) = value?.let {
        attr("value", formatDouble(value, 20, true))
    }

    fun value(value: UUID?) = value?.let { attr("value", value) }
    fun value(value: LocalDate?) = value?.let { attr("value", formatDateISO(value)) }
    fun value(value: LocalDateTime?) = value?.let { attr("value", formatDateTimeISO(value)) }
    fun value(value: Enum<*>?) = value?.let { attr("value", value) }
    fun style(@Language("css") value: String?) = attr("style", value)
    fun min(value: LocalDate?) = value?.let { attr("min", formatDateISO(value)) }
    fun min(value: String?) = value?.let { attr("min", value) }
    fun max(value: LocalDate?) = value?.let { attr("max", formatDateISO(value)) }
    fun max(value: String?) = value?.let { attr("max", value) }
    fun step(value: String?) = value?.let { attr("step", value) }
    fun placeholder(value: String?) = value?.let { attr("placeholder", value) }
    fun scope(value: String?) = attr("scope", value)
    /** value = "off" disable form autocomplete */
    fun autocomplete(value: String?) = attr("autocomplete", value)
    fun action(value: String?) = value?.let { attr("action", value) }
    fun accept(value: String?) = value?.let { attr("accept", value) }
    fun title(value: String?) = value?.let { attr("title", value) }
    fun forAttr(value: String?) = value?.let { attr("for", value) }
    fun role(value: String?) = value?.let { attr("role", value) }

    fun onSubmit(@Language("js") value: String?) = value?.let { attr("onsubmit", value) }
    fun onClick(@Language("js") value: String?) = value?.let { attr("onclick", value) }
    fun onChange(@Language("js") value: String?) = value?.let { attr("onchange", value) }
    fun onInput(@Language("js") value: String?) = value?.let { attr("oninput", value) }

    fun required(v: Boolean? = true) {
        if(v == true) attr("required")
    }

    fun multiple(v: Boolean? = true) {
        if(v == true) attr("multiple")
    }

    fun checked(v: Boolean? = true) {
        if(v == true) attr("checked")
    }

    fun selected(v: Boolean? = true) {
        if(v == true) attr("selected")
    }

    fun readonly(v: Boolean? = true) {
        if(v == true) attr("readonly")
    }

    fun disabled(v: Boolean? = true) {
        if(v == true) attr("disabled")
    }

    fun data(name: String, value: String?) = attr("data-$name", value)
    fun data(name: String, value: Boolean?) = attr("data-$name", value)
    fun data(name: String, value: Int?) = attr("data-$name", value)
    fun data(name: String, value: Long?) = attr("data-$name", value)
    fun data(name: String, value: Enum<*>?) = attr("data-$name", value)

    override fun formatDateTime(t: LocalDateTime?): String? =
        context.dateTimeFormats.formatDateTime(t)

    override fun formatDateTimeSec(t: LocalDateTime?): String? =
        context.dateTimeFormats.formatDateTimeSec(t)

    override fun formatDateTimeMillis(t: LocalDateTime?): String? =
        context.dateTimeFormats.formatDateTimeMillis(t)

    override fun formatDate(d: LocalDate?): String? =
        context.dateTimeFormats.formatDate(d)

    override fun formatTime(t: LocalDateTime?): String? =
        context.dateTimeFormats.formatTime(t)

    override fun formatTimeSec(t: LocalDateTime?): String? =
        context.dateTimeFormats.formatTimeSec(t)

    override fun formatMoney(n: Number?): String? = context.moneyFormats.formatMoney(n)

    override fun formatMoney2(n: Number?): String? = context.moneyFormats.formatMoney2(n)

    override fun formatDouble(n: Number?, digits: Int, trimZeros: Boolean): String? =
        context.moneyFormats.formatDouble(n, digits, trimZeros)

    companion object {
        private val threadLocalContext = ThreadLocal<GossRendererContext?>()

        const val NUMBER_PATTERN_SIMPLE = "[+-]?[0-9]+(\\.[0-9]+)?"                // 12345.789
        const val US_NUMBER = "[+-]?[0-9]{1,3}(,[0-9]{3})*(\\.[0-9]+)?"      // 12,345.789
        const val US_NUMBER_SIMPLE = NUMBER_PATTERN_SIMPLE                // 12345.789
        const val EU_NUMBER = "[+-]?[0-9]{1,3}( [0-9]{3})*(,[0-9]+)?"        // 12 345,789
        const val EU_NUMBER_SIMPLE = "[+-]?[0-9]+(,[0-9]+)?"                  // 12345,789

        val COMMON_NUMBER_PATTERN = "^\\s*(($US_NUMBER)|($EU_NUMBER)|($US_NUMBER_SIMPLE)|($EU_NUMBER_SIMPLE))\\s*$"

        val dateTimeFormatISO = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        val dateTimeFormatISOSec = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val dateFormatISO = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        fun formatDateTimeISO(t: LocalDateTime?): String? = t?.let { dateTimeFormatISO.format(t) }
        fun formatDateTimeISOSec(t: LocalDateTime?): String? = t?.let { dateTimeFormatISOSec.format(t) }
        fun formatDateISO(d: LocalDate?): String? = d?.let { dateFormatISO.format(d) }

        fun urlEncode(s: String): String = URLEncoder.encode(s, Charset.defaultCharset())

        fun htmlEncode(t: String, out: Appendable) {
            var i = 0
            val l = t.length
            while(i < l) {
                when(val c = t[i]) {
                    '<' -> out.append("&lt;")
                    '>' -> out.append("&gt;")
                    '&' -> out.append("&amp;")
                    '"' -> out.append("&quot;")
                    else -> out.append(c)
                }
                i++
            }
        }

        fun htmlEncode(t: String): String = StringBuilder().let {
            htmlEncode(t, it)
            it.toString()
        }

        fun use(
            out: Appendable,
            dateTimeFormats: GossrDateTimeFormatter = DateTimeFormatEurope,
            moneyFormats: GossrMoneyFormatter = DotCommaMoneyFormat,
            throwOnAttributeNotInTag: Boolean = true,
            renderFunction: () -> Unit
        ) {
            try {
                threadLocalContext.set(GossRendererContext(
                    out = out,
                    dateTimeFormats = dateTimeFormats,
                    moneyFormats = moneyFormats,
                    throwOnAttributeNotInTag = throwOnAttributeNotInTag
                ))

                renderFunction()
            }finally {
                threadLocalContext.set(null)
            }
        }
    }
}

