package br.edu.univasf.gerard_curador.app;

import br.edu.univasf.gerard_curador.dominio.narrativo.AcaoNarrativa;
import br.edu.univasf.gerard_curador.dominio.narrativo.ContextoNarrativo;
import br.edu.univasf.gerard_curador.dominio.narrativo.EstadoDePosse;
import br.edu.univasf.gerard_curador.dominio.narrativo.MarcadorTemporal;
import br.edu.univasf.gerard_curador.dominio.narrativo.ParticipanteNarrativo;
import br.edu.univasf.gerard_curador.dominio.narrativo.ReferenciaVisualParticipante;
import br.edu.univasf.gerard_curador.dominio.narrativo.SentidoTransformacao;
import br.edu.univasf.gerard_curador.dominio.narrativo.TransformacaoNarrativa;
import br.edu.univasf.gerard_curador.dominio.quantitativo.CaracteristicaObjeto;
import br.edu.univasf.gerard_curador.dominio.quantitativo.FamiliaObjeto;
import br.edu.univasf.gerard_curador.dominio.narrativo.Inventario;
import br.edu.univasf.gerard_curador.dominio.quantitativo.ObjetoContado;
import br.edu.univasf.gerard_curador.dominio.quantitativo.RealizacaoTextualObjeto;
import br.edu.univasf.gerard_curador.dominio.quantitativo.ReferenciaVisual;
import br.edu.univasf.gerard_curador.dominio.estrutural.ComposicaoDeTransformacoes;
import br.edu.univasf.gerard_curador.dominio.estrutural.IncognitaQuantitativa;
import br.edu.univasf.gerard_curador.dominio.estrutural.PapelQuantitativo;
import br.edu.univasf.gerard_curador.dominio.situacao.CatalogoDaSituacao;
import br.edu.univasf.gerard_curador.dominio.situacao.SituacaoProblema;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Montagem das tres situacoes curadas para a disciplina.
 *
 * <p>Toda associacao aqui e <strong>explicita</strong>: cada participante, objeto
 * contado, quantidade e ordem temporal e declarado por nome, nunca deduzido do
 * enunciado nem da posicao de um campo. Esta classe representa o trabalho do
 * curador humano, que a especificacao exige que permaneca humano.
 *
 * <p>As tres situacoes demonstram a generalizacao do mesmo nucleo de classes:
 * <ul>
 *   <li><strong>rosas-01</strong>: composicao de duas transformacoes, com o total
 *       do estado final como incognita;
 *   <li><strong>figurinhas-lucas-01</strong>: transformacao unica com a incognita
 *       no <em>estado inicial</em>;
 *   <li><strong>bilas-joao-01</strong>: duas transformacoes em partidas
 *       ordenadas, com a incognita na <em>primeira transformacao</em>.
 * </ul>
 *
 * <p>Nenhuma delas exigiu campo ou classe nova: mudam os dados, nao o modelo.
 */
public final class SituacoesCuradas {

    private SituacoesCuradas() {
    }

    // ------------------------------------------------------------------
    // Situacao 1: as rosas da Vovo
    // ------------------------------------------------------------------

