package br.edu.univasf.gerard_curador.persistencia.dto;

/**
 * DTO anemico para transporte JSON de uma transformacao ordenada na narrativa.
 *
 * <p>Diferente do dominio rico, apenas referencia participantes e quantidades por id;
 * o dominio nao importa Gson nem conhece esta representacao.
 */
public final class TransformacaoDto {

    private Integer ordem;
    private String marcador;
    private String ator;
    private String destinatario;
    private AcaoDto acao;
    private QuantidadeDto quantidade;

    public TransformacaoDto() {
    }

    public TransformacaoDto(Integer ordem,
                            String marcador,
                            String ator,
                            String destinatario,
                            AcaoDto acao,
                            QuantidadeDto quantidade) {
        this.ordem = ordem;
        this.marcador = marcador;
        this.ator = ator;
        this.destinatario = destinatario;
        this.acao = acao;
        this.quantidade = quantidade;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public String getMarcador() {
        return marcador;
    }

    public void setMarcador(String marcador) {
        this.marcador = marcador;
    }

    public String getAtor() {
        return ator;
    }

    public void setAtor(String ator) {
        this.ator = ator;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public AcaoDto getAcao() {
        return acao;
    }

    public void setAcao(AcaoDto acao) {
        this.acao = acao;
    }

    public QuantidadeDto getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(QuantidadeDto quantidade) {
        this.quantidade = quantidade;
    }
}
