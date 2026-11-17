package br.edu.univasf.gerard_curador.app;

import br.edu.univasf.gerard_curador.dominio.quantitativo.GrandezaQuantitativa;
import br.edu.univasf.gerard_curador.dominio.quantitativo.UnidadeMedida;

import br.edu.univasf.gerard_curador.dominio.narrativo.EstadoDePosse;
import br.edu.univasf.gerard_curador.dominio.narrativo.SentidoTransformacao;
import br.edu.univasf.gerard_curador.dominio.quantitativo.CaracteristicaObjeto;
import br.edu.univasf.gerard_curador.dominio.quantitativo.FamiliaObjeto;
import br.edu.univasf.gerard_curador.dominio.quantitativo.Numero;
import br.edu.univasf.gerard_curador.dominio.quantitativo.ObjetoContado;
import br.edu.univasf.gerard_curador.dominio.quantitativo.Quantidade;
import br.edu.univasf.gerard_curador.dominio.quantitativo.RealizacaoTextualObjeto;
import br.edu.univasf.gerard_curador.dominio.quantitativo.ReferenciaVisual;
import br.edu.univasf.gerard_curador.dominio.estrutural.ComposicaoDeTransformacoes;
import br.edu.univasf.gerard_curador.dominio.estrutural.IncognitaQuantitativa;
import br.edu.univasf.gerard_curador.dominio.estrutural.PapelQuantitativo;
import br.edu.univasf.gerard_curador.dominio.situacao.SituacaoProblema;
import br.edu.univasf.gerard_curador.dominio.validacao.Diagnostico;
import br.edu.univasf.gerard_curador.dominio.validacao.ResultadoValidacao;
import br.edu.univasf.gerard_curador.dominio.repositorio.Repositorio;
import br.edu.univasf.gerard_curador.persistencia.RepositorioSituacaoJson;
import br.edu.univasf.gerard_curador.representacao.quadrinhos.RenderizadorDeRoteiro;
import br.edu.univasf.gerard_curador.representacao.quadrinhos.RenderizadorHtml;
import br.edu.univasf.gerard_curador.representacao.quadrinhos.RenderizadorTextual;
import java.util.List;
import br.edu.univasf.gerard_curador.representacao.quadrinhos.GeradorRoteiroQuadrinhos;
import br.edu.univasf.gerard_curador.representacao.quadrinhos.RoteiroQuadrinhos;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Demonstracao executavel das tres situacoes curadas.
 *
 * <p>Percorre o caminho completo exigido pela apresentacao final: monta as
 * situacoes, valida, exibe a sequencia semantica, gera os roteiros de quadrinhos
 * em texto e HTML, grava os JSON e confirma a ida e volta pela persistencia.
 *
 * <p>Esta classe pertence a camada de aplicacao. Ela orquestra, mas nao decide
 * regra alguma: cada resposta vem do objeto de dominio responsavel.
 */
public final class Demonstracao {

    private static final Path DIRETORIO_DADOS = Paths.get("dados");
    private static final Path DIRETORIO_ROTEIROS = Paths.get("dados", "roteiros");

    public static void main(String[] argumentos) {
        SituacaoProblema[] situacoes = {
                SituacoesCuradas.rosas(),
                SituacoesCuradas.figurinhasComEstadoInicialDesconhecido(),
                SituacoesCuradas.bilasComDuasPartidas()
        };

        for (SituacaoProblema situacao : situacoes) {
            apresentar(situacao);
        }

        demonstrarPreservacaoDaIncognita();
        demonstrarDivergenciaNaoCorrigida();
        demonstrarRecusaDeOperacoesIncompativeis();
        demonstrarSituacaoForaDoEscopo();
        demonstrarAmbiguidadeSemAssociacaoAutomatica();
    }

