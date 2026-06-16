package ES2.MonitoramentoCardiacoTeste;

import ES2.MonitoramentoCardiaco.dto.RelatorioResponseDTO;
import ES2.MonitoramentoCardiaco.model.MonitoramentoCardiaco;
import ES2.MonitoramentoCardiaco.model.Usuario;
import ES2.MonitoramentoCardiaco.repository.MonitoramentoRepository;
import ES2.MonitoramentoCardiaco.repository.UsuarioRepository;
import ES2.MonitoramentoCardiaco.service.RelatorioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RelatorioServiceTest {

    @Mock
    private MonitoramentoRepository monitoramentoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private RelatorioService relatorioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private MonitoramentoCardiaco registro(Usuario usuario, LocalDateTime data,
                                           Integer sistolica, Integer diastolica,
                                           Integer frequencia, Double oxigenacao, Double peso,
                                           boolean faltaDeAr, boolean dorNoPeito, boolean tontura) {
        MonitoramentoCardiaco m = new MonitoramentoCardiaco();
        m.setId(1L);
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
        return m;
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> relatorioService.gerarRelatorio(99L));
        verify(monitoramentoRepository, never())
                .findByUsuarioIdOrderByDataRegistroDesc(anyLong());
    }

    @Test
    void deveLancarExcecaoQuandoNaoHaRegistros() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(monitoramentoRepository.findByUsuarioIdOrderByDataRegistroDesc(1L))
                .thenReturn(Collections.emptyList());

        assertThrows(IllegalArgumentException.class,
                () -> relatorioService.gerarRelatorio(1L));
    }

    @Test
    void deveGerarRelatorioComEstatisticasHistoricoEAlertas() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        LocalDateTime maisAntigo = LocalDateTime.of(2026, 1, 1, 8, 0);
        LocalDateTime maisRecente = LocalDateTime.of(2026, 1, 10, 8, 0);

        // Repositório retorna em ordem decrescente (mais recente primeiro).
        MonitoramentoCardiaco recente =
                registro(usuario, maisRecente, 150, 95, 110, 90.0, 80.0, true, false, true);
        MonitoramentoCardiaco antigo =
                registro(usuario, maisAntigo, 120, 80, 70, 98.0, 82.0, false, false, false);

        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(monitoramentoRepository.findByUsuarioIdOrderByDataRegistroDesc(1L))
                .thenReturn(List.of(recente, antigo));

        RelatorioResponseDTO relatorio = relatorioService.gerarRelatorio(1L);

        assertEquals(1L, relatorio.getUsuarioId());
        assertEquals(2, relatorio.getTotalRegistros());

        // Histórico deve estar em ordem cronológica crescente.
        assertEquals(maisAntigo, relatorio.getPrimeiroRegistro());
        assertEquals(maisRecente, relatorio.getUltimoRegistro());
        assertEquals(maisAntigo, relatorio.getHistorico().get(0).getDataRegistro());
        assertEquals(maisRecente, relatorio.getHistorico().get(1).getDataRegistro());

        // Estatísticas (média/min/max).
        assertEquals(135.0, relatorio.getPressaoSistolica().getMedia());
        assertEquals(120.0, relatorio.getPressaoSistolica().getMinimo());
        assertEquals(150.0, relatorio.getPressaoSistolica().getMaximo());

        // Contagem de sintomas.
        assertEquals(1, relatorio.getOcorrenciasFaltaDeAr());
        assertEquals(0, relatorio.getOcorrenciasDorNoPeito());
        assertEquals(1, relatorio.getOcorrenciasTontura());

        // Alertas de risco esperados.
        assertTrue(relatorio.getAlertas().stream()
                .anyMatch(a -> a.contains("Pressão sistólica elevada")));
        assertTrue(relatorio.getAlertas().stream()
                .anyMatch(a -> a.contains("Frequência cardíaca elevada")));
        assertTrue(relatorio.getAlertas().stream()
                .anyMatch(a -> a.contains("Nível de oxigenação baixo")));
    }

    @Test
    void deveInformarAusenciaDeRiscoQuandoIndicadoresNormais() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        MonitoramentoCardiaco normal = registro(usuario, LocalDateTime.of(2026, 1, 1, 8, 0),
                120, 80, 70, 98.0, 75.0, false, false, false);

        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(monitoramentoRepository.findByUsuarioIdOrderByDataRegistroDesc(1L))
                .thenReturn(List.of(normal));

        RelatorioResponseDTO relatorio = relatorioService.gerarRelatorio(1L);

        assertEquals(1, relatorio.getAlertas().size());
        assertTrue(relatorio.getAlertas().get(0).contains("Nenhum indicador de risco"));
    }
}
