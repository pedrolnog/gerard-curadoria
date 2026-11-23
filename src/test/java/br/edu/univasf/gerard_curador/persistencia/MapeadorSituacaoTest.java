package br.edu.univasf.gerard_curador.persistencia;

import br.edu.univasf.gerard_curador.dominio.estrutural.ComparacaoQuantitativa;
import br.edu.univasf.gerard_curador.dominio.estrutural.ComposicaoParteTodo;
import br.edu.univasf.gerard_curador.dominio.situacao.EscopoDaSituacao;
import br.edu.univasf.gerard_curador.dominio.situacao.SituacaoProblema;
import br.edu.univasf.gerard_curador.dominio.situacao.StatusCuradoria;
import br.edu.univasf.gerard_curador.dominio.validacao.TipoDiagnostico;
import br.edu.univasf.gerard_curador.persistencia.dto.AcaoDto;
import br.edu.univasf.gerard_curador.persistencia.dto.AmbiguidadeDto;
import br.edu.univasf.gerard_curador.persistencia.dto.EstadoDto;
import br.edu.univasf.gerard_curador.persistencia.dto.QuantidadeDto;
import br.edu.univasf.gerard_curador.persistencia.dto.RelacaoDto;
import br.edu.univasf.gerard_curador.persistencia.dto.SituacaoDto;
import br.edu.univasf.gerard_curador.persistencia.dto.TransformacaoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Mapeador de Situação-Problema entre Domínio e DTO")
class MapeadorSituacaoTest {

    private MapeadorSituacao mapeador;

    @BeforeEach
    void configurar() {
        mapeador = new MapeadorSituacao();
    }

    @Test
    @DisplayName("Ida e volta completa entre domínio e DTO preserva dados da situação das rosas")
    void idaEVoltaPreservaDadosCompletosDasRosas() {
        SituacaoProblema original = SituacoesParaPersistencia.situacaoRosasCompleta();

        SituacaoDto dto = mapeador.paraDto(original);
        assertNotNull(dto);
        assertEquals(original.getId(), dto.getId());
        assertEquals("COMPOSICAO_TRANSFORMACOES", dto.getCategoria());
        assertEquals(StatusCuradoria.CANDIDATA_NAO_CURADA.name(), dto.getStatus());

        SituacaoProblema recuperada = mapeador.paraDominio(dto);
        assertNotNull(recuperada);
        assertEquals(original.getId(), recuperada.getId());
        assertEquals(original.getEnunciado(), recuperada.getEnunciado());
        assertEquals(original.getStatus(), recuperada.getStatus());
        assertEquals(original.getTransformacoes().size(), recuperada.getTransformacoes().size());
        assertTrue(recuperada.validar().ehValido());
        assertEquals(original.getIncognita().getPapelOriginal(), recuperada.getIncognita().getPapelOriginal());
        assertNotNull(recuperada.getRelacaoEstrutural());
        assertEquals(original.getRelacaoEstrutural().getCategoria(), recuperada.getRelacaoEstrutural().getCategoria());
        assertTrue(recuperada.calcularEstadoFinal().getInventario()
                .possuiMesmoConteudoQue(original.calcularEstadoFinal().getInventario()));
    }

