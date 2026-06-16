package ES2.MonitoramentoCardiaco.repository;

import ES2.MonitoramentoCardiaco.model.MonitoramentoCardiaco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MonitoramentoRepository extends JpaRepository<MonitoramentoCardiaco, Long> {

    List<MonitoramentoCardiaco> findByUsuarioIdOrderByDataRegistroDesc(Long usuarioId);
}