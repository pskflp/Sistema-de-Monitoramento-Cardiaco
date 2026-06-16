package ES2.MonitoramentoCardiaco.controller;

import ES2.MonitoramentoCardiaco.dto.RelatorioResponseDTO;
import ES2.MonitoramentoCardiaco.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/relatorios")
@Tag(name = "Relatórios", description = "Geração de relatórios de saúde cardíaca")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @Operation(summary = "Gerar relatório de saúde de um usuário")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Usuário não encontrado ou sem registros")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<RelatorioResponseDTO> gerar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(relatorioService.gerarRelatorio(usuarioId));
    }
}