package br.edu.univasf.gerard_curador.persistencia;

import br.edu.univasf.gerard_curador.dominio.repositorio.Repositorio;
import br.edu.univasf.gerard_curador.dominio.situacao.EscopoDaSituacao;
import br.edu.univasf.gerard_curador.dominio.situacao.SituacaoProblema;
import br.edu.univasf.gerard_curador.dominio.situacao.StatusCuradoria;
import br.edu.univasf.gerard_curador.dominio.validacao.ResultadoValidacao;
import br.edu.univasf.gerard_curador.dominio.validacao.TipoDiagnostico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Repositório JSON de Situações-Problema")
class RepositorioSituacaoJsonTest {

    @TempDir
    Path diretorioTemp;

    private Repositorio<SituacaoProblema, String> repositorio;
    private RepositorioSituacaoJson repositorioConcreto;

    @BeforeEach
    void configurar() {
        repositorioConcreto = new RepositorioSituacaoJson(diretorioTemp);
        // O repositorio e usado pela interface Repositorio<SituacaoProblema, String> (inversao de dependencia)
        repositorio = repositorioConcreto;
    }

    @Test
    @DisplayName("Ida e volta preserva id, enunciado, status, transformações, estado final curado, validação, incógnita, relação e cálculo")
    void idaEVoltaPreservaSituacaoDasRosasCompleta() {
        SituacaoProblema original = SituacoesParaPersistencia.situacaoRosasCompleta();

        repositorio.salvar(original);

        Optional<SituacaoProblema> recarregadaOpt = repositorio.buscarPorId(original.getId());
        assertTrue(recarregadaOpt.isPresent(), "A situação salva deve ser encontrada pelo id");

        SituacaoProblema recarregada = recarregadaOpt.get();

        assertEquals(original.getId(), recarregada.getId(), "Id deve ser preservado");
        assertEquals(original.getEnunciado(), recarregada.getEnunciado(), "Enunciado deve ser preservado");
        assertEquals(StatusCuradoria.CANDIDATA_NAO_CURADA, recarregada.getStatus(), "Status deve ser CANDIDATA_NAO_CURADA");

        assertEquals(original.getTransformacoes().size(), recarregada.getTransformacoes().size(),
                "Quantidade de transformações deve ser a mesma");
        for (int i = 0; i < original.getTransformacoes().size(); i++) {
            var tOriginal = original.getTransformacoes().get(i);
            var tRecarregada = recarregada.getTransformacoes().get(i);
            assertEquals(tOriginal.getOrdem(), tRecarregada.getOrdem(), "Ordem temporal deve ser preservada");
            assertEquals(tOriginal.getAcao().getId(), tRecarregada.getAcao().getId(), "Ação deve ser a mesma");
            assertEquals(tOriginal.getAtor().getId(), tRecarregada.getAtor().getId(), "Ator deve ser o mesmo");
            assertEquals(tOriginal.possuiDestinatario(), tRecarregada.possuiDestinatario(), "Presença de destinatário");
            if (tOriginal.possuiDestinatario()) {
                assertEquals(tOriginal.getDestinatario().getId(), tRecarregada.getDestinatario().getId());
            }
            assertEquals(tOriginal.getQuantidade().getValor(), tRecarregada.getQuantidade().getValor());
        }

        assertNotNull(recarregada.getEstadoFinalCurado(), "Estado final curado deve estar presente");
        assertEquals(original.getEstadoFinalCurado().getPossuidor().getId(),
                recarregada.getEstadoFinalCurado().getPossuidor().getId());
        assertTrue(recarregada.getEstadoFinalCurado().getInventario()
                        .possuiMesmoConteudoQue(original.getEstadoFinalCurado().getInventario()),
                "Estado final curado deve ter o mesmo inventário");

        assertEquals(original.validar().ehValido(), recarregada.validar().ehValido(),
                "Validação deve produzir o mesmo resultado de validade");
        assertTrue(recarregada.validar().ehValido(), "Situação das rosas recarregada deve ser válida");

        assertNotNull(recarregada.getIncognita(), "Incógnita deve ser preservada");
        assertEquals(original.getIncognita().getPapelOriginal(), recarregada.getIncognita().getPapelOriginal(),
                "Papel da incógnita deve ser o mesmo");
        assertEquals(original.getIncognita().getDescricaoDaPergunta(),
                recarregada.getIncognita().getDescricaoDaPergunta());

        assertNotNull(recarregada.getRelacaoEstrutural(), "Relação estrutural deve estar presente");
        assertEquals(original.getRelacaoEstrutural().getCategoria(),
                recarregada.getRelacaoEstrutural().getCategoria(),
                "Categoria da relação estrutural deve ser a mesma");

        assertTrue(recarregada.calcularEstadoFinal().getInventario()
                        .possuiMesmoConteudoQue(original.calcularEstadoFinal().getInventario()),
                "calcularEstadoFinal deve produzir inventário idêntico");
    }

