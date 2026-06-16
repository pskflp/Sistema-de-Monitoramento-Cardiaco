package ES2.MonitoramentoCardiaco.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MonitoramentoResponseDTO {

    private Long id;
    private Long usuarioId;
    private LocalDateTime dataRegistro;
    private Integer pressaoSistolica;
    private Integer pressaoDiastolica;
    private Integer frequenciaCardiaca;
    private Double oxigenacao;
    private Double peso;
    private Boolean faltaDeAr;
    private Boolean dorNoPeito;
    private Boolean tontura;
}