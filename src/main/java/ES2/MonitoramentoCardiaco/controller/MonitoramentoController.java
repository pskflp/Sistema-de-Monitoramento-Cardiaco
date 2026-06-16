package ES2.MonitoramentoCardiaco.controller;

import ES2.MonitoramentoCardiaco.dto.MonitoramentoCreateDTO;
import ES2.MonitoramentoCardiaco.dto.MonitoramentoResponseDTO;
import ES2.MonitoramentoCardiaco.service.MonitoramentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/monitoramentos")
@Tag(name = "Monitoramento", description = "Registro e consulta de dados de saúde cardíaca")
public class MonitoramentoController {

    private final MonitoramentoService monitoramentoService;

    public MonitoramentoController(MonitoramentoService monitoramentoService) {
        this.monitoramentoService = monitoramentoService;
    }

    @Operation(summary = "Registrar novo monitoramento")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Monitoramento registrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou usuário não encontrado")
    })
    @PostMapping
    public ResponseEntity<MonitoramentoResponseDTO> registrar(@RequestBody @Valid MonitoramentoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(monitoramentoService.registrar(dto));
    }

    @Operation(summary = "Listar monitoramentos de um usuário")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Usuário não encontrado")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<MonitoramentoResponseDTO>> listar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(monitoramentoService.listarPorUsuario(usuarioId));
    }
}