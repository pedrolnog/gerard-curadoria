package br.edu.univasf.gerard_curador.representacao.quadrinhos;

/** Realizacao textual do roteiro, para console e arquivo .txt. */
public final class RenderizadorTextual implements RenderizadorDeRoteiro {

    @Override
    public String renderizar(RoteiroQuadrinhos roteiro) {
        return exigirRoteiro(roteiro).descrever();
    }

    @Override
    public String getExtensao() {
        return "txt";
    }
}