    /** Situacao multiplicativa rotulada pelo curador: nao e forcada ao modelo aditivo. */
    private static void demonstrarSituacaoForaDoEscopo() {
        separador("situacao multiplicativa rotulada fora do escopo");
        SituacaoProblema golfinhos = SituacaoProblema.construtor("golfinhos-01",
                        "Cada golfinho comeu 3 peixes. Eram 4 golfinhos. Quantos peixes foram comidos?")
                .comCatalogo(SituacoesCuradas.rosas().getCatalogo())
                .rotuladaForaDoEscopo()
                .construir();
        for (Diagnostico diagnostico : golfinhos.validar().getDiagnosticos()) {
            System.out.println("  " + diagnostico.descrever());
        }
        try {
            golfinhos.calcularEstadoFinal();
        } catch (IllegalStateException recusa) {
            System.out.println("  calculo aditivo recusado: " + recusa.getMessage());
        }
        System.out.println();
    }

    /** Trecho ambiguo vira diagnostico; nenhuma leitura e escolhida pelo sistema. */
    private static void demonstrarAmbiguidadeSemAssociacaoAutomatica() {
        separador("enunciado ambiguo: diagnostico sem associacao automatica");
        SituacaoProblema base = SituacoesCuradas.rosas();
        SituacaoProblema ambigua = SituacaoProblema.construtor("rosas-ambigua",
                        "Vovo deu 4 rosas para as netinhas.")
                .comCatalogo(base.getCatalogo())
                .comEstadoInicial(base.getEstadoInicial())
                .comAmbiguidade("deu 4 rosas para as netinhas",
                        "nao diz quantas rosas cada netinha recebeu")
                .construir();
        for (Diagnostico diagnostico : ambigua.validar().getDiagnosticos()) {
            System.out.println("  " + diagnostico.descrever());
        }
        System.out.println("  transformacoes criadas automaticamente: " + ambigua.getTransformacoes().size());
        try {
            ambigua.promoverParaValidada("professora");
        } catch (IllegalStateException recusa) {
            System.out.println("  promocao recusada: " + recusa.getMessage());
        }
        System.out.println();
    }

    private static void apresentar(SituacaoProblema situacao) {
        separador(situacao.getId());
        System.out.println("Enunciado: " + situacao.getEnunciado());
        System.out.println("Status: " + situacao.getStatus().getNomeExibido());
        System.out.println();

        System.out.println("--- Sequencia narrativa semantica ---");
        System.out.println(situacao.projetarSequenciaNarrativa().descrever());
        System.out.println();

        System.out.println("--- Percurso dos estados (os anteriores permanecem intactos) ---");
        for (EstadoDePosse estado : situacao.calcularPercursoDeEstados()) {
            System.out.println("  " + estado.getMomento().getRotulo() + ": " + estado.descrever());
        }
        System.out.println();

        System.out.println("--- Totais por familia ---");
        for (FamiliaObjeto familia : situacao.getCatalogo().getFamilias()) {
            System.out.println("  inicial (" + familia.getNomeConceitual() + "): "
                    + situacao.getEstadoInicial().totalDaFamilia(familia));
            if (situacao.getEstadoFinalCurado() != null) {
                System.out.println("  final curado (" + familia.getNomeConceitual() + "): "
                        + situacao.getEstadoFinalCurado().totalDaFamilia(familia));
            }
        }
        System.out.println();

        System.out.println("--- Incognita ---");
        System.out.println("  " + situacao.getIncognita().descreverComPapel());
        System.out.println();

        System.out.println("--- Validacao ---");
        ResultadoValidacao resultado = situacao.validar();
        System.out.println("  valido: " + resultado.ehValido());
        for (Diagnostico diagnostico : resultado.getDiagnosticos()) {
            System.out.println("  " + diagnostico.descrever());
        }
        System.out.println();

        GeradorRoteiroQuadrinhos gerador = new GeradorRoteiroQuadrinhos();
        RoteiroQuadrinhos roteiro = gerador.gerar(
                situacao.projetarSequenciaNarrativa(), "Historia de " + situacao.getId());
        System.out.println("--- Roteiro de quadrinhos (" + roteiro.quantidadeDeQuadros()
                + " quadros) ---");
        System.out.println(new RenderizadorTextual().renderizar(roteiro));

        persistir(situacao, roteiro);
    }

