package br.edu.univasf.gerard_curador.dominio.situacao;

/**
 * Escopo em que o curador classificou a situacao.
 *
 * <p>Situacoes multiplicativas ou de divisao da fonte historica ficam fora
 * desta versao (secao 3.2). Rotula-las explicitamente impede que o sistema as
 * force ao modelo aditivo (criterio da secao 10): a validacao apenas relata o
 * rotulo e nao tenta calcular nada.
 */
public enum EscopoDaSituacao {

    ADITIVO("aditivo"),

    FORA_DO_ESCOPO_ADITIVO("fora do escopo aditivo");

    private final String nomeExibido;

    EscopoDaSituacao(String nomeExibido) {
        this.nomeExibido = nomeExibido;
    }

    public String getNomeExibido() {
        return nomeExibido;
    }

    public boolean permiteModeloAditivo() {
        return this == ADITIVO;
    }
}
