package br.com.financeControl.FinanceControl.dto

import br.com.financeControl.FinanceControl.entity.Role
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime


// 1. DTOs para Request/Response
data class LoginRequest(
    @field:NotBlank(message = "Email é obrigatório")
    @field:Email(message = "Email deve ter formato válido")
    val email: String,

    @field:NotBlank(message = "Senha é obrigatória")
    @field:Size(min = 8, message = "Senha deve ter pelo menos 8 caracteres")
    val password: String
)

data class LoginResponse(
    val token: String,
    val refreshToken: String,
    val expiresIn: Long,
    val user: UserResponse
)

data class UserResponse(
    val id: Long?,
    val email: String,
    val username: String,
    val roles: Set<String>
)

data class AuthResponse(
    val token: String,
    val type: String = "Bearer",
    val username: String,
    val email: String,
    val role: Set<Role>,
    val expiresIn: Long
)

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    //val timestamp: LocalDateTime = LocalDateTime.now()
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)