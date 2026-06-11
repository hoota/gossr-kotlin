package kiss.gossr.spring

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.aop.support.AopUtils
import org.springframework.beans.InvalidPropertyException
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.handler.AbstractHandlerMapping
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.lang.reflect.Method
import java.net.URLEncoder
import java.nio.charset.Charset
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor

interface Route

interface GetRoute : Route
/** request requires csrf */
interface ProtectedRoute : Route
interface PostRoute : ProtectedRoute
interface MultipartPostRoute : PostRoute
interface PutRoute : ProtectedRoute
interface DeleteRoute : ProtectedRoute

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RouteHandler(
    val description: String = ""
)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class PathPrefix(
    val value: String
)

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class PathProperty

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class ForceFormFieldPresentCheck

interface RouteAccessibilityChecker {
    fun isAccessible(
        request: HttpServletRequest,
        handlerInfo: RoutesHelper.HandlerInfo
    ): Pair<Int, String>?
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class AccessibleIf(
    val value: KClass<out RouteAccessibilityChecker>
)

@Component
class RoutesHelper(
    val applicationContext: ApplicationContext,
    val handlerMapping: RequestMappingHandlerMapping,
    val pathPrefix: String = "/"
) {
    val log: Logger = LoggerFactory.getLogger(javaClass)
    val routes = HashMap<Class<*>, HandlerInfo>()
    val methods = HashMap<Method?, HandlerInfo>()

    class HandlerInfo(
        val requestMethods: List<RequestMethod>,
        val routeClass: Class<out Route>,
        val pathPrefix: String,
        val pathProperties: List<KProperty1<*, *>>,
        val paramProperties: List<KProperty1<*, *>>,
        val requiredFormProperties: List<KProperty1<*, *>>,
        val bean: Any?,
        val method: Method?,
        val annotations: Array<Annotation>,
        val conditions: List<RouteAccessibilityChecker>?,
    ) {
        val binding: String get() = pathPrefix + pathProperties.joinToString("") { "/{${it.name}}" }
    }

    @PostConstruct
    fun initMappings() {
        instance = this

        registerInterceptor()

        applicationContext.getBeansWithAnnotation(RouteHandler::class.java).forEach { (name, bean) ->
            bean.javaClass.declaredMethods.forEach { m ->
                processMethod(bean, m)
            }
        }

        registerMappings()
    }

    private fun registerInterceptor() {
        val handlerMapping = applicationContext.getBean(RequestMappingHandlerMapping::class.java)

        // adaptedInterceptors — protected field
        val field = AbstractHandlerMapping::class.java.getDeclaredField("adaptedInterceptors")

        field.isAccessible = true

        @Suppress("UNCHECKED_CAST")
        val interceptors = field.get(handlerMapping) as MutableList<Any>

        interceptors.add(RoutesHandlerInterceptor())
    }

    private fun registerMappings() {
        val options = RequestMappingInfo.BuilderConfiguration().apply {
            patternParser = handlerMapping.patternParser
        }

        log.info("using ${options.patternParser?.javaClass?.simpleName ?: "AntPathMatcher"} to bind GOSSR endpoints")

        routes.values.forEach { r ->
            val binding = r.binding
            log.info("${r.routeClass.simpleName} is handled on ${r.requestMethods} :: $binding")

            handlerMapping.registerMapping(
                RequestMappingInfo
                    .paths(binding)
                    .methods(*r.requestMethods.toTypedArray())
                    .options(options)
                    .build(),
                r.bean,
                r.method
            )

            methods[r.method] = r
        }
    }

    private fun processMethod(bean: Any, m: Method) {
        val m = AopUtils.getMostSpecificMethod(m, bean.javaClass)
        if(!m.isAnnotationPresent(RouteHandler::class.java)) return

        if(m.parameters.count { Route::class.java.isAssignableFrom(it.type) } != 1) {
            throw IllegalStateException("${m.declaringClass.simpleName}::${m.name} should have single Route parameter")
        }

        (m.parameters.firstOrNull {
            Route::class.java.isAssignableFrom(it.type)
        }?.type as? Class<Route>)?.let { type ->
            addRoute(bean, m, type)
        }
    }

    fun <T : Route> addRoute(bean: Any, method: Method, type: Class<T>) {
        val requestMethods = listOfNotNull(
            if(GetRoute::class.java.isAssignableFrom(type)) RequestMethod.GET else null,
            if(PostRoute::class.java.isAssignableFrom(type)) RequestMethod.POST else null,
            if(PutRoute::class.java.isAssignableFrom(type)) RequestMethod.PUT else null,
            if(DeleteRoute::class.java.isAssignableFrom(type)) RequestMethod.DELETE else null,
        )

        if(routes.contains(type)) {
            throw IllegalStateException("${type.name} route should be handled by one method only")
        }

        if(requestMethods.count { it != RequestMethod.GET } > 1) {
            throw IllegalStateException("${type.name} route should be GET, GET/POST, GET/PUT or GET/DELETE")
        }

        val constructorParams = type.kotlin.primaryConstructor?.parameters ?: emptyList()

        val properties = type.kotlin.declaredMemberProperties.sortedBy { p ->
            var index = constructorParams.indexOfFirst { it.name == p.name }
            if(index < 0) index = type.declaredFields.indexOfFirst { it.name == p.name }
            if(index < 0) Int.MAX_VALUE else index
        }.groupBy { p ->
            p.annotations.plus(constructorParams.firstOrNull { it.name == p.name }?.annotations ?: emptyList()).any { it is PathProperty }
        }

        routes[type] = HandlerInfo(
            requestMethods = requestMethods,
            routeClass = type,
            pathPrefix = getRouteUrlPathPrefix(type),
            pathProperties = properties[true] ?: emptyList(),
            paramProperties = properties[false] ?: emptyList(),
            requiredFormProperties = properties[false]
                ?.filter { p ->
                    val cp = constructorParams.firstOrNull { it.name == p.name }

                    val force = p.annotations.plus(cp?.annotations ?: emptyList()).any { it is ForceFormFieldPresentCheck }
                    val nullable = p.returnType.isMarkedNullable
                    val optional = cp?.isOptional ?: false

                    force || (!nullable && !optional)
                } ?: emptyList(),
            bean = bean,
            method = method,
            annotations = method.annotations ?: emptyArray(),
            conditions = getConditions(bean, type)
        )
    }

    private fun getConditions(bean: Any, routeClass: Class<*>): List<RouteAccessibilityChecker>? {
        return listOfNotNull(
            AopUtils.getTargetClass(bean).getAnnotation(AccessibleIf::class.java),
            routeClass.annotations.firstNotNullOfOrNull { it as? AccessibleIf }
        ).map { a ->
            applicationContext.getBean(a.value.java) ?: throw InvalidPropertyException(
                routeClass,
                "@EnabledIf",
                "Spring Bean not found: ${routeClass.name}"
            )
        }.ifEmpty { null }
    }

    private fun getRouteUrlPathPrefix(routeClass: Class<*>): String {
        routeClass.annotations.firstNotNullOfOrNull { it as? PathPrefix }?.value?.let {
            return it
        }

        return routeClass.simpleName.replace(Regex("([A-Z]+)")) { m ->
            "/${m.value.lowercase()}"
        }.removeSuffix("/route").let {
            val p = it.trimStart('/')
            val pp = pathPrefix.trim('/')
            if(pp.isEmpty()) "/$p" else "/$pp/$p"
        }
    }

    private fun getRouteUrlPath(handler: HandlerInfo, route: Route): StringBuilder {
        return StringBuilder(handler.pathPrefix).also { path ->
            handler.pathProperties.forEach { pp ->
                path.append("/").append(URLEncoder.encode(pp.getter.call(route).toString(), Charset.defaultCharset()))
            }
        }
    }

    fun getRouteUrlPath(route: Route): StringBuilder {
        return routes.get(route.javaClass)?.let { handler ->
            getRouteUrlPath(handler, route)
        } ?: throw IllegalStateException("Route ${route.javaClass} is not registered")
    }

    fun getRouteUrl(route: GetRoute): StringBuilder {
        return routes.get(route.javaClass)?.let { handler ->
            getRouteUrlPath(handler, route).also { path ->
                var n = 0
                handler.paramProperties.forEach { pp ->
                    pp.getter.call(route)?.let { v ->
                        if(v is Collection<*>) {
                            v.forEach { v ->
                                if(v != null) {
                                    n++
                                    path.append(if(n == 1) '?' else '&')
                                        .append(pp.name)
                                        .append('=')
                                        .append(URLEncoder.encode(v.toString(), Charset.defaultCharset()))
                                }
                            }
                        } else {
                            n++
                            path.append(if(n == 1) '?' else '&')
                                .append(pp.name)
                                .append('=')
                                .append(URLEncoder.encode(v.toString(), Charset.defaultCharset()))
                        }
                    }
                }
            }
        } ?: throw IllegalStateException("Route ${route.javaClass} is not registered")
    }

    fun getAnnotations(route: Route): Array<Annotation>? {
        return routes.get(route.javaClass)?.annotations
    }

    fun getInfo(method: Method): HandlerInfo? = routes.values.firstOrNull { it.method == method }

    private inner class RoutesHandlerInterceptor : HandlerInterceptor {
        override fun preHandle(
            request: HttpServletRequest,
            response: HttpServletResponse,
            handler: Any
        ): Boolean {
            methods[(handler as? HandlerMethod)?.method]?.let { info ->
                info.conditions?.firstNotNullOfOrNull { it.isAccessible(request, info) }?.let {
                    response.sendError(it.first, it.second)
                    return false
                }
            }

            return super.preHandle(request, response, handler)
        }
    }

    companion object {
        lateinit var instance: RoutesHelper
        fun buildRouteUri(route: GetRoute): String = instance.getRouteUrl(route).toString()
        fun buildRouteUriPath(route: Route): String = instance.getRouteUrlPath(route).toString()
        fun getEndpointMethodAnnotations(route: Route): Array<Annotation>? = instance.getAnnotations(route)
        fun getEndpointMethodInfo(method: Method): HandlerInfo? = instance.getInfo(method)
    }
}

fun GetRoute.buildUri(): String = RoutesHelper.buildRouteUri(this)
fun Route.buildUriPath(): String = RoutesHelper.buildRouteUriPath(this)
fun Route.getEndpointMethodAnnotations(): Array<Annotation>? = RoutesHelper.getEndpointMethodAnnotations(this)