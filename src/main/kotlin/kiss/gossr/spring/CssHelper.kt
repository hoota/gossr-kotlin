package kiss.gossr.spring

import org.intellij.lang.annotations.Language
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.util.DigestUtils
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletResponse
import kotlin.reflect.KClass
import kotlin.reflect.jvm.javaMethod

open class CssStyles(
    val setupStype: (CssStyles.() -> Unit)? = null
) {
    @Language("css", prefix = "{", suffix = "}")
    var style: String? = null
    @Language("css", prefix = "{", suffix = "}")
    var hover: String? = null
    @Language("css", prefix = "{", suffix = "}")
    var active: String? = null
    @Language("css", prefix = "{", suffix = "}")
    var focus: String? = null
    @Language("css", prefix = "{", suffix = "}")
    var visited: String? = null
    @Language("css", prefix = "{", suffix = "}")
    var firstChild: String? = null
    @Language("css", prefix = "{", suffix = "}")
    var lastChild: String? = null
    @Language("css", prefix = "{", suffix = "}")
    var checked: String? = null
    @Language("css", prefix = "{", suffix = "}")
    var disabled: String? = null
    @Language("css", prefix = "{", suffix = "}")
    var enabled: String? = null
    @Language("css", prefix = "{", suffix = "}")
    var required: String? = null
    @Language("css", prefix = "{", suffix = "}")
    var optional: String? = null
    @Language("css", prefix = "{", suffix = "}")
    var empty: String? = null
    @Language("css", prefix = "{", suffix = "}")
    var firstOfType: String? = null
    @Language("css", prefix = "{", suffix = "}")
    var lastOfType: String? = null
    @Language("css", prefix = "{", suffix = "}")
    var onlyChild: String? = null
    @Language("css", prefix = "{", suffix = "}")
    var onlyOfType: String? = null
    @Language("css", prefix = "{", suffix = "}")
    var target: String? = null

    @Language("css", prefix = "{", suffix = "}")
    var before: String? = null
    @Language("css", prefix = "{", suffix = "}")
    var after: String? = null
    @Language("css", prefix = "{", suffix = "}")
    var firstLine: String? = null
    @Language("css", prefix = "{", suffix = "}")
    var firstLetter: String? = null
    @Language("css", prefix = "{", suffix = "}")
    var selection: String? = null
    @Language("css", prefix = "{", suffix = "}")
    var placeholder: String? = null
    @Language("css", prefix = "{", suffix = "}")
    var marker: String? = null
    @Language("css", prefix = "{", suffix = "}")
    var fileSelectorButton: String? = null

    var additional: MutableMap<String, String>? = null

    fun add(selector: String, @Language("css", prefix = "{", suffix = "}") value: String) {
        if(additional == null) additional = HashMap()
        additional?.put(selector, value)
    }

    init {
        updateStyle()
    }

    fun updateStyle() {
        setupStype?.invoke(this)
    }
}

abstract class CssDeclaration : CssStyles() {
    abstract val cssClassName: String?
    internal abstract val cssSelector: String
    internal var medias: MutableMap<String, CssStyles>? = null

    abstract fun updateDeclaration()

    @Synchronized
    fun media(
        @Language("css", prefix = "media(", suffix = "){}")
        selector: String,
        styleSetup: CssStyles.() -> Unit
    ) {
        if(medias == null) medias = HashMap()
        medias?.put(selector.lowercase(), CssStyles(styleSetup))
    }
}

private val classNameId = AtomicInteger()
private val classesMap = ConcurrentHashMap<Class<out CssDeclaration>, String>()

fun <T : CssClass> getCssClassName(javaClass: Class<T>): String? = classesMap[javaClass]
fun <T : CssClass> getCssClassName(kClass: KClass<T>): String? = classesMap[kClass.java]

open class CssClass(
    val setup: (CssClass.() -> Unit)
) : CssDeclaration() {
    override var cssClassName = getOrCreateClassName(this.javaClass)
    override val cssSelector get() = ".$cssClassName"

    override fun updateDeclaration() {
        setup.invoke(this)
        classesMap[this.javaClass] = cssClassName
    }

    init {
        updateDeclaration()
    }

    companion object {
        private fun <T : CssClass> getOrCreateClassName(javaClass: Class<T>): String {
            return classesMap.computeIfAbsent(javaClass) {
                classNameId.getAndIncrement().let { "gossr-$it" }
            }
        }
    }
}

open class CssTag(
    override val cssSelector: String,
    val setup: (CssTag.() -> Unit)
) : CssDeclaration() {
    override val cssClassName get() = null

    override fun updateDeclaration() {
        setup.invoke(this)
    }

    init {
        updateDeclaration()
    }
}

