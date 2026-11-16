package br.edu.univasf.gerard_curador.persistencia.dto;

import java.util.List;
import java.util.Map;

/**
 * DTO anemico para transporte JSON da relacao estrutural da situacao.
 *
 * <p>Reune os campos aceitos pelos construtores de cada subclasse de RelacaoEstrutural.
 * Diferente do dominio rico, nao valida equacoes ou medidas; o dominio nao importa Gson.
 */
public record RelacaoDto(
        String id,
        String categoria,
        Map<String, QuantidadeDto> papeis,
        String sentidoPrimeira,
        String sentidoSegunda,
        String sentidoResultante,
        String participanteReferente,
        String participanteReferido,
        String direcao,
        List<QuantidadeDto> partes,
        QuantidadeDto todo) {
}
