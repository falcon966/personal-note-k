package de.tuantu.personalnotek.shared

import org.springframework.security.oauth2.jwt.Jwt
import java.util.*

data class AuthDataDto(
    val userId: UUID,
    val name: String,
    val roles: List<String>
) {
    companion object{
        fun parseAuthData(jwt: Jwt): AuthDataDto {
            return AuthDataDto(
                userId = UUID.fromString(jwt.subject),
                name = jwt.claims["name"].toString(),
                roles = jwt.getClaimAsStringList("roles")
            )
        }
    }
}
