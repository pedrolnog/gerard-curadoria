package br.edu.univasf.gerard_curador.persistencia.dto;

/**
 * DTO anemico para transporte JSON de um participante da historia.
 *
 * <p>Diferente do dominio rico, apenas expoe dados brutos sem invariantes ou comportamento;
 * o dominio nao importa Gson nem conhece esta representacao.
 */
public final class ParticipanteDto {

    private String id;
    private String nome;
    private String referenciaVisual;

    public ParticipanteDto() {
    }

    public ParticipanteDto(String id, String nome, String referenciaVisual) {
        this.id = id;
        this.nome = nome;
        this.referenciaVisual = referenciaVisual;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getReferenciaVisual() {
        return referenciaVisual;
    }

    public void setReferenciaVisual(String referenciaVisual) {
        this.referenciaVisual = referenciaVisual;
    }
}
