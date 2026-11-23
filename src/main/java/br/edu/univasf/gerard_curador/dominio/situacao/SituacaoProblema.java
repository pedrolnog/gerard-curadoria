package br.edu.univasf.gerard_curador.dominio.situacao;

import br.edu.univasf.gerard_curador.dominio.narrativo.ContextoNarrativo;
import br.edu.univasf.gerard_curador.dominio.narrativo.EstadoDePosse;
import br.edu.univasf.gerard_curador.dominio.narrativo.MarcadorTemporal;
import br.edu.univasf.gerard_curador.dominio.narrativo.TransformacaoNarrativa;
import br.edu.univasf.gerard_curador.dominio.quantitativo.Quantidade;
import br.edu.univasf.gerard_curador.dominio.estrutural.IncognitaQuantitativa;
import br.edu.univasf.gerard_curador.dominio.estrutural.RelacaoEstrutural;
import br.edu.univasf.gerard_curador.dominio.validacao.Diagnostico;
import br.edu.univasf.gerard_curador.dominio.validacao.ResultadoValidacao;
import br.edu.univasf.gerard_curador.dominio.validacao.TipoDiagnostico;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Coordena a historia, valida a sequencia e produz a projecao narrativa.
 *
 * <p>Agregado raiz do modelo. Detem a fronteira "ordem e coerencia da historia":
 * conhece o catalogo, o estado inicial, as transformacoes ordenadas, o estado
 * final curado, a incognita designada e a relacao estrutural que rege o arranjo.
 *
 * <p>O que esta classe faz:
 * <ul>
 *   <li><strong>valida</strong> a estrutura, acumulando diagnosticos sem corrigir
 *       nada;
 *   <li><strong>calcula</strong> o percurso dos estados aplicando as
 *       transformacoes em ordem;
 *   <li><strong>projeta</strong> a sequencia narrativa semantica.
 * </ul>
 *
 * <p>O que ela deliberadamente <strong>nao</strong> faz: desenhar quadros
 * (pergunta 9), sobrescrever o estado final curado divergente (caso de teste 9),
 * inferir papeis a partir do enunciado, ou promover a si mesma ao catalogo
 * validado.
 *
 * <p>Entidade mutavel com setters validados e {@code id} fixo. O status de
 * curadoria so muda por {@link #promoverParaValidada(String)} ou
 * {@link #recusar(String, String)}; qualquer alteracao de conteudo devolve a
 * situacao ao status de candidata para que passe por nova revisao humana.
 */
public final class SituacaoProblema {

    private final String id;
    private String enunciado;
    private CatalogoDaSituacao catalogo;
    private EstadoDePosse estadoInicial;
    private List<TransformacaoNarrativa> transformacoes;
    private EstadoDePosse estadoFinalCurado;
    private IncognitaQuantitativa incognita;
    private RelacaoEstrutural relacaoEstrutural;
    private ContextoNarrativo contexto;
    private StatusCuradoria status;
    private String revisor;
    private String motivoDaRecusa;
    private EscopoDaSituacao escopo;
    private final List<AmbiguidadeDeclarada> ambiguidades;

    private SituacaoProblema(String id,
                             String enunciado,
                             CatalogoDaSituacao catalogo,
                             EstadoDePosse estadoInicial,
                             List<TransformacaoNarrativa> transformacoes,
                             EstadoDePosse estadoFinalCurado,
                             IncognitaQuantitativa incognita,
                             RelacaoEstrutural relacaoEstrutural,
                             ContextoNarrativo contexto,
                             StatusCuradoria status,
                             String revisor,
                             String motivoDaRecusa,
                             EscopoDaSituacao escopo,
                             List<AmbiguidadeDeclarada> ambiguidades) {
        validarId(id);
        validarEnunciado(enunciado);
        validarCatalogo(catalogo);
        EscopoDaSituacao escopoNormalizado = normalizarEscopo(escopo);
        // Situacao fora do escopo nao e forcada ao modelo aditivo: nao exige estado inicial.
        validarEstadoInicial(estadoInicial, escopoNormalizado);
        this.escopo = escopoNormalizado;
        this.ambiguidades = ambiguidades == null
                ? new ArrayList<>() : new ArrayList<>(ambiguidades);
        this.id = id;
        this.enunciado = enunciado;
        this.catalogo = catalogo;
        this.estadoInicial = estadoInicial;
        this.transformacoes = transformacoes == null
                ? new ArrayList<>() : new ArrayList<>(transformacoes);
        this.estadoFinalCurado = estadoFinalCurado;
        this.incognita = incognita;
        this.relacaoEstrutural = relacaoEstrutural;
        this.contexto = normalizarContexto(contexto);
        this.status = status == null ? StatusCuradoria.CANDIDATA_NAO_CURADA : status;
        this.revisor = revisor;
        this.motivoDaRecusa = motivoDaRecusa;
    }

    private static void validarId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("situacao exige id");
        }
    }

    private static void validarEnunciado(String enunciado) {
        if (enunciado == null || enunciado.isBlank()) {
            throw new IllegalArgumentException("situacao exige enunciado curado");
        }
    }

    private static void validarCatalogo(CatalogoDaSituacao catalogo) {
        if (catalogo == null) {
            throw new IllegalArgumentException("situacao exige catalogo de participantes e objetos");
        }
    }

    private static void validarEstadoInicial(EstadoDePosse estadoInicial, EscopoDaSituacao escopo) {
        if (estadoInicial == null && escopo.permiteModeloAditivo()) {
            throw new IllegalArgumentException("situacao exige estado inicial");
        }
    }

    private static EscopoDaSituacao normalizarEscopo(EscopoDaSituacao escopo) {
        return escopo == null ? EscopoDaSituacao.ADITIVO : escopo;
    }

    private static ContextoNarrativo normalizarContexto(ContextoNarrativo contexto) {
        return contexto == null ? ContextoNarrativo.naoInformado() : contexto;
    }

    public static Construtor construtor(String id, String enunciado) {
        return new Construtor(id, enunciado);
    }

    public String getId() {
        return id;
    }

    public String getEnunciado() {
        return enunciado;
    }

    /** Atualiza o enunciado curado e devolve a situacao para candidata. */
    public void setEnunciado(String enunciado) {
        validarEnunciado(enunciado);
        this.enunciado = enunciado;
        voltarParaCandidata();
    }

    public CatalogoDaSituacao getCatalogo() {
        return catalogo;
    }

    /** Atualiza o catalogo da situacao e a devolve para candidata. */
    public void setCatalogo(CatalogoDaSituacao catalogo) {
        validarCatalogo(catalogo);
        this.catalogo = catalogo;
        voltarParaCandidata();
    }

    public EstadoDePosse getEstadoInicial() {
        return estadoInicial;
    }

    /** Atualiza o estado inicial conforme o escopo atual e devolve a situacao para candidata. */
    public void setEstadoInicial(EstadoDePosse estadoInicial) {
        validarEstadoInicial(estadoInicial, this.escopo);
        this.estadoInicial = estadoInicial;
        voltarParaCandidata();
    }

    /** Devolve visao nao modificavel das transformacoes ordenadas por ordem temporal. */
    public List<TransformacaoNarrativa> getTransformacoes() {
        List<TransformacaoNarrativa> ordenadas = new ArrayList<>(transformacoes);
        ordenadas.sort((a, b) -> Integer.compare(a.getOrdem(), b.getOrdem()));
        return Collections.unmodifiableList(ordenadas);
    }

    /** Substitui a lista de transformacoes com copia defensiva e devolve a situacao para candidata. */
    public void setTransformacoes(List<TransformacaoNarrativa> transformacoes) {
        this.transformacoes = transformacoes == null
                ? new ArrayList<>() : new ArrayList<>(transformacoes);
        voltarParaCandidata();
    }

    /** Adiciona uma transformacao a situacao e a devolve para candidata. */
    public void adicionarTransformacao(TransformacaoNarrativa transformacao) {
        if (transformacao == null) {
            throw new IllegalArgumentException("transformacao nao pode ser nula");
        }
        this.transformacoes.add(transformacao);
        voltarParaCandidata();
    }

    /** Remove uma transformacao existente da situacao e a devolve para candidata. */
    public void removerTransformacao(TransformacaoNarrativa transformacao) {
        if (transformacao == null || !this.transformacoes.remove(transformacao)) {
            throw new IllegalArgumentException("transformacao nao pertence a situacao");
        }
        voltarParaCandidata();
    }

    public EstadoDePosse getEstadoFinalCurado() {
        return estadoFinalCurado;
    }

    /** Atualiza o estado final curado (opcional) e devolve a situacao para candidata. */
    public void setEstadoFinalCurado(EstadoDePosse estadoFinalCurado) {
        this.estadoFinalCurado = estadoFinalCurado;
        voltarParaCandidata();
    }

    public IncognitaQuantitativa getIncognita() {
        return incognita;
    }

    /** Atualiza a incognita (opcional) e devolve a situacao para candidata. */
    public void setIncognita(IncognitaQuantitativa incognita) {
        this.incognita = incognita;
        voltarParaCandidata();
    }

    public RelacaoEstrutural getRelacaoEstrutural() {
        return relacaoEstrutural;
    }

    /** Atualiza a relacao estrutural (opcional) e devolve a situacao para candidata. */
    public void setRelacaoEstrutural(RelacaoEstrutural relacaoEstrutural) {
        this.relacaoEstrutural = relacaoEstrutural;
        voltarParaCandidata();
    }

    public ContextoNarrativo getContexto() {
        return contexto;
    }

    /** Atualiza o contexto narrativo e devolve a situacao para candidata. */
    public void setContexto(ContextoNarrativo contexto) {
        this.contexto = normalizarContexto(contexto);
        voltarParaCandidata();
    }

    public StatusCuradoria getStatus() {
        return status;
    }

    /** Quem revisou (promoveu ou recusou); nulo enquanto candidata. */
    public String getRevisor() {
        return revisor;
    }

    /** Motivo registrado na recusa; nulo nos demais status. */
    public String getMotivoDaRecusa() {
        return motivoDaRecusa;
    }

    /** Texto de exibicao montado a partir dos dados da revisao (nao e armazenado). */
    public String getObservacoesDaRevisao() {
        return switch (status) {
            case VALIDADA -> "revisada por " + revisor;
            case RECUSADA -> "recusada por " + revisor + ": " + motivoDaRecusa;
            default -> null;
        };
    }

    // ------------------------------------------------------------------
    // Validacao estrutural e aritmetica, sem correcao automatica
    // ------------------------------------------------------------------

    /**
     * Percorre a estrutura acumulando diagnosticos.
     *
     * <p>Nenhuma verificacao interrompe as demais: o curador recebe o quadro
     * completo de uma vez. Nenhuma delas altera dado algum.
     */
    public ResultadoValidacao validar() {
        ResultadoValidacao resultado = ResultadoValidacao.semOcorrencias();
        for (AmbiguidadeDeclarada ambiguidade : ambiguidades) {
            resultado = resultado.acrescentar(TipoDiagnostico.ENUNCIADO_AMBIGUO, id,
                    ambiguidade.descrever() + " (nenhuma leitura foi escolhida automaticamente)");
        }
        if (!escopo.permiteModeloAditivo()) {
            // Relata o rotulo e para: nenhuma regra aditiva e aplicada a esta situacao.
            return resultado.acrescentar(TipoDiagnostico.SITUACAO_FORA_DO_ESCOPO_ADITIVO, id,
                    "situacao rotulada pelo curador como " + escopo.getNomeExibido());
        }
        resultado = validarIncognita(resultado);
        resultado = validarReferenciasDoCatalogo(resultado);
        resultado = validarOrdemTemporal(resultado);
        resultado = validarPercursoDasTransformacoes(resultado);
        resultado = validarEstadoFinalCurado(resultado);
        resultado = validarRelacaoEstrutural(resultado);
        return resultado;
    }

    /** A incognita e designacao obrigatoria: sem ela a situacao nao faz pergunta alguma. */
    private ResultadoValidacao validarIncognita(ResultadoValidacao resultado) {
        if (incognita == null) {
            return resultado.acrescentar(
                    TipoDiagnostico.INCOGNITA_ORIGINAL_NAO_DESIGNADA,
                    id,
                    "a situacao nao designou qual papel era originalmente desconhecido");
        }
        return resultado;
    }

    /**
     * Confere que toda referencia usada consta do catalogo curado.
     *
     * <p>Cobre o caso de teste 8 e os diagnosticos de participante e objeto
     * inexistentes.
     */
    private ResultadoValidacao validarReferenciasDoCatalogo(ResultadoValidacao resultado) {
        if (!catalogo.contem(estadoInicial.getPossuidor())) {
            resultado = resultado.acrescentar(
                    TipoDiagnostico.PARTICIPANTE_INEXISTENTE,
                    "estado inicial",
                    "possuidor " + estadoInicial.getPossuidor().getId()
                            + " nao consta do catalogo");
        }
        for (Quantidade item : estadoInicial.getInventario().getItens()) {
            if (!catalogo.contem(item.getObjeto())) {
                resultado = resultado.acrescentar(
                        TipoDiagnostico.OBJETO_CONTADO_INEXISTENTE,
                        "estado inicial",
                        "objeto " + item.getObjeto().getId() + " nao consta do catalogo");
            }
        }
        for (TransformacaoNarrativa transformacao : getTransformacoes()) {
            String elemento = "transformacao " + transformacao.getOrdem();
            if (!catalogo.contem(transformacao.getAtor())) {
                resultado = resultado.acrescentar(
                        TipoDiagnostico.PARTICIPANTE_INEXISTENTE,
                        elemento,
                        "ator " + transformacao.getAtor().getId() + " nao consta do catalogo");
            }
            if (transformacao.possuiDestinatario()
                    && !catalogo.contem(transformacao.getDestinatario())) {
                resultado = resultado.acrescentar(
                        TipoDiagnostico.PARTICIPANTE_INEXISTENTE,
                        elemento,
                        "destinatario " + transformacao.getDestinatario().getId()
                                + " nao consta do catalogo");
            }
            if (transformacao.getAcao().exigeDestinatario()
                    && !transformacao.possuiDestinatario()) {
                resultado = resultado.acrescentar(
                        TipoDiagnostico.TRANSFORMACAO_SEM_DESTINATARIO,
                        elemento,
                        "acao " + transformacao.getAcao().getId() + " transfere posse mas "
                                + "nenhum destinatario foi curado");
            }
            if (!catalogo.contem(transformacao.getQuantidade().getObjeto())) {
                resultado = resultado.acrescentar(
                        TipoDiagnostico.OBJETO_CONTADO_INEXISTENTE,
                        elemento,
                        "objeto " + transformacao.getQuantidade().getObjeto().getId()
                                + " nao consta do catalogo");
            }
        }
        return resultado;
    }

    /**
     * Duas transformacoes nao podem ocupar a mesma posicao temporal.
     *
     * <p>Caso de teste 7. Duas ordens iguais tornariam a sequencia ambigua: nao
     * haveria como saber qual evento aconteceu primeiro, e em situacoes com
     * retirada a ordem pode decidir se a operacao e possivel.
     */
    private ResultadoValidacao validarOrdemTemporal(ResultadoValidacao resultado) {
        List<TransformacaoNarrativa> ordenadas = getTransformacoes();
        for (int i = 0; i < ordenadas.size(); i++) {
            for (int j = i + 1; j < ordenadas.size(); j++) {
                if (ordenadas.get(i).ocupaMesmaOrdemQue(ordenadas.get(j))) {
                    resultado = resultado.acrescentar(
                            TipoDiagnostico.ORDEM_TEMPORAL_REPETIDA,
                            id,
                            "as transformacoes "
                                    + ordenadas.get(i).getAcao().getId() + " e "
                                    + ordenadas.get(j).getAcao().getId()
                                    + " declaram a mesma ordem "
                                    + ordenadas.get(i).getOrdem());
                }
            }
        }
        return resultado;
    }

    /**
     * Simula o percurso relatando retiradas impossiveis.
     *
     * <p>Diferente de {@link #calcularEstadoFinal()}, que propaga a excecao, esta
     * verificacao interrompe a simulacao no primeiro impedimento e registra o
     * diagnostico: as transformacoes seguintes partiriam de um estado que nunca
     * existiu, e relata-las seria ruido.
     */
    private ResultadoValidacao validarPercursoDasTransformacoes(ResultadoValidacao resultado) {
        EstadoDePosse atual = estadoInicial;
        for (TransformacaoNarrativa transformacao : getTransformacoes()) {
            if (!transformacao.getAtor().equals(atual.getPossuidor())) {
                // Transformacao de outro participante nao altera este percurso.
                continue;
            }
            if (!transformacao.podeSerAplicadaSobre(atual)) {
                return resultado.acrescentar(
                        TipoDiagnostico.QUANTIDADE_INDISPONIVEL_PARA_RETIRADA,
                        "transformacao " + transformacao.getOrdem(),
                        transformacao.getAtor().getNomeExibido() + " possui "
                                + atual.getInventario().descrever()
                                + " e a transformacao retira "
                                + transformacao.getQuantidade().descrever());
            }
            atual = transformacao.aplicarSobre(atual);
        }
        return resultado;
    }

    /**
     * Confronta o estado calculado com o estado final curado.
     *
     * <p>Caso de teste 9 e o coracao da responsabilidade humana: quando divergem,
     * o sistema descreve os dois lados e preserva ambos. Nao ha ramo neste codigo
     * que escreva sobre o estado final curado.
     */
    private ResultadoValidacao validarEstadoFinalCurado(ResultadoValidacao resultado) {
        if (estadoFinalCurado == null) {
            return resultado;
        }
        EstadoDePosse calculado;
        try {
            calculado = calcularEstadoFinal();
        } catch (IllegalArgumentException erro) {
            // A retirada impossivel ja foi diagnosticada no percurso.
            return resultado;
        }
        if (!calculado.getInventario().possuiMesmoConteudoQue(estadoFinalCurado.getInventario())) {
            resultado = resultado.acrescentar(
                    TipoDiagnostico.ESTADO_CALCULADO_DIVERGENTE,
                    "estado final curado",
                    "calculado [" + calculado.getInventario().descrever()
                            + "], curado [" + estadoFinalCurado.getInventario().descrever()
                            + "]; nenhum dos dois foi alterado");
        }
        return resultado;
    }

    /** Delega a verificacao dos papeis a relacao estrutural, que os coordena. */
    private ResultadoValidacao validarRelacaoEstrutural(ResultadoValidacao resultado) {
        if (relacaoEstrutural == null) {
            return resultado;
        }
        return resultado.combinarCom(relacaoEstrutural.validar());
    }

    // ------------------------------------------------------------------
    // Calculo do percurso
    // ------------------------------------------------------------------

    /**
     * Aplica as transformacoes em ordem e devolve o estado resultante.
     *
     * <p>Nao altera o estado inicial: cada aplicacao produz um novo estado, e o
     * original continua disponivel para a sequencia narrativa.
     *
     * @throws IllegalArgumentException quando uma retirada e impossivel.
     */
    public EstadoDePosse calcularEstadoFinal() {
        exigirEscopoAditivo();
        EstadoDePosse atual = estadoInicial;
        for (TransformacaoNarrativa transformacao : getTransformacoes()) {
            if (transformacao.getAtor().equals(atual.getPossuidor())) {
                atual = transformacao.aplicarSobre(atual);
            }
        }
        return atual;
    }

    /**
     * Todos os estados intermediarios do percurso, do inicial ao final.
     *
     * <p>Alimenta a sequencia narrativa e permite demonstrar, na apresentacao,
     * que nenhum estado anterior foi corrompido.
     */
    public List<EstadoDePosse> calcularPercursoDeEstados() {
        exigirEscopoAditivo();
        List<EstadoDePosse> percurso = new ArrayList<>();
        percurso.add(estadoInicial);
        EstadoDePosse atual = estadoInicial;
        for (TransformacaoNarrativa transformacao : getTransformacoes()) {
            if (!transformacao.getAtor().equals(atual.getPossuidor())) {
                continue;
            }
            atual = transformacao.aplicarSobre(atual);
            percurso.add(atual);
        }
        return percurso;
    }

    // ------------------------------------------------------------------
    // Projecao narrativa
    // ------------------------------------------------------------------

    /**
     * Produz a sequencia semantica de estados, eventos e pergunta.
     *
     * <p>A projecao segue o formato da especificacao: o estado inicial, cada
     * transformacao como evento na ordem curada, o estado final e a pergunta.
     * Exibe o estado final <strong>curado</strong> quando existe, porque a
     * historia e a que o curador contou; a divergencia com o calculo, se houver,
     * ja foi relatada pela validacao.
     *
     * <p>Estados intermediarios nao viram episodios: a sequencia esperada mostra
     * estado inicial, os eventos e o estado final. Um renderizador que queira
     * exibir os intermediarios pode obte-los por calcularPercursoDeEstados().
     */
    public SequenciaNarrativa projetarSequenciaNarrativa() {
        exigirEscopoAditivo();
        List<TransformacaoNarrativa> ordenadas = getTransformacoes();
        List<EpisodioNarrativo> episodios = new ArrayList<>();
        episodios.add(EpisodioNarrativo.deEstado(estadoInicial));
        for (TransformacaoNarrativa transformacao : ordenadas) {
            episodios.add(EpisodioNarrativo.deEvento(transformacao));
        }
        EstadoDePosse estadoDeFecho = estadoFinalCurado;
        if (estadoDeFecho == null) {
            try {
                estadoDeFecho = calcularEstadoFinal();
            } catch (IllegalArgumentException erro) {
                estadoDeFecho = null;
            }
        }
        if (estadoDeFecho != null) {
            episodios.add(EpisodioNarrativo.deEstado(estadoDeFecho));
        }
        if (incognita != null) {
            MarcadorTemporal momentoDaPergunta = estadoDeFecho != null
                    ? estadoDeFecho.getMomento()
                    : MarcadorTemporal.daOrdem(ordenadas.size() + 1);
            episodios.add(EpisodioNarrativo.dePergunta(incognita, momentoDaPergunta));
        }
        return new SequenciaNarrativa(id, episodios, contexto);
    }

    // ------------------------------------------------------------------
    // Curadoria humana
    // ------------------------------------------------------------------

    /**
     * Promove a situacao ao catalogo validado.
     *
     * <p>Exige a identificacao de quem revisou, justamente para que a promocao
     * seja um ato humano rastreavel. Nenhum outro metodo desta classe chama este:
     * validar() com sucesso nao promove nada.
     */
    public void promoverParaValidada(String revisor) {
        if (revisor == null || revisor.isBlank()) {
            throw new IllegalArgumentException(
                    "a promocao ao catalogo validado exige a identificacao do revisor humano");
        }
        if (!validar().ehValido()) {
            throw new IllegalStateException(
                    "situacao com erros de validacao nao pode ser promovida: " + id);
        }
        if (!ambiguidades.isEmpty()) {
            throw new IllegalStateException(
                    "situacao com ambiguidade pendente nao pode ser promovida: " + id);
        }
        this.status = StatusCuradoria.VALIDADA;
        this.revisor = revisor;
        this.motivoDaRecusa = null;
    }

    /** Registra a recusa com o motivo; a situacao continua existindo. */
    public void recusar(String revisor, String motivo) {
        if (revisor == null || revisor.isBlank()) {
            throw new IllegalArgumentException("a recusa exige a identificacao do revisor humano");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("a recusa exige motivo registrado");
        }
        this.status = StatusCuradoria.RECUSADA;
        this.revisor = revisor;
        this.motivoDaRecusa = motivo;
    }

    /**
     * Restaura o status para candidata nao curada, pois conteudo alterado
     * depois da revisao precisa de nova revisao humana.
     */
    private void voltarParaCandidata() {
        this.status = StatusCuradoria.CANDIDATA_NAO_CURADA;
        this.revisor = null;
        this.motivoDaRecusa = null;
    }

    private void exigirEscopoAditivo() {
        if (!escopo.permiteModeloAditivo()) {
            throw new IllegalStateException(
                    "situacao fora do escopo aditivo nao e calculada nem narrada: " + id);
        }
    }

    public EscopoDaSituacao getEscopo() {
        return escopo;
    }

    /** Atualiza o escopo da situacao e a devolve para candidata. */
    public void setEscopo(EscopoDaSituacao escopo) {
        EscopoDaSituacao novoEscopo = normalizarEscopo(escopo);
        validarEstadoInicial(this.estadoInicial, novoEscopo);
        this.escopo = novoEscopo;
        voltarParaCandidata();
    }

    /** Devolve visao nao modificavel das ambiguidades declaradas. */
    public List<AmbiguidadeDeclarada> getAmbiguidades() {
        return Collections.unmodifiableList(ambiguidades);
    }

    /** Registra uma ambiguidade no enunciado e devolve a situacao para candidata. */
    public void adicionarAmbiguidade(String trecho, String motivo) {
        this.ambiguidades.add(new AmbiguidadeDeclarada(trecho, motivo));
        voltarParaCandidata();
    }

    /** Remove uma ambiguidade existente da situacao e a devolve para candidata. */
    public void removerAmbiguidade(AmbiguidadeDeclarada ambiguidade) {
        if (ambiguidade == null || !this.ambiguidades.remove(ambiguidade)) {
            throw new IllegalArgumentException("ambiguidade nao pertence a situacao");
        }
        voltarParaCandidata();
    }

    /** Relatorio textual da situacao com sua validacao, para o console. */
    public String descreverComValidacao() {
        StringBuilder texto = new StringBuilder();
        texto.append("Situacao: ").append(id).append(System.lineSeparator());
        texto.append("Enunciado: ").append(enunciado).append(System.lineSeparator());
        texto.append("Status: ").append(status.getNomeExibido()).append(System.lineSeparator());
        texto.append(System.lineSeparator());
        texto.append(projetarSequenciaNarrativa().descrever()).append(System.lineSeparator());
        texto.append(System.lineSeparator());
        ResultadoValidacao resultado = validar();
        texto.append("Validacao: ")
                .append(resultado.ehValido() ? "sem erros" : "com erros")
                .append(System.lineSeparator());
        for (Diagnostico diagnostico : resultado.getDiagnosticos()) {
            texto.append("  ").append(diagnostico.descrever()).append(System.lineSeparator());
        }
        return texto.toString();
    }

    @Override
    public String toString() {
        return id + " (" + status.getNomeExibido() + ")";
    }

    /**
     * Montagem passo a passo da situacao.
     *
     * <p>Necessario porque uma situacao tem muitas partes opcionais: nem toda
     * situacao tem estado final curado, relacao estrutural declarada ou contexto.
     * Um construtor com onze parametros posicionais convidaria exatamente ao erro
     * que o projeto combate, o de associar dados pela posicao.
     */
    public static final class Construtor {

        private final String id;
        private final String enunciado;
        private CatalogoDaSituacao catalogo;
        private EstadoDePosse estadoInicial;
        private final List<TransformacaoNarrativa> transformacoes = new ArrayList<>();
        private EstadoDePosse estadoFinalCurado;
        private IncognitaQuantitativa incognita;
        private RelacaoEstrutural relacaoEstrutural;
        private ContextoNarrativo contexto;
        private EscopoDaSituacao escopo = EscopoDaSituacao.ADITIVO;
        private final List<AmbiguidadeDeclarada> ambiguidades = new ArrayList<>();

        private Construtor(String id, String enunciado) {
            this.id = id;
            this.enunciado = enunciado;
        }

        public Construtor comCatalogo(CatalogoDaSituacao catalogo) {
            this.catalogo = catalogo;
            return this;
        }

        public Construtor comEstadoInicial(EstadoDePosse estadoInicial) {
            this.estadoInicial = estadoInicial;
            return this;
        }

        public Construtor comTransformacao(TransformacaoNarrativa transformacao) {
            if (transformacao != null) {
                transformacoes.add(transformacao);
            }
            return this;
        }

        public Construtor comEstadoFinalCurado(EstadoDePosse estadoFinalCurado) {
            this.estadoFinalCurado = estadoFinalCurado;
            return this;
        }

        public Construtor comIncognita(IncognitaQuantitativa incognita) {
            this.incognita = incognita;
            return this;
        }

        public Construtor comRelacaoEstrutural(RelacaoEstrutural relacaoEstrutural) {
            this.relacaoEstrutural = relacaoEstrutural;
            return this;
        }

        public Construtor comContexto(ContextoNarrativo contexto) {
            this.contexto = contexto;
            return this;
        }

        /** Rotula a situacao como fora do escopo aditivo (ex.: multiplicativa). */
        public Construtor rotuladaForaDoEscopo() {
            this.escopo = EscopoDaSituacao.FORA_DO_ESCOPO_ADITIVO;
            return this;
        }

        /** Registra um trecho ambiguo do enunciado, sem escolher leitura. */
        public Construtor comAmbiguidade(String trecho, String motivo) {
            ambiguidades.add(new AmbiguidadeDeclarada(trecho, motivo));
            return this;
        }

        /** Constroi a situacao sempre como candidata: a curadoria e ato posterior. */
        public SituacaoProblema construir() {
            return new SituacaoProblema(id, enunciado, catalogo, estadoInicial, transformacoes,
                    estadoFinalCurado, incognita, relacaoEstrutural, contexto,
                    StatusCuradoria.CANDIDATA_NAO_CURADA, null, null, escopo, ambiguidades);
        }
    }
}
