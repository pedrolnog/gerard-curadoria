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

@DisplayName("Transformação Narrativa entre estados de posse")
class TransformacaoNarrativaTest {

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

    private final AcaoNarrativa deu = AcaoNarrativa.transferencia(
            "dar", "deu", "dado", SentidoTransformacao.RETIRADA
    );

    private final MarcadorTemporal momentoInicial = MarcadorTemporal.daOrdem(1);
    private final MarcadorTemporal momentoPrimeiraDoacao = MarcadorTemporal.de(2, "primeira doação");
    private final MarcadorTemporal momentoSegundaDoacao = MarcadorTemporal.de(3, "segunda doação");

    private final Quantidade duasBrancas = rosaBranca.contar(2);
    private final Quantidade tresAmarelas = rosaAmarela.contar(3);
    private final Quantidade umaBranca = rosaBranca.contar(1);
    private final Quantidade umaAmarela = rosaAmarela.contar(1);

    @Test
    @DisplayName("Transformação sem ator é rejeitada no construtor com IllegalArgumentException (caso 6)")
    void transformacaoSemAtorERejeitada() {
        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> new TransformacaoNarrativa(null, deu, netinha, umaBranca, momentoPrimeiraDoacao)
        );
        assertEquals("transformacao exige ator", excecao.getMessage());

