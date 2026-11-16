package br.edu.univasf.gerard_curador.representacao.quadrinhos;

/**
 * Estrategia de realizacao de um roteiro em uma midia concreta (padrao Strategy).
 *
 * <p>O roteiro de quadrinhos e produzido uma vez, a partir da sequencia
 * semantica; cada renderizador apenas o apresenta em um formato. Trocar o
 * renderizador textual pelo HTML nao toca no dominio nem no roteiro (caso de
 * teste 12): quem usa depende desta interface, nao de uma implementacao.
 */
public interface RenderizadorDeRoteiro {

    /** Apresenta o roteiro neste formato. Nunca recalcula a matematica. */
    String renderizar(RoteiroQuadrinhos roteiro);

    /** Extensao de arquivo do formato produzido, sem ponto (ex.: "txt", "html"). */
    String getExtensao();

    /** Validacao comum a todas as estrategias (metodo default da interface). */
    default RoteiroQuadrinhos exigirRoteiro(RoteiroQuadrinhos roteiro) {
        if (roteiro == null) {
            throw new IllegalArgumentException("renderizacao exige roteiro");
        }
        return roteiro;
    }
}
