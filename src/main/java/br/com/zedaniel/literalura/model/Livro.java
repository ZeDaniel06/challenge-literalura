package br.com.zedaniel.literalura.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "livros")
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String titulo;
    @ManyToOne
    private Autor autor;
    private String idioma;
    private Integer numDownloads;

    public Livro(DadosLivro dadosLivro){
        this.titulo = dadosLivro.titulo();
        this.autor = dadosLivro.autores().get(0);
        this.idioma = dadosLivro.idiomas().get(0);
        this.numDownloads = dadosLivro.numDownloads();
    }

    public Livro(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getNumDownloads() {
        return numDownloads;
    }

    public void setNumDownloads(Integer numDownloads) {
        this.numDownloads = numDownloads;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    @Override
    public String toString() {
        return "\n------------------\n" +
                "Título: " + titulo +
                "\nAutor: " + autor.getName() +
                "\nIdioma: " + idioma +
                "\nNúmero de Downloads: " + numDownloads +
                "\n------------------\n";
    }

}
