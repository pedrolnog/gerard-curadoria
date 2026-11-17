package br.edu.univasf.gerard_curador.dominio.narrativo;

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

@DisplayName("Sentido da Transformação Narrativa")
class SentidoTransformacaoTest {

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

    private final MarcadorTemporal momentoInicial = MarcadorTemporal.daOrdem(1);
    private final MarcadorTemporal momentoSeguinte = MarcadorTemporal.daOrdem(2);

    private final Quantidade duasBrancas = rosaBranca.contar(2);
    private final Quantidade tresAmarelas = rosaAmarela.contar(3);
    private final Quantidade umaBranca = rosaBranca.contar(1);
    private final Quantidade quatroBrancas = rosaBranca.contar(4);

    @Test
    @DisplayName("Sentido ACRÉSCIMO acrescenta quantidade ao estado por polimorfismo por constante")
    void acrescimoAcrescentaQuantidadeAoEstado() {
        Inventario inventario = Inventario.com(duasBrancas, tresAmarelas);
        EstadoDePosse estado = new EstadoDePosse(vovo, inventario, momentoInicial);

        EstadoDePosse resultado = SentidoTransformacao.ACRESCIMO.aplicar(estado, umaBranca, momentoSeguinte);

        assertEquals(Numero.de(3), resultado.quantidadeDe(rosaBranca).getNumero());
        assertEquals(Numero.de(3), resultado.quantidadeDe(rosaAmarela).getNumero());
        assertEquals(momentoSeguinte, resultado.getMomento());
        assertEquals(1, SentidoTransformacao.ACRESCIMO.getFator());
        assertTrue(SentidoTransformacao.ACRESCIMO.ehAcrescimo());
        assertFalse(SentidoTransformacao.ACRESCIMO.ehRetirada());
        assertEquals("acrescimo", SentidoTransformacao.ACRESCIMO.getNomeExibido());
    }

    @Test
    @DisplayName("Sentido RETIRADA retira quantidade do estado por polimorfismo por constante")
    void retiradaRetiraQuantidadeDoEstado() {
        Inventario inventario = Inventario.com(duasBrancas, tresAmarelas);
        EstadoDePosse estado = new EstadoDePosse(vovo, inventario, momentoInicial);

        EstadoDePosse resultado = SentidoTransformacao.RETIRADA.aplicar(estado, umaBranca, momentoSeguinte);

        assertEquals(Numero.de(1), resultado.quantidadeDe(rosaBranca).getNumero());
        assertEquals(Numero.de(3), resultado.quantidadeDe(rosaAmarela).getNumero());
        assertEquals(momentoSeguinte, resultado.getMomento());
        assertEquals(-1, SentidoTransformacao.RETIRADA.getFator());
        assertTrue(SentidoTransformacao.RETIRADA.ehRetirada());
        assertFalse(SentidoTransformacao.RETIRADA.ehAcrescimo());
        assertEquals("retirada", SentidoTransformacao.RETIRADA.getNomeExibido());
    }

    @Test
    @DisplayName("PodeSerAplicado consulta viabilidade sem causar efeitos colaterais")
    void podeSerAplicadoVerificaDisponibilidade() {
        Inventario inventario = Inventario.com(duasBrancas, tresAmarelas);
        EstadoDePosse estado = new EstadoDePosse(vovo, inventario, momentoInicial);

        // ACRÉSCIMO sempre pode ser aplicado
        assertTrue(SentidoTransformacao.ACRESCIMO.podeSerAplicado(estado, umaBranca));
        assertTrue(SentidoTransformacao.ACRESCIMO.podeSerAplicado(estado, quatroBrancas));

        // RETIRADA só pode ser aplicada se o estado comportar
        assertTrue(SentidoTransformacao.RETIRADA.podeSerAplicado(estado, umaBranca));
        assertTrue(SentidoTransformacao.RETIRADA.podeSerAplicado(estado, duasBrancas));
        assertFalse(SentidoTransformacao.RETIRADA.podeSerAplicado(estado, quatroBrancas));
    }

    @Test
    @DisplayName("Inverter alterna entre ACRÉSCIMO e RETIRADA")
    void inverterSentido() {
        assertEquals(SentidoTransformacao.RETIRADA, SentidoTransformacao.ACRESCIMO.inverter());
        assertEquals(SentidoTransformacao.ACRESCIMO, SentidoTransformacao.RETIRADA.inverter());
    }
}
