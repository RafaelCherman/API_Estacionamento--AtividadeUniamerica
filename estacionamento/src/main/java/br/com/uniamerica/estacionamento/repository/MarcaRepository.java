package br.com.uniamerica.estacionamento.repository;

import br.com.uniamerica.estacionamento.entity.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {

    @Query("select m from Marca m where m.ativo = true")
    List<Marca> findByAtivo();

    @Query("select exists (select m from Marca m where m.nome = :nome)")
    boolean alreadyExists(@Param("nome") final String nome);

    @Query("select exists (select m from Marca m where m.id = :id)")
    boolean doesExist(@Param("id") final Long id);

    @Query("select exists (select m from Modelo m where m.marca.id = :id)")
    boolean isInModelo(@Param("id") final Long id);

}
