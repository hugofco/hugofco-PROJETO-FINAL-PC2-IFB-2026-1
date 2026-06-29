package servidor;

import atendimento.Consulta;
import atendimento.ContaMedica;
import atendimento.MotivoConsulta;
import clinica.Clinica;
import clinica.RelatorioPeriodo;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import comportamento.Internacao;
import comportamento.Internavel;
import comportamento.RegistroVacina;
import comportamento.Vacinavel;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import modelo.Animal;
import modelo.Cachorro;
import modelo.EspeciePassaro;
import modelo.Funcionario;
import modelo.Gato;
import modelo.Passaro;
import modelo.Porte;
import modelo.Tutor;

/**
 * Servidor HTTP simples da clínica veterinária, no mesmo espírito do
 * WebScanner usado como referência: sem framework, só com
 * com.sun.net.httpserver, servindo páginas HTML geradas em memória e
 * processando os POSTs dos formulários.
 *
 * Fluxo em telas separadas, navegáveis por link:
 *   /            -> menu principal
 *   /cadastro     -> formulário de cadastro de tutor + animal
 *   /consulta     -> formulário de registro de consulta (+ internação opcional)
 *   /relatorio    -> formulário de período + resultado do relatório
 */
public class ServidorClinica {

    private final HttpServer servidor;
    private final Clinica clinica;
    private final Funcionario veterinarioPadrao;

    public ServidorClinica(int porta, Clinica clinica) throws IOException {
        this.clinica = clinica;
        this.veterinarioPadrao = new Funcionario(
                "Dra. Marina Souza", "000.000.000-00", "(00) 90000-0000",
                "marina@clinica.com", Funcionario.Cargo.VETERINARIO);
        clinica.cadastrarFuncionario(veterinarioPadrao);

        servidor = HttpServer.create(new InetSocketAddress(porta), 0);
        servidor.createContext("/", this::handleMenu);
        servidor.createContext("/cadastro", this::handleCadastro);
        servidor.createContext("/consulta", this::handleConsulta);
        servidor.createContext("/relatorio", this::handleRelatorio);
        servidor.start();
        System.out.println("Clínica Veterinária rodando → abra http://localhost:" + porta);
    }

    public void close() {
        servidor.stop(0);
        System.out.println("Servidor da clínica encerrado.");
    }

    //tela1 menu principal 

    private void handleMenu(HttpExchange t) throws IOException {
        String html = Paginas.layout("Clínica Veterinária", """
            <h2>Clínica Veterinária — menu</h2>
            <ul class="menu">
              <li><a href="/cadastro">Cadastrar tutor e animal</a></li>
              <li><a href="/consulta">Registrar consulta</a></li>
              <li><a href="/relatorio">Relatório gerencial</a></li>
            </ul>
            <p class="hint">Tutores cadastrados: %d &middot; Consultas registradas: %d</p>
            """.formatted(clinica.getTutores().size(), clinica.getConsultas().size()));
        responder(t, html);
    }

    //tela2 cadastro de tutor + animal

    private void handleCadastro(HttpExchange t) throws IOException {
        if ("POST".equalsIgnoreCase(t.getRequestMethod())) {
            Map<String, String> campos = lerCorpoPost(t);
            String resumo = processarCadastro(campos);
            String html = Paginas.layout("Cadastro realizado", """
                <h2>&#10003; Tutor e animal cadastrados!</h2>
                <p>%s</p>
                <p><a href="/cadastro">Cadastrar outro</a> &middot; <a href="/">Voltar ao menu</a></p>
                """.formatted(Paginas.escapeHtml(resumo)));
            responder(t, html);
            return;
        }
        responder(t, Paginas.layout("Cadastro", Paginas.FORM_CADASTRO));
    }

    /**
     * Processa o cadastro e devolve o resumo como valor de retorno e não como
     * campo de instância. Isso evita que duas requisições concorrentes
     * pisem no resumo uma da outra (cada thread do HttpServer atende uma
     * requisição por vez, mas o campo de instância seria compartilhado).
     */
    private String processarCadastro(Map<String, String> campos) {
        Tutor tutor = new Tutor(
                campos.get("tutorNome"),
                campos.get("tutorCpf"),
                campos.get("tutorTelefone"),
                campos.get("tutorEmail"));
        clinica.cadastrarTutor(tutor);

        String especieAnimal = campos.get("especieAnimal");
        String nomeAnimal = campos.get("animalNome");
        double pesoAnimal = parseDoubleSeguro(campos.get("animalPeso"));

        Animal animal;
        switch (especieAnimal) {
            case "CACHORRO":
                animal = new Cachorro(nomeAnimal, pesoAnimal, tutor,
                        Porte.valueOf(campos.get("porte")));
                break;
            case "GATO":
                animal = new Gato(nomeAnimal, pesoAnimal, tutor,
                        Porte.valueOf(campos.get("porte")));
                break;
            default:
                animal = new Passaro(nomeAnimal, pesoAnimal, tutor,
                        EspeciePassaro.valueOf(campos.get("especiePassaro")));
                break;
        }
        clinica.cadastrarAnimal(animal);

        return "Tutor " + tutor.getNome() + " cadastrado com o animal "
                + animal.getNome() + " (" + animal.getEspecieDescricao()
                + ", RGA " + animal.getRga() + ").";
    }

    //tela 3 registro de consulta

