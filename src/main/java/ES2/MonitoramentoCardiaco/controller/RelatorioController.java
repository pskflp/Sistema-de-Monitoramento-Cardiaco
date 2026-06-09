package ES2.MonitoramentoCardiaco.controller;

import ES2.MonitoramentoCardiaco.dto.RelatorioResponseDTO;
import ES2.MonitoramentoCardiaco.service.RelatorioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<RelatorioResponseDTO> gerar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(relatorioService.gerarRelatorio(usuarioId));
    }
}
