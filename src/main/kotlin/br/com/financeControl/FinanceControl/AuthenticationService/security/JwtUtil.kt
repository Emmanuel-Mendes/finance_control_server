package br.com.financeControl.FinanceControl.AuthenticationService.security

import br.com.financeControl.FinanceControl.entity.User
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtUtil {

    @Value("\${jwt.secret:mySecretKey12345678901234567890}")
    private lateinit var secret: String

    @Value("\${jwt.expiration:86400000}") // 24 horas
    private val expiration: Long = 86400000

    private val logger = LoggerFactory.getLogger(JwtUtil::class.java)

    private fun getSigningKey(): Key {
        val keyBytes = secret.toByteArray()
        return Keys.hmacShaKeyFor(keyBytes)
    }

    fun generateToken(userDetails: UserDetails): String {
        val claims = mapOf<String, Any>(
            "sub" to userDetails.username,
            "authorities" to userDetails.authorities.map { it.authority }
        )
        return createToken(claims, userDetails.username)
    }

    private fun createToken(claims: Map<String, Any>, subject: String): String {
        val now = Date()
        val expiryDate = Date(now.time + expiration)

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact()
    }

    fun getUsernameFromToken(token: String): String? {
        return try {
            val claims = getClaimsFromToken(token)
            claims.subject
        } catch (e: Exception) {
            logger.error("Erro ao extrair username do token: ${e.message}")
            null
        }
    }

    fun getExpirationDateFromToken(token: String): Date? {
        return try {
            val claims = getClaimsFromToken(token)
            claims.expiration
        } catch (e: Exception) {
            logger.error("Erro ao extrair data de expiração do token: ${e.message}")
            null
        }
    }

    private fun getClaimsFromToken(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .body
    }

    fun isTokenExpired(token: String): Boolean {
        val expiration = getExpirationDateFromToken(token)
        return expiration?.before(Date()) ?: true
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        return try {
            val username = getUsernameFromToken(token)
            username == userDetails.username && !isTokenExpired(token)
        } catch (e: Exception) {
            logger.error("Token inválido: ${e.message}")
            false
        }
    }

    fun getExpirationTime(): Long = expiration
}
