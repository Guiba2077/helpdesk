package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.auditoria;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.domains.BaseModel;
import java.time.LocalDateTime;
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
public class AuditoriaPrioridadeModel extends BaseModel {

    private Long chamadoId;
    private String protocoloChamado;
    private String tituloChamado;
    private String prioridadeAnterior;
    private String prioridadeNova;
    private Long alteradoPorId;
    private String alteradoPorNome;
    private String justificativa;
    private LocalDateTime dataAlteracao;
}

