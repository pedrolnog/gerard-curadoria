package br.edu.univasf.gerard_curador.dominio.situacao;

import br.edu.univasf.gerard_curador.dominio.estrutural.IncognitaQuantitativa;
import br.edu.univasf.gerard_curador.dominio.narrativo.EstadoDePosse;
import br.edu.univasf.gerard_curador.dominio.narrativo.MarcadorTemporal;
import br.edu.univasf.gerard_curador.dominio.narrativo.TransformacaoNarrativa;

/**
 * Um momento da sequencia narrativa: um estado, um evento ou a pergunta final.
 *
 * <p>Classe abstrata e {@code sealed}: so as tres subclasses permitidas existem,
 * e cada uma guarda <em>apenas</em> o dado que lhe pertence ({@link EpisodioDeEstado}
 * guarda o estado, {@link EpisodioDeEvento} a transformacao, {@link EpisodioDePergunta}
 * a incognita). Nao ha campos nulos "sobrando" nem {@code switch} sobre o tipo:
 * {@link #descrever()} e resolvido por ligacao dinamica.
 *
 * <p>Os acessores especificos tem aqui um comportamento padrao que recusa o
 * pedido; so a subclasse dona do dado os sobrescreve. Pedir o estado de um evento
 * e um erro de programacao e falha cedo, em vez de devolver null.
 */
public abstract sealed class EpisodioNarrativo
        permits EpisodioDeEstado, EpisodioDeEvento, EpisodioDePergunta {

    private final MarcadorTemporal momento;

    protected EpisodioNarrativo(MarcadorTemporal momento) {
        this.momento = momento;
    }

    public static EpisodioNarrativo deEstado(EstadoDePosse estado) {
        return new EpisodioDeEstado(estado);
    }

    public static EpisodioNarrativo deEvento(TransformacaoNarrativa transformacao) {
        return new EpisodioDeEvento(transformacao);
    }

    public static EpisodioNarrativo dePergunta(IncognitaQuantitativa incognita, MarcadorTemporal momento) {
        return new EpisodioDePergunta(incognita, momento);
    }

    /** Tipo do episodio, para relatorios e rotulos. */
    public abstract TipoEpisodio getTipo();

    /** Texto do episodio na linguagem da historia (polimorfico). */
    public abstract String descrever();

    public MarcadorTemporal getMomento() {
        return momento;
    }

    public EstadoDePosse getEstado() {
        throw new IllegalStateException("episodio de " + getTipo().getRotulo() + " nao possui estado");
    }

    public TransformacaoNarrativa getTransformacao() {
        throw new IllegalStateException("episodio de " + getTipo().getRotulo() + " nao possui transformacao");
    }

    public IncognitaQuantitativa getIncognita() {
        throw new IllegalStateException("episodio de " + getTipo().getRotulo() + " nao possui incognita");
    }

    public boolean ehEstado() {
        return getTipo() == TipoEpisodio.ESTADO;
    }

    public boolean ehEvento() {
        return getTipo() == TipoEpisodio.EVENTO;
    }

    public boolean ehPergunta() {
        return getTipo() == TipoEpisodio.PERGUNTA;
    }

    public String descreverComRotulo() {
        return getTipo().getRotulo() + ": " + descrever();
    }

    @Override
    public String toString() {
        return descreverComRotulo();
    }
}
