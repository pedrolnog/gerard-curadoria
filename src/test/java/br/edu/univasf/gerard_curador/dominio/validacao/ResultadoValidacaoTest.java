package br.edu.univasf.gerard_curador.dominio.validacao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de unidade para ResultadoValidacao")
class ResultadoValidacaoTest {

    @Test
    @DisplayName("Resultado sem ocorrências deve ser válido e vazio")
    void resultadoSemOcorrenciasDeveSerValido() {
        ResultadoValidacao resultado = ResultadoValidacao.semOcorrencias();

        assertTrue(resultado.ehValido());
        assertFalse(resultado.possuiOcorrencias());
        assertFalse(resultado.possuiAvisos());
        assertEquals(0, resultado.quantidadeDeOcorrencias());
        assertTrue(resultado.getDiagnosticos().isEmpty());
        assertTrue(resultado.getErros().isEmpty());
        assertTrue(resultado.getAvisos().isEmpty());
        assertEquals("nenhuma ocorrencia", resultado.descrever());
    }

    @Test
    @DisplayName("Resultado criado com lista vazia ou nula deve ser válido")
    void resultadoComListaNulaOuVaziaDeveSerValido() {
        ResultadoValidacao resultadoNulo = ResultadoValidacao.com(null);
        assertTrue(resultadoNulo.ehValido());
        assertEquals(0, resultadoNulo.quantidadeDeOcorrencias());

        ResultadoValidacao resultadoVazio = ResultadoValidacao.com(List.of());
        assertTrue(resultadoVazio.ehValido());
        assertEquals(0, resultadoVazio.quantidadeDeOcorrencias());
    }

    @Test
    @DisplayName("Um erro deve tornar o resultado inválido")
    void umErroTornaOResultadoInvalido() {
        ResultadoValidacao resultado = ResultadoValidacao.semOcorrencias()
                .acrescentar(TipoDiagnostico.PARTICIPANTE_INEXISTENTE, "ator", "participante nao cadastrado");

        assertFalse(resultado.ehValido());
        assertTrue(resultado.possuiOcorrencias());
        assertEquals(1, resultado.quantidadeDeOcorrencias());
        assertEquals(1, resultado.getErros().size());
        assertTrue(resultado.getAvisos().isEmpty());
    }

    @Test
    @DisplayName("Aviso não deve invalidar o resultado")
    void avisoNaoInvalidaOResultado() {
        ResultadoValidacao resultado = ResultadoValidacao.semOcorrencias()
                .acrescentar(TipoDiagnostico.QUANTIDADE_POR_DESTINATARIO_AMBIGUA, "destinatario", "ambiguidade detectada");

        assertTrue(resultado.ehValido(), "Presença de aviso isolado deve manter o resultado válido");
        assertTrue(resultado.possuiOcorrencias());
        assertTrue(resultado.possuiAvisos());
        assertEquals(1, resultado.quantidadeDeOcorrencias());
        assertEquals(1, resultado.getAvisos().size());
        assertTrue(resultado.getErros().isEmpty());
    }

    @Test
    @DisplayName("Resultado deve ser imutável: acrescentar devolve nova instância sem alterar a original")
    void acrescentarGaranteImutabilidade() {
        ResultadoValidacao original = ResultadoValidacao.semOcorrencias();
        Diagnostico diagnostico = new Diagnostico(TipoDiagnostico.TRANSFORMACAO_SEM_ATOR, "transf", "sem ator");

        ResultadoValidacao novo = original.acrescentar(diagnostico);

        assertNotSame(original, novo, "Acrescentar deve retornar uma nova instância");
        assertEquals(0, original.quantidadeDeOcorrencias(), "A instância original não deve ser modificada");
        assertTrue(original.ehValido(), "A instância original permanece válida");

        assertEquals(1, novo.quantidadeDeOcorrencias());
        assertFalse(novo.ehValido());
        assertTrue(novo.getDiagnosticos().contains(diagnostico));
    }

    @Test
    @DisplayName("Acrescentar diagnóstico nulo deve devolver a própria instância sem alterações")
    void acrescentarNuloNaoAlteraResultado() {
        ResultadoValidacao original = ResultadoValidacao.semOcorrencias();
        ResultadoValidacao retornado = original.acrescentar((Diagnostico) null);

        assertSame(original, retornado);
        assertEquals(0, retornado.quantidadeDeOcorrencias());
    }

    @Test
    @DisplayName("Lista retornada por getDiagnosticos deve ser cópia defensiva sem afetar o resultado")
    void getDiagnosticosNaoPermiteMutacaoExterna() {
        ResultadoValidacao resultado = ResultadoValidacao.semOcorrencias()
                .acrescentar(TipoDiagnostico.TRANSFORMACAO_SEM_ATOR, "transf", "sem ator");

        List<Diagnostico> lista = resultado.getDiagnosticos();
        lista.clear();

        assertEquals(1, resultado.quantidadeDeOcorrencias(), "Modificar a lista retornada não deve alterar o ResultadoValidacao");
    }

