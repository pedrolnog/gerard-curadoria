package br.edu.univasf.gerard_curador.dominio.estrutural;

/**
 * Funcao ocupada por uma quantidade na representacao da situacao.
 *
 * <p>Vocabulario fechado das posicoes que um numero pode ocupar numa estrutura
 * aditiva. E o papel, e nao o valor, que da sentido ao numero: 12 pode ser um
 * estado inicial, uma transformacao ou um valor relativo, e a diferenca muda
 * completamente o que a situacao afirma.
 *
 * <p>Os papeis cobrem as variacoes exigidas pela matriz de generalizacao:
 * transformacao (Maria ganhou 22 figurinhas), composicao parte-todo (Paulo tem
 * 17 bolas, 11 azuis) e comparacao (Jose tem 8 bolas a mais que Paulo).
 *
 * <p>Deliberadamente <em>nao</em> implementa as seis categorias de estruturas
 * aditivas do Gerard, listadas no escopo como fora desta versao. Sao papeis
 * concretos que as situacoes curadas exigem, nao uma taxonomia teorica.
 */
public enum PapelQuantitativo {

    /** O que se possuia antes de qualquer transformacao. */
    ESTADO_INICIAL("estado inicial"),

    /** Posse apos a primeira transformacao, quando ha mais de uma. */
    ESTADO_INTERMEDIARIO("estado intermediario"),

    /** O que se possui ao final da sequencia. */
    ESTADO_FINAL("estado final"),

    /** Primeira mudanca da sequencia temporal. */
    TRANSFORMACAO_PRIMEIRA("primeira transformacao"),

    /** Segunda mudanca da sequencia temporal. */
    TRANSFORMACAO_SEGUNDA("segunda transformacao"),

    /** Efeito combinado das transformacoes: o saldo da historia inteira. */
    TRANSFORMACAO_RESULTANTE("transformacao resultante"),

    /** Uma das partes de uma composicao: as 11 bolas azuis das 17 de Paulo. */
    PARTE("parte"),

    /** O todo de uma composicao: as 17 bolas de Paulo. */
    TODO("todo"),

    /** Diferenca declarada entre dois participantes: as 8 bolas a mais de Jose. */
    VALOR_RELATIVO("valor relativo"),

    /** Termo tomado como base da comparacao: Paulo, no enunciado das bolas. */
    REFERENTE("referente"),

    /** Termo comparado ao referente: Jose, no mesmo enunciado. */
    REFERIDO("referido");

    private final String nomeExibido;

    PapelQuantitativo(String nomeExibido) {
        this.nomeExibido = nomeExibido;
    }

    public String getNomeExibido() {
        return nomeExibido;
    }

    public boolean ehEstado() {
        return this == ESTADO_INICIAL || this == ESTADO_INTERMEDIARIO || this == ESTADO_FINAL;
    }

    public boolean ehTransformacao() {
        return this == TRANSFORMACAO_PRIMEIRA
                || this == TRANSFORMACAO_SEGUNDA
                || this == TRANSFORMACAO_RESULTANTE;
    }

    public boolean ehComposicao() {
        return this == PARTE || this == TODO;
    }

    public boolean ehComparacao() {
        return this == VALOR_RELATIVO || this == REFERENTE || this == REFERIDO;
    }
}