    /** Grava JSON e as duas realizacoes do roteiro, confirmando a ida e volta. */
    private static void persistir(SituacaoProblema situacao, RoteiroQuadrinhos roteiro) {
        // Depende da interface do dominio; a implementacao JSON e escolhida so aqui.
        Repositorio<SituacaoProblema, String> repositorio = new RepositorioSituacaoJson(DIRETORIO_DADOS);
        Path arquivoJson = DIRETORIO_DADOS.resolve(situacao.getId() + ".json");
        repositorio.salvar(situacao);

        SituacaoProblema recarregada = repositorio.buscarPorId(situacao.getId()).orElseThrow();
        boolean idaEVolta = recarregada.getId().equals(situacao.getId())
                && recarregada.getIncognita().getPapelOriginal()
                        == situacao.getIncognita().getPapelOriginal()
                && recarregada.getEstadoInicial().getInventario()
                        .possuiMesmoConteudoQue(situacao.getEstadoInicial().getInventario());

        System.out.println("--- Persistencia ---");
        System.out.println("  gravado em: " + arquivoJson);
        System.out.println("  ida e volta preserva o modelo: " + idaEVolta);

        // Strategy: o mesmo roteiro, cada renderizador em sua midia.
        List<RenderizadorDeRoteiro> renderizadores = List.of(new RenderizadorTextual(), new RenderizadorHtml());
        for (RenderizadorDeRoteiro renderizador : renderizadores) {
            gravarTexto(DIRETORIO_ROTEIROS.resolve(situacao.getId() + "." + renderizador.getExtensao()),
                    renderizador.renderizar(roteiro));
        }
        System.out.println("  roteiros em: " + DIRETORIO_ROTEIROS.resolve(situacao.getId()));
        System.out.println();
    }

    private static void gravarTexto(Path destino, String conteudo) {
        try {
            if (destino.getParent() != null) {
                Files.createDirectories(destino.getParent());
            }
            Files.writeString(destino, conteudo, StandardCharsets.UTF_8);
        } catch (IOException erro) {
            System.out.println("  falha ao gravar " + destino + ": " + erro.getMessage());
        }
    }

    /**
     * Mostra que a incognita continua identificada depois de resolvida.
     *
     * <p>Evidencia pedida na apresentacao final.
     */
    private static void demonstrarPreservacaoDaIncognita() {
        separador("preservacao da incognita original");
        SituacaoProblema situacao = SituacoesCuradas.figurinhasComEstadoInicialDesconhecido();

        System.out.println("Antes de resolver: " + situacao.getIncognita().descreverComPapel());
        System.out.println("Papel designado: " + situacao.getIncognita().getPapelOriginal());
        System.out.println("Chave estavel para o JSON: " + situacao.getIncognita().chaveDoPapel());
        System.out.println("Ja esta resolvida? " + situacao.getIncognita().estaResolvida());

        // A relacao equivalente sem o estado inicial curado e o que o aluno
        // enfrenta: dados 12 de acrescimo e 17 ao final, quanto havia antes?
        ComposicaoDeTransformacoes comoOAlunoVe =
                ComposicaoDeTransformacoes.construtor("figurinhas-sem-inicial")
                        .comPrimeiraTransformacao(
                                figurinhas(12), SentidoTransformacao.ACRESCIMO)
                        .comEstadoFinal(figurinhas(17))
                        .construir();
        Numero respostaCalculada = comoOAlunoVe.resolver(PapelQuantitativo.ESTADO_INICIAL);
        System.out.println("A relacao resolve o estado inicial como: " + respostaCalculada);

        IncognitaQuantitativa resolvida =
                situacao.getIncognita().resolver(objetoDeFigurinha().contar(
                        respostaCalculada.getValor()));
        System.out.println("Depois de resolver: " + resolvida.descreverComPapel());
        System.out.println("O papel original permanece consultavel: "
                + resolvida.getPapelOriginal().getNomeExibido());
        System.out.println("Continua designando o estado inicial? "
                + resolvida.designa(PapelQuantitativo.ESTADO_INICIAL));
        System.out.println();
    }

