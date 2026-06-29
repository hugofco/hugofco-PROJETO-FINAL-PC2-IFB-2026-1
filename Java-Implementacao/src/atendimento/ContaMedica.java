package atendimento;

import comportamento.Internacao;

/**
 * Conta médica final apresentada ao tutor: soma o valor da consulta com o
 * valor da internação (se houver), aplica desconto e devolve o total a
 * pagar. A internação é se quiser aqui, muitas consultas não geram
 * internação, e por isso o campo pode ser null sem problemas
 */
public class ContaMedica {

    public enum FormaPagamento {
        DINHEIRO,
        PIX,
        CARTAO_CREDITO,
        CARTAO_DEBITO
    }

    private final Consulta consulta;
    private final Internacao internacao; // pode ser null
    private final FormaPagamento formaPagamento;
    private final double percentualDesconto; // de 0 a 100

    public ContaMedica(Consulta consulta, Internacao internacao,
                        FormaPagamento formaPagamento, double percentualDesconto) {
        this.consulta = consulta;
        this.internacao = internacao;
        this.formaPagamento = formaPagamento;
        this.percentualDesconto = percentualDesconto;
    }

    public double calcularValorFinal() {
        double total = consulta.calcularValorTotal();
        if (internacao != null) {
            total += internacao.calcularValorTotal();
        }
        double desconto = total * (percentualDesconto / 100.0);
        return total - desconto;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public Internacao getInternacao() {
        return internacao;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public double getPercentualDesconto() {
        return percentualDesconto;
    }
}