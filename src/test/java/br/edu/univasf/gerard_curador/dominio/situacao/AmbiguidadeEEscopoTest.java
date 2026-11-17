package br.edu.univasf.gerard_curador.dominio.situacao;

import br.edu.univasf.gerard_curador.dominio.validacao.ResultadoValidacao;
import br.edu.univasf.gerard_curador.dominio.validacao.TipoDiagnostico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Ambiguidade declarada e escopo da situação")
class AmbiguidadeEEscopoTest {

    @Test
    @DisplayName("Situação com ambiguidade gera ENUNCIADO_AMBIGUO, não cria transformações automáticas e recusa promoção (Caso 18)")
    void ambiguidadeGeraDiagnosticoNaoCriaTransformacoesERecusaPromocao() {
        String trechoAmbiguo = "Vovó deu 4 rosas para as netinhas";
        String motivoAmbiguo = "Não especifica quantas rosas cada netinha recebeu";

        SituacaoProblema situacao = SituacaoProblema.construtor(
                "situacao-ambigua",
                "Vovó tinha rosas e deu 4 para as netinhas."
        )
                .comCatalogo(SituacoesDeTeste.catalogoDasRosas())
                .comEstadoInicial(SituacoesDeTeste.estadoInicialDasRosas())
                .comAmbiguidade(trechoAmbiguo, motivoAmbiguo)
                .comIncognita(SituacoesDeTeste.incognitaDasRosas())
                .construir();

        // 1. Nenhuma transformação é deduzida ou criada automaticamente
        assertTrue(situacao.getTransformacoes().isEmpty(),
                "O sistema nunca escolhe uma leitura ou cria transformações sozinho diante de ambiguidade");

        // 2. Diagnóstico ENUNCIADO_AMBIGUO é emitido na validação
        ResultadoValidacao resultado = situacao.validar();
        assertTrue(resultado.contem(TipoDiagnostico.ENUNCIADO_AMBIGUO));
        List<AmbiguidadeDeclarada> ambiguidades = situacao.getAmbiguidades();
        assertEquals(1, ambiguidades.size());
        assertEquals(trechoAmbiguo, ambiguidades.get(0).getTrecho());
        assertEquals(motivoAmbiguo, ambiguidades.get(0).getMotivo());

        // 3. A promoção ao catálogo validado é recusada mesmo se o revisor for informado
        IllegalStateException excecao = assertThrows(IllegalStateException.class,
                () -> situacao.promoverParaValidada("Prof. Gerard"));

        assertTrue(excecao.getMessage().contains("situacao com ambiguidade pendente nao pode ser promovida"));
        assertEquals(StatusCuradoria.CANDIDATA_NAO_CURADA, situacao.getStatus());
    }

    @Test
    @DisplayName("rotuladaForaDoEscopo não exige estado inicial, diagnostica SITUACAO_FORA_DO_ESCOPO_ADITIVO e recusa cálculo")
    void rotuladaForaDoEscopoAditivoNaoExigeEstadoInicialELancaExcecaoAoCalcular() {
        // Constrói sem fornecer estado inicial
        SituacaoProblema multiplicativa = SituacaoProblema.construtor(
                "situacao-multiplicativa",
                "Comprei 3 pacotes com 4 balas cada."
        )
                .comCatalogo(SituacoesDeTeste.catalogoDasRosas())
                .rotuladaForaDoEscopo()
                .construir();

        assertEquals(EscopoDaSituacao.FORA_DO_ESCOPO_ADITIVO, multiplicativa.getEscopo());
        assertNull(multiplicativa.getEstadoInicial(), "Situação fora do escopo aditivo não exige estado inicial");

        // Validação relata diagnóstico específico e para sem aplicar regras aditivas
        ResultadoValidacao resultado = multiplicativa.validar();
        assertFalse(resultado.ehValido());
        assertTrue(resultado.contem(TipoDiagnostico.SITUACAO_FORA_DO_ESCOPO_ADITIVO));

        // calcularEstadoFinal lança IllegalStateException para não forçar o modelo aditivo
        IllegalStateException erroCalculo = assertThrows(IllegalStateException.class,
                multiplicativa::calcularEstadoFinal);
        assertTrue(erroCalculo.getMessage().contains("fora do escopo aditivo nao e calculada nem narrada"));

        // projetarSequenciaNarrativa também recusa
        IllegalStateException erroNarrativa = assertThrows(IllegalStateException.class,
                multiplicativa::projetarSequenciaNarrativa);
        assertTrue(erroNarrativa.getMessage().contains("fora do escopo aditivo nao e calculada nem narrada"));
    }

    @Test
    @DisplayName("Classe de valor AmbiguidadeDeclarada valida parâmetros e formata descrição")
    void ambiguidadeDeclaradaValidaParametrosEFormata() {
        assertThrows(IllegalArgumentException.class, () -> new AmbiguidadeDeclarada(null, "motivo"));
        assertThrows(IllegalArgumentException.class, () -> new AmbiguidadeDeclarada("  ", "motivo"));
        assertThrows(IllegalArgumentException.class, () -> new AmbiguidadeDeclarada("trecho", null));
        assertThrows(IllegalArgumentException.class, () -> new AmbiguidadeDeclarada("trecho", "  "));

        AmbiguidadeDeclarada ambiguidade1 = new AmbiguidadeDeclarada("trecho A", "motivo A");
        AmbiguidadeDeclarada ambiguidade2 = new AmbiguidadeDeclarada("trecho A", "motivo A");

        assertEquals("\"trecho A\": motivo A", ambiguidade1.descrever());
        assertEquals(ambiguidade1.descrever(), ambiguidade1.toString());
        assertEquals(ambiguidade1, ambiguidade2);
        assertEquals(ambiguidade1.hashCode(), ambiguidade2.hashCode());
    }

    @Test
    @DisplayName("Enum EscopoDaSituacao define corretamente a permissão para modelo aditivo")
    void escopoDaSituacaoPermiteModeloAditivo() {
        assertTrue(EscopoDaSituacao.ADITIVO.permiteModeloAditivo());
        assertEquals("aditivo", EscopoDaSituacao.ADITIVO.getNomeExibido());

        assertFalse(EscopoDaSituacao.FORA_DO_ESCOPO_ADITIVO.permiteModeloAditivo());
        assertEquals("fora do escopo aditivo", EscopoDaSituacao.FORA_DO_ESCOPO_ADITIVO.getNomeExibido());
    }
}
