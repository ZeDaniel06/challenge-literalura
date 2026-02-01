package br.com.zedaniel.literalura.principal;

import br.com.zedaniel.literalura.model.Autor;
import br.com.zedaniel.literalura.model.DadosResults;
import br.com.zedaniel.literalura.model.Livro;
import br.com.zedaniel.literalura.repository.AutorRepository;
import br.com.zedaniel.literalura.repository.LivroRepository;
import br.com.zedaniel.literalura.service.ConsumoAPI;
import br.com.zedaniel.literalura.service.ConversaoDados;

import java.net.URLEncoder;
import java.util.*;

public class Principal {

    Scanner scanner = new Scanner(System.in);
    ConsumoAPI consumoAPI = new ConsumoAPI();
    ConversaoDados conversor = new ConversaoDados();

    private final String BASE = "https://gutendex.com/books";
    private final String SORTPOPULAR = "/?sort=popular";
    private final String BUSCA = "/?search=";
    private String buscaA;
    private String buscaB;

    private LivroRepository livroRepository;
    private AutorRepository autorRepository;

    private int menu;

    public Principal(LivroRepository livroRepository, AutorRepository autorRepository){
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
    }

    public int coletaMenu(String mensagem){
        boolean coletado = false;
        int menu = -1;
        while(coletado == false){
            try{
                String mensagemMenu = mensagem;
                System.out.println(mensagemMenu);
                menu = scanner.nextInt();
                scanner.nextLine();
                coletado = true;
            }catch (InputMismatchException e){
                System.out.println("Você deve digitar apenas números!");
                scanner.nextLine();
                menu = -1;
            }
        }
        return menu;
    }

    public void exibirMenu(){
        menu = -1;

        String mensagem = """
                    Escolha uma opção:
                    1 - Buscar livro por título
                    2 - Exibir livros registrados
                    3 - Exibir autores registrados
                    4 - Exibir autores vivos em ano específico
                    5 - Exibir livros de idioma específico
                    6 - Top 10 mais baixados
                    7 - Número máximo de downloads
                    8 - Buscar autor na base de dados
                    
                    0 - Sair
                    """;

        while (menu != 0){

            menu = coletaMenu(mensagem);

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
                case 6:
                    top10Downloads();
                    break;
                case 7:
                    maxDownloads();
                    break;
                case 8:
                    buscarAutor();
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

    private void buscarAutor() {
        System.out.println("Digite o nome do autor que deseja buscar: ");
        var busca = scanner.nextLine();

        List<Autor> autores = autorRepository.findByNameContainingIgnoreCase(busca);
        if(autores.size() > 0){
            autores.stream().forEach(System.out::println);
        }
        else {
            System.out.println("Nenhum autor encontrado na base de dados que corresponda a esta busca.");
        }
    }

    private void maxDownloads() {
        List<Livro> livros = livroRepository.findAll();
        double soma = livros.stream()
                .mapToDouble(Livro::getNumDownloads)
                .sum();
        System.out.println("A soma de todos os downloads é de: " + soma);
    }

    private void top10Downloads() {
        menu = -1;
        while (menu != 1 && menu != 2){
            String mensagem = """
                Obter o top 10 da api ou da base de dados?
                1 - API
                2 - Base de dados
                """;
            menu = coletaMenu(mensagem);
            switch (menu){
                case 1:
                    top10Api();
                    break;
                case 2:
                    top10Base();
                    break;
                default:
                    System.out.println("Opção inválida");
                    break;
            }
        }



    }

    private void top10Api(){
        String url = BASE + SORTPOPULAR;

        String json = consumoAPI.obterDados(url);
        //System.out.println(json);
        DadosResults dadosResults = conversor.obterDados(json, DadosResults.class);
        List<Livro> livros = new ArrayList<>();
        dadosResults.livros().stream()
                .limit(10)
                .forEach(d ->{
                    Livro livro = new Livro(d);
                    livros.add(livro);
                });

        for(int i = 0; i < 10; i++){
            System.out.println(i+1 + "° lugar:");
            System.out.println(livros.get(i));
        }
    }
    private void top10Base(){
        List<Livro> livros = livroRepository.findTop10ByOrderByNumDownloadsDesc();
        if(livros.size() > 0){
            for(int i = 0; i < livros.size(); i++){
                System.out.println(i+1 + "° lugar:");
                System.out.println(livros.get(i));
            }
        }
        else {
            System.out.println("Você não possui nenhum livro cadastrado na base de dados.");
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
        String mensagem = "Que ano você deseja buscar? ";
        var ano = coletaMenu(mensagem);

        List<Autor> autores = autorRepository.buscarAutorPorAno(ano);
        if(autores.size() == 0){
            System.out.println("Não havia nenhum autor vivo neste ano registrado na base de dados.");
        }
        else {
            autores.stream()
                    .forEach(System.out::println);
        }
        DoubleSummaryStatistics statistics = new DoubleSummaryStatistics();


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
