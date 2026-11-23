package br.edu.univasf.gerard_curador.persistencia.dto;

/**
 * DTO anemico para transporte JSON da incognita quantitativa designada.
 *
 * <p>Diferente do dominio rico, apenas transporta os dados da pergunta e eventual resolucao;
 * o dominio nao importa Gson nem conhece esta representacao.
 */
public final class IncognitaDto {

    private String papel;
    private String pergunta;
    private Boolean resolvida;
    private Integer valorResolvido;
    private String objetoResolvido;

    public IncognitaDto() {
    }

    public IncognitaDto(String papel,
                        String pergunta,
                        Boolean resolvida,
                        Integer valorResolvido,
                        String objetoResolvido) {
        this.papel = papel;
        this.pergunta = pergunta;
        this.resolvida = resolvida;
        this.valorResolvido = valorResolvido;
        this.objetoResolvido = objetoResolvido;
    }

    public String getPapel() {
        return papel;
    }

    public void setPapel(String papel) {
        this.papel = papel;
    }

    public String getPergunta() {
        return pergunta;
    }

    public void setPergunta(String pergunta) {
        this.pergunta = pergunta;
    }

    public Boolean getResolvida() {
        return resolvida;
    }

    public void setResolvida(Boolean resolvida) {
        this.resolvida = resolvida;
    }

    public Integer getValorResolvido() {
        return valorResolvido;
    }

    public void setValorResolvido(Integer valorResolvido) {
        this.valorResolvido = valorResolvido;
    }

    public String getObjetoResolvido() {
        return objetoResolvido;
    }

    public void setObjetoResolvido(String objetoResolvido) {
        this.objetoResolvido = objetoResolvido;
    }
}
