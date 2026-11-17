package br.edu.univasf.gerard_curador.persistencia;

import br.edu.univasf.gerard_curador.dominio.estrutural.ComposicaoDeTransformacoes;
import br.edu.univasf.gerard_curador.dominio.estrutural.IncognitaQuantitativa;
import br.edu.univasf.gerard_curador.dominio.estrutural.PapelQuantitativo;
import br.edu.univasf.gerard_curador.dominio.narrativo.AcaoNarrativa;
import br.edu.univasf.gerard_curador.dominio.narrativo.ContextoNarrativo;
import br.edu.univasf.gerard_curador.dominio.narrativo.EstadoDePosse;
import br.edu.univasf.gerard_curador.dominio.narrativo.Inventario;
import br.edu.univasf.gerard_curador.dominio.narrativo.MarcadorTemporal;
import br.edu.univasf.gerard_curador.dominio.narrativo.ParticipanteNarrativo;
import br.edu.univasf.gerard_curador.dominio.narrativo.SentidoTransformacao;
import br.edu.univasf.gerard_curador.dominio.narrativo.TransformacaoNarrativa;
import br.edu.univasf.gerard_curador.dominio.quantitativo.CaracteristicaObjeto;
import br.edu.univasf.gerard_curador.dominio.quantitativo.FamiliaObjeto;
import br.edu.univasf.gerard_curador.dominio.quantitativo.ObjetoContado;
import br.edu.univasf.gerard_curador.dominio.quantitativo.RealizacaoTextualObjeto;
import br.edu.univasf.gerard_curador.dominio.quantitativo.ReferenciaVisual;
import br.edu.univasf.gerard_curador.dominio.situacao.CatalogoDaSituacao;
import br.edu.univasf.gerard_curador.dominio.situacao.SituacaoProblema;

import java.util.Set;

/**
 * Montagens auxiliares para os testes da camada de persistencia.
 *
 * <p>Copia a montagem canonica da situacao das rosas de SituacoesDeTeste
 * (package-private no pacote de dominio) e acrescenta variantes rotulada
 * fora do escopo, com ambiguidade declarada e com estado final divergente.
 */
class SituacoesParaPersistencia {

    static final ParticipanteNarrativo VOVO = new ParticipanteNarrativo("vovo", "Vovó");
    static final ParticipanteNarrativo NETINHA = new ParticipanteNarrativo("netinha", "Netinha");

    static final FamiliaObjeto FAMILIA_ROSA = new FamiliaObjeto("rosa", "Rosa");

    static final ObjetoContado ROSA_BRANCA = new ObjetoContado(
            "rosa_branca",
            FAMILIA_ROSA,
            Set.of(CaracteristicaObjeto.de("cor", "branca")),
            RealizacaoTextualObjeto.emPortugues("rosa branca", "rosas brancas"),
            ReferenciaVisual.de("rosa_branca")
    );

    static final ObjetoContado ROSA_AMARELA = new ObjetoContado(
            "rosa_amarela",
            FAMILIA_ROSA,
            Set.of(CaracteristicaObjeto.de("cor", "amarela")),
            RealizacaoTextualObjeto.emPortugues("rosa amarela", "rosas amarelas"),
            ReferenciaVisual.de("rosa_amarela")
    );

    static final AcaoNarrativa ACAO_DAR = AcaoNarrativa.transferencia(
            "dar", "deu", "dado", SentidoTransformacao.RETIRADA
    );

    private SituacoesParaPersistencia() {
    }

    static CatalogoDaSituacao catalogoDasRosas() {
        return CatalogoDaSituacao.construtor()
                .comParticipante(VOVO)
                .comParticipante(NETINHA)
                .comObjeto(ROSA_BRANCA)
                .comObjeto(ROSA_AMARELA)
                .construir();
    }

    static EstadoDePosse estadoInicialDasRosas() {
        Inventario inventario = Inventario.com(
                ROSA_BRANCA.contar(2),
                ROSA_AMARELA.contar(3)
        );
        return new EstadoDePosse(VOVO, inventario, MarcadorTemporal.de(1, "inicial"));
    }

    static TransformacaoNarrativa t1DasRosas() {
        return new TransformacaoNarrativa(
                VOVO,
                ACAO_DAR,
                NETINHA,
                ROSA_BRANCA.contar(1),
                MarcadorTemporal.de(1, "primeira doação")
        );
    }

    static TransformacaoNarrativa t2DasRosas() {
        return new TransformacaoNarrativa(
                VOVO,
                ACAO_DAR,
                NETINHA,
                ROSA_AMARELA.contar(1),
                MarcadorTemporal.de(2, "segunda doação")
        );
    }

