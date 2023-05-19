package br.com.uniamerica.estacionamento.service;

import br.com.uniamerica.estacionamento.entity.Modelo;
import br.com.uniamerica.estacionamento.repository.MarcaRepository;
import br.com.uniamerica.estacionamento.repository.ModeloRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class ModeloService {


    //   matches
    @Autowired
    private ModeloRepository modeloRepository;

    @Autowired
    private MarcaRepository marcaRepository;


    @Transactional(rollbackOn = Exception.class)
    public List<Modelo> ativo()
    {
        List<Modelo> lista = this.modeloRepository.findByAtivo();
        if(lista.size() == 0)
        {
            throw new RuntimeException("Lista vazia");
        }

        return lista;
    }

    @Transactional(rollbackOn = Exception.class)
    public List<Modelo> tudo()
    {
        List<Modelo> lista = this.modeloRepository.findAll();
        if(lista.size() == 0)
        {
            throw new RuntimeException("Lista vazia");
        }

        return lista;
    }

    @Transactional(rollbackOn = Exception.class)
    public void cadastrar(final Modelo modelo){


        Assert.notNull(modelo.getNome(), "Nome não pode ser nulo.");
        Assert.isTrue(modelo.getNome().matches("[a-zA-Z\\s]{1,50}"), "Nome invalido.");
        Assert.isTrue(!(modeloRepository.alreadyExists(modelo.getNome())), "Nome ja existe.");
        Assert.notNull(modelo.getMarca(), "Marca não pode ser nula.");
        Assert.isTrue(marcaRepository.doesExist(modelo.getMarca().getId()), "Marca não existe.");


        this.modeloRepository.save(modelo);

    }

    @Transactional(rollbackOn = Exception.class)
    public void editar(final Modelo modelo, final Long urlId)
    {
        Assert.isTrue(modeloRepository.doesExist(urlId), "Modelo não existe.");
        Assert.isTrue(modelo.getId().equals(urlId), "Não foi possivel identificar o registro informado.");
        Assert.notNull(modelo.getNome(), "Nome não pode ser nulo.");
        Assert.isTrue(modelo.getNome().matches("[a-zA-Z\\s]{1,50}"), "Nome invalido.");
        if(modeloRepository.alreadyExists(modelo.getNome()))
        {
            if(!(modeloRepository.isTheSame(modelo.getNome()).equals(urlId)))
            {
                throw new RuntimeException("Modelo ja existe.");
            }
        }
        Assert.notNull(modelo.getMarca(), "Marca não pode ser nula.");
        Assert.isTrue(marcaRepository.doesExist(modelo.getMarca().getId()), "Marca não existe.");

        this.modeloRepository.save(modelo);
    }

    @Transactional(rollbackOn = Exception.class)
    public boolean deleta(final Long urlId) {
        Modelo modelo = this.modeloRepository.findById(urlId).orElse(null);

        Assert.notNull(modelo, "Modelo não existe.");

        if(this.modeloRepository.isInVeiculo(urlId))
        {
            modelo.setAtivo(false);
            this.modeloRepository.save(modelo);
            return true;
        }
        else
        {
            this.modeloRepository.delete(modelo);
            return false;
        }

    }





}
