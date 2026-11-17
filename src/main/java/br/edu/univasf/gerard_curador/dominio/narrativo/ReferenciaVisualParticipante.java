package br.edu.univasf.gerard_curador.dominio.narrativo;

import java.util.Objects;

/**
 * Chave abstrata do aspecto visual de um participante.
 *
 * <p>Analoga a ReferenciaVisual dos objetos contados, mas separada dela porque
 * personagens e objetos sao catalogos visuais distintos: um renderizador pode
 * ter avatares para pessoas e icones para itens, resolvidos por caminhos
 * diferentes.
 *
 * <p>Guarda "vovo" como chave, nunca um caminho de imagem: pixels e arquivos
 * pertencem ao renderizador da plataforma.
 */
public final class ReferenciaVisualParticipante {

    private static final String CHAVE_INDEFINIDA = "indefinida";

    private final String chave;

    public ReferenciaVisualParticipante(String chave) {
        if (chave == null || chave.isBlank()) {
            throw new IllegalArgumentException("referencia visual de participante exige chave");
        }
        this.chave = chave;
    }

    public static ReferenciaVisualParticipante de(String chave) {
        return new ReferenciaVisualParticipante(chave);
    }

    public static ReferenciaVisualParticipante naoDefinida() {
        return new ReferenciaVisualParticipante(CHAVE_INDEFINIDA);
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
        if (!(obj instanceof ReferenciaVisualParticipante)) {
            return false;
        }
        return chave.equals(((ReferenciaVisualParticipante) obj).chave);
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
