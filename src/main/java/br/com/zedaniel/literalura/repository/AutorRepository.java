package br.com.zedaniel.literalura.repository;

import br.com.zedaniel.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    @Query("SELECT a FROM Autor a WHERE a.birth_year < :ano AND a.death_year > :ano")
    List<Autor> buscarAutorPorAno(Integer ano);
    List<Autor> findByNameContainingIgnoreCase(String nome);
}
