package br.edu.univasf.gerard_curador.dominio.situacao;

import br.edu.univasf.gerard_curador.dominio.narrativo.ContextoNarrativo;
import br.edu.univasf.gerard_curador.dominio.narrativo.EstadoDePosse;
import br.edu.univasf.gerard_curador.dominio.narrativo.Inventario;
import br.edu.univasf.gerard_curador.dominio.narrativo.MarcadorTemporal;
import br.edu.univasf.gerard_curador.dominio.narrativo.TransformacaoNarrativa;
import br.edu.univasf.gerard_curador.dominio.quantitativo.FamiliaObjeto;
import br.edu.univasf.gerard_curador.dominio.quantitativo.ObjetoContado;
import br.edu.univasf.gerard_curador.dominio.quantitativo.RealizacaoTextualObjeto;
import br.edu.univasf.gerard_curador.dominio.validacao.ResultadoValidacao;
import br.edu.univasf.gerard_curador.dominio.validacao.TipoDiagnostico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Coordenação e validação da Situação-Problema")
class SituacaoProblemaTest {

    @Test
    @DisplayName("Situação canônica das rosas é válida e calcularEstadoFinal totaliza 3")
    void situacaoRosasValidaETotalizaTres() {
        SituacaoProblema situacao = SituacoesDeTeste.situacaoRosasCompleta();

        ResultadoValidacao resultado = situacao.validar();

        assertTrue(resultado.ehValido(), "A situação das rosas deve ser válida");
        assertFalse(resultado.possuiOcorrencias(), "Não deve haver diagnósticos de erro nem aviso");

        EstadoDePosse estadoFinalCalculado = situacao.calcularEstadoFinal();
        assertEquals(3, estadoFinalCalculado.totalDaFamilia(SituacoesDeTeste.FAMILIA_ROSA).getValor(),
                "O cálculo do percurso deve totalizar 3 rosas");
        assertEquals(3, estadoFinalCalculado.totalGeral().getValor());
        assertEquals(1, estadoFinalCalculado.quantidadeDe(SituacoesDeTeste.ROSA_BRANCA).getValor());
        assertEquals(2, estadoFinalCalculado.quantidadeDe(SituacoesDeTeste.ROSA_AMARELA).getValor());
    }

    @Test
    @DisplayName("Duas transformações com a mesma ordem temporal são inválidas (Caso 7)")
    void duasTransformacoesComMesmaOrdemGeramDiagnosticoOrdemTemporalRepetida() {
        TransformacaoNarrativa t1 = SituacoesDeTeste.t1DasRosas(); // ordem 1
        TransformacaoNarrativa t2ComMesmaOrdem = new TransformacaoNarrativa(
                SituacoesDeTeste.VOVO,
                SituacoesDeTeste.ACAO_DAR,
                SituacoesDeTeste.NETINHA,
                SituacoesDeTeste.ROSA_AMARELA.contar(1),
                MarcadorTemporal.de(1, "segunda doação na mesma ordem") // ordem 1 repetida
        );

        SituacaoProblema situacao = SituacaoProblema.construtor("ordem-repetida", "Enunciado com ordens iguais")
                .comCatalogo(SituacoesDeTeste.catalogoDasRosas())
                .comEstadoInicial(SituacoesDeTeste.estadoInicialDasRosas())
                .comTransformacao(t1)
                .comTransformacao(t2ComMesmaOrdem)
                .comIncognita(SituacoesDeTeste.incognitaDasRosas())
                .construir();

        ResultadoValidacao resultado = situacao.validar();

        assertFalse(resultado.ehValido());
        assertTrue(resultado.contem(TipoDiagnostico.ORDEM_TEMPORAL_REPETIDA),
                "Deve registrar ORDEM_TEMPORAL_REPETIDA quando duas transformações têm a mesma ordem");
    }

