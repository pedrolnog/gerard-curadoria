package br.edu.univasf.gerard_curador.persistencia.dto;

/**
 * DTO anemico para transporte JSON de uma quantidade referenciando um objeto por id.
 *
 * <p>Diferente do dominio rico, nao valida compatibilidade de grandeza, unidade ou sinal;
 * o dominio nao importa Gson nem conhece esta representacao.
 */
public final class QuantidadeDto {

    private String objeto;
    private Integer numero;
    private String grandeza;
    private String unidade;

    public QuantidadeDto() {
    }

    public QuantidadeDto(String objeto, Integer numero, String grandeza, String unidade) {
        this.objeto = objeto;
        this.numero = numero;
        this.grandeza = grandeza;
        this.unidade = unidade;
    }

    public String getObjeto() {
        return objeto;
    }

    public void setObjeto(String objeto) {
        this.objeto = objeto;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getGrandeza() {
        return grandeza;
    }

    public void setGrandeza(String grandeza) {
        this.grandeza = grandeza;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }
}
