package br.edu.univasf.gerard_curador.dominio.narrativo;

import java.util.Objects;

/**
 * Identidade e nome de uma pessoa ou entidade da historia.
 *
 * <p>Entidade mutavel por setters validados; o id nao muda (e a identidade
 * usada em equals/hashCode).
 *
 * <p>Responde a pergunta 6 da apresentacao final: o participante nao guarda
 * permanentemente o papel de destinatario. "Vovo" e uma identidade estavel; ser
 * ator ou destinataria e uma posicao que ela ocupa <em>em uma transformacao
 * especifica</em>. Guardar o papel dentro do participante impediria que a mesma
 * pessoa desse rosas numa cena e recebesse figurinhas em outra, e obrigaria a
 * duplicar o participante a cada mudanca de papel.
 *
 * <p>Por isso o papel mora em TransformacaoNarrativa, que referencia
 * participantes como ator e destinatario. Aqui ficam apenas identidade e nome.
 *
 * <p>Entidade tambem cobre nao pessoas presentes nas situacoes historicas: uma
 * caixa, um buque, uma loja ou um vaso podem possuir objetos e participar de
 * transformacoes.
 */
public final class ParticipanteNarrativo {

    private final String id;
    private String nomeExibido;
    private ReferenciaVisualParticipante referenciaVisual;

    public ParticipanteNarrativo(String id, String nomeExibido) {
        this(id, nomeExibido, null);
    }

    public ParticipanteNarrativo(String id,
                                 String nomeExibido,
                                 ReferenciaVisualParticipante referenciaVisual) {
        this.id = validarId(id);
        this.nomeExibido = validarNomeExibido(nomeExibido);
        this.referenciaVisual = normalizarReferenciaVisual(referenciaVisual);
    }

    private static String validarId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("participante exige id");
        }
        return id;
    }

    private static String validarNomeExibido(String nomeExibido) {
        if (nomeExibido == null || nomeExibido.isBlank()) {
            throw new IllegalArgumentException("participante exige nome exibido");
        }
        return nomeExibido;
    }

    private static ReferenciaVisualParticipante normalizarReferenciaVisual(
            ReferenciaVisualParticipante referenciaVisual) {
        return referenciaVisual == null
                ? ReferenciaVisualParticipante.naoDefinida()
                : referenciaVisual;
    }

    public String getId() {
        return id;
    }

    public String getNomeExibido() {
        return nomeExibido;
    }

    /**
     * Altera o nome exibido do participante.
     */
    public void setNomeExibido(String nomeExibido) {
        this.nomeExibido = validarNomeExibido(nomeExibido);
    }

    public ReferenciaVisualParticipante getReferenciaVisual() {
        return referenciaVisual;
    }

    /**
     * Altera a referencia visual do participante.
     */
    public void setReferenciaVisual(ReferenciaVisualParticipante referenciaVisual) {
        this.referenciaVisual = normalizarReferenciaVisual(referenciaVisual);
    }

    /**
     * A identidade e o id curado.
     *
     * <p>Dois participantes chamados "Maria" em situacoes diferentes sao pessoas
     * diferentes quando seus ids diferem; o nome exibido nao define identidade.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ParticipanteNarrativo)) {
            return false;
        }
        return id.equals(((ParticipanteNarrativo) obj).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nomeExibido;
    }
}
