package br.edu.univasf.gerard_curador.representacao.quadrinhos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testes de Quadro e Balão")
class QuadroEBalaoTest {

    @Test
    @DisplayName("Balao.fala deve criar balão com tipo FALA e emissor informado")
    void balaoFalaDeveConfigurarEmissorETipoCorretos() {
        Balao balao = Balao.fala("Vovó", "Olá, queridas rosas!");

        assertEquals(Balao.Tipo.FALA, balao.getTipo());
        assertEquals("Vovó", balao.getEmissor());
        assertEquals("Olá, queridas rosas!", balao.getTexto());
        assertTrue(balao.possuiEmissor());
        assertEquals("[fala] Vovó: Olá, queridas rosas!", balao.descrever());
        assertEquals(balao.descrever(), balao.toString());
    }

    @Test
    @DisplayName("Balao.narracao deve criar balão com tipo NARRACAO e sem emissor")
    void balaoNarracaoDeveConfigurarSemEmissor() {
        Balao balao = Balao.narracao("Vovó caminha pelo jardim.");

        assertEquals(Balao.Tipo.NARRACAO, balao.getTipo());
        assertNull(balao.getEmissor());
        assertEquals("Vovó caminha pelo jardim.", balao.getTexto());
        assertFalse(balao.possuiEmissor());
        assertEquals("[narracao] Vovó caminha pelo jardim.", balao.descrever());
        assertEquals(balao.descrever(), balao.toString());
    }

    @Test
    @DisplayName("Balao.pergunta deve criar balão com tipo PERGUNTA e sem emissor")
    void balaoPerguntaDeveConfigurarSemEmissor() {
        Balao balao = Balao.pergunta("Quantas rosas sobraram?");

        assertEquals(Balao.Tipo.PERGUNTA, balao.getTipo());
        assertNull(balao.getEmissor());
        assertEquals("Quantas rosas sobraram?", balao.getTexto());
        assertFalse(balao.possuiEmissor());
        assertEquals("[pergunta] Quantas rosas sobraram?", balao.descrever());
    }

    @Test
    @DisplayName("Balao com tipo PENSAMENTO deve ser instanciado via construtor geral")
    void balaoPensamentoDeveSerInstanciadoViaConstrutorGeral() {
        Balao balao = new Balao(Balao.Tipo.PENSAMENTO, "Netinha", "Adoro flores!");

        assertEquals(Balao.Tipo.PENSAMENTO, balao.getTipo());
        assertEquals("Netinha", balao.getEmissor());
        assertEquals("Adoro flores!", balao.getTexto());
        assertTrue(balao.possuiEmissor());
        assertEquals("[pensamento] Netinha: Adoro flores!", balao.descrever());
    }

    @Test
    @DisplayName("Balao deve validar que tipo e texto são obrigatórios")
    void balaoDeveValidarTipoETextoObrigatorios() {
        assertThrows(IllegalArgumentException.class, () -> new Balao(null, "Emissor", "Texto"));
        assertThrows(IllegalArgumentException.class, () -> new Balao(Balao.Tipo.FALA, "Emissor", null));
        assertThrows(IllegalArgumentException.class, () -> new Balao(Balao.Tipo.FALA, "Emissor", "   "));
    }

    @Test
    @DisplayName("Balao deve respeitar regras de igualdade e código hash")
    void balaoDeveRespeitarRegrasDeIgualdadeEHashCode() {
        Balao b1 = Balao.fala("Vovó", "Bom dia!");
        Balao b2 = Balao.fala("Vovó", "Bom dia!");
        Balao b3 = Balao.fala("Netinha", "Bom dia!");
        Balao b4 = Balao.narracao("Bom dia!");
        Balao b5 = Balao.fala("Vovó", "Boa tarde!");

        assertEquals(b1, b1);
        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());

        assertNotEquals(b1, b3);
        assertNotEquals(b1, b4);
        assertNotEquals(b1, b5);
        assertNotEquals(b1, null);
        assertNotEquals(b1, "outro objeto");