    /**
     * "Vovo possui 2 rosas brancas e 3 rosas amarelas. Deu 1 rosa branca e 1 rosa
     * amarela para a sua netinha. Com quantas rosas vovo ficou?"
     *
     * <p>Rosa branca e rosa amarela sao objetos contados distintos da mesma
     * familia rosa, distinguidos por uma caracteristica de cor, e nao por
     * subclasses. O inventario inicial totaliza cinco rosas; o final, tres.
     */
    public static SituacaoProblema rosas() {
        FamiliaObjeto rosa = new FamiliaObjeto("rosa", "rosa");

        ObjetoContado rosaBranca = new ObjetoContado(
                "rosa_branca",
                rosa,
                caracteristicas(CaracteristicaObjeto.de("cor", "branca")),
                RealizacaoTextualObjeto.emPortugues("rosa branca", "rosas brancas"),
                ReferenciaVisual.de("rosa_branca"));

        ObjetoContado rosaAmarela = new ObjetoContado(
                "rosa_amarela",
                rosa,
                caracteristicas(CaracteristicaObjeto.de("cor", "amarela")),
                RealizacaoTextualObjeto.emPortugues("rosa amarela", "rosas amarelas"),
                ReferenciaVisual.de("rosa_amarela"));

        ParticipanteNarrativo vovo = new ParticipanteNarrativo(
                "vovo", "Vovo", ReferenciaVisualParticipante.de("vovo"));
        ParticipanteNarrativo netinha = new ParticipanteNarrativo(
                "netinha", "Netinha", ReferenciaVisualParticipante.de("netinha"));

        CatalogoDaSituacao catalogo = CatalogoDaSituacao.construtor()
                .comParticipante(vovo)
                .comParticipante(netinha)
                .comObjeto(rosaBranca)
                .comObjeto(rosaAmarela)
                .construir();

        // Estado inicial detalhado por tipo de objeto: preserva a composicao do
        // total, e nao apenas o numero 5.
        EstadoDePosse estadoInicial = new EstadoDePosse(
                vovo,
                Inventario.com(rosaBranca.contar(2), rosaAmarela.contar(3)),
                MarcadorTemporal.de(1, "no comeco"));

        AcaoNarrativa dar = AcaoNarrativa.transferencia(
                "dar", "deu", "dada", SentidoTransformacao.RETIRADA);

        TransformacaoNarrativa primeira = new TransformacaoNarrativa(
                vovo, dar, netinha, rosaBranca.contar(1),
                MarcadorTemporal.de(2, "primeira entrega"));

        TransformacaoNarrativa segunda = new TransformacaoNarrativa(
                vovo, dar, netinha, rosaAmarela.contar(1),
                MarcadorTemporal.de(3, "segunda entrega"));

        EstadoDePosse estadoFinalCurado = new EstadoDePosse(
                vovo,
                Inventario.com(rosaBranca.contar(1), rosaAmarela.contar(2)),
                MarcadorTemporal.de(4, "no fim"));

        // A relacao estrutural coordena os papeis pelos totais da familia rosa:
        // 5 - 1 - 1 = 3.
        ComposicaoDeTransformacoes relacao = ComposicaoDeTransformacoes.construtor("rosas-relacao")
                .comEstadoInicial(rosaBranca.contar(5))
                .comPrimeiraTransformacao(rosaBranca.contar(1), SentidoTransformacao.RETIRADA)
                .comSegundaTransformacao(rosaBranca.contar(1), SentidoTransformacao.RETIRADA)
                .comEstadoFinal(rosaBranca.contar(3))
                .construir();

        return SituacaoProblema.construtor(
                        "rosas-01",
                        "Vovo possui 2 rosas brancas e 3 rosas amarelas. Deu 1 rosa branca e "
                                + "1 rosa amarela para a sua netinha. Com quantas rosas vovo ficou?")
                .comCatalogo(catalogo)
                .comEstadoInicial(estadoInicial)
                .comTransformacao(primeira)
                .comTransformacao(segunda)
                .comEstadoFinalCurado(estadoFinalCurado)
                .comIncognita(IncognitaQuantitativa.designar(
                        PapelQuantitativo.ESTADO_FINAL,
                        "Com quantas rosas vovo ficou?"))
                .comRelacaoEstrutural(relacao)
                .comContexto(new ContextoNarrativo(null, "colecao de flores", null))
                .construir();
    }

    // ------------------------------------------------------------------
    // Situacao 2: as figurinhas do Lucas (incognita no estado inicial)
    // ------------------------------------------------------------------

