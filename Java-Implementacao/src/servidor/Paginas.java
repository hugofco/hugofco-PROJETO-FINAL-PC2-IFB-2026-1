package servidor;

import clinica.Clinica;
import modelo.Animal;
import modelo.Tutor;

/**
 * Classe responsável apenas por montar o HTML das telas
 * mantida separada do ServidorClinica para não misturar lógica de negócio
 * com geração de página, o servidor cuida sobre o que mostrar e essa classe
 * decide o como
 */
public final class Paginas {

    private Paginas() {
        // classe utilitária não deve ser instanciada
    }

    /** envolve o conteúdo de cada tela no mesmo layout simples CSS único */
    public static String layout(String titulo, String conteudo) {
        return """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
              <meta charset="UTF-8">
              <title>%s</title>
              <style>
                * { box-sizing: border-box; }
                body {
                  font-family: sans-serif;
                  max-width: 520px;
                  margin: 50px auto;
                  background: #f5f7fa;
                  color: #333;
                  padding: 0 16px;
                }
                h2 { color: #1a2b45; margin-bottom: 20px; }
                label { display: block; margin-top: 14px; font-weight: bold; font-size: 14px; }
                input, select { width: 100%%; padding: 8px 10px; margin-top: 4px;
                  border: 1px solid #ccc; border-radius: 5px; font-size: 15px; }
                input[type=checkbox] { width: auto; margin-top: 0; }
                .checkbox-linha { display: flex; align-items: center; gap: 8px; margin-top: 14px; }
                .checkbox-linha label { margin: 0; }
                button { margin-top: 22px; width: 100%%; padding: 10px; background: #1565C0;
                  color: white; border: none; border-radius: 5px; font-size: 16px; cursor: pointer; }
                button:hover { background: #0D47A1; }
                ul.menu { list-style: none; padding: 0; }
                ul.menu li { margin: 10px 0; }
                ul.menu a, p a { color: #1565C0; text-decoration: none; font-weight: bold; }
                ul.menu a:hover, p a:hover { text-decoration: underline; }
                .hint { font-size: 12px; color: #888; margin-top: 24px; }
                .resultado { background: white; padding: 12px; border-radius: 6px;
                  border: 1px solid #ddd; white-space: pre-wrap; font-family: monospace; }
                fieldset { border: 1px solid #ddd; border-radius: 6px; margin-top: 18px; padding: 10px 14px; }
                legend { font-weight: bold; color: #1a2b45; }
              </style>
            </head>
            <body>
              %s
            </body>
            </html>
            """.formatted(titulo, conteudo);
    }

    public static String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /**Form de cadastro de tutor + animal, com campos específicos por espécie via JS simples. */
    public static final String FORM_CADASTRO = """
        <h2>Cadastrar tutor e animal</h2>
        <form method="POST" action="/cadastro">
          <fieldset>
            <legend>Dados do tutor</legend>
            <label for="tutorNome">Nome</label>
            <input id="tutorNome" name="tutorNome" required>
            <label for="tutorCpf">CPF</label>
            <input id="tutorCpf" name="tutorCpf" required>
            <label for="tutorTelefone">Telefone</label>
            <input id="tutorTelefone" name="tutorTelefone" required>
            <label for="tutorEmail">E-mail</label>
            <input id="tutorEmail" name="tutorEmail" type="email" required>
          </fieldset>

          <fieldset>
            <legend>Dados do animal</legend>
            <label for="animalNome">Nome do animal</label>
            <input id="animalNome" name="animalNome" required>
            <label for="animalPeso">Peso (kg)</label>
            <input id="animalPeso" name="animalPeso" type="number" step="0.1" min="0" required>

            <label for="especieAnimal">Espécie</label>
            <select id="especieAnimal" name="especieAnimal" onchange="atualizarCamposEspecie()">
              <option value="CACHORRO">Cachorro</option>
              <option value="GATO">Gato</option>
              <option value="PASSARO">Pássaro</option>
            </select>

            <div id="campoPorte">
              <label for="porte">Porte</label>
              <select id="porte" name="porte">
                <option value="PEQUENO">Pequeno</option>
                <option value="MEDIO">Médio</option>
                <option value="GRANDE">Grande</option>
              </select>
            </div>

            <div id="campoEspeciePassaro" style="display:none">
              <label for="especiePassaro">Espécie da ave</label>
              <select id="especiePassaro" name="especiePassaro">
                <option value="CALOPSITA">Calopsita</option>
                <option value="PERIQUITO">Periquito</option>
                <option value="CANARIO">Canário</option>
                <option value="OUTRA">Outra</option>
              </select>
            </div>
          </fieldset>

          <button type="submit">Cadastrar →</button>
        </form>
        <p class="hint"><a href="/">Voltar ao menu</a></p>

        <script>
          // Mostra/esconde campos específicos conforme a espécie escolhida —
          // só uma facilidade de tela; a validação real é feita no Java.
          function atualizarCamposEspecie() {
            var especie = document.getElementById('especieAnimal').value;
            document.getElementById('campoPorte').style.display =
              (especie === 'PASSARO') ? 'none' : 'block';
            document.getElementById('campoEspeciePassaro').style.display =
              (especie === 'PASSARO') ? 'block' : 'none';
          }
        </script>
        """;

