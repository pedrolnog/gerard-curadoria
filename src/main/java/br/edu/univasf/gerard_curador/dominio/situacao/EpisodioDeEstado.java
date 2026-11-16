package br.edu.univasf.gerard_curador.dominio.situacao;

import br.edu.univasf.gerard_curador.dominio.narrativo.EstadoDePosse;

/** Episodio que mostra a posse de um participante num momento. */
public final class EpisodioDeEstado extends EpisodioNarrativo {

    private final EstadoDePosse estado;

    EpisodioDeEstado(EstadoDePosse estado) {
        super(exigir(estado).getMomento());
        this.estado = estado;
    }

    private static EstadoDePosse exigir(EstadoDePosse estado) {
        if (estado == null) {
            throw new IllegalArgumentException("episodio de estado exige estado de posse");
        }
        return estado;
    }

    @Override
    public TipoEpisodio getTipo() {
        return TipoEpisodio.ESTADO;
    }

    @Override
    public EstadoDePosse getEstado() {
        return estado;
    }

    @Override
    public String descrever() {
        return estado.descrever();
    }
}
