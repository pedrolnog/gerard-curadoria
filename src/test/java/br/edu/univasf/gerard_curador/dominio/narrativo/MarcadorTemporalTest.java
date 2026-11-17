package br.edu.univasf.gerard_curador.dominio.narrativo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Marcador Temporal da narrativa")
class MarcadorTemporalTest {

    @Test
    @DisplayName("Marcador preserva a ordem curada e o rótulo do momento")
    void ordemCuradaERotulo() {
        MarcadorTemporal momento1 = new MarcadorTemporal(1, "ontem");
        MarcadorTemporal momento2 = MarcadorTemporal.de(2, "hoje");
        MarcadorTemporal momento3 = MarcadorTemporal.daOrdem(3);

        assertEquals(1, momento1.getOrdem());
        assertEquals("ontem", momento1.getRotulo());

        assertEquals(2, momento2.getOrdem());
        assertEquals("hoje", momento2.getRotulo());

        assertEquals(3, momento3.getOrdem());
        assertEquals("momento 3", momento3.getRotulo());
    }

    @Test
    @DisplayName("OcupaMesmaOrdemQue identifica quando dois marcadores disputam a mesma posição temporal")
    void ocupaMesmaOrdemQue() {
        MarcadorTemporal m1 = new MarcadorTemporal(1, "primeira partida");
        MarcadorTemporal m1OutroRotulo = new MarcadorTemporal(1, "momento 1");
        MarcadorTemporal m2 = new MarcadorTemporal(2, "segunda partida");

        assertTrue(m1.ocupaMesmaOrdemQue(m1OutroRotulo));
        assertFalse(m1.ocupaMesmaOrdemQue(m2));
        assertFalse(m1.ocupaMesmaOrdemQue(null));
    }

    @Test
    @DisplayName("EhAnteriorA e ehPosteriorA comparam as posições temporais")
    void anteriorEPosterior() {
        MarcadorTemporal t1 = MarcadorTemporal.daOrdem(1);
        MarcadorTemporal t2 = MarcadorTemporal.daOrdem(2);

        assertTrue(t1.ehAnteriorA(t2));
        assertFalse(t2.ehAnteriorA(t1));
        assertFalse(t1.ehAnteriorA(null));

        assertTrue(t2.ehPosteriorA(t1));
        assertFalse(t1.ehPosteriorA(t2));
        assertFalse(t2.ehPosteriorA(null));
    }

    @Test
    @DisplayName("CompareTo ordena os marcadores pela ordem numérica")
    void comparacaoTemporal() {
        MarcadorTemporal t1 = MarcadorTemporal.de(1, "segunda partida no texto");
        MarcadorTemporal t2 = MarcadorTemporal.de(2, "primeira partida no texto");

        assertTrue(t1.compareTo(t2) < 0);
        assertTrue(t2.compareTo(t1) > 0);
        assertEquals(0, t1.compareTo(new MarcadorTemporal(1, "outro rotulo")));
        assertThrows(IllegalArgumentException.class, () -> t1.compareTo(null));
    }

    @Test
    @DisplayName("Equals e hashCode consideram ordem e rótulo")
    void equalsEHashCode() {
        MarcadorTemporal m1a = new MarcadorTemporal(1, "ontem");
        MarcadorTemporal m1b = new MarcadorTemporal(1, "ontem");
        MarcadorTemporal m1c = new MarcadorTemporal(1, "hoje");
        MarcadorTemporal m2 = new MarcadorTemporal(2, "ontem");

        assertEquals(m1a, m1b);
        assertEquals(m1a.hashCode(), m1b.hashCode());
        assertNotEquals(m1a, m1c);
        assertNotEquals(m1a, m2);
        assertNotEquals(m1a, null);
        assertNotEquals(m1a, "ontem");
    }

    @Test
    @DisplayName("ToString retorna o rótulo do marcador")
    void toStringRetornaRotulo() {
        MarcadorTemporal marcador = MarcadorTemporal.de(1, "primeira partida");
        assertEquals("primeira partida", marcador.toString());
    }

    @Test
    @DisplayName("Rejeita ordem menor que 1")
    void rejeitaOrdemInvalida() {
        assertThrows(IllegalArgumentException.class, () -> new MarcadorTemporal(0, "inicio"));
        assertThrows(IllegalArgumentException.class, () -> new MarcadorTemporal(-1, "inicio"));
    }

    @Test
    @DisplayName("Rejeita rótulo nulo ou em branco")
    void rejeitaRotuloInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new MarcadorTemporal(1, null));
        assertThrows(IllegalArgumentException.class, () -> new MarcadorTemporal(1, ""));
        assertThrows(IllegalArgumentException.class, () -> new MarcadorTemporal(1, "   "));
    }
}
