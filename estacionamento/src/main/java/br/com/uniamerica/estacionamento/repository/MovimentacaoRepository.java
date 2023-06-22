package br.com.uniamerica.estacionamento.repository;

import br.com.uniamerica.estacionamento.entity.Condutor;
import br.com.uniamerica.estacionamento.entity.Movimentacao;
import br.com.uniamerica.estacionamento.entity.Veiculo;
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

    @Query("select c from Condutor c where c.cpf = :cpf")
    Condutor findCondutorByCpf(@Param("cpf") final String cpf);

    @Query("select v from Veiculo v where v.placa = :placa")
    Veiculo findVeiculoByPlaca(@Param("placa") final String placa);

}
