package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.chamados;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.dtos.BaseDTO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ChamadoDTO extends BaseDTO {

    private String protocolo;
    private String titulo;
    private String descricao;
    private String categoria;
    private String prioridade;
    private String status;
    private Long solicitanteId;
    private String solicitanteNome;
    private String solicitanteEmail;
    private String solicitanteSetor;
    private Long tecnicoId;
    private String tecnicoNome;
    private String tecnicoEspecialidade;
    private String solucao;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataAtendimento;
    private LocalDateTime dataFechamento;
    private List<HistoricoChamadoDTO> historicos;
    private List<ApontamentoAtendimentoDTO> apontamentos;
}

