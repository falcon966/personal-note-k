package de.tuantu.personalnotek.controller

import com.ninjasquad.springmockk.MockkBean
import de.tuantu.personalnotek.SecurityConfig
import de.tuantu.personalnotek.service.OverviewService
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.UUID

@WebMvcTest(PersonController::class)
@ImportAutoConfiguration(
    SecurityAutoConfiguration::class,
    SecurityFilterAutoConfiguration::class,
)
@Import(SecurityConfig::class)
class PersonControllerTest {
    @MockkBean(relaxed = true)
    lateinit var overviewService: OverviewService

    @Autowired
    private val mockMvc: MockMvc? = null

    @Test
    @Throws(Exception::class)
    fun `get all persons - success - returns empty list`() {
        every { overviewService.getAllPersons(any()) } returns emptyList()
        val mockUserId: UUID = UUID.randomUUID()
        mockMvc!!
            .perform(
                MockMvcRequestBuilders
                    .get("/person/all")
                    .with(
                        jwt()
                            .jwt { builder ->
                                builder.claim("sub", mockUserId.toString())
                                builder.claim("name", "falcon TNT")
                                builder.claim("roles", listOf("ADMIN", "offline_access"))
                            }
                            .authorities(SimpleGrantedAuthority("ROLE_ADMIN"))
                    )
            ).andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0))
    }

    @Test
    @Throws(Exception::class)
    fun `get all persons - failed - unauthorized`() {
        mockMvc!!
            .perform(
                MockMvcRequestBuilders.get("/person/all"),
            ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @Throws(Exception::class)
    fun `get all persons - failed - not the right role`() {
        val mockUserId: UUID = UUID.randomUUID()
        mockMvc!!
            .perform(
                MockMvcRequestBuilders
                    .get("/person/all")
                    .with(
                        jwt()
                            .jwt { builder ->
                                builder.claim("sub", mockUserId.toString())
                                builder.claim("name", "falcon TNT")
                                builder.claim("roles", listOf("ADMIN", "offline_access"))
                            }
                            .authorities(SimpleGrantedAuthority("ROLE_INVALID"))
                    )
            ).andExpect(MockMvcResultMatchers.status().isForbidden)
    }
}