    @Test
    @DisplayName("Serialização contém status CANDIDATA_NAO_CURADA, snake_case e não contém regras nem a palavra 'calculado'")
    void serializacaoContemChavesSnakeCaseStatusENaoContemRegras() {
        SituacaoProblema rosas = SituacoesParaPersistencia.situacaoRosasCompleta();

        String json = repositorioConcreto.serializar(rosas);

        assertTrue(json.contains("\"status\": \"CANDIDATA_NAO_CURADA\""),
                "JSON deve conter o status CANDIDATA_NAO_CURADA");
        assertTrue(json.contains("\"estado_inicial\""),
                "JSON deve usar snake_case para 'estado_inicial'");
        assertTrue(json.contains("\"objetos_contados\""),
                "JSON deve usar snake_case para 'objetos_contados'");
        assertFalse(json.toLowerCase().contains("calculado"),
                "JSON não deve conter palavras de regras como 'calculado'");
    }

    @Test
    @DisplayName("Estado final curado divergente é preservado na leitura sem recálculo e com diagnóstico ESTADO_CALCULADO_DIVERGENTE")
    void estadoFinalCuradoDivergentePreservadoSemRecalculo() {
        SituacaoProblema divergente = SituacoesParaPersistencia.situacaoComEstadoFinalDivergente();

        repositorio.salvar(divergente);

        SituacaoProblema recarregada = repositorio.buscarPorId(divergente.getId())
                .orElseThrow(() -> new AssertionError("Deveria encontrar a situação divergente"));

        assertTrue(recarregada.getEstadoFinalCurado().getInventario()
                        .possuiMesmoConteudoQue(divergente.getEstadoFinalCurado().getInventario()),
                "O estado final curado lido deve ser exatamente o divergente salvo, sem ser recalculado");

        assertFalse(recarregada.calcularEstadoFinal().getInventario()
                        .possuiMesmoConteudoQue(recarregada.getEstadoFinalCurado().getInventario()),
                "O cálculo das transformações difere do estado final curado");

        ResultadoValidacao resultado = recarregada.validar();
        assertFalse(resultado.ehValido(), "Situação com estado divergente não deve ser válida");
        assertTrue(resultado.contem(TipoDiagnostico.ESTADO_CALCULADO_DIVERGENTE),
                "Recarregada continua apontando ESTADO_CALCULADO_DIVERGENTE");
    }

    @Test
    @DisplayName("Situação promovida para VALIDADA sobrevive à ida e volta com status VALIDADA")
    void situacaoPromovidaParaValidadaVoltaValidada() {
        SituacaoProblema situacao = SituacoesParaPersistencia.situacaoRosasCompleta();
        situacao.promoverParaValidada("Curador Responsável");
        assertEquals(StatusCuradoria.VALIDADA, situacao.getStatus());

        repositorio.salvar(situacao);

        SituacaoProblema recarregada = repositorio.buscarPorId(situacao.getId())
                .orElseThrow(() -> new AssertionError("Deveria encontrar a situação promovida"));

        assertEquals(StatusCuradoria.VALIDADA, recarregada.getStatus(),
                "Situação promovida deve voltar com status VALIDADA");
        assertEquals("revisada por Curador Responsável", recarregada.getObservacoesDaRevisao(),
                "Observações da revisão devem ser preservadas");
    }

    @Test
    @DisplayName("Situação rotulada fora do escopo aditivo sobrevive à ida e volta")
    void situacaoForaDoEscopoSobreviveAIdaEVolta() {
        SituacaoProblema foraEscopo = SituacoesParaPersistencia.situacaoForaDoEscopo();

        repositorio.salvar(foraEscopo);

        SituacaoProblema recarregada = repositorio.buscarPorId(foraEscopo.getId())
                .orElseThrow(() -> new AssertionError("Deveria encontrar a situação fora do escopo"));

        assertEquals(EscopoDaSituacao.FORA_DO_ESCOPO_ADITIVO, recarregada.getEscopo(),
                "Escopo fora do aditivo deve ser preservado");
        assertTrue(recarregada.validar().contem(TipoDiagnostico.SITUACAO_FORA_DO_ESCOPO_ADITIVO),
                "Validação deve apontar diagnóstico SITUACAO_FORA_DO_ESCOPO_ADITIVO");
    }

