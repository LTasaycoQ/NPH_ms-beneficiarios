package pe.edu.vallegrande.beneficiary.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.reactive.ReactiveOAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import pe.edu.vallegrande.beneficiary.config.SecurityTestConfig;
import pe.edu.vallegrande.beneficiary.dto.PersonDTO;
import pe.edu.vallegrande.beneficiary.service.PersonService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@WebFluxTest(controllers = PersonController.class, excludeAutoConfiguration = {
    ReactiveOAuth2ResourceServerAutoConfiguration.class
})
@Import(SecurityTestConfig.class)
class PersonControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PersonService personService;

    private PersonDTO mockPerson;

    @BeforeEach
    void init() {
        mockPerson = new PersonDTO();
        mockPerson.setIdPerson(1);
        mockPerson.setName("Luis");
        mockPerson.setSurname("Tasayco");
    }

    @Test
    @DisplayName("Registrar persona")
    void testRegistrar() {
        Mockito.when(personService.registerPerson(mockPerson)).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/api/persons/register")
                .bodyValue(mockPerson)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Obtener persona por ID")
    void testObtenerPorId() {
        Mockito.when(personService.getPersonByIdWithDetails(1)).thenReturn(Mono.just(mockPerson));

        webTestClient.get()
                .uri("/api/persons/1/details")
                .exchange()
                .expectStatus().isOk()
                .expectBody(PersonDTO.class)
                .value(p -> {
                    assert p.getIdPerson() == 1;
                    assert p.getName().equals("Luis");
                });
    }

    @Test
    @DisplayName("Eliminar persona")
    void testEliminar() {
        Mockito.when(personService.deletePerson(1)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/persons/1/delete")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Actualizar persona")
    void testActualizar() {
        Mockito.when(personService.updatePersonData(mockPerson)).thenReturn(Mono.empty());

        webTestClient.put()
                .uri("/api/persons/1/update-person")
                .bodyValue(mockPerson)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Restaurar persona")
    void testRestaurar() {
        Mockito.when(personService.restorePerson(1)).thenReturn(Mono.empty());

        webTestClient.put()
                .uri("/api/persons/1/restore")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Filtrar por parentesco y estado")
    void testFiltrarPorTipoYEstado() {
        Mockito.when(personService.getPersonsByTypeKinshipAndState("HIJO", "A"))
                .thenReturn(Flux.just(mockPerson));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/persons/filter")
                        .queryParam("typeKinship", "HIJO")
                        .queryParam("state", "A")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PersonDTO.class)
                .hasSize(1);
    }

    @Test
    @DisplayName("Filtrar por apadrinamiento y estado")
    void testFiltrarPorApadrinadoYEstado() {
        Mockito.when(personService.getPersonsBySponsoredAndState("SI", "A"))
                .thenReturn(Flux.just(mockPerson));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/persons/filter-sponsored")
                        .queryParam("sponsored", "SI")
                        .queryParam("state", "A")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PersonDTO.class)
                .hasSize(1);
    }
}
