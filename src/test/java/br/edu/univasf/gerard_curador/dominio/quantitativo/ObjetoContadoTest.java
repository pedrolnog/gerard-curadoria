package br.edu.univasf.gerard_curador.dominio.quantitativo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ObjetoContadoTest {

    private final FamiliaObjeto familiaRosa = new FamiliaObjeto("rosa", "Rosa");
    private final FamiliaObjeto familiaBoneca = new FamiliaObjeto("boneca", "Boneca");
    private final RealizacaoTextualObjeto textoRosa = RealizacaoTextualObjeto.emPortugues("rosa", "rosas");

    @Test
    @DisplayName("Deve instanciar objeto contado com todos os atributos válidos")
    void deveInstanciarComAtributosValidos() {
        CaracteristicaObjeto cor = CaracteristicaObjeto.de("cor", "branca");
        ReferenciaVisual visual = ReferenciaVisual.de("rosa_branca");

        ObjetoContado objeto = new ObjetoContado(
                "rosa_branca",
                familiaRosa,
                Set.of(cor),
                textoRosa,
                visual
        );

        assertEquals("rosa_branca", objeto.getId());
        assertEquals(familiaRosa, objeto.getFamilia());
        assertEquals(Set.of(cor), objeto.getCaracteristicas());
        assertEquals(textoRosa, objeto.getRealizacaoTextual());
        assertEquals(visual, objeto.getReferenciaVisual());
    }

    @Test
    @DisplayName("Deve aplicar padrões para características e referência visual quando forem nulas")
    void deveAplicarPadroesParaOpcionaisNulos() {
        ObjetoContado objeto = new ObjetoContado(
                "rosa_simples",
                familiaRosa,
                null,
                textoRosa,
                null
        );

        assertNotNull(objeto.getCaracteristicas());
        assertTrue(objeto.getCaracteristicas().isEmpty());
        assertNotNull(objeto.getReferenciaVisual());
        assertFalse(objeto.getReferenciaVisual().estaDefinida());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando atributos obrigatórios forem inválidos")
    void deveLancarExcecaoParaAtributosObrigatoriosInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> new ObjetoContado(null, familiaRosa, Set.of(), textoRosa, null));
        assertThrows(IllegalArgumentException.class, () -> new ObjetoContado("", familiaRosa, Set.of(), textoRosa, null));
        assertThrows(IllegalArgumentException.class, () -> new ObjetoContado("   ", familiaRosa, Set.of(), textoRosa, null));
        assertThrows(IllegalArgumentException.class, () -> new ObjetoContado("rosa", null, Set.of(), textoRosa, null));
        assertThrows(IllegalArgumentException.class, () -> new ObjetoContado("rosa", familiaRosa, Set.of(), null, null));
    }

    @Test
    @DisplayName("Deve manter características imutáveis e protegidas por cópia defensiva")
    void deveProtegerConjuntoDeCaracteristicasContraMutacao() {
        CaracteristicaObjeto cor = CaracteristicaObjeto.de("cor", "branca");
        Set<CaracteristicaObjeto> mutavel = new HashSet<>();
        mutavel.add(cor);

        ObjetoContado objeto = new ObjetoContado("rosa_branca", familiaRosa, mutavel, textoRosa, null);

        mutavel.add(CaracteristicaObjeto.de("tamanho", "grande"));
        assertEquals(1, objeto.getCaracteristicas().size());

        Set<CaracteristicaObjeto> exposta = objeto.getCaracteristicas();
        assertThrows(UnsupportedOperationException.class, () -> exposta.add(CaracteristicaObjeto.de("perfume", "suave")));
    }

    @Test
    @DisplayName("Rosa branca e rosa amarela devem ser objetos distintos da mesma família")
    void rosaBrancaERosaAmarelaDevemSerDistintosDaMesmaFamilia() {
        ObjetoContado rosaBranca = new ObjetoContado(
                "rosa_branca",
                familiaRosa,
                Set.of(CaracteristicaObjeto.de("cor", "branca")),
                RealizacaoTextualObjeto.emPortugues("rosa branca", "rosas brancas"),
                ReferenciaVisual.de("rosa_branca")
        );

        ObjetoContado rosaAmarela = new ObjetoContado(
                "rosa_amarela",
                familiaRosa,
                Set.of(CaracteristicaObjeto.de("cor", "amarela")),
                RealizacaoTextualObjeto.emPortugues("rosa amarela", "rosas amarelas"),
                ReferenciaVisual.de("rosa_amarela")
        );

        ObjetoContado boneca = new ObjetoContado(
                "boneca",
                familiaBoneca,
                Set.of(),
                RealizacaoTextualObjeto.emPortugues("boneca", "bonecas"),
                ReferenciaVisual.de("boneca")
        );

        assertNotEquals(rosaBranca, rosaAmarela);
        assertTrue(rosaBranca.pertenceAMesmaFamilia(rosaAmarela));
        assertTrue(rosaAmarela.pertenceAMesmaFamilia(rosaBranca));
        assertTrue(rosaBranca.pertenceAFamilia(familiaRosa));

        assertFalse(rosaBranca.pertenceAMesmaFamilia(boneca));
        assertFalse(rosaBranca.pertenceAFamilia(familiaBoneca));
        assertFalse(rosaBranca.pertenceAMesmaFamilia(null));
    }

    @Test
    @DisplayName("Deve consultar corretamente existência e valor de características")
    void deveConsultarCaracteristicas() {
        CaracteristicaObjeto cor = CaracteristicaObjeto.de("cor", "branca");
        CaracteristicaObjeto tamanho = CaracteristicaObjeto.de("tamanho", "media");

        ObjetoContado objeto = new ObjetoContado("rosa_branca", familiaRosa, Set.of(cor, tamanho), textoRosa, null);

        assertTrue(objeto.possuiCaracteristica(cor));
        assertFalse(objeto.possuiCaracteristica(CaracteristicaObjeto.de("cor", "amarela")));
        assertEquals("branca", objeto.valorDaCaracteristica("cor"));
        assertEquals("media", objeto.valorDaCaracteristica("tamanho"));
        assertNull(objeto.valorDaCaracteristica("material"));
    }

    @Test
    @DisplayName("Deve produzir quantidades vinculadas a si mesmo via quantificar e contar")
    void deveProduzirQuantidadesVinculadas() {
        ObjetoContado objeto = new ObjetoContado("rosa_branca", familiaRosa, Set.of(), textoRosa, null);

        Quantidade qtdContar = objeto.contar(3);
        assertEquals(3, qtdContar.getValor());
        assertEquals(objeto, qtdContar.getObjeto());
        assertEquals(GrandezaQuantitativa.CONTAGEM, qtdContar.getGrandeza());
        assertEquals(UnidadeMedida.UNIDADE, qtdContar.getUnidade());

        Quantidade qtdQuantificar = objeto.quantificar(Numero.de(5), GrandezaQuantitativa.CONTAGEM, UnidadeMedida.UNIDADE);
        assertEquals(5, qtdQuantificar.getValor());
        assertEquals(objeto, qtdQuantificar.getObjeto());
    }

    @Test
    @DisplayName("Deve basear igualdade e código hash exclusivamente no id")
    void deveBasearEqualsEHashCodeNoId() {
        ObjetoContado o1 = new ObjetoContado("rosa_1", familiaRosa, Set.of(), textoRosa, null);
        ObjetoContado o2 = new ObjetoContado("rosa_1", familiaBoneca, Set.of(), textoRosa, null);
        ObjetoContado o3 = new ObjetoContado("rosa_2", familiaRosa, Set.of(), textoRosa, null);

        assertEquals(o1, o1);
        assertEquals(o1, o2);
        assertEquals(o1.hashCode(), o2.hashCode());

        assertNotEquals(o1, o3);
        assertNotEquals(o1, null);
        assertNotEquals(o1, "rosa_1");
    }

    @Test
    @DisplayName("Deve retornar o id no toString")
    void deveRetornarIdNoToString() {
        ObjetoContado o = new ObjetoContado("rosa_branca", familiaRosa, Set.of(), textoRosa, null);
        assertEquals("rosa_branca", o.toString());
    }
}
