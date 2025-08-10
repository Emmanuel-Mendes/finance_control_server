package br.com.financeControl.FinanceControl.repository

import br.com.financeControl.FinanceControl.entity.User
import br.com.financeControl.FinanceControl.userImpl.UserDetaillsImpl
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun findByUsername(username: String): Optional<User>
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean


    @Modifying
    @Query("UPDATE User u SET u.failedAttempts = :attempts WHERE u.email = :email")
    fun updateFailedLoginAttempts(@Param("attempts") attempsts: Int, @Param("email") email: String)

    @Modifying
    @Query("UPDATE User u SET u.accountNonLocked = :locked, u.lockTime = :lockeTime WHERE u.email = :email")
    fun lockedUser(@Param("locked") locked: Boolean, @Param("lockTime") lockTime: LocalDateTime?, @Param("email") email: String): UserDetails

    @Modifying
    @Query("UPDATE User u SET u.lastLogin = :lastLogin WHERE u.username = :username")
    fun updateLastLogin(username: String, lastLogin: LocalDateTime)

    @Modifying
    @Query("SELECT u FROM User u WHERE u.accountNonLocked = false")
    fun findUsersLocked(): List<UserDetaillsImpl>
}