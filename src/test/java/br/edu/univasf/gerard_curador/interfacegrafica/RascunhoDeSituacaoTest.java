package br.edu.univasf.gerard_curador.interfacegrafica;

import br.edu.univasf.gerard_curador.app.SituacoesCuradas;
import br.edu.univasf.gerard_curador.dominio.estrutural.PapelQuantitativo;
import br.edu.univasf.gerard_curador.dominio.narrativo.SentidoTransformacao;
import br.edu.univasf.gerard_curador.dominio.narrativo.TransformacaoNarrativa;
import br.edu.univasf.gerard_curador.dominio.quantitativo.FamiliaObjeto;
import br.edu.univasf.gerard_curador.dominio.situacao.SituacaoProblema;
import br.edu.univasf.gerard_curador.dominio.situacao.StatusCuradoria;
import br.edu.univasf.gerard_curador.dominio.validacao.TipoDiagnostico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da camada de interface gráfica - RascunhoDeSituacao e AcoesDisponiveis")
class RascunhoDeSituacaoTest {

    /** Reproduz exatamente o que a interface faz ao carregar o exemplo das rosas. */
    private RascunhoDeSituacao montarRascunhoDasRosas() {
        RascunhoDeSituacao rascunho = new RascunhoDeSituacao();
        rascunho.setId("rosas-01");
        rascunho.setEnunciado("Vovo possui 2 rosas brancas e 3 rosas amarelas. "
                + "Deu 1 rosa branca e 1 rosa amarela para a sua netinha. "
                + "Com quantas rosas vovo ficou?");
        rascunho.setContexto("", "colecao de flores", "");

        rascunho.adicionarParticipante("vovo", "Vovo", "vovo");
        rascunho.adicionarParticipante("netinha", "Netinha", "netinha");

        rascunho.adicionarObjeto("rosa_branca", "rosa", "rosa",
                Map.of("cor", "branca"), "rosa branca", "rosas brancas", "rosa_branca");
        rascunho.adicionarObjeto("rosa_amarela", "rosa", "rosa",
                Map.of("cor", "amarela"), "rosa amarela", "rosas amarelas", "rosa_amarela");

        rascunho.setPossuidorInicial("vovo");
        rascunho.definirItemInicial("rosa_branca", 2);
        rascunho.definirItemInicial("rosa_amarela", 3);

        rascunho.adicionarTransformacao(new RascunhoDeSituacao.TransformacaoRascunhada(
                2, "primeira entrega", "vovo", "netinha", "rosa_branca", 1,
                "dar", "deu", "dada", SentidoTransformacao.RETIRADA, true));
        rascunho.adicionarTransformacao(new RascunhoDeSituacao.TransformacaoRascunhada(
                3, "segunda entrega", "vovo", "netinha", "rosa_amarela", 1,
                "dar", "deu", "dada", SentidoTransformacao.RETIRADA, true));

        rascunho.setPossuidorFinal("vovo");
        rascunho.definirItemFinal("rosa_branca", 1);
        rascunho.definirItemFinal("rosa_amarela", 2);

        rascunho.setIncognita(PapelQuantitativo.ESTADO_FINAL, "Com quantas rosas vovo ficou?");
        return rascunho;
    }

    @Test
    @DisplayName("Montagem da situação das rosas a partir de rascunho preenchido produz situação válida")
    void testarMontagemDaSituacaoDasRosas() {
        RascunhoDeSituacao rascunho = montarRascunhoDasRosas();

        assertEquals(2, rascunho.getParticipantes().size(), "o catálogo tem 2 participantes");
        assertEquals(2, rascunho.getObjetos().size(), "o catálogo tem 2 objetos contados");
        assertEquals(1, rascunho.getFamilias().size(), "as duas rosas compartilham uma família");

        SituacaoProblema situacao = rascunho.montarSituacao();
        FamiliaObjeto rosa = situacao.getCatalogo().getFamilias().get(0);

        assertTrue(situacao.validar().ehValido(), "a situação montada pela tela é válida");
        assertEquals(5, situacao.getEstadoInicial().totalDaFamilia(rosa).getValor(),
                "o estado inicial totaliza cinco rosas");
        assertEquals(3, situacao.getEstadoFinalCurado().totalDaFamilia(rosa).getValor(),
                "o estado final curado totaliza três rosas");
        assertEquals(2, situacao.getTransformacoes().size(), "as duas transformações foram montadas");
        assertEquals(2, situacao.getTransformacoes().get(0).getOrdem(), "a ordem curada foi preservada");
        assertEquals(PapelQuantitativo.ESTADO_FINAL, situacao.getIncognita().getPapelOriginal(),
                "a incógnita designa o estado final");
        assertEquals(StatusCuradoria.CANDIDATA_NAO_CURADA, situacao.getStatus(),
                "nasce como candidata não curada");
        assertEquals(5, situacao.projetarSequenciaNarrativa().quantidadeDeEpisodios(),
                "a sequência tem 5 episódios");
    }

