package br.edu.univasf.gerard_curador.dominio.situacao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EpisodioNarrativoTest {

    private final SituacaoProblema rosas = SituacoesDeTeste.situacaoRosasCompleta();
    private final List<EpisodioNarrativo> episodios =
            rosas.projetarSequenciaNarrativa().getEpisodios();

    @Test
    @DisplayName("Cada tipo de episodio e uma subclasse propria da hierarquia selada")
    void subclassesPorTipo() {
        assertInstanceOf(EpisodioDeEstado.class, episodios.get(0));
        assertInstanceOf(EpisodioDeEvento.class, episodios.get(1));
        assertInstanceOf(EpisodioDePergunta.class, episodios.get(episodios.size() - 1));
    }

    @Test
    @DisplayName("descrever() e resolvido por ligacao dinamica, sem switch sobre o tipo")
    void descreverPolimorfico() {
        EpisodioNarrativo inicial = episodios.get(0);
        EpisodioNarrativo evento = episodios.get(1);
        assertEquals(inicial.getEstado().descrever(), inicial.descrever());
        assertEquals(evento.getTransformacao().descrever(), evento.descrever());
        assertSame(rosas.getEstadoInicial(), inicial.getEstado());
    }

    @Test
    @DisplayName("Pedir a um episodio um dado que nao lhe pertence falha cedo em vez de devolver null")
    void acessorErradoFalha() {
        EpisodioNarrativo evento = episodios.get(1);
        assertTrue(evento.ehEvento());
        assertThrows(IllegalStateException.class, evento::getEstado);
        assertThrows(IllegalStateException.class, evento::getIncognita);
        assertThrows(IllegalStateException.class, () -> episodios.get(0).getTransformacao());
    }

    @Test
    @DisplayName("As fabricas recusam argumento nulo")
    void fabricasRecusamNulo() {
        assertThrows(IllegalArgumentException.class, () -> EpisodioNarrativo.deEstado(null));
        assertThrows(IllegalArgumentException.class, () -> EpisodioNarrativo.deEvento(null));
        assertThrows(IllegalArgumentException.class, () -> EpisodioNarrativo.dePergunta(null, null));
    }
}
