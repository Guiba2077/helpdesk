package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.chamados;

import lombok.Getter;

@Getter
public enum PrioridadeChamado {
    BAIXA("Baixa", 48),
    MEDIA("Média", 24),
    ALTA("Alta", 8),
    CRITICA("Crítica", 2);

    private final String rotulo;
    private final int slaHorasPadrao;

    PrioridadeChamado(String rotulo, int slaHorasPadrao) {
        this.rotulo = rotulo;
        this.slaHorasPadrao = slaHorasPadrao;
    }

    public static PrioridadeChamado fromString(String valor) {
        if (valor == null || valor.isBlank()) {
            return MEDIA;
        }
        String limpo = valor.trim();
        for (PrioridadeChamado p : values()) {
            if (p.name().equalsIgnoreCase(limpo) || p.rotulo.equalsIgnoreCase(limpo)) {
                return p;
            }
        }
        return MEDIA;
    }
}

