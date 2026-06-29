package atendimento;

import java.time.LocalDate;
import modelo.Animal;
import modelo.Funcionario;

/**
 * Representa uma consulta veterinária.
 *
 * Decisão de design (dada a pista do enunciado: "quem sabe calcular o valor da
 * consulta?"): o valor base é responsabilidade do próprio Animal, pois é
 * ele quem conhece sua espécie/porte. A Consulta, por sua vez, é quem
 * conhece o motivo do atendimento — então é ela quem aplica o multiplicador
 * de motivo sobre o valor base do animal. Cada classe é responsável só
 * pela informação que ela mesma possui; nenhuma das duas precisa conhecer
 * detalhes internos da outra.
 */
public class Consulta {

    private final LocalDate data;
    private final Funcionario veterinario;
    private final Animal animal;
    private final MotivoConsulta motivo;
    private String resultado; // preenchido pelo veterinário após o atendimento

    public Consulta(LocalDate data, Funcionario veterinario, Animal animal, MotivoConsulta motivo) {
        if (!veterinario.ehVeterinario()) {
            throw new IllegalArgumentException("Apenas um veterinário pode ser responsável por uma consulta.");
        }
        this.data = data;
        this.veterinario = veterinario;
        this.animal = animal;
        this.motivo = motivo;
    }

    /**
     * Valor final da consulta = valor base do animal (espécie/porte) com o
     * multiplicador do motivo do atendimento aplicado por cima.
     */
    public double calcularValorTotal() {
        return animal.calcularValorBaseConsulta() * multiplicadorPorMotivo();
    }

    private double multiplicadorPorMotivo() {
        switch (motivo) {
            case EMERGENCIA: return 1.8;
            case CIRURGIA: return 2.5;
            case VACINACAO: return 0.7;
            case RETORNO: return 0.5;
            default: return 1.0; // CHECK_UP
        }
    }

    public void registrarResultado(String resultado) {
        this.resultado = resultado;
    }

    public LocalDate getData() {
        return data;
    }

    public Funcionario getVeterinario() {
        return veterinario;
    }

    public Animal getAnimal() {
        return animal;
    }

    public MotivoConsulta getMotivo() {
        return motivo;
    }

    public String getResultado() {
        return resultado;
    }
}