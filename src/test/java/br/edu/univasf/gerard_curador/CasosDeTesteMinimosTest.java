package br.edu.univasf.gerard_curador;

import br.edu.univasf.gerard_curador.app.SituacoesCuradas;
import br.edu.univasf.gerard_curador.dominio.estrutural.ComposicaoDeTransformacoes;
import br.edu.univasf.gerard_curador.dominio.estrutural.IncognitaQuantitativa;
import br.edu.univasf.gerard_curador.dominio.estrutural.PapelQuantitativo;
import br.edu.univasf.gerard_curador.dominio.excecao.QuantidadeIncompativelException;
import br.edu.univasf.gerard_curador.dominio.excecao.RetiradaImpossivelException;
import br.edu.univasf.gerard_curador.dominio.narrativo.EstadoDePosse;
import br.edu.univasf.gerard_curador.dominio.narrativo.Inventario;
import br.edu.univasf.gerard_curador.dominio.narrativo.MarcadorTemporal;
import br.edu.univasf.gerard_curador.dominio.narrativo.SentidoTransformacao;
import br.edu.univasf.gerard_curador.dominio.narrativo.TransformacaoNarrativa;
import br.edu.univasf.gerard_curador.dominio.quantitativo.FamiliaObjeto;
import br.edu.univasf.gerard_curador.dominio.quantitativo.GrandezaQuantitativa;
import br.edu.univasf.gerard_curador.dominio.quantitativo.Numero;
import br.edu.univasf.gerard_curador.dominio.quantitativo.ObjetoContado;
import br.edu.univasf.gerard_curador.dominio.quantitativo.Quantidade;
import br.edu.univasf.gerard_curador.dominio.quantitativo.RealizacaoTextualObjeto;
import br.edu.univasf.gerard_curador.dominio.quantitativo.ReferenciaVisual;
import br.edu.univasf.gerard_curador.dominio.quantitativo.UnidadeMedida;
import br.edu.univasf.gerard_curador.dominio.situacao.EpisodioNarrativo;
import br.edu.univasf.gerard_curador.dominio.situacao.SequenciaNarrativa;
import br.edu.univasf.gerard_curador.dominio.situacao.SituacaoProblema;
import br.edu.univasf.gerard_curador.dominio.validacao.ResultadoValidacao;
import br.edu.univasf.gerard_curador.dominio.validacao.TipoDiagnostico;
import br.edu.univasf.gerard_curador.representacao.quadrinhos.GeradorRoteiroQuadrinhos;
import br.edu.univasf.gerard_curador.representacao.quadrinhos.Quadro;
import br.edu.univasf.gerard_curador.representacao.quadrinhos.RenderizadorDeRoteiro;
import br.edu.univasf.gerard_curador.representacao.quadrinhos.RenderizadorHtml;
import br.edu.univasf.gerard_curador.representacao.quadrinhos.RenderizadorTextual;
import br.edu.univasf.gerard_curador.representacao.quadrinhos.RoteiroQuadrinhos;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Casos de teste mínimos da especificação Gerard Curador")
class CasosDeTesteMinimosTest {

    @Test
    @Order(1)
    @DisplayName("01. Duas rosas brancas mais três amarelas totalizam cinco rosas.")
    void caso01_duasRosasBrancasMaisTresAmarelasTotalizamCincoRosas() {
        SituacaoProblema rosas = SituacoesCuradas.rosas();
        EstadoDePosse estadoInicial = rosas.getEstadoInicial();

        FamiliaObjeto familiaRosa = estadoInicial.getInventario().familiasPresentes().get(0);
        ObjetoContado rosaBranca = rosas.getTransformacoes().get(0).getQuantidade().getObjeto();
        ObjetoContado rosaAmarela = rosas.getTransformacoes().get(1).getQuantidade().getObjeto();

        assertEquals(2, estadoInicial.quantidadeDe(rosaBranca).getValor());
        assertEquals(3, estadoInicial.quantidadeDe(rosaAmarela).getValor());
        assertEquals(Numero.de(5), estadoInicial.totalDaFamilia(familiaRosa));
        assertEquals(5, estadoInicial.totalDaFamilia(familiaRosa).getValor());
        assertEquals(5, estadoInicial.totalGeral().getValor());
    }

