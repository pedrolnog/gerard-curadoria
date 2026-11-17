package br.edu.univasf.gerard_curador.dominio.estrutural;

import br.edu.univasf.gerard_curador.dominio.quantitativo.*;
import br.edu.univasf.gerard_curador.dominio.validacao.ResultadoValidacao;
import br.edu.univasf.gerard_curador.dominio.validacao.TipoDiagnostico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Composição Parte-Todo")
class ComposicaoParteTodoTest {

    private ObjetoContado bola;
    private ObjetoContado dinheiro;

    @BeforeEach
    void setUp() {
        bola = new ObjetoContado(
                "bola",
                new FamiliaObjeto("bola", "Bola"),
                Set.of(),
                RealizacaoTextualObjeto.emPortugues("bola", "bolas"),
                null
        );
        dinheiro = new ObjetoContado(
                "dinheiro",
                new FamiliaObjeto("dinheiro", "Dinheiro"),
                Set.of(),
                RealizacaoTextualObjeto.emPortugues("dinheiro", "dinheiro"),
                null
        );
    }

    @Test
    @DisplayName("Paulo tem 17 bolas e 11 azuis resolve PARTE = 6")
    void pauloResolveParte() {
        ComposicaoParteTodo relacao = ComposicaoParteTodo.construtor("paulo-bolas")
                .comTodo(bola.contar(17))
                .comParte(bola.contar(11))
                .construir();

        Numero parteCalculada = relacao.resolver(PapelQuantitativo.PARTE);
        assertEquals(Numero.de(6), parteCalculada);
        assertEquals(Numero.de(6), relacao.resolverParteAusente(2));
    }

    @Test
    @DisplayName("Resolver TODO realiza a soma das partes conhecidas")
    void resolverTodoSomaAsPartes() {
        ComposicaoParteTodo relacao = ComposicaoParteTodo.construtor("soma-partes")
                .comParte(bola.contar(6))
                .comParte(bola.contar(11))
                .construir();

        Numero todo = relacao.resolver(PapelQuantitativo.TODO);
        assertEquals(Numero.de(17), todo);
        assertEquals(Numero.de(17), relacao.resolverTodo());
    }

    @Test
    @DisplayName("Soma divergente do todo curado gera diagnóstico ESTADO_CALCULADO_DIVERGENTE")
    void somaDivergenteGeraDiagnosticoPreservandoDadoCurado() {
        ComposicaoParteTodo relacao = ComposicaoParteTodo.construtor("soma-divergente")
                .comTodo(bola.contar(20))
                .comParte(bola.contar(11))
                .comParte(bola.contar(6))
                .construir();

        ResultadoValidacao resultado = relacao.validar();

        assertFalse(resultado.ehValido());
        assertTrue(resultado.contem(TipoDiagnostico.ESTADO_CALCULADO_DIVERGENTE));
        assertEquals(20, relacao.getTodo().getValor(), "Valor curado do todo deve ser preservado");
    }

    @Test
    @DisplayName("Composição válida com soma das partes igual ao todo")
    void composicaoValida() {
        ComposicaoParteTodo relacao = ComposicaoParteTodo.construtor("composicao-valida")
                .comTodo(bola.contar(17))
                .comParte(bola.contar(11))
                .comParte(bola.contar(6))
                .construir();

        ResultadoValidacao resultado = relacao.validar();

        assertTrue(resultado.ehValido());
        assertFalse(resultado.possuiOcorrencias());
        assertEquals("COMPOSICAO_PARTE_TODO", relacao.getCategoria());
        assertEquals(2, relacao.getPartes().size());
    }

    @Test
    @DisplayName("Grandezas ou unidades incompatíveis entre as partes e o todo geram GRANDEZAS_OU_UNIDADES_INCOMPATIVEIS (Caso 15)")
    void grandezasOuUnidadesIncompativeisGeramDiagnostico() {
        Quantidade todoContagem = bola.contar(17);
        Quantidade parteMonetaria = new Quantidade(
                Numero.de(11), dinheiro, GrandezaQuantitativa.VALOR_MONETARIO, UnidadeMedida.REAL);

        ComposicaoParteTodo relacao = ComposicaoParteTodo.construtor("unidades-incompativeis")
                .comTodo(todoContagem)
                .comParte(parteMonetaria)
                .construir();

        ResultadoValidacao resultado = relacao.validar();

        assertFalse(resultado.ehValido());
        assertTrue(resultado.contem(TipoDiagnostico.GRANDEZAS_OU_UNIDADES_INCOMPATIVEIS));
    }
}
