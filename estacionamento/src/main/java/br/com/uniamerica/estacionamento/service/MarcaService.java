package br.com.uniamerica.estacionamento.service;

import br.com.uniamerica.estacionamento.entity.Marca;
import br.com.uniamerica.estacionamento.repository.MarcaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class MarcaService {

    @Autowired
    private MarcaRepository marcaRepository;

    @Transactional(rollbackOn = Exception.class)
    public List<Marca> ativo()
    {
        List<Marca> lista = this.marcaRepository.findByAtivo();
        if(lista.size() == 0)
        {
            throw new RuntimeException("Lista vazia");
        }

        return lista;
    }

    @Transactional(rollbackOn = Exception.class)
    public List<Marca> tudo()
    {
        List<Marca> lista = this.marcaRepository.findAll();
        if(lista.size() == 0)
        {
            throw new RuntimeException("Lista vazia");
        }

        return lista;
    }

    @Transactional(rollbackOn = Exception.class)
    public void cadastrar(final Marca marca){


        Assert.notNull(marca.getNome(), "Nome não pode ser nulo.");
        Assert.isTrue(marca.getNome().matches("[a-zA-Z\\s]{1,50}"), "Nome invalido.");
        Assert.isTrue(!(marcaRepository.alreadyExists(marca.getNome())), "Nome ja existe.");


        this.marcaRepository.save(marca);

    }

    @Transactional(rollbackOn = Exception.class)
    public void editar(final Marca marca, final Long urlId)
    {
        Assert.isTrue(marcaRepository.doesExist(urlId), "Marca não existe.");
        Assert.isTrue(marca.getId().equals(urlId), "Não foi possivel identificar o registro informado.");
        Assert.notNull(marca.getNome(), "Nome não pode ser nulo.");
        Assert.isTrue(marca.getNome().matches("[a-zA-Z\\s]{1,50}"), "Nome invalido.");
        Assert.isTrue(!(marcaRepository.alreadyExists(marca.getNome())), "Nome ja existe.");

        this.marcaRepository.save(marca);
    }

    @Transactional(rollbackOn = Exception.class)
    public boolean deleta(final Long urlId) {
        Marca marca = this.marcaRepository.findById(urlId).orElse(null);

        Assert.notNull(marca, "Modelo não existe.");

        if(this.marcaRepository.isInModelo(urlId))
        {
            marca.setAtivo(false);
            this.marcaRepository.save(marca);
            return true;
        }
        else
        {
            this.marcaRepository.delete(marca);
            return false;
        }

    }
}
