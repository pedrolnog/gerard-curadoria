package br.edu.univasf.gerard_curador.dominio.situacao;

import br.edu.univasf.gerard_curador.dominio.narrativo.EstadoDePosse;
import br.edu.univasf.gerard_curador.dominio.narrativo.MarcadorTemporal;
import br.edu.univasf.gerard_curador.dominio.narrativo.TransformacaoNarrativa;
import br.edu.univasf.gerard_curador.dominio.estrutural.IncognitaQuantitativa;

/**
 * Um momento da sequencia narrativa: um estado, um evento ou a pergunta.
 *
 * <p>Peca central da fronteira entre dominio e representacao. O episodio conserva
 * <strong>referencias aos objetos do dominio</strong> (o estado de posse, a
 * transformacao, a incognita), e nao um texto ja formatado. E o que atende a
 * exigencia "a sequencia conserva referencias a participantes, objetos e
 * numeros".
 *
 * <p>A consequencia pratica e o caso de teste 12: trocar o renderizador textual
 * pelo de quadrinhos nao altera o dominio, porque cada renderizador recebe os
 * mesmos objetos e decide sozinho como apresenta-los. Um roteiro pode escrever
 * "Vovo possui 2 rosas brancas e 3 amarelas" enquanto outro desenha cinco rosas
 * e escreve so o nome da personagem; ambos leem o mesmo EstadoDePosse.
 *
 * <p>O metodo descrever() existe como conveniencia de depuracao e para a
 * realizacao textual mais simples. Ele delega a descricao aos objetos que detem
 * o conhecimento, sem formatar nada por conta propria.
 */
public final class EpisodioNarrativo {

    private final TipoEpisodio tipo;
    private final MarcadorTemporal momento;
    private final EstadoDePosse estado;
    private final TransformacaoNarrativa transformacao;
    private final IncognitaQuantitativa incognita;

    private EpisodioNarrativo(TipoEpisodio tipo,
                              MarcadorTemporal momento,
                              EstadoDePosse estado,
                              TransformacaoNarrativa transformacao,
                              IncognitaQuantitativa incognita) {
        this.tipo = tipo;
        this.momento = momento;
        this.estado = estado;
        this.transformacao = transformacao;
        this.incognita = incognita;
    }

    public static EpisodioNarrativo deEstado(EstadoDePosse estado) {
        if (estado == null) {
            throw new IllegalArgumentException("episodio de estado exige estado de posse");
        }
        return new EpisodioNarrativo(
                TipoEpisodio.ESTADO, estado.getMomento(), estado, null, null);
    }

    public static EpisodioNarrativo deEvento(TransformacaoNarrativa transformacao) {
        if (transformacao == null) {
            throw new IllegalArgumentException("episodio de evento exige transformacao");
        }
        return new EpisodioNarrativo(
                TipoEpisodio.EVENTO, transformacao.getMarcador(), null, transformacao, null);
    }

    public static EpisodioNarrativo dePergunta(IncognitaQuantitativa incognita,
                                               MarcadorTemporal momento) {
        if (incognita == null) {
            throw new IllegalArgumentException("episodio de pergunta exige incognita");
        }
        return new EpisodioNarrativo(
                TipoEpisodio.PERGUNTA, momento, null, null, incognita);
    }

    public TipoEpisodio getTipo() {
        return tipo;
    }

    public MarcadorTemporal getMomento() {
        return momento;
    }

    /** Estado de posse referido, quando o episodio e do tipo ESTADO. */
    public EstadoDePosse getEstado() {
        return estado;
    }

    /** Transformacao referida, quando o episodio e do tipo EVENTO. */
    public TransformacaoNarrativa getTransformacao() {
        return transformacao;
    }

    /** Incognita referida, quando o episodio e do tipo PERGUNTA. */
    public IncognitaQuantitativa getIncognita() {
        return incognita;
    }

    public boolean ehEstado() {
        return tipo == TipoEpisodio.ESTADO;
    }

    public boolean ehEvento() {
        return tipo == TipoEpisodio.EVENTO;
    }

    public boolean ehPergunta() {
        return tipo == TipoEpisodio.PERGUNTA;
    }

    /** Descricao delegada ao objeto de dominio que detem o conhecimento. */
    public String descrever() {
        switch (tipo) {
            case ESTADO:
                return estado.descrever();
            case EVENTO:
                return transformacao.descrever();
            case PERGUNTA:
                return incognita.descrever();
            default:
                return "";
        }
    }

    /** "ESTADO: Vovo possui 2 rosas brancas e 3 rosas amarelas." */
    public String descreverComRotulo() {
        return tipo.getRotulo() + ": " + descrever();
    }

    @Override
    public String toString() {
        return descreverComRotulo();
    }
}
