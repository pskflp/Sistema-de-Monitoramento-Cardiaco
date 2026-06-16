package ES2.MonitoramentoCardiaco.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EstatisticaDTO {

    private Double media;
    private Double minimo;
    private Double maximo;
}
