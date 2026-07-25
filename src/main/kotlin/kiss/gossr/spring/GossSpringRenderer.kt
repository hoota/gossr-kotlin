package kiss.gossr.spring

import kiss.gossr.DateTimeFormatEurope
import kiss.gossr.DotCommaMoneyFormat
import kiss.gossr.GossRenderer
import kiss.gossr.GossrDateTimeFormatter
import kiss.gossr.GossrMoneyFormatter
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

@Suppress("FunctionNaming")
open class GossSpringRenderer : GossRenderer() {

    fun href(route: GetRoute) = attr("href", RoutesHelper.getRouteUrl(route))

    inline fun <R : Route> FORM(
        route: R,
        checkRouteFieldsExistence: Boolean = true,
        body: (R) -> Unit,
    ) = EL("FORM") {
        action(RoutesHelper.getRouteUrlPath(route))

        val saved = context.formFieldNamesCollectionEnabled

        context.formFieldNamesCollectionEnabled = checkRouteFieldsExistence
        context.formFieldNames.clear()

        when(route) {
            is MultipartPostRoute -> {
                method("POST")
                enctype("multipart/form-data")
            }
            is PostRoute -> {
                method("POST")
            }
            is PutRoute -> {
                method("PUT")
            }
            is DeleteRoute -> {
                method("DELETE")
            }
            else -> method("GET")
        }

        try {
            context.formFieldNames.clear()

            body(route)

            if(route is ProtectedRoute) csrf()?.let {
                HIDDEN(it.first, it.second)
            }
        } finally {
            if(checkRouteFieldsExistence) doCheckFormInputs(route)
            context.formFieldNamesCollectionEnabled = saved
            context.formFieldNames.clear()
        }
    }

    private fun Set<String>.contains(p: KProperty1<*, *>): Boolean = p.name in this ||
        "${p.name}[".let { prefix -> this.any { it.startsWith(prefix) } } ||
        "${p.name}.".let { prefix -> this.any { it.startsWith(prefix) } }

    fun <R : Route> doCheckFormInputs(route: R) {
        RoutesHelper.instance.routes[route.javaClass]?.let { handler ->
            if(!handler.requiredFormProperties.all {
                context.formFieldNames.contains(it)
            }) {
                onRoutePropertyIsMissingInForm(
                    route,
                    handler.requiredFormProperties.filterNot { context.formFieldNames.contains(it) }
                )
            }
        }
    }

    open fun <R : Route> onRoutePropertyIsMissingInForm(
        route: R,
        missingProperties: List<KProperty1<*, *>>
    ) {
        // should be implemented by developer
    }

    fun <T : CssClass> classes(css: KClass<T>) = classes(getCssClassName(css))
    fun <T : CssClass> classes(css: Class<T>) = classes(getCssClassName(css))
    fun classes(css: CssClass) = classes(css.cssClassName)
    fun classes(css1: CssClass, css2: CssClass) = classes("${css1.cssClassName} ${css2.cssClassName}")
    fun classes(css1: CssClass, css2: CssClass, vararg more: CssClass) = classes(more.joinToString(
        prefix = "${css1.cssClassName} ${css2.cssClassName} ",
        separator = " "
    ) { it.cssClassName })

