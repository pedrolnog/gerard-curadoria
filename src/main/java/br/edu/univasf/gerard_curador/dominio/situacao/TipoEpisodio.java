package br.edu.univasf.gerard_curador.dominio.situacao;

/**
 * Natureza semantica de um episodio da sequencia narrativa.
 *
 * <p>Corresponde aos rotulos da sequencia esperada na especificacao: ESTADO,
 * EVENTO e PERGUNTA. E o vocabulario minimo que o gerador de roteiro precisa
 * para decidir como desenhar cada quadro, sem reinterpretar o conteudo.
 */
public enum TipoEpisodio {

    /** Fotografia de uma posse num momento: "Vovo possui 2 rosas brancas...". */
    ESTADO("ESTADO"),

    /** Uma transformacao acontecendo: "Vovo da 1 rosa branca a netinha". */
    EVENTO("EVENTO"),

    /** A incognita original, enunciada: "Qual e o total do estado final?". */
    PERGUNTA("PERGUNTA");

    private final String rotulo;

    TipoEpisodio(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
