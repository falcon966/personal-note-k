package de.tuantu.personalnotek

import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
    fromApplication<PersonalNoteKApplication>().with(TestcontainersConfiguration::class).run(*args)
}
