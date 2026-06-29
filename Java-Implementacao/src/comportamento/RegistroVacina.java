package comportamento;

import java.time.LocalDate;

/**
 * Um único registro de vacina aplicada: nome da vacina e data de aplicação.
 * ter ele como classe própria (em vez de um campo de texto único) é o que
 * permite a carteira de vacinação crescer com o tempo e ser consultada
 * vacina por vacina, como o enunciado pede ("consultar e atualizar").
 */
public class RegistroVacina {

    private final String nomeVacina;
    private final LocalDate dataAplicacao;

    public RegistroVacina(String nomeVacina, LocalDate dataAplicacao) {
        this.nomeVacina = nomeVacina;
        this.dataAplicacao = dataAplicacao;
    }

    public String getNomeVacina() {
        return nomeVacina;
    }

    public LocalDate getDataAplicacao() {
        return dataAplicacao;
    }

    @Override
    public String toString() {
        return nomeVacina + " em " + dataAplicacao;
    }
}