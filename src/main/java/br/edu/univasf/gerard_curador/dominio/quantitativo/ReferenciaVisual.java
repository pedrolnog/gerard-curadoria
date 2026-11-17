package br.edu.univasf.gerard_curador.dominio.quantitativo;

import java.util.Objects;

/**
 * Chave abstrata que permite a outra camada escolher desenho, icone ou animacao.
 *
 * <p>O dominio guarda "rosa_branca" como chave; quem decide se isso vira um PNG,
 * um emoji ou um sprite animado e o renderizador da plataforma. Essa indirecao e
 * o que permite trocar o renderizador textual pelo de quadrinhos sem alterar o
 * dominio (caso de teste 12).
 *
 * <p>Deliberadamente nao guarda caminho de arquivo, cor hexadecimal ou dimensao
 * em pixels: sao decisoes de apresentacao.
 */
public final class ReferenciaVisual {

    private static final String CHAVE_INDEFINIDA = "indefinida";

    private final String chave;

    public ReferenciaVisual(String chave) {
        if (chave == null || chave.isBlank()) {
            throw new IllegalArgumentException("referencia visual exige chave");
        }
        this.chave = chave;
    }

    public static ReferenciaVisual de(String chave) {
        return new ReferenciaVisual(chave);
    }

    /** Referencia ausente, para situacoes curadas antes de qualquer decisao visual. */
    public static ReferenciaVisual naoDefinida() {
        return new ReferenciaVisual(CHAVE_INDEFINIDA);
    }

    public String getChave() {
        return chave;
    }

    public boolean estaDefinida() {
        return !CHAVE_INDEFINIDA.equals(chave);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ReferenciaVisual)) {
            return false;
        }
        return chave.equals(((ReferenciaVisual) obj).chave);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chave);
    }

    @Override
    public String toString() {
        return chave;
    }
}