        assertThrows(
                IllegalArgumentException.class,
                () -> TransformacaoNarrativa.semDestinatario(null, deu, umaBranca, momentoPrimeiraDoacao)
        );
    }

    @Test
    @DisplayName("AplicarSobre deduz rosas da Vovó na sequência de doações")
    void aplicarSobreAtualizaEstadoDoAtor() {
        Inventario inventarioInicial = Inventario.com(duasBrancas, tresAmarelas);
        EstadoDePosse estadoVovo = new EstadoDePosse(vovo, inventarioInicial, momentoInicial);

        TransformacaoNarrativa trans1 = new TransformacaoNarrativa(
                vovo, deu, netinha, umaBranca, momentoPrimeiraDoacao
        );
        TransformacaoNarrativa trans2 = new TransformacaoNarrativa(
                vovo, deu, netinha, umaAmarela, momentoSegundaDoacao
        );

        EstadoDePosse vovoAposTrans1 = trans1.aplicarSobre(estadoVovo);
        assertEquals(Numero.de(1), vovoAposTrans1.quantidadeDe(rosaBranca).getNumero());
        assertEquals(Numero.de(3), vovoAposTrans1.quantidadeDe(rosaAmarela).getNumero());
        assertEquals(momentoPrimeiraDoacao, vovoAposTrans1.getMomento());

        EstadoDePosse vovoAposTrans2 = trans2.aplicarSobre(vovoAposTrans1);
        assertEquals(Numero.de(1), vovoAposTrans2.quantidadeDe(rosaBranca).getNumero());
        assertEquals(Numero.de(2), vovoAposTrans2.quantidadeDe(rosaAmarela).getNumero());
        assertEquals(Numero.de(3), vovoAposTrans2.totalDaFamilia(familiaRosa));
        assertEquals(momentoSegundaDoacao, vovoAposTrans2.getMomento());
    }

    @Test
    @DisplayName("AplicarSobreDestinatario espelha o efeito e a Netinha recebe as rosas transferidas")
    void aplicarSobreDestinatarioEspelhaEfeito() {
        TransformacaoNarrativa trans1 = new TransformacaoNarrativa(
                vovo, deu, netinha, umaBranca, momentoPrimeiraDoacao
        );
        TransformacaoNarrativa trans2 = new TransformacaoNarrativa(
                vovo, deu, netinha, umaAmarela, momentoSegundaDoacao
        );

        EstadoDePosse estadoNetinha = new EstadoDePosse(netinha, Inventario.vazio(), momentoInicial);

        EstadoDePosse netinhaAposTrans1 = trans1.aplicarSobreDestinatario(estadoNetinha);
        assertEquals(Numero.de(1), netinhaAposTrans1.quantidadeDe(rosaBranca).getNumero());
        assertEquals(Numero.de(1), netinhaAposTrans1.totalDaFamilia(familiaRosa));
        assertEquals(momentoPrimeiraDoacao, netinhaAposTrans1.getMomento());

        EstadoDePosse netinhaAposTrans2 = trans2.aplicarSobreDestinatario(netinhaAposTrans1);
        assertEquals(Numero.de(1), netinhaAposTrans2.quantidadeDe(rosaBranca).getNumero());
        assertEquals(Numero.de(1), netinhaAposTrans2.quantidadeDe(rosaAmarela).getNumero());
        assertEquals(Numero.de(2), netinhaAposTrans2.totalDaFamilia(familiaRosa));
        assertEquals(momentoSegundaDoacao, netinhaAposTrans2.getMomento());
    }

    @Test
    @DisplayName("AplicarSobreDestinatario falha com IllegalStateException em transformações sem destinatário")
    void aplicarSobreDestinatarioRejeitaNaoTransferencia() {
        AcaoNarrativa comeu = AcaoNarrativa.semDestinatario(
                "comer", "comeu", "comido", SentidoTransformacao.RETIRADA
        );
        TransformacaoNarrativa trans = TransformacaoNarrativa.semDestinatario(
                vovo, comeu, umaBranca, momentoPrimeiraDoacao
        );

        EstadoDePosse estado = new EstadoDePosse(netinha, Inventario.vazio(), momentoInicial);
        assertThrows(IllegalStateException.class, () -> trans.aplicarSobreDestinatario(estado));
    }

    @Test
    @DisplayName("PodeSerAplicadaSobre verifica disponibilidade sem aplicar a transformação")
    void podeSerAplicadaSobreVerificaDisponibilidade() {
        Inventario inventario = Inventario.com(duasBrancas);
        EstadoDePosse estado = new EstadoDePosse(vovo, inventario, momentoInicial);

        TransformacaoNarrativa doarUmaBranca = new TransformacaoNarrativa(
                vovo, deu, netinha, umaBranca, momentoPrimeiraDoacao
        );
        TransformacaoNarrativa doarTresBrancas = new TransformacaoNarrativa(
                vovo, deu, netinha, rosaBranca.contar(3), momentoPrimeiraDoacao
        );

        assertTrue(doarUmaBranca.podeSerAplicadaSobre(estado));
        assertFalse(doarTresBrancas.podeSerAplicadaSobre(estado));
        assertFalse(doarUmaBranca.podeSerAplicadaSobre(null));
    }

    @Test
    @DisplayName("Descrever formata a transformação com sujeito, verbo, quantidade e destinatário")
    void descreverTransformacao() {
        TransformacaoNarrativa trans = new TransformacaoNarrativa(
                vovo, deu, netinha, umaBranca, momentoPrimeiraDoacao
        );

        assertEquals("Vovó deu 1 rosa branca para Netinha", trans.descrever());
        assertEquals("primeira doação: Vovó deu 1 rosa branca para Netinha", trans.toString());
    }

    @Test
    @DisplayName("OcupaMesmaOrdemQue verifica se duas transformações disputam a mesma ordem temporal")
    void ocupaMesmaOrdemQue() {
        TransformacaoNarrativa trans1 = new TransformacaoNarrativa(
                vovo, deu, netinha, umaBranca, momentoPrimeiraDoacao
        );
        TransformacaoNarrativa transMesmaOrdem = new TransformacaoNarrativa(
                vovo, deu, netinha, umaAmarela, MarcadorTemporal.de(2, "outro rotulo")
        );
        TransformacaoNarrativa transOutraOrdem = new TransformacaoNarrativa(
                vovo, deu, netinha, umaAmarela, momentoSegundaDoacao
        );

        assertTrue(trans1.ocupaMesmaOrdemQue(transMesmaOrdem));
        assertFalse(trans1.ocupaMesmaOrdemQue(transOutraOrdem));
    }

    @Test
    @DisplayName("Construtor valida obrigatoriedade de campos e invariantes de destinatário")
    void validacoesConstrutor() {
        // Ação nula
        assertThrows(IllegalArgumentException.class, () ->
                new TransformacaoNarrativa(vovo, null, netinha, umaBranca, momentoPrimeiraDoacao));

        // Quantidade nula
        assertThrows(IllegalArgumentException.class, () ->
                new TransformacaoNarrativa(vovo, deu, netinha, null, momentoPrimeiraDoacao));

        // Marcador nulo
        assertThrows(IllegalArgumentException.class, () ->
                new TransformacaoNarrativa(vovo, deu, netinha, umaBranca, null));

        // Ação exige destinatário, mas destinatário é nulo
        assertThrows(IllegalArgumentException.class, () ->
                new TransformacaoNarrativa(vovo, deu, null, umaBranca, momentoPrimeiraDoacao));

        // Ator e destinatário iguais
        assertThrows(IllegalArgumentException.class, () ->
                new TransformacaoNarrativa(vovo, deu, vovo, umaBranca, momentoPrimeiraDoacao));
    }
}
