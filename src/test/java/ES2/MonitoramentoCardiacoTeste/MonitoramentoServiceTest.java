package ES2.MonitoramentoCardiacoTeste;

import ES2.MonitoramentoCardiaco.dto.MonitoramentoCreateDTO;
import ES2.MonitoramentoCardiaco.dto.MonitoramentoResponseDTO;
import ES2.MonitoramentoCardiaco.model.MonitoramentoCardiaco;
import ES2.MonitoramentoCardiaco.model.Usuario;
import ES2.MonitoramentoCardiaco.repository.MonitoramentoRepository;
import ES2.MonitoramentoCardiaco.repository.UsuarioRepository;
import ES2.MonitoramentoCardiaco.service.MonitoramentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MonitoramentoServiceTest {

    @Mock
    private MonitoramentoRepository monitoramentoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private MonitoramentoService monitoramentoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveRegistrarMonitoramentoComSucesso() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        MonitoramentoCreateDTO dto = new MonitoramentoCreateDTO();
        dto.setUsuarioId(1L);
        dto.setDataRegistro(LocalDateTime.now());
        dto.setPressaoSistolica(120);
        dto.setPressaoDiastolica(80);
        dto.setFrequenciaCardiaca(75);
        dto.setOxigenacao(98.0);
        dto.setPeso(70.0);
        dto.setFaltaDeAr(false);
        dto.setDorNoPeito(false);
        dto.setTontura(false);

        MonitoramentoCardiaco salvo = new MonitoramentoCardiaco();
        salvo.setId(1L);
        salvo.setUsuario(usuario);
        salvo.setDataRegistro(dto.getDataRegistro());
        salvo.setPressaoSistolica(120);
        salvo.setPressaoDiastolica(80);
        salvo.setFrequenciaCardiaca(75);
        salvo.setOxigenacao(98.0);
        salvo.setPeso(70.0);
        salvo.setFaltaDeAr(false);
        salvo.setDorNoPeito(false);
        salvo.setTontura(false);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(monitoramentoRepository.save(any())).thenReturn(salvo);

        MonitoramentoResponseDTO response = monitoramentoService.registrar(dto);

        assertNotNull(response);
        assertEquals(120, response.getPressaoSistolica());
        assertEquals(98.0, response.getOxigenacao());
        verify(monitoramentoRepository, times(1)).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        MonitoramentoCreateDTO dto = new MonitoramentoCreateDTO();
        dto.setUsuarioId(99L);

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> monitoramentoService.registrar(dto));
    }

    @Test
    void deveListarMonitoramentosPorUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        MonitoramentoCardiaco m = new MonitoramentoCardiaco();
        m.setId(1L);
        m.setUsuario(usuario);
        m.setDataRegistro(LocalDateTime.now());
        m.setFaltaDeAr(false);
        m.setDorNoPeito(false);
        m.setTontura(false);

        when(monitoramentoRepository.findByUsuarioIdOrderByDataRegistroDesc(1L))
                .thenReturn(List.of(m));

        List<MonitoramentoResponseDTO> lista = monitoramentoService.listarPorUsuario(1L);

        assertEquals(1, lista.size());
        verify(monitoramentoRepository, times(1))
                .findByUsuarioIdOrderByDataRegistroDesc(1L);
    }
}