package br.edu.univasf.gerard_curador.persistencia.dto;

/**
 * DTO anemico para transporte JSON da acao de uma transformacao narrativa.
 *
 * <p>Diferente do dominio rico, nao aplica regencia padrao nem valida sentido;
 * o dominio nao importa Gson nem conhece esta representacao.
 */
public record AcaoDto(
        String id,
        String verbo,
        String participio,
        String sentido,
        Boolean exigeDestinatario,
        String preposicaoDestinatario) {
}
