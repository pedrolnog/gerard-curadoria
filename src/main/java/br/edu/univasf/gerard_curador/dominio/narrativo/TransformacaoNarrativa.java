package br.edu.univasf.gerard_curador.dominio.narrativo;

import br.edu.univasf.gerard_curador.dominio.quantitativo.Quantidade;

/**
 * Mudanca ordenada entre estados; referencia acao, participantes e quantidade.
 *
 * <p>Esta e a classe onde "ator, destinatario e objeto sao referencias
 * explicitas" se realiza. Nenhum papel e inferido pela posicao de um campo: o
 * curador aponta objetos concretos, e a transformacao os guarda como referencias,
 * nao como textos.
 *
 * <p>Compare com o formulario plano da interface Swing original, onde
 * "personagem_1", "personagem_2" e "personagem_3" sao caixas de texto cuja
 * relacao com a operacao depende inteiramente da posicao na tela. Ali, quem e o
 * ator e quem e o destinatario e uma convencao nao escrita; aqui, e uma
 * associacao declarada.
 *
 * <p>Invariantes protegidas:
 * <ul>
 *   <li>toda transformacao tem ator, acao, quantidade e marcador temporal;
 *   <li>uma acao que exige destinatario nao pode ficar sem ele (a validacao
 *       estrutural relata; o construtor recusa);
 *   <li>ator e destinatario devem ser participantes distintos: ninguem da rosas
 *       a si mesmo.
 * </ul>
 *
 * <p>A transformacao <em>aplica</em> seu efeito sobre um estado, mas nao decide
 * se a sequencia inteira e coerente: isso e da situacao e da relacao estrutural.
 */
public final class TransformacaoNarrativa {

    private final ParticipanteNarrativo ator;
    private final AcaoNarrativa acao;
    private final ParticipanteNarrativo destinatario;
    private final Quantidade quantidade;
    private final MarcadorTemporal marcador;

    public TransformacaoNarrativa(ParticipanteNarrativo ator,
                                  AcaoNarrativa acao,
                                  ParticipanteNarrativo destinatario,
                                  Quantidade quantidade,
                                  MarcadorTemporal marcador) {
        if (ator == null) {
            throw new IllegalArgumentException("transformacao exige ator");
        }
        if (acao == null) {
            throw new IllegalArgumentException("transformacao exige acao");
        }
        if (quantidade == null) {
            throw new IllegalArgumentException("transformacao exige quantidade");
        }
        if (marcador == null) {
            throw new IllegalArgumentException("transformacao exige marcador temporal");
        }
        if (acao.exigeDestinatario() && destinatario == null) {
            throw new IllegalArgumentException(
                    "acao " + acao.getId() + " exige destinatario explicito");
        }
        if (destinatario != null && destinatario.equals(ator)) {
            throw new IllegalArgumentException(
                    "ator e destinatario nao podem ser o mesmo participante: " + ator.getId());
        }
        this.ator = ator;
        this.acao = acao;
        this.destinatario = destinatario;
        this.quantidade = quantidade;
        this.marcador = marcador;
    }

    /** Transformacao sem contraparte: consumo, perda ou aparecimento. */
    public static TransformacaoNarrativa semDestinatario(ParticipanteNarrativo ator,
                                                         AcaoNarrativa acao,
                                                         Quantidade quantidade,
                                                         MarcadorTemporal marcador) {
        return new TransformacaoNarrativa(ator, acao, null, quantidade, marcador);
    }

    public ParticipanteNarrativo getAtor() {
        return ator;
    }

    public AcaoNarrativa getAcao() {
        return acao;
    }

    public ParticipanteNarrativo getDestinatario() {
        return destinatario;
    }

    public Quantidade getQuantidade() {
        return quantidade;
    }

    public MarcadorTemporal getMarcador() {
        return marcador;
    }

    public int getOrdem() {
        return marcador.getOrdem();
    }

    /** O sentido vem da acao curada, nao do sinal de um numero. */
    public SentidoTransformacao getSentido() {
        return acao.getSentido();
    }

    public boolean possuiDestinatario() {
        return destinatario != null;
    }

    public boolean ehTransferencia() {
        return acao.ehTransferencia() && destinatario != null;
    }

    /**
     * Aplica o efeito desta transformacao sobre o estado de quem a sofre.
     *
     * <p>O sentido da acao decide a direcao: acrescimo adiciona, retirada
     * subtrai. A excecao de retirada impossivel sobe do inventario sem que
     * nenhum estado intermediario invalido seja criado.
     */
    public EstadoDePosse aplicarSobre(EstadoDePosse estado) {
        if (estado == null) {
            throw new IllegalArgumentException("aplicacao exige estado de origem");
        }
        return getSentido().aplicar(estado, quantidade, marcador);
    }

    /**
     * Aplica o efeito espelhado no destinatario de uma transferencia.
     *
     * <p>Quando a Vovo da uma rosa, a netinha recebe exatamente essa rosa: o
     * sentido se inverte do lado de quem recebe. Permite acompanhar os dois
     * inventarios quando a situacao curada declara o estado da destinataria.
     */
    public EstadoDePosse aplicarSobreDestinatario(EstadoDePosse estadoDestinatario) {
        if (!ehTransferencia()) {
            throw new IllegalStateException(
                    "transformacao sem destinatario nao possui efeito espelhado");
        }
        if (estadoDestinatario == null) {
            throw new IllegalArgumentException("aplicacao exige estado do destinatario");
        }
        return getSentido().inverter().aplicar(estadoDestinatario, quantidade, marcador);
    }

    /** Consulta previa, para a validacao relatar sem interromper. */
    public boolean podeSerAplicadaSobre(EstadoDePosse estado) {
        if (estado == null) {
            return false;
        }
        return getSentido().podeSerAplicado(estado, quantidade);
    }

    public boolean ocupaMesmaOrdemQue(TransformacaoNarrativa outra) {
        return outra != null && marcador.ocupaMesmaOrdemQue(outra.marcador);
    }

    /** "Vovo deu 1 rosa branca para Netinha". */
    public String descrever() {
        StringBuilder texto = new StringBuilder();
        texto.append(ator.getNomeExibido())
                .append(" ")
                .append(acao.getVerbo())
                .append(" ")
                .append(quantidade.descrever());
        if (destinatario != null) {
            // A preposicao vem da acao: "deu para a netinha", "ganhou do tio".
            texto.append(" ")
                    .append(acao.getPreposicaoDoDestinatario())
                    .append(" ")
                    .append(destinatario.getNomeExibido());
        }
        return texto.toString();
    }

    @Override
    public String toString() {
        return marcador.getRotulo() + ": " + descrever();
    }
}
