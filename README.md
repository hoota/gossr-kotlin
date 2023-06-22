# Good Old Server Side Rendering for Kotlin

One day I got tired of fixing errors in thymeleaf templates.
I really wanted the UI-generating code to be as clear, reusable, secure, and strictly-typed as the other code in my Kotlin project. I really wanted the refactoring to not cause errors in the HTML templates. And that's how Good Old Server Sire Rendering for Kotlin came about.

```Kotlin
DIV {
    classes("my-css-class")
    +"Hello World"
    BR()
    +formatDateTime(LocalDateTime.now())
}
```

This project has only main Kotlin dependencies.
It does not provide any framework integration.
For Spring users, please, check out [Gossr for Kotlin and Spring](https://github.com/hoota/gossr-kotlin).