    @Test
    @DisplayName("Objeto da transformação ausente do catálogo gera OBJETO_CONTADO_INEXISTENTE (Caso 8)")
    void objetoDaTransformacaoAusenteDoCatalogoGeraDiagnosticoObjetoInexistente() {
        ObjetoContado rosaRoxaNaoCatalogada = new ObjetoContado(
                "rosa_roxa",
                SituacoesDeTeste.FAMILIA_ROSA,
                Set.of(),
                RealizacaoTextualObjeto.emPortugues("rosa roxa", "rosas roxas"),
                null
        );

        TransformacaoNarrativa transformacaoInvalida = new TransformacaoNarrativa(
                SituacoesDeTeste.VOVO,
                SituacoesDeTeste.ACAO_DAR,
                SituacoesDeTeste.NETINHA,
                rosaRoxaNaoCatalogada.contar(1),
                MarcadorTemporal.de(1, "primeira doação")
        );

        SituacaoProblema situacao = SituacaoProblema.construtor("objeto-nao-catalogado", "Enunciado")
                .comCatalogo(SituacoesDeTeste.catalogoDasRosas()) // Não contém rosa_roxa
                .comEstadoInicial(SituacoesDeTeste.estadoInicialDasRosas())
                .comTransformacao(transformacaoInvalida)
                .comIncognita(SituacoesDeTeste.incognitaDasRosas())
                .construir();

        ResultadoValidacao resultado = situacao.validar();

        assertFalse(resultado.ehValido());
        assertTrue(resultado.contem(TipoDiagnostico.OBJETO_CONTADO_INEXISTENTE),
                "Deve gerar OBJETO_CONTADO_INEXISTENTE ao referir objeto fora do catálogo");
    }

    @Test
    @DisplayName("Estado final curado divergente gera ESTADO_CALCULADO_DIVERGENTE e preserva valor curado (Caso 9)")
    void estadoFinalCuradoDivergenteGeraDiagnosticoEPreservaValorCurado() {
        // O cálculo produz 1 branca e 2 amarelas (total 3). Curador informa 2 brancas e 2 amarelas (total 4).
        Inventario inventarioDivergente = Inventario.com(
                SituacoesDeTeste.ROSA_BRANCA.contar(2),
                SituacoesDeTeste.ROSA_AMARELA.contar(2)
        );
        EstadoDePosse estadoFinalDivergente = new EstadoDePosse(
                SituacoesDeTeste.VOVO,
                inventarioDivergente,
                MarcadorTemporal.de(3, "final curado")
        );

        SituacaoProblema situacao = SituacaoProblema.construtor("rosas-divergente", "Enunciado")
                .comCatalogo(SituacoesDeTeste.catalogoDasRosas())
                .comEstadoInicial(SituacoesDeTeste.estadoInicialDasRosas())
                .comTransformacao(SituacoesDeTeste.t1DasRosas())
                .comTransformacao(SituacoesDeTeste.t2DasRosas())
                .comEstadoFinalCurado(estadoFinalDivergente)
                .comIncognita(SituacoesDeTeste.incognitaDasRosas())
                .construir();

        ResultadoValidacao resultado = situacao.validar();

        assertFalse(resultado.ehValido());
        assertTrue(resultado.contem(TipoDiagnostico.ESTADO_CALCULADO_DIVERGENTE));

        // Invariante humana: o estado curado divergente NÃO pode ser sobrescrito
        assertSame(estadoFinalDivergente, situacao.getEstadoFinalCurado());
        assertEquals(4, situacao.getEstadoFinalCurado().totalGeral().getValor(),
                "O valor informado pelo curador deve ser preservado intacto");

        // O cálculo autônomo continua determinando o resultado matemático correto
        assertEquals(3, situacao.calcularEstadoFinal().totalGeral().getValor());
    }

    @Test
    @DisplayName("Transformações inseridas fora de ordem no builder saem ordenadas pela ordem curada (Casos 10 e 17)")
    void transformacoesInseridasForaDeOrdemSaemOrdenadasPelaOrdemCurada() {
        TransformacaoNarrativa t1 = SituacoesDeTeste.t1DasRosas(); // ordem 1
        TransformacaoNarrativa t2 = SituacoesDeTeste.t2DasRosas(); // ordem 2

        // Inserção intencionalmente invertida: T2 antes de T1
        SituacaoProblema situacao = SituacaoProblema.construtor("fora-de-ordem", "Enunciado invertido")
                .comCatalogo(SituacoesDeTeste.catalogoDasRosas())
                .comEstadoInicial(SituacoesDeTeste.estadoInicialDasRosas())
                .comTransformacao(t2)
                .comTransformacao(t1)
                .comIncognita(SituacoesDeTeste.incognitaDasRosas())
                .construir();

        List<TransformacaoNarrativa> transformacoes = situacao.getTransformacoes();
        assertEquals(2, transformacoes.size());
        assertEquals(1, transformacoes.get(0).getOrdem(), "A primeira da lista deve ter ordem 1");
        assertEquals(2, transformacoes.get(1).getOrdem(), "A segunda da lista deve ter ordem 2");
        assertEquals(SituacoesDeTeste.ROSA_BRANCA.getId(),
                transformacoes.get(0).getQuantidade().getObjeto().getId());
        assertEquals(SituacoesDeTeste.ROSA_AMARELA.getId(),
                transformacoes.get(1).getQuantidade().getObjeto().getId());
    }

