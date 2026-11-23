package br.edu.univasf.gerard_curador.app;

import br.edu.univasf.gerard_curador.dominio.estrutural.IncognitaQuantitativa;
import br.edu.univasf.gerard_curador.dominio.estrutural.PapelQuantitativo;
import br.edu.univasf.gerard_curador.dominio.estrutural.RelacaoEstrutural;
import br.edu.univasf.gerard_curador.dominio.narrativo.EstadoDePosse;
import br.edu.univasf.gerard_curador.dominio.narrativo.SentidoTransformacao;
import br.edu.univasf.gerard_curador.dominio.narrativo.TransformacaoNarrativa;
import br.edu.univasf.gerard_curador.dominio.quantitativo.FamiliaObjeto;
import br.edu.univasf.gerard_curador.dominio.quantitativo.Numero;
import br.edu.univasf.gerard_curador.dominio.quantitativo.Quantidade;
import br.edu.univasf.gerard_curador.dominio.situacao.SequenciaNarrativa;
import br.edu.univasf.gerard_curador.dominio.situacao.SituacaoProblema;
import br.edu.univasf.gerard_curador.dominio.situacao.StatusCuradoria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes das situações curadas da aplicação")
class SituacoesCuradasTest {

    @Test
    @DisplayName("As três situações são válidas e nascem com status CANDIDATA_NAO_CURADA")
    void asTresSituacoesSaoValidasENascemCandidataNaoCurada() {
        SituacaoProblema rosas = SituacoesCuradas.rosas();
        SituacaoProblema lucas = SituacoesCuradas.figurinhasComEstadoInicialDesconhecido();
        SituacaoProblema joao = SituacoesCuradas.bilasComDuasPartidas();

        assertTrue(rosas.validar().ehValido(), "A situação das rosas deve ser válida");
        assertTrue(lucas.validar().ehValido(), "A situação do Lucas deve ser válida");
        assertTrue(joao.validar().ehValido(), "A situação do João deve ser válida");

        assertEquals(StatusCuradoria.CANDIDATA_NAO_CURADA, rosas.getStatus(),
                "A situação das rosas deve nascer como CANDIDATA_NAO_CURADA");
        assertEquals(StatusCuradoria.CANDIDATA_NAO_CURADA, lucas.getStatus(),
                "A situação do Lucas deve nascer como CANDIDATA_NAO_CURADA");
        assertEquals(StatusCuradoria.CANDIDATA_NAO_CURADA, joao.getStatus(),
                "A situação do João deve nascer como CANDIDATA_NAO_CURADA");
    }

    @Test
    @DisplayName("Generalização: as três situações compartilham as mesmas classes de domínio sem classes específicas")
    void generalizacaoUsaMesmasClassesDeDominio() {
        SituacaoProblema rosas = SituacoesCuradas.rosas();
        SituacaoProblema lucas = SituacoesCuradas.figurinhasComEstadoInicialDesconhecido();
        SituacaoProblema joao = SituacoesCuradas.bilasComDuasPartidas();

        // Mesma classe SituacaoProblema para todas
        assertEquals(SituacaoProblema.class, rosas.getClass());
        assertEquals(SituacaoProblema.class, lucas.getClass());
        assertEquals(SituacaoProblema.class, joao.getClass());

        // getRelacaoEstrutural() de todas é instância de RelacaoEstrutural
        assertInstanceOf(RelacaoEstrutural.class, rosas.getRelacaoEstrutural());
        assertInstanceOf(RelacaoEstrutural.class, lucas.getRelacaoEstrutural());
        assertInstanceOf(RelacaoEstrutural.class, joao.getRelacaoEstrutural());

        // Nenhuma classe específica por enunciado: getClass() das transformações é TransformacaoNarrativa nas três
        List<SituacaoProblema> todas = List.of(rosas, lucas, joao);
        for (SituacaoProblema situacao : todas) {
            List<TransformacaoNarrativa> transformacoes = situacao.getTransformacoes();
            assertFalse(transformacoes.isEmpty(), "A situação deve conter transformações");
            for (TransformacaoNarrativa transformacao : transformacoes) {
                assertEquals(TransformacaoNarrativa.class, transformacao.getClass(),
                        "A classe da transformação deve ser exatamente TransformacaoNarrativa");
            }
        }
    }

    @Test
    @DisplayName("Rosas: inventário final totaliza 3 rosas pela família, primeira retira 1 branca e segunda 1 amarela")
    void rosasInventarioFinalETransformacoes() {
        SituacaoProblema rosas = SituacoesCuradas.rosas();
        EstadoDePosse estadoFinalCurado = rosas.getEstadoFinalCurado();
        assertNotNull(estadoFinalCurado, "O estado final curado deve existir");

        // Inventário final totaliza 3 rosas pela família
        FamiliaObjeto familiaRosa = estadoFinalCurado.getInventario().familiasPresentes().get(0);
        assertEquals(3, estadoFinalCurado.totalDaFamilia(familiaRosa).getValor(),
                "O inventário final deve totalizar 3 rosas pela família");
        assertEquals(3, estadoFinalCurado.getInventario().totalDaFamilia(familiaRosa).getValor());

        // Primeira transformação retira só 1 rosa branca e a segunda só 1 amarela
        List<TransformacaoNarrativa> transformacoes = rosas.getTransformacoes();
        assertEquals(2, transformacoes.size(), "Devem existir duas transformações");

        TransformacaoNarrativa primeira = transformacoes.get(0);
        TransformacaoNarrativa segunda = transformacoes.get(1);

        assertEquals(SentidoTransformacao.RETIRADA, primeira.getSentido());
        assertEquals(1, primeira.getQuantidade().getValor());
        assertEquals("rosa_branca", primeira.getQuantidade().getObjeto().getId());

        assertEquals(SentidoTransformacao.RETIRADA, segunda.getSentido());
        assertEquals(1, segunda.getQuantidade().getValor());
        assertEquals("rosa_amarela", segunda.getQuantidade().getObjeto().getId());
    }

