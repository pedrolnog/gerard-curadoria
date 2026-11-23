package br.edu.univasf.gerard_curador.persistencia.dto;

/**
 * DTO anemico para transporte JSON da acao de uma transformacao narrativa.
 *
 * <p>Diferente do dominio rico, nao aplica regencia padrao nem valida sentido;
 * o dominio nao importa Gson nem conhece esta representacao.
 */
public final class AcaoDto {

    private String id;
    private String verbo;
    private String participio;
    private String sentido;
    private Boolean exigeDestinatario;
    private String preposicaoDestinatario;

    public AcaoDto() {
    }

    public AcaoDto(String id,
                   String verbo,
                   String participio,
                   String sentido,
                   Boolean exigeDestinatario,
                   String preposicaoDestinatario) {
        this.id = id;
        this.verbo = verbo;
        this.participio = participio;
        this.sentido = sentido;
        this.exigeDestinatario = exigeDestinatario;
        this.preposicaoDestinatario = preposicaoDestinatario;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVerbo() {
        return verbo;
    }

    public void setVerbo(String verbo) {
        this.verbo = verbo;
    }

    public String getParticipio() {
        return participio;
    }

    public void setParticipio(String participio) {
        this.participio = participio;
    }

    public String getSentido() {
        return sentido;
    }

    public void setSentido(String sentido) {
        this.sentido = sentido;
    }

    public Boolean getExigeDestinatario() {
        return exigeDestinatario;
    }

    public void setExigeDestinatario(Boolean exigeDestinatario) {
        this.exigeDestinatario = exigeDestinatario;
    }

    public String getPreposicaoDestinatario() {
        return preposicaoDestinatario;
    }

    public void setPreposicaoDestinatario(String preposicaoDestinatario) {
        this.preposicaoDestinatario = preposicaoDestinatario;
    }
}
