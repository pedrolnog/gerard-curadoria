package br.edu.univasf.gerard_curador.representacao.quadrinhos;

import br.edu.univasf.gerard_curador.dominio.situacao.SequenciaNarrativa;
import br.edu.univasf.gerard_curador.dominio.situacao.SituacaoProblema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testes do Gerador de Roteiro de Quadrinhos")
class GeradorRoteiroQuadrinhosTest {

    @Test
    @DisplayName("Caso 11: Roteiro das rosas deve ter entre 3 e 5 quadros (exatamente 4)")
    void roteiroDasRosasDeveConterQuatroQuadrosERespeitarFaixaDeTresACinco() {
        RoteiroQuadrinhos roteiro = RoteiroDeTeste.roteiroDasRosas();

        assertNotNull(roteiro);
        assertEquals("situacao-rosas", roteiro.getIdSituacao());
        assertEquals("As Rosas da Vovó", roteiro.getTitulo());
        assertEquals(4, roteiro.quantidadeDeQuadros());
        assertTrue(roteiro.respeitaFaixaDeQuadros(), "O roteiro deve respeitar a faixa de 3 a 5 quadros");
    }

    @Test
    @DisplayName("Caso 11: Quadro 1 deve retratar o estado inicial com ambientação e inventário")
    void quadroInicialDeveConterAmbientacaoPersonagemItensEBalaoDeNarracao() {
        RoteiroQuadrinhos roteiro = RoteiroDeTeste.roteiroDasRosas();
        Quadro quadro1 = roteiro.getPrimeiroQuadro();

        assertNotNull(quadro1);
        assertEquals(1, quadro1.getNumero());
        assertEquals("Estado inicial", quadro1.getTitulo());
        assertTrue(quadro1.possuiLegenda());
        assertTrue(quadro1.possuiCena());
        assertFalse(quadro1.possuiPergunta());

        DescritorCena cena = quadro1.getCena();
        assertTrue(cena.getPersonagensVisiveis().stream().anyMatch(chave -> chave.toLowerCase().contains("vovo")),
                () -> "personagens visiveis: " + cena.getPersonagensVisiveis());
        assertEquals(2, cena.getElementosVisuais().get("rosa_branca"));
        assertEquals(3, cena.getElementosVisuais().get("rosa_amarela"));
        assertTrue(cena.possuiAmbientacao());
        assertTrue(cena.getAmbientacao().contains("jardim"));

        List<Balao> baloes = quadro1.getBaloes();
        assertEquals(1, baloes.size());
        assertEquals(Balao.Tipo.NARRACAO, baloes.get(0).getTipo());
        assertFalse(baloes.get(0).possuiEmissor());
    }

    @Test
    @DisplayName("Caso 11: Quadros 2 e 3 devem conter os dois eventos de ação com ator, destinatário e objeto")
    void quadrosDeEventoDevemRepresentarAsDuasAcoesOrdenadas() {
        RoteiroQuadrinhos roteiro = RoteiroDeTeste.roteiroDasRosas();
        List<Quadro> quadros = roteiro.getQuadros();

        // Quadro 2: primeira doação (rosa branca)
        Quadro quadro2 = quadros.get(1);
        assertEquals(2, quadro2.getNumero());
        assertEquals("primeira doação", quadro2.getTitulo());
        assertTrue(quadro2.possuiLegenda());
        assertTrue(quadro2.possuiCena());
        assertFalse(quadro2.possuiPergunta());

        DescritorCena cena2 = quadro2.getCena();
        assertTrue(cena2.getPersonagensVisiveis().stream().anyMatch(chave -> chave.toLowerCase().contains("vovo")),
                () -> "personagens visiveis: " + cena2.getPersonagensVisiveis());
        assertTrue(cena2.getPersonagensVisiveis().stream().anyMatch(chave -> chave.toLowerCase().contains("netinha")),
                () -> "personagens visiveis: " + cena2.getPersonagensVisiveis());
        assertEquals(1, cena2.getElementosVisuais().get("rosa_branca"));
        assertNull(cena2.getElementosVisuais().get("rosa_amarela"));
        assertEquals(1, quadro2.getBaloes().size());
        assertEquals(Balao.Tipo.NARRACAO, quadro2.getBaloes().get(0).getTipo());

        // Quadro 3: segunda doação (rosa amarela)
        Quadro quadro3 = quadros.get(2);
        assertEquals(3, quadro3.getNumero());
        assertEquals("segunda doação", quadro3.getTitulo());
        assertTrue(quadro3.possuiLegenda());
        assertTrue(quadro3.possuiCena());
        assertFalse(quadro3.possuiPergunta());

        DescritorCena cena3 = quadro3.getCena();
        assertTrue(cena3.getPersonagensVisiveis().stream().anyMatch(chave -> chave.toLowerCase().contains("vovo")),
                () -> "personagens visiveis: " + cena3.getPersonagensVisiveis());
        assertTrue(cena3.getPersonagensVisiveis().stream().anyMatch(chave -> chave.toLowerCase().contains("netinha")),
                () -> "personagens visiveis: " + cena3.getPersonagensVisiveis());
        assertNull(cena3.getElementosVisuais().get("rosa_branca"));
        assertEquals(1, cena3.getElementosVisuais().get("rosa_amarela"));
        assertEquals(1, quadro3.getBaloes().size());
        assertEquals(Balao.Tipo.NARRACAO, quadro3.getBaloes().get(0).getTipo());
    }

