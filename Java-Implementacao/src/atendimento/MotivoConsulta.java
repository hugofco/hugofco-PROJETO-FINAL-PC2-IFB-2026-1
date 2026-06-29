package atendimento;

/**
 * Motivo do atendimento. O valor cobrado varia conforme o motivo
 * (emergência custa mais que retorno, conforme o enunciado).
 */
public enum MotivoConsulta {
    CHECK_UP,
    EMERGENCIA,
    RETORNO,
    VACINACAO,
    CIRURGIA
}