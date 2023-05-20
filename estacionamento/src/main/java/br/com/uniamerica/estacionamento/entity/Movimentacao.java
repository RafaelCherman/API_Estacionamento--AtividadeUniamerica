package br.com.uniamerica.estacionamento.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Audited
@Table(name = "movimentacoes", schema = "public")
@AuditTable(value = "movimentacoes_audit", schema = "audit")
public class Movimentacao extends AbstractEntity{

    @Getter @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "veiculo", nullable = false)
    private Veiculo veiculo;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "condutor", nullable = false)
    private Condutor condutor;

    @Getter @Setter
    @Column(name = "hora_da_entrada", nullable = false)
    private LocalDateTime entrada;

    @Getter @Setter
    @Column(name = "hora_da_saida")
    private LocalDateTime saida;

    @Getter @Setter
    @Column(name = "tempo_total_hora")
    private int tempoHora;

    @Getter @Setter
    @Column(name = "tempo_total_minuto")
    private int tempoMinuto;

    @Getter @Setter
    @Column(name = "tempo_do_desconto")
    private int tempoDesconto;

    @Getter @Setter
    @Column(name = "tempo_da_multa_hora")
    private int tempoMultaHora;

    @Getter @Setter
    @Column(name = "tempo_da_multa_minuto")
    private int tempoMultaMinuto;


    @Getter @Setter
    @Column(name = "valor_do_desconto")
    private BigDecimal valorDesconto;

    @Getter @Setter
    @Column(name = "valor_da_multa")
    private BigDecimal valorMulta;

    @Getter @Setter
    @Column(name = "valor_total")
    private BigDecimal valorTotal;

    @Getter @Setter
    @Column(name = "valor_da_hora")
    private BigDecimal valorHora;

    @Getter @Setter
    @Column(name = "valor_da_hora_da_multa")
    private BigDecimal valorHoraMulta;

}