        Balao n1 = Balao.narracao("Texto");
        Balao n2 = Balao.narracao("Texto");
        assertEquals(n1, n2);
        assertEquals(n1.hashCode(), n2.hashCode());
    }

    @Test
    @DisplayName("Quadro deve validar que número >= 1 e título não é vazio")
    void quadroDeveValidarNumeroETitulo() {
        assertThrows(IllegalArgumentException.class, () -> Quadro.construtor(0, "Título").construir());
        assertThrows(IllegalArgumentException.class, () -> Quadro.construtor(-1, "Título").construir());
        assertThrows(IllegalArgumentException.class, () -> Quadro.construtor(1, null).construir());
        assertThrows(IllegalArgumentException.class, () -> Quadro.construtor(1, "   ").construir());
    }

    @Test
    @DisplayName("Quadro deve armazenar legenda, cena e balões corretamente")
    void quadroDeveArmazenarLegendaCenaEBaloes() {
        DescritorCena cena = DescritorCena.construtor()
                .comPersonagem("vovo")
                .comElemento("rosa_branca", 2)
                .comAmbientacao("jardim")
                .construir();

        Balao fala = Balao.fala("vovo", "Veja o canteiro!");
        Balao narracao = Balao.narracao("O dia estava ensolarado.");

        Quadro quadro = Quadro.construtor(1, "Início do dia")
                .comLegenda("Vovó no jardim")
                .comCena(cena)
                .comBalao(fala)
                .comBalao(narracao)
                .comBalao(null) // nulo deve ser ignorado
                .construir();

        assertEquals(1, quadro.getNumero());
        assertEquals("Início do dia", quadro.getTitulo());
        assertTrue(quadro.possuiLegenda());
        assertEquals("Vovó no jardim", quadro.getLegenda());
        assertTrue(quadro.possuiCena());
        assertNotNull(quadro.getCena());
        assertFalse(quadro.possuiPergunta());

        List<Balao> baloes = quadro.getBaloes();
        assertEquals(2, baloes.size());
        assertEquals(fala, baloes.get(0));
        assertEquals(narracao, baloes.get(1));

        // A lista de balões retornada deve ser cópia/imutável
        baloes.clear();
        assertEquals(2, quadro.getBaloes().size(), "getBaloes não deve expor a lista interna para modificação");
    }

    @Test
    @DisplayName("Quadro deve identificar presença de pergunta através de seus balões")
    void quadroDeveIdentificarPresencaDePergunta() {
        Quadro quadroSemPergunta = Quadro.construtor(1, "Sem pergunta")
                .comBalao(Balao.fala("vovo", "Oi"))
                .construir();
        assertFalse(quadroSemPergunta.possuiPergunta());

        Quadro quadroComPergunta = Quadro.construtor(2, "Com pergunta")
                .comBalao(Balao.fala("vovo", "Oi"))
                .comBalao(Balao.pergunta("Quantas rosas?"))
                .construir();
        assertTrue(quadroComPergunta.possuiPergunta());
    }

    @Test
    @DisplayName("Quadro descrever e toString devem conter número, título, legenda, cena e balões")
    void quadroDescreverDeveConterTodasInformacoes() {
        DescritorCena cena = DescritorCena.construtor()
                .comPersonagem("vovo")
                .construir();

        Quadro quadro = Quadro.construtor(1, "Título Quadro")
                .comLegenda("Legenda informativa")
                .comCena(cena)
                .comBalao(Balao.narracao("Narração da cena"))
                .construir();

        String descricao = quadro.descrever();
        assertTrue(descricao.contains("Quadro 1 - Título Quadro"));
        assertTrue(descricao.contains("legenda: Legenda informativa"));
        assertTrue(descricao.contains("cena: "));
        assertTrue(descricao.contains("[narracao] Narração da cena"));
        assertEquals(descricao, quadro.toString());
    }

    @Test
    @DisplayName("DescritorCena deve acumular elementos, controlar personagens e ambientação")
    void descritorCenaDeveAcumularElementosEControlarAtributos() {
        DescritorCena cena = DescritorCena.construtor()
                .comPersonagem("vovo")
                .comPersonagem("netinha")
                .comPersonagem("vovo") // duplicata ignorada
                .comPersonagem(null) // nulo ignorado
                .comPersonagem("   ") // em branco ignorado
                .comElemento("rosa_branca", 2)
                .comElemento("rosa_branca", 3) // acumula 2 + 3 = 5
                .comElemento("rosa_amarela", 1)
                .comElemento(null, 10) // ignorado
                .comElemento("invalido", 0) // quantidade <= 0 ignorado
                .comAmbientacao("canteiro de flores")
                .construir();

        assertEquals(List.of("vovo", "netinha"), cena.getPersonagensVisiveis());
        assertEquals(5, cena.getElementosVisuais().get("rosa_branca"));
        assertEquals(1, cena.getElementosVisuais().get("rosa_amarela"));
        assertEquals(6, cena.totalDeElementos());
        assertTrue(cena.possuiAmbientacao());
        assertEquals("canteiro de flores", cena.getAmbientacao());

        String descricao = cena.descrever();
        assertTrue(descricao.contains("personagens: vovo, netinha"));
        assertTrue(descricao.contains("5x rosa_branca"));
        assertTrue(descricao.contains("1x rosa_amarela"));
        assertTrue(descricao.contains("ambientacao: canteiro de flores"));
        assertEquals(descricao, cena.toString());
    }
}
