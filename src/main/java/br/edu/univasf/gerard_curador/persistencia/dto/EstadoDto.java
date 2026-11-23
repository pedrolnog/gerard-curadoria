package br.edu.univasf.gerard_curador.persistencia.dto;

import java.util.List;

/**
 * DTO anemico para transporte JSON de um estado de posse situado no tempo.
 *
 * <p>Diferente do dominio rico, apenas lista itens e referencias por id sem comportamento de inventario;
 * o dominio nao importa Gson nem conhece esta representacao.
 */
public final class EstadoDto {

    private String possuidor;
    private Integer ordemTemporal;
    private String rotuloTemporal;
    private List<QuantidadeDto> itens;

    public EstadoDto() {
    }

    public EstadoDto(String possuidor,
                     Integer ordemTemporal,
                     String rotuloTemporal,
                     List<QuantidadeDto> itens) {
        this.possuidor = possuidor;
        this.ordemTemporal = ordemTemporal;
        this.rotuloTemporal = rotuloTemporal;
        this.itens = itens;
    }

    public String getPossuidor() {
        return possuidor;
    }

    public void setPossuidor(String possuidor) {
        this.possuidor = possuidor;
    }

    public Integer getOrdemTemporal() {
        return ordemTemporal;
    }

    public void setOrdemTemporal(Integer ordemTemporal) {
        this.ordemTemporal = ordemTemporal;
    }

    public String getRotuloTemporal() {
        return rotuloTemporal;
    }

    public void setRotuloTemporal(String rotuloTemporal) {
        this.rotuloTemporal = rotuloTemporal;
    }

    public List<QuantidadeDto> getItens() {
        return itens;
    }

    public void setItens(List<QuantidadeDto> itens) {
        this.itens = itens;
    }
}
