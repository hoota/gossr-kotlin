package kiss.gossr.spring.examples

import kiss.gossr.spring.GossSpringRenderer
import kiss.gossr.spring.GossrSpringView
import java.time.LocalDateTime

class HelloWorldPage(
    val text: String,
    val time: LocalDateTime
) : GossSpringRenderer(), GossrSpringView {
    override fun draw() {
        DIV {
            +"Hello $text"
            BR()
            formatDateTime(time)
        }
    }
}