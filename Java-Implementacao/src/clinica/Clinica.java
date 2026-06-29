package clinica;

import atendimento.Consulta;
import atendimento.ContaMedica;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import modelo.Animal;
import modelo.Funcionario;
import modelo.Tutor;

/**
 * Classe de gestão da clínica: centraliza cadastro de tutores/animais,
 * registro de consultas e geração do relatório gerencial.
 *
 * As consultas e suas respectivas contas médicas são guardadas num Map
 * (em vez de duas listas separadas) para garantir que cada Consulta
 * sempre aponte para a ContaMedica correta, sem depender de os dois
 * serem inseridos sempre na mesma sequência em listas paralelas.
 */
public class Clinica {

    private final List<Tutor> tutores = new ArrayList<>();
    private final List<Funcionario> funcionarios = new ArrayList<>();
    private final Map<Consulta, ContaMedica> contasPorConsulta = new LinkedHashMap<>();

    public void cadastrarTutor(Tutor tutor) {
        tutores.add(tutor);
    }

    public void cadastrarFuncionario(Funcionario funcionario) {
        funcionarios.add(funcionario);
    }

    /** Cadastra o animal vinculando ele no tutor informado. */
    public void cadastrarAnimal(Animal animal) {
        animal.getTutor().adicionarAnimal(animal);
    }

    public void registrarConsulta(Consulta consulta, ContaMedica conta) {
        contasPorConsulta.put(consulta, conta);
        consulta.getAnimal().registrarConsulta(consulta);
    }

    public List<Tutor> getTutores() {
        return tutores;
    }

    public List<Funcionario> getFuncionarios() {
        return funcionarios;
    }

    public List<Consulta> getConsultas() {
        return new ArrayList<>(contasPorConsulta.keySet());
    }

    /** Gera o relatório gerencial pedido pelo enunciado para um período. */
    public RelatorioPeriodo gerarRelatorio(LocalDate inicio, LocalDate fim) {
        double totalArrecadado = 0;
        int quantidadeConsultas = 0;
        Set<Animal> animaisAtendidos = new HashSet<>();

        for (Map.Entry<Consulta, ContaMedica> entrada : contasPorConsulta.entrySet()) {
            Consulta consulta = entrada.getKey();
            LocalDate data = consulta.getData();
            boolean dentroDoPeriodo = !data.isBefore(inicio) && !data.isAfter(fim);
            if (dentroDoPeriodo) {
                totalArrecadado += entrada.getValue().calcularValorFinal();
                quantidadeConsultas++;
                animaisAtendidos.add(consulta.getAnimal());
            }
        }

        return new RelatorioPeriodo(inicio, fim, totalArrecadado, quantidadeConsultas, animaisAtendidos);
    }
}