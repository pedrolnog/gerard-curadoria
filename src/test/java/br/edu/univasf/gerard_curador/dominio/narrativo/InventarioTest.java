package br.edu.univasf.gerard_curador.dominio.narrativo;

import br.edu.univasf.gerard_curador.dominio.excecao.RetiradaImpossivelException;
import br.edu.univasf.gerard_curador.dominio.quantitativo.CaracteristicaObjeto;
import br.edu.univasf.gerard_curador.dominio.quantitativo.FamiliaObjeto;
import br.edu.univasf.gerard_curador.dominio.quantitativo.Numero;
import br.edu.univasf.gerard_curador.dominio.quantitativo.ObjetoContado;
import br.edu.univasf.gerard_curador.dominio.quantitativo.Quantidade;
import br.edu.univasf.gerard_curador.dominio.quantitativo.RealizacaoTextualObjeto;
import br.edu.univasf.gerard_curador.dominio.quantitativo.ReferenciaVisual;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Inventário de quantidades possuídas")
class InventarioTest {

    private final FamiliaObjeto familiaRosa = new FamiliaObjeto("rosa", "Rosa");

    private final ObjetoContado rosaBranca = new ObjetoContado(
            "rosa_branca",
            familiaRosa,
            Set.of(CaracteristicaObjeto.de("cor", "branca")),
            RealizacaoTextualObjeto.emPortugues("rosa branca", "rosas brancas"),
            ReferenciaVisual.de("rosa_branca")
    );

    private final ObjetoContado rosaAmarela = new ObjetoContado(
            "rosa_amarela",
            familiaRosa,
            Set.of(CaracteristicaObjeto.de("cor", "amarela")),
            RealizacaoTextualObjeto.emPortugues("rosa amarela", "rosas amarelas"),
            ReferenciaVisual.de("rosa_amarela")
    );

    private final Quantidade duasBrancas = rosaBranca.contar(2);
    private final Quantidade tresAmarelas = rosaAmarela.contar(3);
    private final Quantidade umaBranca = rosaBranca.contar(1);
    private final Quantidade umaAmarela = rosaAmarela.contar(1);
    private final Quantidade tresBrancas = rosaBranca.contar(3);

    @Test
    @DisplayName("Inventário inicial totaliza cinco rosas pela família (caso 1)")
    void inventarioInicialTotalizaCincoRosasPelaFamilia() {
        Inventario inventario = Inventario.com(duasBrancas, tresAmarelas);

        Numero totalRosas = inventario.totalDaFamilia(familiaRosa);

        assertEquals(Numero.de(5), totalRosas);
        assertEquals(Numero.de(2), inventario.numeroDe(rosaBranca));
        assertEquals(Numero.de(3), inventario.numeroDe(rosaAmarela));
    }

    @Test
    @DisplayName("Retirar uma rosa branca deixa uma rosa branca (caso 2)")
    void retirarUmaBrancaDeixaUmaBranca() {
        Inventario inventario = Inventario.com(duasBrancas, tresAmarelas);

        Inventario aposRetirarBranca = inventario.retirar(umaBranca);

        assertEquals(Numero.de(1), aposRetirarBranca.numeroDe(rosaBranca));
        assertEquals(Numero.de(3), aposRetirarBranca.numeroDe(rosaAmarela));
    }

    @Test
    @DisplayName("Retirar uma rosa amarela depois deixa duas rosas amarelas (caso 3)")
    void retirarUmaAmarelaDepoisDeixaDuasAmarelas() {
        Inventario inventario = Inventario.com(duasBrancas, tresAmarelas);
        Inventario aposRetirarBranca = inventario.retirar(umaBranca);

        Inventario aposRetirarAmarela = aposRetirarBranca.retirar(umaAmarela);

        assertEquals(Numero.de(1), aposRetirarAmarela.numeroDe(rosaBranca));
        assertEquals(Numero.de(2), aposRetirarAmarela.numeroDe(rosaAmarela));
    }

    @Test
    @DisplayName("Total final após as retiradas resulta em três rosas (caso 4)")
    void totalFinalTresRosas() {
        Inventario inventario = Inventario.com(duasBrancas, tresAmarelas)
                .retirar(umaBranca)
                .retirar(umaAmarela);

        assertEquals(Numero.de(3), inventario.totalDaFamilia(familiaRosa));
        assertEquals(Numero.de(3), inventario.totalGeral());
    }

    @Test
    @DisplayName("Retirar três brancas quando existem duas lança RetiradaImpossivelException e mantém inventário com duas (caso 5)")
    void retirarMaisDoQuePossuiLancaExcecaoEMantemEstadoOriginal() {
        Inventario inventario = Inventario.com(duasBrancas, tresAmarelas);

        assertThrows(RetiradaImpossivelException.class, () -> inventario.retirar(tresBrancas));

        assertEquals(Numero.de(2), inventario.numeroDe(rosaBranca));
        assertEquals(Numero.de(3), inventario.numeroDe(rosaAmarela));
        assertEquals(Numero.de(5), inventario.totalDaFamilia(familiaRosa));
    }

