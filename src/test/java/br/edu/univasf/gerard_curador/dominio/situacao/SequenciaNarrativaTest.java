package br.edu.univasf.gerard_curador.dominio.situacao;

import br.edu.univasf.gerard_curador.dominio.narrativo.ContextoNarrativo;
import br.edu.univasf.gerard_curador.dominio.narrativo.MarcadorTemporal;
import br.edu.univasf.gerard_curador.dominio.narrativo.TransformacaoNarrativa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Projeção e estrutura da Sequência Narrativa")
class SequenciaNarrativaTest {

    @Test
    @DisplayName("projetarSequenciaNarrativa mantém a ordem canônica: estado inicial, eventos ordenados, estado final e pergunta (Casos 10 e 11)")
    void projetarSequenciaNarrativaMantemOrdemCanonica() {
        SituacaoProblema situacao = SituacoesDeTeste.situacaoRosasCompleta();

        SequenciaNarrativa sequencia = situacao.projetarSequenciaNarrativa();

        assertNotNull(sequencia);
        assertEquals("situacao-rosas", sequencia.getIdSituacao());
        assertEquals(5, sequencia.quantidadeDeEpisodios());

        List<EpisodioNarrativo> episodios = sequencia.getEpisodios();

        // Episódio 1: Estado Inicial
        EpisodioNarrativo ep1 = episodios.get(0);
        assertEquals(TipoEpisodio.ESTADO, ep1.getTipo());
        assertTrue(ep1.ehEstado());
        assertSame(situacao.getEstadoInicial(), ep1.getEstado());

        // Episódio 2: Evento T1 (ordem 1)
        EpisodioNarrativo ep2 = episodios.get(1);
        assertEquals(TipoEpisodio.EVENTO, ep2.getTipo());
        assertTrue(ep2.ehEvento());
        assertEquals(1, ep2.getTransformacao().getOrdem());
        assertEquals(SituacoesDeTeste.ROSA_BRANCA.getId(),
                ep2.getTransformacao().getQuantidade().getObjeto().getId());

        // Episódio 3: Evento T2 (ordem 2)
        EpisodioNarrativo ep3 = episodios.get(2);
        assertEquals(TipoEpisodio.EVENTO, ep3.getTipo());
        assertTrue(ep3.ehEvento());
        assertEquals(2, ep3.getTransformacao().getOrdem());
        assertEquals(SituacoesDeTeste.ROSA_AMARELA.getId(),
                ep3.getTransformacao().getQuantidade().getObjeto().getId());

        // Episódio 4: Estado Final (curado)
        EpisodioNarrativo ep4 = episodios.get(3);
        assertEquals(TipoEpisodio.ESTADO, ep4.getTipo());
        assertTrue(ep4.ehEstado());
        assertSame(situacao.getEstadoFinalCurado(), ep4.getEstado());

        // Episódio 5: Pergunta
        EpisodioNarrativo ep5 = episodios.get(4);
        assertEquals(TipoEpisodio.PERGUNTA, ep5.getTipo());
        assertTrue(ep5.ehPergunta());
        assertSame(situacao.getIncognita(), ep5.getIncognita());

        // Validação da ordem temporal dos eventos
        assertTrue(sequencia.preservaOrdemTemporal());
    }

    @Test
    @DisplayName("Filtragem de episódios por tipo na sequência narrativa")
    void filtragemDeEpisodiosPorTipo() {
        SituacaoProblema situacao = SituacoesDeTeste.situacaoRosasCompleta();
        SequenciaNarrativa sequencia = situacao.projetarSequenciaNarrativa();

        List<EpisodioNarrativo> estados = sequencia.getEstados();
        assertEquals(2, estados.size());
        assertSame(sequencia.getPrimeiroEpisodio(), estados.get(0));
        assertSame(sequencia.getUltimoEstado(), estados.get(1));

        List<EpisodioNarrativo> eventos = sequencia.getEventos();
        assertEquals(2, eventos.size());
        assertEquals(1, eventos.get(0).getMomento().getOrdem());
        assertEquals(2, eventos.get(1).getMomento().getOrdem());

        EpisodioNarrativo pergunta = sequencia.getPergunta();
        assertNotNull(pergunta);
        assertTrue(pergunta.ehPergunta());
    }

    @Test
    @DisplayName("projetarSequenciaNarrativa utiliza estado calculado quando estado final curado é omitido")
    void projetaComEstadoCalculadoQuandoCuradoNaoInformado() {
        SituacaoProblema situacaoSemFinalCurado = SituacaoProblema.construtor("sem-final", "Enunciado")
                .comCatalogo(SituacoesDeTeste.catalogoDasRosas())
                .comEstadoInicial(SituacoesDeTeste.estadoInicialDasRosas())
                .comTransformacao(SituacoesDeTeste.t1DasRosas())
                .comTransformacao(SituacoesDeTeste.t2DasRosas())
                .comIncognita(SituacoesDeTeste.incognitaDasRosas())
                .construir();

        SequenciaNarrativa sequencia = situacaoSemFinalCurado.projetarSequenciaNarrativa();

        assertEquals(5, sequencia.quantidadeDeEpisodios());
        EpisodioNarrativo epFinal = sequencia.getUltimoEstado();
        assertNotNull(epFinal);
        assertEquals(3, epFinal.getEstado().totalGeral().getValor());
    }

    @Test
    @DisplayName("preservaOrdemTemporal detecta inversão na ordem dos eventos")
    void preservaOrdemTemporalDetectaInversao() {
        TransformacaoNarrativa t1 = SituacoesDeTeste.t1DasRosas(); // ordem 1
        TransformacaoNarrativa t2 = SituacoesDeTeste.t2DasRosas(); // ordem 2

        List<EpisodioNarrativo> episodiosInvertidos = List.of(
                EpisodioNarrativo.deEvento(t2),
                EpisodioNarrativo.deEvento(t1)
        );

        SequenciaNarrativa sequencia = new SequenciaNarrativa(
                "seq-invertida",
                episodiosInvertidos,
                ContextoNarrativo.naoInformado()
        );

        assertFalse(sequencia.preservaOrdemTemporal(),
                "Deve retornar false quando um evento possui ordem inferior ao anterior");
    }

    @Test
    @DisplayName("EpisodioNarrativo e SequenciaNarrativa formatam descrições com rótulos semânticos")
    void descricoesFormatamComRotulosSemanticos() {
        SituacaoProblema situacao = SituacoesDeTeste.situacaoRosasCompleta();
        SequenciaNarrativa sequencia = situacao.projetarSequenciaNarrativa();

        String descricao = sequencia.descrever();
        assertTrue(descricao.contains("ESTADO:"));
        assertTrue(descricao.contains("EVENTO:"));
        assertTrue(descricao.contains("PERGUNTA:"));
        assertEquals(descricao, sequencia.toString());
    }

    @Test
    @DisplayName("Validação dos construtores de EpisodioNarrativo e SequenciaNarrativa")
    void validacaoDeConstrutores() {
        assertThrows(IllegalArgumentException.class, () ->
                new SequenciaNarrativa(null, List.of(), null));
        assertThrows(IllegalArgumentException.class, () ->
                new SequenciaNarrativa("   ", List.of(), null));

        assertThrows(IllegalArgumentException.class, () ->
                EpisodioNarrativo.deEstado(null));
        assertThrows(IllegalArgumentException.class, () ->
                EpisodioNarrativo.deEvento(null));
        assertThrows(IllegalArgumentException.class, () ->
                EpisodioNarrativo.dePergunta(null, MarcadorTemporal.daOrdem(1)));
    }
}
