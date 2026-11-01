package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.tecnicos;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.exceptions.EntidadeNaoEncontradaException;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.services.GenericService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("businessTecnicoService")
public class TecnicoService extends GenericService<TecnicoModel, TecnicoDTO, ITecnicoRepository>
        implements ITecnicoService {

    private final TecnicoMapper mapper;
    private final ITecnicoValidation validation;

    public TecnicoService(
            ITecnicoRepository repository,
            TecnicoMapper mapper,
            ITecnicoValidation validation) {
        super(repository);
        this.mapper = mapper;
        this.validation = validation;
    }

    @Override
    protected TecnicoDTO toDTO(TecnicoModel model) {
        return mapper.toDto(model);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TecnicoDTO> listarDisponiveis() {
        return repository.listarDisponiveis().stream().map(mapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EspecialidadeDTO> listarEspecialidades() {
        return repository.listarEspecialidades().stream()
                .map(e -> new EspecialidadeDTO(e.getCodigo(), e.getNome(), e.getNivelAtendimento()))
                .toList();
    }

    @Override
    @Transactional
    public TecnicoDTO salvar(TecnicoModel model) {
        validation.validar(model);
        return executarComTratamentoDuplicidade(() -> mapper.toDto(repository.salvar(model)));
    }

    @Override
    @Transactional
    public TecnicoDTO atualizar(Long id, TecnicoModel model) {
        TecnicoModel existente = repository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Técnico não encontrado com ID: " + id));
        model.setId(existente.getId());
        model.setUsuarioId(existente.getUsuarioId());
        validation.validar(model);
        return executarComTratamentoDuplicidade(() -> {
            repository.atualizar(model);
            return buscarPorId(id);
        });
    }

    @Override
    @Transactional
    public TecnicoDTO alternarDisponibilidade(Long id) {
        repository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Técnico não encontrado com ID: " + id));
        repository.alternarDisponibilidade(id);
        return buscarPorId(id);
    }
}