@Component
class CssHelper(
    val applicationContext: ApplicationContext,
    val handlerMapping: RequestMappingHandlerMapping,
    val cssUrl: String = "/assets/gossr-styles-{hash}.css",
    val devMode: Boolean = false
) {
    private val log: Logger = LoggerFactory.getLogger(javaClass)
    private lateinit var declarations: Map<KClass<out CssDeclaration>, CssDeclaration>
    private var hash: String? = null

    fun getHash(): String {
        if(devMode) return "dev"
        return hash ?: run {
            val out = StringBuilder()

            outAll(out)

            val h = DigestUtils.md5DigestAsHex(out.toString().toByteArray(Charsets.UTF_8))
            hash = h
            h
        }
    }

    fun getUrl() = cssUrl.replace("{hash}", getHash())

    @PostConstruct
    fun postConstruct() {
        instance = this

        declarations = applicationContext.getBeansOfType(CssDeclaration::class.java)
            .values
            .associateBy { it.javaClass.kotlin }

        log.info("using ${handlerMapping.patternParser?.javaClass?.simpleName ?: "AntPathMatcher"} to bind CSS endpoint: $cssUrl")

        handlerMapping.registerMapping(
            RequestMappingInfo
                .paths(cssUrl)
                .methods(RequestMethod.GET)
                .options(RequestMappingInfo.BuilderConfiguration().apply {
                    patternParser = handlerMapping.patternParser
                })
                .build(),
            this,
            this::makeFile.javaMethod
        )
    }

    fun makeFile(
        response: HttpServletResponse,
        @PathVariable hash: String
    ) {
        response.also {
            it.contentType = "text/css"

            if(devMode) {
                response.setHeader(HttpHeaders.PRAGMA, "no-cache")
                response.setHeader(HttpHeaders.EXPIRES, "0")
                response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0")
            } else {
                response.setDateHeader(HttpHeaders.EXPIRES, System.currentTimeMillis() + TimeUnit.DAYS.toMillis(365))
                response.setHeader(HttpHeaders.CACHE_CONTROL, "public, max-age=" + (365L * 24 * 60 * 60))
            }

        }.outputStream.writer(Charsets.UTF_8).buffered(1 shl 15).use { out ->
            outAll(out)
        }
    }

    private fun outAll(out: java.lang.Appendable) {
        val medias = HashMap<String, ArrayList<Pair<String, CssStyles>>>()

        declarations.values.forEach { cls ->
            appendOneCssClass(out, medias, cls)
        }

        medias.forEach { (media, styles) ->
            appendMedia(out, media, styles)
        }
    }

    private fun appendMedia(out: java.lang.Appendable, media: String, styles: ArrayList<Pair<String, CssStyles>>) {
        out.append("@media(").append(media).append(") {\n")
        styles.forEach { appendOneCssStyle(out, it.first, it.second) }
        out.append("}\n")
    }

    private fun appendOneCssClass(
        out: Appendable,
        medias: HashMap<String, ArrayList<Pair<String, CssStyles>>>,
        cls: CssDeclaration
    ) {
        if(devMode) {
            // updating css-class data to support JVM hot reload in debug mode
            cls.updateDeclaration()
        }

        appendOneCssStyle(out, cls.cssSelector, cls)

        cls.medias?.forEach { (media, style) ->
            if(devMode) {
                // updating css-class data to support JVM hot reload in debug mode
                style.updateStyle()
            }
            medias.computeIfAbsent(media) { ArrayList() }.add(cls.cssSelector to style)
        }
    }

    private fun appendOneCssStyle(out: Appendable, selector: String, styles: CssStyles) {
        styles.style?.let { appendOneCssRule(out, selector, "", it) }
        styles.hover?.let { appendOneCssRule(out, selector, ":hover", it) }
        styles.active?.let { appendOneCssRule(out, selector, ":active", it) }
        styles.focus?.let { appendOneCssRule(out, selector, ":focus", it) }
        styles.visited?.let { appendOneCssRule(out, selector, ":visited", it) }
        styles.firstChild?.let { appendOneCssRule(out, selector, ":first-child", it) }
        styles.lastChild?.let { appendOneCssRule(out, selector, ":last-child", it) }
        styles.checked?.let { appendOneCssRule(out, selector, ":checked", it) }
        styles.disabled?.let { appendOneCssRule(out, selector, ":disabled", it) }
        styles.enabled?.let { appendOneCssRule(out, selector, ":enabled", it) }
        styles.required?.let { appendOneCssRule(out, selector, ":required", it) }
        styles.optional?.let { appendOneCssRule(out, selector, ":optional", it) }
        styles.empty?.let { appendOneCssRule(out, selector, ":empty", it) }
        styles.firstOfType?.let { appendOneCssRule(out, selector, ":first-of-type", it) }
        styles.lastOfType?.let { appendOneCssRule(out, selector, ":last-of-type", it) }
        styles.onlyChild?.let { appendOneCssRule(out, selector, ":only-child", it) }
        styles.onlyOfType?.let { appendOneCssRule(out, selector, ":only-of-type", it) }
        styles.target?.let { appendOneCssRule(out, selector, ":target", it) }

        styles.before?.let { appendOneCssRule(out, selector, "::before", it) }
        styles.after?.let { appendOneCssRule(out, selector, "::after", it) }
        styles.firstLine?.let { appendOneCssRule(out, selector, "::first-line", it) }
        styles.firstLetter?.let { appendOneCssRule(out, selector, "::first-letter", it) }
        styles.selection?.let { appendOneCssRule(out, selector, "::selection", it) }
        styles.placeholder?.let { appendOneCssRule(out, selector, "::placeholder", it) }
        styles.marker?.let { appendOneCssRule(out, selector, "::marker", it) }
        styles.fileSelectorButton?.let { appendOneCssRule(out, selector, "::file-selector-button", it) }

        styles.additional?.forEach { (pseudo, styles) ->
            appendOneCssRule(out, selector, pseudo, styles)
        }
    }

    private fun appendOneCssRule(out: Appendable, selector: String, addition: String, styles: String) {
        out.append(selector).append(addition).append(" {")
            .append(styles.replace('\r', ' ').replace('\n', ' '))
            .append("}\n")
    }

    companion object {
        lateinit var instance: CssHelper
    }
}