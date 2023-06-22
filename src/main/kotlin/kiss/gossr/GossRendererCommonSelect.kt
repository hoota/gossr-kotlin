package kiss.gossr

import java.util.*

@Suppress("FunctionNaming", "MethodOverloading")
class GossRendererCommonSelect : GossRenderer() {
    fun OPTION(value: Boolean?, text: String?, selected: Boolean = false) = EL("OPTION") {
        value(value)
        selected(selected)
        +text
    }
    fun OPTION(value: Int?, text: String?, selected: Boolean = false) = EL("OPTION") {
        value(value)
        selected(selected)
        +text
    }
    fun OPTION(value: Long?, text: String?, selected: Boolean = false) = EL("OPTION") {
        value(value)
        selected(selected)
        +text
    }
    fun OPTION(value: String?, text: String?, selected: Boolean = false) = EL("OPTION") {
        value(value)
        selected(selected)
        +text
    }
    fun OPTION(value: UUID?, text: String?, selected: Boolean = false) = EL("OPTION") {
        value(value)
        selected(selected)
        +text
    }
    fun OPTION(value: Enum<*>?, text: String?, selected: Boolean = false) = EL("OPTION") {
        value(value)
        selected(selected)
        +text
    }
}
