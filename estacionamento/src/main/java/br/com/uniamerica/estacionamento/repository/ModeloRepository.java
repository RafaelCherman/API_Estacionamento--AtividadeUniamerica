package br.com.uniamerica.estacionamento.repository;

import br.com.uniamerica.estacionamento.entity.Modelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModeloRepository extends JpaRepository<Modelo, Long> {


    @Query("select m from Modelo m where m.ativo = true")
    List<Modelo> findByAtivo();

    @Query("select exists (select m from Modelo m where m.nome = :nome)")
    boolean alreadyExists(@Param("nome") final String nome);

    @Query("select exists (select m from Modelo m where m.id = :id)")
    boolean doesExist(@Param("id") final Long id);

    @Query("select m.id from Modelo m where m.nome = :nome")
    Long isTheSame(@Param("nome") final String nome);

    @Query("select exists (select v from Veiculo v where v.modelo.id = :id)")
    boolean isInVeiculo(@Param("id") final Long id);
















    //@Query("SELECT m FROM modelos")
    //List<?> findByAtivo();

    //@Query(value = "select * from modelos ", nativeQuery = true)
    //List<?> findByAtivo();

    //public List<Modelo> findByNome(final String nome);

    //@Query("from Modelo where nome like :nome")
    //List<Modelo> findByNomeLike(@Param("nome") final String nome);


    //@Query(value = "select * from modelos where nome like :nome", nativeQuery = true)
    //public List<Modelo> findByNomeLike(@Param("nome") final String nome);


}
