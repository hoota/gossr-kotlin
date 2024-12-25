package kiss.gossr

@Suppress("FunctionNaming", "MethodOverloading")
class GossRendererCommonSelect : GossRenderer() {
    fun OPTION(value: Any?, title: String?, selected: Boolean = false) = EL("OPTION") {
        value(value)
        selected(selected)
        +title
    }
}
