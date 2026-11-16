package br.edu.univasf.gerard_curador.dominio.quantitativo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Teste de fumaca do build: garante que JUnit 5 roda e que o nucleo quantitativo compila. */
class QuantidadeBuildTest {

    private final FamiliaObjeto rosa = new FamiliaObjeto("rosa", "Rosa");
    private final ObjetoContado rosaBranca = new ObjetoContado("rosa-branca", rosa, Set.of());

    @Test
    @DisplayName("Somar quantidades compativeis produz nova quantidade")
    void somaCompativel() {
        Quantidade duas = rosaBranca.quantificar(new Numero(2), GrandezaQuantitativa.CONTAGEM, UnidadeMedida.UNIDADE);
        Quantidade tres = rosaBranca.quantificar(new Numero(3), GrandezaQuantitativa.CONTAGEM, UnidadeMedida.UNIDADE);
        Quantidade cinco = duas.somar(tres);
        assertEquals(5, cinco.subtrair(rosaBranca.quantificar(new Numero(0), GrandezaQuantitativa.CONTAGEM, UnidadeMedida.UNIDADE)).getNumero().getValor());
    }

    @Test
    @DisplayName("Subtracao que deixaria saldo negativo e recusada")
    void subtracaoNegativa() {
        Quantidade uma = rosaBranca.quantificar(new Numero(1), GrandezaQuantitativa.CONTAGEM, UnidadeMedida.UNIDADE);
        Quantidade duas = rosaBranca.quantificar(new Numero(2), GrandezaQuantitativa.CONTAGEM, UnidadeMedida.UNIDADE);
        assertThrows(IllegalArgumentException.class, () -> uma.subtrair(duas));
    }
}
