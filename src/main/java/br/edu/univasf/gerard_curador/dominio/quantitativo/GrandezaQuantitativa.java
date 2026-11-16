package br.edu.univasf.gerard_curador.dominio.quantitativo;

/**
 * Natureza da quantidade: o que esta sendo medido.
 *
 * <p>Vocabulario fechado representado por enumeracao, conforme o objetivo de
 * aprendizagem 4. Uma String livre permitiria "contagem", "Contagem" e
 * "contagens" como valores distintos; a enumeracao torna a comparacao exata e
 * o conjunto de valores conhecido em tempo de compilacao.
 *
 * <p>A grandeza detem a autoridade sobre quais unidades lhe sao compativeis.
 * Esse conhecimento nao pertence a Quantidade nem a interface: perguntar
 * "quilograma serve para dinheiro?" e uma pergunta sobre a natureza da
 * grandeza, entao ela mesma responde.
 */
public enum GrandezaQuantitativa {

    /** Contagem de objetos discretos: rosas, figurinhas, bolas. */
    CONTAGEM("contagem"),

    /** Valor monetario: reais das situacoes de feira e mesada. */
    VALOR_MONETARIO("valor monetario"),

    /** Massa: os quilogramas de arroz da situacao da dispensa. */
    MASSA("massa");

    private final String nomeExibido;

    GrandezaQuantitativa(String nomeExibido) {
        this.nomeExibido = nomeExibido;
    }

    public String getNomeExibido() {
        return nomeExibido;
    }

    /**
     * A grandeza decide se uma unidade lhe pertence.
     *
     * <p>Esta e a regra que impede "5 quilogramas de dinheiro" de existir.
     */
    public boolean aceitaUnidade(UnidadeMedida unidade) {
        return unidade != null && unidade.getGrandeza() == this;
    }

    /**
     * Duas grandezas so operam juntas quando sao a mesma grandeza.
     *
     * <p>Nao ha conversao implicita entre contagem e dinheiro: converter exigiria
     * um conhecimento (preco unitario) que nao pertence a este objeto.
     */
    public boolean ehCompativelCom(GrandezaQuantitativa outra) {
        return this == outra;
    }

    /** Sobrecarga: verdadeiro quando a unidade mede esta grandeza. */
    public boolean ehCompativelCom(UnidadeMedida unidade) {
        return aceitaUnidade(unidade);
    }

    /** Unidade usada quando o curador nao informa outra. */
    public UnidadeMedida getUnidadePadrao() {
        return switch (this) {
            case CONTAGEM -> UnidadeMedida.UNIDADE;
            case VALOR_MONETARIO -> UnidadeMedida.REAL;
            case MASSA -> UnidadeMedida.QUILOGRAMA;
        };
    }
}
