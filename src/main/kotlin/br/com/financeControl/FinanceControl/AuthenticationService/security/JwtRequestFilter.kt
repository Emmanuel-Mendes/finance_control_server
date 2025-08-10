package br.com.financeControl.FinanceControl.AuthenticationService.security

import br.com.financeControl.FinanceControl.AuthenticationService.setup.CustomUserDetailsService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.slf4j.LoggerFactory

@Component
class JwtRequestFilter(
    private val userDetailsService: CustomUserDetailsService,
    private val jwtUtil: JwtUtil
) : OncePerRequestFilter() {

    @Suppress("PROPERTY_HIDES_JAVA_FIELD")
    private val logger = LoggerFactory.getLogger(JwtRequestFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorizationHeader = request.getHeader("Authorization")

        var username: String? = null
        var jwtToken: String? = null

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7)
            try {
                username = jwtUtil.getUsernameFromToken(jwtToken)
            } catch (e: Exception) {
                logger.error("Erro ao processar JWT token: ${e.message}")
            }
        }

        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = userDetailsService.loadUserByUsername(username)

            if (jwtToken != null && jwtUtil.validateToken(jwtToken, userDetails)) {
                val authToken = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities
                )
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authToken
            }
        }

        filterChain.doFilter(request, response)
    }
}