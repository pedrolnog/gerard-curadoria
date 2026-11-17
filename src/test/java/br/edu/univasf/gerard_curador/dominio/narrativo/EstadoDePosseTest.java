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

@DisplayName("Estado de Posse de um participante em um momento")
class EstadoDePosseTest {

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

    private final ParticipanteNarrativo vovo = new ParticipanteNarrativo("vovo", "Vovó");
    private final ParticipanteNarrativo netinha = new ParticipanteNarrativo("netinha", "Netinha");

    private final MarcadorTemporal momentoInicial = MarcadorTemporal.daOrdem(1);
    private final MarcadorTemporal momento2 = MarcadorTemporal.daOrdem(2);
    private final MarcadorTemporal momento3 = MarcadorTemporal.daOrdem(3);

    private final Quantidade duasBrancas = rosaBranca.contar(2);
    private final Quantidade tresAmarelas = rosaAmarela.contar(3);
    private final Quantidade umaBranca = rosaBranca.contar(1);
    private final Quantidade umaAmarela = rosaAmarela.contar(1);
    private final Quantidade tresBrancas = rosaBranca.contar(3);

    @Test
    @DisplayName("Estado inicial da Vovó possui 2 rosas brancas e 3 amarelas")
    void estadoInicialDaVovoPossuiCincoRosas() {
        Inventario inventario = Inventario.com(duasBrancas, tresAmarelas);
        EstadoDePosse estado = new EstadoDePosse(vovo, inventario, momentoInicial);

        assertEquals(vovo, estado.getPossuidor());
        assertEquals(momentoInicial, estado.getMomento());
        assertEquals(Numero.de(5), estado.totalDaFamilia(familiaRosa));
        assertEquals(Numero.de(5), estado.totalGeral());
        assertEquals(duasBrancas, estado.quantidadeDe(rosaBranca));
        assertEquals(tresAmarelas, estado.quantidadeDe(rosaAmarela));
        assertTrue(estado.pertenceA(vovo));
        assertFalse(estado.pertenceA(netinha));
    }

    @Test
    @DisplayName("Estado de posse é imutável: retirar e acrescentar produzem novas instâncias mantendo o original intacto")
    void estadoDePosseEImutavel() {
        Inventario inventario = Inventario.com(duasBrancas, tresAmarelas);
        EstadoDePosse inicial = new EstadoDePosse(vovo, inventario, momentoInicial);

        EstadoDePosse aposRetirarBranca = inicial.retirar(umaBranca, momento2);
        assertNotSame(inicial, aposRetirarBranca);
        assertEquals(momentoInicial, inicial.getMomento());
        assertEquals(Numero.de(5), inicial.totalDaFamilia(familiaRosa));
        assertEquals(momento2, aposRetirarBranca.getMomento());
        assertEquals(Numero.de(4), aposRetirarBranca.totalDaFamilia(familiaRosa));

        EstadoDePosse aposAcrescentar = aposRetirarBranca.acrescentar(umaBranca, momento3);
        assertNotSame(aposRetirarBranca, aposAcrescentar);
        assertEquals(momento3, aposAcrescentar.getMomento());
        assertEquals(Numero.de(5), aposAcrescentar.totalDaFamilia(familiaRosa));
    }

    @Test
    @DisplayName("Retirar além da disponibilidade propaga RetiradaImpossivelException e mantém estado intacto")
    void retiradaImpossivelNaoCorrompeEstado() {
        Inventario inventario = Inventario.com(duasBrancas, tresAmarelas);
        EstadoDePosse estado = new EstadoDePosse(vovo, inventario, momentoInicial);

        assertThrows(RetiradaImpossivelException.class, () -> estado.retirar(tresBrancas, momento2));
        assertEquals(Numero.de(5), estado.totalDaFamilia(familiaRosa));
        assertEquals(momentoInicial, estado.getMomento());
    }

    @Test
    @DisplayName("Comporta consulta capacidade do inventário sem efeitos colaterais")
    void comportaVerificaCapacidade() {
        Inventario inventario = Inventario.com(duasBrancas, tresAmarelas);
        EstadoDePosse estado = new EstadoDePosse(vovo, inventario, momentoInicial);

        assertTrue(estado.comporta(duasBrancas));
        assertTrue(estado.comporta(umaAmarela));
        assertFalse(estado.comporta(tresBrancas));
    }

    @Test
    @DisplayName("PossuiMesmoConteudoQue compara participante e itens possuídos independentemente do momento")
    void comparaConteudoIgnorandoMomento() {
        Inventario inv1 = Inventario.com(duasBrancas, tresAmarelas);
        Inventario inv2 = Inventario.com(tresAmarelas, duasBrancas);

        EstadoDePosse estado1 = new EstadoDePosse(vovo, inv1, momentoInicial);
        EstadoDePosse estado2 = new EstadoDePosse(vovo, inv2, momento2);
        EstadoDePosse estadoNetinha = new EstadoDePosse(netinha, inv1, momentoInicial);

        assertTrue(estado1.possuiMesmoConteudoQue(estado2));
        assertFalse(estado1.possuiMesmoConteudoQue(estadoNetinha));
        assertFalse(estado1.possuiMesmoConteudoQue(null));
    }

    @Test
    @DisplayName("Descrever formata o possuidor e a descrição do inventário")
    void descreverEstado() {
        Inventario inventario = Inventario.com(duasBrancas, tresAmarelas);
        EstadoDePosse estado = new EstadoDePosse(vovo, inventario, momentoInicial);

        assertEquals("Vovó possui 2 rosas brancas e 3 rosas amarelas", estado.descrever());
        assertEquals(estado.descrever(), estado.toString());
    }

    @Test
    @DisplayName("Construtor valida obrigatoriedade de possuidor, inventário e momento")
    void validacoesConstrutor() {
        Inventario inventario = Inventario.com(duasBrancas);

        assertThrows(IllegalArgumentException.class, () -> new EstadoDePosse(null, inventario, momentoInicial));
        assertThrows(IllegalArgumentException.class, () -> new EstadoDePosse(vovo, null, momentoInicial));
        assertThrows(IllegalArgumentException.class, () -> new EstadoDePosse(vovo, inventario, null));
    }
}
