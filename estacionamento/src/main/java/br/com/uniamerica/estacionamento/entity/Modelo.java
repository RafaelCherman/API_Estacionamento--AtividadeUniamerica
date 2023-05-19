package br.com.uniamerica.estacionamento.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "modelos", schema = "public")
@AuditTable(value = "modelos_audit", schema = "audit")
public class Modelo extends AbstractEntity{

    @Getter @Setter
    @Column(name = "nome_do_modelo", length = 50, unique = true, nullable = false)
    private String nome;

    @Getter @Setter
    @ManyToOne//(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "marca", nullable = false)
    private Marca marca;

}
