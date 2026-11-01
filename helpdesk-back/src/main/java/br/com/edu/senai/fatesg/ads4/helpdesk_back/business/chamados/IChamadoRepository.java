package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.chamados;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.repositories.Repository;
import java.util.List;

public interface IChamadoRepository extends Repository<ChamadoModel> {

    List<ChamadoModel> buscarPorStatus(String status);

    void alocarTecnicoViaProcedure(Long chamadoId, Long tecnicoId, Long usuarioResponsavelId);

    void atualizarPrioridade(Long chamadoId, String novaPrioridade, String justificativa);

    void encerrarChamado(Long chamadoId, String solucao);

    List<HistoricoChamado> listarHistoricoPorChamado(Long chamadoId);

    ApontamentoAtendimento adicionarApontamento(Long chamadoId, Long autorId, String descricao);

    List<ApontamentoAtendimento> listarApontamentosPorChamado(Long chamadoId);

    DashboardChamadoDTO obterResumoDashboard();
}

