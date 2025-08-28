package kiss.gossr

import org.intellij.lang.annotations.Language
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URLEncoder
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.full.isSubclassOf

@Suppress("FunctionNaming", "TooManyFunctions", "MethodOverloading")
abstract class GossRenderer : GossrDateTimeFormatter, GossrMoneyFormatter {
    private var localContext: GossRendererContext? = null

    val context: GossRendererContext get() = localContext
        ?: threadLocalContext.get()?.also {
            localContext = it
        }
        ?: throw GossRendererException("GossRendererContext.threadLocal is empty")

    fun closeTagOpening(ket: String = ">") {
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
            context.out.append(ket)
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
                closeTagOpening("/>")
            } else {
                closeTagOpening()
                context.out.append("</").append(tag).append(">")
            }
            if(context.newLineAfterTagClose) context.out.append('\n')
        }
    }

    inline fun CENTER(classes: String? = null, body: () -> Unit = {}) = EL("CENTER") {
        classes(classes)
        body()
    }

    inline fun DIV(classes: String? = null, body: () -> Unit = {}) = EL("DIV") {
        classes(classes)
        body()
    }

    /** small text style */
    inline fun SMALL(classes: String? = null, body: () -> Unit = {}) = EL("SMALL") {
        classes(classes)
        body()
    }

    /** strike-through text */
    inline fun STRIKE(classes: String? = null, body: () -> Unit = {}) = EL("STRIKE") {
        classes(classes)
        body()
    }

    /** strong emphasis */
    inline fun STRONG(classes: String? = null, body: () -> Unit = {}) = EL("STRONG") {
        classes(classes)
        body()
    }

    /** subscript */
    inline fun SUB(classes: String? = null, body: () -> Unit = {}) = EL("SUB") {
        classes(classes)
        body()
    }

    /** superscript */
    inline fun SUP(classes: String? = null, body: () -> Unit = {}) = EL("SUP") {
        classes(classes)
        body()
    }

    inline fun SPAN(classes: String? = null, body: () -> Unit = {}) = EL("SPAN") {
        classes(classes)
        body()
    }

    inline fun LI(classes: String? = null, body: () -> Unit = {}) = EL("LI") {
        classes(classes)
        body()
    }

    inline fun OL(classes: String? = null, body: () -> Unit = {}) = EL("OL") {
        classes(classes)
        body()
    }

    inline fun TABLE(classes: String? = null, body: () -> Unit = {}) = EL("TABLE") {
        classes(classes)
        body()
    }

    inline fun TBODY(classes: String? = null, body: () -> Unit = {}) = EL("TBODY") {
        classes(classes)
        body()
    }

    inline fun THEAD(classes: String? = null, body: () -> Unit = {}) = EL("THEAD") {
        classes(classes)
        body()
    }

    inline fun TFOOT(classes: String? = null, body: () -> Unit = {}) = EL("TFOOT") {
        classes(classes)
        body()
    }

    inline fun TH(classes: String? = null, body: () -> Unit = {}) = EL("TH") {
        classes(classes)
        body()
    }

    inline fun TR(classes: String? = null, body: () -> Unit = {}) = EL("TR") {
        classes(classes)
        body()
    }

    inline fun TD(classes: String? = null, body: () -> Unit = {}) = EL("TD") {
        classes(classes)
        body()
    }

    inline fun PRE(classes: String? = null, body: () -> Unit = {}) = EL("PRE") {
        classes(classes)
        body()
    }

    inline fun TT(classes: String? = null, body: () -> Unit = {}) = EL("TT") {
        classes(classes)
        body()
    }

    /** anchor */
    inline fun A(classes: String? = null, body: () -> Unit = {}) = EL("A") {
        classes(classes)
        body()
    }

    inline fun B(classes: String? = null, body: () -> Unit = {}) = EL("B") {
        classes(classes)
        body()
    }

    inline fun P(classes: String? = null, body: () -> Unit = {}) = EL("P") {
        classes(classes)
        body()
    }

    inline fun S(classes: String? = null, body: () -> Unit = {}) = EL("S") {
        classes(classes)
        body()
    }

    inline fun I(classes: String? = null, body: () -> Unit = {}) = EL("I") {
        classes(classes)
        body()
    }

    /** underlined text style */
    inline fun U(classes: String? = null, body: () -> Unit = {}) = EL("U") {
        classes(classes)
        body()
    }

    /** short inline quotation */
    inline fun Q(classes: String? = null, body: () -> Unit = {}) = EL("Q") {
        classes(classes)
        body()
    }

    /** unordered list */
    inline fun UL(classes: String? = null, body: () -> Unit = {}) = EL("UL") {
        classes(classes)
        body()
    }

    inline fun NAV(classes: String? = null, body: () -> Unit = {}) = EL("NAV") {
        classes(classes)
        body()
    }

    inline fun H1(classes: String? = null, body: () -> Unit = {}) = EL("H1") {
        classes(classes)
        body()
    }

    inline fun H2(classes: String? = null, body: () -> Unit = {}) = EL("H2") {
        classes(classes)
        body()
    }

    inline fun H3(classes: String? = null, body: () -> Unit = {}) = EL("H3") {
        classes(classes)
        body()
    }

    inline fun H4(classes: String? = null, body: () -> Unit = {}) = EL("H4") {
        classes(classes)
        body()
    }

    inline fun H5(classes: String? = null, body: () -> Unit = {}) = EL("H5") {
        classes(classes)
        body()
    }

    inline fun H6(classes: String? = null, body: () -> Unit = {}) = EL("H6") {
        classes(classes)
        body()
    }

    inline fun NOBR(classes: String? = null, body: () -> Unit = {}) = EL("NOBR") {
        classes(classes)
        body()
    }

    fun BR() = EL("BR", noBody = true) {}
    fun HR() = EL("HR", noBody = true) {}

    inline fun LABEL(classes: String? = null, body: () -> Unit = {}) = EL("LABEL") {
        classes(classes)
        body()
    }

    inline fun SELECT(classes: String? = null, body: GossRendererCommonSelect.() -> Unit) = EL("SELECT") {
        classes(classes)
        GossRendererCommonSelect().body()
    }

    inline fun <T> SELECT(property: KProperty0<T?>, body: GossRendererTypedSelect<T>.() -> Unit) = EL("SELECT") {
        name(property)
        GossRendererTypedSelect(property.get()).body()
    }

    inline fun <E, C : Collection<E>> MULTISELECT(
        property: KProperty0<C?>,
        body: GossRendererTypedMultiSelect<E>.() -> Unit
    ) = EL("SELECT") {
        name(property)
        multiple(true)
        GossRendererTypedMultiSelect(property.get()).body()
    }

    fun OPTION(empty: String?) = EL("OPTION") {
        value("")
        +empty
    }

    inline fun TEXTAREA(classes: String? = null, body: () -> Unit = {}) = EL("TEXTAREA") {
        classes(classes)
        body()
    }

    inline fun HTML(body: () -> Unit) = EL("HTML", body = body)
    inline fun HEAD(body: () -> Unit) = EL("HEAD", body = body)
    inline fun BODY(body: () -> Unit) = EL("BODY", body = body)

    inline fun BUTTON(classes: String? = null, body: () -> Unit = {}) = EL("BUTTON") {
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
        html?.let {
            closeTagOpening()
            context.out.append(html)
        }
    }

    inline fun INPUT(classes: String? = null, body: () -> Unit) = EL("INPUT", noBody = true) {
        classes(classes)
        body()
    }

    inline fun SUBMIT(classes: String? = null, text: String? = null, body: () -> Unit = {}) = INPUT {
        typeSubmit(classes)
        value(text)
        body()
    }

    inline fun SUBMIT(classes: String?, property: KProperty<String?>, text: String?, body: () -> Unit = {}) = INPUT {
        typeSubmit(classes)
        name(property)
        value(text)
        body()
    }

    inline fun <T> RADIO(
        property: KProperty0<T?>,
        value: T,
        withId: Boolean = false,
        body: () -> Unit = {}
    ): String? {
        val id = if(withId) "checkbox-${UUID.randomUUID()}" else null
        INPUT {
            type("radio")
            name(property)
            value(value)
            checked(property.get() == value)
            id(id)
            body()
        }
        return id
    }

    inline fun CHECKBOX(
        property: KProperty0<Boolean?>,
        withId: Boolean = false,
        body: () -> Unit = {}
    ): String = INPUT {
        type("checkbox")
        name(property)
        if(withId) id(property.name)
        value("true")
        checked(property.get())
        body()
    }.let {
        property.name
    }

    fun <T> CHECKBOX(
        property: KProperty0<Iterable<T>?>,
        value: T,
        withId: Boolean = false,
        body: () -> Unit = {}
    ): String? {
        val id = if(withId) "checkbox-${UUID.randomUUID()}" else null
        INPUT {
            type("checkbox")
            if(withId) id(id)
            name(property)
            value(value)
            checked(property.get()?.contains(value))
            body()
        }
        return id
    }

    fun HIDDEN(name: String, value: Any?) = INPUT {
        type("hidden")
        name(name)
        value(value)
    }

    fun HIDDEN(property: KProperty0<*>) = INPUT {
        type("hidden")
        name(property)
        value(property.get())
    }

    fun <V> HIDDEN_ITERABLE(property: KProperty0<Iterable<V?>?>, transform: ((V) -> CharSequence)? = null) {
        property.get()?.mapNotNull { it }.nullIfEmpty()?.let { values ->
            HIDDEN(property.name, values.joinToString(",", transform = transform))
        }
    }

    fun TEXTAREA(classes: String? = null, value: String? = null, body: () -> Unit = {}) =
        EL("TEXTAREA") {
            classes(classes)
            body()
            +value
        }

    fun TEXTAREA(property: KProperty0<String?>, body: () -> Unit = {}) =
        EL("TEXTAREA") {
            name(property.name)
            required(!property.returnType.isMarkedNullable)
            body()
            +property.get()
        }

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

    inline fun IMG(src: String, width: Int? = null, height: Int? = null, body: () -> Unit = {}) = EL("IMG", noBody = true) {
        src(src)
        if(width != null) width(width)
        if(height != null) height(height)
        body()
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

    fun attr(name: String, value: CharSequence?) {
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
    fun pattern(value: String?) = value?.let { attr("pattern", value) }
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

    open fun typeMoney(name: String?, value: Number? = null, required: Boolean = false) {
        type("number")
        step("0.01")
        name(name)
        val value = (value as? BigDecimal)
            ?: (value as? BigInteger)?.toBigDecimal()
            ?: value?.toDouble()?.let { if(it.isNaN()) null else it }
        value(value?.let { "%.02f".format(it).replace(".00", "") })
        required(required)
    }

    fun nameValueMoney(property: KProperty<Number?>, value: Double? = null) =
        typeMoney(property.name, value, required = !property.returnType.isMarkedNullable)

    fun nameValueMoney(property: KProperty0<Number?>) =
        typeMoney(property.name, property.get(), required = !property.returnType.isMarkedNullable)

    fun nameValueLong(property: KProperty0<Long?>, inputType: String = "number") {
        type(inputType)
        name(property)
        value(property.get())
        required(!property.returnType.isMarkedNullable)
    }

    fun nameValueDouble(property: KProperty0<Double?>, inputType: String = "text") {
        attr("pattern", "^\\s*${NUMBER_PATTERN_SIMPLE}\\s*$")
        type(inputType)
        name(property)
        value(property.get())
        required(!property.returnType.isMarkedNullable)
    }

    fun nameValueBigDecimal(property: KProperty0<BigDecimal?>, inputType: String = "text") {
        attr("pattern", "^\\s*${NUMBER_PATTERN_SIMPLE}\\s*$")
        type(inputType)
        name(property)
        value(property.get()?.stripTrailingZeros()?.toPlainString())
        required(!property.returnType.isMarkedNullable)
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
        required(!property.returnType.isMarkedNullable)
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
        required(!property.returnType.isMarkedNullable)
    }

    fun name(value: String?) = attr(
        "name",
        if(context.nameAsMapKey) {
            context.namePrefix?.let { "$it[$value]" } ?: "[$value]"
        } else {
            context.namePrefix?.let { it + value } ?: value
        }
    )

    fun name(property: KProperty<*>) = name(property.name)
    fun name(e: Enum<*>) = name(e.name)

    fun <T> name(property: KProperty0<Map<T, *>?>, value: T) =
        name("${property.name}[$value]")

    inline fun namePrefix(prefix: String, reset: Boolean = false, mapKeys: Boolean = false, body: () -> Unit) {
        val savedNamePrefix = context.namePrefix
        val savedNameAsMapKey = context.nameAsMapKey

        if(reset) context.namePrefix = null

        context.namePrefix = context.namePrefix?.let { it + prefix } ?: prefix
        context.nameAsMapKey = mapKeys

        try {
            body()
        } finally {
            context.namePrefix = savedNamePrefix
            context.nameAsMapKey = savedNameAsMapKey
        }
    }

    inline fun namePrefix(property: KProperty<*>, reset: Boolean = false, body: () -> Unit) =
        namePrefix("${property.name}.", reset, body = body)

    inline fun namePrefix(property: KProperty0<Map<*, *>?>, reset: Boolean = false, body: () -> Unit) =
        namePrefix(property.name, reset, mapKeys = true, body = body)

    inline fun <K, reified V> namePrefix(property: KProperty0<Map<K, V>?>, value: K, reset: Boolean = false, body: () -> Unit) =
        if(V::class.isSubclassOf(Map::class)) {
            namePrefix("${property.name}[$value]", reset, mapKeys = true, body = body)
        } else {
            namePrefix("${property.name}[$value].", reset, body = body)
        }

    inline fun newLineAfterTagClose(v: Boolean, body: () -> Unit) {
        val oldV = context.newLineAfterTagClose
        try {
            context.newLineAfterTagClose = v
            body()
        }finally {
            context.newLineAfterTagClose = oldV
        }
    }

    fun nameValueUUID(property: KProperty0<UUID?>) {
        type("text")
        name(property)
        value(property.get())
        required(!property.returnType.isMarkedNullable)
    }

    fun nameValueDate(property: KProperty0<LocalDate?>, type: String = "date") {
        type(type)
        name(property)
        value(property.get())
        required(!property.returnType.isMarkedNullable)
    }

    fun nameValueDatetimeLocal(property: KProperty0<LocalDateTime?>, type: String = "datetime-local") {
        type(type)
        name(property)
        value(property.get())
        required(!property.returnType.isMarkedNullable)
    }

    fun method(value: String?) = value?.let { attr("method", value) }
    fun enctype(value: String?) = value?.let { attr("enctype", value) }
    fun target(value: String?) = value?.let { attr("target", value) }

    fun value(value: CharSequence?) = value?.let { attr("value", value) }
    fun value(value: Double?) = value?.let {
        attr("value", formatDouble(value, 20, true))
    }

    fun value(value: LocalDate?) = value?.let { attr("value", formatDateISO(value)) }
    fun value(value: LocalDateTime?) = value?.let { attr("value", formatDateTimeISO(value)) }
    fun value(value: Enum<*>?) = value?.let { attr("value", value) }

    open fun value(v: Any?) = when(v) {
        null -> run {}
        is Enum<*> -> value(v)
        is Double -> value(v)
        is LocalDate -> value(v)
        is LocalDateTime -> value(v)
        else -> value(v.toString())
    }

    fun style(@Language("css", prefix = "{", suffix = "}") value: String?) = attr("style", value)
    fun min(value: String?) = value?.let { attr("min", value) }
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
    fun onPaste(@Language("js") value: String?) = value?.let { attr("onpaste", value) }
    fun onBlur(@Language("js") value: String?) = value?.let { attr("onblur", value) }

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

    open fun data(name: String, v: Any?) = when(v) {
        null -> run {}
        is Enum<*> -> attr("data-$name", v)
        is Double -> attr("data-$name", formatDouble(v, 20, trimZeros = true))
        is LocalDate -> attr("data-$name", formatDateISO(v))
        is LocalDateTime -> attr("data-$name", formatDateTimeISO(v))
        else -> attr("data-$name", v.toString())
    }

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

    open fun csrf(): Pair<String, String>? = null

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

        fun htmlEncode(t: CharSequence, out: Appendable) {
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
            newLineAfterTagClose: Boolean = true,
            renderFunction: () -> Unit
        ) {
            try {
                threadLocalContext.set(GossRendererContext(
                    out = out,
                    dateTimeFormats = dateTimeFormats,
                    moneyFormats = moneyFormats,
                    throwOnAttributeNotInTag = throwOnAttributeNotInTag,
                    newLineAfterTagClose = newLineAfterTagClose
                ))

                renderFunction()
            }finally {
                threadLocalContext.set(null)
            }
        }

        private fun <T: Collection<*>> T?.nullIfEmpty(): T? {
            return if(isNullOrEmpty()) null else this
        }

        private fun String?.nullIfEmpty(): String? {
            return if(isNullOrEmpty()) null else this
        }

        private fun <T: Map<*, *>> T?.nullIfEmpty(): T? {
            return if(isNullOrEmpty()) null else this
        }
    }
}