    @Test
    @Order(2)
    @DisplayName("02. Retirar uma rosa branca deixa uma rosa branca.")
    void caso02_retirarUmaRosaBrancaDeixaUmaRosaBranca() {
        SituacaoProblema rosas = SituacoesCuradas.rosas();
        TransformacaoNarrativa t1 = rosas.getTransformacoes().get(0);
        ObjetoContado rosaBranca = t1.getQuantidade().getObjeto();

        EstadoDePosse estadoAposT1 = t1.aplicarSobre(rosas.getEstadoInicial());

        assertEquals(1, estadoAposT1.quantidadeDe(rosaBranca).getValor());
        assertEquals(Numero.de(1), estadoAposT1.getInventario().numeroDe(rosaBranca));

        Inventario inventarioAposRetirar = rosas.getEstadoInicial().getInventario().retirar(t1.getQuantidade());
        assertEquals(Numero.de(1), inventarioAposRetirar.numeroDe(rosaBranca));
    }

    @Test
    @Order(3)
    @DisplayName("03. Retirar uma amarela depois deixa duas amarelas.")
    void caso03_retirarUmaAmarelaDepoisDeixaDuasAmarelas() {
        SituacaoProblema rosas = SituacoesCuradas.rosas();
        TransformacaoNarrativa t1 = rosas.getTransformacoes().get(0);
        TransformacaoNarrativa t2 = rosas.getTransformacoes().get(1);
        ObjetoContado rosaAmarela = t2.getQuantidade().getObjeto();

        EstadoDePosse aposT1 = t1.aplicarSobre(rosas.getEstadoInicial());
        EstadoDePosse aposT2 = t2.aplicarSobre(aposT1);

        assertEquals(1, aposT2.quantidadeDe(t1.getQuantidade().getObjeto()).getValor());
        assertEquals(2, aposT2.quantidadeDe(rosaAmarela).getValor());
        assertEquals(Numero.de(2), aposT2.getInventario().numeroDe(rosaAmarela));
    }

    @Test
    @Order(4)
    @DisplayName("04. O total final é três.")
    void caso04_oTotalFinalETres() {
        SituacaoProblema rosas = SituacoesCuradas.rosas();
        EstadoDePosse estadoFinalCalculado = rosas.calcularEstadoFinal();
        EstadoDePosse estadoFinalCurado = rosas.getEstadoFinalCurado();
        FamiliaObjeto familiaRosa = estadoFinalCalculado.getInventario().familiasPresentes().get(0);

        assertEquals(3, estadoFinalCalculado.totalGeral().getValor());
        assertEquals(3, estadoFinalCalculado.totalDaFamilia(familiaRosa).getValor());

        assertNotNull(estadoFinalCurado);
        assertEquals(3, estadoFinalCurado.totalGeral().getValor());
        assertEquals(3, estadoFinalCurado.totalDaFamilia(familiaRosa).getValor());
    }

    @Test
    @Order(5)
    @DisplayName("05. Retirar três rosas brancas quando existem duas é rejeitado.")
    void caso05_retirarTresRosasBrancasQuandoExistemDuasERejeitado() {
        SituacaoProblema rosas = SituacoesCuradas.rosas();
        EstadoDePosse estadoInicial = rosas.getEstadoInicial();
        ObjetoContado rosaBranca = rosas.getTransformacoes().get(0).getQuantidade().getObjeto();
        Quantidade tresBrancas = rosaBranca.contar(3);

        assertThrows(RetiradaImpossivelException.class, () ->
                estadoInicial.getInventario().retirar(tresBrancas));

        assertThrows(RetiradaImpossivelException.class, () ->
                estadoInicial.retirar(tresBrancas, MarcadorTemporal.de(2, "retirada excessiva")));

        assertEquals(2, estadoInicial.quantidadeDe(rosaBranca).getValor());
        assertEquals(5, estadoInicial.totalGeral().getValor());
    }

