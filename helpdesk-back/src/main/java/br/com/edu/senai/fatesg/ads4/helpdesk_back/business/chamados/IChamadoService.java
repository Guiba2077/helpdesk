package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.chamados;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.services.IGenericService;
import java.util.List;

public interface IChamadoService extends IGenericService<ChamadoDTO> {

    ChamadoDTO abrirChamado(ChamadoModel model, Long usuarioLogadoId);

    ChamadoDTO atualizarDadosBasicos(Long id, ChamadoModel model, Long usuarioLogadoId);

    ChamadoDTO alocarTecnico(Long chamadoId, Long tecnicoId, Long usuarioLogadoId);

    ChamadoDTO alterarPrioridade(Long chamadoId, String novaPrioridade, String justificativa, Long usuarioLogadoId);

    ChamadoDTO encerrarChamado(Long chamadoId, String solucao, Long usuarioLogadoId);

    List<ChamadoDTO> listarChamadosPorUsuario(Long usuarioLogadoId, boolean incluirInativos);

    List<ChamadoDTO> buscarPorStatus(String status);

    List<ChamadoDTO> buscarPorStatus(String status, Long usuarioLogadoId);

    List<HistoricoChamadoDTO> listarHistorico(Long chamadoId);

    ApontamentoAtendimentoDTO adicionarApontamento(Long chamadoId, Long autorId, String descricao);

    List<ApontamentoAtendimentoDTO> listarApontamentos(Long chamadoId);

    DashboardChamadoDTO obterResumoDashboard();
}

