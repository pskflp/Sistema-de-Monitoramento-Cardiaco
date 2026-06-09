package ES2.MonitoramentoCardiaco.service;

import ES2.MonitoramentoCardiaco.dto.MonitoramentoCreateDTO;
import ES2.MonitoramentoCardiaco.dto.MonitoramentoResponseDTO;
import ES2.MonitoramentoCardiaco.model.MonitoramentoCardiaco;
import ES2.MonitoramentoCardiaco.model.Usuario;
import ES2.MonitoramentoCardiaco.repository.MonitoramentoRepository;
import ES2.MonitoramentoCardiaco.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MonitoramentoService {

    private final MonitoramentoRepository monitoramentoRepository;
    private final UsuarioRepository usuarioRepository;

    public MonitoramentoService(MonitoramentoRepository monitoramentoRepository,
                                UsuarioRepository usuarioRepository) {
        this.monitoramentoRepository = monitoramentoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public MonitoramentoResponseDTO registrar(MonitoramentoCreateDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        MonitoramentoCardiaco m = new MonitoramentoCardiaco();
        m.setUsuario(usuario);
        m.setDataRegistro(dto.getDataRegistro());
        m.setPressaoSistolica(dto.getPressaoSistolica());
        m.setPressaoDiastolica(dto.getPressaoDiastolica());
        m.setFrequenciaCardiaca(dto.getFrequenciaCardiaca());
        m.setOxigenacao(dto.getOxigenacao());
        m.setPeso(dto.getPeso());
        m.setFaltaDeAr(dto.getFaltaDeAr());
        m.setDorNoPeito(dto.getDorNoPeito());
        m.setTontura(dto.getTontura());

        MonitoramentoCardiaco salvo = monitoramentoRepository.save(m);
        return toDTO(salvo);
    }

    public List<MonitoramentoResponseDTO> listarPorUsuario(Long usuarioId) {
        return monitoramentoRepository
                .findByUsuarioIdOrderByDataRegistroDesc(usuarioId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private MonitoramentoResponseDTO toDTO(MonitoramentoCardiaco m) {
        return new MonitoramentoResponseDTO(
                m.getId(),
                m.getUsuario().getId(),
                m.getDataRegistro(),
                m.getPressaoSistolica(),
                m.getPressaoDiastolica(),
                m.getFrequenciaCardiaca(),
                m.getOxigenacao(),
                m.getPeso(),
                m.getFaltaDeAr(),
                m.getDorNoPeito(),
                m.getTontura()
        );
    }
}