    inline fun <T : CssClass> DIV(css: KClass<T>, body: () -> Unit = {}) = DIV(getCssClassName(css), body)
    inline fun <T : CssClass> SPAN(css: KClass<T>, body: () -> Unit = {}) = SPAN(getCssClassName(css), body)
    inline fun <T : CssClass> BUTTON(css: KClass<T>, body: () -> Unit = {}) = BUTTON(getCssClassName(css), body)
    inline fun <T : CssClass> TABLE(css: KClass<T>, body: () -> Unit = {}) = TABLE(getCssClassName(css), body)
    inline fun <T : CssClass> TH(css: KClass<T>, body: () -> Unit = {}) = TH(getCssClassName(css), body)
    inline fun <T : CssClass> TR(css: KClass<T>, body: () -> Unit = {}) = TR(getCssClassName(css), body)
    inline fun <T : CssClass> TD(css: KClass<T>, body: () -> Unit = {}) = TD(getCssClassName(css), body)
    inline fun <T : CssClass> A(css: KClass<T>, body: () -> Unit = {}) = A(getCssClassName(css), body)
    inline fun <T : CssClass> UL(css: KClass<T>, body: () -> Unit = {}) = UL(getCssClassName(css), body)
    inline fun <T : CssClass> LI(css: KClass<T>, body: () -> Unit = {}) = LI(getCssClassName(css), body)
    inline fun <T : CssClass> INPUT(css: KClass<T>, body: () -> Unit = {}) = INPUT(getCssClassName(css), body)
    inline fun <T : CssClass> LABEL(css: KClass<T>, body: () -> Unit = {}) = LABEL(getCssClassName(css), body)
    inline fun <T : CssClass> CENTER(css: KClass<T>, body: () -> Unit = {}) = CENTER(getCssClassName(css), body)
    inline fun <T : CssClass> SMALL(css: KClass<T>, body: () -> Unit = {}) = SMALL(getCssClassName(css), body)
    inline fun <T : CssClass> STRIKE(css: KClass<T>, body: () -> Unit = {}) = STRIKE(getCssClassName(css), body)
    inline fun <T : CssClass> STRONG(css: KClass<T>, body: () -> Unit = {}) = STRONG(getCssClassName(css), body)
    inline fun <T : CssClass> SUB(css: KClass<T>, body: () -> Unit = {}) = SUB(getCssClassName(css), body)
    inline fun <T : CssClass> SUP(css: KClass<T>, body: () -> Unit = {}) = SUP(getCssClassName(css), body)
    inline fun <T : CssClass> OL(css: KClass<T>, body: () -> Unit = {}) = OL(getCssClassName(css), body)
    inline fun <T : CssClass> TBODY(css: KClass<T>, body: () -> Unit = {}) = TBODY(getCssClassName(css), body)
    inline fun <T : CssClass> THEAD(css: KClass<T>, body: () -> Unit = {}) = THEAD(getCssClassName(css), body)
    inline fun <T : CssClass> TFOOT(css: KClass<T>, body: () -> Unit = {}) = TFOOT(getCssClassName(css), body)
    inline fun <T : CssClass> PRE(css: KClass<T>, body: () -> Unit = {}) = PRE(getCssClassName(css), body)
    inline fun <T : CssClass> TT(css: KClass<T>, body: () -> Unit = {}) = TT(getCssClassName(css), body)
    inline fun <T : CssClass> B(css: KClass<T>, body: () -> Unit = {}) = B(getCssClassName(css), body)
    inline fun <T : CssClass> P(css: KClass<T>, body: () -> Unit = {}) = P(getCssClassName(css), body)
    inline fun <T : CssClass> S(css: KClass<T>, body: () -> Unit = {}) = S(getCssClassName(css), body)
    inline fun <T : CssClass> I(css: KClass<T>, body: () -> Unit = {}) = I(getCssClassName(css), body)
    inline fun <T : CssClass> U(css: KClass<T>, body: () -> Unit = {}) = U(getCssClassName(css), body)
    inline fun <T : CssClass> Q(css: KClass<T>, body: () -> Unit = {}) = Q(getCssClassName(css), body)
    inline fun <T : CssClass> NAV(css: KClass<T>, body: () -> Unit = {}) = NAV(getCssClassName(css), body)
    inline fun <T : CssClass> H1(css: KClass<T>, body: () -> Unit = {}) = H1(getCssClassName(css), body)
    inline fun <T : CssClass> H2(css: KClass<T>, body: () -> Unit = {}) = H2(getCssClassName(css), body)
    inline fun <T : CssClass> H3(css: KClass<T>, body: () -> Unit = {}) = H3(getCssClassName(css), body)
    inline fun <T : CssClass> H4(css: KClass<T>, body: () -> Unit = {}) = H4(getCssClassName(css), body)
    inline fun <T : CssClass> H5(css: KClass<T>, body: () -> Unit = {}) = H5(getCssClassName(css), body)
    inline fun <T : CssClass> H6(css: KClass<T>, body: () -> Unit = {}) = H6(getCssClassName(css), body)
    inline fun <T : CssClass> NOBR(css: KClass<T>, body: () -> Unit = {}) = NOBR(getCssClassName(css), body)
    inline fun <T : CssClass> TEXTAREA(css: KClass<T>, body: () -> Unit = {}) = TEXTAREA(getCssClassName(css), body)
    inline fun <T : CssClass> SECTION(css: KClass<T>, body: () -> Unit = {}) = SECTION(getCssClassName(css), body)
    inline fun <T : CssClass> ARTICLE(css: KClass<T>, body: () -> Unit = {}) = ARTICLE(getCssClassName(css), body)
    inline fun <T : CssClass> ASIDE(css: KClass<T>, body: () -> Unit = {}) = ASIDE(getCssClassName(css), body)
    inline fun <T : CssClass> HEADER(css: KClass<T>, body: () -> Unit = {}) = HEADER(getCssClassName(css), body)
    inline fun <T : CssClass> FOOTER(css: KClass<T>, body: () -> Unit = {}) = FOOTER(getCssClassName(css), body)
    inline fun <T : CssClass> MAIN(css: KClass<T>, body: () -> Unit = {}) = MAIN(getCssClassName(css), body)
    inline fun <T : CssClass> FIGURE(css: KClass<T>, body: () -> Unit = {}) = FIGURE(getCssClassName(css), body)
    inline fun <T : CssClass> FIGCAPTION(css: KClass<T>, body: () -> Unit = {}) = FIGCAPTION(getCssClassName(css), body)
    inline fun <T : CssClass> FIELDSET(css: KClass<T>, body: () -> Unit = {}) = FIELDSET(getCssClassName(css), body)
    inline fun <T : CssClass> LEGEND(css: KClass<T>, body: () -> Unit = {}) = LEGEND(getCssClassName(css), body)
    inline fun <T : CssClass> EM(css: KClass<T>, body: () -> Unit = {}) = EM(getCssClassName(css), body)
    inline fun <T : CssClass> BLOCKQUOTE(css: KClass<T>, body: () -> Unit = {}) = BLOCKQUOTE(getCssClassName(css), body)
    inline fun <T : CssClass> CODE(css: KClass<T>, body: () -> Unit = {}) = CODE(getCssClassName(css), body)
    inline fun <T : CssClass> MARK(css: KClass<T>, body: () -> Unit = {}) = MARK(getCssClassName(css), body)
    inline fun <T : CssClass> CITE(css: KClass<T>, body: () -> Unit = {}) = CITE(getCssClassName(css), body)
    inline fun <T : CssClass> DL(css: KClass<T>, body: () -> Unit = {}) = DL(getCssClassName(css), body)
    inline fun <T : CssClass> DT(css: KClass<T>, body: () -> Unit = {}) = DT(getCssClassName(css), body)
    inline fun <T : CssClass> DD(css: KClass<T>, body: () -> Unit = {}) = DD(getCssClassName(css), body)

