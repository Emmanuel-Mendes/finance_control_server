package br.com.financeControl.FinanceControl.AuthenticationService

import br.com.financeControl.Exception.ExceptionHandler
import br.com.financeControl.FinanceControl.AuthenticationService.security.JwtUtil
import br.com.financeControl.FinanceControl.dto.AuthResponse
import br.com.financeControl.FinanceControl.dto.LoginRequest
import br.com.financeControl.FinanceControl.dto.RegisterRequest
import br.com.financeControl.FinanceControl.entity.Role
import br.com.financeControl.FinanceControl.entity.User
import br.com.financeControl.FinanceControl.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JwtUtil
) {

    private val logger = LoggerFactory.getLogger(AuthService::class.java)
    private val maxFailedAttempts = 5
    private val lockTimeMinutes = 15L

    fun login(loginRequest: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(loginRequest.email)
            ?: throw BadCredentialsException("Email ou senha inválidos")

        try {
            if (!passwordEncoder.matches(loginRequest.password, user.password)) {
                throw BadCredentialsException("Email ou senha inválidos 2")
            }
            val authentication: Authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password)
            )

            val userDetails = authentication.principal
            val token = jwtUtil.generateToken(userDetails as UserDetails)

            // Reset failed attempts e atualizar último login
            if (user.failedAttempts > 0) {
                userRepository.updateFailedLoginAttempts(user.failedAttempts, user.email)
            }
            userRepository.updateLastLogin(user.username, LocalDateTime.now())

            logger.info("Login realizado com sucesso para usuário: ${user.username}")

            return AuthResponse(
                token = token,
                username = user.username,
                email = user.email,
                role = setOf(Role.USER),
                expiresIn = jwtUtil.getExpirationTime()
            )

        } catch (e: AuthenticationException) {
            handleFailedLogin(user)
            throw BadCredentialsException("Credenciais inválidas")
        }
    }

    fun register(registerRequest: RegisterRequest): AuthResponse {
        // Validações
        if (userRepository.existsByUsername(registerRequest.username)) {
            throw IllegalArgumentException("Nome de usuário já existe")
        }

        if (userRepository.existsByEmail(registerRequest.email)) {
            throw IllegalArgumentException("Email já cadastrado")
        }

        // Criar novo usuário
        val user = User(
            username = registerRequest.username,
            email = registerRequest.email,
            password = passwordEncoder.encode(registerRequest.password),
            roles = setOf(Role.USER)
        )

        val savedUser = userRepository.save(user)
        val token = jwtUtil.generateToken(savedUser as UserDetails)

        logger.info("Novo usuário registrado: ${savedUser.email}")

        return AuthResponse(
            token = token,
            username = savedUser.username,
            email = savedUser.email,
            role = savedUser.roles,
            expiresIn = jwtUtil.getExpirationTime()
        )
    }

    private fun handleFailedLogin(user: User) {
        val newFailedAttempts = user.failedAttempts + 1
        userRepository.updateFailedLoginAttempts(user.failedAttempts, email = user.email)

        if (newFailedAttempts >= maxFailedAttempts) {
            val lockUntil = LocalDateTime.now().plusMinutes(lockTimeMinutes)
            userRepository.lockedUser(locked = true, lockUntil, email = user.email)
            logger.warn("Conta bloqueada para usuário: ${user.email} até $lockUntil")
        }
    }
}