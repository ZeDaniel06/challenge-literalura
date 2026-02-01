package br.com.zedaniel.literalura.principal;

import br.com.zedaniel.literalura.model.Autor;
import br.com.zedaniel.literalura.model.DadosResults;
import br.com.zedaniel.literalura.model.Livro;
import br.com.zedaniel.literalura.repository.AutorRepository;
import br.com.zedaniel.literalura.repository.LivroRepository;
import br.com.zedaniel.literalura.service.ConsumoAPI;
import br.com.zedaniel.literalura.service.ConversaoDados;

import java.net.URLEncoder;
import java.util.List;
import java.util.Scanner;

public class Principal {

    Scanner scanner = new Scanner(System.in);
    ConsumoAPI consumoAPI = new ConsumoAPI();
    ConversaoDados conversor = new ConversaoDados();

    private final String BASE = "https://gutendex.com/books";
    private final String BUSCA = "/?search=";
    private String buscaA;
    private String buscaB;

    private LivroRepository livroRepository;
    private AutorRepository autorRepository;

    public Principal(LivroRepository livroRepository, AutorRepository autorRepository){
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
    }

    public void exibirMenu(){
        int menu = -1;

        while (menu != 0){

            String mensagemMenu = """
                    Escolha uma opção:
                    1 - Buscar livro por título
                    2 - Exibir livros registrados
                    3 - Exibir autores registrados
                    4 - Exibir autores vivos em ano específico
                    5 - Exibir livros de idioma específico
                    
                    0 - Sair
                    """;
            System.out.println(mensagemMenu);
            menu = scanner.nextInt();
            scanner.nextLine();

            switch (menu){
                case 1:
                    consultarLivros();
                    break;
                case 2:
                    exibirLivrosRegistrados();
                    break;
                case 3:
                    exibirAutoresRegistrados();
                    break;
                case 4:
                    exibirAutoresVivosPorAno();
                    break;
                case 5:
                    exibirLivrosPorIdioma();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção Inválida!");
                    break;
            }
        }


    }

    private void exibirLivrosPorIdioma() {
        String mensagem = """
                Digite o idioma desejado com sigla de duas letras,
                por exemplo:
                pt = Português,
                en = English,
                fr = Francês""";
        System.out.println(mensagem);

        var idioma = scanner.nextLine();
        List<Livro> livros = livroRepository.findByIdioma(idioma);
        System.out.println("Foram encontrados " + livros.size() + " livros no idioma escolhido.");
        livros.stream()
                .forEach(System.out::println);

    }

    private void exibirAutoresVivosPorAno() {
        System.out.println("Que ano você deseja buscar? ");
        var ano = scanner.nextInt();
        scanner.nextLine();

        List<Autor> autores = autorRepository.buscarAutorPorAno(ano);
        autores.stream()
                .forEach(System.out::println);
    }

    private void exibirAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAll();
        autores.stream()
                .forEach(System.out::println);
    }

    private void exibirLivrosRegistrados() {
        List<Livro> livros = livroRepository.findAll();
        livros.stream()
                .forEach(System.out::println);
        
    }

    private void consultarLivros() {
        System.out.println("Digite o nome do livro que deseja buscar: ");
        var busca = scanner.nextLine();

        List<Livro> livros = livroRepository.findByTituloContainingIgnoreCase(busca);
        if(livros.size() > 0){
            System.out.println("Livro encontrado na base de dados!");
            System.out.println(livros.get(0));
        }
        else {
            System.out.println("Livro não encontrado na base de dados.");
            System.out.println("Buscando na api Gutendex...");
            String url = BASE + BUSCA + URLEncoder.encode(busca);

            String json = consumoAPI.obterDados(url);
            System.out.println(json);
            DadosResults dadosResults = conversor.obterDados(json, DadosResults.class);

            if(dadosResults.livros().size() > 0){
                System.out.println("Livro encontrado na api gutendex!");
                Livro livro = new Livro(dadosResults.livros().get(0));
                System.out.println(dadosResults.livros().size());

                System.out.println(livro);
                livro.getAutor().getLivros().add(livro);
                autorRepository.save(livro.getAutor());
                livroRepository.save(livro);
                System.out.println("Livro registrado na base de dados.");
            }
            else {
                System.out.println("Nenhum resultado encontrado!");
            }
        }




    }
}
