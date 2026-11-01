package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.tecnicos;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.repositories.Repository;
import java.util.List;

public interface ITecnicoRepository extends Repository<TecnicoModel> {

    List<TecnicoModel> listarDisponiveis();

    java.util.Optional<TecnicoModel> buscarPorUsuarioId(Long usuarioId);

    List<Especialidade> listarEspecialidades();

    Especialidade cadastrarEspecialidade(Especialidade especialidade);

    void alternarDisponibilidade(Long id);
}