    @Test
    @DisplayName("Recusas de inconsistências produzidas pelas validações de domínio")
    void testarRecusasVindasDoDominio() {
        RascunhoDeSituacao rascunho = montarRascunhoDasRosas();

        assertThrows(IllegalArgumentException.class,
                () -> rascunho.adicionarParticipante("vovo", "Outra Vovo", "vovo"),
                "participante duplicado é recusado");
        assertThrows(IllegalArgumentException.class,
                () -> rascunho.adicionarObjeto("rosa_branca", "rosa", "rosa",
                        Map.of(), "x", "y", "z"),
                "objeto duplicado é recusado");
        assertThrows(IllegalArgumentException.class,
                () -> rascunho.adicionarObjeto("rosa_roxa", "rosa", "rosa",
                        Map.of("cor", "roxa"), "", "rosas roxas", "rosa_roxa"),
                "objeto sem singular é recusado");
        assertThrows(IllegalArgumentException.class,
                () -> rascunho.adicionarParticipante("tio", "", "tio"),
                "participante sem nome é recusado");

        // Transformação referindo objeto que não está no catálogo
        RascunhoDeSituacao comObjetoInvalido = montarRascunhoDasRosas();
        comObjetoInvalido.adicionarTransformacao(
                new RascunhoDeSituacao.TransformacaoRascunhada(
                        4, "terceira", "vovo", "netinha", "rosa_inexistente", 1,
                        "dar", "deu", "dada", SentidoTransformacao.RETIRADA, true));
        assertThrows(IllegalArgumentException.class,
                comObjetoInvalido::montarSituacao,
                "transformação com objeto fora do catálogo é recusada");

        // Ator igual ao destinatário
        RascunhoDeSituacao comAtorIgual = montarRascunhoDasRosas();
        comAtorIgual.adicionarTransformacao(
                new RascunhoDeSituacao.TransformacaoRascunhada(
                        5, "quarta", "vovo", "vovo", "rosa_branca", 1,
                        "dar", "deu", "dada", SentidoTransformacao.RETIRADA, true));
        assertThrows(IllegalArgumentException.class,
                comAtorIgual::montarSituacao,
                "ator igual ao destinatário é recusado");

        // Estado inicial sem possuidor selecionado
        RascunhoDeSituacao semPossuidor = new RascunhoDeSituacao();
        semPossuidor.setId("x");
        semPossuidor.setEnunciado("enunciado de teste");
        assertThrows(IllegalArgumentException.class,
                semPossuidor::montarSituacao,
                "estado inicial sem possuidor é recusado");
    }

    @Test
    @DisplayName("Divergência entre estado curado e calculado é diagnosticada sem ser corrigida")
    void testarDivergenciaNaoCorrigida() {
        RascunhoDeSituacao rascunho = montarRascunhoDasRosas();
        // O curador digita 4 onde o cálculo produz 3
        rascunho.definirItemFinal("rosa_amarela", 3);

        SituacaoProblema situacao = rascunho.montarSituacao();
        FamiliaObjeto rosa = situacao.getCatalogo().getFamilias().get(0);

        assertFalse(situacao.validar().ehValido(), "a divergência invalida a situação");
        assertTrue(situacao.validar().contem(TipoDiagnostico.ESTADO_CALCULADO_DIVERGENTE),
                "o diagnóstico é o de estado calculado divergente");
        assertEquals(4, situacao.getEstadoFinalCurado().totalDaFamilia(rosa).getValor(),
                "o estado final curado permanece com 4 rosas");
        assertEquals(3, situacao.calcularEstadoFinal().totalDaFamilia(rosa).getValor(),
                "o estado calculado continua sendo 3");
    }

    @Test
    @DisplayName("Ações disponíveis fornecem sentido e regras sem escolha solta")
    void testarAcoesDisponiveis() {
        assertFalse(AcoesDisponiveis.listar().isEmpty(), "há ações curadas disponíveis");

        boolean darRetira = AcoesDisponiveis.listar().stream()
                .filter(acao -> "dar".equals(acao.getId()))
                .allMatch(acao -> acao.getSentido() == SentidoTransformacao.RETIRADA);
        assertTrue(darRetira, "dar sempre implica retirada");

        boolean ganharAcrescenta = AcoesDisponiveis.listar().stream()
                .filter(acao -> "ganhar".equals(acao.getId()))
                .allMatch(acao -> acao.getSentido() == SentidoTransformacao.ACRESCIMO);
        assertTrue(ganharAcrescenta, "ganhar sempre implica acréscimo");

        boolean comerSemDestinatario = AcoesDisponiveis.listar().stream()
                .filter(acao -> "comer".equals(acao.getId()))
                .noneMatch(acao -> acao.exigeDestinatario());
        assertTrue(comerSemDestinatario, "comer não exige destinatário");

        // O rótulo do menu revela o sentido ao curador no momento da escolha
        String rotulo = AcoesDisponiveis.rotularParaMenu(AcoesDisponiveis.listar().get(0));
        assertTrue(rotulo.contains("retirada") || rotulo.contains("acréscimo") || rotulo.contains("acrescimo"),
                "o rótulo do menu revela o sentido");
    }

