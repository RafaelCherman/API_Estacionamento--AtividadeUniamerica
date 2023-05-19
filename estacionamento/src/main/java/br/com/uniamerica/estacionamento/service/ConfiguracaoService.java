package br.com.uniamerica.estacionamento.service;

import br.com.uniamerica.estacionamento.entity.Configuracao;
import br.com.uniamerica.estacionamento.entity.Modelo;
import br.com.uniamerica.estacionamento.repository.ConfiguracaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class ConfiguracaoService {

    @Autowired
    private ConfiguracaoRepository configuracaoRepository;


    @Transactional(rollbackOn = Exception.class)
    public void cadastrar(final Configuracao configuracao){

        this.configuracaoRepository.save(configuracao);

    }

    @Transactional(rollbackOn = Exception.class)
    public void editar(final Configuracao configuracao, final Long urlId)
    {
        Assert.isTrue(configuracaoRepository.doesExist(urlId), "Não foi possivel encontrar o registro informado.");
        Assert.isTrue(configuracao.getId().equals(urlId), "Não foi possivel identificar o registro informado.");

        this.configuracaoRepository.save(configuracao);
    }

}
