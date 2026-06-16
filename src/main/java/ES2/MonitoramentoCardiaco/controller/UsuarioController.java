package ES2.MonitoramentoCardiaco.controller;

import ES2.MonitoramentoCardiaco.dto.LoginRequestDTO;
import ES2.MonitoramentoCardiaco.dto.UsuarioCreateDTO;
import ES2.MonitoramentoCardiaco.dto.UsuarioResponseDTO;
import ES2.MonitoramentoCardiaco.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuários", description = "Cadastro e autenticação de usuários")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Cadastrar novo usuário")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou e-mail já cadastrado")
    })
    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@RequestBody @Valid UsuarioCreateDTO dto) {
        UsuarioResponseDTO response = usuarioService.criarConta(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        UsuarioResponseDTO response = usuarioService.login(dto);
        return ResponseEntity.ok(response);
    }
}