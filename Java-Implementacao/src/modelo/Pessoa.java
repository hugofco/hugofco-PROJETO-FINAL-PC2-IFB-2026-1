package modelo;

/**
 * Superclasse abstrata para qualquer pessoa cadastrada no sistema da clínica.
 * Tanto funcionários quanto tutores têm nome e telefone, esse é o contrato
 * mínimo comum entre eles. CPF e e-mail ficam aqui também pois são dados de
 * contato/identificação que todos tem.
 */
public abstract class Pessoa {

    private final String nome;
    private final String cpf;
    private final String telefone;
    private final String email;

    protected Pessoa(String nome, String cpf, String telefone, String email) {
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    /**
     * Cada subclasse decide como se descreve (ex: incluindo cargo, ou
     * quantidade de animais cadastrados). Por isso o método é abstrato aqui.
     */
    public abstract String descricao();
}