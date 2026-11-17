package br.edu.univasf.gerard_curador.persistencia.dto;

/**
 * DTO anemico para transporte JSON de um participante da historia.
 *
 * <p>Diferente do dominio rico, apenas expoe dados brutos sem invariantes ou comportamento;
 * o dominio nao importa Gson nem conhece esta representacao.
 */
public record ParticipanteDto(
        String id,
        String nome,
        String referenciaVisual) {
}
