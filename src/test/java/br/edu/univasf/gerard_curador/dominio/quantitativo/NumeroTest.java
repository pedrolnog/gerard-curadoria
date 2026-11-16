package br.edu.univasf.gerard_curador.dominio.quantitativo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumeroTest {

    @Test
    @DisplayName("Deve instanciar via construtor e método estático preservando o valor")
    void deveInstanciarPreservandoValor() {
        Numero n1 = new Numero(42);
        Numero n2 = Numero.de(-10);

        assertEquals(42, n1.getValor());
        assertEquals(-10, n2.getValor());
        assertEquals(0, Numero.ZERO.getValor());
    }

    @Test
    @DisplayName("Deve somar valores retornando nova instância sem alterar os operandos originais")
    void deveSomarMantendoImutabilidade() {
        Numero original = Numero.de(5);
        Numero incremento = Numero.de(3);

        Numero resultado = original.somar(incremento);

        assertEquals(8, resultado.getValor());
        assertEquals(5, original.getValor());
        assertEquals(3, incremento.getValor());
        assertNotSame(original, resultado);
    }

    @Test
    @DisplayName("Deve subtrair valores retornando nova instância sem alterar os operandos originais")
    void deveSubtrairMantendoImutabilidade() {
        Numero original = Numero.de(10);
        Numero decremento = Numero.de(4);

        Numero resultado = original.subtrair(decremento);

        assertEquals(6, resultado.getValor());
        assertEquals(10, original.getValor());
        assertEquals(4, decremento.getValor());
        assertNotSame(original, resultado);
    }

    @Test
    @DisplayName("Deve calcular a diferença absoluta retornando nova instância sem alterar os originais")
    void deveCalcularDiferencaAbsolutaMantendoImutabilidade() {
        Numero a = Numero.de(3);
        Numero b = Numero.de(7);

        Numero diff1 = a.diferencaAbsolutaPara(b);
        Numero diff2 = b.diferencaAbsolutaPara(a);

        assertEquals(4, diff1.getValor());
        assertEquals(4, diff2.getValor());
        assertEquals(3, a.getValor());
        assertEquals(7, b.getValor());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao operar com operando nulo")
    void deveLancarExcecaoParaOperacaoComNulo() {
        Numero numero = Numero.de(5);

        assertThrows(IllegalArgumentException.class, () -> numero.somar(null));
        assertThrows(IllegalArgumentException.class, () -> numero.subtrair(null));
        assertThrows(IllegalArgumentException.class, () -> numero.diferencaAbsolutaPara(null));
        assertThrows(IllegalArgumentException.class, () -> numero.ehMenorQue(null));
        assertThrows(IllegalArgumentException.class, () -> numero.ehMaiorQue(null));
        assertThrows(IllegalArgumentException.class, () -> numero.compareTo(null));
    }

    @Test
    @DisplayName("Deve identificar corretamente números negativos, zero e positivos")
    void deveClassificarSinalDoNumero() {
        Numero negativo = Numero.de(-5);
        Numero zero = Numero.de(0);
        Numero positivo = Numero.de(5);

        assertTrue(negativo.ehNegativo());
        assertFalse(negativo.ehZero());
        assertFalse(negativo.ehPositivo());

        assertFalse(zero.ehNegativo());
        assertTrue(zero.ehZero());
        assertFalse(zero.ehPositivo());

        assertFalse(positivo.ehNegativo());
        assertFalse(positivo.ehZero());
        assertTrue(positivo.ehPositivo());
    }

    @Test
    @DisplayName("Deve comparar relação de grandeza entre números")
    void deveCompararValores() {
        Numero menor = Numero.de(2);
        Numero maior = Numero.de(8);

        assertTrue(menor.ehMenorQue(maior));
        assertFalse(maior.ehMenorQue(menor));
        assertFalse(menor.ehMenorQue(menor));

        assertTrue(maior.ehMaiorQue(menor));
        assertFalse(menor.ehMaiorQue(maior));
        assertFalse(maior.ehMaiorQue(maior));

        assertTrue(menor.compareTo(maior) < 0);
        assertTrue(maior.compareTo(menor) > 0);
        assertEquals(0, menor.compareTo(Numero.de(2)));
    }

    @Test
    @DisplayName("Deve respeitar o contrato de equals e hashCode baseado no valor")
    void deveRespeitarEqualsEHashCode() {
        Numero n1 = Numero.de(15);
        Numero n2 = new Numero(15);
        Numero n3 = Numero.de(20);

        assertEquals(n1, n1);
        assertEquals(n1, n2);
        assertEquals(n1.hashCode(), n2.hashCode());

        assertNotEquals(n1, n3);
        assertNotEquals(n1, null);
        assertNotEquals(n1, "15");
    }

    @Test
    @DisplayName("Deve representar textualmente o valor inteiro")
    void deveRepresentarComoTexto() {
        assertEquals("42", Numero.de(42).toString());
        assertEquals("-7", Numero.de(-7).toString());
    }
}
