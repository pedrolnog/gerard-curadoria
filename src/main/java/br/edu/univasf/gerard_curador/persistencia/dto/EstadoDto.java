package br.edu.univasf.gerard_curador.persistencia.dto;

import java.util.List;

/**
 * DTO anemico para transporte JSON de um estado de posse situado no tempo.
 *
 * <p>Diferente do dominio rico, apenas lista itens e referencias por id sem comportamento de inventario;
 * o dominio nao importa Gson nem conhece esta representacao.
 */
public record EstadoDto(
        String possuidor,
        Integer ordemTemporal,
        String rotuloTemporal,
        List<QuantidadeDto> itens) {
}
