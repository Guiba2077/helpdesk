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

    /**
     * Valida se o chamado pode receber um técnico. Não altera a entidade: a mudança de
     * status, técnico e data de atendimento é feita pela procedure app.sp_alocar_chamado.
     * Alterar a entidade gerenciada aqui faria o Hibernate gravar o novo status antes da
     * procedure rodar, e ela rejeitaria o chamado por já estar em ATENDIMENTO.
     */
    public static void validarAlocacaoTecnico(ChamadoModel chamado, Long tecnicoId) {
        StatusChamado statusAtual = StatusChamado.fromString(chamado.getStatus());
        if (statusAtual == StatusChamado.FECHADO) {
            throw new NegocioException("Não é permitido alocar técnico em um chamado que já está FECHADO.");
        }
        if (statusAtual == StatusChamado.ATENDIMENTO) {
            throw new NegocioException("Este chamado já possui um técnico alocado e está em ATENDIMENTO.");
        }
        if (tecnicoId == null || tecnicoId <= 0) {
            throw new NegocioException("É obrigatório informar um técnico válido para iniciar o atendimento.");
        }
    }

    /**
     * Valida se o chamado pode ser encerrado e devolve a solução normalizada. Não altera a
     * entidade: a gravação do status FECHADO é feita pela query nativa do repositório.
     */
    public static String validarEncerramento(ChamadoModel chamado, String solucao) {
        StatusChamado statusAtual = StatusChamado.fromString(chamado.getStatus());
        if (statusAtual == StatusChamado.FECHADO) {
            throw new NegocioException("Este chamado já se encontra FECHADO.");
        }
        if (statusAtual != StatusChamado.ATENDIMENTO) {
            throw new NegocioException(
                    "O chamado precisa estar em status ATENDIMENTO e possuir um técnico alocado antes de ser encerrado.");
        }
        if (solucao == null || solucao.trim().isEmpty()) {
            throw new NegocioException("É obrigatório informar a descrição da solução técnica para encerrar o chamado.");
        }
        return solucao.trim();
    }
}

