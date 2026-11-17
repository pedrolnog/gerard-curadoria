package br.edu.univasf.gerard_curador.dominio.estrutural;

import br.edu.univasf.gerard_curador.dominio.narrativo.SentidoTransformacao;
import br.edu.univasf.gerard_curador.dominio.quantitativo.*;
import br.edu.univasf.gerard_curador.dominio.validacao.ResultadoValidacao;
import br.edu.univasf.gerard_curador.dominio.validacao.TipoDiagnostico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Composição de Transformações")
class ComposicaoDeTransformacoesTest {

    private ObjetoContado rosa;
    private ObjetoContado figurinha;
    private ObjetoContado dinheiro;

    @BeforeEach
    void setUp() {
        rosa = new ObjetoContado(
                "rosa",
                new FamiliaObjeto("rosa", "Rosa"),
                Set.of(),
                RealizacaoTextualObjeto.emPortugues("rosa", "rosas"),
                null
        );
        figurinha = new ObjetoContado(
                "figurinha",
                new FamiliaObjeto("figurinha", "Figurinha"),
                Set.of(),
                RealizacaoTextualObjeto.emPortugues("figurinha", "figurinhas"),
                null
        );
        dinheiro = new ObjetoContado(
                "dinheiro",
                new FamiliaObjeto("dinheiro", "Dinheiro"),
                Set.of(),
                RealizacaoTextualObjeto.emPortugues("dinheiro", "dinheiro"),
                null
        );
    }

    @Test
    @DisplayName("Situação das rosas (Ei=5, T1 retira 1, T2 retira 1, Ef=3) é válida")
    void situacaoDasRosasValida() {
        ComposicaoDeTransformacoes relacao = ComposicaoDeTransformacoes.construtor("rosas-valida")
                .comEstadoInicial(rosa.contar(5))
                .comPrimeiraTransformacao(rosa.contar(1), SentidoTransformacao.RETIRADA)
                .comSegundaTransformacao(rosa.contar(1), SentidoTransformacao.RETIRADA)
                .comEstadoFinal(rosa.contar(3))
                .construir();

        ResultadoValidacao resultado = relacao.validar();

        assertTrue(resultado.ehValido());
        assertFalse(resultado.possuiOcorrencias());
        assertEquals("COMPOSICAO_TRANSFORMACOES", relacao.getCategoria());
        assertEquals(5, relacao.quantidadeDoPapel(PapelQuantitativo.ESTADO_INICIAL).getValor());
        assertEquals(3, relacao.quantidadeDoPapel(PapelQuantitativo.ESTADO_FINAL).getValor());
    }

    @Test
    @DisplayName("Estado final curado divergente (ex. 4) gera ESTADO_CALCULADO_DIVERGENTE sem alterar a quantidade curada (Caso 9)")
    void estadoFinalDivergenteGeraDiagnosticoPreservandoValorCurado() {
        ComposicaoDeTransformacoes relacao = ComposicaoDeTransformacoes.construtor("rosas-divergente")
                .comEstadoInicial(rosa.contar(5))
                .comPrimeiraTransformacao(rosa.contar(1), SentidoTransformacao.RETIRADA)
                .comSegundaTransformacao(rosa.contar(1), SentidoTransformacao.RETIRADA)
                .comEstadoFinal(rosa.contar(4))
                .construir();

        ResultadoValidacao resultado = relacao.validar();

        assertFalse(resultado.ehValido());
        assertTrue(resultado.contem(TipoDiagnostico.ESTADO_CALCULADO_DIVERGENTE));
        assertEquals(4, relacao.quantidadeDoPapel(PapelQuantitativo.ESTADO_FINAL).getValor(),
                "O valor do estado final informado pelo curador deve ser preservado intacto");
    }