    @Test
    @DisplayName("Ambiguidade declarada sobrevive à ida e volta")
    void ambiguidadeDeclaradaSobreviveAIdaEVolta() {
        SituacaoProblema ambigua = SituacoesParaPersistencia.situacaoComAmbiguidadeDeclarada();

        repositorio.salvar(ambigua);

        SituacaoProblema recarregada = repositorio.buscarPorId(ambigua.getId())
                .orElseThrow(() -> new AssertionError("Deveria encontrar a situação ambígua"));

        assertEquals(1, recarregada.getAmbiguidades().size(), "Deve conter 1 ambiguidade declarada");
        assertEquals("deu 1 branca", recarregada.getAmbiguidades().get(0).getTrecho());
        assertEquals("momento da doação não explicitado no enunciado",
                recarregada.getAmbiguidades().get(0).getMotivo());
        assertTrue(recarregada.validar().contem(TipoDiagnostico.ENUNCIADO_AMBIGUO),
                "Validação deve apontar ENUNCIADO_AMBIGUO");
    }

    @Test
    @DisplayName("JSON com transformação referindo participante inexistente lança ErroDeLeituraJson")
    void jsonComTransformacaoReferindoParticipanteInexistenteLancaErroDeLeituraJson() throws IOException {
        String jsonValido = repositorioConcreto.serializar(SituacoesParaPersistencia.situacaoRosasCompleta());
        String jsonComParticipanteInexistente = jsonValido.replace(
                "\"ator\": \"vovo\"",
                "\"ator\": \"participante_fantasma\""
        );

        assertThrows(ErroDeLeituraJson.class,
                () -> repositorioConcreto.desserializar(jsonComParticipanteInexistente),
                "Desserializar participante inexistente na transformação deve lançar ErroDeLeituraJson");

        Files.writeString(diretorioTemp.resolve("situacao-fantasma.json"), jsonComParticipanteInexistente);
        assertThrows(ErroDeLeituraJson.class,
                () -> repositorio.buscarPorId("situacao-fantasma"),
                "buscarPorId de arquivo com participante inexistente deve lançar ErroDeLeituraJson");
    }

    @Test
    @DisplayName("JSON malformado lança ErroDeLeituraJson")
    void jsonMalformadoLancaErroDeLeituraJson() throws IOException {
        String jsonMalformado = "{ \"id\": \"situacao-invalida\", ";

        assertThrows(ErroDeLeituraJson.class,
                () -> repositorioConcreto.desserializar(jsonMalformado),
                "Desserializar JSON malformado deve lançar ErroDeLeituraJson");

        Files.writeString(diretorioTemp.resolve("situacao-malformada.json"), jsonMalformado);
        assertThrows(ErroDeLeituraJson.class,
                () -> repositorio.buscarPorId("situacao-malformada"),
                "buscarPorId de arquivo com JSON malformado deve lançar ErroDeLeituraJson");
    }

    @Test
    @DisplayName("buscarPorId inexistente retorna Optional.empty e aceita valores nulos ou vazios")
    void buscarPorIdInexistenteRetornaOptionalEmpty() {
        assertTrue(repositorio.buscarPorId("id_que_nao_existe").isEmpty(),
                "Buscar por ID inexistente deve retornar Optional.empty");
        assertTrue(repositorio.buscarPorId(null).isEmpty(),
                "Buscar por ID nulo deve retornar Optional.empty");
        assertTrue(repositorio.buscarPorId("   ").isEmpty(),
                "Buscar por ID em branco deve retornar Optional.empty");
    }

    @Test
    @DisplayName("listarTodos lista situações salvas e remover exclui arquivo com sucesso")
    void listarTodosERemoverFuncionamCorretamente() {
        assertTrue(repositorio.listarTodos().isEmpty(), "Repositório inicia vazio");

        SituacaoProblema rosas = SituacoesParaPersistencia.situacaoRosasCompleta();
        SituacaoProblema foraEscopo = SituacoesParaPersistencia.situacaoForaDoEscopo();

        repositorio.salvar(rosas);
        repositorio.salvar(foraEscopo);

        List<SituacaoProblema> todas = repositorio.listarTodos();
        assertEquals(2, todas.size(), "Devem existir 2 situações cadastradas");

        assertFalse(repositorio.remover("id_inexistente"), "Remover id inexistente deve retornar false");
        assertFalse(repositorio.remover(null), "Remover id nulo deve retornar false");
        assertFalse(repositorio.remover("  "), "Remover id vazio deve retornar false");

        assertTrue(repositorio.remover(rosas.getId()), "Remover situação existente deve retornar true");
        assertTrue(repositorio.buscarPorId(rosas.getId()).isEmpty(),
                "Situação removida não deve mais ser encontrada");

        List<SituacaoProblema> restantes = repositorio.listarTodos();
        assertEquals(1, restantes.size(), "Deve restar apenas 1 situação");
        assertEquals(foraEscopo.getId(), restantes.get(0).getId());
    }
}
