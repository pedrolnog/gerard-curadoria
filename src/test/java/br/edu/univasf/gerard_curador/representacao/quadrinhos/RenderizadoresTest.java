package br.edu.univasf.gerard_curador.representacao.quadrinhos;

import br.edu.univasf.gerard_curador.dominio.narrativo.EstadoDePosse;
import br.edu.univasf.gerard_curador.dominio.situacao.SituacaoProblema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testes dos Renderizadores de Roteiro")
class RenderizadoresTest {

    @Test
    @DisplayName("Caso 12: RenderizadorTextual e RenderizadorHtml devem renderizar o mesmo roteiro sem alterar roteiro nem situação")
    void renderizadoresDevemProcessarMesmoRoteiroSemAlterarRoteiroNemSituacao() {
        SituacaoProblema situacao = RoteiroDeTeste.situacaoRosasCompleta();
        RoteiroQuadrinhos roteiro = RoteiroDeTeste.gerador().gerar(
                situacao.projetarSequenciaNarrativa(),
                "As Rosas da Vovó"
        );

        List<RenderizadorDeRoteiro> renderizadores = List.of(
                new RenderizadorTextual(),
                new RenderizadorHtml()
        );

        // Estado prévio do roteiro
        int qtdQuadrosAntes = roteiro.quantidadeDeQuadros();
        String idSituacaoAntes = roteiro.getIdSituacao();
        String tituloAntes = roteiro.getTitulo();
        String descricaoRoteiroAntes = roteiro.descrever();

        // Estado prévio da situação
        EstadoDePosse estadoInicialAntes = situacao.getEstadoInicial();
        EstadoDePosse estadoFinalAntes = situacao.getEstadoFinalCurado();
        int qtdTransformacoesAntes = situacao.getTransformacoes().size();
        String descricaoSituacaoAntes = situacao.descreverComValidacao();

        for (RenderizadorDeRoteiro renderizador : renderizadores) {
            String resultado = renderizador.renderizar(roteiro);

            assertNotNull(resultado, "A saída do renderizador não deve ser nula");
            assertFalse(resultado.isBlank(), "A saída do renderizador não deve estar em branco");

            // Roteiro permanece inalterado
            assertEquals(qtdQuadrosAntes, roteiro.quantidadeDeQuadros());
            assertEquals(idSituacaoAntes, roteiro.getIdSituacao());
            assertEquals(tituloAntes, roteiro.getTitulo());
            assertEquals(descricaoRoteiroAntes, roteiro.descrever());

            // Situação do domínio permanece inalterada
            assertEquals(estadoInicialAntes, situacao.getEstadoInicial());
            assertEquals(estadoFinalAntes, situacao.getEstadoFinalCurado());
            assertEquals(qtdTransformacoesAntes, situacao.getTransformacoes().size());
            assertEquals(descricaoSituacaoAntes, situacao.descreverComValidacao());
        }
    }

    @Test
    @DisplayName("Extensões de arquivo devem ser 'txt' e 'html'")
    void extensoesDeArquivoDevemSerTxtEHtml() {
        RenderizadorDeRoteiro textual = new RenderizadorTextual();
        RenderizadorDeRoteiro html = new RenderizadorHtml();

        assertEquals("txt", textual.getExtensao());
        assertEquals("html", html.getExtensao());
    }

    @Test
    @DisplayName("HTML gerado deve começar com <!DOCTYPE html> e conter estrutura básica")
    void htmlComecaComDoctypeEContemEstruturaBasica() {
        RoteiroQuadrinhos roteiro = RoteiroDeTeste.roteiroDasRosas();
        RenderizadorHtml renderizadorHtml = new RenderizadorHtml();

        String html = renderizadorHtml.renderizar(roteiro);

        assertTrue(html.startsWith("<!DOCTYPE html>"), "O HTML deve começar com <!DOCTYPE html>");
        assertTrue(html.contains("<html lang=\"pt-BR\">"));
        assertTrue(html.contains("<title>As Rosas da Vovó</title>"));
        assertTrue(html.contains("<h1>As Rosas da Vovó</h1>"));
        assertTrue(html.contains("<div class=\"tira\">"));
        assertTrue(html.contains("</html>"));
    }

    @Test
    @DisplayName("RenderizadorHtml deve escapar caracteres especiais <, > e &")
    void htmlDeveEscaparCaracteresEspeciais() {
        DescritorCena cena = DescritorCena.construtor()
                .comPersonagem("Vovó & Netinha")
                .comElemento("rosa", 1)
                .comAmbientacao("jardim <aberto>")
                .construir();

        Quadro quadro = Quadro.construtor(1, "Quadro com & e <tags>")
                .comLegenda("Legenda com <script> & \"aspas\" > 1")
                .comCena(cena)
                .comBalao(Balao.fala("Vovó & Netinha", "Texto com <balao> & > 5"))
                .construir();

        RoteiroQuadrinhos roteiroEspecial = new RoteiroQuadrinhos(
                "sit-especial",
                "História & Arte <Quadrinhos>",
                List.of(quadro)
        );

        RenderizadorHtml renderizadorHtml = new RenderizadorHtml();
        String html = renderizadorHtml.renderizar(roteiroEspecial);

        assertTrue(html.startsWith("<!DOCTYPE html>"));
        // Caracteres escapados no título
        assertTrue(html.contains("História &amp; Arte &lt;Quadrinhos&gt;"));
        // No título do quadro
        assertTrue(html.contains("Quadro com &amp; e &lt;tags&gt;"));
        // Na legenda
        assertTrue(html.contains("Legenda com &lt;script&gt; &amp; &quot;aspas&quot; &gt; 1"));
        // No emissor e texto do balão
        assertTrue(html.contains("Vovó &amp; Netinha"));
        assertTrue(html.contains("Texto com &lt;balao&gt; &amp; &gt; 5"));
        // Na descrição da cena
        assertTrue(html.contains("jardim &lt;aberto&gt;"));

        // Não deve conter as tags brutas
        assertFalse(html.contains("<script>"));
        assertFalse(html.contains("<balao>"));
        assertFalse(html.contains("<tags>"));
        assertFalse(html.contains("<aberto>"));
    }

    @Test
    @DisplayName("renderizar(null) deve lançar IllegalArgumentException para todos os renderizadores")
    void renderizarComRoteiroNuloDeveLancarExcecao() {
        List<RenderizadorDeRoteiro> renderizadores = List.of(
                new RenderizadorTextual(),
                new RenderizadorHtml()
        );

        for (RenderizadorDeRoteiro renderizador : renderizadores) {
            IllegalArgumentException excecao = assertThrows(
                    IllegalArgumentException.class,
                    () -> renderizador.renderizar(null),
                    "Renderizador " + renderizador.getClass().getSimpleName() + " deve lançar exceção para roteiro nulo"
            );
            assertEquals("renderizacao exige roteiro", excecao.getMessage());
        }
    }

    @Test
    @DisplayName("RenderizadorTextual deve apresentar a representação textual do roteiro")
    void renderizadorTextualDeveApresentarTextoDoRoteiro() {
        RoteiroQuadrinhos roteiro = RoteiroDeTeste.roteiroDasRosas();
        RenderizadorTextual renderizadorTextual = new RenderizadorTextual();

        String saida = renderizadorTextual.renderizar(roteiro);

        assertEquals(roteiro.descrever(), saida);
        assertTrue(saida.contains("Roteiro: As Rosas da Vovó"));
        assertTrue(saida.contains("Situacao: situacao-rosas"));
        assertTrue(saida.contains("Quadro 1 - Estado inicial"));
    }
}
