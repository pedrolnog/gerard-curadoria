package br.edu.univasf.gerard_curador.dominio.estrutural;

import br.edu.univasf.gerard_curador.dominio.quantitativo.FamiliaObjeto;
import br.edu.univasf.gerard_curador.dominio.quantitativo.ObjetoContado;
import br.edu.univasf.gerard_curador.dominio.quantitativo.Quantidade;
import br.edu.univasf.gerard_curador.dominio.quantitativo.RealizacaoTextualObjeto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Incógnita Quantitativa")
class IncognitaQuantitativaTest {

    private ObjetoContado rosa;

    @BeforeEach
    void setUp() {
        rosa = new ObjetoContado(
                "rosa",
                new FamiliaObjeto("rosa", "Rosa"),
                Set.of(),
                RealizacaoTextualObjeto.emPortugues("rosa", "rosas"),
                null
        );
    }

    @Test
    @DisplayName("Incógnita preserva a designação do papel original após ser resolvida (Caso 13: estado inicial desconhecido continua designado como ESTADO_INICIAL)")
    void incognitaPreservaDesignacaoAposResolvida() {
        IncognitaQuantitativa incognita = IncognitaQuantitativa.designar(
                PapelQuantitativo.ESTADO_INICIAL,
                "Quantas rosas a vovó tinha no início?"
        );

        assertFalse(incognita.estaResolvida());
        assertEquals(PapelQuantitativo.ESTADO_INICIAL, incognita.getPapelOriginal());
        assertTrue(incognita.designa(PapelQuantitativo.ESTADO_INICIAL));
        assertNull(incognita.getValorResolvido());

        Quantidade valorResolvido = rosa.contar(5);
        incognita.resolver(valorResolvido);

        // A própria incógnita resolvida preserva a designação do papel original
        assertTrue(incognita.estaResolvida());
        assertEquals(PapelQuantitativo.ESTADO_INICIAL, incognita.getPapelOriginal());
        assertTrue(incognita.designa(PapelQuantitativo.ESTADO_INICIAL));
        assertEquals(valorResolvido, incognita.getValorResolvido());
        assertEquals("Quantas rosas a vovó tinha no início?", incognita.getDescricaoDaPergunta());
    }

    @Test
    @DisplayName("Chave do papel e descrições textual e completa refletem o papel original")
    void chaveDoPapelEDescricoes() {
        IncognitaQuantitativa incognita = IncognitaQuantitativa.designar(
                PapelQuantitativo.ESTADO_INICIAL,
                "Quantas rosas havia no início?"
        );

        assertEquals("papel.ESTADO_INICIAL", incognita.chaveDoPapel());
        assertEquals("Quantas rosas havia no início?", incognita.descrever());
        assertEquals("Quantas rosas havia no início? [papel original: estado inicial]", incognita.descreverComPapel());
        assertEquals(incognita.descreverComPapel(), incognita.toString());

        incognita.resolver(rosa.contar(5));
        assertEquals("Quantas rosas havia no início? [papel original: estado inicial] = 5 rosas", incognita.descreverComPapel());
    }

    @Test
    @DisplayName("Tentativa de resolver novamente uma incógnita já resolvida lança IllegalStateException")
    void resolverIncognitaJaResolvidaLancaExcecao() {
        IncognitaQuantitativa incognita = IncognitaQuantitativa.designar(
                PapelQuantitativo.ESTADO_INICIAL,
                "Quantas rosas?"
        );
        incognita.resolver(rosa.contar(5));

        assertThrows(IllegalStateException.class, () -> incognita.resolver(rosa.contar(5)));
    }

    @Test
    @DisplayName("Validação de argumentos obrigatórios na designação e na resolução")
    void validacaoDeArgumentosObrigatorios() {
        assertThrows(IllegalArgumentException.class, () ->
                IncognitaQuantitativa.designar(null, "Pergunta?"));
        assertThrows(IllegalArgumentException.class, () ->
                IncognitaQuantitativa.designar(PapelQuantitativo.ESTADO_INICIAL, null));
        assertThrows(IllegalArgumentException.class, () ->
                IncognitaQuantitativa.designar(PapelQuantitativo.ESTADO_INICIAL, "   "));

        IncognitaQuantitativa incognita = IncognitaQuantitativa.designar(
                PapelQuantitativo.ESTADO_INICIAL, "Pergunta?");
        assertThrows(IllegalArgumentException.class, () -> incognita.resolver(null));
    }

    @Test
    @DisplayName("Designa retorna falso para papéis diferentes do original")
    void designaRetornaFalsoParaOutrosPapeis() {
        IncognitaQuantitativa incognita = IncognitaQuantitativa.designar(
                PapelQuantitativo.ESTADO_INICIAL, "Pergunta?");

        assertTrue(incognita.designa(PapelQuantitativo.ESTADO_INICIAL));
        assertFalse(incognita.designa(PapelQuantitativo.ESTADO_FINAL));
        assertFalse(incognita.designa(PapelQuantitativo.TRANSFORMACAO_PRIMEIRA));
    }
}
