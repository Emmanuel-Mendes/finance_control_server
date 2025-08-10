package br.com.financeControl.FinanceControl.userImpl

import br.com.financeControl.FinanceControl.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


class UserDetaillsImpl (private val user: User) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return user.authorities.map { SimpleGrantedAuthority("ROLE_${it.authority}") }
    }

    override fun getPassword(): String = user.password

    override fun getUsername(): String = user.username

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = user.isAccountNonLocked

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = user.isEnabled

    fun getUser(): User = user

}