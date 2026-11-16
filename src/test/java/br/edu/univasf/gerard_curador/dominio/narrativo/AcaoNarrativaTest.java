package br.edu.univasf.gerard_curador.dominio.narrativo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Ação Narrativa curada")
class AcaoNarrativaTest {

    @Test
    @DisplayName("Ação de transferência define destinatário obrigatório e preposição padrão coerente com o sentido")
    void acaoTransferenciaComPreposicaoPadrao() {
        AcaoNarrativa deu = AcaoNarrativa.transferencia(
                "dar", "deu", "dado", SentidoTransformacao.RETIRADA
        );

        assertEquals("dar", deu.getId());
        assertEquals("deu", deu.getVerbo());
        assertEquals("dado", deu.getParticipio());
        assertEquals(SentidoTransformacao.RETIRADA, deu.getSentido());
        assertTrue(deu.exigeDestinatario());
        assertTrue(deu.ehTransferencia());
        assertEquals("para", deu.getPreposicaoDoDestinatario());

        AcaoNarrativa ganhou = AcaoNarrativa.transferencia(
                "ganhar", "ganhou", "ganhado", SentidoTransformacao.ACRESCIMO
        );
        assertEquals("de", ganhou.getPreposicaoDoDestinatario());
    }

    @Test
    @DisplayName("Ação de transferência com regência explícita respeita a preposição informada")
    void acaoTransferenciaComPreposicaoExplicita() {
        AcaoNarrativa entregou = AcaoNarrativa.transferencia(
                "entregar", "entregou", "entregue", SentidoTransformacao.RETIRADA, "a"
        );

        assertEquals("a", entregou.getPreposicaoDoDestinatario());
        assertTrue(entregou.ehTransferencia());
    }

    @Test
    @DisplayName("Ação sem destinatário representa consumo ou aparecimento sem contraparte")
    void acaoSemDestinatario() {
        AcaoNarrativa perdeu = AcaoNarrativa.semDestinatario(
                "perder", "perdeu", "perdido", SentidoTransformacao.RETIRADA
        );

        assertEquals("perder", perdeu.getId());
        assertFalse(perdeu.exigeDestinatario());
        assertFalse(perdeu.ehTransferencia());
        assertEquals("para", perdeu.getPreposicaoDoDestinatario());
    }

    @Test
    @DisplayName("Igualdade e hashCode são baseados exclusivamente no identificador da ação")
    void igualdadePorId() {
        AcaoNarrativa acao1 = new AcaoNarrativa("dar", "deu", "dado", SentidoTransformacao.RETIRADA, true);
        AcaoNarrativa acao2 = new AcaoNarrativa("dar", "doou", "doado", SentidoTransformacao.RETIRADA, true);
        AcaoNarrativa acaoOutra = new AcaoNarrativa("comer", "comeu", "comido", SentidoTransformacao.RETIRADA, false);

        assertEquals(acao1, acao2);
        assertEquals(acao1.hashCode(), acao2.hashCode());
        assertNotEquals(acao1, acaoOutra);
        assertNotEquals(acao1, null);
        assertNotEquals(acao1, "dar");
    }

    @Test
    @DisplayName("ToString retorna o verbo curado")
    void toStringRetornaVerbo() {
        AcaoNarrativa deu = AcaoNarrativa.transferencia(
                "dar", "deu", "dado", SentidoTransformacao.RETIRADA
        );

        assertEquals("deu", deu.toString());
    }

    @Test
    @DisplayName("Construtor valida obrigatoriedade de campos")
    void validacoesDoConstrutor() {
        assertThrows(IllegalArgumentException.class, () ->
                new AcaoNarrativa(null, "deu", "dado", SentidoTransformacao.RETIRADA, true));
        assertThrows(IllegalArgumentException.class, () ->
                new AcaoNarrativa("dar", null, "dado", SentidoTransformacao.RETIRADA, true));
        assertThrows(IllegalArgumentException.class, () ->
                new AcaoNarrativa("dar", "deu", null, SentidoTransformacao.RETIRADA, true));
        assertThrows(IllegalArgumentException.class, () ->
                new AcaoNarrativa("dar", "deu", "dado", null, true));
        assertThrows(IllegalArgumentException.class, () ->
                new AcaoNarrativa("dar", "deu", "dado", SentidoTransformacao.RETIRADA, true, null));
    }
}
