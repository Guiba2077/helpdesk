package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.auditoria;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("businessAuditoriaPrioridadeJpaRepository")
public interface AuditoriaPrioridadeJpaRepository extends JpaRepository<AuditoriaPrioridadeModel, Long> {

    List<AuditoriaPrioridadeModel> findByChamadoIdOrderByDataAlteracaoDesc(Long chamadoId);

    @Query("SELECT a FROM AuditoriaPrioridadeModel a ORDER BY a.dataAlteracao DESC")
    List<AuditoriaPrioridadeModel> findAllOrderByDataAlteracaoDesc();
}

