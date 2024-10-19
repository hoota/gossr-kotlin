package kiss.gossr

@Suppress("FunctionNaming")
class GossRendererTypedSelect<T>(
    val currentSelectedValue: T?
) : GossRenderer() {
    fun OPTION(value: T?, text: String?, disabled: Boolean = false, body: () -> Unit = {}) = EL("OPTION") {
        value((value as? Enum<*>)?.name ?: value?.toString() ?: "")
        selected(value == currentSelectedValue)
        disabled(disabled)
        body()
        +text
    }

    fun OPTION(value: T) = OPTION(value, value.toString())
}

@Suppress("FunctionNaming")
class GossRendererTypedMultiSelect<T>(
    val currentSelectedValues: Collection<T>?
) : GossRenderer() {
    fun OPTION(value: T?, text: String?, disabled: Boolean = false, body: () -> Unit = {}) = EL("OPTION") {
        value((value as? Enum<*>)?.name ?: value?.toString() ?: "")
        selected(currentSelectedValues?.contains(value) == true)
        disabled(disabled)
        body()
        +text
    }

    fun OPTION(value: T) = OPTION(value, value.toString())
}
