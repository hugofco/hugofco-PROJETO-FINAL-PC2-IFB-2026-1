package modelo;

import comportamento.CarteiraVacinacao;
import comportamento.Internacao;
import comportamento.Internavel;
import comportamento.RegistroVacina;
import comportamento.Vacinavel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Cachorro tem porte (influencia o valor da consulta), recebe vacinas
 * (implementa Vacinavel) e pode ser internado (implementa Internavel).
 */
public class Cachorro extends Animal implements Vacinavel, Internavel {

    private final Porte porte;
    private final CarteiraVacinacao carteiraVacinacao = new CarteiraVacinacao();
    private final List<Internacao> internacoes = new ArrayList<>();

    public Cachorro(String nome, double peso, Tutor tutor, Porte porte) {
        super(nome, peso, tutor);
        this.porte = porte;
    }

    public Porte getPorte() {
        return porte;
    }

    @Override
    public double calcularValorBaseConsulta() {
        switch (porte) {
            case GRANDE: return 150.0;
            case MEDIO: return 120.0;
            default: return 90.0;
        }
    }

    @Override
    public String getEspecieDescricao() {
        return "Cachorro";
    }

    @Override
    public void registrarVacina(RegistroVacina registro) {
        carteiraVacinacao.adicionarRegistro(registro);
    }

    @Override
    public CarteiraVacinacao consultarCarteira() {
        return carteiraVacinacao;
    }

    @Override
    public Internacao internar(LocalDate dataEntrada) {
        Internacao internacao = new Internacao(this, dataEntrada, getValorDiariaInternacao());
        internacoes.add(internacao);
        return internacao;
    }

    @Override
    public double getValorDiariaInternacao() {
        switch (porte) {
            case GRANDE: return 80.0;
            case MEDIO: return 60.0;
            default: return 45.0;
        }
    }

    @Override
    public List<Internacao> getInternacoes() {
        return internacoes;
    }
}