    private void handleConsulta(HttpExchange t) throws IOException {
        if ("POST".equalsIgnoreCase(t.getRequestMethod())) {
            Map<String, String> campos = lerCorpoPost(t);
            String resultado = processarConsulta(campos);
            String html = Paginas.layout("Consulta registrada", """
                <h2>&#10003; Consulta registrada!</h2>
                <pre class="resultado">%s</pre>
                <p><a href="/consulta">Registrar outra</a> &middot; <a href="/">Voltar ao menu</a></p>
                """.formatted(Paginas.escapeHtml(resultado)));
            responder(t, html);
            return;
        }
        responder(t, Paginas.layout("Registrar consulta", Paginas.formConsulta(clinica)));
    }

    private String processarConsulta(Map<String, String> campos) {
        int rga = Integer.parseInt(campos.get("rgaAnimal"));
        Animal animal = buscarAnimalPorRga(rga);
        if (animal == null) {
            return "Animal com RGA " + rga + " não encontrado.";
        }

        MotivoConsulta motivo = MotivoConsulta.valueOf(campos.get("motivo"));
        Consulta consulta = new Consulta(LocalDate.now(), veterinarioPadrao, animal, motivo);
        consulta.registrarResultado(campos.getOrDefault("resultado", ""));

        //vacinação só é processada se o animal implementar Vacinavel
        String nomeVacina = campos.get("vacina");
        if (animal instanceof Vacinavel && nomeVacina != null && !nomeVacina.isBlank()) {
            ((Vacinavel) animal).registrarVacina(new RegistroVacina(nomeVacina, LocalDate.now()));
        }

        //internação é opcional, só ocorre se o campo "internar" vier marcado.
        Internacao internacao = null;
        double valorDiaria = 0;
        if ("on".equals(campos.get("internar")) && animal instanceof Internavel) {
            Internavel animalInternavel = (Internavel) animal;
            internacao = animalInternavel.internar(LocalDate.now());
            valorDiaria = animalInternavel.getValorDiariaInternacao();
        }

        ContaMedica conta = new ContaMedica(consulta, internacao,
                ContaMedica.FormaPagamento.PIX, 0);
        clinica.registrarConsulta(consulta, conta);

        StringBuilder resumo = new StringBuilder();
        resumo.append("Animal: ").append(animal.getNome())
              .append(" (").append(animal.getEspecieDescricao()).append(")\n");
        resumo.append("Motivo: ").append(motivo).append("\n");
        resumo.append("Valor da consulta: R$ ").append(String.format("%.2f", consulta.calcularValorTotal())).append("\n");
        if (internacao != null) {
            //valorDiaria já foi obtido acima, sem precisar checar instanceof de novo.
            resumo.append("Internação iniciada — diária de R$ ")
                  .append(String.format("%.2f", valorDiaria)).append("\n");
        }
        resumo.append("Valor total da conta: R$ ").append(String.format("%.2f", conta.calcularValorFinal()));
        return resumo.toString();
    }

    private Animal buscarAnimalPorRga(int rga) {
        for (Tutor tutor : clinica.getTutores()) {
            for (Animal animal : tutor.getAnimais()) {
                if (animal.getRga() == rga) {
                    return animal;
                }
            }
        }
        return null;
    }

    //tela 4 relatório gerencial

    private void handleRelatorio(HttpExchange t) throws IOException {
        if ("POST".equalsIgnoreCase(t.getRequestMethod())) {
            Map<String, String> campos = lerCorpoPost(t);
            LocalDate inicio = LocalDate.parse(campos.get("dataInicio"));
            LocalDate fim = LocalDate.parse(campos.get("dataFim"));
            RelatorioPeriodo relatorio = clinica.gerarRelatorio(inicio, fim);

            StringBuilder animais = new StringBuilder();
            for (Animal a : relatorio.getAnimaisAtendidos()) {
                animais.append("- ").append(a.getNome())
                       .append(" (").append(a.getEspecieDescricao()).append(")\n");
            }
            if (animais.length() == 0) {
                animais.append("(nenhum animal atendido no período)");
            }

            String html = Paginas.layout("Relatório", """
                <h2>Relatório de %s até %s</h2>
                <p>Total arrecadado: <b>R$ %.2f</b></p>
                <p>Consultas realizadas: <b>%d</b></p>
                <p>Animais atendidos:</p>
                <pre class="resultado">%s</pre>
                <p><a href="/relatorio">Novo período</a> &middot; <a href="/">Voltar ao menu</a></p>
                """.formatted(inicio, fim, relatorio.getTotalArrecadado(),
                        relatorio.getQuantidadeConsultas(), Paginas.escapeHtml(animais.toString())));
            responder(t, html);
            return;
        }
        responder(t, Paginas.layout("Relatório gerencial", Paginas.FORM_RELATORIO));
    }

    //utilidades HTTP

    private Map<String, String> lerCorpoPost(HttpExchange t) throws IOException {
        String corpo = new String(t.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> params = new LinkedHashMap<>();
        for (String par : corpo.split("&")) {
            String[] kv = par.split("=", 2);
            if (kv.length == 2) {
                params.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            } else if (kv.length == 1 && !kv[0].isEmpty()) {
                params.put(kv[0], "");
            }
        }
        return params;
    }

    private void responder(HttpExchange t, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        t.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        t.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = t.getResponseBody()) {
            os.write(bytes);
        }
    }

    private double parseDoubleSeguro(String valor) {
        try {
            return Double.parseDouble(valor);
        } catch (Exception e) {
            return 0.0;
        }
    }
}