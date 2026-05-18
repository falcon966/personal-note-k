package de.tuantu.personalnotek.controller

import de.tuantu.personalnotek.service.OverviewService
import de.tuantu.personalnotek.shared.Roles
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/person")
class PersonController(
    val overviewService: OverviewService,
) {
    @GetMapping("/all")
    @PreAuthorize("hasRole('${Roles.ADMIN}')")
    fun getAllPersons() = overviewService.getAllPersons()
}