    @Test
    @DisplayName("Características diferentes da mesma família são totalizadas juntas (caso 16)")
    void caracteristicasDiferentesDaMesmaFamiliaSaoTotalizadasJuntas() {
        Inventario inventario = Inventario.com(duasBrancas, tresAmarelas);

        assertFalse(rosaBranca.getCaracteristicas().equals(rosaAmarela.getCaracteristicas()));
        assertEquals(familiaRosa, rosaBranca.getFamilia());
        assertEquals(familiaRosa, rosaAmarela.getFamilia());
        assertEquals(Numero.de(5), inventario.totalDaFamilia(familiaRosa));
    }

    @Test
    @DisplayName("Inventário é imutável e operações de adicionar e retirar devolvem nova instância")
    void inventarioEImutavel() {
        Inventario inicial = Inventario.com(duasBrancas);

        Inventario aposAdicao = inicial.adicionar(tresAmarelas);
        assertNotSame(inicial, aposAdicao);
        assertEquals(Numero.de(2), inicial.totalGeral());
        assertEquals(Numero.de(5), aposAdicao.totalGeral());

        Inventario aposRetirada = aposAdicao.retirar(umaBranca);
        assertNotSame(aposAdicao, aposRetirada);
        assertEquals(Numero.de(5), aposAdicao.totalGeral());
        assertEquals(Numero.de(4), aposRetirada.totalGeral());
    }

    @Test
    @DisplayName("Retirar objeto inexistente no inventário lança RetiradaImpossivelException")
    void retirarObjetoInexistenteLancaExcecao() {
        Inventario inventario = Inventario.com(tresAmarelas);

        assertThrows(RetiradaImpossivelException.class, () -> inventario.retirar(umaBranca));
    }

    @Test
    @DisplayName("Comporta retorna verdadeiro apenas quando a quantidade pretendida está disponível")
    void comportaVerificaDisponibilidadeSemEfeitoColateral() {
        Inventario inventario = Inventario.com(duasBrancas, tresAmarelas);

        assertTrue(inventario.comporta(duasBrancas));
        assertTrue(inventario.comporta(umaBranca));
        assertFalse(inventario.comporta(tresBrancas));
        assertFalse(inventario.comporta(null));
    }

    @Test
    @DisplayName("Totalização ignora objetos de famílias diferentes")
    void totalizacaoIgnoraOutrasFamilias() {
        FamiliaObjeto familiaBoneca = new FamiliaObjeto("boneca", "Boneca");
        ObjetoContado boneca = new ObjetoContado(
                "boneca",
                familiaBoneca,
                Set.of(),
                RealizacaoTextualObjeto.emPortugues("boneca", "bonecas"),
                ReferenciaVisual.de("boneca")
        );

        Inventario inventario = Inventario.com(duasBrancas, boneca.contar(4));

        assertEquals(Numero.de(2), inventario.totalDaFamilia(familiaRosa));
        assertEquals(Numero.de(4), inventario.totalDaFamilia(familiaBoneca));
        assertEquals(Numero.de(6), inventario.totalGeral());
    }

    @Test
    @DisplayName("Dois inventários possuem o mesmo conteúdo quando os itens presentes coincidem")
    void comparacaoDeConteudoEntreInventarios() {
        Inventario inv1 = Inventario.com(duasBrancas, tresAmarelas);
        Inventario inv2 = Inventario.com(tresAmarelas, duasBrancas);

        assertTrue(inv1.possuiMesmoConteudoQue(inv2));
        assertFalse(inv1.possuiMesmoConteudoQue(null));
        assertFalse(inv1.possuiMesmoConteudoQue(Inventario.com(duasBrancas)));
    }

    @Test
    @DisplayName("Descrever formata o inventário textualizando os itens presentes")
    void descreverInventario() {
        Inventario inventario = Inventario.com(duasBrancas, tresAmarelas);

        assertEquals("2 rosas brancas e 3 rosas amarelas", inventario.descrever());
        assertEquals("nada", Inventario.vazio().descrever());
    }

    @Test
    @DisplayName("Operações rejeitam argumentos nulos com IllegalArgumentException")
    void validacoesDeNulo() {
        Inventario inventario = Inventario.com(duasBrancas);

        assertThrows(IllegalArgumentException.class, () -> inventario.adicionar(null));
        assertThrows(IllegalArgumentException.class, () -> inventario.retirar(null));
        assertThrows(IllegalArgumentException.class, () -> inventario.totalDaFamilia(null));
    }
}
