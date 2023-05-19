package br.com.uniamerica.estacionamento.repository;

import br.com.uniamerica.estacionamento.entity.Configuracao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracaoRepository extends JpaRepository<Configuracao, Long> {

    @Query("select exists (select c from Configuracao c where c.id = :id)")
    boolean doesExist(@Param("id") final Long id);

    @Query("select c from Configuracao c")
    Configuracao getConfig();

}
