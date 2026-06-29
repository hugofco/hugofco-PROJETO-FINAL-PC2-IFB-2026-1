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
 * o gato, assim como o cachorro, tem porte, vacina e pode ser internado.
 *
 * cachorro e gato tem estrutura parecida (ambos tem porte, ambos vacinam,
 * ambos internam), mas isso é coincidência de domínio, não uma relação de
 * herança real, por isso não existe uma superclasse comum entre os dois.
 * O que evita duplicação de lógica é o método abstrato
 * calcularValorBaseConsulta() em Animal: cada classe define seu próprio
 * valor, sem copiar/colar cálculo. Olhe o README para a justificativa completa.
 */
public class Gato extends Animal implements Vacinavel, Internavel {

    private final Porte porte;
    private final CarteiraVacinacao carteiraVacinacao = new CarteiraVacinacao();
    private final List<Internacao> internacoes = new ArrayList<>();

    public Gato(String nome, double peso, Tutor tutor, Porte porte) {
        super(nome, peso, tutor);
        this.porte = porte;
    }

    public Porte getPorte() {
        return porte;
    }

    @Override
    public double calcularValorBaseConsulta() {
        switch (porte) {
            case GRANDE: return 130.0;
            case MEDIO: return 100.0;
            default: return 80.0;
        }
    }

    @Override
    public String getEspecieDescricao() {
        return "Gato";
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
            case GRANDE: return 70.0;
            case MEDIO: return 55.0;
            default: return 40.0;
        }
    }

    @Override
    public List<Internacao> getInternacoes() {
        return internacoes;
    }
}