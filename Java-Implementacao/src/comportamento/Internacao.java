package comportamento;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import modelo.Animal;

/**
 * Representa um período de internação de um animal.
 * Gera uma cobrança própria separada da consulta (requisito do enunciado),
 * calculada com base na quantidade de dias internado multiplicada pela
 * diária que cada animal/espécie já sabe informar
 * (getValorDiariaInternacao(), vindo da interface Internavel).
 */
public class Internacao {

    private final Animal animal;
    private final LocalDate dataEntrada;
    private LocalDate dataSaida;
    private final double valorDiaria;

    public Internacao(Animal animal, LocalDate dataEntrada, double valorDiaria) {
        this.animal = animal;
        this.dataEntrada = dataEntrada;
        this.valorDiaria = valorDiaria;
    }

    public void darAlta(LocalDate dataSaida) {
        this.dataSaida = dataSaida;
    }

    public boolean estaEmAndamento() {
        return dataSaida == null;
    }

    /** Quantidade de dias internados, se ainda não recebeu alta, conta até hoje. */
    public long calcularDiasInternado() {
        LocalDate fim = (dataSaida != null) ? dataSaida : LocalDate.now();
        long dias = ChronoUnit.DAYS.between(dataEntrada, fim);
        return Math.max(dias, 1); // toda internação cobra, no mínimo, 1 diária
    }

    public double calcularValorTotal() {
        return calcularDiasInternado() * valorDiaria;
    }

    public Animal getAnimal() {
        return animal;
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public LocalDate getDataSaida() {
        return dataSaida;
    }
}