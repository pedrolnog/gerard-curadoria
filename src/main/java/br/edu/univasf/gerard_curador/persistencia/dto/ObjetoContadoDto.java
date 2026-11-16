package br.edu.univasf.gerard_curador.persistencia.dto;

import java.util.Map;

/**
 * DTO anemico para transporte JSON de um objeto contado do catalogo.
 *
 * <p>Diferente do dominio rico, apenas espelha campos serializaveis sem validar identidade ou familia;
 * o dominio nao importa Gson nem conhece esta representacao.
 */
public record ObjetoContadoDto(
        String id,
        String familia,
        String familiaNome,
        Map<String, String> caracteristicas,
        RealizacaoTextualDto realizacaoTextual,
        String referenciaVisual) {
}
