package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.tecnicos;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.services.IGenericService;
import java.util.List;

public interface ITecnicoService extends IGenericService<TecnicoDTO> {

    List<TecnicoDTO> listarDisponiveis();

    List<EspecialidadeDTO> listarEspecialidades();

    TecnicoDTO salvar(TecnicoModel model);

    TecnicoDTO atualizar(Long id, TecnicoModel model);

    TecnicoDTO alternarDisponibilidade(Long id);
}

