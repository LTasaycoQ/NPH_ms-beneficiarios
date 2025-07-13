package pe.edu.vallegrande.beneficiary.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.vallegrande.beneficiary.dto.EducationDTO;
import pe.edu.vallegrande.beneficiary.dto.HealthDTO;
import pe.edu.vallegrande.beneficiary.dto.PersonDTO;
import pe.edu.vallegrande.beneficiary.dto.PersonWithSingleDetailsDTO;
import pe.edu.vallegrande.beneficiary.model.Person;
import pe.edu.vallegrande.beneficiary.repository.PersonRepository;
import pe.edu.vallegrande.beneficiary.webclient.PersonClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository repository;

    @Mock
    private PersonClient personClient;

    @InjectMocks
    private PersonService service;

    private Person person;
    private PersonDTO personDTO;

    @BeforeEach
    void setup() {
        person = new Person();
        person.setIdPerson(1);
        person.setName("Luis");
        person.setSurname("Tasayco");
        person.setAge(25);
        person.setBirthdate(LocalDate.of(2000, 1, 1));
        person.setTypeDocument("DNI");
        person.setDocumentNumber("12345678");
        person.setTypeKinship("HIJO");
        person.setSponsored("SI");
        person.setState("A");
        person.setFamilyId(100);

        personDTO = new PersonDTO();
        personDTO.setIdPerson(1);
        personDTO.setName("Luis");
        personDTO.setSurname("Tasayco");
        personDTO.setAge(25);
        personDTO.setBirthdate(LocalDate.of(2000, 1, 1));
        personDTO.setTypeDocument("DNI");
        personDTO.setDocumentNumber("12345678");
        personDTO.setTypeKinship("HIJO");
        personDTO.setSponsored("SI");
        personDTO.setState("A");
        personDTO.setFamilyId(100);
    }

    @Test
    void testGetPersonByIdWithDetails() {
        when(repository.findById(1)).thenReturn(Mono.just(person));
        when(personClient.getEducationByPersonId(1)).thenReturn(Flux.just(new EducationDTO()));
        when(personClient.getHealthByPersonId(1)).thenReturn(Flux.just(new HealthDTO()));

        StepVerifier.create(service.getPersonByIdWithDetails(1))
                .expectNextMatches(p -> p.getName().equals("Luis"))
                .verifyComplete();
    }

    @Test
    void testDeletePerson() {
        when(repository.updateStateById(1, "I")).thenReturn(Mono.empty());

        StepVerifier.create(service.deletePerson(1)).verifyComplete();
    }

    @Test
    void testRestorePerson() {
        when(repository.updateStateById(1, "A")).thenReturn(Mono.empty());

        StepVerifier.create(service.restorePerson(1)).verifyComplete();
    }

    @Test
    void testUpdatePersonData() {
        when(repository.updatePerson(
                eq(1), anyString(), anyString(), anyInt(), any(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyInt()
        )).thenReturn(Mono.empty());

        StepVerifier.create(service.updatePersonData(personDTO)).verifyComplete();
    }

    @Test
    void testGetPersonsBySponsoredAndState() {
        when(repository.findBySponsoredAndState("SI", "A")).thenReturn(Flux.just(person));

        StepVerifier.create(service.getPersonsBySponsoredAndState("SI", "A"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testGetPersonsByTypeKinshipAndState() {
        when(repository.findByTypeKinshipAndState("HIJO", "A")).thenReturn(Flux.just(person));

        StepVerifier.create(service.getPersonsByTypeKinshipAndState("HIJO", "A"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testGetPersonsWithSingleDetails() {
        when(repository.findAll()).thenReturn(Flux.just(person));
        when(personClient.getEducationByPersonId(1)).thenReturn(Flux.just(new EducationDTO()));
        when(personClient.getHealthByPersonId(1)).thenReturn(Flux.just(new HealthDTO()));

        StepVerifier.create(service.getPersonsWithSingleDetails("HIJO", "SI", "A"))
                .expectNextMatches(dto -> dto.getName().equals("Luis"))
                .verifyComplete();
    }

    @Test
    void testRegisterPerson() {
        EducationDTO edu = new EducationDTO();
        HealthDTO health = new HealthDTO();
        personDTO.setEducation(List.of(edu));
        personDTO.setHealth(List.of(health));

        when(repository.insertPerson(anyString(), anyString(), anyInt(), any(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyInt())).thenReturn(Mono.empty());
        when(repository.getLastInsertedId()).thenReturn(Mono.just(1));
        when(personClient.registerEducation(any())).thenReturn(Mono.empty());
        when(personClient.registerHealth(any())).thenReturn(Mono.empty());

        StepVerifier.create(service.registerPerson(personDTO)).verifyComplete();
    }

    @Test
    void testCorrectEducationAndHealth() {
        EducationDTO edu = new EducationDTO();
        HealthDTO health = new HealthDTO();
        personDTO.setEducation(List.of(edu));
        personDTO.setHealth(List.of(health));

        when(personClient.updateEducation(any())).thenReturn(Mono.empty());
        when(personClient.updateHealth(any())).thenReturn(Mono.empty());

        StepVerifier.create(service.correctEducationAndHealth(personDTO)).verifyComplete();
    }

    @Test
    void testUpdatePersonWithNewIds() {
        EducationDTO edu = new EducationDTO();
        HealthDTO health = new HealthDTO();
        personDTO.setEducation(List.of(edu));
        personDTO.setHealth(List.of(health));

        when(personClient.registerEducation(any())).thenReturn(Mono.empty());
        when(personClient.registerHealth(any())).thenReturn(Mono.empty());

        StepVerifier.create(service.updatePersonWithNewIds(personDTO)).verifyComplete();
    }
@Test
void testGetPersonsWithSingleDetailsWithAllNulls() {
    when(repository.findAll()).thenReturn(Flux.just(person));
    when(personClient.getEducationByPersonId(anyInt())).thenReturn(Flux.just(new EducationDTO()));
    when(personClient.getHealthByPersonId(anyInt())).thenReturn(Flux.just(new HealthDTO()));

    StepVerifier.create(service.getPersonsWithSingleDetails(null, null, null))
            .expectNextCount(1)
            .verifyComplete();
}

@Test
void testGetPersonsWithSingleDetailsWithNonNulls() {
    when(repository.findAll()).thenReturn(Flux.just(person));
    when(personClient.getEducationByPersonId(anyInt())).thenReturn(Flux.just(new EducationDTO()));
    when(personClient.getHealthByPersonId(anyInt())).thenReturn(Flux.just(new HealthDTO()));

    StepVerifier.create(service.getPersonsWithSingleDetails("HIJO", "SI", "A"))
            .expectNextCount(1)
            .verifyComplete();
}



@Test
void testUpdatePersonWithNewIdsEmptyLists() {
    personDTO.setEducation(null);
    personDTO.setHealth(null);

    StepVerifier.create(service.updatePersonWithNewIds(personDTO))
            .verifyComplete();
}
@Test
void testUpdatePersonWithNewIds_UpdateExisting() {
    EducationDTO edu = new EducationDTO();
    edu.setIdEducation(10);  // NO null
    HealthDTO health = new HealthDTO();
    health.setIdHealth(20);  // NO null

    personDTO.setEducation(List.of(edu));
    personDTO.setHealth(List.of(health));

    when(personClient.updateEducation(any())).thenReturn(Mono.empty());
    when(personClient.updateHealth(any())).thenReturn(Mono.empty());

    StepVerifier.create(service.updatePersonWithNewIds(personDTO)).verifyComplete();
}



@Test
void testGetPersonById() {
    when(repository.findById(1)).thenReturn(Mono.just(person));

    StepVerifier.create(service.getPersonById(1))
            .expectNextMatches(p -> p.getName().equals("Luis"))
            .verifyComplete();
}


}
