package br.com.edu.senai.fatesg.ads4.helpdesk_back.helpers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class FormatadorHelper {

    private static final DateTimeFormatter FORMATTER_DATA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private FormatadorHelper() {
    }

    public static String formatarDataHora(LocalDateTime dataHora) {
        if (dataHora == null) {
            return "";
        }
        return dataHora.format(FORMATTER_DATA_HORA);
    }

    public static String limparTexto(String valor) {
        return valor == null ? "" : valor.trim();
    }
}

