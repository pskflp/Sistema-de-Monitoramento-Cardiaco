package ES2.MonitoramentoCardiaco.controller;

import ES2.MonitoramentoCardiaco.dto.MonitoramentoCreateDTO;
import ES2.MonitoramentoCardiaco.dto.MonitoramentoResponseDTO;
import ES2.MonitoramentoCardiaco.service.MonitoramentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/monitoramentos")
public class MonitoramentoController {

    private final MonitoramentoService monitoramentoService;

    public MonitoramentoController(MonitoramentoService monitoramentoService) {
        this.monitoramentoService = monitoramentoService;
    }

    @PostMapping
    public ResponseEntity<MonitoramentoResponseDTO> registrar(@RequestBody @Valid MonitoramentoCreateDTO dto) {
        MonitoramentoResponseDTO response = monitoramentoService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<MonitoramentoResponseDTO>> listar(@PathVariable Long usuarioId) {
        List<MonitoramentoResponseDTO> lista = monitoramentoService.listarPorUsuario(usuarioId);
        return ResponseEntity.ok(lista);
    }
}