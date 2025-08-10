package br.com.financeControl.FinanceControl.AuthenticationService.security

// ========== IMPORTS NECESSÁRIOS ==========

// Spring Framework
import br.com.financeControl.FinanceControl.userImpl.UserDetaillsImpl
import io.jsonwebtoken.Claims

// Spring Framework
import org.springframework.stereotype.Service

// Spring Security

// JWT - JJWT Library
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value

// Java Time/Date
import java.util.*

@Service
class JwtService (
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val expiration: Long,
    @Value("\${jwt.refresh-expiration}") private val refreshExpiration: Long
){

    fun generateUserToken(userDetails: UserDetaillsImpl): String {
        val claims = mapOf(
            "userId" to userDetails.getUser().id,
            "roles" to userDetails.authorities.map { it.authority }
        )

        return createToken(claims = claims, userDetails.username, expiration)
    }

    fun generateRefreshTokenUser(userDetails: UserDetaillsImpl): String {
        return createToken(emptyMap(), userDetails.username, refreshExpiration)
    }

    private fun createToken(claims: Map<String, Any?>, subject: String, expiration: Long): String {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expiration))
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact()
    }


    fun getUserNameFromToken(token: String): String {
        return getClaimFromToken(token) { it.subject }
    }
    fun getExpirationDateFromToken(token: String): Date {
        return getClaimFromToken(token) { it.expiration }
    }
    fun <T> getClaimFromToken(token: String, claimsResolver: (Claims) -> T): T {
        val claims = getAllClaimsFromToken(token)
        return claimsResolver(claims)
    }

    private fun getAllClaimsFromToken(token: String): Claims {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body
    }

    private fun isTokenExpired(token: String): Boolean {
        val expiration = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }
    fun validateToken(token: String, userDetails: UserDetaillsImpl): Boolean {
        val username = getUserNameFromToken(token)
        return username == userDetails.username && !isTokenExpired(token)
    }

    fun getExpirationTime(): Long = expiration / 1000


}