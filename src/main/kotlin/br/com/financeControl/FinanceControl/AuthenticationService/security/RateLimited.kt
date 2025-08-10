package br.com.financeControl.FinanceControl.AuthenticationService.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RateLimited(
    val requests: Int = 5,
    val windowSeconds: Long = 300 // 5 minutos
)

@Component
class RateLimitInterceptor : HandlerInterceptor {
    private val requestCounts = ConcurrentHashMap<String, MutableMap<String, Int>>()
    private val windowStart = ConcurrentHashMap<String, Long>()

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        if (handler is HandlerMethod) {
            val rateLimited = handler.getMethodAnnotation(RateLimited::class.java)
            if (rateLimited != null) {
                val clientIp = getClientIp(request)
                val currentTime = System.currentTimeMillis()
                val windowKey = "${clientIp}_${currentTime / (rateLimited.windowSeconds * 1000)}"

                val count = requestCounts.computeIfAbsent(windowKey) { mutableMapOf() }
                val requests = count.compute(clientIp) { _, current -> (current ?: 0) + 1 } ?: 1

                if (requests > rateLimited.requests) {
                    response.status = HttpStatus.TOO_MANY_REQUESTS.value()
                    response.contentType = "application/json"
                    response.writer.write("""{"success":false,"message":"Rate limit exceeded","timestamp":"${LocalDateTime.now()}"}""")
                    return false
                }
            }
        }
        return true
    }

    private fun getClientIp(request: HttpServletRequest): String {
        return request.getHeader("X-Forwarded-For")?.split(",")?.first()?.trim()
            ?: request.remoteAddr
    }
}