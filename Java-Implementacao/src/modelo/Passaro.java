package modelo;

import comportamento.Internacao;
import comportamento.Internavel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Pássaro: não implementa Vacinavel, pois o protocolo da clínica não prevê
 * vacinação para aves. Implementa Internavel, pois a clínica também
 * interna aves.
 */
public class Passaro extends Animal implements Internavel {

    private final EspeciePassaro especie;
    private final List<Internacao> internacoes = new ArrayList<>();

    public Passaro(String nome, double peso, Tutor tutor, EspeciePassaro especie) {
        super(nome, peso, tutor);
        this.especie = especie;
    }

    public EspeciePassaro getEspecie() {
        return especie;
    }

    @Override
    public double calcularValorBaseConsulta() {
        switch (especie) {
            case CALOPSITA:
            case PERIQUITO:
                return 70.0;
            case CANARIO:
                return 75.0;
            default:
                return 100.0;
        }
    }

    @Override
    public String getEspecieDescricao() {
        return "Pássaro (" + especie + ")";
    }

    @Override
    public Internacao internar(LocalDate dataEntrada) {
        Internacao internacao = new Internacao(this, dataEntrada, getValorDiariaInternacao());
        internacoes.add(internacao);
        return internacao;
    }

    @Override
    public double getValorDiariaInternacao() {
        return 35.0;
    }

    @Override
    public List<Internacao> getInternacoes() {
        return internacoes;
    }
}