    @Test
    @DisplayName("Caso 11: Quadro 4 deve retratar o estado final e conter a pergunta no último balão")
    void quadroFinalDeveConterEstadoFinalEPerguntaNoUltimoBalao() {
        RoteiroQuadrinhos roteiro = RoteiroDeTeste.roteiroDasRosas();
        Quadro quadro4 = roteiro.getUltimoQuadro();

        assertNotNull(quadro4);
        assertEquals(4, quadro4.getNumero());
        assertEquals("Estado final e pergunta", quadro4.getTitulo());
        assertTrue(quadro4.possuiLegenda());
        assertTrue(quadro4.possuiCena());
        assertTrue(quadro4.possuiPergunta());

        DescritorCena cena4 = quadro4.getCena();
        assertTrue(cena4.getPersonagensVisiveis().stream().anyMatch(chave -> chave.toLowerCase().contains("vovo")),
                () -> "personagens visiveis: " + cena4.getPersonagensVisiveis());
        assertEquals(1, cena4.getElementosVisuais().get("rosa_branca"));
        assertEquals(2, cena4.getElementosVisuais().get("rosa_amarela"));

        List<Balao> baloes = quadro4.getBaloes();
        assertEquals(2, baloes.size());
        assertEquals(Balao.Tipo.NARRACAO, baloes.get(0).getTipo());
        assertEquals(Balao.Tipo.PERGUNTA, baloes.get(1).getTipo());
        assertTrue(baloes.get(1).getTexto().contains("quantas"));

        assertSame(quadro4, roteiro.getQuadroComPergunta());
    }

    @Test
    @DisplayName("Caso 10: Ordem das transformações deve ser preservada na sequência narrativa e nos quadros")
    void ordemDasTransformacoesDeveSerPreservadaNaSequenciaENosQuadros() {
        // Constrói a situação inserindo transformações em ordem inversa (t2 antes de t1)
        SituacaoProblema situacaoComOrdemInvertida = SituacaoProblema.construtor(
                "situacao-invertida",
                "Enunciado de teste para verificar a ordenação curada das transformações"
        )
                .comCatalogo(RoteiroDeTeste.catalogoDasRosas())
                .comEstadoInicial(RoteiroDeTeste.estadoInicialDasRosas())
                .comTransformacao(RoteiroDeTeste.t2DasRosas()) // ordem 2 inserida antes
                .comTransformacao(RoteiroDeTeste.t1DasRosas()) // ordem 1 inserida depois
                .comEstadoFinalCurado(RoteiroDeTeste.estadoFinalCuradoDasRosas())
                .comIncognita(RoteiroDeTeste.incognitaDasRosas())
                .comContexto(RoteiroDeTeste.situacaoRosasCompleta().getContexto())
                .construir();

        SequenciaNarrativa sequencia = situacaoComOrdemInvertida.projetarSequenciaNarrativa();
        assertTrue(sequencia.preservaOrdemTemporal(), "A sequência deve preservar a ordem temporal curada");

        RoteiroQuadrinhos roteiro = new GeradorRoteiroQuadrinhos().gerar(sequencia, "Roteiro Ordenado");
        List<Quadro> quadros = roteiro.getQuadros();

        assertEquals(4, quadros.size());
        assertEquals("primeira doação", quadros.get(1).getTitulo(), "Quadro 2 deve ser a primeira transformação");
        assertEquals("segunda doação", quadros.get(2).getTitulo(), "Quadro 3 deve ser a segunda transformação");
    }

    @Test
    @DisplayName("Gerador deve lançar IllegalArgumentException quando sequência narrativa for nula")
    void geradorDeveLancarExcecaoQuandoSequenciaForNula() {
        GeradorRoteiroQuadrinhos gerador = new GeradorRoteiroQuadrinhos();
        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> gerador.gerar(null, "Título")
        );
        assertEquals("geracao exige sequencia narrativa", excecao.getMessage());
    }

    @Test
    @DisplayName("RoteiroQuadrinhos deve validar parâmetros obrigatórios e verificar faixa")
    void roteiroQuadrinhosDeveValidarParametrosEVerificarFaixa() {
        assertThrows(IllegalArgumentException.class, () -> new RoteiroQuadrinhos(null, "Titulo", List.of()));
        assertThrows(IllegalArgumentException.class, () -> new RoteiroQuadrinhos("  ", "Titulo", List.of()));
        assertThrows(IllegalArgumentException.class, () -> new RoteiroQuadrinhos("id", null, List.of()));
        assertThrows(IllegalArgumentException.class, () -> new RoteiroQuadrinhos("id", "   ", List.of()));

        RoteiroQuadrinhos roteiroVazio = new RoteiroQuadrinhos("id", "Vazio", null);
        assertEquals(0, roteiroVazio.quantidadeDeQuadros());
        assertFalse(roteiroVazio.respeitaFaixaDeQuadros());
        assertNull(roteiroVazio.getPrimeiroQuadro());
        assertNull(roteiroVazio.getUltimoQuadro());
        assertNull(roteiroVazio.getQuadroComPergunta());
    }

    @Test
    @DisplayName("RoteiroQuadrinhos descrever e toString devem conter resumo formatado")
    void roteiroQuadrinhosDescreverDeveConterInformacoesDosQuadros() {
        RoteiroQuadrinhos roteiro = RoteiroDeTeste.roteiroDasRosas();
        String descricao = roteiro.descrever();

        assertTrue(descricao.contains("Roteiro: As Rosas da Vovó"));
        assertTrue(descricao.contains("Situacao: situacao-rosas"));
        assertTrue(descricao.contains("Quadro 1 - Estado inicial"));
        assertTrue(descricao.contains("Quadro 4 - Estado final e pergunta"));
        assertEquals(descricao, roteiro.toString());
    }
}
