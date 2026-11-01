package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios;

import lombok.Getter;

@Getter
public enum PerfilUsuario {
    ADMIN("Administrador"),
    TECNICO("Técnico de TI"),
    OPERADOR("Operador / Solicitante");

    private final String descricao;

    PerfilUsuario(String descricao) {
        this.descricao = descricao;
    }

    public static PerfilUsuario fromString(String valor) {
        if (valor == null || valor.isBlank()) {
            return OPERADOR;
        }
        for (PerfilUsuario p : values()) {
            if (p.name().equalsIgnoreCase(valor.trim()) || p.descricao.equalsIgnoreCase(valor.trim())) {
                return p;
            }
        }
        return OPERADOR;
    }
}