    @Test
    @DisplayName("Percurso de estados intermediários não corrompe estado inicial (imutabilidade)")
    void calcularPercursoDeEstadosNaoCorrompeEstadoInicial() {
        SituacaoProblema situacao = SituacoesDeTeste.situacaoRosasCompleta();

        List<EstadoDePosse> percurso = situacao.calcularPercursoDeEstados();

        assertEquals(3, percurso.size(), "Percurso deve conter: estado inicial, após T1 e após T2");
        assertEquals(5, percurso.get(0).totalGeral().getValor());
        assertEquals(4, percurso.get(1).totalGeral().getValor());
        assertEquals(3, percurso.get(2).totalGeral().getValor());

        // Estado inicial da situação não foi modificado
        assertEquals(5, situacao.getEstadoInicial().totalGeral().getValor());
        assertEquals(2, situacao.getEstadoInicial().quantidadeDe(SituacoesDeTeste.ROSA_BRANCA).getValor());
        assertEquals(3, situacao.getEstadoInicial().quantidadeDe(SituacoesDeTeste.ROSA_AMARELA).getValor());
    }

    @Test
    @DisplayName("Construtor da situação problema valida obrigatoriedade de campos mínimos")
    void construtorValidaCamposObrigatorios() {
        assertThrows(IllegalArgumentException.class, () ->
                SituacaoProblema.construtor(null, "Enunciado")
                        .comCatalogo(SituacoesDeTeste.catalogoDasRosas())
                        .comEstadoInicial(SituacoesDeTeste.estadoInicialDasRosas())
                        .construir());

        assertThrows(IllegalArgumentException.class, () ->
                SituacaoProblema.construtor("   ", "Enunciado")
                        .comCatalogo(SituacoesDeTeste.catalogoDasRosas())
                        .comEstadoInicial(SituacoesDeTeste.estadoInicialDasRosas())
                        .construir());

        assertThrows(IllegalArgumentException.class, () ->
                SituacaoProblema.construtor("id", "")
                        .comCatalogo(SituacoesDeTeste.catalogoDasRosas())
                        .comEstadoInicial(SituacoesDeTeste.estadoInicialDasRosas())
                        .construir());

        assertThrows(IllegalArgumentException.class, () ->
                SituacaoProblema.construtor("id", "Enunciado")
                        .comCatalogo(null)
                        .comEstadoInicial(SituacoesDeTeste.estadoInicialDasRosas())
                        .construir());

        assertThrows(IllegalArgumentException.class, () ->
                SituacaoProblema.construtor("id", "Enunciado")
                        .comCatalogo(SituacoesDeTeste.catalogoDasRosas())
                        .comEstadoInicial(null)
                        .construir(),
                "No escopo aditivo padrão, estado inicial é obrigatório");
    }

    @Test
    @DisplayName("setEnunciado altera o texto e rejeita em branco com IllegalArgumentException preservando anterior")
    void setEnunciadoAlteraERejeitaEmBranco() {
        SituacaoProblema situacao = SituacoesDeTeste.situacaoRosasCompleta();

        situacao.setEnunciado("Novo enunciado curado");
        assertEquals("Novo enunciado curado", situacao.getEnunciado());

        assertThrows(IllegalArgumentException.class, () -> situacao.setEnunciado(""));
        assertEquals("Novo enunciado curado", situacao.getEnunciado());

        assertThrows(IllegalArgumentException.class, () -> situacao.setEnunciado("   "));
        assertEquals("Novo enunciado curado", situacao.getEnunciado());

        assertThrows(IllegalArgumentException.class, () -> situacao.setEnunciado(null));
        assertEquals("Novo enunciado curado", situacao.getEnunciado());
    }