    static EstadoDePosse estadoFinalCuradoDasRosas() {
        Inventario inventario = Inventario.com(
                ROSA_BRANCA.contar(1),
                ROSA_AMARELA.contar(2)
        );
        return new EstadoDePosse(VOVO, inventario, MarcadorTemporal.de(3, "final"));
    }

    static IncognitaQuantitativa incognitaDasRosas() {
        return IncognitaQuantitativa.designar(
                PapelQuantitativo.ESTADO_FINAL,
                "Com quantas rosas a Vovó ficou?"
        );
    }

    static ComposicaoDeTransformacoes relacaoDasRosas() {
        return ComposicaoDeTransformacoes.construtor("relacao-rosas")
                .comEstadoInicial(ROSA_BRANCA.contar(5))
                .comPrimeiraTransformacao(ROSA_BRANCA.contar(1), SentidoTransformacao.RETIRADA)
                .comSegundaTransformacao(ROSA_BRANCA.contar(1), SentidoTransformacao.RETIRADA)
                .comEstadoFinal(ROSA_BRANCA.contar(3))
                .construir();
    }

    static SituacaoProblema.Construtor construtorSituacaoRosas() {
        return SituacaoProblema.construtor(
                "situacao-rosas",
                "Vovó tinha 2 rosas brancas e 3 amarelas. Deu 1 branca e 1 amarela para a Netinha. Com quantas ficou?"
        )
                .comCatalogo(catalogoDasRosas())
                .comEstadoInicial(estadoInicialDasRosas())
                .comTransformacao(t1DasRosas())
                .comTransformacao(t2DasRosas())
                .comEstadoFinalCurado(estadoFinalCuradoDasRosas())
                .comIncognita(incognitaDasRosas())
                .comRelacaoEstrutural(relacaoDasRosas())
                .comContexto(new ContextoNarrativo("jardim", "presente", "hoje"));
    }

    static SituacaoProblema situacaoRosasCompleta() {
        return construtorSituacaoRosas().construir();
    }

    static SituacaoProblema situacaoForaDoEscopo() {
        return SituacaoProblema.construtor(
                "situacao-fora-do-escopo",
                "Três crianças ganharam 4 figurinhas cada. Quantas figurinhas ganharam ao todo?"
        )
                .comCatalogo(catalogoDasRosas())
                .rotuladaForaDoEscopo()
                .construir();
    }

    static SituacaoProblema situacaoComAmbiguidadeDeclarada() {
        return SituacaoProblema.construtor(
                "situacao-rosas-ambigua",
                "Vovó tinha 2 rosas brancas e 3 amarelas. Deu 1 branca e 1 amarela para a Netinha. Com quantas ficou?"
        )
                .comCatalogo(catalogoDasRosas())
                .comEstadoInicial(estadoInicialDasRosas())
                .comTransformacao(t1DasRosas())
                .comTransformacao(t2DasRosas())
                .comEstadoFinalCurado(estadoFinalCuradoDasRosas())
                .comIncognita(incognitaDasRosas())
                .comRelacaoEstrutural(relacaoDasRosas())
                .comContexto(new ContextoNarrativo("jardim", "presente", "hoje"))
                .comAmbiguidade("deu 1 branca", "momento da doação não explicitado no enunciado")
                .construir();
    }

    static SituacaoProblema situacaoComEstadoFinalDivergente() {
        Inventario inventarioDivergente = Inventario.com(
                ROSA_BRANCA.contar(2),
                ROSA_AMARELA.contar(2)
        );
        EstadoDePosse estadoFinalDivergente = new EstadoDePosse(
                VOVO, inventarioDivergente, MarcadorTemporal.de(3, "final divergente")
        );
        return SituacaoProblema.construtor(
                "situacao-rosas-divergente",
                "Vovó tinha 2 rosas brancas e 3 amarelas. Deu 1 branca e 1 amarela para a Netinha. Com quantas ficou?"
        )
                .comCatalogo(catalogoDasRosas())
                .comEstadoInicial(estadoInicialDasRosas())
                .comTransformacao(t1DasRosas())
                .comTransformacao(t2DasRosas())
                .comEstadoFinalCurado(estadoFinalDivergente)
                .comIncognita(incognitaDasRosas())
                .comRelacaoEstrutural(relacaoDasRosas())
                .comContexto(new ContextoNarrativo("jardim", "presente", "hoje"))
                .construir();
    }
}
