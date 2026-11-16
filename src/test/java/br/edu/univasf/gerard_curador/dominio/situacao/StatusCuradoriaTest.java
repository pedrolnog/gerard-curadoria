package br.edu.univasf.gerard_curador.dominio.situacao;

import br.edu.univasf.gerard_curador.dominio.narrativo.MarcadorTemporal;
import br.edu.univasf.gerard_curador.dominio.narrativo.TransformacaoNarrativa;
import br.edu.univasf.gerard_curador.dominio.validacao.ResultadoValidacao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Ciclo de vida e status de curadoria humana")
class StatusCuradoriaTest {

    @Test
    @DisplayName("Situação recém-construída nasce com status CANDIDATA_NAO_CURADA")
    void situacaoNasceComoCandidataNaoCurada() {
        SituacaoProblema situacao = SituacoesDeTeste.situacaoRosasCompleta();

        assertEquals(StatusCuradoria.CANDIDATA_NAO_CURADA, situacao.getStatus());
        assertTrue(situacao.getStatus().aguardaRevisao());
        assertFalse(situacao.getStatus().foiRevisada());
        assertNull(situacao.getObservacoesDaRevisao());
    }

    @Test
    @DisplayName("Apenas validar com sucesso não promove a situação (curadoria é ato humano)")
    void validarNaoPromoveSituacao() {
        SituacaoProblema situacao = SituacoesDeTeste.situacaoRosasCompleta();

        ResultadoValidacao resultado = situacao.validar();

        assertTrue(resultado.ehValido());
        assertEquals(StatusCuradoria.CANDIDATA_NAO_CURADA, situacao.getStatus(),
                "Validar não pode alterar o status da situação");
    }

    @Test
    @DisplayName("promoverParaValidada exige identificação do revisor não nula e não em branco")
    void promoverParaValidadaExigeRevisorNaoVazio() {
        SituacaoProblema situacao = SituacoesDeTeste.situacaoRosasCompleta();

        assertThrows(IllegalArgumentException.class, () -> situacao.promoverParaValidada(null));
        assertThrows(IllegalArgumentException.class, () -> situacao.promoverParaValidada(""));
        assertThrows(IllegalArgumentException.class, () -> situacao.promoverParaValidada("   "));
    }

    @Test
    @DisplayName("promoverParaValidada promove situação válida gerando nova instância (imutabilidade)")
    void promoveSituacaoValidaEPreservaOriginal() {
        SituacaoProblema original = SituacoesDeTeste.situacaoRosasCompleta();

        SituacaoProblema promovida = original.promoverParaValidada("Prof. Gerard");

        assertNotSame(original, promovida);
        assertEquals(StatusCuradoria.VALIDADA, promovida.getStatus());
        assertTrue(promovida.getStatus().foiRevisada());
        assertFalse(promovida.getStatus().aguardaRevisao());
        assertEquals("revisada por Prof. Gerard", promovida.getObservacoesDaRevisao());

        // A original deve permanecer intacta com status CANDIDATA_NAO_CURADA
        assertEquals(StatusCuradoria.CANDIDATA_NAO_CURADA, original.getStatus(),
                "A situação original deve continuar candidata");
        assertNull(original.getObservacoesDaRevisao());
    }

    @Test
    @DisplayName("promoverParaValidada recusa situação inválida lançando IllegalStateException")
    void recusaPromocaoDeSituacaoInvalidaComIllegalStateException() {
        // Situação inválida por ordens temporais repetidas
        TransformacaoNarrativa t1 = SituacoesDeTeste.t1DasRosas();
        TransformacaoNarrativa t2MesmaOrdem = new TransformacaoNarrativa(
                SituacoesDeTeste.VOVO,
                SituacoesDeTeste.ACAO_DAR,
                SituacoesDeTeste.NETINHA,
                SituacoesDeTeste.ROSA_AMARELA.contar(1),
                MarcadorTemporal.de(1, "mesma ordem")
        );

        SituacaoProblema situacaoInvalida = SituacaoProblema.construtor("invalida", "Enunciado")
                .comCatalogo(SituacoesDeTeste.catalogoDasRosas())
                .comEstadoInicial(SituacoesDeTeste.estadoInicialDasRosas())
                .comTransformacao(t1)
                .comTransformacao(t2MesmaOrdem)
                .comIncognita(SituacoesDeTeste.incognitaDasRosas())
                .construir();

        IllegalStateException excecao = assertThrows(IllegalStateException.class,
                () -> situacaoInvalida.promoverParaValidada("Prof. Gerard"));

        assertTrue(excecao.getMessage().contains("situacao com erros de validacao nao pode ser promovida"));
        assertEquals(StatusCuradoria.CANDIDATA_NAO_CURADA, situacaoInvalida.getStatus());
    }

    @Test
    @DisplayName("recusar exige revisor e motivo e produz nova situação com status RECUSADA")
    void recusarSituacaoExigeParametrosEAlteraStatusPreservandoOriginal() {
        SituacaoProblema original = SituacoesDeTeste.situacaoRosasCompleta();

        assertThrows(IllegalArgumentException.class, () -> original.recusar(null, "motivo"));
        assertThrows(IllegalArgumentException.class, () -> original.recusar("   ", "motivo"));
        assertThrows(IllegalArgumentException.class, () -> original.recusar("Revisor", null));
        assertThrows(IllegalArgumentException.class, () -> original.recusar("Revisor", "   "));

        SituacaoProblema recusada = original.recusar("Prof. Gerard", "Faltou contextualização");

        assertNotSame(original, recusada);
        assertEquals(StatusCuradoria.RECUSADA, recusada.getStatus());
        assertTrue(recusada.getStatus().foiRevisada());
        assertFalse(recusada.getStatus().aguardaRevisao());
        assertEquals("recusada por Prof. Gerard: Faltou contextualização", recusada.getObservacoesDaRevisao());

        // A original continua intacta
        assertEquals(StatusCuradoria.CANDIDATA_NAO_CURADA, original.getStatus());
    }

    @Test
    @DisplayName("Constantes do enum StatusCuradoria possuem nomes exibidos coerentes")
    void nomesExibidosDoStatusCuradoria() {
        assertEquals("candidata nao curada", StatusCuradoria.CANDIDATA_NAO_CURADA.getNomeExibido());
        assertEquals("validada", StatusCuradoria.VALIDADA.getNomeExibido());
        assertEquals("recusada", StatusCuradoria.RECUSADA.getNomeExibido());
    }
}