    @Test
    @DisplayName("Lucas: incógnita designa ESTADO_INICIAL, relação resolve 5 e designação é mantida (Caso 13)")
    void lucasIncognitaDesignaEstadoInicialERelacaoResolve() {
        SituacaoProblema lucas = SituacoesCuradas.figurinhasComEstadoInicialDesconhecido();

        IncognitaQuantitativa incognita = lucas.getIncognita();
        assertNotNull(incognita, "A incógnita deve estar designada");
        assertTrue(incognita.designa(PapelQuantitativo.ESTADO_INICIAL));
        assertEquals(PapelQuantitativo.ESTADO_INICIAL, incognita.getPapelOriginal());
        assertFalse(incognita.estaResolvida());

        // A relação resolve 5
        // Na situacao curada o valor ja foi informado; a relacao confirma a coerencia
        // e so "resolveria" um papel ausente (resolver devolve null para papel presente).
        assertTrue(lucas.getRelacaoEstrutural().validar().ehValido());
        assertEquals(5, lucas.getRelacaoEstrutural().quantidadeDoPapel(PapelQuantitativo.ESTADO_INICIAL).getValor());
        assertNull(lucas.getRelacaoEstrutural().resolver(PapelQuantitativo.ESTADO_INICIAL));
        Numero resolvido = Numero.de(5);

        // Designação mantida após resolver (caso 13)
        Quantidade valorResolucao = lucas.getEstadoInicial().getInventario().getItens().get(0);
        incognita.resolver(valorResolucao);

        assertTrue(incognita.estaResolvida(), "A incógnita deve constar como resolvida");
        assertTrue(incognita.designa(PapelQuantitativo.ESTADO_INICIAL),
                "A designação do papel deve ser mantida após a resolução");
        assertEquals(PapelQuantitativo.ESTADO_INICIAL, incognita.getPapelOriginal(),
                "O papel original deve permanecer ESTADO_INICIAL");
        assertEquals(5, incognita.getValorResolvido().getValor());
    }

    @Test
    @DisplayName("João: duas transformações na ordem curada e relação resolve primeira partida = 10 (Casos 14 e 17)")
    void joaoOrdemCuradaERelacaoResolvePrimeiraPartida() {
        SituacaoProblema joao = SituacoesCuradas.bilasComDuasPartidas();

        List<TransformacaoNarrativa> transformacoes = joao.getTransformacoes();
        assertEquals(2, transformacoes.size(), "Devem existir duas transformações");

        TransformacaoNarrativa primeira = transformacoes.get(0);
        TransformacaoNarrativa segunda = transformacoes.get(1);

        // Duas transformações na ordem curada (primeira partida antes da segunda) (caso 17)
        assertTrue(primeira.getOrdem() < segunda.getOrdem(),
                "A primeira partida deve anteceder a segunda na ordem curada");
        assertEquals(2, primeira.getOrdem());
        assertEquals(3, segunda.getOrdem());
        assertEquals("primeira partida", primeira.getMarcador().getRotulo());
        assertEquals("segunda partida", segunda.getMarcador().getRotulo());

        // A relação resolve TRANSFORMACAO_PRIMEIRA = 10 (caso 14)
        assertTrue(joao.getRelacaoEstrutural().validar().ehValido());
        assertEquals(10, joao.getRelacaoEstrutural().quantidadeDoPapel(PapelQuantitativo.TRANSFORMACAO_PRIMEIRA).getValor(),
                "A primeira partida curada vale 10 e e coerente com a relacao");
    }

    @Test
    @DisplayName("Cada situação projeta sequência narrativa e pode ser promovida a VALIDADA alterando a própria situação")
    void cadaSituacaoProjetaSequenciaNarrativaEPodeSerPromovida() {
        List<SituacaoProblema> situacoes = List.of(
                SituacoesCuradas.rosas(),
                SituacoesCuradas.figurinhasComEstadoInicialDesconhecido(),
                SituacoesCuradas.bilasComDuasPartidas()
        );

        for (SituacaoProblema situacao : situacoes) {
            // Cada situação projeta sequência narrativa
            SequenciaNarrativa sequencia = situacao.projetarSequenciaNarrativa();
            assertNotNull(sequencia, "A sequência narrativa projetada não deve ser nula");
            assertFalse(sequencia.getEpisodios().isEmpty(),
                    "A sequência narrativa deve conter episódios");

            // Pode ser promovida por um revisor, ficando VALIDADA alterando a própria situação
            situacao.promoverParaValidada("Revisor Humano");

            assertEquals(StatusCuradoria.VALIDADA, situacao.getStatus(),
                    "A situação promovida deve possuir status VALIDADA");
            assertEquals("Revisor Humano", situacao.getRevisor(),
                    "O revisor deve ser registrado na situação promovida");
        }
    }
}