    @Test
    @DisplayName("Curadoria continua humana e promoção exige revisor identificado")
    void testarCuradoriaHumana() {
        SituacaoProblema situacao = montarRascunhoDasRosas().montarSituacao();

        assertEquals(StatusCuradoria.CANDIDATA_NAO_CURADA, situacao.getStatus(),
                "a situação montada pela tela nasce candidata");
        assertTrue(situacao.validar().ehValido()
                        && situacao.getStatus() == StatusCuradoria.CANDIDATA_NAO_CURADA,
                "validar com sucesso não promove a situação");
        assertThrows(IllegalArgumentException.class,
                () -> situacao.promoverParaValidada(""),
                "promover sem revisor é recusado");

        SituacaoProblema validada = situacao.promoverParaValidada("professor");
        assertEquals(StatusCuradoria.VALIDADA, validada.getStatus(),
                "com revisor identificado, fica validada");
        assertEquals(StatusCuradoria.CANDIDATA_NAO_CURADA, situacao.getStatus(),
                "a situação original permanece candidata");
    }

    @Test
    @DisplayName("aPartirDe com situação das rosas produz situação válida com mesmo id, transformações e estado final curado")
    void testarRascunhoAPartirDeSituacaoCurada() {
        SituacaoProblema original = SituacoesCuradas.rosas();
        RascunhoDeSituacao rascunho = RascunhoDeSituacao.aPartirDe(original);
        SituacaoProblema reconstruida = rascunho.montarSituacao();

        assertTrue(reconstruida.validar().ehValido(), "a situação reconstruída deve ser válida");
        assertEquals(original.getId(), reconstruida.getId(), "deve possuir o mesmo id");

        assertEquals(original.getTransformacoes().size(), reconstruida.getTransformacoes().size(),
                "deve possuir a mesma quantidade de transformações");
        for (int i = 0; i < original.getTransformacoes().size(); i++) {
            TransformacaoNarrativa originalTrans = original.getTransformacoes().get(i);
            TransformacaoNarrativa reconTrans = reconstruida.getTransformacoes().get(i);

            assertEquals(originalTrans.getOrdem(), reconTrans.getOrdem(),
                    "ordem da transformação deve ser idêntica");
            assertEquals(originalTrans.getMarcador().getRotulo(), reconTrans.getMarcador().getRotulo(),
                    "rótulo temporal da transformação deve ser idêntico");
            assertEquals(originalTrans.getAtor().getId(), reconTrans.getAtor().getId(),
                    "ator da transformação deve ser idêntico");
            if (originalTrans.possuiDestinatario()) {
                assertNotNull(reconTrans.getDestinatario(), "destinatário deve existir");
                assertEquals(originalTrans.getDestinatario().getId(), reconTrans.getDestinatario().getId(),
                        "destinatário deve ser idêntico");
            } else {
                assertNull(reconTrans.getDestinatario(), "não deve possuir destinatário");
            }
            assertEquals(originalTrans.getQuantidade().getObjeto().getId(),
                    reconTrans.getQuantidade().getObjeto().getId(),
                    "objeto da quantidade deve ser idêntico");
            assertEquals(originalTrans.getQuantidade().getValor(),
                    reconTrans.getQuantidade().getValor(),
                    "valor da quantidade deve ser idêntico");
            assertEquals(originalTrans.getAcao().getId(), reconTrans.getAcao().getId(),
                    "ação deve ser idêntica");
            assertEquals(originalTrans.getAcao().getSentido(), reconTrans.getAcao().getSentido(),
                    "sentido da ação deve ser idêntico");
        }

        assertNotNull(original.getEstadoFinalCurado(), "estado final curado original existe");
        assertNotNull(reconstruida.getEstadoFinalCurado(), "estado final curado reconstruído existe");
        assertEquals(original.getEstadoFinalCurado().getPossuidor().getId(),
                reconstruida.getEstadoFinalCurado().getPossuidor().getId(),
                "possuidor do estado final curado deve coincidir");
        assertTrue(original.getEstadoFinalCurado().possuiMesmoConteudoQue(reconstruida.getEstadoFinalCurado()),
                "mesmo estado final curado");
    }
}