    /**
     * "Lucas ganhou 12 figurinhas de seu tio. Ele possui agora 17 figurinhas.
     * Quantas figurinhas ele tinha antes?"
     *
     * <p>Demonstra que a incognita pode ocupar o estado inicial sem mudanca
     * alguma no modelo. Depois de resolvida (17 - 12 = 5), a incognita continua
     * declarando que o desconhecido era o ESTADO_INICIAL: e o caso de teste 13.
     *
     * <p>O estado inicial e curado como 5 porque o curador conhece a resposta;
     * o que a incognita registra e que <em>esse</em> era o papel perguntado ao
     * aluno.
     */
    public static SituacaoProblema figurinhasComEstadoInicialDesconhecido() {
        FamiliaObjeto figurinha = new FamiliaObjeto("figurinha", "figurinha");

        ObjetoContado figurinhaComum = new ObjetoContado(
                "figurinha_comum",
                figurinha,
                caracteristicas(CaracteristicaObjeto.de("colecao", "album do tio")),
                RealizacaoTextualObjeto.emPortugues("figurinha", "figurinhas"),
                ReferenciaVisual.de("figurinha"));

        ParticipanteNarrativo lucas = new ParticipanteNarrativo(
                "lucas", "Lucas", ReferenciaVisualParticipante.de("lucas"));
        ParticipanteNarrativo tio = new ParticipanteNarrativo(
                "tio", "Tio", ReferenciaVisualParticipante.de("tio"));

        CatalogoDaSituacao catalogo = CatalogoDaSituacao.construtor()
                .comParticipante(lucas)
                .comParticipante(tio)
                .comObjeto(figurinhaComum)
                .construir();

        EstadoDePosse estadoInicial = new EstadoDePosse(
                lucas,
                Inventario.com(figurinhaComum.contar(5)),
                MarcadorTemporal.de(1, "antes"));

        // Ganhar e transferencia com acrescimo para o ator: o tio e a origem,
        // registrada como destinatario da relacao inversa.
        AcaoNarrativa ganhar = AcaoNarrativa.transferencia(
                "ganhar", "ganhou", "ganha", SentidoTransformacao.ACRESCIMO);

        TransformacaoNarrativa presente = new TransformacaoNarrativa(
                lucas, ganhar, tio, figurinhaComum.contar(12),
                MarcadorTemporal.de(2, "quando o tio deu"));

        EstadoDePosse estadoFinalCurado = new EstadoDePosse(
                lucas,
                Inventario.com(figurinhaComum.contar(17)),
                MarcadorTemporal.de(3, "agora"));

        ComposicaoDeTransformacoes relacao =
                ComposicaoDeTransformacoes.construtor("figurinhas-relacao")
                        .comEstadoInicial(figurinhaComum.contar(5))
                        .comPrimeiraTransformacao(
                                figurinhaComum.contar(12), SentidoTransformacao.ACRESCIMO)
                        .comEstadoFinal(figurinhaComum.contar(17))
                        .construir();

        return SituacaoProblema.construtor(
                        "figurinhas-lucas-01",
                        "Lucas ganhou 12 figurinhas de seu tio. Ele possui agora 17 figurinhas. "
                                + "Quantas figurinhas ele tinha antes de ganhar as figurinhas do tio?")
                .comCatalogo(catalogo)
                .comEstadoInicial(estadoInicial)
                .comTransformacao(presente)
                .comEstadoFinalCurado(estadoFinalCurado)
                .comIncognita(IncognitaQuantitativa.designar(
                        PapelQuantitativo.ESTADO_INICIAL,
                        "Quantas figurinhas Lucas tinha antes?"))
                .comRelacaoEstrutural(relacao)
                .comContexto(new ContextoNarrativo(null, "colecao de figurinhas", null))
                .construir();
    }

    // ------------------------------------------------------------------
    // Situacao 3: as bilas do Joao (duas transformacoes ordenadas)
    // ------------------------------------------------------------------

