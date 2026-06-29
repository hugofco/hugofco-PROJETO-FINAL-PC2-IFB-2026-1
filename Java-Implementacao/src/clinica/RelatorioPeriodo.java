package clinica;

import java.time.LocalDate;
import java.util.Set;
import modelo.Animal;

/**
 * Objeto simples sem comportamento que carrega o resultado do relatório
 * gerencial de um período: total arrecadado, quantidade de consultas e o
 * conjunto de animais atendidos.
 */
public class RelatorioPeriodo {

    private final LocalDate inicio;
    private final LocalDate fim;
    private final double totalArrecadado;
    private final int quantidadeConsultas;
    private final Set<Animal> animaisAtendidos;

    public RelatorioPeriodo(LocalDate inicio, LocalDate fim, double totalArrecadado,
                             int quantidadeConsultas, Set<Animal> animaisAtendidos) {
        this.inicio = inicio;
        this.fim = fim;
        this.totalArrecadado = totalArrecadado;
        this.quantidadeConsultas = quantidadeConsultas;
        this.animaisAtendidos = animaisAtendidos;
    }

    public LocalDate getInicio() {
        return inicio;
    }

    public LocalDate getFim() {
        return fim;
    }

    public double getTotalArrecadado() {
        return totalArrecadado;
    }

    public int getQuantidadeConsultas() {
        return quantidadeConsultas;
    }

    public Set<Animal> getAnimaisAtendidos() {
        return animaisAtendidos;
    }
}