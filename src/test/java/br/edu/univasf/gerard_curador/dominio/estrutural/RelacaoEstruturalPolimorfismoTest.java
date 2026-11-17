package br.edu.univasf.gerard_curador.dominio.estrutural;

import br.edu.univasf.gerard_curador.dominio.narrativo.ParticipanteNarrativo;
import br.edu.univasf.gerard_curador.dominio.narrativo.SentidoTransformacao;
import br.edu.univasf.gerard_curador.dominio.quantitativo.*;
import br.edu.univasf.gerard_curador.dominio.validacao.ResultadoValidacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Polimorfismo de Relação Estrutural")
class RelacaoEstruturalPolimorfismoTest {

    private ObjetoContado item;
    private ParticipanteNarrativo paulo;
    private ParticipanteNarrativo jose;

    @BeforeEach
    void setUp() {
        item = new ObjetoContado(
                "item",
                new FamiliaObjeto("item", "Item"),
                Set.of(),
                RealizacaoTextualObjeto.emPortugues("item", "itens"),
                null
        );
        paulo = new ParticipanteNarrativo("paulo", "Paulo");
        jose = new ParticipanteNarrativo("jose", "José");
    }

    @Test
    @DisplayName("Invoca validar(), resolver() e getCategoria() através de referências polimórficas de RelacaoEstrutural")
    void invocaMetodosPolimorficosViaReferenciaDaSuperclasse() {
        RelacaoEstrutural transf = ComposicaoDeTransformacoes.construtor("polimorfismo-transf")
                .comEstadoInicial(item.contar(5))
                .comPrimeiraTransformacao(item.contar(1), SentidoTransformacao.RETIRADA)
                .comSegundaTransformacao(item.contar(1), SentidoTransformacao.RETIRADA)
                .construir();

        RelacaoEstrutural parteTodo = ComposicaoParteTodo.construtor("polimorfismo-parte-todo")
                .comParte(item.contar(6))
                .comParte(item.contar(11))
                .construir();

        RelacaoEstrutural comp = ComparacaoQuantitativa.construtor("polimorfismo-comp")
                .comReferente(paulo, item.contar(6))
                .comReferido(jose, null)
                .comValorRelativo(item.contar(8), ComparacaoQuantitativa.Direcao.A_MAIS)
                .construir();

        List<RelacaoEstrutural> relacoes = List.of(transf, parteTodo, comp);

        // 1. Invocar validar() via referência da superclasse (Template Method com ligação dinâmica)
        for (RelacaoEstrutural relacao : relacoes) {
            ResultadoValidacao resultado = relacao.validar();
            assertNotNull(resultado, "O resultado da validação polimórfica não pode ser nulo");
            assertTrue(resultado.ehValido(), "Estruturas consistentes devem ser válidas");
        }

        // 2. Invocar resolver() via referência da superclasse (ligação dinâmica)
        Numero efResolvido = transf.resolver(PapelQuantitativo.ESTADO_FINAL);
        assertEquals(Numero.de(3), efResolvido,
                "Ligação dinâmica deve resolver ESTADO_FINAL na ComposiçãoDeTransformacoes");

        Numero todoResolvido = parteTodo.resolver(PapelQuantitativo.TODO);
        assertEquals(Numero.de(17), todoResolvido,
                "Ligação dinâmica deve resolver TODO na ComposiçãoParteTodo");

        Numero referidoResolvido = comp.resolver(PapelQuantitativo.REFERIDO);
        assertEquals(Numero.de(14), referidoResolvido,
                "Ligação dinâmica deve resolver REFERIDO na ComparacaoQuantitativa");

        // 3. Verificar que getCategoria() é distinto para cada subclasse
        Set<String> categoriasDistintas = new HashSet<>();
        for (RelacaoEstrutural relacao : relacoes) {
            String categoria = relacao.getCategoria();
            assertNotNull(categoria);
            assertFalse(categoria.isBlank());
            categoriasDistintas.add(categoria);
        }

        assertEquals(3, categoriasDistintas.size(), "Cada subclasse deve retornar uma categoria distinta");
        assertEquals("COMPOSICAO_TRANSFORMACOES", transf.getCategoria());
        assertEquals("COMPOSICAO_PARTE_TODO", parteTodo.getCategoria());
        assertEquals("COMPARACAO", comp.getCategoria());
    }

    @Test
    @DisplayName("Resolve parte desconhecida via referência polimórfica de RelacaoEstrutural")
    void resolveParteDesconhecidaViaReferenciaPolimorfica() {
        RelacaoEstrutural parteTodo = ComposicaoParteTodo.construtor("polimorfismo-parte-ausente")
                .comTodo(item.contar(17))
                .comParte(item.contar(11))
                .construir();

        Numero parteResolvida = parteTodo.resolver(PapelQuantitativo.PARTE);
        assertEquals(Numero.de(6), parteResolvida);
    }
}
