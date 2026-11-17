package br.edu.univasf.gerard_curador.dominio.situacao;

import br.edu.univasf.gerard_curador.dominio.narrativo.TransformacaoNarrativa;

/** Episodio que narra uma transformacao (quem fez o que, quando). */
public final class EpisodioDeEvento extends EpisodioNarrativo {

    private final TransformacaoNarrativa transformacao;

    EpisodioDeEvento(TransformacaoNarrativa transformacao) {
        super(exigir(transformacao).getMarcador());
        this.transformacao = transformacao;
    }

    private static TransformacaoNarrativa exigir(TransformacaoNarrativa transformacao) {
        if (transformacao == null) {
            throw new IllegalArgumentException("episodio de evento exige transformacao");
        }
        return transformacao;
    }

    @Override
    public TipoEpisodio getTipo() {
        return TipoEpisodio.EVENTO;
    }

    @Override
    public TransformacaoNarrativa getTransformacao() {
        return transformacao;
    }

    @Override
    public String descrever() {
        return transformacao.descrever();
    }
}
