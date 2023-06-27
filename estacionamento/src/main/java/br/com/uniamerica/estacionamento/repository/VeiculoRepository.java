package br.com.uniamerica.estacionamento.repository;

import br.com.uniamerica.estacionamento.entity.Modelo;
import br.com.uniamerica.estacionamento.entity.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    @Query("select v from Veiculo v where v.ativo = true")
    List<Veiculo> findByAtivo();

    @Query("select exists (select v from Veiculo v where v.placa = :placa)")
    boolean placaAlreadyExists(@Param("placa") final String placa);

    @Query("select exists (select v from Veiculo v where v.id = :id)")
    boolean doesExist(@Param("id") final Long id);

    @Query("select v.id from Veiculo v where v.placa = :placa")
    Long isTheSame(@Param("placa") final String placa);

    @Query("select exists (select v from Veiculo v where v.modelo.id = :id)")
    boolean isInMovimentacao(@Param("id") final Long id);

    @Query("select m from Modelo m where m.nome = :nome")
    Modelo findModeloByNome(@Param("nome") final String nome);


}