    @Test
    @DisplayName("João: T2 ganhou 12, total ganho 22 resolve TRANSFORMACAO_PRIMEIRA = 10 (Caso 14, transformação desconhecida validada pela relação)")
    void joaoPartidasResolvePrimeiraTransformacaoEValida() {
        ComposicaoDeTransformacoes relacao = ComposicaoDeTransformacoes.construtor("joao-partidas")
                .comSegundaTransformacao(figurinha.contar(12), SentidoTransformacao.ACRESCIMO)
                .comTransformacaoResultante(figurinha.contar(22), SentidoTransformacao.ACRESCIMO)
                .construir();

        Numero t1 = relacao.resolver(PapelQuantitativo.TRANSFORMACAO_PRIMEIRA);
        assertEquals(Numero.de(10), t1);

        // Caso 14: a transformação desconhecida encontrada é validada pela relação
        ComposicaoDeTransformacoes relacaoCompleta = ComposicaoDeTransformacoes.construtor("joao-partidas-completa")
                .comPrimeiraTransformacao(figurinha.contar(10), SentidoTransformacao.ACRESCIMO)
                .comSegundaTransformacao(figurinha.contar(12), SentidoTransformacao.ACRESCIMO)
                .comTransformacaoResultante(figurinha.contar(22), SentidoTransformacao.ACRESCIMO)
                .construir();

        ResultadoValidacao resultado = relacaoCompleta.validar();
        assertTrue(resultado.ehValido());
    }

    @Test
    @DisplayName("Lucas: ganhou 12 e tem 17 resolve ESTADO_INICIAL = 5")
    void lucasResolveEstadoInicial() {
        ComposicaoDeTransformacoes relacao = ComposicaoDeTransformacoes.construtor("lucas-ganhou")
                .comPrimeiraTransformacao(figurinha.contar(12), SentidoTransformacao.ACRESCIMO)
                .comEstadoFinal(figurinha.contar(17))
                .construir();

        Numero estadoInicial = relacao.resolver(PapelQuantitativo.ESTADO_INICIAL);
        assertEquals(Numero.de(5), estadoInicial);
    }

    @Test
    @DisplayName("Grandezas ou unidades incompatíveis entre papéis geram GRANDEZAS_OU_UNIDADES_INCOMPATIVEIS (Caso 15)")
    void grandezasOuUnidadesIncompativeisGeramDiagnostico() {
        Quantidade inicialContagem = rosa.contar(5);
        Quantidade transfMonetaria = new Quantidade(
                Numero.de(2), dinheiro, GrandezaQuantitativa.VALOR_MONETARIO, UnidadeMedida.REAL);

        ComposicaoDeTransformacoes relacao = ComposicaoDeTransformacoes.construtor("incompativel")
                .comEstadoInicial(inicialContagem)
                .comPrimeiraTransformacao(transfMonetaria, SentidoTransformacao.RETIRADA)
                .construir();

        ResultadoValidacao resultado = relacao.validar();

        assertFalse(resultado.ehValido());
        assertTrue(resultado.contem(TipoDiagnostico.GRANDEZAS_OU_UNIDADES_INCOMPATIVEIS));
    }

    @Test
    @DisplayName("Deve resolver estado final e segunda transformação")
    void deveResolverEstadoFinalESegundaTransformacao() {
        ComposicaoDeTransformacoes relacaoEf = ComposicaoDeTransformacoes.construtor("resolve-ef")
                .comEstadoInicial(rosa.contar(5))
                .comPrimeiraTransformacao(rosa.contar(1), SentidoTransformacao.RETIRADA)
                .comSegundaTransformacao(rosa.contar(1), SentidoTransformacao.RETIRADA)
                .construir();

        assertEquals(Numero.de(3), relacaoEf.resolver(PapelQuantitativo.ESTADO_FINAL));

        ComposicaoDeTransformacoes relacaoT2 = ComposicaoDeTransformacoes.construtor("resolve-t2")
                .comPrimeiraTransformacao(figurinha.contar(10), SentidoTransformacao.ACRESCIMO)
                .comTransformacaoResultante(figurinha.contar(22), SentidoTransformacao.ACRESCIMO)
                .construir();

        assertEquals(Numero.de(12), relacaoT2.resolver(PapelQuantitativo.TRANSFORMACAO_SEGUNDA));
    }

    @Test
    @DisplayName("Retirada que excede o estado inicial gera QUANTIDADE_INDISPONIVEL_PARA_RETIRADA")
    void retiradaQueExcedeEstadoInicialGeraDiagnostico() {
        ComposicaoDeTransformacoes relacao = ComposicaoDeTransformacoes.construtor("retirada-excessiva")
                .comEstadoInicial(rosa.contar(1))
                .comPrimeiraTransformacao(rosa.contar(2), SentidoTransformacao.RETIRADA)
                .comEstadoFinal(rosa.contar(0))
                .construir();

        ResultadoValidacao resultado = relacao.validar();

        assertFalse(resultado.ehValido());
        assertTrue(resultado.contem(TipoDiagnostico.QUANTIDADE_INDISPONIVEL_PARA_RETIRADA));
    }
}
