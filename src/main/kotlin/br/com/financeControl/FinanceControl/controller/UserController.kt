package br.com.financeControl.FinanceControl.controller

import br.com.financeControl.FinanceControl.AuthenticationService.AuthService
import br.com.financeControl.FinanceControl.AuthenticationService.security.RateLimited
import br.com.financeControl.FinanceControl.dto.ApiResponse
import br.com.financeControl.FinanceControl.dto.AuthResponse
import br.com.financeControl.FinanceControl.dto.LoginRequest
import br.com.financeControl.FinanceControl.dto.RegisterRequest
import br.com.financeControl.FinanceControl.entity.Role
import br.com.financeControl.FinanceControl.entity.User
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication


@RestController
@RequestMapping("/api/auth")
class UserController(
    private val authService: AuthService
) {
    private val logger = LoggerFactory.getLogger(AuthService::class.java)

    @PostMapping("/login")
    @RateLimited(requests = 5, windowSeconds = 300)
    fun login(
        @Valid @RequestBody loginRequest: LoginRequest,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<AuthResponse>> {
        return try {
            val clientIp = getClientIpAddress(request)
            logger.info("Tentativa de login para usuário: ${loginRequest.email} do IP: $clientIp")

            val authResponse = authService.login(loginRequest)

            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "Login realizado com sucesso",
                    data = authResponse
                )
            )
        } catch (e: Exception) {
            logger.error("Erro no login: ${e.message}")
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse<AuthResponse>(
                    success = false,
                    message = e.message ?: "Erro na autenticação"
                )
            )
        }
    }

    @PostMapping("/register")
    @RateLimited(requests = 5, windowSeconds = 300)
    fun register(
        @Valid @RequestBody registerRequest: RegisterRequest,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<AuthResponse>> {
        return try {
            val clientIp = getClientIpAddress(request)
            logger.info("Tentativa de registro para usuário: ${registerRequest.username} do IP: $clientIp")

            val authResponse = authService.register(registerRequest)

            ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse(
                    success = true,
                    message = "Usuário registrado com sucesso",
                    data = authResponse
                )
            )
        } catch (e: Exception) {
            logger.error("Erro no registro: ${e.message}")
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse<AuthResponse>(
                    success = false,
                    message = e.message ?: "Erro no registro"
                )
            )
        }
    }

    @GetMapping("/profile")
    fun getUserProfile(authentication: Authentication): ResponseEntity<ApiResponse<Map<String, Any>>> {
        val user = authentication.principal as User

        val profileData: Map<String, Any> = mapOf(
            "username" to user.username,
            "email" to user.email,
            "role" to setOf(Role.USER),
            "createdAt" to user.createdAt.toString(),
            "lastLogin" to  (user.lastLogin?.toString() ?: "Nunca")
        )

        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                message = "Perfil do usuário",
                data = profileData,
                //timestamp = LocalDateTime.now()
            )
        )
    }

    private fun getClientIpAddress(request: HttpServletRequest): String {
        val xForwardedFor = request.getHeader("X-Forwarded-For")
        return if (xForwardedFor.isNullOrBlank()) {
            request.remoteAddr
        } else {
            xForwardedFor.split(",")[0].trim()
        }
    }


}