    /**
     * Mostra que um estado final curado divergente gera diagnostico, nao correcao.
     *
     * <p>Monta deliberadamente uma situacao errada: a Vovo daria as duas rosas,
     * mas o curador digitou 4 como total final. O sistema aponta e preserva.
     */
    private static void demonstrarDivergenciaNaoCorrigida() {
        separador("divergencia diagnosticada, nunca sobrescrita");
        ComposicaoDeTransformacoes relacaoErrada =
                ComposicaoDeTransformacoes.construtor("rosas-divergente")
                        .comEstadoInicial(quantidadeDeRosas(5))
                        .comPrimeiraTransformacao(
                                quantidadeDeRosas(1), SentidoTransformacao.RETIRADA)
                        .comSegundaTransformacao(
                                quantidadeDeRosas(1), SentidoTransformacao.RETIRADA)
                        .comEstadoFinal(quantidadeDeRosas(4))
                        .construir();

        ResultadoValidacao resultado = relacaoErrada.validar();
        System.out.println("Valido: " + resultado.ehValido());
        for (Diagnostico diagnostico : resultado.getDiagnosticos()) {
            System.out.println("  " + diagnostico.descrever());
        }
        System.out.println("O valor curado continua sendo: "
                + relacaoErrada.quantidadeDoPapel(PapelQuantitativo.ESTADO_FINAL).getValor());
        System.out.println();
    }

    /** Mostra as duas recusas exigidas pelos criterios de aceitacao. */
    private static void demonstrarRecusaDeOperacoesIncompativeis() {
        separador("operacoes incompativeis recusadas pelo dominio");

        ObjetoContado rosa = objetoDeRosa();
        ObjetoContado boneca = new ObjetoContado(
                "boneca",
                new FamiliaObjeto("boneca", "boneca"),
                java.util.Collections.emptySet(),
                RealizacaoTextualObjeto.emPortugues("boneca", "bonecas"),
                ReferenciaVisual.de("boneca"));

        try {
            rosa.contar(2).somar(boneca.contar(3));
            System.out.println("  ERRO: a soma de rosas e bonecas nao foi recusada");
        } catch (IllegalArgumentException erro) {
            System.out.println("  somar rosas e bonecas: " + erro.getMessage());
        }

        Quantidade emReais = rosa.quantificar(
                Numero.de(18),
                br.edu.univasf.gerard_curador.dominio.quantitativo.GrandezaQuantitativa.VALOR_MONETARIO,
                br.edu.univasf.gerard_curador.dominio.quantitativo.UnidadeMedida.REAL);
        Quantidade emQuilos = rosa.quantificar(
                Numero.de(7),
                br.edu.univasf.gerard_curador.dominio.quantitativo.GrandezaQuantitativa.MASSA,
                br.edu.univasf.gerard_curador.dominio.quantitativo.UnidadeMedida.QUILOGRAMA);
        try {
            emReais.somar(emQuilos);
            System.out.println("  ERRO: a soma de reais e quilogramas nao foi recusada");
        } catch (IllegalArgumentException erro) {
            System.out.println("  somar reais e quilogramas: " + erro.getMessage());
        }

        try {
            rosa.contar(2).subtrair(rosa.contar(3));
            System.out.println("  ERRO: a retirada impossivel nao foi recusada");
        } catch (IllegalArgumentException erro) {
            System.out.println("  retirar 3 de 2: " + erro.getMessage());
        }
        System.out.println();
    }

    private static ObjetoContado objetoDeRosa() {
        return new ObjetoContado(
                "rosa_branca",
                new FamiliaObjeto("rosa", "rosa"),
                java.util.Collections.singleton(CaracteristicaObjeto.de("cor", "branca")),
                RealizacaoTextualObjeto.emPortugues("rosa branca", "rosas brancas"),
                ReferenciaVisual.de("rosa_branca"));
    }

    private static ObjetoContado objetoDeFigurinha() {
        return new ObjetoContado(
                "figurinha_comum",
                new FamiliaObjeto("figurinha", "figurinha"),
                java.util.Collections.emptySet(),
                RealizacaoTextualObjeto.emPortugues("figurinha", "figurinhas"),
                ReferenciaVisual.de("figurinha"));
    }

    private static Quantidade quantidadeDeRosas(int valor) {
        return objetoDeRosa().contar(valor);
    }

    private static Quantidade figurinhas(int valor) {
        return objetoDeFigurinha().contar(valor);
    }

    private static void separador(String titulo) {
        System.out.println("=".repeat(72));
        System.out.println(titulo.toUpperCase());
        System.out.println("=".repeat(72));
    }
}