    @Test
    @DisplayName("Alteração de conteúdo após promoção devolve situação para candidata e anula revisor")
    void alteracaoDeConteudoAposPromocaoDevolveParaCandidata() {
        SituacaoProblema situacao = SituacoesDeTeste.situacaoRosasCompleta();
        situacao.promoverParaValidada("Prof");
        assertEquals(StatusCuradoria.VALIDADA, situacao.getStatus());
        assertEquals("Prof", situacao.getRevisor());

        situacao.setEnunciado("Enunciado alterado");
        assertEquals(StatusCuradoria.CANDIDATA_NAO_CURADA, situacao.getStatus());
        assertNull(situacao.getRevisor());

        situacao.promoverParaValidada("Prof");
        assertEquals(StatusCuradoria.VALIDADA, situacao.getStatus());
        assertEquals("Prof", situacao.getRevisor());

        situacao.setContexto(new ContextoNarrativo("horta", "passado", "ontem"));
        assertEquals(StatusCuradoria.CANDIDATA_NAO_CURADA, situacao.getStatus());
        assertNull(situacao.getRevisor());

        situacao.promoverParaValidada("Prof");
        assertEquals(StatusCuradoria.VALIDADA, situacao.getStatus());
        assertEquals("Prof", situacao.getRevisor());

        situacao.adicionarAmbiguidade("ontem", "tempo incerto");
        assertEquals(StatusCuradoria.CANDIDATA_NAO_CURADA, situacao.getStatus());
        assertNull(situacao.getRevisor());
    }

    @Test
    @DisplayName("getTransformacoes devolve lista não modificável e ordenada por ordem temporal")
    void getTransformacoesNaoModificavelEOrdenadaPorOrdem() {
        SituacaoProblema situacao = SituacoesDeTeste.situacaoRosasCompleta();
        List<TransformacaoNarrativa> transformacoes = situacao.getTransformacoes();

        assertThrows(UnsupportedOperationException.class, () ->
                transformacoes.add(SituacoesDeTeste.t1DasRosas()));

        assertEquals(2, transformacoes.size());
        TransformacaoNarrativa primeira = transformacoes.get(0);
        assertEquals(1, primeira.getOrdem());

        primeira.setMarcador(MarcadorTemporal.de(5, "doação tardia"));

        List<TransformacaoNarrativa> atualizadas = situacao.getTransformacoes();
        assertEquals(2, atualizadas.size());
        assertEquals(primeira, atualizadas.get(1));
        assertEquals(5, atualizadas.get(1).getOrdem());
    }

    @Test
    @DisplayName("Escopo fora do aditivo permite estado inicial nulo e retorno ao aditivo sem estado lança exceção")
    void escopoForaDoAditivoPermiteEstadoInicialNulo() {
        SituacaoProblema situacao = SituacoesDeTeste.situacaoRosasCompleta();

        situacao.setEscopo(EscopoDaSituacao.FORA_DO_ESCOPO_ADITIVO);
        situacao.setEstadoInicial(null);
        assertNull(situacao.getEstadoInicial());
        assertEquals(EscopoDaSituacao.FORA_DO_ESCOPO_ADITIVO, situacao.getEscopo());

        assertThrows(IllegalArgumentException.class, () -> situacao.setEscopo(EscopoDaSituacao.ADITIVO));
        assertEquals(EscopoDaSituacao.FORA_DO_ESCOPO_ADITIVO, situacao.getEscopo());
    }

    @Test
    @DisplayName("adicionarAmbiguidade impede promoção e removerAmbiguidade inexistente lança IllegalArgumentException")
    void ambiguidadeImpedePromocaoEValidacaoDeRemocao() {
        SituacaoProblema situacao = SituacoesDeTeste.situacaoRosasCompleta();

        situacao.adicionarAmbiguidade("2 rosas", "não especifica se são do mesmo buquê");
        assertThrows(IllegalStateException.class, () -> situacao.promoverParaValidada("Prof"));

        AmbiguidadeDeclarada inexistente = new AmbiguidadeDeclarada("trecho", "motivo");
        assertThrows(IllegalArgumentException.class, () -> situacao.removerAmbiguidade(inexistente));
        assertThrows(IllegalArgumentException.class, () -> situacao.removerAmbiguidade(null));

        AmbiguidadeDeclarada cadastrada = situacao.getAmbiguidades().get(0);
        situacao.removerAmbiguidade(cadastrada);
        assertTrue(situacao.getAmbiguidades().isEmpty());

        situacao.promoverParaValidada("Prof");
        assertEquals(StatusCuradoria.VALIDADA, situacao.getStatus());
    }
}
