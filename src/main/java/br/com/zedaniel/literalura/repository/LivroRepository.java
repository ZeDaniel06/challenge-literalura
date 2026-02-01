package br.com.zedaniel.literalura.repository;

import br.com.zedaniel.literalura.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LivroRepository extends JpaRepository<Livro, Long> {
    List<Livro> findByIdioma(String idioma);
    List<Livro> findByTituloContainingIgnoreCase(String titulo);
}
