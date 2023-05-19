package br.com.uniamerica.estacionamento.repository;

import br.com.uniamerica.estacionamento.entity.Condutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CondutorRepository extends JpaRepository<Condutor, Long> {


    @Query("select c from Condutor c where c.ativo = true")
    List<Condutor> findByAtivo();

    @Query("select exists (select c from Condutor c where c.cpf = :cpf)")
    boolean cpfAlreadyExists(@Param("cpf") final String cpf);

    @Query("select exists (select c from Condutor c where c.id = :id)")
    boolean doesExist(@Param("id") final Long id);

    @Query("select c.id from Condutor c where c.cpf = :cpf")
    Long isTheSame(@Param("cpf") final String cpf);

    @Query("select exists (select m from Movimentacao m where m.veiculo.id = :id)")
    boolean isInMovimentacao(@Param("id") final Long id);


    @Query("select c from Condutor c where c.id = :id")
    Condutor getMovimentacaoCondutor(@Param("id") final Long id);

}
