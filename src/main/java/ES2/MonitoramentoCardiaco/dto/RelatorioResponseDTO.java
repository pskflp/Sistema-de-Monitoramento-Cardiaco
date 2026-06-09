package ES2.MonitoramentoCardiaco.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class RelatorioResponseDTO {

    private Long usuarioId;
    private int totalRegistros;
    private LocalDateTime primeiroRegistro;
    private LocalDateTime ultimoRegistro;

    private EstatisticaDTO pressaoSistolica;
    private EstatisticaDTO pressaoDiastolica;
    private EstatisticaDTO frequenciaCardiaca;
    private EstatisticaDTO oxigenacao;
    private EstatisticaDTO peso;

    private long ocorrenciasFaltaDeAr;
    private long ocorrenciasDorNoPeito;
    private long ocorrenciasTontura;

    private List<String> alertas;

    // Série em ordem cronológica (crescente) para alimentar gráficos e histórico.
    private List<MonitoramentoResponseDTO> historico;
}
