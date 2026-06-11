package kiss.gossr.spring

import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.format.FormatterRegistry
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.View
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.time.LocalDate
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.reflect.KProperty1
import kotlin.test.Test
import kotlin.test.assertEquals

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = ["kiss.gossr.spring"])
open class TestApplicationConfig : WebMvcConfigurer {

    @Bean
    open fun mockMvc(webApplicationContext: WebApplicationContext): MockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext).build()

    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverter(LocalDateRequestParamConverter())
    }
}

@Component
object Button : CssClass({
    style = "color: white;"
    hover = "color: black;"

    add(">a", "color: green;")

    media("max-width: 991px") {
        style = "color: red;"
    }
})

@Component
class CustomCss : CssClass({
    cssClassName = "custom"
    style = "display: none;"
})

@Component
class DivStyles : CssTag("div", {
    style = "color: black;"
    media("max-width: 991px") {
        style = "color: green;"
    }
})

@Component
class AccessibilityChecker : RouteAccessibilityChecker {
    override fun isAccessible(
        request: HttpServletRequest,
        handlerInfo: RoutesHelper.HandlerInfo
    ): Pair<Int, String>? {
        return HttpServletResponse.SC_NOT_FOUND to "not found"
    }
}

@Component
@RouteHandler
@AccessibleIf(AccessibilityChecker::class)
class NotAccessibleController {
    class RandomRoute : GetRoute

    @RouteHandler
    fun random(route: RandomRoute) {
        TODO("should never run this")
    }
}

@Component
@RouteHandler
class TestRouteHandler {
    class CssTestRoute : GetRoute

    @RouteHandler
    fun cssTest(route: CssTestRoute): View = object : GossSpringRenderer(), GossrSpringView {
        override fun draw() {
            DIV(Button) {
                +"Click Me"
            }
            DIV(CustomCss::class) {
                +"OK"
            }
        }
    }

    data class SimpleRoute(
        @PathProperty
        val a: Int,
        val b: String,
        val c: UUID,
        val d: Double? = null,
        val e: List<Int>
    ) : GetRoute

    @RouteHandler
    fun getRouteHandler(route: SimpleRoute): View {
        return object : GossSpringRenderer(), GossrSpringView {
            override fun draw() {
                DIV { + route.a.toString() }
                DIV { + route.b }
                DIV { + route.c }
                DIV { + route.e.joinToString("-")}
                SPAN { +"GET" }
            }
        }
    }

    data class SimplePostRoute(
        val a: Int,
        val b: Double,
        val c: LocalDate?
    ) : PostRoute

    @RouteHandler
    fun postRouteHandler(route: SimplePostRoute): View {
        return object : GossSpringRenderer(), GossrSpringView {
            override fun draw() {
                DIV { + route.a.toString() }
                DIV { + formatMoney2(route.b) }
                DIV { + formatDate(route.c) }
                SPAN { +"POST" }
            }
        }
    }

    class SimplePutRoute : PutRoute

    @RouteHandler
    fun putRouteHandler(route: SimplePutRoute): View {
        return object : GossSpringRenderer(), GossrSpringView {
            override fun draw() {
                SPAN { +"PUT" }
            }
        }
    }

    class SimpleDeleteRoute : DeleteRoute

    @RouteHandler
    fun delRouteHandler(route: SimpleDeleteRoute): View {
        return object : GossSpringRenderer(), GossrSpringView {
            override fun draw() {
                SPAN { +"DELETE" }
            }
        }
    }

    class SimpleMultiRoute : GetRoute, PostRoute

    @RouteHandler
    fun multiRouteHandler(
        request: HttpServletRequest,
        route: SimpleMultiRoute
    ): View {
        return object : GossSpringRenderer(), GossrSpringView {
            override fun draw() {
                SPAN { +request.method }
            }
        }
    }

    class CheckFormRouteFieldsRoute : GetRoute

    data class NestedParam(val v: Int = 0)

    data class MissingFormRouteFieldRoute(
        @PathProperty
        val pathProperty: Int,
        val myProperty: Long,
        val propertyWithDefault: Int = 0,
        val nullableProperty: String?,
        @ForceFormFieldPresentCheck
        val forcedNullablePropertyWithDefault: String? = "",

        var justMap: Map<String, String> = emptyMap(),
        @ForceFormFieldPresentCheck
        var forcedMap: Map<String, String> = emptyMap(),
        @ForceFormFieldPresentCheck
        var forcedMissingMap: Map<String, String> = emptyMap(),

        var justList: List<String> = emptyList(),
        @ForceFormFieldPresentCheck
        var forcedList: List<String> = emptyList(),
        @ForceFormFieldPresentCheck
        var forcedMissingList: List<String> = emptyList(),

        var justNested: NestedParam = NestedParam(),
        @ForceFormFieldPresentCheck
        var forcedNested: NestedParam = NestedParam(),
        @ForceFormFieldPresentCheck
        var forcedMissingNested: NestedParam = NestedParam(),

    ): PostRoute

