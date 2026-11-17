package br.edu.univasf.gerard_curador.dominio.validacao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de unidade para TipoDiagnostico")
class TipoDiagnosticoTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "PARTICIPANTE_INEXISTENTE",
            "OBJETO_CONTADO_INEXISTENTE",
            "TRANSFORMACAO_SEM_ATOR",
            "ORDEM_TEMPORAL_REPETIDA",
            "QUANTIDADE_INDISPONIVEL_PARA_RETIRADA",
            "GRANDEZAS_OU_UNIDADES_INCOMPATIVEIS",
            "INCOGNITA_ORIGINAL_NAO_DESIGNADA",
            "ESTADO_CALCULADO_DIVERGENTE",
            "QUANTIDADE_POR_DESTINATARIO_AMBIGUA",
            "PARTICIPANTE_INCLUIDO_NO_TOTAL_NAO_DEFINIDO",
            "PAPEL_DESCONHECIDO_NAO_INFORMADO"
    })
    @DisplayName("Deve conter no enum todos os tipos de diagnósticos da seção 11.1")
    void deveConterDiagnosticosObrigatoriosDaSecao11(String nomeEnum) {
        assertDoesNotThrow(() -> {
            TipoDiagnostico tipo = TipoDiagnostico.valueOf(nomeEnum);
            assertNotNull(tipo);
        }, "O tipo de diagnóstico " + nomeEnum + " deve existir no enum");
    }

    @Test
    @DisplayName("Deve classificar corretamente as severidades dos tipos da seção 11.1")
    void deveClassificarSeveridadesCorretamente() {
        // Erros da seção 11.1
        assertTrue(TipoDiagnostico.PARTICIPANTE_INEXISTENTE.ehErro());
        assertFalse(TipoDiagnostico.PARTICIPANTE_INEXISTENTE.ehAviso());
        assertEquals(TipoDiagnostico.Severidade.ERRO, TipoDiagnostico.PARTICIPANTE_INEXISTENTE.getSeveridade());

        assertTrue(TipoDiagnostico.OBJETO_CONTADO_INEXISTENTE.ehErro());
        assertTrue(TipoDiagnostico.TRANSFORMACAO_SEM_ATOR.ehErro());
        assertTrue(TipoDiagnostico.ORDEM_TEMPORAL_REPETIDA.ehErro());
        assertTrue(TipoDiagnostico.QUANTIDADE_INDISPONIVEL_PARA_RETIRADA.ehErro());
        assertTrue(TipoDiagnostico.GRANDEZAS_OU_UNIDADES_INCOMPATIVEIS.ehErro());
        assertTrue(TipoDiagnostico.INCOGNITA_ORIGINAL_NAO_DESIGNADA.ehErro());
        assertTrue(TipoDiagnostico.ESTADO_CALCULADO_DIVERGENTE.ehErro());
        assertTrue(TipoDiagnostico.PAPEL_DESCONHECIDO_NAO_INFORMADO.ehErro());

        // Avisos da seção 11.1
        assertTrue(TipoDiagnostico.QUANTIDADE_POR_DESTINATARIO_AMBIGUA.ehAviso());
        assertFalse(TipoDiagnostico.QUANTIDADE_POR_DESTINATARIO_AMBIGUA.ehErro());
        assertEquals(TipoDiagnostico.Severidade.AVISO, TipoDiagnostico.QUANTIDADE_POR_DESTINATARIO_AMBIGUA.getSeveridade());

        assertTrue(TipoDiagnostico.PARTICIPANTE_INCLUIDO_NO_TOTAL_NAO_DEFINIDO.ehAviso());
        assertFalse(TipoDiagnostico.PARTICIPANTE_INCLUIDO_NO_TOTAL_NAO_DEFINIDO.ehErro());
        assertEquals(TipoDiagnostico.Severidade.AVISO, TipoDiagnostico.PARTICIPANTE_INCLUIDO_NO_TOTAL_NAO_DEFINIDO.getSeveridade());
    }

    @ParameterizedTest
    @EnumSource(TipoDiagnostico.class)
    @DisplayName("Deve possuir nome exibido preenchido e severidade coerente para todos os valores do enum")
    void devePossuirNomeExibidoESeveridadeCoerente(TipoDiagnostico tipo) {
        assertNotNull(tipo.getNomeExibido());
        assertFalse(tipo.getNomeExibido().isBlank());
        assertNotNull(tipo.getSeveridade());
        assertNotEquals(tipo.ehErro(), tipo.ehAviso(), "Um tipo deve ser exclusivamente erro ou aviso");
    }
}