    /**
     * "Joao jogou duas partidas de bila. Na segunda ele ganhou 12 bolas. Ao final
     * das duas partidas ele tinha ganhado 22. Quantas bilas ele ganhou na
     * primeira partida?"
     *
     * <p>Exercita tres pontos de uma vez. O primeiro e a ordem temporal curada: o
     * enunciado menciona a segunda partida antes da primeira, e o modelo preserva
     * a ordem real dos eventos (caso de teste 17). O segundo e a transformacao
     * resultante: o saldo 22 e conferido contra a soma das parcelas 10 + 12. O
     * terceiro e a incognita numa transformacao, e nao num estado.
     *
     * <p>Note que nao ha estado inicial no enunciado: quantas bilas Joao tinha
     * antes de jogar e informacao que o problema nao fornece. O curador declara
     * zero como ponto de partida da <em>contagem de ganhos</em>, e o contexto
     * narrativo registra que se trata de um jogo.
     */
    public static SituacaoProblema bilasComDuasPartidas() {
        FamiliaObjeto bila = new FamiliaObjeto("bila", "bila");

        ObjetoContado bilaDeVidro = new ObjetoContado(
                "bila_vidro",
                bila,
                caracteristicas(CaracteristicaObjeto.de("material", "vidro")),
                RealizacaoTextualObjeto.emPortugues("bila", "bilas"),
                ReferenciaVisual.de("bila"));

        ParticipanteNarrativo joao = new ParticipanteNarrativo(
                "joao", "Joao", ReferenciaVisualParticipante.de("joao"));
        ParticipanteNarrativo adversario = new ParticipanteNarrativo(
                "adversario", "Adversario", ReferenciaVisualParticipante.de("adversario"));

        CatalogoDaSituacao catalogo = CatalogoDaSituacao.construtor()
                .comParticipante(joao)
                .comParticipante(adversario)
                .comObjeto(bilaDeVidro)
                .construir();

        EstadoDePosse estadoInicial = new EstadoDePosse(
                joao,
                Inventario.com(bilaDeVidro.contar(0)),
                MarcadorTemporal.de(1, "antes das partidas"));

        AcaoNarrativa ganhar = AcaoNarrativa.transferencia(
                "ganhar", "ganhou", "ganha", SentidoTransformacao.ACRESCIMO);

        // A ordem vem do marcador curado, nao da ordem de declaracao: a primeira
        // partida e declarada aqui, ainda que o enunciado cite a segunda antes.
        TransformacaoNarrativa primeiraPartida = new TransformacaoNarrativa(
                joao, ganhar, adversario, bilaDeVidro.contar(10),
                MarcadorTemporal.de(2, "primeira partida"));

        TransformacaoNarrativa segundaPartida = new TransformacaoNarrativa(
                joao, ganhar, adversario, bilaDeVidro.contar(12),
                MarcadorTemporal.de(3, "segunda partida"));

        EstadoDePosse estadoFinalCurado = new EstadoDePosse(
                joao,
                Inventario.com(bilaDeVidro.contar(22)),
                MarcadorTemporal.de(4, "ao final das duas partidas"));

        ComposicaoDeTransformacoes relacao =
                ComposicaoDeTransformacoes.construtor("bilas-relacao")
                        .comEstadoInicial(bilaDeVidro.contar(0))
                        .comPrimeiraTransformacao(
                                bilaDeVidro.contar(10), SentidoTransformacao.ACRESCIMO)
                        .comSegundaTransformacao(
                                bilaDeVidro.contar(12), SentidoTransformacao.ACRESCIMO)
                        .comTransformacaoResultante(
                                bilaDeVidro.contar(22), SentidoTransformacao.ACRESCIMO)
                        .comEstadoFinal(bilaDeVidro.contar(22))
                        .construir();

        return SituacaoProblema.construtor(
                        "bilas-joao-01",
                        "Joao jogou duas partidas de bila. Na segunda ele ganhou 12 bolas. "
                                + "Ao final das duas partidas ele tinha ganhado 22. "
                                + "Quantas bilas ele ganhou na primeira partida?")
                .comCatalogo(catalogo)
                .comEstadoInicial(estadoInicial)
                .comTransformacao(segundaPartida)
                .comTransformacao(primeiraPartida)
                .comEstadoFinalCurado(estadoFinalCurado)
                .comIncognita(IncognitaQuantitativa.designar(
                        PapelQuantitativo.TRANSFORMACAO_PRIMEIRA,
                        "Quantas bilas Joao ganhou na primeira partida?"))
                .comRelacaoEstrutural(relacao)
                .comContexto(new ContextoNarrativo(null, "jogo de bila", null))
                .construir();
    }

    private static Set<CaracteristicaObjeto> caracteristicas(CaracteristicaObjeto... itens) {
        Set<CaracteristicaObjeto> conjunto = new LinkedHashSet<>();
        for (CaracteristicaObjeto item : itens) {
            conjunto.add(item);
        }
        return conjunto;
    }
}
