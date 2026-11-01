package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.chamados;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApontamentoJpaRepository extends JpaRepository<ApontamentoAtendimento, Long> {

    List<ApontamentoAtendimento> findByChamadoIdOrderByCriadoEmAsc(Long chamadoId);
}

