package ES2.MonitoramentoCardiacoTeste.integration;

import ES2.MonitoramentoCardiaco.MonitoramentoCardiacoApplication;
import ES2.MonitoramentoCardiaco.dto.MonitoramentoCreateDTO;
import ES2.MonitoramentoCardiaco.model.Usuario;
import ES2.MonitoramentoCardiaco.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integração do registro e listagem de monitoramentos cardíacos.
 */
@SpringBootTest(classes = MonitoramentoCardiacoApplication.class)
@AutoConfigureMockMvc
@Transactional
class MonitoramentoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ObjectMapper local apenas para serializar os corpos das requisições de teste.
    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static Throwable causaRaiz(Throwable t) {
        Throwable c = t;
        while (c.getCause() != null && c.getCause() != c) {
            c = c.getCause();
        }
        return c;
    }

    private Usuario novoUsuarioPersistido() {
        Usuario usuario = new Usuario();
        usuario.setNome("João");
        usuario.setSobrenome("Silva");
        usuario.setEmail("joao@email.com");
        usuario.setTelefone("11999999999");
        usuario.setSenha("senha123");
        usuario.setDataNascimento(LocalDate.of(1990, 1, 1));
        usuario.setSexo("M");
        usuario.setPais("Brasil");
        return usuarioRepository.save(usuario);
    }

    private MonitoramentoCreateDTO monitoramentoDe(Long usuarioId) {
        MonitoramentoCreateDTO dto = new MonitoramentoCreateDTO();
        dto.setUsuarioId(usuarioId);
        dto.setDataRegistro(LocalDateTime.of(2026, 1, 5, 8, 0));
        dto.setPressaoSistolica(120);
        dto.setPressaoDiastolica(80);
        dto.setFrequenciaCardiaca(75);
        dto.setOxigenacao(98.0);
        dto.setPeso(70.0);
        dto.setFaltaDeAr(false);
        dto.setDorNoPeito(false);
        dto.setTontura(false);
        return dto;
    }

    @Test
    void deveRegistrarMonitoramentoERetornar201() throws Exception {
        Usuario usuario = novoUsuarioPersistido();

        mockMvc.perform(post("/monitoramentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(monitoramentoDe(usuario.getId()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.usuarioId").value(usuario.getId()))
                .andExpect(jsonPath("$.pressaoSistolica").value(120))
                .andExpect(jsonPath("$.oxigenacao").value(98.0));
    }

    @Test
    void deveRetornar400QuandoUsuarioIdAusente() throws Exception {
        MonitoramentoCreateDTO dto = monitoramentoDe(null); // usuarioId é @NotNull

        mockMvc.perform(post("/monitoramentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void devePropagarExcecaoQuandoUsuarioNaoEncontrado() {
        MonitoramentoCreateDTO dto = monitoramentoDe(999L);

        Throwable thrown = assertThrows(Throwable.class,
                () -> mockMvc.perform(post("/monitoramentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))));

        assertTrue(causaRaiz(thrown) instanceof IllegalArgumentException);
    }

    @Test
    void deveListarMonitoramentosPorUsuarioERetornar200() throws Exception {
        Usuario usuario = novoUsuarioPersistido();

        // Registra dois monitoramentos via endpoint.
        mockMvc.perform(post("/monitoramentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(monitoramentoDe(usuario.getId()))))
                .andExpect(status().isCreated());

        MonitoramentoCreateDTO segundo = monitoramentoDe(usuario.getId());
        segundo.setDataRegistro(LocalDateTime.of(2026, 1, 6, 8, 0));
        mockMvc.perform(post("/monitoramentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segundo)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/monitoramentos/usuario/{usuarioId}", usuario.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].usuarioId").value(usuario.getId()));
    }

    @Test
    void deveRetornarListaVaziaQuandoUsuarioSemRegistros() throws Exception {
        Usuario usuario = novoUsuarioPersistido();

        mockMvc.perform(get("/monitoramentos/usuario/{usuarioId}", usuario.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
