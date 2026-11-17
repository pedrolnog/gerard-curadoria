package br.edu.univasf.gerard_curador.dominio.quantitativo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RealizacaoTextualObjetoTest {

    @Test
    @DisplayName("Deve instanciar via construtor completo e via fábrica em português")
    void deveInstanciarCorretamente() {
        RealizacaoTextualObjeto r1 = new RealizacaoTextualObjeto("rose", "roses", "en-US");
        RealizacaoTextualObjeto r2 = RealizacaoTextualObjeto.emPortugues("rosa", "rosas");

        assertEquals("rose", r1.getSingular());
        assertEquals("roses", r1.getPlural());
        assertEquals("en-US", r1.getIdioma());

        assertEquals("rosa", r2.getSingular());
        assertEquals("rosas", r2.getPlural());
        assertEquals("pt-BR", r2.getIdioma());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o singular for nulo, vazio ou em branco")
    void deveLancarExcecaoParaSingularInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new RealizacaoTextualObjeto(null, "rosas", "pt-BR"));
        assertThrows(IllegalArgumentException.class, () -> new RealizacaoTextualObjeto("", "rosas", "pt-BR"));
        assertThrows(IllegalArgumentException.class, () -> new RealizacaoTextualObjeto("   ", "rosas", "pt-BR"));
        assertThrows(IllegalArgumentException.class, () -> RealizacaoTextualObjeto.emPortugues(null, "rosas"));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o plural for nulo, vazio ou em branco")
    void deveLancarExcecaoParaPluralInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new RealizacaoTextualObjeto("rosa", null, "pt-BR"));
        assertThrows(IllegalArgumentException.class, () -> new RealizacaoTextualObjeto("rosa", "", "pt-BR"));
        assertThrows(IllegalArgumentException.class, () -> new RealizacaoTextualObjeto("rosa", "   ", "pt-BR"));
        assertThrows(IllegalArgumentException.class, () -> RealizacaoTextualObjeto.emPortugues("rosa", null));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o idioma for nulo, vazio ou em branco")
    void deveLancarExcecaoParaIdiomaInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new RealizacaoTextualObjeto("rosa", "rosas", null));
        assertThrows(IllegalArgumentException.class, () -> new RealizacaoTextualObjeto("rosa", "rosas", ""));
        assertThrows(IllegalArgumentException.class, () -> new RealizacaoTextualObjeto("rosa", "rosas", "   "));
    }

    @Test
    @DisplayName("Deve flexionar no singular para o número 1 e no plural para os demais")
    void deveFlexionarConformeNumero() {
        RealizacaoTextualObjeto texto = RealizacaoTextualObjeto.emPortugues("rosa", "rosas");

        assertEquals("rosa", texto.flexionar(Numero.de(1)));
        assertEquals("rosas", texto.flexionar(Numero.de(0)));
        assertEquals("rosas", texto.flexionar(Numero.de(2)));
        assertEquals("rosas", texto.flexionar(Numero.de(10)));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao tentar flexionar com número nulo")
    void deveLancarExcecaoAoFlexionarComNulo() {
        RealizacaoTextualObjeto texto = RealizacaoTextualObjeto.emPortugues("rosa", "rosas");
        assertThrows(IllegalArgumentException.class, () -> texto.flexionar(null));
    }

    @Test
    @DisplayName("Deve descrever número e termo flexionado corretamente")
    void deveDescreverComNumeroETermo() {
        RealizacaoTextualObjeto texto = RealizacaoTextualObjeto.emPortugues("rosa branca", "rosas brancas");

        assertEquals("1 rosa branca", texto.descrever(Numero.de(1)));
        assertEquals("2 rosas brancas", texto.descrever(Numero.de(2)));
        assertEquals("0 rosas brancas", texto.descrever(Numero.de(0)));
    }

    @Test
    @DisplayName("Deve respeitar o contrato de equals e hashCode baseado em singular, plural e idioma")
    void deveRespeitarEqualsEHashCode() {
        RealizacaoTextualObjeto r1 = RealizacaoTextualObjeto.emPortugues("rosa", "rosas");
        RealizacaoTextualObjeto r2 = new RealizacaoTextualObjeto("rosa", "rosas", "pt-BR");
        RealizacaoTextualObjeto r3 = new RealizacaoTextualObjeto("rosa", "rosas", "pt-PT");
        RealizacaoTextualObjeto r4 = RealizacaoTextualObjeto.emPortugues("boneca", "bonecas");

        assertEquals(r1, r1);
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());

        assertNotEquals(r1, r3);
        assertNotEquals(r1, r4);
        assertNotEquals(r1, null);
        assertNotEquals(r1, "rosa");
    }

    @Test
    @DisplayName("Deve formatar textualmente no padrão singular/plural (idioma)")
    void deveFormatarComoTexto() {
        RealizacaoTextualObjeto texto = RealizacaoTextualObjeto.emPortugues("rosa", "rosas");
        assertEquals("rosa/rosas (pt-BR)", texto.toString());
    }
}
