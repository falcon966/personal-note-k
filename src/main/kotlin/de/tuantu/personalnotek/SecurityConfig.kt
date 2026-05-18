package de.tuantu.personalnotek

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {
    @Value("\${spring.security.user.name}")
    private val swaggerUsername: String? = null

    @Value("\${spring.security.user.password}")
    private val swaggerPassword: String? = null

    // ----------------------------------------------------------------
    // Chain 1 (höchste Priorität): Basic Auth nur für Swagger-Pfade
    // ----------------------------------------------------------------
    @Bean
    @Order(1)
    @Throws(Exception::class)
    fun swaggerFilterChain(http: HttpSecurity): SecurityFilterChain? {
        http
            .securityMatcher("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**")
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.anyRequest().authenticated()
            }.httpBasic(Customizer.withDefaults())
            .userDetailsService(swaggerUserDetailsService(passwordEncoder()))

        return http.build()
    }

    // ----------------------------------------------------------------
    // Chain 2: OAuth2/JWT für alle anderen Endpoints
    // ----------------------------------------------------------------
    @Bean
    @Order(2)
    @Throws(java.lang.Exception::class)
    fun apiFilterChain(http: HttpSecurity): SecurityFilterChain? {
        http
            .cors {
                it.configurationSource(
                    corsConfigurationSource(),
                )
            }.csrf { it.disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/public/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }.oauth2ResourceServer { oauth2 ->
                oauth2
                    .jwt {
                        it.jwtAuthenticationConverter(jwtAuthenticationConverter())
                    }
            }

        return http.build()
    }

    // ----------------------------------------------------------------
    // Keycloak JWT Roles mappen (realm_access.roles → ROLE_xxx)
    // ----------------------------------------------------------------
    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val converter = JwtGrantedAuthoritiesConverter()
        converter.setAuthoritiesClaimName("roles") // jetzt Top-Level!
        converter.setAuthorityPrefix("ROLE_")

        val jwtConverter = JwtAuthenticationConverter()
        jwtConverter.setJwtGrantedAuthoritiesConverter(converter)
        return jwtConverter
    }

    // ----------------------------------------------------------------
    // Separater InMemory-User nur für Swagger Basic Auth
    // ----------------------------------------------------------------
    fun swaggerUserDetailsService(encoder: PasswordEncoder): UserDetailsService {
        val swaggerUser =
            User
                .builder()
                .username(swaggerUsername!!)
                .password(encoder.encode(swaggerPassword))
                .roles("SWAGGER")
                .build()
        return InMemoryUserDetailsManager(swaggerUser)
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        // Die URL des UI-Dev-Servers
        configuration.allowedOrigins =
            mutableListOf(
                "http://localhost:5173",
                "https://personal-note.trantuantu.de",
            )
        configuration.setAllowedMethods(mutableListOf("GET", "POST", "PUT", "DELETE", "OPTIONS"))
        configuration.allowedHeaders = mutableListOf("Authorization", "Content-Type", "X-Requested-With")
        configuration.allowCredentials = true // Wichtig für Cookies/Session-Handling

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
