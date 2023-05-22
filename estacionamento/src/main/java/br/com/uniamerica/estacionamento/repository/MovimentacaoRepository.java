package br.com.uniamerica.estacionamento.repository;

import br.com.uniamerica.estacionamento.entity.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    @Query("select m from Movimentacao m where m.saida = null")
    List<Movimentacao> findByAberto();

    @Query("select exists (select m from Movimentacao m where m.id = :id)")
    boolean doesExist(@Param("id") final Long id);

    @Query("select m from Movimentacao m where m.id = :id")
    Movimentacao getMovimentacaoById(@Param("id") final Long id);

}
