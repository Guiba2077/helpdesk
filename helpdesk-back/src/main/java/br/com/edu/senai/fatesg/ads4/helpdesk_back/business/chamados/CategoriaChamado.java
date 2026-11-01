package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.chamados;

import lombok.Getter;

@Getter
public enum CategoriaChamado {
    REDE("Infraestrutura e Rede"),
    HARDWARE("Hardware e Equipamentos"),
    SOFTWARE("Sistemas e Software"),
    BANCO_DADOS("Banco de Dados"),
    ACESSO("Segurança e Permissões de Acesso");

    private final String descricao;

    CategoriaChamado(String descricao) {
        this.descricao = descricao;
    }

    public static CategoriaChamado fromString(String valor) {
        if (valor == null || valor.isBlank()) {
            return SOFTWARE;
        }
        for (CategoriaChamado c : values()) {
            if (c.name().equalsIgnoreCase(valor.trim()) || c.descricao.equalsIgnoreCase(valor.trim())) {
                return c;
            }
        }
        return SOFTWARE;
    }
}

