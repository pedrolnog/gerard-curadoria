package br.edu.univasf.gerard_curador.persistencia.dto;

import java.util.List;

/**
 * DTO anemico raiz para transporte JSON de uma situacao-problema.
 *
 * <p>Diferente do dominio rico ({@code SituacaoProblema}), apenas espelha a estrutura do
 * arquivo JSON sem regras de validacao, calculo ou curadoria; o dominio nao importa Gson.
 */
public record SituacaoDto(
        String id,
        String enunciado,
        String categoria,
        String escopo,
        List<ParticipanteDto> participantes,
        List<ObjetoContadoDto> objetosContados,
        ContextoDto contexto,
        IncognitaDto incognita,
        EstadoDto estadoInicial,
        List<TransformacaoDto> transformacoes,
        EstadoDto estadoFinalCurado,
        RelacaoDto relacao,
        List<AmbiguidadeDto> ambiguidades,
        String status,
        String revisor,
        String motivoRecusa) {
}
