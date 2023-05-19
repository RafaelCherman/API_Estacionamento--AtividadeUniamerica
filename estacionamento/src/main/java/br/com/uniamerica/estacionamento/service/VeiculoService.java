package br.com.uniamerica.estacionamento.service;

import br.com.uniamerica.estacionamento.entity.Veiculo;
import br.com.uniamerica.estacionamento.repository.ModeloRepository;
import br.com.uniamerica.estacionamento.repository.VeiculoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class VeiculoService {


    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private ModeloRepository modeloRepository;

    @Transactional(rollbackOn = Exception.class)
    public List<Veiculo> ativo()
    {
        List<Veiculo> lista = this.veiculoRepository.findByAtivo();
        if(lista.size() == 0)
        {
            throw new RuntimeException("Lista vazia");
        }

        return lista;
    }

    @Transactional(rollbackOn = Exception.class)
    public List<Veiculo> tudo()
    {
        List<Veiculo> lista = this.veiculoRepository.findAll();
        if(lista.size() == 0)
        {
            throw new RuntimeException("Lista vazia");
        }

        return lista;
    }

    @Transactional(rollbackOn = Exception.class)
    public void cadastrar(final Veiculo veiculo){


        Assert.notNull(veiculo.getPlaca(), "Placa não pode ser nula.");
        Assert.isTrue(veiculo.getPlaca().matches("[A-Z0-9]{7}"), "Placa invalida.");
        Assert.isTrue(!(veiculoRepository.placaAlreadyExists(veiculo.getPlaca())), "Placa ja existe.");
        Assert.notNull(veiculo.getModelo(), "Modelo não pode ser nulo.");
        Assert.isTrue(modeloRepository.doesExist(veiculo.getModelo().getId()), "Modelo não existe.");
        Assert.notNull(veiculo.getCor(), "Cor não pode ser nula.");
        Assert.notNull(veiculo.getTipo(), "Tipo não pode ser nulo.");
        Assert.notNull(veiculo.getAno(),"Ano não pode ser nulo.");


        this.veiculoRepository.save(veiculo);

    }

    @Transactional(rollbackOn = Exception.class)
    public void editar(final Veiculo veiculo, final Long urlId)
    {
        Assert.isTrue(veiculoRepository.doesExist(urlId), "Veiculo não existe.");
        Assert.isTrue(veiculo.getId().equals(urlId), "Não foi possivel identificar o registro informado.");
        Assert.notNull(veiculo.getPlaca(), "Placa não pode ser nula.");
        Assert.isTrue(veiculo.getPlaca().matches("[A-Z0-9]{7}"), "Placa invalida.");
        if(veiculoRepository.placaAlreadyExists(veiculo.getPlaca()))
        {
            if(!(veiculoRepository.isTheSame(veiculo.getPlaca()).equals(urlId)))
            {
                throw new RuntimeException("Placa ja existe.");
            }
        }
        Assert.notNull(veiculo.getModelo(), "Modelo não pode ser nulo.");
        Assert.isTrue(modeloRepository.doesExist(veiculo.getModelo().getId()), "Modelo não existe.");
        Assert.notNull(veiculo.getCor(), "Cor não pode ser nula.");
        Assert.notNull(veiculo.getTipo(), "Tipo não pode ser nulo.");
        Assert.notNull(veiculo.getAno(),"Ano não pode ser nulo.");
        this.veiculoRepository.save(veiculo);
    }

    @Transactional(rollbackOn = Exception.class)
    public boolean deleta(final Long urlId) {
        Veiculo veiculo = this.veiculoRepository.findById(urlId).orElse(null);

        Assert.notNull(veiculo, "Veiculo não existe.");

        if(this.veiculoRepository.isInMovimentacao(urlId))
        {
            veiculo.setAtivo(false);
            this.veiculoRepository.save(veiculo);
            return true;
        }
        else
        {
            this.veiculoRepository.delete(veiculo);
            return false;
        }

    }
}
