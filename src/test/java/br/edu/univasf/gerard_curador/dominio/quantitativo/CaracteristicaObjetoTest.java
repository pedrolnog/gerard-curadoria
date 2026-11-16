package br.edu.univasf.gerard_curador.dominio.quantitativo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CaracteristicaObjetoTest {

    @Test
    @DisplayName("Deve criar característica com dimensão e valor válidos")
    void deveCriarCaracteristicaValida() {
        CaracteristicaObjeto c1 = new CaracteristicaObjeto("cor", "branca");
        CaracteristicaObjeto c2 = CaracteristicaObjeto.de("material", "vidro");

        assertEquals("cor", c1.getDimensao());
        assertEquals("branca", c1.getValor());
        assertEquals("material", c2.getDimensao());
        assertEquals("vidro", c2.getValor());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando dimensão for nula, vazia ou em branco")
    void deveLancarExcecaoParaDimensaoInvalida() {
        assertThrows(IllegalArgumentException.class, () -> new CaracteristicaObjeto(null, "branca"));
        assertThrows(IllegalArgumentException.class, () -> new CaracteristicaObjeto("", "branca"));
        assertThrows(IllegalArgumentException.class, () -> new CaracteristicaObjeto("   ", "branca"));
        assertThrows(IllegalArgumentException.class, () -> CaracteristicaObjeto.de(null, "branca"));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando valor for nulo, vazio ou em branco")
    void deveLancarExcecaoParaValorInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new CaracteristicaObjeto("cor", null));
        assertThrows(IllegalArgumentException.class, () -> new CaracteristicaObjeto("cor", ""));
        assertThrows(IllegalArgumentException.class, () -> new CaracteristicaObjeto("cor", "   "));
        assertThrows(IllegalArgumentException.class, () -> CaracteristicaObjeto.de("cor", null));
    }

    @Test
    @DisplayName("Deve verificar se duas características descrevem a mesma dimensão")
    void deveIdentificarMesmaDimensao() {
        CaracteristicaObjeto corBranca = CaracteristicaObjeto.de("cor", "branca");
        CaracteristicaObjeto corAmarela = CaracteristicaObjeto.de("cor", "amarela");
        CaracteristicaObjeto materialVidro = CaracteristicaObjeto.de("material", "vidro");

        assertTrue(corBranca.descreveMesmaDimensaoQue(corAmarela));
        assertTrue(corAmarela.descreveMesmaDimensaoQue(corBranca));
        assertFalse(corBranca.descreveMesmaDimensaoQue(materialVidro));
        assertFalse(corBranca.descreveMesmaDimensaoQue(null));
    }

    @Test
    @DisplayName("Deve respeitar o contrato de equals e hashCode baseado em dimensão e valor")
    void deveRespeitarEqualsEHashCode() {
        CaracteristicaObjeto c1 = CaracteristicaObjeto.de("cor", "branca");
        CaracteristicaObjeto c2 = new CaracteristicaObjeto("cor", "branca");
        CaracteristicaObjeto c3 = CaracteristicaObjeto.de("cor", "amarela");
        CaracteristicaObjeto c4 = CaracteristicaObjeto.de("tamanho", "branca");

        assertEquals(c1, c1);
        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());

        assertNotEquals(c1, c3);
        assertNotEquals(c1, c4);
        assertNotEquals(c1, null);
        assertNotEquals(c1, "cor=branca");
    }

    @Test
    @DisplayName("Deve formatar como texto no padrão dimensao=valor")
    void deveFormatarComoTexto() {
        CaracteristicaObjeto c = CaracteristicaObjeto.de("cor", "branca");
        assertEquals("cor=branca", c.toString());
    }
}
