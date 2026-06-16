package ES2.MonitoramentoCardiaco.service;

import ES2.MonitoramentoCardiaco.dto.EstatisticaDTO;
import ES2.MonitoramentoCardiaco.dto.MonitoramentoResponseDTO;
import ES2.MonitoramentoCardiaco.dto.RelatorioResponseDTO;
import ES2.MonitoramentoCardiaco.model.MonitoramentoCardiaco;
import ES2.MonitoramentoCardiaco.repository.MonitoramentoRepository;
import ES2.MonitoramentoCardiaco.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    private final MonitoramentoRepository monitoramentoRepository;
    private final UsuarioRepository usuarioRepository;

    public RelatorioService(MonitoramentoRepository monitoramentoRepository,
                            UsuarioRepository usuarioRepository) {
        this.monitoramentoRepository = monitoramentoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public RelatorioResponseDTO gerarRelatorio(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        List<MonitoramentoCardiaco> registros =
                monitoramentoRepository.findByUsuarioIdOrderByDataRegistroDesc(usuarioId);

        if (registros.isEmpty()) {
            throw new IllegalArgumentException(
                    "Nenhum registro de monitoramento encontrado para este usuário.");
        }

        // Histórico em ordem cronológica (crescente) para os gráficos.
        List<MonitoramentoResponseDTO> historico = registros.stream()
                .sorted(Comparator.comparing(MonitoramentoCardiaco::getDataRegistro))
                .map(this::toDTO)
                .collect(Collectors.toList());

        LocalDateTime primeiro = historico.get(0).getDataRegistro();
        LocalDateTime ultimo = historico.get(historico.size() - 1).getDataRegistro();

        EstatisticaDTO pressaoSistolica =
                calcularInteiro(registros, MonitoramentoCardiaco::getPressaoSistolica);
        EstatisticaDTO pressaoDiastolica =
                calcularInteiro(registros, MonitoramentoCardiaco::getPressaoDiastolica);
        EstatisticaDTO frequenciaCardiaca =
                calcularInteiro(registros, MonitoramentoCardiaco::getFrequenciaCardiaca);
        EstatisticaDTO oxigenacao =
                calcularDouble(registros, MonitoramentoCardiaco::getOxigenacao);
        EstatisticaDTO peso =
                calcularDouble(registros, MonitoramentoCardiaco::getPeso);

        long faltaDeAr = registros.stream()
                .filter(r -> Boolean.TRUE.equals(r.getFaltaDeAr())).count();
        long dorNoPeito = registros.stream()
                .filter(r -> Boolean.TRUE.equals(r.getDorNoPeito())).count();
        long tontura = registros.stream()
                .filter(r -> Boolean.TRUE.equals(r.getTontura())).count();

        List<String> alertas = gerarAlertas(pressaoSistolica, pressaoDiastolica,
                frequenciaCardiaca, oxigenacao, faltaDeAr, dorNoPeito, tontura);

        return new RelatorioResponseDTO(
                usuarioId,
                registros.size(),
                primeiro,
                ultimo,
                pressaoSistolica,
                pressaoDiastolica,
                frequenciaCardiaca,
                oxigenacao,
                peso,
                faltaDeAr,
                dorNoPeito,
                tontura,
                alertas,
                historico
        );
    }

    private EstatisticaDTO calcularInteiro(List<MonitoramentoCardiaco> registros,
                                           Function<MonitoramentoCardiaco, Integer> extrator) {
        List<Double> valores = registros.stream()
                .map(extrator)
                .filter(v -> v != null)
                .map(Integer::doubleValue)
                .collect(Collectors.toList());
        return estatisticaDe(valores);
    }

    private EstatisticaDTO calcularDouble(List<MonitoramentoCardiaco> registros,
                                          Function<MonitoramentoCardiaco, Double> extrator) {
        List<Double> valores = registros.stream()
                .map(extrator)
                .filter(v -> v != null)
                .collect(Collectors.toList());
        return estatisticaDe(valores);
    }

    private EstatisticaDTO estatisticaDe(List<Double> valores) {
        if (valores.isEmpty()) {
            return new EstatisticaDTO(null, null, null);
        }
        double media = valores.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double min = valores.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double max = valores.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        media = Math.round(media * 100.0) / 100.0; // arredonda a média para 2 casas
        return new EstatisticaDTO(media, min, max);
    }

    private List<String> gerarAlertas(EstatisticaDTO sistolica, EstatisticaDTO diastolica,
                                      EstatisticaDTO frequencia, EstatisticaDTO oxigenacao,
                                      long faltaDeAr, long dorNoPeito, long tontura) {
        List<String> alertas = new ArrayList<>();

        if (sistolica.getMaximo() != null && sistolica.getMaximo() >= 140) {
            alertas.add("Pressão sistólica elevada detectada (>= 140 mmHg).");
        }
        if (diastolica.getMaximo() != null && diastolica.getMaximo() >= 90) {
            alertas.add("Pressão diastólica elevada detectada (>= 90 mmHg).");
        }
        if (frequencia.getMaximo() != null && frequencia.getMaximo() > 100) {
            alertas.add("Frequência cardíaca elevada detectada (> 100 bpm).");
        }
        if (frequencia.getMinimo() != null && frequencia.getMinimo() < 50) {
            alertas.add("Frequência cardíaca baixa detectada (< 50 bpm).");
        }
        if (oxigenacao.getMinimo() != null && oxigenacao.getMinimo() < 92) {
            alertas.add("Nível de oxigenação baixo detectado (< 92%).");
        }
        if (faltaDeAr > 0) {
            alertas.add("Episódios de falta de ar registrados: " + faltaDeAr + ".");
        }
        if (dorNoPeito > 0) {
            alertas.add("Episódios de dor no peito registrados: " + dorNoPeito + ".");
        }
        if (tontura > 0) {
            alertas.add("Episódios de tontura registrados: " + tontura + ".");
        }
        if (alertas.isEmpty()) {
            alertas.add("Nenhum indicador de risco identificado no período.");
        }
        return alertas;
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
