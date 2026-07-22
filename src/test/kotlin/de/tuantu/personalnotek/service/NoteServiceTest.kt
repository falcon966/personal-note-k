package de.tuantu.personalnotek.service

import de.tuantu.personalnotek.RepositoryTest
import de.tuantu.personalnotek.persistence.NoteRepository
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired

@RepositoryTest
class NoteServiceTest {

    @Autowired
    lateinit var noteRepository: NoteRepository

    lateinit var noteService: NoteService

    @BeforeEach
    fun setup(){
        noteService = NoteService(noteRepository)
    }

}