package br.edu.univasf.gerard_curador.dominio.narrativo;

import java.util.Objects;

/**
 * Significado curado da acao, seus participantes e efeitos nos estados envolvidos.
 *
 * <p>A especificacao e explicita: AcaoNarrativa nao deve ser somente o verbo
 * exibido. Um verbo solto ("deu") nao diz se alguem recebe o que foi dado, nem
 * qual o efeito sobre quem possuia. Esta classe carrega esse significado:
 *
 * <ul>
 *   <li><strong>sentido</strong>: o efeito sobre o inventario de quem possuia;
 *   <li><strong>exigeDestinatario</strong>: se a acao transfere posse a alguem
 *       ou apenas consome. "Dar" exige destinatario; "comer" e "perder" nao;
 *   <li><strong>verbo e particípio</strong>: as formas linguisticas curadas,
 *       usadas pela realizacao textual e pelo roteiro.
 * </ul>
 *
 * <p>A distincao entre transferencia e consumo importa ao dominio, nao apenas ao
 * texto: quando a acao transfere, o destinatario ganha exatamente o que o ator
 * perdeu, e a validacao pode conferir os dois lados. Quando apenas consome
 * (a irma comeu tres morangos, as rosas foram tiradas do buque), nao ha
 * inventario de destino a conferir.
 *
 * <p>As acoes abaixo sao as curadas para as situacoes da disciplina. Nao e uma
 * enumeracao porque o conjunto e aberto: novas situacoes trazem novos verbos sem
 * exigir recompilacao do vocabulario.
 */
public final class AcaoNarrativa {

    /** Preposicao padrao de quem entrega algo a alguem. */
    private static final String PREPOSICAO_DESTINO = "para";

    /** Preposicao padrao de quem recebe algo de alguem. */
    private static final String PREPOSICAO_ORIGEM = "de";

    private final String id;
    private final String verbo;
    private final String participio;
    private final SentidoTransformacao sentido;
    private final boolean exigeDestinatario;
    private final String preposicaoDoDestinatario;

    public AcaoNarrativa(String id,
                         String verbo,
                         String participio,
                         SentidoTransformacao sentido,
                         boolean exigeDestinatario) {
        this(id, verbo, participio, sentido, exigeDestinatario,
                preposicaoPadraoPara(sentido));
    }

    public AcaoNarrativa(String id,
                         String verbo,
                         String participio,
                         SentidoTransformacao sentido,
                         boolean exigeDestinatario,
                         String preposicaoDoDestinatario) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("acao exige id");
        }
        if (verbo == null || verbo.isBlank()) {
            throw new IllegalArgumentException("acao exige verbo curado");
        }
        if (participio == null || participio.isBlank()) {
            throw new IllegalArgumentException("acao exige participio curado");
        }
        if (sentido == null) {
            throw new IllegalArgumentException("acao exige sentido");
        }
        if (preposicaoDoDestinatario == null || preposicaoDoDestinatario.isBlank()) {
            throw new IllegalArgumentException("acao exige preposicao do destinatario");
        }
        this.id = id;
        this.verbo = verbo;
        this.participio = participio;
        this.sentido = sentido;
        this.exigeDestinatario = exigeDestinatario;
        this.preposicaoDoDestinatario = preposicaoDoDestinatario;
    }

    /**
     * Preposicao coerente com o sentido, quando o curador nao a informa.
     *
     * <p>Quem perde algo entrega "para" alguem; quem recebe, recebe "de" alguem.
     * A regra vale para os verbos das situacoes curadas, e o construtor completo
     * permite sobrepo-la quando um verbo exigir outra regencia.
     */
    private static String preposicaoPadraoPara(SentidoTransformacao sentido) {
        return sentido == SentidoTransformacao.RETIRADA
                ? PREPOSICAO_DESTINO
                : PREPOSICAO_ORIGEM;
    }

    /** Transferencia: o que sai de um participante entra em outro. */
    public static AcaoNarrativa transferencia(String id,
                                              String verbo,
                                              String participio,
                                              SentidoTransformacao sentido) {
        return new AcaoNarrativa(id, verbo, participio, sentido, true);
    }

    /** Transferencia com regencia curada explicitamente. */
    public static AcaoNarrativa transferencia(String id,
                                              String verbo,
                                              String participio,
                                              SentidoTransformacao sentido,
                                              String preposicaoDoDestinatario) {
        return new AcaoNarrativa(
                id, verbo, participio, sentido, true, preposicaoDoDestinatario);
    }

    /** Consumo ou aparecimento: altera um inventario sem contraparte. */
    public static AcaoNarrativa semDestinatario(String id,
                                                String verbo,
                                                String participio,
                                                SentidoTransformacao sentido) {
        return new AcaoNarrativa(id, verbo, participio, sentido, false);
    }

    public String getId() {
        return id;
    }

    public String getVerbo() {
        return verbo;
    }

    public String getParticipio() {
        return participio;
    }

    public SentidoTransformacao getSentido() {
        return sentido;
    }

    public boolean exigeDestinatario() {
        return exigeDestinatario;
    }

    /**
     * Preposicao que liga o verbo ao destinatario.
     *
     * <p>Mora aqui, e nao na transformacao, porque a regencia e propriedade do
     * verbo: "deu para a netinha", mas "ganhou do tio". Fixar "para" na
     * transformacao produziria "ganhou 12 figurinhas para o tio", que inverte o
     * sentido da frase.
     */
    public String getPreposicaoDoDestinatario() {
        return preposicaoDoDestinatario;
    }

    public boolean ehTransferencia() {
        return exigeDestinatario;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AcaoNarrativa)) {
            return false;
        }
        return id.equals(((AcaoNarrativa) obj).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return verbo;
    }
}
