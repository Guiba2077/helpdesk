package br.com.edu.senai.fatesg.ads4.helpdesk_back.core.exceptions;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.controllers.response.ApiResponse;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<ApiResponse<Void>> handleNaoEncontrado(EntidadeNaoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.erro(ex.getMessage()));
    }

    @ExceptionHandler({NegocioException.class, BusinessException.class})
    public ResponseEntity<ApiResponse<Void>> handleNegocio(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.erro(ex.getMessage()));
    }

    @ExceptionHandler(FieldValidationException.class)
    public ResponseEntity<ErrorResponse> handleFieldValidation(FieldValidationException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .message(ex.getMessage())
                .severity(ex.getSeverity())
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RuleValidationException.class)
    public ResponseEntity<ErrorResponse> handleRuleValidation(RuleValidationException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .message(ex.getMessage())
                .severity(ex.getSeverity())
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .build();
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleBeanValidation(MethodArgumentNotValidException ex) {
        List<String> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.erro("Erro de validação nos dados enviados.", erros));
    }

    @ExceptionHandler({QueryTimeoutException.class, TimeoutBancoException.class})
    public ResponseEntity<ApiResponse<Void>> handleTimeoutBanco(RuntimeException ex) {
        String msg = "Operação cancelada pelo PostgreSQL (statement_timeout excedido): "
                + "consultas ou relatórios pesados são bloqueados para proteger a disponibilidade do banco.";
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body(ApiResponse.erro(msg, List.of(ex.getMessage())));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataAccessException(DataAccessException ex) {
        Throwable root = ex.getMostSpecificCause();
        if (root instanceof SQLException sqlEx) {
            String sqlState = sqlEx.getSQLState();
            // 57014 = query_canceled (statement_timeout no PostgreSQL)
            if ("57014".equals(sqlState)) {
                String msg = "Consulta cancelada pelo servidor PostgreSQL (SQLState 57014 - statement_timeout curto ativo).";
                return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                        .body(ApiResponse.erro(msg, List.of(sqlEx.getMessage())));
            }
            // 42501 = insufficient_privilege (RBAC: rl_leitor tentando escrever ou violação de RLS)
            if ("42501".equals(sqlState)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.erro("Permissão negada no PostgreSQL (RBAC/RLS): " + sqlEx.getMessage()));
            }
            // P0001 / P0002 = RAISE EXCEPTION em Procedures ou Triggers PL/pgSQL
            if (sqlState != null && sqlState.startsWith("P00")) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(ApiResponse.erro(sqlEx.getMessage()));
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.erro("Erro de acesso ao banco de dados: " + root.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.erro("Erro interno: " + ex.getMessage()));
    }
}

