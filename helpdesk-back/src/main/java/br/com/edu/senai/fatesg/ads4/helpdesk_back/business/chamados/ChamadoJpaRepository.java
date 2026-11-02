package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.chamados;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("businessChamadoJpaRepository")
public interface ChamadoJpaRepository extends JpaRepository<ChamadoModel, Long> {

    List<ChamadoModel> findByStatusIgnoreCaseAndAtivoTrueOrderByDataAberturaDesc(String status);

    List<ChamadoModel> findByAtivoTrueOrderByDataAberturaDesc();

    List<ChamadoModel> findAllByOrderByDataAberturaDesc();

    // clearAutomatically: a procedure altera o chamado por fora do Hibernate, então o cache
    // de primeiro nível é limpo para que a leitura seguinte traga o status atualizado do banco
    @Modifying(clearAutomatically = true)
    @Query(value = "CALL app.sp_alocar_chamado(:chamadoId, :tecnicoId, :usuarioResponsavelId)", nativeQuery = true)
    void chamarSpAlocarChamado(
            @Param("chamadoId") Long chamadoId,
            @Param("tecnicoId") Long tecnicoId,
            @Param("usuarioResponsavelId") Long usuarioResponsavelId);

    @Modifying(clearAutomatically = true)
    @Query(value = """
            UPDATE app.chamado
               SET status = 'FECHADO',
                   solucao = :solucao,
                   fechado_em = now(),
                   atualizado_em = now()
             WHERE id = :chamadoId
            """, nativeQuery = true)
    void encerrarChamado(@Param("chamadoId") Long chamadoId, @Param("solucao") String solucao);

    @Modifying
    @Query("UPDATE ChamadoModel c SET c.ativo = CASE WHEN c.ativo = true THEN false ELSE true END WHERE c.id = :id")
    void alternarStatus(@Param("id") Long id);

    @Modifying
    @Query("UPDATE ChamadoModel c SET c.ativo = false WHERE c.id = :id")
    void inativar(@Param("id") Long id);

    @Query(value = """
            SELECT COUNT(*)::BIGINT AS total,
                   COUNT(CASE WHEN status = 'ABERTO' THEN 1 END)::BIGINT AS abertos,
                   COUNT(CASE WHEN status = 'ATENDIMENTO' THEN 1 END)::BIGINT AS em_atendimento,
                   COUNT(CASE WHEN status = 'FECHADO' THEN 1 END)::BIGINT AS fechados,
                   COUNT(CASE WHEN prioridade = 'CRITICA' AND status <> 'FECHADO' THEN 1 END)::BIGINT AS criticos
              FROM app.chamado
             WHERE ativo = true
            """, nativeQuery = true)
    List<Object[]> obterResumoDashboardRaw();
}

