package comportamento;

/**
 * Contrato para animais que recebem vacina pelo protocolo da clínica.
 *
 * exatamente a situação descrita na dica de modelagem do enunciado:
 * "vacinar" não faz sentido para todo animal (pássaros não vacinam aqui),
 * então esse comportamento não pode morar na superclasse Animal,  ele
 * pertence a uma interface, implementada só por quem precisa dela.
 */
public interface Vacinavel {

    void registrarVacina(RegistroVacina registro);

    CarteiraVacinacao consultarCarteira();
}