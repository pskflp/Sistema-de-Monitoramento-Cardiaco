package ES2.MonitoramentoCardiaco.controller;

import ES2.MonitoramentoCardiaco.dto.LoginRequestDTO;
import ES2.MonitoramentoCardiaco.dto.UsuarioCreateDTO;
import ES2.MonitoramentoCardiaco.dto.UsuarioResponseDTO;
import ES2.MonitoramentoCardiaco.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ES2.MonitoramentoCardiaco.dto.LoginRequestDTO;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@RequestBody @Valid UsuarioCreateDTO dto) {
        UsuarioResponseDTO response = usuarioService.criarConta(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } @PostMapping("/login")
public ResponseEntity<UsuarioResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
    UsuarioResponseDTO response = usuarioService.login(dto);
    return ResponseEntity.ok(response);
}
}