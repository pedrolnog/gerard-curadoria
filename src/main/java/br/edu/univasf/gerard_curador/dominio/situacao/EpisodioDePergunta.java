package br.edu.univasf.gerard_curador.dominio.situacao;

import br.edu.univasf.gerard_curador.dominio.estrutural.IncognitaQuantitativa;
import br.edu.univasf.gerard_curador.dominio.narrativo.MarcadorTemporal;

/** Episodio final que formula a pergunta sobre a incognita. */
public final class EpisodioDePergunta extends EpisodioNarrativo {

    private final IncognitaQuantitativa incognita;

    EpisodioDePergunta(IncognitaQuantitativa incognita, MarcadorTemporal momento) {
        super(momento);
        if (incognita == null) {
            throw new IllegalArgumentException("episodio de pergunta exige incognita");
        }
        this.incognita = incognita;
    }

    @Override
    public TipoEpisodio getTipo() {
        return TipoEpisodio.PERGUNTA;
    }

    @Override
    public IncognitaQuantitativa getIncognita() {
        return incognita;
    }

    @Override
    public String descrever() {
        return incognita.descrever();
    }
}
