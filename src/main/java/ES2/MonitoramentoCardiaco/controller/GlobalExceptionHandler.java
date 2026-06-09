package ES2.MonitoramentoCardiaco.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Tratamento centralizado de exceções da API.
 * Garante que erros de negócio retornem status HTTP semânticos
 * em vez de 500 Internal Server Error.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Erros de validação de campos (@Valid): 400 Bad Request.
     * Ex: e-mail inválido, campos obrigatórios ausentes.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        String detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity.badRequest().body(corpo(400, "Erro de validação", detalhes));
    }

    /**
     * Regras de negócio violadas (IllegalArgumentException): mapeadas
     * para 400, 401 ou 409 conforme a mensagem.
     *
     * Casos mapeados:
     *  - senhas não coincidem       → 400 Bad Request
     *  - e-mail já cadastrado       → 409 Conflict
     *  - usuário não encontrado     → 404 Not Found
     *  - senha incorreta            → 401 Unauthorized
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex) {

        String msg = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";

        if (msg.contains("senha") && msg.contains("não coincidem")) {
            return ResponseEntity.badRequest()
                    .body(corpo(400, "Dados inválidos", ex.getMessage()));
        }
        if (msg.contains("já existe") || msg.contains("e-mail")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(corpo(409, "Conflito", ex.getMessage()));
        }
        if (msg.contains("senha incorreta")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(corpo(401, "Não autorizado", ex.getMessage()));
        }
        if (msg.contains("não encontrado")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(corpo(404, "Não encontrado", ex.getMessage()));
        }

        // Fallback genérico para outros erros de negócio.
        return ResponseEntity.badRequest()
                .body(corpo(400, "Requisição inválida", ex.getMessage()));
    }

    private Map<String, Object> corpo(int status, String erro, String mensagem) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status);
        body.put("erro", erro);
        body.put("mensagem", mensagem);
        return body;
    }
}