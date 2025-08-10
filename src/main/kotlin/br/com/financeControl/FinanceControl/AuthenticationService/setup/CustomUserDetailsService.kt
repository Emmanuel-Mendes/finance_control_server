package br.com.financeControl.FinanceControl.AuthenticationService.setup

import br.com.financeControl.FinanceControl.repository.UserRepository
import br.com.financeControl.FinanceControl.userImpl.UserDetaillsImpl
import org.apache.catalina.User
import org.springframework.context.annotation.Primary
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Primary
@Transactional
class CustomUserDetailsService (
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            .orElseThrow { UsernameNotFoundException("Usuário não encontrado: $username") } as UserDetails
        return user
    }


}