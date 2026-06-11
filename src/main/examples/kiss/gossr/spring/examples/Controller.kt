package kiss.gossr.spring.examples

import kiss.gossr.spring.GetRoute
import kiss.gossr.spring.RouteHandler
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.View
import java.time.LocalDateTime

@Controller
class Controller {
    @GetMapping("/some/url")
    fun someUrlEndpoint(): View {
        return HelloWorldPage(
            text = "world",
            time = LocalDateTime.now()
        )
    }

    data class MyGetEndpointRoute(val param: String): GetRoute

    @RouteHandler
    fun myGetEndpoint(route: MyGetEndpointRoute): View {
        return HelloWorldPage(
            text = route.param,
            time = LocalDateTime.now()
        )
    }
}