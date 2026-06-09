package ES2.MonitoramentoCardiaco.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MonitoramentoCreateDTO {

    @NotNull(message = "ID do usuário é obrigatório.")
    private Long usuarioId;

    @NotNull(message = "Data do registro é obrigatória.")
    private LocalDateTime dataRegistro;

    private Integer pressaoSistolica;
    private Integer pressaoDiastolica;
    private Integer frequenciaCardiaca;
    private Double oxigenacao;
    private Double peso;

    private Boolean faltaDeAr = false;
    private Boolean dorNoPeito = false;
    private Boolean tontura = false;
}