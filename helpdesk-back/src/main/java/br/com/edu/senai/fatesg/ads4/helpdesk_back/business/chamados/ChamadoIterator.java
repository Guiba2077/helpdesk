package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.chamados;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Padrão Iterator (inspirado em OrdemServicoIterator do AV-CAR)
 * para percorrer chamados filtrando por StatusChamado.
 */
public class ChamadoIterator implements Iterator<ChamadoModel> {

    private final List<ChamadoModel> chamados;
    private final StatusChamado statusFiltro;
    private int proximoIndex = -1;
    private int posicaoAtual = 0;

    public ChamadoIterator(List<ChamadoModel> chamados, StatusChamado statusFiltro) {
        this.chamados = chamados != null ? chamados : List.of();
        this.statusFiltro = statusFiltro;
        avancarParaProximo();
    }

    private void avancarParaProximo() {
        proximoIndex = -1;
        while (posicaoAtual < chamados.size()) {
            ChamadoModel candidato = chamados.get(posicaoAtual);
            if (statusFiltro == null
                    || StatusChamado.fromString(candidato.getStatus()) == statusFiltro) {
                proximoIndex = posicaoAtual;
                break;
            }
            posicaoAtual++;
        }
    }

    @Override
    public boolean hasNext() {
        return proximoIndex >= 0;
    }

    @Override
    public ChamadoModel next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Não há mais chamados para o filtro informado.");
        }
        ChamadoModel atual = chamados.get(proximoIndex);
        posicaoAtual = proximoIndex + 1;
        avancarParaProximo();
        return atual;
    }
}

