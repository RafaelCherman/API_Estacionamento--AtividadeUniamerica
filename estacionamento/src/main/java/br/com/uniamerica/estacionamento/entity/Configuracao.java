package br.com.uniamerica.estacionamento.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Audited
@Table(name = "configuracoes", schema = "public")
@AuditTable(value = "configuracoes_audit", schema = "audit")
public class Configuracao extends AbstractEntity {

    @Getter @Setter
    @Column(name = "valor_da_hora")
    private BigDecimal valorHora;

    @Getter @Setter
    @Column(name = "valor_da_multa")
    private BigDecimal valorMinutoMulta;

    @Getter @Setter
    @Column(name = "inicio_do_expediente")
    private LocalDateTime inicioExpediente;

    @Getter @Setter
    @Column(name = "fim_do_expediente")
    private LocalDateTime fimExpediente;

    @Getter @Setter
    @Column(name = "tempo_para_desconto")
    private int tempoParaDesconto;

    @Getter @Setter
    @Column(name = "tempo_de_desconto")
    private int tempoDeDesconto;

    @Getter @Setter
    @Column(name = "desconto_ativo")
    private boolean gerarDesconto;

    @Getter @Setter
    @Column(name = "vagas_de_moto")
    private int vagasMoto;

    @Getter @Setter
    @Column(name = "vagas_de_carro")
    private int vagasCarro;

    @Getter @Setter
    @Column(name = "vagas_de_van")
    private int vagasVan;

}
