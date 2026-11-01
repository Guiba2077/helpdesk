package br.com.edu.senai.fatesg.ads4.helpdesk_back.core.domains;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private LocalDateTime dataCadastro = LocalDateTime.now();

    @Transient
    private boolean ativo = true;

    protected BaseModel(Long id, boolean ativo) {
        this.id = id;
        this.ativo = ativo;
        this.dataCadastro = LocalDateTime.now();
    }
}

