package comportamento;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Histórico de vacinação de um animal. Mantida como classe própria (e não
 * só uma lista solta dentro do Animal) para deixar claro que é um conceito
 * com identidade própria, a "carteirinha de vacinação" citada no
 * enunciado.
 */
public class CarteiraVacinacao {

    private final List<RegistroVacina> registros = new ArrayList<>();

    public void adicionarRegistro(RegistroVacina registro) {
        registros.add(registro);
    }

    public List<RegistroVacina> getRegistros() {
        return Collections.unmodifiableList(registros);
    }
}