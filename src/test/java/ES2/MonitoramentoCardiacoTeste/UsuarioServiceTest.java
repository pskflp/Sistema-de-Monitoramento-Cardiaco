package ES2.MonitoramentoCardiacoTeste;

import ES2.MonitoramentoCardiaco.dto.LoginRequestDTO;
import ES2.MonitoramentoCardiaco.dto.UsuarioCreateDTO;
import ES2.MonitoramentoCardiaco.dto.UsuarioResponseDTO;
import ES2.MonitoramentoCardiaco.model.Usuario;
import ES2.MonitoramentoCardiaco.repository.UsuarioRepository;
import ES2.MonitoramentoCardiaco.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCriarContaComSucesso() {
        UsuarioCreateDTO dto = new UsuarioCreateDTO(
                "João", "Silva", "joao@email.com", "11999999999",
                "senha123", "senha123", LocalDate.of(1990, 1, 1), "M", "Brasil"
        );

        Usuario usuarioSalvo = new Usuario();
        usuarioSalvo.setId(1L);
        usuarioSalvo.setNome("João");
        usuarioSalvo.setSobrenome("Silva");
        usuarioSalvo.setEmail("joao@email.com");
        usuarioSalvo.setTelefone("11999999999");
        usuarioSalvo.setSenha("senha123");
        usuarioSalvo.setDataNascimento(LocalDate.of(1990, 1, 1));
        usuarioSalvo.setSexo("M");
        usuarioSalvo.setPais("Brasil");

        when(usuarioRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);

        UsuarioResponseDTO response = usuarioService.criarConta(dto);

        assertNotNull(response);
        assertEquals("joao@email.com", response.getEmail());
        assertEquals("João", response.getNome());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        UsuarioCreateDTO dto = new UsuarioCreateDTO(
                "João", "Silva", "joao@email.com", "11999999999",
                "senha123", "senha123", LocalDate.of(1990, 1, 1), "M", "Brasil"
        );

        when(usuarioRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.of(new Usuario()));

        assertThrows(IllegalArgumentException.class, () -> usuarioService.criarConta(dto));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveRealizarLoginComSucesso() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João");
        usuario.setSobrenome("Silva");
        usuario.setEmail("joao@email.com");
        usuario.setSenha("senha123");
        usuario.setTelefone("11999999999");
        usuario.setDataNascimento(LocalDate.of(1990, 1, 1));
        usuario.setSexo("M");
        usuario.setPais("Brasil");

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("joao@email.com");
        dto.setSenha("senha123");

        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(usuario));

        UsuarioResponseDTO response = usuarioService.login(dto);

        assertNotNull(response);
        assertEquals("joao@email.com", response.getEmail());
    }

    @Test
    void deveLancarExcecaoQuandoSenhaIncorreta() {
        Usuario usuario = new Usuario();
        usuario.setEmail("joao@email.com");
        usuario.setSenha("senha123");

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("joao@email.com");
        dto.setSenha("senhaErrada");

        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(usuario));

        assertThrows(IllegalArgumentException.class, () -> usuarioService.login(dto));
    }
}