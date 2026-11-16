package br.edu.univasf.gerard_curador.persistencia.dto;

/**
 * DTO anemico para transporte JSON de uma transformacao ordenada na narrativa.
 *
 * <p>Diferente do dominio rico, apenas referencia participantes e quantidades por id;
 * o dominio nao importa Gson nem conhece esta representacao.
 */
public record TransformacaoDto(
        Integer ordem,
        String marcador,
        String ator,
        String destinatario,
        AcaoDto acao,
        QuantidadeDto quantidade) {
}