    @Test
    @DisplayName("contem() deve identificar presença de tipo registrado e ausência de não registrado")
    void deveVerificarContemTipo() {
        ResultadoValidacao resultado = ResultadoValidacao.semOcorrencias()
                .acrescentar(TipoDiagnostico.ESTADO_CALCULADO_DIVERGENTE, "estado", "divergente");

        assertTrue(resultado.contem(TipoDiagnostico.ESTADO_CALCULADO_DIVERGENTE));
        assertFalse(resultado.contem(TipoDiagnostico.PARTICIPANTE_INEXISTENTE));
    }

    @Test
    @DisplayName("combinarCom() deve unir diagnósticos preservando imutabilidade das instâncias originais")
    void deveCombinarResultadosPreservandoImutabilidade() {
        ResultadoValidacao r1 = ResultadoValidacao.semOcorrencias()
                .acrescentar(TipoDiagnostico.PARTICIPANTE_INEXISTENTE, "ator", "detalhe 1");

        ResultadoValidacao r2 = ResultadoValidacao.semOcorrencias()
                .acrescentar(TipoDiagnostico.QUANTIDADE_POR_DESTINATARIO_AMBIGUA, "dest", "detalhe 2");

        ResultadoValidacao combinado = r1.combinarCom(r2);

        assertNotSame(r1, combinado);
        assertNotSame(r2, combinado);
        assertEquals(1, r1.quantidadeDeOcorrencias());
        assertEquals(1, r2.quantidadeDeOcorrencias());
        assertEquals(2, combinado.quantidadeDeOcorrencias());
        assertTrue(combinado.contem(TipoDiagnostico.PARTICIPANTE_INEXISTENTE));
        assertTrue(combinado.contem(TipoDiagnostico.QUANTIDADE_POR_DESTINATARIO_AMBIGUA));
        assertFalse(combinado.ehValido(), "Como r1 possui erro, combinado deve ser inválido");
    }

    @Test
    @DisplayName("combinarCom() com nulo ou resultado vazio deve retornar a própria instância")
    void combinarComNuloOuVazioRetornaMesmaInstancia() {
        ResultadoValidacao r = ResultadoValidacao.semOcorrencias()
                .acrescentar(TipoDiagnostico.ORDEM_TEMPORAL_REPETIDA, "ordem", "repetida");

        assertSame(r, r.combinarCom(null));
        assertSame(r, r.combinarCom(ResultadoValidacao.semOcorrencias()));
    }

    @Test
    @DisplayName("getErros() e getAvisos() devem separar diagnósticos por severidade")
    void deveSepararErrosEAvisosPorSeveridade() {
        Diagnostico erro1 = new Diagnostico(TipoDiagnostico.PARTICIPANTE_INEXISTENTE, "p1", "nao existe");
        Diagnostico erro2 = new Diagnostico(TipoDiagnostico.ESTADO_CALCULADO_DIVERGENTE, "p2", "calculo errado");
        Diagnostico aviso1 = new Diagnostico(TipoDiagnostico.QUANTIDADE_POR_DESTINATARIO_AMBIGUA, "p3", "ambiguo");
        Diagnostico aviso2 = new Diagnostico(TipoDiagnostico.PARTICIPANTE_INCLUIDO_NO_TOTAL_NAO_DEFINIDO, "p4", "nao definido");

        ResultadoValidacao resultado = ResultadoValidacao.semOcorrencias()
                .acrescentar(erro1)
                .acrescentar(aviso1)
                .acrescentar(erro2)
                .acrescentar(aviso2);

        List<Diagnostico> erros = resultado.getErros();
        List<Diagnostico> avisos = resultado.getAvisos();

        assertEquals(2, erros.size());
        assertTrue(erros.contains(erro1));
        assertTrue(erros.contains(erro2));

        assertEquals(2, avisos.size());
        assertTrue(avisos.contains(aviso1));
        assertTrue(avisos.contains(aviso2));

        assertFalse(resultado.ehValido());
        assertTrue(resultado.possuiAvisos());
        assertTrue(resultado.possuiOcorrencias());
    }

    @Test
    @DisplayName("descrever() deve listar todas as ocorrências separadas por quebra de linha")
    void deveDescreverOcorrenciasEmLinhas() {
        Diagnostico erro = new Diagnostico(TipoDiagnostico.TRANSFORMACAO_SEM_ATOR, "t1", "ator ausente");
        Diagnostico aviso = new Diagnostico(TipoDiagnostico.REFERENCIA_VISUAL_AUSENTE, "t1", "sem imagem");

        ResultadoValidacao resultado = ResultadoValidacao.semOcorrencias()
                .acrescentar(erro)
                .acrescentar(aviso);

        String relatorio = resultado.descrever();
        assertTrue(relatorio.contains(erro.descrever()));
        assertTrue(relatorio.contains(aviso.descrever()));
        assertTrue(relatorio.contains(System.lineSeparator()));
        assertEquals(relatorio, resultado.toString());
    }
}