    @Test
    @Order(6)
    @DisplayName("06. Uma transformação sem ator é inválida.")
    void caso06_umaTransformacaoSemAtorEInvalida() {
        SituacaoProblema rosas = SituacoesCuradas.rosas();
        TransformacaoNarrativa t1 = rosas.getTransformacoes().get(0);

        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () ->
                new TransformacaoNarrativa(
                        null,
                        t1.getAcao(),
                        t1.getDestinatario(),
                        t1.getQuantidade(),
                        t1.getMarcador()
                ));
        assertTrue(ex1.getMessage().contains("ator"));

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () ->
                TransformacaoNarrativa.semDestinatario(
                        null,
                        t1.getAcao(),
                        t1.getQuantidade(),
                        t1.getMarcador()
                ));
        assertTrue(ex2.getMessage().contains("ator"));
    }

    @Test
    @Order(7)
    @DisplayName("07. Duas transformações com a mesma ordem são inválidas.")
    void caso07_duasTransformacoesComAMesmaOrdemSaoInvalidas() {
        SituacaoProblema rosas = SituacoesCuradas.rosas();
        TransformacaoNarrativa t1 = rosas.getTransformacoes().get(0);
        TransformacaoNarrativa t2 = rosas.getTransformacoes().get(1);

        TransformacaoNarrativa t2MesmaOrdem = new TransformacaoNarrativa(
                t2.getAtor(),
                t2.getAcao(),
                t2.getDestinatario(),
                t2.getQuantidade(),
                MarcadorTemporal.de(t1.getOrdem(), "mesma ordem temporal")
        );

        SituacaoProblema situacao = SituacaoProblema.construtor("ordem-repetida", "Duas ordens iguais")
                .comCatalogo(rosas.getCatalogo())
                .comEstadoInicial(rosas.getEstadoInicial())
                .comTransformacao(t1)
                .comTransformacao(t2MesmaOrdem)
                .comIncognita(rosas.getIncognita())
                .construir();

        ResultadoValidacao resultado = situacao.validar();
        assertFalse(resultado.ehValido());
        assertTrue(resultado.contem(TipoDiagnostico.ORDEM_TEMPORAL_REPETIDA));
    }

    @Test
    @Order(8)
    @DisplayName("08. Todo objeto referido por uma transformação existe no catálogo.")
    void caso08_todoObjetoReferidoPorUmaTransformacaoExisteNoCatalogo() {
        SituacaoProblema rosas = SituacoesCuradas.rosas();
        assertTrue(rosas.validar().ehValido());
        assertFalse(rosas.validar().contem(TipoDiagnostico.OBJETO_CONTADO_INEXISTENTE));

        FamiliaObjeto familia = rosas.getEstadoInicial().getInventario().familiasPresentes().get(0);
        ObjetoContado objetoNaoCatalogado = new ObjetoContado(
                "rosa_roxa",
                familia,
                Set.of(),
                RealizacaoTextualObjeto.emPortugues("rosa roxa", "rosas roxas"),
                ReferenciaVisual.de("rosa_roxa")
        );

        TransformacaoNarrativa transformacaoInvalida = new TransformacaoNarrativa(
                rosas.getTransformacoes().get(0).getAtor(),
                rosas.getTransformacoes().get(0).getAcao(),
                rosas.getTransformacoes().get(0).getDestinatario(),
                objetoNaoCatalogado.contar(1),
                MarcadorTemporal.de(2, "transformacao com objeto fora do catalogo")
        );

        SituacaoProblema situacao = SituacaoProblema.construtor("objeto-nao-catalogado", "Objeto ausente")
                .comCatalogo(rosas.getCatalogo())
                .comEstadoInicial(rosas.getEstadoInicial())
                .comTransformacao(transformacaoInvalida)
                .comIncognita(rosas.getIncognita())
                .construir();

        ResultadoValidacao resultado = situacao.validar();
        assertFalse(resultado.ehValido());
        assertTrue(resultado.contem(TipoDiagnostico.OBJETO_CONTADO_INEXISTENTE));
    }

    @Test
    @Order(9)
    @DisplayName("09. Estado final curado divergente gera diagnóstico, não correção.")
    void caso09_estadoFinalCuradoDivergenteGeraDiagnosticoNaoCorrecao() {
        SituacaoProblema rosas = SituacoesCuradas.rosas();
        ObjetoContado rosaBranca = rosas.getTransformacoes().get(0).getQuantidade().getObjeto();
        ObjetoContado rosaAmarela = rosas.getTransformacoes().get(1).getQuantidade().getObjeto();

        EstadoDePosse estadoFinalDivergente = new EstadoDePosse(
                rosas.getEstadoInicial().getPossuidor(),
                Inventario.com(rosaBranca.contar(2), rosaAmarela.contar(2)),
                MarcadorTemporal.de(4, "final divergente informado pelo curador")
        );

        SituacaoProblema situacao = SituacaoProblema.construtor("rosas-divergente", "Divergência curada")
                .comCatalogo(rosas.getCatalogo())
                .comEstadoInicial(rosas.getEstadoInicial())
                .comTransformacao(rosas.getTransformacoes().get(0))
                .comTransformacao(rosas.getTransformacoes().get(1))
                .comEstadoFinalCurado(estadoFinalDivergente)
                .comIncognita(rosas.getIncognita())
                .construir();

        ResultadoValidacao resultado = situacao.validar();
        assertFalse(resultado.ehValido());
        assertTrue(resultado.contem(TipoDiagnostico.ESTADO_CALCULADO_DIVERGENTE));

        assertSame(estadoFinalDivergente, situacao.getEstadoFinalCurado());
        assertEquals(4, situacao.getEstadoFinalCurado().totalGeral().getValor());
        assertEquals(3, situacao.calcularEstadoFinal().totalGeral().getValor());
    }

    @Test
    @Order(10)
    @DisplayName("10. A sequência narrativa mantém a ordem das transformações.")
    void caso10_aSequenciaNarrativaMantemAOrdemDasTransformacoes() {
        SituacaoProblema rosas = SituacoesCuradas.rosas();
        TransformacaoNarrativa t1 = rosas.getTransformacoes().get(0);
        TransformacaoNarrativa t2 = rosas.getTransformacoes().get(1);

        SituacaoProblema situacaoInvertida = SituacaoProblema.construtor("invertida", "Ordem inversa")
                .comCatalogo(rosas.getCatalogo())
                .comEstadoInicial(rosas.getEstadoInicial())
                .comTransformacao(t2)
                .comTransformacao(t1)
                .comIncognita(rosas.getIncognita())
                .construir();

        SequenciaNarrativa sequencia = situacaoInvertida.projetarSequenciaNarrativa();
        assertTrue(sequencia.preservaOrdemTemporal());

        List<EpisodioNarrativo> eventos = sequencia.getEventos();
        assertEquals(2, eventos.size());
        assertEquals(t1.getOrdem(), eventos.get(0).getMomento().getOrdem());
        assertEquals(t2.getOrdem(), eventos.get(1).getMomento().getOrdem());
        assertTrue(eventos.get(0).getMomento().getOrdem() < eventos.get(1).getMomento().getOrdem());
    }

    @Test
    @Order(11)
    @DisplayName("11. O roteiro possui estado inicial, duas ações e estado final.")
    void caso11_oRoteiroPossuiEstadoInicialDuasAcoesEEstadoFinal() {
        SituacaoProblema rosas = SituacoesCuradas.rosas();
        SequenciaNarrativa sequencia = rosas.projetarSequenciaNarrativa();

        GeradorRoteiroQuadrinhos gerador = new GeradorRoteiroQuadrinhos();
        RoteiroQuadrinhos roteiro = gerador.gerar(sequencia, "Rosas da Vovó");

        assertEquals(4, roteiro.quantidadeDeQuadros());
        assertTrue(roteiro.respeitaFaixaDeQuadros());

        List<Quadro> quadros = roteiro.getQuadros();
        assertEquals("Estado inicial", quadros.get(0).getTitulo());
        assertEquals("primeira entrega", quadros.get(1).getTitulo());
        assertEquals("segunda entrega", quadros.get(2).getTitulo());
        assertTrue(quadros.get(3).getTitulo().contains("Estado final"));
        assertTrue(quadros.get(3).possuiPergunta());
    }

    @Test
    @Order(12)
    @DisplayName("12. Trocar o renderizador textual pelo de quadrinhos não altera o domínio.")
    void caso12_trocarORenderizadorTextualPeloDeQuadrinhosNaoAlteraODominio() {
        SituacaoProblema rosas = SituacoesCuradas.rosas();
        GeradorRoteiroQuadrinhos gerador = new GeradorRoteiroQuadrinhos();
        RoteiroQuadrinhos roteiro = gerador.gerar(rosas.projetarSequenciaNarrativa(), "Rosas da Vovó");

        List<RenderizadorDeRoteiro> renderizadores = List.of(
                new RenderizadorTextual(),
                new RenderizadorHtml()
        );

        int totalInicialAntes = rosas.getEstadoInicial().totalGeral().getValor();
        int totalFinalAntes = rosas.getEstadoFinalCurado().totalGeral().getValor();
        int totalQuadrosAntes = roteiro.quantidadeDeQuadros();
        String idSituacaoAntes = rosas.getId();
        String tituloRoteiroAntes = roteiro.getTitulo();

        for (RenderizadorDeRoteiro renderizador : renderizadores) {
            String resultado = renderizador.renderizar(roteiro);
            assertNotNull(resultado);
            assertFalse(resultado.isBlank());

            assertEquals(totalInicialAntes, rosas.getEstadoInicial().totalGeral().getValor());
            assertEquals(totalFinalAntes, rosas.getEstadoFinalCurado().totalGeral().getValor());
            assertEquals(idSituacaoAntes, rosas.getId());
            assertEquals(totalQuadrosAntes, roteiro.quantidadeDeQuadros());
            assertEquals(tituloRoteiroAntes, roteiro.getTitulo());
            assertTrue(rosas.validar().ehValido());
        }
    }

    @Test
    @Order(13)
    @DisplayName("13. Uma situação com estado inicial desconhecido mantém essa designação após a resolução.")
    void caso13_umaSituacaoComEstadoInicialDesconhecidoMantemEssaDesignacaoAposAResolucao() {
        SituacaoProblema lucas = SituacoesCuradas.figurinhasComEstadoInicialDesconhecido();
        IncognitaQuantitativa incognita = lucas.getIncognita();

        assertNotNull(incognita);
        assertEquals(PapelQuantitativo.ESTADO_INICIAL, incognita.getPapelOriginal());
        assertTrue(incognita.designa(PapelQuantitativo.ESTADO_INICIAL));
        assertFalse(incognita.estaResolvida());

        Quantidade valorResolvido = lucas.getEstadoInicial().getInventario().getItens().get(0);
        IncognitaQuantitativa resolvida = incognita.resolver(valorResolvido);

        assertTrue(resolvida.estaResolvida());
        assertEquals(PapelQuantitativo.ESTADO_INICIAL, resolvida.getPapelOriginal());
        assertTrue(resolvida.designa(PapelQuantitativo.ESTADO_INICIAL));
        assertEquals(5, resolvida.getValorResolvido().getValor());
    }

    @Test
    @Order(14)
    @DisplayName("14. Uma situação com transformação desconhecida é validada pela relação estrutural.")
    void caso14_umaSituacaoComTransformacaoDesconhecidaEValidadaPelaRelacaoEstrutural() {
        SituacaoProblema joao = SituacoesCuradas.bilasComDuasPartidas();

        assertEquals(PapelQuantitativo.TRANSFORMACAO_PRIMEIRA, joao.getIncognita().getPapelOriginal());
        assertTrue(joao.getRelacaoEstrutural().validar().ehValido());

        ObjetoContado bila = joao.getEstadoInicial().getInventario().getItens().get(0).getObjeto();

        ComposicaoDeTransformacoes relacaoSemPrimeira = ComposicaoDeTransformacoes.construtor("bilas-sem-t1")
                .comEstadoInicial(bila.contar(0))
                .comSegundaTransformacao(bila.contar(12), SentidoTransformacao.ACRESCIMO)
                .comTransformacaoResultante(bila.contar(22), SentidoTransformacao.ACRESCIMO)
                .comEstadoFinal(bila.contar(22))
                .construir();

        Numero resolvido = relacaoSemPrimeira.resolver(PapelQuantitativo.TRANSFORMACAO_PRIMEIRA);
        assertNotNull(resolvido);
        assertEquals(10, resolvido.getValor());
    }

    @Test
    @Order(15)
    @DisplayName("15. Grandezas ou unidades incompatíveis não participam da mesma operação.")
    void caso15_grandezasOuUnidadesIncompativeisNaoParticipamDaMesmaOperacao() {
        FamiliaObjeto familiaRosa = new FamiliaObjeto("rosa", "Rosa");
        FamiliaObjeto familiaDinheiro = new FamiliaObjeto("dinheiro", "Dinheiro");
        FamiliaObjeto familiaAlimento = new FamiliaObjeto("alimento", "Alimento");

        ObjetoContado rosa = new ObjetoContado("rosa", familiaRosa, Set.of(),
                RealizacaoTextualObjeto.emPortugues("rosa", "rosas"), null);
        ObjetoContado dinheiro = new ObjetoContado("dinheiro", familiaDinheiro, Set.of(),
                RealizacaoTextualObjeto.emPortugues("dinheiro", "dinheiro"), null);
        ObjetoContado arroz = new ObjetoContado("arroz", familiaAlimento, Set.of(),
                RealizacaoTextualObjeto.emPortugues("arroz", "arroz"), null);

        Quantidade rosas = rosa.contar(5);
        Quantidade valorMonetario = dinheiro.quantificar(
                Numero.de(10), GrandezaQuantitativa.VALOR_MONETARIO, UnidadeMedida.REAL);
        Quantidade centavos = dinheiro.quantificar(
                Numero.de(50), GrandezaQuantitativa.VALOR_MONETARIO, UnidadeMedida.CENTAVO);
        Quantidade massa = arroz.quantificar(
                Numero.de(2), GrandezaQuantitativa.MASSA, UnidadeMedida.QUILOGRAMA);

        assertThrows(QuantidadeIncompativelException.class, () -> valorMonetario.somar(massa));
        assertThrows(QuantidadeIncompativelException.class, () -> rosas.somar(valorMonetario));
        assertThrows(QuantidadeIncompativelException.class, () -> valorMonetario.subtrair(massa));
        assertThrows(QuantidadeIncompativelException.class, () -> valorMonetario.somar(centavos));

        assertThrows(IllegalArgumentException.class, () ->
                new Quantidade(Numero.de(10), rosa, GrandezaQuantitativa.CONTAGEM, UnidadeMedida.REAL));
        assertThrows(IllegalArgumentException.class, () ->
                new Quantidade(Numero.de(5), arroz, GrandezaQuantitativa.MASSA, UnidadeMedida.UNIDADE));
    }

    @Test
    @Order(16)
    @DisplayName("16. Características diferentes podem ser totalizadas quando pertencem à mesma família.")
    void caso16_caracteristicasDiferentesPodemSerTotalizadasQuandoPertencemAMesmaFamilia() {
        SituacaoProblema rosas = SituacoesCuradas.rosas();
        EstadoDePosse estadoInicial = rosas.getEstadoInicial();

        ObjetoContado rosaBranca = rosas.getTransformacoes().get(0).getQuantidade().getObjeto();
        ObjetoContado rosaAmarela = rosas.getTransformacoes().get(1).getQuantidade().getObjeto();

        assertNotEquals(rosaBranca.getCaracteristicas(), rosaAmarela.getCaracteristicas());
        assertEquals("branca", rosaBranca.valorDaCaracteristica("cor"));
        assertEquals("amarela", rosaAmarela.valorDaCaracteristica("cor"));
        assertEquals(rosaBranca.getFamilia(), rosaAmarela.getFamilia());
        assertTrue(rosaBranca.pertenceAMesmaFamilia(rosaAmarela));

        Numero totalFamilia = estadoInicial.totalDaFamilia(rosaBranca.getFamilia());
        assertEquals(Numero.de(5), totalFamilia);
        assertEquals(5, totalFamilia.getValor());
    }

    @Test
    @Order(17)
    @DisplayName("17. Primeira e segunda partida preservam a ordem curada dos eventos.")
    void caso17_primeiraESegundaPartidaPreservamAOrdemCuradaDosEventos() {
        SituacaoProblema joao = SituacoesCuradas.bilasComDuasPartidas();
        List<TransformacaoNarrativa> transformacoes = joao.getTransformacoes();

        assertEquals(2, transformacoes.size());
        TransformacaoNarrativa primeira = transformacoes.get(0);
        TransformacaoNarrativa segunda = transformacoes.get(1);

        assertEquals(2, primeira.getOrdem());
        assertEquals(3, segunda.getOrdem());
        assertEquals("primeira partida", primeira.getMarcador().getRotulo());
        assertEquals("segunda partida", segunda.getMarcador().getRotulo());
        assertTrue(primeira.getOrdem() < segunda.getOrdem());

        SequenciaNarrativa sequencia = joao.projetarSequenciaNarrativa();
        assertTrue(sequencia.preservaOrdemTemporal());
        List<EpisodioNarrativo> eventos = sequencia.getEventos();
        assertEquals(2, eventos.size());
        assertEquals("primeira partida", eventos.get(0).getMomento().getRotulo());
        assertEquals("segunda partida", eventos.get(1).getMomento().getRotulo());
    }

    @Test
    @Order(18)
    @DisplayName("18. Um enunciado ambíguo gera diagnóstico sem associação automática.")
    void caso18_umEnunciadoAmbiguoGeraDiagnosticoSemAssociacaoAutomatica() {
        SituacaoProblema rosas = SituacoesCuradas.rosas();

        SituacaoProblema ambigua = SituacaoProblema.construtor(
                        "rosas-ambigua-01",
                        "Vovó tinha rosas e deu rosas para alguém.")
                .comCatalogo(rosas.getCatalogo())
                .comEstadoInicial(rosas.getEstadoInicial())
                .comIncognita(rosas.getIncognita())
                .comAmbiguidade("deu rosas para alguém", "destinatário e quantidade indeterminados")
                .construir();

        ResultadoValidacao resultado = ambigua.validar();

        assertTrue(resultado.contem(TipoDiagnostico.ENUNCIADO_AMBIGUO));
        assertTrue(ambigua.getTransformacoes().isEmpty());
        assertThrows(IllegalStateException.class, () -> ambigua.promoverParaValidada("Revisor Humano"));
    }
}
