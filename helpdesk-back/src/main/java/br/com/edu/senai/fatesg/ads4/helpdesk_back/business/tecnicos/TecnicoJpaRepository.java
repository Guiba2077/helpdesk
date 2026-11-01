package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.tecnicos;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("businessTecnicoJpaRepository")
public interface TecnicoJpaRepository extends JpaRepository<TecnicoModel, Long> {

    @Query("""
            SELECT t FROM TecnicoModel t
             WHERE t.disponivel = true AND t.ativo = true
             ORDER BY t.id
            """)
    List<TecnicoModel> findDisponiveis();

    List<TecnicoModel> findByAtivoTrueOrderById();

    java.util.Optional<TecnicoModel> findByUsuarioId(Long usuarioId);

    List<TecnicoModel> findAllByOrderById();

    @Modifying
    @Query("UPDATE TecnicoModel t SET t.disponivel = CASE WHEN t.disponivel = true THEN false ELSE true END WHERE t.id = :id")
    void alternarDisponibilidade(@Param("id") Long id);

    @Modifying
    @Query("UPDATE TecnicoModel t SET t.ativo = CASE WHEN t.ativo = true THEN false ELSE true END WHERE t.id = :id")
    void alternarStatus(@Param("id") Long id);

    @Modifying
    @Query("UPDATE TecnicoModel t SET t.ativo = false WHERE t.id = :id")
    void inativar(@Param("id") Long id);
}

