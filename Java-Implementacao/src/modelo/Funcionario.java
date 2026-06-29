package modelo;

/**
 * Representa um membro da equipe da clínica, recepcionista ou veterinário.
 * O campo "cargo" define o que essa pessoa pode fazer no sistema
 * (ex: só o veterinário registra resultado de consulta).
 */
public class Funcionario extends Pessoa {

    public enum Cargo {
        RECEPCIONISTA,
        VETERINARIO
    }

    private final Cargo cargo;

    public Funcionario(String nome, String cpf, String telefone, String email, Cargo cargo) {
        super(nome, cpf, telefone, email);
        this.cargo = cargo;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public boolean ehVeterinario() {
        return cargo == Cargo.VETERINARIO;
    }

    @Override
    public String descricao() {
        return getNome() + " (" + cargo + ")";
    }
}