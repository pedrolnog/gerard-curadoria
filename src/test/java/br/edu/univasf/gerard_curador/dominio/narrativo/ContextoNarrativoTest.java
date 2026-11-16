package br.edu.univasf.gerard_curador.dominio.narrativo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Contexto Narrativo da história")
class ContextoNarrativoTest {

    @Test
    @DisplayName("Contexto completo normaliza e fornece local, situação e momento")
    void contextoCompleto() {
        ContextoNarrativo contexto = new ContextoNarrativo("no jardim", "cultivo de rosas", "ontem");

        assertEquals("no jardim", contexto.getLocal());
        assertEquals("cultivo de rosas", contexto.getSituacao());
        assertEquals("ontem", contexto.getMomento());
        assertTrue(contexto.possuiLocal());
        assertTrue(contexto.possuiSituacao());
        assertTrue(contexto.possuiMomento());
        assertFalse(contexto.estaVazio());
        assertEquals("cultivo de rosas, no jardim, ontem", contexto.descrever());
        assertEquals("cultivo de rosas, no jardim, ontem", contexto.toString());
    }

    @Test
    @DisplayName("Contexto não informado instancia contexto totalmente vazio")
    void contextoNaoInformado() {
        ContextoNarrativo vazio = ContextoNarrativo.naoInformado();

        assertNull(vazio.getLocal());
        assertNull(vazio.getSituacao());
        assertNull(vazio.getMomento());
        assertFalse(vazio.possuiLocal());
        assertFalse(vazio.possuiSituacao());
        assertFalse(vazio.possuiMomento());
        assertTrue(vazio.estaVazio());
        assertNull(vazio.descrever());
        assertEquals("contexto nao informado", vazio.toString());
    }

    @Test
    @DisplayName("Construtor normaliza strings em branco para null")
    void normalizacaoDeBrancos() {
        ContextoNarrativo contexto = new ContextoNarrativo("   ", "", null);

        assertTrue(contexto.estaVazio());
        assertNull(contexto.getLocal());
        assertNull(contexto.getSituacao());
        assertNull(contexto.getMomento());
    }

    @Test
    @DisplayName("Descrever monta formatação combinando campos presentes")
    void descreverCamposParciais() {
        ContextoNarrativo soLocal = new ContextoNarrativo("na feira", null, null);
        assertEquals("na feira", soLocal.descrever());

        ContextoNarrativo soSituacao = new ContextoNarrativo(null, "jogo de bafo", null);
        assertEquals("jogo de bafo", soSituacao.descrever());

        ContextoNarrativo situacaoELocal = new ContextoNarrativo("na escola", "recreio", null);
        assertEquals("recreio, na escola", situacaoELocal.descrever());
    }

    @Test
    @DisplayName("Equals e hashCode comparam local, situação e momento")
    void equalsEHashCode() {
        ContextoNarrativo c1 = new ContextoNarrativo("na feira", "compras", "hoje");
        ContextoNarrativo c2 = new ContextoNarrativo("na feira", "compras", "hoje");
        ContextoNarrativo c3 = new ContextoNarrativo("em casa", "compras", "hoje");

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
        assertNotEquals(c1, c3);
        assertNotEquals(c1, null);
        assertNotEquals(c1, "na feira");
    }
}
