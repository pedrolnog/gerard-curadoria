package br.edu.univasf.gerard_curador.dominio.narrativo;

import java.util.Objects;

/**
 * Identidade e nome de uma pessoa ou entidade da historia.
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
    private final String nomeExibido;
    private final ReferenciaVisualParticipante referenciaVisual;

    public ParticipanteNarrativo(String id, String nomeExibido) {
        this(id, nomeExibido, null);
    }

    public ParticipanteNarrativo(String id,
                                 String nomeExibido,
                                 ReferenciaVisualParticipante referenciaVisual) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("participante exige id");
        }
        if (nomeExibido == null || nomeExibido.isBlank()) {
            throw new IllegalArgumentException("participante exige nome exibido");
        }
        this.id = id;
        this.nomeExibido = nomeExibido;
        this.referenciaVisual = referenciaVisual == null
                ? ReferenciaVisualParticipante.naoDefinida()
                : referenciaVisual;
    }

    public String getId() {
        return id;
    }

    public String getNomeExibido() {
        return nomeExibido;
    }

    public ReferenciaVisualParticipante getReferenciaVisual() {
        return referenciaVisual;
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
