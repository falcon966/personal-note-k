package de.tuantu.personalnotek.controller

import de.tuantu.personalnotek.service.OverviewService
import de.tuantu.personalnotek.service.domain.PersonDto
import de.tuantu.personalnotek.shared.AuthDataDto
import de.tuantu.personalnotek.shared.Roles
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PathVariable
import java.util.UUID

@RestController
@RequestMapping("/person")
class PersonController(
    val overviewService: OverviewService,
) {
    @GetMapping("/all")
    @PreAuthorize("hasRole('${Roles.ADMIN}')")
    fun getAllPersons(
        @AuthenticationPrincipal jwt: Jwt
    ): List<PersonDto> {
        val authData: AuthDataDto = AuthDataDto.parseAuthData(jwt)
        return overviewService.getAllPersons(authData.userId)
    }

    @GetMapping("/{id}")
    fun getPersonDetails(
        @PathVariable id: UUID,
        @AuthenticationPrincipal jwt: Jwt
    ): PersonDto {
        val authData: AuthDataDto = AuthDataDto.parseAuthData(jwt)
        val personDto = overviewService.getPersonById(id, authData.userId) ?: throw NoSuchElementException("Person with id $id not found")
        return personDto
    }
}