    @RouteHandler
    fun checkFormRouteFields(route: CheckFormRouteFieldsRoute): View {
        return object : GossSpringRenderer(), GossrSpringView {
            override fun draw() {
                newLineAfterTagClose(false) {
                    FORM(MissingFormRouteFieldRoute(
                        pathProperty = 0,
                        myProperty = 0,
                        nullableProperty = null
                    )) { r ->
                        namePrefix(r::forcedMap) {
                            HIDDEN("a", "b")
                        }

                        HIDDEN(r::forcedList)

                        namePrefix(r::forcedNested) {
                            HIDDEN(r.forcedNested::v)
                        }
                    }
                }
            }

            override fun <R : Route> onRoutePropertyIsMissingInForm(
                route: R,
                missingProperties: List<KProperty1<*, *>>
            ) {
                +"Missing form fields: ${missingProperties.joinToString { it.name }}"
            }
        }
    }

    @RouteHandler
    fun missingFormRouteField(route: MissingFormRouteFieldRoute) {

    }

    @AccessibleIf(AccessibilityChecker::class)
    class AccessibilityCheckRoute : GetRoute

    @RouteHandler
    fun accessibilityCheck(route: AccessibilityCheckRoute) {
        TODO("should never run this")
    }
}

@RunWith(SpringJUnit4ClassRunner::class)
@WebAppConfiguration
@ContextConfiguration(classes = [TestApplicationConfig::class])
class Tests {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun accessibilityCheck() {
        mockMvc.perform(MockMvcRequestBuilders.get(RoutesHelper.buildRouteUri(TestRouteHandler.AccessibilityCheckRoute())))
            .andExpect(MockMvcResultMatchers.status().isNotFound)

        mockMvc.perform(MockMvcRequestBuilders.get(RoutesHelper.buildRouteUri(NotAccessibleController.RandomRoute())))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun testCss() {
        mockMvc.perform(MockMvcRequestBuilders.get(RoutesHelper.buildRouteUri(TestRouteHandler.CssTestRoute())))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("<DIV class=\"gossr-0\">Click Me</DIV>\n<DIV class=\"custom\">OK</DIV>\n"))

        assertEquals("/assets/gossr-styles-057c7f9d741bb5e67c189d3d7026a21e.css", CssHelper.instance.getUrl())

        mockMvc.perform(MockMvcRequestBuilders.get(CssHelper.instance.getUrl()))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string(".gossr-0 {color: white;}\n.gossr-0:hover {color: black;}\n.gossr-0>a {color: green;}\n.custom {display: none;}\ndiv {color: black;}\n@media(max-width: 991px) {\n.gossr-0 {color: red;}\ndiv {color: green;}\n}\n"))
    }

    @Test
    fun testGet() {
        val route = TestRouteHandler.SimpleRoute(a = 123, b = "Hello", c = UUID.randomUUID(), e = listOf(1,2,3))
        val url = RoutesHelper.buildRouteUri(route)

        assertEquals("/simple/123?b=Hello&c=${route.c}&e=1&e=2&e=3", url)

        mockMvc.perform(MockMvcRequestBuilders.get(url))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("<DIV>123</DIV>\n" +
                "<DIV>Hello</DIV>\n" +
                "<DIV>${route.c}</DIV>\n" +
                "<DIV>1-2-3</DIV>\n" +
                "<SPAN>GET</SPAN>\n")
            )
    }

    @Test
    fun testPost() {
        val today = LocalDate.now()
        val route = TestRouteHandler.SimplePostRoute(a = 123, b = 1234.5678, c = today)
        val url = RoutesHelper.buildRouteUriPath(route)

        assertEquals("/simple/post", url)

        mockMvc.perform(
            MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("a", route.a.toString())
                .param("b", route.b.toString())
                .param("c", route.c.toString())
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("<DIV>123</DIV>\n" +
                "<DIV>1234.57</DIV>\n"+
                "<DIV>$today</DIV>\n"+
                "<SPAN>POST</SPAN>\n")
            )
    }

    @Test
    fun testPut() {
        val route = TestRouteHandler.SimplePutRoute()
        val url = RoutesHelper.buildRouteUriPath(route)

        assertEquals("/simple/put", url)

        mockMvc.perform(
            MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("<SPAN>PUT</SPAN>\n"))
    }

    @Test
    fun testDel() {
        val route = TestRouteHandler.SimpleDeleteRoute()
        val url = RoutesHelper.buildRouteUriPath(route)

        assertEquals("/simple/delete", url)

        mockMvc.perform(
            MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("<SPAN>DELETE</SPAN>\n"))
    }

    @Test
    fun missingFormFieldsTest() {
        val res = mockMvc.perform(
            MockMvcRequestBuilders.get(
                RoutesHelper.buildRouteUriPath(TestRouteHandler.CheckFormRouteFieldsRoute())
            )
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("""<FORM action="/missing/form/route/field/0" method="POST"><INPUT type="hidden" name="forcedMap[a]" value="b"/><INPUT type="hidden" name="forcedList" value="[]"/><INPUT type="hidden" name="forcedNested.v" value="0"/>Missing form fields: myProperty, forcedNullablePropertyWithDefault, forcedMissingMap, forcedMissingList, forcedMissingNested</FORM>"""))
    }

    @Test
    fun testMulti() {
        val route = TestRouteHandler.SimpleMultiRoute()
        val url = RoutesHelper.buildRouteUriPath(route)

        assertEquals("/simple/multi", url)

        mockMvc.perform(
            MockMvcRequestBuilders.get(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("<SPAN>GET</SPAN>\n"))

        mockMvc.perform(
            MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("<SPAN>POST</SPAN>\n"))

        mockMvc.perform(
            MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
            .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed)

        mockMvc.perform(
            MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
            .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed)
    }
}