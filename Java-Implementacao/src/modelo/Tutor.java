package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa o dono/responsável por um ou mais animais.
 * A clínica precisa, no mínimo, de nome e telefone de contato (requisito do
 * enunciado); os demais campos de Pessoa são reaproveitados normalmente.
 *
 * O Tutor mantém a lista dos próprios animais para que a recepção consiga
 * localizar rapidamente "quais bichos esse dono tem cadastrado".
 */
public class Tutor extends Pessoa {

    private final List<Animal> animais = new ArrayList<>();

    public Tutor(String nome, String cpf, String telefone, String email) {
        super(nome, cpf, telefone, email);
    }

    public void adicionarAnimal(Animal animal) {
        animais.add(animal);
    }

    public List<Animal> getAnimais() {
        return animais;
    }

    @Override
    public String descricao() {
        return getNome() + " (" + animais.size() + " animal(is) cadastrado(s))";
    }
}