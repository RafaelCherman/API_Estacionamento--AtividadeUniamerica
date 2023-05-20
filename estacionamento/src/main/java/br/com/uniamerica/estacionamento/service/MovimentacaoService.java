package br.com.uniamerica.estacionamento.service;

import br.com.uniamerica.estacionamento.entity.Condutor;
import br.com.uniamerica.estacionamento.entity.Configuracao;
import br.com.uniamerica.estacionamento.entity.Movimentacao;
import br.com.uniamerica.estacionamento.repository.CondutorRepository;
import br.com.uniamerica.estacionamento.repository.ConfiguracaoRepository;
import br.com.uniamerica.estacionamento.repository.MovimentacaoRepository;
import br.com.uniamerica.estacionamento.repository.VeiculoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class MovimentacaoService {

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private CondutorRepository condutorRepository;

    @Autowired
    private ConfiguracaoRepository configuracaoRepository;

    @Transactional(rollbackOn = Exception.class)
    public List<Movimentacao> aberto()
    {
        List<Movimentacao> lista = this.movimentacaoRepository.findByAberto();
        if(lista.size() == 0)
        {
            throw new RuntimeException("Lista vazia");
        }

        return lista;
    }

    @Transactional(rollbackOn = Exception.class)
    public List<Movimentacao> tudo()
    {
        List<Movimentacao> lista = this.movimentacaoRepository.findAll();
        if(lista.size() == 0)
        {
            throw new RuntimeException("Lista vazia");
        }

        return lista;
    }

    @Transactional(rollbackOn = Exception.class)
    public void cadastrar(final Movimentacao movimentacao){


        Assert.notNull(movimentacao.getCondutor(), "Condutor não pode ser nulo.");
        Assert.isTrue(condutorRepository.doesExist(movimentacao.getCondutor().getId()), "Condutor não existe");
        Assert.notNull(movimentacao.getVeiculo(), "Veiculo não pode ser nulo.");
        Assert.isTrue(veiculoRepository.doesExist(movimentacao.getVeiculo().getId()), "Veiculo não existe");
        Assert.notNull(movimentacao.getEntrada(), "Entrada não pode ser nula");




        if(movimentacao.getSaida() != null)
        {
            calculaTempoEstacionado(movimentacao);
        }

        this.movimentacaoRepository.save(movimentacao);

    }

    @Transactional(rollbackOn = Exception.class)
    public void editar(final Movimentacao movimentacao, final Long urlId)
    {
        Assert.isTrue(movimentacaoRepository.doesExist(urlId), "Movimentacao não existe.");
        Assert.isTrue(movimentacao.getId().equals(urlId), "Não foi possivel identificar o registro informado.");
        Assert.notNull(movimentacao.getCondutor(), "Condutor não pode ser nulo.");
        Assert.isTrue(condutorRepository.doesExist(movimentacao.getCondutor().getId()), "Condutor não existe");
        Assert.notNull(movimentacao.getVeiculo(), "Veiculo não pode ser nulo.");
        Assert.isTrue(veiculoRepository.doesExist(movimentacao.getVeiculo().getId()), "Veiculo não existe");
        Assert.notNull(movimentacao.getEntrada(), "Entrada não pode ser nula");




        if(movimentacao.getSaida() != null)
        {
            calculaTempoEstacionado(movimentacao);
        }

        this.movimentacaoRepository.save(movimentacao);
    }

    @Transactional(rollbackOn = Exception.class)
    public void deleta(final Long urlId) {
        Movimentacao movimentacao = this.movimentacaoRepository.findById(urlId).orElse(null);

        Assert.notNull(movimentacao, "Movimentacao não existe.");

        movimentacao.setAtivo(false);
        this.movimentacaoRepository.save(movimentacao);

    }


    private void calculaTempoEstacionado(Movimentacao movimentacao)
    {
        int umDiaS = 86400;
        Configuracao objetoConfig = configuracaoRepository.getConfig();
        int tempoEstacionado = 0;
        int tempoForaMulta = 0;
        int tempoNaMulta = 0;
        boolean c = true;

        LocalTime entrada = objetoConfig.getInicioExpediente();
        LocalTime saida = objetoConfig.getFimExpediente();

        int tempoDeExpediente = ((int) Duration.between(entrada, saida).getSeconds());
        int tempoForaDeExpediente = umDiaS - tempoDeExpediente;
        Long diasEstacionado = ChronoUnit.DAYS.between(movimentacao.getEntrada(), movimentacao.getSaida());



        int tempoSaidaInicio = (((int) Duration.between(movimentacao.getSaida().toLocalTime(), objetoConfig.getInicioExpediente()).getSeconds()));
        int tempoEntradaFim = ((int) Duration.between(movimentacao.getEntrada().toLocalTime(), objetoConfig.getFimExpediente()).getSeconds());
        if(tempoSaidaInicio < 0)
        {
            tempoSaidaInicio = (-1)*tempoSaidaInicio;
            tempoSaidaInicio = umDiaS - tempoSaidaInicio;
        }
        if(tempoEntradaFim < 0)
        {
            tempoEntradaFim = (-1)*tempoEntradaFim;
            tempoEntradaFim = umDiaS - tempoEntradaFim;
        }

        tempoEstacionado = tempoEntradaFim;


        tempoEstacionado = ((int) Duration.between(movimentacao.getEntrada(), movimentacao.getSaida()).getSeconds());


        System.out.println("\n\nExpediente: "+tempoDeExpediente+"\n\nFora de Expediente: "+tempoForaDeExpediente+"\n\nDias estacioado: "+diasEstacionado+"\n\n");
        System.out.println(" entre saida e inicio "+ tempoSaidaInicio +"\n\n");
        System.out.println(" tempoForaDeExpediente - tempoSaidaInicio "+ (tempoForaDeExpediente - tempoSaidaInicio) +"\n\n");

        System.out.println(" entre entrada e fim "+ tempoEntradaFim +"\n\n");
        System.out.println(" tempoDeExpediente - tempoEntradaFim "+ (tempoDeExpediente - tempoEntradaFim) +"\n\n");

        System.out.println(" tempoForaDeExpediente - tempoEntradaFim "+ (tempoForaDeExpediente - tempoEntradaFim) +"\n\n");



        if(movimentacao.getEntrada().toLocalTime().isBefore(objetoConfig.getInicioExpediente()) &&
                movimentacao.getSaida().toLocalTime().isBefore(objetoConfig.getInicioExpediente()) ||

                movimentacao.getEntrada().toLocalTime().isAfter(objetoConfig.getFimExpediente()) &&
                        movimentacao.getSaida().toLocalTime().isBefore(objetoConfig.getInicioExpediente()) ||

                movimentacao.getEntrada().toLocalTime().isAfter(objetoConfig.getFimExpediente()) &&
                        movimentacao.getSaida().toLocalTime().isAfter(objetoConfig.getFimExpediente()))
        {
            tempoNaMulta += ((int) Duration.between(movimentacao.getEntrada().toLocalTime(), movimentacao.getSaida().toLocalTime()).getSeconds());
            c = false;
            System.out.println(" if-01 \n\n");
        }
        else if (movimentacao.getEntrada().toLocalTime().isBefore(objetoConfig.getInicioExpediente()) ||
                movimentacao.getEntrada().toLocalTime().isAfter(objetoConfig.getFimExpediente()))
        {
            tempoNaMulta += ((int) Duration.between(movimentacao.getEntrada().toLocalTime(), objetoConfig.getInicioExpediente()).getSeconds());
            System.out.println(" if-02 \n\n");
        }

        if (tempoNaMulta < 0)
        {
            tempoNaMulta = (-1)*tempoNaMulta;
            tempoNaMulta = umDiaS - tempoNaMulta;
            System.out.println(" if-03 \n\n");
        }


        /*if(movimentacao.getSaida().toLocalTime().isAfter(objetoConfig.getFimExpediente()))
        {
            tempoNaMulta += ((int) Duration.between(objetoConfig.getFimExpediente(), movimentacao.getSaida().toLocalTime()).getSeconds());
        }*/




        //se for negativo significa que não tem multa
        if(diasEstacionado > 0 && movimentacao.getSaida().toLocalTime().isAfter(objetoConfig.getFimExpediente()))
        {
            tempoNaMulta += (diasEstacionado * tempoForaDeExpediente);
            System.out.println(" if-04 \n\n");
        }
        else if (diasEstacionado > 0 && movimentacao.getSaida().toLocalTime().isBefore(objetoConfig.getInicioExpediente()))
        {
            tempoNaMulta += (diasEstacionado * tempoForaDeExpediente);
            System.out.println(" if-05 \n\n");
        }
        else if (diasEstacionado > 0 &&
                movimentacao.getSaida().toLocalTime().isBefore(objetoConfig.getInicioExpediente()) &&
                movimentacao.getSaida().toLocalTime().isAfter(movimentacao.getEntrada().toLocalTime()))
        {
            //IF para quando passou por exemplos 6 dias entre o dia 1 e 7
            //mas não pegou a multa inteira do ultimo dia dos 6
            tempoNaMulta += ((diasEstacionado-1) * tempoForaDeExpediente);
            System.out.println(" if-06 \n\n");
        }
        else if(diasEstacionado > 0 &&
                movimentacao.getSaida().toLocalTime().isAfter(objetoConfig.getInicioExpediente()) &&
                movimentacao.getSaida().toLocalTime().isBefore(objetoConfig.getFimExpediente()))
        {
            //IF para quando passar dias e ele saiu durante o expediente
            tempoNaMulta += tempoForaDeExpediente * diasEstacionado;
            System.out.println(" if-07 \n\n");
        }

        if(tempoForaDeExpediente - tempoSaidaInicio > 0 && c)
        {
            tempoNaMulta += tempoForaDeExpediente - tempoSaidaInicio;
            System.out.println(" if-08 \n\n");
        }

        System.out.println(" tempo multa "+ tempoNaMulta +"\n\n");

        if(tempoNaMulta < 0)
        {
            tempoNaMulta = 0;
            System.out.println(" if-09 \n\n");
        }
        System.out.println(" tempo multa "+ tempoNaMulta +"\n\n");

        /*
        if(movimentacao.getSaida().toLocalTime().isAfter(objetoConfig.getFimExpediente()) && diasEstacionado < 1)
        {
            tempoForaMulta = ((int) Duration.between(movimentacao.getEntrada().toLocalTime(), objetoConfig.getFimExpediente()).getSeconds());
            tempoMulta = tempoEstacionado - tempoForaMulta;
        }
        else if(diasEstacionado == 1)
        {
            //Verificar se entrou as 17h de um dia e saiu a 10h do dia seguinte E.G.
            //Verificar se saiu antes ou durante ou depois do tempo de expediente
        }
        else if(diasEstacionado > 1)
        {
            //Provalvemente fazer (diasEstacionado-1)*tempoExpediente mas analisar melhor
        }
        else
        {
            tempoForaMulta = tempoEstacionado;
        }*/


        atribuiValores(movimentacao, tempoEstacionado, tempoForaMulta, tempoNaMulta);
    }

    private void atribuiValores(Movimentacao movimentacao, int tempoEstacionado, int tempoForaMulta, int tempoNaMulta)
    {
        Configuracao objetoConfig = configuracaoRepository.getConfig();

        movimentacao.setTempoHora( ( (tempoEstacionado / 60) / 60) );
        movimentacao.setTempoMinuto( (tempoEstacionado / 60) % 60);

        movimentacao.setTempoMultaHora( ( (tempoNaMulta / 60) / 60) );
        movimentacao.setTempoMinuto( (tempoNaMulta / 60) % 60);


        BigDecimal valorMulta = new BigDecimal( (tempoNaMulta / 60) );
        BigDecimal valorTotal = new BigDecimal( (tempoForaMulta / 60) );

        BigDecimal op = new BigDecimal(60);

        movimentacao.setValorMulta( valorMulta.multiply( objetoConfig.getValorMinutoMulta() ) );
        movimentacao.setValorTotal( (valorTotal.multiply( objetoConfig.getValorHora()  ) ).divide(op, 2, RoundingMode.HALF_UP) );



        movimentacao.setValorHoraMulta( objetoConfig.getValorMinutoMulta().multiply(op) );
        movimentacao.setValorHora( objetoConfig.getValorHora() );

        atribuiCondutor(movimentacao, tempoForaMulta);

    }

    private void atribuiCondutor(Movimentacao movimentacao, int tempoForaMulta)
    {
        Condutor objetoCondutor = condutorRepository.getMovimentacaoCondutor(movimentacao.getCondutor().getId());

        int horaAtual = objetoCondutor.getTempoPagoHora();
        int minutoAtual = objetoCondutor.getTempoPagoMinuto();

        minutoAtual += ((tempoForaMulta / 60) % 60 );

        if(minutoAtual >= 60)
        {
            horaAtual += 1;
            minutoAtual = minutoAtual - 60;
        }

        horaAtual += ((tempoForaMulta / 60) / 60 );
        if(horaAtual % 50 == 0)
        {
            objetoCondutor.setTempoDescontoHora( (objetoCondutor.getTempoDescontoHora() + 5)  );
        }

        objetoCondutor.setTempoPagoMinuto(minutoAtual);
        objetoCondutor.setTempoPagoHora(horaAtual);

        this.condutorRepository.save(objetoCondutor);
    }
}
