package br.com.edu.senai.fatesg.ads4.helpdesk_back.core.controllers.response;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean sucesso;
    private String mensagem;
    private T dados;
    private List<String> erros;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> ok(T dados, String mensagem) {
        return ApiResponse.<T>builder()
                .sucesso(true)
                .mensagem(mensagem)
                .dados(dados)
                .erros(Collections.emptyList())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> erro(String mensagemErro) {
        return ApiResponse.<T>builder()
                .sucesso(false)
                .mensagem(mensagemErro)
                .dados(null)
                .erros(Collections.singletonList(mensagemErro))
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> erro(String mensagemErro, List<String> detalhes) {
        return ApiResponse.<T>builder()
                .sucesso(false)
                .mensagem(mensagemErro)
                .dados(null)
                .erros(detalhes)
                .timestamp(LocalDateTime.now())
                .build();
    }
}

