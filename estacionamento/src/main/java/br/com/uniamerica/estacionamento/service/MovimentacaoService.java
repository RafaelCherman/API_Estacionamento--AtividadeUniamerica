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

    //Tratar erros 400
    //Stress test
    //Arrumar a nota final

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
    public String cadastrar(final Movimentacao movimentacao){

        String resposta;

        Assert.notNull(movimentacao.getCondutor(), "Condutor não pode ser nulo.");
        Assert.isTrue(condutorRepository.doesExist(movimentacao.getCondutor().getId()), "Condutor não existe");
        Assert.notNull(movimentacao.getVeiculo(), "Veiculo não pode ser nulo.");
        Assert.isTrue(veiculoRepository.doesExist(movimentacao.getVeiculo().getId()), "Veiculo não existe");
        Assert.notNull(movimentacao.getEntrada(), "Entrada não pode ser nula");


        if(movimentacao.getSaida() != null)
        {
            calculaTempoEstacionado(movimentacao);
            resposta = criaResposta(movimentacao);
        }
        else
        {
            resposta = "Registro cadastrado com sucesso";
        }

        this.movimentacaoRepository.save(movimentacao);

        return resposta;

    }

    @Transactional(rollbackOn = Exception.class)
    public String editar(final Movimentacao movimentacao, final Long urlId)
    {
        String resposta;

        Assert.isTrue(movimentacaoRepository.doesExist(urlId), "Movimentacao não existe.");
        Assert.isTrue(movimentacao.getId().equals(urlId), "Não foi possivel identificar o registro informado.");
        Assert.notNull(movimentacao.getCondutor(), "Condutor não pode ser nulo.");
        Assert.isTrue(condutorRepository.doesExist(movimentacao.getCondutor().getId()), "Condutor não existe");
        Assert.notNull(movimentacao.getVeiculo(), "Veiculo não pode ser nulo.");
        Assert.isTrue(veiculoRepository.doesExist(movimentacao.getVeiculo().getId()), "Veiculo não existe");
        Assert.notNull(movimentacao.getEntrada(), "Entrada não pode ser nula");

        Movimentacao oldMovimentacao = this.movimentacaoRepository.getMovimentacaoById(urlId);

        if(oldMovimentacao.getSaida() != movimentacao.getSaida() ||
            oldMovimentacao.getEntrada() != movimentacao.getEntrada())
        {
            movimentacao.setCondutor( editaMovimentoCondutor(oldMovimentacao.getCondutor(), oldMovimentacao) );
            calculaTempoEstacionado(movimentacao);
            resposta = criaResposta(movimentacao);
        }
        else
        {
            resposta = "Registro atualizado com sucesso";
        }

        this.movimentacaoRepository.save(movimentacao);

        return resposta;
    }

    @Transactional(rollbackOn = Exception.class)
    public String fecha(final Movimentacao movimentacao, final Long urlId)
    {
        String resposta;

        Assert.isTrue(movimentacaoRepository.doesExist(urlId), "Movimentacao não existe.");
        Assert.isTrue(movimentacao.getId().equals(urlId), "Não foi possivel identificar o registro informado.");
        Assert.notNull(movimentacao.getCondutor(), "Condutor não pode ser nulo.");
        Assert.isTrue(condutorRepository.doesExist(movimentacao.getCondutor().getId()), "Condutor não existe");
        Assert.notNull(movimentacao.getVeiculo(), "Veiculo não pode ser nulo.");
        Assert.isTrue(veiculoRepository.doesExist(movimentacao.getVeiculo().getId()), "Veiculo não existe");
        Assert.notNull(movimentacao.getEntrada(), "Entrada não pode ser nula");
        Assert.notNull(movimentacao.getSaida(), "Saida não pode ser nula");
        Assert.isNull(movimentacaoRepository.getMovimentacaoById(urlId).getSaida(), "Movimentação já foi fechada");

        calculaTempoEstacionado(movimentacao);

        this.movimentacaoRepository.save(movimentacao);

        resposta = criaResposta(movimentacao);

        return resposta;

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
        int tempoNoExpediente = 0;
        int tempoNaMulta = 0;
        boolean c = true;

        LocalTime entrada = objetoConfig.getInicioExpediente();
        LocalTime saida = objetoConfig.getFimExpediente();

        int tempoDeExpediente = ((int) Duration.between(entrada, saida).getSeconds());
        int tempoForaDeExpediente = umDiaS - tempoDeExpediente;
        Long diasEstacionado = ChronoUnit.DAYS.between(movimentacao.getEntrada(), movimentacao.getSaida());



        int tempoSaidaInicio = (((int) Duration.between(movimentacao.getSaida().toLocalTime(), objetoConfig.getInicioExpediente()).getSeconds()));
        //int tempoEntradaFim = ((int) Duration.between(movimentacao.getEntrada().toLocalTime(), objetoConfig.getFimExpediente()).getSeconds());
        if(tempoSaidaInicio < 0)
        {
            tempoSaidaInicio = (-1)*tempoSaidaInicio;
            tempoSaidaInicio = umDiaS - tempoSaidaInicio;
        }
        /*if(tempoEntradaFim < 0)
        {
            tempoEntradaFim = (-1)*tempoEntradaFim;
            tempoEntradaFim = umDiaS - tempoEntradaFim;
        }*/


        tempoEstacionado = ((int) Duration.between(movimentacao.getEntrada(), movimentacao.getSaida()).getSeconds());


        //IFs para movimentações improvaveis, porem não está 100%
        if(movimentacao.getEntrada().toLocalTime().isBefore(objetoConfig.getInicioExpediente()) &&
                movimentacao.getSaida().toLocalTime().isBefore(objetoConfig.getInicioExpediente()) ||

                movimentacao.getEntrada().toLocalTime().isAfter(objetoConfig.getFimExpediente()) &&
                        movimentacao.getSaida().toLocalTime().isBefore(objetoConfig.getInicioExpediente()) ||

                movimentacao.getEntrada().toLocalTime().isAfter(objetoConfig.getFimExpediente()) &&
                        movimentacao.getSaida().toLocalTime().isAfter(objetoConfig.getFimExpediente()))
        {
            tempoNaMulta += ((int) Duration.between(movimentacao.getEntrada().toLocalTime(), movimentacao.getSaida().toLocalTime()).getSeconds());
            c = false;
        }
        else if (movimentacao.getEntrada().toLocalTime().isBefore(objetoConfig.getInicioExpediente()) ||
                movimentacao.getEntrada().toLocalTime().isAfter(objetoConfig.getFimExpediente()))
        {
            tempoNaMulta += ((int) Duration.between(movimentacao.getEntrada().toLocalTime(), objetoConfig.getInicioExpediente()).getSeconds());
        }

        if (tempoNaMulta < 0)
        {
            tempoNaMulta = (-1)*tempoNaMulta;
            tempoNaMulta = umDiaS - tempoNaMulta;
        }



        if(diasEstacionado > 0 && movimentacao.getSaida().toLocalTime().isAfter(objetoConfig.getFimExpediente()))
        {
            tempoNaMulta += (diasEstacionado * tempoForaDeExpediente);
        }
        else if (diasEstacionado > 0 && movimentacao.getSaida().toLocalTime().isBefore(objetoConfig.getInicioExpediente()))
        {
            tempoNaMulta += (diasEstacionado * tempoForaDeExpediente);
        }
        else if (diasEstacionado > 0 &&
                movimentacao.getSaida().toLocalTime().isBefore(objetoConfig.getInicioExpediente()) &&
                movimentacao.getSaida().toLocalTime().isAfter(movimentacao.getEntrada().toLocalTime()))
        {
            tempoNaMulta += ((diasEstacionado-1) * tempoForaDeExpediente);
        }
        else if(diasEstacionado > 0 &&
                movimentacao.getSaida().toLocalTime().isAfter(objetoConfig.getInicioExpediente()) &&
                movimentacao.getSaida().toLocalTime().isBefore(objetoConfig.getFimExpediente()))
        {
            tempoNaMulta += tempoForaDeExpediente * diasEstacionado;
        }

        if(tempoForaDeExpediente - tempoSaidaInicio > 0 && c)
        {
            tempoNaMulta += tempoForaDeExpediente - tempoSaidaInicio;
        }


        if(tempoNaMulta < 0)
        {
            tempoNaMulta = 0;
        }

        if(tempoNaMulta > 0)
        {
            tempoNoExpediente = tempoEstacionado - tempoNaMulta;
        }
        else
        {
            tempoNoExpediente = tempoEstacionado;
        }


        atribuiValores(movimentacao, tempoEstacionado, tempoNoExpediente, tempoNaMulta);
    }

    private void atribuiValores(Movimentacao movimentacao, int tempoEstacionado, int tempoNoExpediente, int tempoNaMulta)
    {
        Configuracao objetoConfig = configuracaoRepository.getConfig();
        Condutor objetoCondutor = condutorRepository.getMovimentacaoCondutor(movimentacao.getCondutor().getId());

        int desconto = 0;
        int descontoHora = objetoCondutor.getTempoDescontoHora();
        int descontoMinuto = objetoCondutor.getTempoDescontoMinuto();

        BigDecimal op = new BigDecimal(60);
        BigDecimal valorDoDesconto = new BigDecimal(0);

        int tempoPagar = 0;


        movimentacao.setTempoHora( ( (tempoEstacionado / 60) / 60) );
        movimentacao.setTempoMinuto( (tempoEstacionado / 60) % 60);

        movimentacao.setTempoMultaHora( ( (tempoNaMulta / 60) / 60) );
        movimentacao.setTempoMinuto( (tempoNaMulta / 60) % 60);

        tempoPagar = tempoEstacionado;


        if(descontoHora > 0 || descontoMinuto > 0)
        {
            tempoPagar = tempoEstacionado - ((descontoHora * 60) * 60);
            if(tempoPagar < 0)
            {
                desconto = ((descontoHora * 60) * 60) - tempoEstacionado;

                descontoHora = ((desconto / 60) / 60);
                descontoMinuto = ((desconto / 60) % 60);

            }
            else if(descontoMinuto > 0)
            {
                tempoPagar = (tempoEstacionado - ((descontoHora * 60) * 60)) - (descontoMinuto * 60);
                if(tempoPagar < 0)
                {
                    desconto = (descontoMinuto * 60) - tempoEstacionado;

                    descontoHora = ((desconto / 60) / 60);
                    descontoMinuto = ((desconto / 60) % 60);
                }
                else
                {
                    descontoHora = 0;
                    descontoMinuto = 0;
                }
            }
            else
            {
                descontoHora = 0;
            }

            valorDoDesconto = BigDecimal.valueOf( ((desconto * 60) * 60) - ((objetoConfig.getTempoDeDesconto() * 60) * 60) );

        }
        System.out.println("\n\nDesconto hora: " + descontoHora);
        System.out.println("\n\nDesconto minuto: " + descontoMinuto);

        objetoCondutor.setTempoDescontoHora(descontoHora);
        objetoCondutor.setTempoDescontoMinuto(descontoMinuto);

        if(tempoPagar < 0)
        {
            tempoPagar = 0;
        }


        BigDecimal valorMulta = new BigDecimal( (tempoNaMulta / 60) );
        BigDecimal valorTotal = new BigDecimal( (tempoPagar / 60) );



        movimentacao.setValorMulta( valorMulta.multiply( objetoConfig.getValorMinutoMulta() ) );
        movimentacao.setValorTotal( (valorTotal.multiply( ( objetoConfig.getValorHora()  ) ).divide(op, 2, RoundingMode.HALF_UP) ) );
        if(valorDoDesconto.compareTo(BigDecimal.valueOf(0)) > 0)
        {
            movimentacao.setValorDesconto( (valorDoDesconto.multiply( ( objetoConfig.getValorHora()  ) ).divide(op, 2, RoundingMode.HALF_UP) ) );
        }
        else
        {
            movimentacao.setValorDesconto(new BigDecimal(0));
        }


        movimentacao.setValorHoraMulta( objetoConfig.getValorMinutoMulta().multiply(op) );
        movimentacao.setValorHora( objetoConfig.getValorHora() );

        atribuiCondutor(movimentacao, objetoCondutor, tempoPagar);

    }

    private void atribuiCondutor(Movimentacao movimentacao, Condutor objetoCondutor, int tempoPagar)
    {
        Configuracao objetoConfig = configuracaoRepository.getConfig();

        int horaAtual = objetoCondutor.getTempoPagoHora();
        int minutoAtual = objetoCondutor.getTempoPagoMinuto();
        int calculaDesconto = horaAtual / objetoConfig.getTempoParaDesconto();

        minutoAtual += ((tempoPagar / 60) % 60 );

        if(minutoAtual >= 60)
        {
            horaAtual += 1;
            minutoAtual = minutoAtual - 60;
        }

        horaAtual += ((tempoPagar / 60) / 60 );

        if(objetoConfig.isGerarDesconto() &&
                objetoCondutor.getTempoDescontoHora() == 0 &&
                objetoCondutor.getTempoDescontoMinuto() == 0)
        {
            if(horaAtual >= objetoConfig.getTempoParaDesconto() * (calculaDesconto+1))
            {
                objetoCondutor.setTempoDescontoHora(objetoConfig.getTempoDeDesconto());
            }
        }

        objetoCondutor.setTempoPagoMinuto(minutoAtual);
        objetoCondutor.setTempoPagoHora(horaAtual);

        this.condutorRepository.save(objetoCondutor);

    }

    private Condutor editaMovimentoCondutor(Condutor condutor, Movimentacao movimentacao)
    {
        Configuracao objetoconfig = this.configuracaoRepository.getConfig();

        int calculaDesconto = condutor.getTempoPagoHora()  / objetoconfig.getTempoParaDesconto();
        int horaAutal;

        BigDecimal valorPago = movimentacao.getValorTotal();
        BigDecimal valorTotalTempo = movimentacao.getValorHora().multiply( BigDecimal.valueOf( ( ( (movimentacao.getTempoHora() * 60) + movimentacao.getTempoMinuto() ) / 60 ) ) );
        BigDecimal valorTotalMulta = (movimentacao.getValorHoraMulta().divide(BigDecimal.valueOf(60))).multiply(BigDecimal.valueOf( ( (movimentacao.getTempoMultaHora() * 60) + movimentacao.getTempoMultaMinuto() ) ) );
        BigDecimal valorTotal = valorTotalTempo.add(valorTotalMulta);


        condutor.setTempoPagoHora( condutor.getTempoPagoHora() - (movimentacao.getTempoHora() - movimentacao.getTempoMultaHora()));
        condutor.setTempoPagoMinuto( condutor.getTempoPagoMinuto() - (movimentacao.getTempoMinuto() - movimentacao.getTempoMultaMinuto()));

        horaAutal = condutor.getTempoPagoHora();

        if(horaAutal < (objetoconfig.getTempoParaDesconto() * calculaDesconto))
        {
            condutor.setTempoDescontoHora( condutor.getTempoDescontoHora() - objetoconfig.getTempoDeDesconto() );
        }
        else if(condutor.getTempoDescontoHora() > 0)
        {
            condutor.setTempoDescontoHora(objetoconfig.getTempoDeDesconto() - condutor.getTempoDescontoHora() );
        }

        if(condutor.getTempoDescontoMinuto() > 0)
        {
            condutor.setTempoDescontoMinuto(60 - condutor.getTempoDescontoMinuto());
        }

        if( !(valorPago.equals(valorTotal)) )
        {
            condutor.setTempoDescontoHora(objetoconfig.getTempoDeDesconto());
        }

        return condutor;
    }

    private String criaResposta(Movimentacao movimentacao)
    {
        Configuracao objetoconfig = this.configuracaoRepository.getConfig();
        String resposta;

        resposta = "Entrada: " + movimentacao.getEntrada() + "\n" +
                "Saida: " + movimentacao.getSaida() + "\n" +
                "Condutor: " + movimentacao.getCondutor().getNome() + "\n" +
                "Veiculo: " + movimentacao.getVeiculo().getPlaca() + "\n" +
                "Quantidade de horas: " + movimentacao.getTempoHora() + " Minutos: " + movimentacao.getTempoMinuto() + "\n" +
                "Valor da multa: " + movimentacao.getValorMulta() + "\n" +
                "Valor total a pagar: " + movimentacao.getValorTotal() + "\n";

        if(objetoconfig.isGerarDesconto())
        {
            resposta += "Quantidade de horas para o desconto: " + "\n" +
                    "Valor do desconto: " + movimentacao.getValorDesconto() + "\n";
        }


        return resposta;
    }
}
