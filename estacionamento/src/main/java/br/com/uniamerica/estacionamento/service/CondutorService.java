package br.com.uniamerica.estacionamento.service;

import br.com.uniamerica.estacionamento.entity.Condutor;
import br.com.uniamerica.estacionamento.repository.CondutorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class CondutorService {

    @Autowired
    private CondutorRepository condutorRepository;

    @Transactional(rollbackOn = Exception.class)
    public List<Condutor> ativo()
    {
        List<Condutor> lista = this.condutorRepository.findByAtivo();
        if(lista.size() == 0)
        {
            throw new RuntimeException("Lista vazia");
        }

        return lista;
    }

    @Transactional(rollbackOn = Exception.class)
    public List<Condutor> tudo()
    {
        List<Condutor> lista = this.condutorRepository.findAll();
        if(lista.size() == 0)
        {
            throw new RuntimeException("Lista vazia");
        }

        return lista;
    }

    @Transactional(rollbackOn = Exception.class)
    public void cadastrar(final Condutor condutor)
    {
        Assert.notNull(condutor.getNome(),"Nome não pode ser nulo.");
        Assert.isTrue(condutor.getNome().matches("[a-zA-Z\\s]{1,100}"), "Nome invalido.");
        Assert.notNull(condutor.getCpf(),"CPF não pode ser nulo.");
        Assert.isTrue(condutor.getCpf().matches("[0-9]{3}[.][0-9]{3}[.][0-9]{3}[-][0-9]{2}"), "CPF invalido.");
        Assert.isTrue(!(condutorRepository.cpfAlreadyExists(condutor.getCpf())), "CPF ja existe.");
        Assert.notNull(condutor.getTelefone(), "Telefone não pode ser nulo.");
        Assert.isTrue(condutor.getTelefone().matches("[0-9]{2}\\s[0-9]{5}[-][0-9]{4}"), "Telefone invalido");

        this.condutorRepository.save(condutor);
    }

    @Transactional(rollbackOn = Exception.class)
    public void editar(final Condutor condutor, final Long urlId)
    {
        Assert.isTrue(condutorRepository.doesExist(urlId), "Condutor não existe.");
        Assert.isTrue(condutor.getId().equals(urlId), "Não foi possivel identificar o registro informado.");
        Assert.notNull(condutor.getNome(),"Nome não pode ser nulo.");
        Assert.isTrue(condutor.getNome().matches("[a-zA-Z\\s]{1,100}"), "Nome invalido.");
        Assert.notNull(condutor.getCpf(),"CPF não pode ser nulo.");
        Assert.isTrue(condutor.getCpf().matches("[0-9]{3}[.][0-9]{3}[.][0-9]{3}[-][0-9]{2}"), "CPF invalido.");
        if (condutorRepository.cpfAlreadyExists(condutor.getCpf()));
        {
            if(!(condutorRepository.isTheSame(condutor.getCpf()).equals(urlId)))
            {
                throw new RuntimeException("CPF ja existe.");
            }
        }
        Assert.notNull(condutor.getTelefone(), "Telefone não pode ser nulo.");
        Assert.isTrue(condutor.getTelefone().matches("[0-9]{2}\\s[0-9]{5}[-][0-9]{4}"), "Telefone invalido");

        this.condutorRepository.save(condutor);
    }

    @Transactional(rollbackOn = Exception.class)
    public boolean deleta(final Long urlId){
        Condutor condutor = this.condutorRepository.findById(urlId).orElse(null);

        Assert.notNull(condutor, "Condutor não existe");

        if(this.condutorRepository.isInMovimentacao(urlId))
        {
            condutor.setAtivo(false);
            this.condutorRepository.save(condutor);
            return true;
        }
        else
        {
            this.condutorRepository.delete(condutor);
            return false;
        }

    }
}
