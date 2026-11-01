package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.chamados;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.domains.BaseModel;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entidade rica de domínio do Agregado Central (Chamado de TI).
 * Equivalente à OrdemServico do projeto AV-CAR.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Chamado extends BaseModel {

    private String protocolo;
    private String titulo;
    private String descricao;
    private CategoriaChamado categoria;
    private PrioridadeChamado prioridade;
    private StatusChamado status;
    private Long solicitanteId;
    private String solicitanteNome;
    private Long tecnicoId;
    private String tecnicoNome;
    private String solucao;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataAtendimento;
    private LocalDateTime dataFechamento;

    @Builder.Default
    private List<HistoricoChamado> historicos = new ArrayList<>();

    @Builder.Default
    private List<ApontamentoAtendimento> apontamentos = new ArrayList<>();

    public void adicionarApontamento(ApontamentoAtendimento apontamento) {
        if (this.apontamentos == null) {
            this.apontamentos = new ArrayList<>();
        }
        this.apontamentos.add(apontamento);
    }
}

