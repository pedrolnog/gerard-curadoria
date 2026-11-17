package br.edu.univasf.gerard_curador.dominio.validacao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de unidade para Diagnostico")
class DiagnosticoTest {

    @Test
    @DisplayName("Deve lançar exceção ao instanciar diagnóstico com tipo nulo")
    void deveLancarExcecaoQuandoTipoForNulo() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Diagnostico(null, "elemento", "detalhe")
        );
        assertEquals("diagnostico exige tipo", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Deve adotar 'situacao' como elemento padrão quando elemento envolvido for nulo ou em branco")
    void deveAdotarElementoPadraoQuandoElementoForNuloOuEmBranco(String elemento) {
        Diagnostico diagnostico = new Diagnostico(TipoDiagnostico.PARTICIPANTE_INEXISTENTE, elemento, "detalhe");
        assertEquals("situacao", diagnostico.getElementoEnvolvido());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Deve adotar o nome exibido do tipo como detalhe padrão quando detalhe for nulo ou em branco")
    void deveAdotarDetalhePadraoQuandoDetalheForNuloOuEmBranco(String detalhe) {
        TipoDiagnostico tipo = TipoDiagnostico.PARTICIPANTE_INEXISTENTE;
        Diagnostico diagnostico = new Diagnostico(tipo, "elemento", detalhe);
        assertEquals(tipo.getNomeExibido(), diagnostico.getDetalhe());
    }

    @Test
    @DisplayName("Deve instanciar corretamente pelos construtores e fábrica estática com argumentos válidos")
    void deveInstanciarCorretamenteComValoresValidos() {
        Diagnostico diagnostico = Diagnostico.de(
                TipoDiagnostico.TRANSFORMACAO_SEM_ATOR,
                "transf1",
                "ator obrigatorio ausente"
        );

        assertEquals(TipoDiagnostico.TRANSFORMACAO_SEM_ATOR, diagnostico.getTipo());
        assertEquals("transf1", diagnostico.getElementoEnvolvido());
        assertEquals("ator obrigatorio ausente", diagnostico.getDetalhe());
        assertTrue(diagnostico.ehErro());
        assertFalse(diagnostico.ehAviso());
        assertTrue(diagnostico.ehDoTipo(TipoDiagnostico.TRANSFORMACAO_SEM_ATOR));
        assertFalse(diagnostico.ehDoTipo(TipoDiagnostico.PARTICIPANTE_INEXISTENTE));
    }

    @Test
    @DisplayName("Deve garantir igualdade por valor com equals e hashCode")
    void deveGarantirIgualdadePorValor() {
        Diagnostico d1 = new Diagnostico(TipoDiagnostico.PARTICIPANTE_INEXISTENTE, "ator", "detalhe");
        Diagnostico d2 = new Diagnostico(TipoDiagnostico.PARTICIPANTE_INEXISTENTE, "ator", "detalhe");
        Diagnostico dOutroTipo = new Diagnostico(TipoDiagnostico.OBJETO_CONTADO_INEXISTENTE, "ator", "detalhe");
        Diagnostico dOutroElemento = new Diagnostico(TipoDiagnostico.PARTICIPANTE_INEXISTENTE, "outro", "detalhe");
        Diagnostico dOutroDetalhe = new Diagnostico(TipoDiagnostico.PARTICIPANTE_INEXISTENTE, "ator", "outro");

        assertEquals(d1, d1, "Deve ser reflexivo");
        assertEquals(d1, d2, "Instâncias com mesmos valores devem ser iguais");
        assertEquals(d1.hashCode(), d2.hashCode(), "Instâncias iguais devem ter mesmo hashCode");

        assertNotEquals(d1, dOutroTipo, "Instâncias com tipos diferentes não devem ser iguais");
        assertNotEquals(d1, dOutroElemento, "Instâncias com elementos diferentes não devem ser iguais");
        assertNotEquals(d1, dOutroDetalhe, "Instâncias com detalhes diferentes não devem ser iguais");
        assertNotEquals(d1, null, "Não deve ser igual a null");
        assertNotEquals(d1, "uma string", "Não deve ser igual a objeto de outra classe");
    }

    @Test
    @DisplayName("Deve formatar a descrição contendo severidade, nome, elemento e detalhe")
    void deveFormatarDescricaoCorretamente() {
        Diagnostico diagnostico = new Diagnostico(
                TipoDiagnostico.ESTADO_CALCULADO_DIVERGENTE,
                "estado_final_curado",
                "esperado 3, obtido 4"
        );
        String descricao = diagnostico.descrever();

        assertEquals(
                "[ERRO] estado calculado divergente do estado final curado (em: estado_final_curado): esperado 3, obtido 4",
                descricao
        );
        assertEquals(descricao, diagnostico.toString());
    }
}
