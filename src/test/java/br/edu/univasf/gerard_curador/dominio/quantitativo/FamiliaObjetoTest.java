package br.edu.univasf.gerard_curador.dominio.quantitativo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FamiliaObjetoTest {

    @Test
    @DisplayName("Deve instanciar família normalizando id para minúsculo e aplicando trim")
    void deveInstanciarNormalizandoDados() {
        FamiliaObjeto familia = new FamiliaObjeto("  ROSA  ", "  Rosa Vermelha  ");

        assertEquals("rosa", familia.getId());
        assertEquals("Rosa Vermelha", familia.getNomeConceitual());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o id for nulo, vazio ou em branco")
    void deveLancarExcecaoParaIdInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new FamiliaObjeto(null, "Rosa"));
        assertThrows(IllegalArgumentException.class, () -> new FamiliaObjeto("", "Rosa"));
        assertThrows(IllegalArgumentException.class, () -> new FamiliaObjeto("   ", "Rosa"));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o nome conceitual for nulo, vazio ou em branco")
    void deveLancarExcecaoParaNomeConceitualInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new FamiliaObjeto("rosa", null));
        assertThrows(IllegalArgumentException.class, () -> new FamiliaObjeto("rosa", ""));
        assertThrows(IllegalArgumentException.class, () -> new FamiliaObjeto("rosa", "   "));
    }

    @Test
    @DisplayName("Deve considerar iguais famílias com o mesmo id, mesmo com nomes conceituais diferentes")
    void deveCompararPeloIdCurado() {
        FamiliaObjeto f1 = new FamiliaObjeto("rosa", "Rosa");
        FamiliaObjeto f2 = new FamiliaObjeto("ROSA", "Rose");
        FamiliaObjeto f3 = new FamiliaObjeto("boneca", "Boneca");

        assertEquals(f1, f1);
        assertEquals(f1, f2);
        assertEquals(f1.hashCode(), f2.hashCode());

        assertNotEquals(f1, f3);
        assertNotEquals(f1, null);
        assertNotEquals(f1, "rosa");
    }

    @Test
    @DisplayName("Deve retornar o nome conceitual no toString")
    void deveRetornarNomeConceitualNoToString() {
        FamiliaObjeto familia = new FamiliaObjeto("rosa", "Rosa");
        assertEquals("Rosa", familia.toString());
    }
}
