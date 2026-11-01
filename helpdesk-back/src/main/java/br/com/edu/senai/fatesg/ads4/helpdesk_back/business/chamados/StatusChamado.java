package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.chamados;

import lombok.Getter;

@Getter
public enum StatusChamado {
    ABERTO("Aberto", "Chamado aguardando alocação de técnico"),
    ATENDIMENTO("Atendimento", "Chamado em atendimento por técnico alocado"),
    FECHADO("Fechado", "Chamado solucionado e encerrado");

    private final String rotulo;
    private final String descricao;

    StatusChamado(String rotulo, String descricao) {
        this.rotulo = rotulo;
        this.descricao = descricao;
    }

    public static StatusChamado fromString(String valor) {
        if (valor == null || valor.isBlank()) {
            return ABERTO;
        }
        String limpo = valor.trim();
        for (StatusChamado s : values()) {
            if (s.name().equalsIgnoreCase(limpo) || s.rotulo.equalsIgnoreCase(limpo)) {
                return s;
            }
        }
        return ABERTO;
    }
}

