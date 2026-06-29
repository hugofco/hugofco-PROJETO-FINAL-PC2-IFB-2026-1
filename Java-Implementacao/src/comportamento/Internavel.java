package comportamento;

import java.time.LocalDate;
import java.util.List;

/**
 * Contrato para animais que podem ficar internados na clínica.
 *
 * Assim como Vacinavel, esse comportamento foi colocado para interface
 * porque, em outro cenário de modelagem, poderia haver um tipo de animal
 * sem estrutura de internação disponível. Mesmo neste projeto, em que as
 * três espécies internam, manter a interface separada é o que permite essa
 * decisão mudar no futuro sem reestruturar as classes
 */
public interface Internavel {

    Internacao internar(LocalDate dataEntrada);

    /** o valor da diária de internação pode variar por tipo/porte do animal. */
    double getValorDiariaInternacao();

    List<Internacao> getInternacoes();
}