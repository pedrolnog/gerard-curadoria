package br.edu.univasf.gerard_curador.persistencia.dto;

/**
 * DTO anemico para transporte JSON de uma ambiguidade declarada pelo curador.
 *
 * <p>Diferente do dominio rico, apenas carrega trecho e motivo sem validacao;
 * o dominio nao importa Gson nem conhece esta representacao.
 */
public final class AmbiguidadeDto {

    private String trecho;
    private String motivo;

    public AmbiguidadeDto() {
    }

    public AmbiguidadeDto(String trecho, String motivo) {
        this.trecho = trecho;
        this.motivo = motivo;
    }

    public String getTrecho() {
        return trecho;
    }

    public void setTrecho(String trecho) {
        this.trecho = trecho;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
