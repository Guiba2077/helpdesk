package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.tecnicos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EspecialidadeJpaRepository extends JpaRepository<EspecialidadeModel, Long> {

    List<EspecialidadeModel> findAllByAtivoTrueOrderByNomeAsc();

    Optional<EspecialidadeModel> findByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCase(String codigo);
}