    @Test
    @DisplayName("paraDto com situação nula lança IllegalArgumentException")
    void paraDtoComSituacaoNulaLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> mapeador.paraDto(null));
    }

    @Test
    @DisplayName("paraDominio com DTO nulo lança ErroDeLeituraJson")
    void paraDominioComDtoNuloLancaErroDeLeituraJson() {
        assertThrows(ErroDeLeituraJson.class, () -> mapeador.paraDominio(null));
    }

    @Test
    @DisplayName("DTO com id ausente ou vazio lança ErroDeLeituraJson")
    void dtoComIdAusenteLancaErroDeLeituraJson() {
        SituacaoDto base = mapeador.paraDto(SituacoesParaPersistencia.situacaoRosasCompleta());

        SituacaoDto dtoIdNulo = new SituacaoDto(
                null, base.getEnunciado(), base.getCategoria(), base.getEscopo(),
                base.getParticipantes(), base.getObjetosContados(), base.getContexto(),
                base.getIncognita(), base.getEstadoInicial(), base.getTransformacoes(),
                base.getEstadoFinalCurado(), base.getRelacao(), base.getAmbiguidades(),
                base.getStatus(), base.getRevisor(), base.getMotivoRecusa());
        assertThrows(ErroDeLeituraJson.class, () -> mapeador.paraDominio(dtoIdNulo));

        SituacaoDto dtoIdVazio = new SituacaoDto(
                "   ", base.getEnunciado(), base.getCategoria(), base.getEscopo(),
                base.getParticipantes(), base.getObjetosContados(), base.getContexto(),
                base.getIncognita(), base.getEstadoInicial(), base.getTransformacoes(),
                base.getEstadoFinalCurado(), base.getRelacao(), base.getAmbiguidades(),
                base.getStatus(), base.getRevisor(), base.getMotivoRecusa());
        assertThrows(ErroDeLeituraJson.class, () -> mapeador.paraDominio(dtoIdVazio));
    }

    @Test
    @DisplayName("DTO com enunciado ausente ou vazio lança ErroDeLeituraJson")
    void dtoComEnunciadoAusenteLancaErroDeLeituraJson() {
        SituacaoDto base = mapeador.paraDto(SituacoesParaPersistencia.situacaoRosasCompleta());

        SituacaoDto dtoEnunciadoVazio = new SituacaoDto(
                base.getId(), "  ", base.getCategoria(), base.getEscopo(),
                base.getParticipantes(), base.getObjetosContados(), base.getContexto(),
                base.getIncognita(), base.getEstadoInicial(), base.getTransformacoes(),
                base.getEstadoFinalCurado(), base.getRelacao(), base.getAmbiguidades(),
                base.getStatus(), base.getRevisor(), base.getMotivoRecusa());
        assertThrows(ErroDeLeituraJson.class, () -> mapeador.paraDominio(dtoEnunciadoVazio));
    }

    @Test
    @DisplayName("DTO com participantes ausente lança ErroDeLeituraJson")
    void dtoComParticipantesAusenteLancaErroDeLeituraJson() {
        SituacaoDto base = mapeador.paraDto(SituacoesParaPersistencia.situacaoRosasCompleta());

        SituacaoDto dtoSemParticipantes = new SituacaoDto(
                base.getId(), base.getEnunciado(), base.getCategoria(), base.getEscopo(),
                null, base.getObjetosContados(), base.getContexto(),
                base.getIncognita(), base.getEstadoInicial(), base.getTransformacoes(),
                base.getEstadoFinalCurado(), base.getRelacao(), base.getAmbiguidades(),
                base.getStatus(), base.getRevisor(), base.getMotivoRecusa());
        assertThrows(ErroDeLeituraJson.class, () -> mapeador.paraDominio(dtoSemParticipantes));
    }

    @Test
    @DisplayName("DTO com objetos contados ausente lança ErroDeLeituraJson")
    void dtoComObjetosContadosAusenteLancaErroDeLeituraJson() {
        SituacaoDto base = mapeador.paraDto(SituacoesParaPersistencia.situacaoRosasCompleta());

        SituacaoDto dtoSemObjetos = new SituacaoDto(
                base.getId(), base.getEnunciado(), base.getCategoria(), base.getEscopo(),
                base.getParticipantes(), null, base.getContexto(),
                base.getIncognita(), base.getEstadoInicial(), base.getTransformacoes(),
                base.getEstadoFinalCurado(), base.getRelacao(), base.getAmbiguidades(),
                base.getStatus(), base.getRevisor(), base.getMotivoRecusa());
        assertThrows(ErroDeLeituraJson.class, () -> mapeador.paraDominio(dtoSemObjetos));
    }

    @Test
    @DisplayName("DTO com estado inicial ausente em escopo aditivo lança ErroDeLeituraJson")
    void dtoComEstadoInicialAusenteEmEscopoAditivoLancaErroDeLeituraJson() {
        SituacaoDto base = mapeador.paraDto(SituacoesParaPersistencia.situacaoRosasCompleta());

        SituacaoDto dtoSemEstadoInicial = new SituacaoDto(
                base.getId(), base.getEnunciado(), base.getCategoria(), "ADITIVO",
                base.getParticipantes(), base.getObjetosContados(), base.getContexto(),
                base.getIncognita(), null, base.getTransformacoes(),
                base.getEstadoFinalCurado(), base.getRelacao(), base.getAmbiguidades(),
                base.getStatus(), base.getRevisor(), base.getMotivoRecusa());
        assertThrows(ErroDeLeituraJson.class, () -> mapeador.paraDominio(dtoSemEstadoInicial));
    }

    @Test
    @DisplayName("DTO com transformação referindo participante inexistente lança ErroDeLeituraJson")
    void dtoComTransformacaoReferindoParticipanteInexistenteLancaErroDeLeituraJson() {
        SituacaoDto base = mapeador.paraDto(SituacoesParaPersistencia.situacaoRosasCompleta());
        TransformacaoDto tOriginal = base.getTransformacoes().get(0);
        TransformacaoDto tInvalida = new TransformacaoDto(
                tOriginal.getOrdem(), tOriginal.getMarcador(), "participante_inexistente",
                tOriginal.getDestinatario(), tOriginal.getAcao(), tOriginal.getQuantidade());

        SituacaoDto dtoInvalido = new SituacaoDto(
                base.getId(), base.getEnunciado(), base.getCategoria(), base.getEscopo(),
                base.getParticipantes(), base.getObjetosContados(), base.getContexto(),
                base.getIncognita(), base.getEstadoInicial(), List.of(tInvalida),
                base.getEstadoFinalCurado(), base.getRelacao(), base.getAmbiguidades(),
                base.getStatus(), base.getRevisor(), base.getMotivoRecusa());

        assertThrows(ErroDeLeituraJson.class, () -> mapeador.paraDominio(dtoInvalido));
    }

    @Test
    @DisplayName("DTO com transformação referindo destinatário inexistente lança ErroDeLeituraJson")
    void dtoComTransformacaoReferindoDestinatarioInexistenteLancaErroDeLeituraJson() {
        SituacaoDto base = mapeador.paraDto(SituacoesParaPersistencia.situacaoRosasCompleta());
        TransformacaoDto tOriginal = base.getTransformacoes().get(0);
        TransformacaoDto tInvalida = new TransformacaoDto(
                tOriginal.getOrdem(), tOriginal.getMarcador(), tOriginal.getAtor(),
                "destinatario_inexistente", tOriginal.getAcao(), tOriginal.getQuantidade());

        SituacaoDto dtoInvalido = new SituacaoDto(
                base.getId(), base.getEnunciado(), base.getCategoria(), base.getEscopo(),
                base.getParticipantes(), base.getObjetosContados(), base.getContexto(),
                base.getIncognita(), base.getEstadoInicial(), List.of(tInvalida),
                base.getEstadoFinalCurado(), base.getRelacao(), base.getAmbiguidades(),
                base.getStatus(), base.getRevisor(), base.getMotivoRecusa());

        assertThrows(ErroDeLeituraJson.class, () -> mapeador.paraDominio(dtoInvalido));
    }

    @Test
    @DisplayName("DTO com estado inicial referindo possuidor inexistente lança ErroDeLeituraJson")
    void dtoComEstadoInicialReferindoPossuidorInexistenteLancaErroDeLeituraJson() {
        SituacaoDto base = mapeador.paraDto(SituacoesParaPersistencia.situacaoRosasCompleta());
        EstadoDto estadoInvalido = new EstadoDto(
                "possuidor_fantasma", base.getEstadoInicial().getOrdemTemporal(),
                base.getEstadoInicial().getRotuloTemporal(), base.getEstadoInicial().getItens());

        SituacaoDto dtoInvalido = new SituacaoDto(
                base.getId(), base.getEnunciado(), base.getCategoria(), base.getEscopo(),
                base.getParticipantes(), base.getObjetosContados(), base.getContexto(),
                base.getIncognita(), estadoInvalido, base.getTransformacoes(),
                base.getEstadoFinalCurado(), base.getRelacao(), base.getAmbiguidades(),
                base.getStatus(), base.getRevisor(), base.getMotivoRecusa());

        assertThrows(ErroDeLeituraJson.class, () -> mapeador.paraDominio(dtoInvalido));
    }

    @Test
    @DisplayName("DTO com quantidade referindo objeto inexistente lança ErroDeLeituraJson")
    void dtoComQuantidadeReferindoObjetoInexistenteLancaErroDeLeituraJson() {
        SituacaoDto base = mapeador.paraDto(SituacoesParaPersistencia.situacaoRosasCompleta());
        QuantidadeDto itemInexistente = new QuantidadeDto(
                "objeto_inexistente", 2, "DISCRETA", "UNIDADE");
        EstadoDto estadoInvalido = new EstadoDto(
                base.getEstadoInicial().getPossuidor(), base.getEstadoInicial().getOrdemTemporal(),
                base.getEstadoInicial().getRotuloTemporal(), List.of(itemInexistente));

        SituacaoDto dtoInvalido = new SituacaoDto(
                base.getId(), base.getEnunciado(), base.getCategoria(), base.getEscopo(),
                base.getParticipantes(), base.getObjetosContados(), base.getContexto(),
                base.getIncognita(), estadoInvalido, base.getTransformacoes(),
                base.getEstadoFinalCurado(), base.getRelacao(), base.getAmbiguidades(),
                base.getStatus(), base.getRevisor(), base.getMotivoRecusa());

        assertThrows(ErroDeLeituraJson.class, () -> mapeador.paraDominio(dtoInvalido));
    }

    @Test
    @DisplayName("DTO com categoria de relação desconhecida lança ErroDeLeituraJson")
    void dtoComCategoriaDeRelacaoDesconhecidaLancaErroDeLeituraJson() {
        SituacaoDto base = mapeador.paraDto(SituacoesParaPersistencia.situacaoRosasCompleta());
        RelacaoDto relacaoInvalida = new RelacaoDto(
                "rel-invalida", "RELACAO_INVENTADA", Collections.emptyMap(),
                null, null, null, null, null, null, null, null);

        SituacaoDto dtoInvalido = new SituacaoDto(
                base.getId(), base.getEnunciado(), base.getCategoria(), base.getEscopo(),
                base.getParticipantes(), base.getObjetosContados(), base.getContexto(),
                base.getIncognita(), base.getEstadoInicial(), base.getTransformacoes(),
                base.getEstadoFinalCurado(), relacaoInvalida, base.getAmbiguidades(),
                base.getStatus(), base.getRevisor(), base.getMotivoRecusa());

        assertThrows(ErroDeLeituraJson.class, () -> mapeador.paraDominio(dtoInvalido));
    }

    @Test
    @DisplayName("DTO com status desconhecido lança ErroDeLeituraJson")
    void dtoComStatusDesconhecidoLancaErroDeLeituraJson() {
        SituacaoDto base = mapeador.paraDto(SituacoesParaPersistencia.situacaoRosasCompleta());

        SituacaoDto dtoInvalido = new SituacaoDto(
                base.getId(), base.getEnunciado(), base.getCategoria(), base.getEscopo(),
                base.getParticipantes(), base.getObjetosContados(), base.getContexto(),
                base.getIncognita(), base.getEstadoInicial(), base.getTransformacoes(),
                base.getEstadoFinalCurado(), base.getRelacao(), base.getAmbiguidades(),
                "STATUS_INVENTADO", base.getRevisor(), base.getMotivoRecusa());

        assertThrows(ErroDeLeituraJson.class, () -> mapeador.paraDominio(dtoInvalido));
    }

    @Test
    @DisplayName("Preserva estado final divergente sem recálculo")
    void preservaEstadoFinalDivergenteSemRecalculo() {
        SituacaoProblema divergente = SituacoesParaPersistencia.situacaoComEstadoFinalDivergente();

        SituacaoDto dto = mapeador.paraDto(divergente);
        SituacaoProblema reconstruida = mapeador.paraDominio(dto);

        assertTrue(reconstruida.getEstadoFinalCurado().getInventario()
                .possuiMesmoConteudoQue(divergente.getEstadoFinalCurado().getInventario()));
        assertTrue(reconstruida.validar().contem(TipoDiagnostico.ESTADO_CALCULADO_DIVERGENTE));
    }

    @Test
    @DisplayName("Preserva situação promovida para VALIDADA")
    void preservaSituacaoPromovidaParaValidada() {
        SituacaoProblema promovida = SituacoesParaPersistencia.situacaoRosasCompleta();
        promovida.promoverParaValidada("Revisor Humano");

        SituacaoDto dto = mapeador.paraDto(promovida);
        assertEquals(StatusCuradoria.VALIDADA.name(), dto.getStatus());

        SituacaoProblema reconstruida = mapeador.paraDominio(dto);
        assertEquals(StatusCuradoria.VALIDADA, reconstruida.getStatus());
        assertEquals("revisada por Revisor Humano", reconstruida.getObservacoesDaRevisao());
    }

    @Test
    @DisplayName("Preserva situação com status RECUSADA")
    void preservaSituacaoRecusada() {
        SituacaoProblema recusada = SituacoesParaPersistencia.situacaoRosasCompleta();
        recusada.recusar("Revisor Humano", "Enunciado ambíguo demais");

        SituacaoDto dto = mapeador.paraDto(recusada);
        assertEquals(StatusCuradoria.RECUSADA.name(), dto.getStatus());

        SituacaoProblema reconstruida = mapeador.paraDominio(dto);
        assertEquals(StatusCuradoria.RECUSADA, reconstruida.getStatus());
        assertEquals("recusada por Revisor Humano: Enunciado ambíguo demais",
                reconstruida.getObservacoesDaRevisao());
    }

    @Test
    @DisplayName("Mapeia situação fora do escopo sem exigir estado inicial")
    void mapeiaSituacaoForaDoEscopoSemExigirEstadoInicial() {
        SituacaoProblema foraEscopo = SituacoesParaPersistencia.situacaoForaDoEscopo();

        SituacaoDto dto = mapeador.paraDto(foraEscopo);
        assertEquals(EscopoDaSituacao.FORA_DO_ESCOPO_ADITIVO.name(), dto.getEscopo());
        assertNull(dto.getEstadoInicial());

        SituacaoProblema reconstruida = mapeador.paraDominio(dto);
        assertEquals(EscopoDaSituacao.FORA_DO_ESCOPO_ADITIVO, reconstruida.getEscopo());
        assertTrue(reconstruida.validar().contem(TipoDiagnostico.SITUACAO_FORA_DO_ESCOPO_ADITIVO));
    }

    @Test
    @DisplayName("Preserva ambiguidades declaradas na conversão")
    void preservaAmbiguidadesDeclaradas() {
        SituacaoProblema ambigua = SituacoesParaPersistencia.situacaoComAmbiguidadeDeclarada();

        SituacaoDto dto = mapeador.paraDto(ambigua);
        assertEquals(1, dto.getAmbiguidades().size());
        assertEquals("deu 1 branca", dto.getAmbiguidades().get(0).getTrecho());

        SituacaoProblema reconstruida = mapeador.paraDominio(dto);
        assertEquals(1, reconstruida.getAmbiguidades().size());
        assertEquals("deu 1 branca", reconstruida.getAmbiguidades().get(0).getTrecho());
        assertTrue(reconstruida.validar().contem(TipoDiagnostico.ENUNCIADO_AMBIGUO));
    }

    @Test
    @DisplayName("Mapeia situação com relação de Comparação Quantitativa")
    void mapeiaRelacaoComparacaoQuantitativa() {
        ComparacaoQuantitativa relacao = ComparacaoQuantitativa.construtor("rel-comp")
                .comReferente(SituacoesParaPersistencia.NETINHA, SituacoesParaPersistencia.ROSA_BRANCA.contar(6))
                .comReferido(SituacoesParaPersistencia.VOVO, SituacoesParaPersistencia.ROSA_BRANCA.contar(14))
                .comValorRelativo(SituacoesParaPersistencia.ROSA_BRANCA.contar(8), ComparacaoQuantitativa.Direcao.A_MAIS)
                .construir();

        SituacaoProblema situacao = SituacaoProblema.construtor(
                "situacao-comp", "Vovó tem 8 rosas a mais que a Netinha, que tem 6.")
                .comCatalogo(SituacoesParaPersistencia.catalogoDasRosas())
                .comEstadoInicial(SituacoesParaPersistencia.estadoInicialDasRosas())
                .comIncognita(SituacoesParaPersistencia.incognitaDasRosas())
                .comRelacaoEstrutural(relacao)
                .construir();

        SituacaoDto dto = mapeador.paraDto(situacao);
        assertEquals("COMPARACAO", dto.getCategoria());

        SituacaoProblema reconstruida = mapeador.paraDominio(dto);
        assertNotNull(reconstruida.getRelacaoEstrutural());
        assertEquals("COMPARACAO", reconstruida.getRelacaoEstrutural().getCategoria());
    }

    @Test
    @DisplayName("Mapeia situação com relação de Composição Parte-Todo")
    void mapeiaRelacaoComposicaoParteTodo() {
        ComposicaoParteTodo relacao = ComposicaoParteTodo.construtor("rel-parte-todo")
                .comParte(SituacoesParaPersistencia.ROSA_BRANCA.contar(2))
                .comParte(SituacoesParaPersistencia.ROSA_AMARELA.contar(3))
                .comTodo(SituacoesParaPersistencia.ROSA_BRANCA.contar(5))
                .construir();

        SituacaoProblema situacao = SituacaoProblema.construtor(
                "situacao-parte-todo", "No jardim há 2 rosas brancas e 3 amarelas. Quantas ao todo?")
                .comCatalogo(SituacoesParaPersistencia.catalogoDasRosas())
                .comEstadoInicial(SituacoesParaPersistencia.estadoInicialDasRosas())
                .comIncognita(SituacoesParaPersistencia.incognitaDasRosas())
                .comRelacaoEstrutural(relacao)
                .construir();

        SituacaoDto dto = mapeador.paraDto(situacao);
        assertEquals("COMPOSICAO_PARTE_TODO", dto.getCategoria());

        SituacaoProblema reconstruida = mapeador.paraDominio(dto);
        assertNotNull(reconstruida.getRelacaoEstrutural());
        assertEquals("COMPOSICAO_PARTE_TODO", reconstruida.getRelacaoEstrutural().getCategoria());
    }
}
