package br.edu.univasf.gerard_curador.persistencia.dto;

/**
 * DTO anemico para transporte JSON do contexto narrativo da situacao.
 *
 * <p>Diferente do dominio rico, apenas guarda campos opcionais em texto;
 * o dominio nao importa Gson nem conhece esta representacao.
 */
public final class ContextoDto {

    private String local;
    private String situacao;
    private String momento;

    public ContextoDto() {
    }

    public ContextoDto(String local, String situacao, String momento) {
        this.local = local;
        this.situacao = situacao;
        this.momento = momento;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getMomento() {
        return momento;
    }

    public void setMomento(String momento) {
        this.momento = momento;
    }
}
