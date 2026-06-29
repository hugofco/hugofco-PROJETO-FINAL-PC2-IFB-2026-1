package modelo;

import atendimento.Consulta;
import java.util.ArrayList;
import java.util.List;

/**
 * Superclasse abstrata de todo animal atendido pela clínica.
 *
 * esta classe guarda apenas o que é comum em todos os animais
 * (nome, código de identificação, peso, tutor, histórico de consultas).
 * Particularidades de cada espécie (porte do cão/gato, espécie da ave)
 * ficam nas subclasses
 *
 * O método calcularValorBaseConsulta() é abstrato de propósito, é cada
 * subclasse que sabe o valor base da sua espécie. Isso responde à pergunta
 * "quem sabe calcular o valor de uma consulta?", a resposta escolhida foi
 * "o próprio "animal" sabe o valor base; a Consulta aplica a variação por
 * motivo por cima desse valor".
 *
 * O que é uma vantagem, se a clínica passar a atender coelhos, é só criar uma
 * classe Coelho que estende Animal e implementa este método, nenhuma
 * outra classe do sistema precisa ser alterada.
 */
public abstract class Animal {

    private static int proximoRga = 1;

    private final int rga; // registro do animal gerado automaticamente
    private final String nome;
    private double peso;
    private final Tutor tutor;
    private final List<Consulta> historicoConsultas = new ArrayList<>();

    protected Animal(String nome, double peso, Tutor tutor) {
        this.rga = proximoRga++;
        this.nome = nome;
        this.peso = peso;
        this.tutor = tutor;
    }

    /**cada espécie define seu próprio valor base de consulta. */
    public abstract double calcularValorBaseConsulta();

    /**nome de exibição da espécie (ex: "Cachorro", "Gato", "Pássaro"). */
    public abstract String getEspecieDescricao();

    public void registrarConsulta(Consulta consulta) {
        historicoConsultas.add(consulta);
    }

    public List<Consulta> getHistoricoConsultas() {
        return historicoConsultas;
    }

    public int getRga() {
        return rga;
    }

    public String getNome() {
        return nome;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public Tutor getTutor() {
        return tutor;
    }
}