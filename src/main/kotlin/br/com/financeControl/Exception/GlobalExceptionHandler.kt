package br.com.financeControl.Exception


import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.userdetails.UsernameNotFoundException

data class ErrorResponse(
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(ex: BadCredentialsException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse("Email ou senha inválidos"))
    }

    @ExceptionHandler(DisabledException::class)
    fun handleDisabled(ex: DisabledException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ErrorResponse("Conta desabilitada"))
    }

    @ExceptionHandler(LockedException::class)
    fun handleLocked(ex: LockedException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.LOCKED)
            .body(ErrorResponse("Conta bloqueada"))
    }

    @ExceptionHandler(UsernameNotFoundException::class)
    fun handleUserNotFound(ex: UsernameNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse("Email ou senha inválidos"))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse("Erro interno do servidor"))
    }
}