    /**Form de registro de consulta, listando os animais já cadastrados. */
    public static String formConsulta(Clinica clinica) {
        StringBuilder opcoesAnimais = new StringBuilder();
        for (Tutor tutor : clinica.getTutores()) {
            for (Animal animal : tutor.getAnimais()) {
                opcoesAnimais.append("<option value=\"").append(animal.getRga()).append("\">")
                        .append(escapeHtml(animal.getNome())).append(" — ")
                        .append(escapeHtml(animal.getEspecieDescricao()))
                        .append(" (RGA ").append(animal.getRga()).append(")</option>\n");
            }
        }
        if (opcoesAnimais.length() == 0) {
            opcoesAnimais.append("<option value=\"\">Nenhum animal cadastrado ainda</option>");
        }

        return """
            <h2>Registrar consulta</h2>
            <form method="POST" action="/consulta">
              <label for="rgaAnimal">Animal</label>
              <select id="rgaAnimal" name="rgaAnimal" required>
                %s
              </select>

              <label for="motivo">Motivo do atendimento</label>
              <select id="motivo" name="motivo">
                <option value="CHECK_UP">Check-up</option>
                <option value="EMERGENCIA">Emergência</option>
                <option value="RETORNO">Retorno</option>
                <option value="VACINACAO">Vacinação</option>
                <option value="CIRURGIA">Cirurgia</option>
              </select>

              <label for="resultado">Resultado / observações</label>
              <input id="resultado" name="resultado" placeholder="Ex: animal saudável, retorno em 30 dias">

              <label for="vacina">Vacina aplicada (deixe vazio se não houve)</label>
              <input id="vacina" name="vacina" placeholder="Ex: V10, Antirrábica">
              <p class="hint">Ignorado automaticamente se o animal selecionado for um pássaro.</p>

              <div class="checkbox-linha">
                <input id="internar" name="internar" type="checkbox">
                <label for="internar">Internar este animal agora</label>
              </div>

              <button type="submit">Registrar →</button>
            </form>
            <p class="hint"><a href="/">Voltar ao menu</a></p>
            """.formatted(opcoesAnimais.toString());
    }

    /** Form de período para o relatório gerencial. */
    public static final String FORM_RELATORIO = """
        <h2>Relatório gerencial</h2>
        <form method="POST" action="/relatorio">
          <label for="dataInicio">Data inicial</label>
          <input id="dataInicio" name="dataInicio" type="date" required>

          <label for="dataFim">Data final</label>
          <input id="dataFim" name="dataFim" type="date" required>

          <button type="submit">Gerar relatório →</button>
        </form>
        <p class="hint"><a href="/">Voltar ao menu</a></p>
        """;
}