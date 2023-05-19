package br.com.uniamerica.estacionamento.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import java.time.LocalTime;

@Entity
@Audited
@Table(name = "condutores", schema = "public")
@AuditTable(value = "condutores_audit", schema = "audit")
public class Condutor extends AbstractEntity{

    @Getter @Setter
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Getter @Setter
    @Column(name = "cpf", nullable = false, unique = true, length = 15)
    private String cpf;

    @Getter @Setter
    @Column(name = "telefone", nullable = false, length = 17)
    private String telefone;

    @Getter @Setter
    @Column(name = "tempo_pago_hora")
    private int tempoPagoHora;

    @Getter @Setter
    @Column(name = "tempo_pago_minuto")
    private int tempoPagoMinuto;

    @Getter @Setter
    @Column(name = "tempo_desconto_hora")
    private int tempoDescontoHora;

}
