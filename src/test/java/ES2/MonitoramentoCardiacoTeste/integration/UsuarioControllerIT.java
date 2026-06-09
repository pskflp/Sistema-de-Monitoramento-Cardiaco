package ES2.MonitoramentoCardiacoTeste.integration;

import ES2.MonitoramentoCardiaco.MonitoramentoCardiacoApplication;
import ES2.MonitoramentoCardiaco.dto.LoginRequestDTO;
import ES2.MonitoramentoCardiaco.dto.UsuarioCreateDTO;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integração do fluxo de usuário (cadastro e login).
 * Sobem o contexto Spring completo e exercitam controller -> service -> repository -> H2.
 *
 * Observação: a API não possui tratamento global de exceções. Violações de regra de
 * negócio lançam IllegalArgumentException, que se propaga pela camada web. Estes testes
 * refletem esse comportamento atual (a exceção é propagada por MockMvc.perform).
 */
@SpringBootTest(classes = MonitoramentoCardiacoApplication.class)
@AutoConfigureMockMvc
@Transactional
class UsuarioControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ObjectMapper local apenas para serializar os corpos das requisições de teste.
    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private UsuarioCreateDTO usuarioValido() {
        return new UsuarioCreateDTO(
                "João", "Silva", "joao@email.com", "11999999999",
                "senha123", "senha123", LocalDate.of(1990, 1, 1), "M", "Brasil");
    }

    private static Throwable causaRaiz(Throwable t) {
        Throwable c = t;
        while (c.getCause() != null && c.getCause() != c) {
            c = c.getCause();
        }
        return c;
    }

    @Test
    void deveCadastrarUsuarioERetornar201() throws Exception {
        mockMvc.perform(post("/usuarios/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioValido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.nome").value("João"));

        assertTrue(usuarioRepository.findByEmail("joao@email.com").isPresent());
    }

    @Test
    void deveRetornar400QuandoEmailInvalido() throws Exception {
        UsuarioCreateDTO dto = usuarioValido();
        dto.setEmail("email-invalido");

        mockMvc.perform(post("/usuarios/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void devePropagarExcecaoQuandoSenhasNaoCoincidem() {
        UsuarioCreateDTO dto = usuarioValido();
        dto.setConfirmarSenha("outraSenha");

        Throwable thrown = assertThrows(Throwable.class,
                () -> mockMvc.perform(post("/usuarios/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))));

        Throwable raiz = causaRaiz(thrown);
        assertTrue(raiz instanceof IllegalArgumentException);
        assertTrue(raiz.getMessage().contains("não coincidem"));
    }

    @Test
    void devePropagarExcecaoQuandoEmailJaCadastrado() throws Exception {
        // Primeiro cadastro tem sucesso.
        mockMvc.perform(post("/usuarios/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioValido())))
                .andExpect(status().isCreated());

        // Segundo cadastro com o mesmo e-mail dispara IllegalArgumentException.
        Throwable thrown = assertThrows(Throwable.class,
                () -> mockMvc.perform(post("/usuarios/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioValido()))));

        assertTrue(causaRaiz(thrown) instanceof IllegalArgumentException);
    }

    @Test
    void deveRealizarLoginComSucessoERetornar200() throws Exception {
        // Cadastra via endpoint para garantir o usuário no banco.
        mockMvc.perform(post("/usuarios/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioValido())))
                .andExpect(status().isCreated());

        LoginRequestDTO login = new LoginRequestDTO();
        login.setEmail("joao@email.com");
        login.setSenha("senha123");

        mockMvc.perform(post("/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    void devePropagarExcecaoQuandoSenhaIncorreta() throws Exception {
        mockMvc.perform(post("/usuarios/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioValido())))
                .andExpect(status().isCreated());

        LoginRequestDTO login = new LoginRequestDTO();
        login.setEmail("joao@email.com");
        login.setSenha("senhaErrada");

        Throwable thrown = assertThrows(Throwable.class,
                () -> mockMvc.perform(post("/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login))));

        assertTrue(causaRaiz(thrown) instanceof IllegalArgumentException);
    }
}
