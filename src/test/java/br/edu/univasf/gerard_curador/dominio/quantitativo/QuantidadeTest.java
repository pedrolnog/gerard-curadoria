package br.edu.univasf.gerard_curador.dominio.quantitativo;

import br.edu.univasf.gerard_curador.dominio.excecao.QuantidadeIncompativelException;
import br.edu.univasf.gerard_curador.dominio.excecao.RetiradaImpossivelException;
import br.edu.univasf.gerard_curador.dominio.excecao.ViolacaoDeDominioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class QuantidadeTest {

    private final FamiliaObjeto familiaRosa = new FamiliaObjeto("rosa", "Rosa");
    private final FamiliaObjeto familiaBoneca = new FamiliaObjeto("boneca", "Boneca");
    private final FamiliaObjeto familiaDinheiro = new FamiliaObjeto("dinheiro", "Dinheiro");
    private final FamiliaObjeto familiaAlimento = new FamiliaObjeto("alimento", "Alimento");

    private final ObjetoContado rosaBranca = new ObjetoContado(
            "rosa_branca",
            familiaRosa,
            Set.of(CaracteristicaObjeto.de("cor", "branca")),
            RealizacaoTextualObjeto.emPortugues("rosa branca", "rosas brancas"),
            ReferenciaVisual.de("rosa_branca")
    );

    private final ObjetoContado boneca = new ObjetoContado(
            "boneca",
            familiaBoneca,
            Set.of(),
            RealizacaoTextualObjeto.emPortugues("boneca", "bonecas"),
            ReferenciaVisual.de("boneca")
    );

    private final ObjetoContado dinheiro = new ObjetoContado(
            "dinheiro",
            familiaDinheiro,
            Set.of(),
            RealizacaoTextualObjeto.emPortugues("dinheiro", "dinheiro"),
            ReferenciaVisual.de("dinheiro")
    );

    private final ObjetoContado arroz = new ObjetoContado(
            "arroz",
            familiaAlimento,
            Set.of(),
            RealizacaoTextualObjeto.emPortugues("arroz", "arroz"),
            ReferenciaVisual.de("arroz")
    );

    @Test
    @DisplayName("Deve instanciar quantidade válida e expor atributos")
    void deveInstanciarQuantidadeValida() {
        Quantidade quantidade = new Quantidade(Numero.de(5), rosaBranca, GrandezaQuantitativa.CONTAGEM, UnidadeMedida.UNIDADE);

        assertEquals(Numero.de(5), quantidade.getNumero());
        assertEquals(5, quantidade.getValor());
        assertEquals(rosaBranca, quantidade.getObjeto());
        assertEquals(GrandezaQuantitativa.CONTAGEM, quantidade.getGrandeza());
        assertEquals(UnidadeMedida.UNIDADE, quantidade.getUnidade());
        assertFalse(quantidade.ehZero());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando algum atributo do construtor for nulo")
    void deveLancarExcecaoParaAtributosNulosNoConstrutor() {
        assertThrows(IllegalArgumentException.class, () ->
                new Quantidade(null, rosaBranca, GrandezaQuantitativa.CONTAGEM, UnidadeMedida.UNIDADE));
        assertThrows(IllegalArgumentException.class, () ->
                new Quantidade(Numero.de(1), null, GrandezaQuantitativa.CONTAGEM, UnidadeMedida.UNIDADE));
        assertThrows(IllegalArgumentException.class, () ->
                new Quantidade(Numero.de(1), rosaBranca, null, UnidadeMedida.UNIDADE));
        assertThrows(IllegalArgumentException.class, () ->
                new Quantidade(Numero.de(1), rosaBranca, GrandezaQuantitativa.CONTAGEM, null));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao tentar criar quantidade com unidade incompatível com a grandeza")
    void deveLancarExcecaoParaUnidadeIncompativelComGrandeza() {
        assertThrows(IllegalArgumentException.class, () ->
                new Quantidade(Numero.de(10), rosaBranca, GrandezaQuantitativa.CONTAGEM, UnidadeMedida.REAL));
        assertThrows(IllegalArgumentException.class, () ->
                new Quantidade(Numero.de(5), arroz, GrandezaQuantitativa.MASSA, UnidadeMedida.UNIDADE));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao tentar instanciar quantidade com número negativo")
    void deveLancarExcecaoParaNumeroNegativo() {
        assertThrows(IllegalArgumentException.class, () ->
                new Quantidade(Numero.de(-1), rosaBranca, GrandezaQuantitativa.CONTAGEM, UnidadeMedida.UNIDADE));
    }

    @Test
    @DisplayName("Deve somar quantidades compatíveis retornando nova instância sem alterar as originais")
    void deveSomarMantendoImutabilidade() {
        Quantidade q1 = rosaBranca.contar(3);
        Quantidade q2 = rosaBranca.contar(2);

        Quantidade soma = q1.somar(q2);

        assertEquals(5, soma.getValor());
        assertEquals(3, q1.getValor());
        assertEquals(2, q2.getValor());
        assertNotSame(q1, soma);
        assertNotSame(q2, soma);
    }

    @Test
    @DisplayName("Deve subtrair quantidades compatíveis retornando nova instância sem alterar as originais")
    void deveSubtrairMantendoImutabilidade() {
        Quantidade q1 = rosaBranca.contar(5);
        Quantidade q2 = rosaBranca.contar(2);

        Quantidade resto = q1.subtrair(q2);

        assertEquals(3, resto.getValor());
        assertEquals(5, q1.getValor());
        assertEquals(2, q2.getValor());
        assertNotSame(q1, resto);
        assertNotSame(q2, resto);
    }

    @Test
    @DisplayName("Somar rosas e bonecas lança QuantidadeIncompativelException")
    void somarRosasEBonecasLancaQuantidadeIncompativelException() {
        Quantidade rosas = rosaBranca.contar(3);
        Quantidade bonecas = boneca.contar(2);

        QuantidadeIncompativelException ex = assertThrows(
                QuantidadeIncompativelException.class,
                () -> rosas.somar(bonecas)
        );

        assertTrue(ex instanceof ViolacaoDeDominioException);
        assertNotNull(rosas.descreverIncompatibilidadeCom(bonecas));
    }

    @Test
    @DisplayName("Somar reais e quilogramas lança QuantidadeIncompativelException")
    void somarReaisEQuilogramasLancaQuantidadeIncompativelException() {
        Quantidade valorMonetario = dinheiro.quantificar(
                Numero.de(10), GrandezaQuantitativa.VALOR_MONETARIO, UnidadeMedida.REAL);
        Quantidade massa = arroz.quantificar(
                Numero.de(2), GrandezaQuantitativa.MASSA, UnidadeMedida.QUILOGRAMA);

        QuantidadeIncompativelException ex = assertThrows(
                QuantidadeIncompativelException.class,
                () -> valorMonetario.somar(massa)
        );

        assertTrue(ex instanceof ViolacaoDeDominioException);
        assertNotNull(valorMonetario.descreverIncompatibilidadeCom(massa));
    }

    @Test
    @DisplayName("Retirar mais do que existe lança RetiradaImpossivelException e a original continua intacta")
    void retirarMaisDoQueExisteLancaRetiradaImpossivelExceptionEOriginalContinuaIntacta() {
        Quantidade disponivel = rosaBranca.contar(2);
        Quantidade retiradaPretendida = rosaBranca.contar(3);

        RetiradaImpossivelException ex = assertThrows(
                RetiradaImpossivelException.class,
                () -> disponivel.subtrair(retiradaPretendida)
        );

        assertTrue(ex instanceof ViolacaoDeDominioException);
        assertEquals(2, disponivel.getValor());
        assertEquals(3, retiradaPretendida.getValor());
    }

    @Test
    @DisplayName("Deve verificar comportamento de comportar retirada e zero")
    void deveVerificarComportamentoDeRetiradaEZero() {
        Quantidade cinco = rosaBranca.contar(5);
        Quantidade tres = rosaBranca.contar(3);
        Quantidade zero = rosaBranca.contar(0);

        assertTrue(cinco.comportaRetiradaDe(tres));
        assertTrue(cinco.comportaRetiradaDe(cinco));
        assertFalse(tres.comportaRetiradaDe(cinco));
        assertFalse(cinco.comportaRetiradaDe(boneca.contar(1)));

        assertTrue(zero.ehZero());
        assertFalse(cinco.ehZero());
    }

    @Test
    @DisplayName("Deve comparar compatibilidade completa e mesma medida")
    void deveCompararCompatibilidadeEMesmaMedida() {
        Quantidade rosas1 = rosaBranca.contar(2);
        Quantidade rosas2 = rosaBranca.contar(5);
        Quantidade bonecas = boneca.contar(2);

        assertTrue(rosas1.ehCompativelCom(rosas2));
        assertFalse(rosas1.ehCompativelCom(bonecas));
        assertFalse(rosas1.ehCompativelCom(null));

        assertTrue(rosas1.possuiMesmaMedidaQue(bonecas));
        assertFalse(rosas1.possuiMesmaMedidaQue(null));
        assertNull(rosas1.descreverIncompatibilidadeCom(rosas2));
    }

    @Test
    @DisplayName("Deve descrever textualmente a contagem e outras grandezas")
    void deveDescreverTextualmente() {
        Quantidade duasRosas = rosaBranca.contar(2);
        assertEquals("2 rosas brancas", duasRosas.descrever());
        assertEquals("2 rosas brancas", duasRosas.toString());

        Quantidade dezReais = dinheiro.quantificar(Numero.de(10), GrandezaQuantitativa.VALOR_MONETARIO, UnidadeMedida.REAL);
        assertEquals("10 reais de dinheiro", dezReais.descrever());

        Quantidade umReal = dinheiro.quantificar(Numero.de(1), GrandezaQuantitativa.VALOR_MONETARIO, UnidadeMedida.REAL);
        assertEquals("1 real de dinheiro", umReal.descrever());
    }

    @Test
    @DisplayName("Deve respeitar o contrato de equals e hashCode baseado nos quatro atributos")
    void deveRespeitarEqualsEHashCode() {
        Quantidade q1 = rosaBranca.contar(3);
        Quantidade q2 = rosaBranca.contar(3);
        Quantidade q3 = rosaBranca.contar(4);
        Quantidade q4 = boneca.contar(3);

        assertEquals(q1, q1);
        assertEquals(q1, q2);
        assertEquals(q1.hashCode(), q2.hashCode());

        assertNotEquals(q1, q3);
        assertNotEquals(q1, q4);
        assertNotEquals(q1, null);
        assertNotEquals(q1, "3");
    }
}
