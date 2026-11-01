package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.chamados;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.exceptions.NegocioException;
import java.time.LocalDateTime;

/**
 * Padrão Template Method (inspirado em OrdemServicoTemplate do AV-CAR)
 * que define o ritual de transição de status do chamado: ABERTO -> ATENDIMENTO -> FECHADO.
 */
public abstract class ChamadoTemplate {

    public final ChamadoModel executar(ChamadoModel chamado) {
        validarTransicao(chamado);
        aplicarMudancaStatus(chamado);
        return chamado;
    }

    protected abstract void validarTransicao(ChamadoModel chamado);

    protected abstract StatusChamado proximoStatus();

    protected void aplicarMudancaStatus(ChamadoModel chamado) {
        StatusChamado destino = proximoStatus();
        chamado.setStatus(destino.name());
        if (destino == StatusChamado.ATENDIMENTO) {
            chamado.setDataAtendimento(LocalDateTime.now());
        } else if (destino == StatusChamado.FECHADO) {
            chamado.setDataFechamento(LocalDateTime.now());
        }
    }

    public static ChamadoModel validarEIniciarAtendimento(ChamadoModel chamado, Long tecnicoId) {
        StatusChamado statusAtual = StatusChamado.fromString(chamado.getStatus());
        if (statusAtual == StatusChamado.FECHADO) {
            throw new NegocioException("Não é permitido alocar técnico em um chamado que já está FECHADO.");
        }
        if (tecnicoId == null || tecnicoId <= 0) {
            throw new NegocioException("É obrigatório informar um técnico válido para iniciar o atendimento.");
        }
        chamado.setTecnicoId(tecnicoId);
        chamado.setStatus(StatusChamado.ATENDIMENTO.name());
        chamado.setDataAtendimento(LocalDateTime.now());
        return chamado;
    }

    public static ChamadoModel validarEEncerrar(ChamadoModel chamado, String solucao) {
        StatusChamado statusAtual = StatusChamado.fromString(chamado.getStatus());
        if (statusAtual == StatusChamado.FECHADO) {
            throw new NegocioException("Este chamado já se encontra FECHADO.");
        }
        if (solucao == null || solucao.trim().isEmpty()) {
            throw new NegocioException("É obrigatório informar a descrição da solução técnica para encerrar o chamado.");
        }
        chamado.setSolucao(solucao.trim());
        chamado.setStatus(StatusChamado.FECHADO.name());
        chamado.setDataFechamento(LocalDateTime.now());
        return chamado;
    }
}

