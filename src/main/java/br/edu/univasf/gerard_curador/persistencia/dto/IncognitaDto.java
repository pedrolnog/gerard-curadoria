package br.edu.univasf.gerard_curador.persistencia.dto;

/**
 * DTO anemico para transporte JSON da incognita quantitativa designada.
 *
 * <p>Diferente do dominio rico, apenas transporta os dados da pergunta e eventual resolucao;
 * o dominio nao importa Gson nem conhece esta representacao.
 */
public record IncognitaDto(
        String papel,
        String pergunta,
        Boolean resolvida,
        Integer valorResolvido,
        String objetoResolvido) {
}