    inline fun DIV(css: CssClass, body: () -> Unit = {}) = DIV(css.cssClassName, body)
    inline fun DIV(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = DIV { classes(css1, css2); body() }
    inline fun DIV(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = DIV { classes(css1, css2, *more); body() }
    inline fun SPAN(css: CssClass, body: () -> Unit = {}) = SPAN(css.cssClassName, body)
    inline fun SPAN(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = SPAN { classes(css1, css2); body() }
    inline fun SPAN(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = SPAN { classes(css1, css2, *more); body() }
    inline fun BUTTON(css: CssClass, body: () -> Unit = {}) = BUTTON(css.cssClassName, body)
    inline fun BUTTON(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = BUTTON { classes(css1, css2); body() }
    inline fun BUTTON(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = BUTTON { classes(css1, css2, *more); body() }
    inline fun TABLE(css: CssClass, body: () -> Unit = {}) = TABLE(css.cssClassName, body)
    inline fun TABLE(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = TABLE { classes(css1, css2); body() }
    inline fun TABLE(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = TABLE { classes(css1, css2, *more); body() }
    inline fun TH(css: CssClass, body: () -> Unit = {}) = TH(css.cssClassName, body)
    inline fun TH(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = TH { classes(css1, css2); body() }
    inline fun TH(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = TH { classes(css1, css2, *more); body() }
    inline fun TR(css: CssClass, body: () -> Unit = {}) = TR(css.cssClassName, body)
    inline fun TR(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = TR { classes(css1, css2); body() }
    inline fun TR(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = TR { classes(css1, css2, *more); body() }
    inline fun TD(css: CssClass, body: () -> Unit = {}) = TD(css.cssClassName, body)
    inline fun TD(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = TD { classes(css1, css2); body() }
    inline fun TD(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = TD { classes(css1, css2, *more); body() }
    inline fun A(css: CssClass, body: () -> Unit = {}) = A(css.cssClassName, body)
    inline fun A(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = A { classes(css1, css2); body() }
    inline fun A(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = A { classes(css1, css2, *more); body() }
    inline fun UL(css: CssClass, body: () -> Unit = {}) = UL(css.cssClassName, body)
    inline fun UL(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = UL { classes(css1, css2); body() }
    inline fun UL(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = UL { classes(css1, css2, *more); body() }
    inline fun LI(css: CssClass, body: () -> Unit = {}) = LI(css.cssClassName, body)
    inline fun LI(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = LI { classes(css1, css2); body() }
    inline fun LI(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = LI { classes(css1, css2, *more); body() }
    inline fun INPUT(css: CssClass, body: () -> Unit = {}) = INPUT(css.cssClassName, body)
    inline fun INPUT(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = INPUT { classes(css1, css2); body() }
    inline fun INPUT(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = INPUT { classes(css1, css2, *more); body() }
    inline fun LABEL(css: CssClass, body: () -> Unit = {}) = LABEL(css.cssClassName, body)
    inline fun LABEL(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = LABEL { classes(css1, css2); body() }
    inline fun LABEL(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = LABEL { classes(css1, css2, *more); body() }
    inline fun CENTER(css: CssClass, body: () -> Unit = {}) = CENTER(css.cssClassName, body)
    inline fun CENTER(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = CENTER { classes(css1, css2); body() }
    inline fun CENTER(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = CENTER { classes(css1, css2, *more); body() }
    inline fun SMALL(css: CssClass, body: () -> Unit = {}) = SMALL(css.cssClassName, body)
    inline fun SMALL(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = SMALL { classes(css1, css2); body() }
    inline fun SMALL(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = SMALL { classes(css1, css2, *more); body() }
    inline fun STRIKE(css: CssClass, body: () -> Unit = {}) = STRIKE(css.cssClassName, body)
    inline fun STRIKE(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = STRIKE { classes(css1, css2); body() }
    inline fun STRIKE(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = STRIKE { classes(css1, css2, *more); body() }
    inline fun STRONG(css: CssClass, body: () -> Unit = {}) = STRONG(css.cssClassName, body)
    inline fun STRONG(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = STRONG { classes(css1, css2); body() }
    inline fun STRONG(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = STRONG { classes(css1, css2, *more); body() }
    inline fun SUB(css: CssClass, body: () -> Unit = {}) = SUB(css.cssClassName, body)
    inline fun SUB(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = SUB { classes(css1, css2); body() }
    inline fun SUB(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = SUB { classes(css1, css2, *more); body() }
    inline fun SUP(css: CssClass, body: () -> Unit = {}) = SUP(css.cssClassName, body)
    inline fun SUP(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = SUP { classes(css1, css2); body() }
    inline fun SUP(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = SUP { classes(css1, css2, *more); body() }
    inline fun OL(css: CssClass, body: () -> Unit = {}) = OL(css.cssClassName, body)
    inline fun OL(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = OL { classes(css1, css2); body() }
    inline fun OL(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = OL { classes(css1, css2, *more); body() }
    inline fun TBODY(css: CssClass, body: () -> Unit = {}) = TBODY(css.cssClassName, body)
    inline fun TBODY(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = TBODY { classes(css1, css2); body() }
    inline fun TBODY(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = TBODY { classes(css1, css2, *more); body() }
    inline fun THEAD(css: CssClass, body: () -> Unit = {}) = THEAD(css.cssClassName, body)
    inline fun THEAD(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = THEAD { classes(css1, css2); body() }
    inline fun THEAD(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = THEAD { classes(css1, css2, *more); body() }
    inline fun TFOOT(css: CssClass, body: () -> Unit = {}) = TFOOT(css.cssClassName, body)
    inline fun TFOOT(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = TFOOT { classes(css1, css2); body() }
    inline fun TFOOT(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = TFOOT { classes(css1, css2, *more); body() }
    inline fun PRE(css: CssClass, body: () -> Unit = {}) = PRE(css.cssClassName, body)
    inline fun PRE(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = PRE { classes(css1, css2); body() }
    inline fun PRE(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = PRE { classes(css1, css2, *more); body() }
    inline fun TT(css: CssClass, body: () -> Unit = {}) = TT(css.cssClassName, body)
    inline fun TT(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = TT { classes(css1, css2); body() }
    inline fun TT(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = TT { classes(css1, css2, *more); body() }
    inline fun B(css: CssClass, body: () -> Unit = {}) = B(css.cssClassName, body)
    inline fun B(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = B { classes(css1, css2); body() }
    inline fun B(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = B { classes(css1, css2, *more); body() }
    inline fun P(css: CssClass, body: () -> Unit = {}) = P(css.cssClassName, body)
    inline fun P(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = P { classes(css1, css2); body() }
    inline fun P(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = P { classes(css1, css2, *more); body() }
    inline fun S(css: CssClass, body: () -> Unit = {}) = S(css.cssClassName, body)
    inline fun S(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = S { classes(css1, css2); body() }
    inline fun S(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = S { classes(css1, css2, *more); body() }
    inline fun I(css: CssClass, body: () -> Unit = {}) = I(css.cssClassName, body)
    inline fun I(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = I { classes(css1, css2); body() }
    inline fun I(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = I { classes(css1, css2, *more); body() }
    inline fun U(css: CssClass, body: () -> Unit = {}) = U(css.cssClassName, body)
    inline fun U(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = U { classes(css1, css2); body() }
    inline fun U(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = U { classes(css1, css2, *more); body() }
    inline fun Q(css: CssClass, body: () -> Unit = {}) = Q(css.cssClassName, body)
    inline fun Q(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = Q { classes(css1, css2); body() }
    inline fun Q(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = Q { classes(css1, css2, *more); body() }
    inline fun NAV(css: CssClass, body: () -> Unit = {}) = NAV(css.cssClassName, body)
    inline fun NAV(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = NAV { classes(css1, css2); body() }
    inline fun NAV(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = NAV { classes(css1, css2, *more); body() }
    inline fun H1(css: CssClass, body: () -> Unit = {}) = H1(css.cssClassName, body)
    inline fun H1(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = H1 { classes(css1, css2); body() }
    inline fun H1(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = H1 { classes(css1, css2, *more); body() }
    inline fun H2(css: CssClass, body: () -> Unit = {}) = H2(css.cssClassName, body)
    inline fun H2(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = H2 { classes(css1, css2); body() }
    inline fun H2(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = H2 { classes(css1, css2, *more); body() }
    inline fun H3(css: CssClass, body: () -> Unit = {}) = H3(css.cssClassName, body)
    inline fun H3(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = H3 { classes(css1, css2); body() }
    inline fun H3(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = H3 { classes(css1, css2, *more); body() }
    inline fun H4(css: CssClass, body: () -> Unit = {}) = H4(css.cssClassName, body)
    inline fun H4(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = H4 { classes(css1, css2); body() }
    inline fun H4(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = H4 { classes(css1, css2, *more); body() }
    inline fun H5(css: CssClass, body: () -> Unit = {}) = H5(css.cssClassName, body)
    inline fun H5(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = H5 { classes(css1, css2); body() }
    inline fun H5(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = H5 { classes(css1, css2, *more); body() }
    inline fun H6(css: CssClass, body: () -> Unit = {}) = H6(css.cssClassName, body)
    inline fun H6(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = H6 { classes(css1, css2); body() }
    inline fun H6(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = H6 { classes(css1, css2, *more); body() }
    inline fun NOBR(css: CssClass, body: () -> Unit = {}) = NOBR(css.cssClassName, body)
    inline fun NOBR(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = NOBR { classes(css1, css2); body() }
    inline fun NOBR(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = NOBR { classes(css1, css2, *more); body() }
    inline fun TEXTAREA(css: CssClass, body: () -> Unit = {}) = TEXTAREA(css.cssClassName, body)
    inline fun TEXTAREA(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = TEXTAREA { classes(css1, css2); body() }
    inline fun TEXTAREA(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = TEXTAREA { classes(css1, css2, *more); body() }
    inline fun SECTION(css: CssClass, body: () -> Unit = {}) = SECTION(css.cssClassName, body)
    inline fun SECTION(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = SECTION { classes(css1, css2); body() }
    inline fun SECTION(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = SECTION { classes(css1, css2, *more); body() }
    inline fun ARTICLE(css: CssClass, body: () -> Unit = {}) = ARTICLE(css.cssClassName, body)
    inline fun ARTICLE(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = ARTICLE { classes(css1, css2); body() }
    inline fun ARTICLE(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = ARTICLE { classes(css1, css2, *more); body() }
    inline fun ASIDE(css: CssClass, body: () -> Unit = {}) = ASIDE(css.cssClassName, body)
    inline fun ASIDE(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = ASIDE { classes(css1, css2); body() }
    inline fun ASIDE(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = ASIDE { classes(css1, css2, *more); body() }
    inline fun HEADER(css: CssClass, body: () -> Unit = {}) = HEADER(css.cssClassName, body)
    inline fun HEADER(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = HEADER { classes(css1, css2); body() }
    inline fun HEADER(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = HEADER { classes(css1, css2, *more); body() }
    inline fun FOOTER(css: CssClass, body: () -> Unit = {}) = FOOTER(css.cssClassName, body)
    inline fun FOOTER(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = FOOTER { classes(css1, css2); body() }
    inline fun FOOTER(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = FOOTER { classes(css1, css2, *more); body() }
    inline fun MAIN(css: CssClass, body: () -> Unit = {}) = MAIN(css.cssClassName, body)
    inline fun MAIN(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = MAIN { classes(css1, css2); body() }
    inline fun MAIN(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = MAIN { classes(css1, css2, *more); body() }
    inline fun FIGURE(css: CssClass, body: () -> Unit = {}) = FIGURE(css.cssClassName, body)
    inline fun FIGURE(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = FIGURE { classes(css1, css2); body() }
    inline fun FIGURE(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = FIGURE { classes(css1, css2, *more); body() }
    inline fun FIGCAPTION(css: CssClass, body: () -> Unit = {}) = FIGCAPTION(css.cssClassName, body)
    inline fun FIGCAPTION(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = FIGCAPTION { classes(css1, css2); body() }
    inline fun FIGCAPTION(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = FIGCAPTION { classes(css1, css2, *more); body() }
    inline fun FIELDSET(css: CssClass, body: () -> Unit = {}) = FIELDSET(css.cssClassName, body)
    inline fun FIELDSET(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = FIELDSET { classes(css1, css2); body() }
    inline fun FIELDSET(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = FIELDSET { classes(css1, css2, *more); body() }
    inline fun LEGEND(css: CssClass, body: () -> Unit = {}) = LEGEND(css.cssClassName, body)
    inline fun LEGEND(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = LEGEND { classes(css1, css2); body() }
    inline fun LEGEND(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = LEGEND { classes(css1, css2, *more); body() }
    inline fun EM(css: CssClass, body: () -> Unit = {}) = EM(css.cssClassName, body)
    inline fun EM(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = EM { classes(css1, css2); body() }
    inline fun EM(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = EM { classes(css1, css2, *more); body() }
    inline fun BLOCKQUOTE(css: CssClass, body: () -> Unit = {}) = BLOCKQUOTE(css.cssClassName, body)
    inline fun BLOCKQUOTE(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = BLOCKQUOTE { classes(css1, css2); body() }
    inline fun BLOCKQUOTE(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = BLOCKQUOTE { classes(css1, css2, *more); body() }
    inline fun CODE(css: CssClass, body: () -> Unit = {}) = CODE(css.cssClassName, body)
    inline fun CODE(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = CODE { classes(css1, css2); body() }
    inline fun CODE(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = CODE { classes(css1, css2, *more); body() }
    inline fun MARK(css: CssClass, body: () -> Unit = {}) = MARK(css.cssClassName, body)
    inline fun MARK(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = MARK { classes(css1, css2); body() }
    inline fun MARK(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = MARK { classes(css1, css2, *more); body() }
    inline fun CITE(css: CssClass, body: () -> Unit = {}) = CITE(css.cssClassName, body)
    inline fun CITE(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = CITE { classes(css1, css2); body() }
    inline fun CITE(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = CITE { classes(css1, css2, *more); body() }
    inline fun DL(css: CssClass, body: () -> Unit = {}) = DL(css.cssClassName, body)
    inline fun DL(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = DL { classes(css1, css2); body() }
    inline fun DL(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = DL { classes(css1, css2, *more); body() }
    inline fun DT(css: CssClass, body: () -> Unit = {}) = DT(css.cssClassName, body)
    inline fun DT(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = DT { classes(css1, css2); body() }
    inline fun DT(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = DT { classes(css1, css2, *more); body() }
    inline fun DD(css: CssClass, body: () -> Unit = {}) = DD(css.cssClassName, body)
    inline fun DD(css1: CssClass, css2: CssClass, body: () -> Unit = {}) = DD { classes(css1, css2); body() }
    inline fun DD(css1: CssClass, css2: CssClass, vararg more: CssClass, body: () -> Unit = {}) = DD { classes(css1, css2, *more); body() }


    companion object {
        private const val dateFormatAttributeKey = "FTGossRenderer.dateFormatAttributeKey"
        private const val moneyFormatAttributeKey = "FTGossRenderer.moneyFormatAttributeKey"

        fun request() = (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.request
        fun response() = (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.response

        fun requestHost(): String? = request()?.requestURL?.replace(Regex("(https?://[^/]+)/.*"), "$1")

        fun setMoneyFormats(f: GossrMoneyFormatter?) {
            val request = request()
            request?.setAttribute(moneyFormatAttributeKey, f)
            request?.session?.setAttribute(moneyFormatAttributeKey, f)
        }

        fun getMoneyFormats(): GossrMoneyFormatter = (request()?.let { r ->
            r.getAttribute(moneyFormatAttributeKey) ?: r.getSession(false)?.getAttribute(moneyFormatAttributeKey)
        } ?: DotCommaMoneyFormat) as GossrMoneyFormatter

        fun setDateTimeFormats(f: GossrDateTimeFormatter?) {
            val request = request()
            request?.setAttribute(dateFormatAttributeKey, f)
            request?.session?.setAttribute(dateFormatAttributeKey, f)
        }

        fun getDateTimeFormats(): GossrDateTimeFormatter = (request()?.let { r ->
            r.getAttribute(dateFormatAttributeKey) ?: r.getSession(false)?.getAttribute(dateFormatAttributeKey)
        } ?: DateTimeFormatEurope) as GossrDateTimeFormatter

        fun isAuthenticated(): Boolean =
            request()?.userPrincipal?.name != null
    }
}