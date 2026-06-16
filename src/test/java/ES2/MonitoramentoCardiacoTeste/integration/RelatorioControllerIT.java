package ES2.MonitoramentoCardiacoTeste.integration;

import ES2.MonitoramentoCardiaco.MonitoramentoCardiacoApplication;
import ES2.MonitoramentoCardiaco.model.MonitoramentoCardiaco;
import ES2.MonitoramentoCardiaco.model.Usuario;
import ES2.MonitoramentoCardiaco.repository.MonitoramentoRepository;
import ES2.MonitoramentoCardiaco.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integração do relatório de saúde cardíaca (requisito 4).
 */
@SpringBootTest(classes = MonitoramentoCardiacoApplication.class)
@AutoConfigureMockMvc
@Transactional
class RelatorioControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MonitoramentoRepository monitoramentoRepository;

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

    private void persistirMonitoramento(Usuario usuario, LocalDateTime data,
                                        int sistolica, int diastolica, int frequencia,
                                        double oxigenacao, double peso,
                                        boolean faltaDeAr, boolean dorNoPeito, boolean tontura) {
        MonitoramentoCardiaco m = new MonitoramentoCardiaco();
        m.setUsuario(usuario);
        m.setDataRegistro(data);
        m.setPressaoSistolica(sistolica);
        m.setPressaoDiastolica(diastolica);
        m.setFrequenciaCardiaca(frequencia);
        m.setOxigenacao(oxigenacao);
        m.setPeso(peso);
        m.setFaltaDeAr(faltaDeAr);
        m.setDorNoPeito(dorNoPeito);
        m.setTontura(tontura);
        monitoramentoRepository.save(m);
    }

    @Test
    void deveGerarRelatorioERetornar200() throws Exception {
        Usuario usuario = novoUsuarioPersistido();
        persistirMonitoramento(usuario, LocalDateTime.of(2026, 1, 1, 8, 0),
                120, 80, 70, 98.0, 82.0, false, false, false);
        persistirMonitoramento(usuario, LocalDateTime.of(2026, 1, 10, 8, 0),
                150, 95, 110, 90.0, 80.0, true, false, true);

        mockMvc.perform(get("/relatorios/usuario/{usuarioId}", usuario.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioId").value(usuario.getId()))
                .andExpect(jsonPath("$.totalRegistros").value(2))
                .andExpect(jsonPath("$.pressaoSistolica.media").value(135.0))
                .andExpect(jsonPath("$.pressaoSistolica.minimo").value(120.0))
                .andExpect(jsonPath("$.pressaoSistolica.maximo").value(150.0))
                .andExpect(jsonPath("$.ocorrenciasFaltaDeAr").value(1))
                .andExpect(jsonPath("$.ocorrenciasTontura").value(1))
                .andExpect(jsonPath("$.historico.length()").value(2))
                .andExpect(jsonPath("$.alertas").isNotEmpty());
    }

    @Test
    void devePropagarExcecaoQuandoUsuarioSemRegistros() {
        Usuario usuario = novoUsuarioPersistido();

        Throwable thrown = assertThrows(Throwable.class,
                () -> mockMvc.perform(get("/relatorios/usuario/{usuarioId}", usuario.getId())));

        assertTrue(causaRaiz(thrown) instanceof IllegalArgumentException);
    }

    @Test
    void devePropagarExcecaoQuandoUsuarioNaoEncontrado() {
        Throwable thrown = assertThrows(Throwable.class,
                () -> mockMvc.perform(get("/relatorios/usuario/{usuarioId}", 999L)));

        assertTrue(causaRaiz(thrown) instanceof IllegalArgumentException);
    }
}
