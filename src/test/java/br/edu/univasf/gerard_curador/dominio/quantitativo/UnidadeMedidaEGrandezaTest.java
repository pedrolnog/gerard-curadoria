package br.edu.univasf.gerard_curador.dominio.quantitativo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnidadeMedidaEGrandezaTest {

    @Test
    @DisplayName("Deve retornar a unidade padrão correta para cada grandeza quantitativa")
    void deveRetornarUnidadePadraoParaCadaGrandeza() {
        assertEquals(UnidadeMedida.UNIDADE, GrandezaQuantitativa.CONTAGEM.getUnidadePadrao());
        assertEquals(UnidadeMedida.REAL, GrandezaQuantitativa.VALOR_MONETARIO.getUnidadePadrao());
        assertEquals(UnidadeMedida.QUILOGRAMA, GrandezaQuantitativa.MASSA.getUnidadePadrao());
    }

    @Test
    @DisplayName("Deve validar a compatibilidade bidirecional entre unidade e grandeza")
    void deveValidarCompatibilidadeEntreUnidadeEGrandeza() {
        assertTrue(GrandezaQuantitativa.CONTAGEM.aceitaUnidade(UnidadeMedida.UNIDADE));
        assertTrue(GrandezaQuantitativa.CONTAGEM.ehCompativelCom(UnidadeMedida.UNIDADE));
        assertTrue(UnidadeMedida.UNIDADE.ehCompativelCom(GrandezaQuantitativa.CONTAGEM));

        assertTrue(GrandezaQuantitativa.VALOR_MONETARIO.aceitaUnidade(UnidadeMedida.REAL));
        assertTrue(GrandezaQuantitativa.VALOR_MONETARIO.aceitaUnidade(UnidadeMedida.CENTAVO));
        assertTrue(UnidadeMedida.REAL.ehCompativelCom(GrandezaQuantitativa.VALOR_MONETARIO));

        assertTrue(GrandezaQuantitativa.MASSA.aceitaUnidade(UnidadeMedida.QUILOGRAMA));
        assertTrue(GrandezaQuantitativa.MASSA.aceitaUnidade(UnidadeMedida.GRAMA));
        assertTrue(UnidadeMedida.QUILOGRAMA.ehCompativelCom(GrandezaQuantitativa.MASSA));

        assertFalse(GrandezaQuantitativa.CONTAGEM.aceitaUnidade(UnidadeMedida.REAL));
        assertFalse(GrandezaQuantitativa.VALOR_MONETARIO.aceitaUnidade(UnidadeMedida.QUILOGRAMA));
        assertFalse(GrandezaQuantitativa.MASSA.aceitaUnidade(UnidadeMedida.UNIDADE));

        assertFalse(UnidadeMedida.REAL.ehCompativelCom(GrandezaQuantitativa.MASSA));
        assertFalse(GrandezaQuantitativa.CONTAGEM.aceitaUnidade(null));
        assertFalse(GrandezaQuantitativa.CONTAGEM.ehCompativelCom((UnidadeMedida) null));
    }

    @Test
    @DisplayName("Deve verificar compatibilidade entre grandezas quantitativas")
    void deveVerificarCompatibilidadeEntreGrandezas() {
        assertTrue(GrandezaQuantitativa.CONTAGEM.ehCompativelCom(GrandezaQuantitativa.CONTAGEM));
        assertFalse(GrandezaQuantitativa.CONTAGEM.ehCompativelCom(GrandezaQuantitativa.VALOR_MONETARIO));
        assertFalse(GrandezaQuantitativa.CONTAGEM.ehCompativelCom(GrandezaQuantitativa.MASSA));
    }

    @Test
    @DisplayName("Deve verificar compatibilidade entre unidades de medida")
    void deveVerificarCompatibilidadeEntreUnidades() {
        assertTrue(UnidadeMedida.REAL.ehCompativelCom(UnidadeMedida.REAL));
        assertFalse(UnidadeMedida.REAL.ehCompativelCom(UnidadeMedida.CENTAVO));
        assertFalse(UnidadeMedida.QUILOGRAMA.ehCompativelCom(UnidadeMedida.GRAMA));
    }

    @Test
    @DisplayName("Deve flexionar a unidade no singular para 1 e no plural para outros números")
    void deveFlexionarUnidade() {
        assertEquals("real", UnidadeMedida.REAL.flexionar(Numero.de(1)));
        assertEquals("reais", UnidadeMedida.REAL.flexionar(Numero.de(0)));
        assertEquals("reais", UnidadeMedida.REAL.flexionar(Numero.de(2)));
        assertEquals("reais", UnidadeMedida.REAL.flexionar(Numero.de(15)));

        assertEquals("quilograma", UnidadeMedida.QUILOGRAMA.flexionar(Numero.de(1)));
        assertEquals("quilogramas", UnidadeMedida.QUILOGRAMA.flexionar(Numero.de(5)));

        assertEquals("unidade", UnidadeMedida.UNIDADE.flexionar(Numero.de(1)));
        assertEquals("unidades", UnidadeMedida.UNIDADE.flexionar(Numero.de(10)));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao tentar flexionar unidade com número nulo")
    void deveLancarExcecaoAoFlexionarComNulo() {
        assertThrows(IllegalArgumentException.class, () -> UnidadeMedida.REAL.flexionar(null));
    }

    @Test
    @DisplayName("Deve expor atributos declarados de grandezas e unidades")
    void deveExporAtributosDeclarados() {
        assertEquals("contagem", GrandezaQuantitativa.CONTAGEM.getNomeExibido());
        assertEquals("valor monetario", GrandezaQuantitativa.VALOR_MONETARIO.getNomeExibido());
        assertEquals("massa", GrandezaQuantitativa.MASSA.getNomeExibido());

        assertEquals(GrandezaQuantitativa.VALOR_MONETARIO, UnidadeMedida.REAL.getGrandeza());
        assertEquals("real", UnidadeMedida.REAL.getSingular());
        assertEquals("reais", UnidadeMedida.REAL.getPlural());
    }
}
