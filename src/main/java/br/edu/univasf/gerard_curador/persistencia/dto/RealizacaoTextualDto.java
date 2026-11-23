package br.edu.univasf.gerard_curador.persistencia.dto;

/**
 * DTO anemico para transporte JSON das formas linguisticas de um objeto contado.
 *
 * <p>Diferente do dominio rico, apenas agrupa texto bruto sem regras de flexao;
 * o dominio nao importa Gson nem conhece esta representacao.
 */
public final class RealizacaoTextualDto {

    private String singular;
    private String plural;
    private String idioma;

    public RealizacaoTextualDto() {
    }

    public RealizacaoTextualDto(String singular, String plural, String idioma) {
        this.singular = singular;
        this.plural = plural;
        this.idioma = idioma;
    }

    public String getSingular() {
        return singular;
    }

    public void setSingular(String singular) {
        this.singular = singular;
    }

    public String getPlural() {
        return plural;
    }

    public void setPlural(String plural) {
        this.plural = plural;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }
}
