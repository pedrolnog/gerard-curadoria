package br.edu.univasf.gerard_curador.interfacegrafica;

import br.edu.univasf.gerard_curador.dominio.narrativo.AcaoNarrativa;
import br.edu.univasf.gerard_curador.dominio.narrativo.ContextoNarrativo;
import br.edu.univasf.gerard_curador.dominio.narrativo.EstadoDePosse;
import br.edu.univasf.gerard_curador.dominio.narrativo.MarcadorTemporal;
import br.edu.univasf.gerard_curador.dominio.narrativo.ParticipanteNarrativo;
import br.edu.univasf.gerard_curador.dominio.narrativo.ReferenciaVisualParticipante;
import br.edu.univasf.gerard_curador.dominio.narrativo.SentidoTransformacao;
import br.edu.univasf.gerard_curador.dominio.narrativo.TransformacaoNarrativa;
import br.edu.univasf.gerard_curador.dominio.quantitativo.CaracteristicaObjeto;
import br.edu.univasf.gerard_curador.dominio.quantitativo.FamiliaObjeto;
import br.edu.univasf.gerard_curador.dominio.narrativo.Inventario;
import br.edu.univasf.gerard_curador.dominio.quantitativo.ObjetoContado;
import br.edu.univasf.gerard_curador.dominio.quantitativo.Quantidade;
import br.edu.univasf.gerard_curador.dominio.quantitativo.RealizacaoTextualObjeto;
import br.edu.univasf.gerard_curador.dominio.quantitativo.ReferenciaVisual;
import br.edu.univasf.gerard_curador.dominio.estrutural.IncognitaQuantitativa;
import br.edu.univasf.gerard_curador.dominio.estrutural.PapelQuantitativo;
import br.edu.univasf.gerard_curador.dominio.situacao.CatalogoDaSituacao;
import br.edu.univasf.gerard_curador.dominio.situacao.SituacaoProblema;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Rascunho editavel de uma situacao em curadoria.
 *
 * <p>Existe porque os objetos do dominio sao imutaveis, e uma tela precisa de
 * um estado que muda enquanto o curador digita. O rascunho acumula os dados
 * conforme sao informados e so entao {@link #montarSituacao()} constroi os
 * objetos definitivos.
 *
 * <p>Fronteira importante: este objeto <strong>nao valida regra de dominio
 * alguma</strong>. Ele acumula o que o curador informou e delega a construcao ao
 * dominio; se um dado for incoerente, quem recusa e o construtor da classe
 * responsavel, e a excecao chega a tela como diagnostico. O rascunho tambem nao
 * calcula totais, nao deduz papeis e nao corrige valores.
 *
 * <p>Pertence a camada de interface, nao ao dominio: nenhuma classe de
 * {@code curador.dominio} importa este pacote.
 */
public final class RascunhoDeSituacao {

    private String id = "";
    private String enunciado = "";
    private String local = "";
    private String situacaoContexto = "";
    private String momento = "";

    private final Map<String, FamiliaObjeto> familias = new LinkedHashMap<>();
    private final Map<String, ParticipanteNarrativo> participantes = new LinkedHashMap<>();
    private final Map<String, ObjetoContado> objetos = new LinkedHashMap<>();

    /** Estado inicial: possuidor e quantidades por objeto contado. */
    private String idPossuidorInicial;
    private final Map<String, Integer> itensIniciais = new LinkedHashMap<>();

    /** Estado final curado: opcional, informado pelo curador. */
    private String idPossuidorFinal;
    private final Map<String, Integer> itensFinais = new LinkedHashMap<>();

    private final List<TransformacaoRascunhada> transformacoes = new ArrayList<>();

    private PapelQuantitativo papelDaIncognita;
    private String perguntaDaIncognita = "";

    // ------------------------------------------------------------------
    // Identificacao e contexto
    // ------------------------------------------------------------------

    public void setId(String id) {
        this.id = id == null ? "" : id.trim();
    }

    public String getId() {
        return id;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado == null ? "" : enunciado.trim();
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setContexto(String local, String situacao, String momento) {
        this.local = local == null ? "" : local.trim();
        this.situacaoContexto = situacao == null ? "" : situacao.trim();
        this.momento = momento == null ? "" : momento.trim();
    }

    public String getLocal() {
        return local;
    }

    public String getSituacaoContexto() {
        return situacaoContexto;
    }

    public String getMomento() {
        return momento;
    }

    // ------------------------------------------------------------------
    // Catalogo
    // ------------------------------------------------------------------

    /**
     * Cadastra um participante.
     *
     * <p>Sem papel algum associado: ser ator ou destinatario e posicao ocupada
     * numa transformacao, nao propriedade da pessoa.
     */
    public void adicionarParticipante(String idParticipante, String nome, String chaveVisual) {
        ParticipanteNarrativo participante = new ParticipanteNarrativo(
                idParticipante,
                nome,
                chaveVisual == null || chaveVisual.isBlank()
                        ? ReferenciaVisualParticipante.naoDefinida()
                        : ReferenciaVisualParticipante.de(chaveVisual));
        if (participantes.containsKey(participante.getId())) {
            throw new IllegalArgumentException(
                    "participante ja cadastrado: " + participante.getId());
        }
        participantes.put(participante.getId(), participante);
    }

    public void removerParticipante(String idParticipante) {
        participantes.remove(idParticipante);
        transformacoes.removeIf(transformacao ->
                idParticipante.equals(transformacao.idAtor)
                        || idParticipante.equals(transformacao.idDestinatario));
        if (idParticipante.equals(idPossuidorInicial)) {
            idPossuidorInicial = null;
        }
        if (idParticipante.equals(idPossuidorFinal)) {
            idPossuidorFinal = null;
        }
    }

    public List<ParticipanteNarrativo> getParticipantes() {
        return new ArrayList<>(participantes.values());
    }

    public ParticipanteNarrativo participantePorId(String idParticipante) {
        return participantes.get(idParticipante);
    }

    /**
     * Cadastra um objeto contado com sua familia e caracteristicas.
     *
     * <p>A familia e reutilizada quando ja existe: e assim que rosa branca e rosa
     * amarela acabam na mesma familia rosa, permitindo a totalizacao.
     */
    public void adicionarObjeto(String idObjeto,
                                String idFamilia,
                                String nomeFamilia,
                                Map<String, String> caracteristicasInformadas,
                                String singular,
                                String plural,
                                String chaveVisual) {
        FamiliaObjeto familia = familias.get(idFamilia);
        if (familia == null) {
            familia = new FamiliaObjeto(
                    idFamilia,
                    nomeFamilia == null || nomeFamilia.isBlank() ? idFamilia : nomeFamilia);
            familias.put(idFamilia, familia);
        }
        Set<CaracteristicaObjeto> caracteristicas = new LinkedHashSet<>();
        if (caracteristicasInformadas != null) {
            for (Map.Entry<String, String> entrada : caracteristicasInformadas.entrySet()) {
                caracteristicas.add(
                        CaracteristicaObjeto.de(entrada.getKey(), entrada.getValue()));
            }
        }
        ObjetoContado objeto = new ObjetoContado(
                idObjeto,
                familia,
                caracteristicas,
                RealizacaoTextualObjeto.emPortugues(singular, plural),
                chaveVisual == null || chaveVisual.isBlank()
                        ? ReferenciaVisual.naoDefinida()
                        : ReferenciaVisual.de(chaveVisual));
        if (objetos.containsKey(objeto.getId())) {
            throw new IllegalArgumentException("objeto ja cadastrado: " + objeto.getId());
        }
        objetos.put(objeto.getId(), objeto);
    }

    public void removerObjeto(String idObjeto) {
        objetos.remove(idObjeto);
        itensIniciais.remove(idObjeto);
        itensFinais.remove(idObjeto);
        transformacoes.removeIf(transformacao -> idObjeto.equals(transformacao.idObjeto));
    }

    public List<ObjetoContado> getObjetos() {
        return new ArrayList<>(objetos.values());
    }

    public ObjetoContado objetoPorId(String idObjeto) {
        return objetos.get(idObjeto);
    }

    public List<FamiliaObjeto> getFamilias() {
        return new ArrayList<>(familias.values());
    }

    // ------------------------------------------------------------------
    // Estados
    // ------------------------------------------------------------------

    public void setPossuidorInicial(String idPossuidor) {
        this.idPossuidorInicial = idPossuidor;
    }

    public String getIdPossuidorInicial() {
        return idPossuidorInicial;
    }

    public void definirItemInicial(String idObjeto, int quantidade) {
        itensIniciais.put(idObjeto, quantidade);
    }

    public void removerItemInicial(String idObjeto) {
        itensIniciais.remove(idObjeto);
    }

    public Map<String, Integer> getItensIniciais() {
        return new LinkedHashMap<>(itensIniciais);
    }

    public void setPossuidorFinal(String idPossuidor) {
        this.idPossuidorFinal = idPossuidor;
    }

    public String getIdPossuidorFinal() {
        return idPossuidorFinal;
    }

    public void definirItemFinal(String idObjeto, int quantidade) {
        itensFinais.put(idObjeto, quantidade);
    }

    public void removerItemFinal(String idObjeto) {
        itensFinais.remove(idObjeto);
    }

    public Map<String, Integer> getItensFinais() {
        return new LinkedHashMap<>(itensFinais);
    }

    public boolean possuiEstadoFinalCurado() {
        return idPossuidorFinal != null && !itensFinais.isEmpty();
    }

    // ------------------------------------------------------------------
    // Transformacoes
    // ------------------------------------------------------------------

    public void adicionarTransformacao(TransformacaoRascunhada transformacao) {
        transformacoes.add(transformacao);
    }

    public void removerTransformacao(int indice) {
        if (indice >= 0 && indice < transformacoes.size()) {
            transformacoes.remove(indice);
        }
    }

    public List<TransformacaoRascunhada> getTransformacoes() {
        return new ArrayList<>(transformacoes);
    }

    // ------------------------------------------------------------------
    // Incognita
    // ------------------------------------------------------------------

    public void setIncognita(PapelQuantitativo papel, String pergunta) {
        this.papelDaIncognita = papel;
        this.perguntaDaIncognita = pergunta == null ? "" : pergunta.trim();
    }

    public PapelQuantitativo getPapelDaIncognita() {
        return papelDaIncognita;
    }

    public String getPerguntaDaIncognita() {
        return perguntaDaIncognita;
    }

    // ------------------------------------------------------------------
    // Substituicao do conteudo
    // ------------------------------------------------------------------

    /**
     * Descarta o conteudo atual e assume o do rascunho informado.
     *
     * <p>Usado quando o interpretador produz uma proposta a partir do enunciado:
     * a tela precisa trocar tudo de uma vez, mantendo a mesma instancia de
     * rascunho a que os paineis ja estao ligados.
     *
     * <p>Copia estrutura, nao referencia: alterar o rascunho de origem depois
     * disso nao afeta este. As colecoes internas sao recriadas, e os objetos de
     * dominio compartilhados sao imutaveis.
     */
    public void substituirPor(RascunhoDeSituacao origem) {
        if (origem == null) {
            throw new IllegalArgumentException("a substituicao exige um rascunho de origem");
        }
        limpar();

        this.id = origem.id;
        this.enunciado = origem.enunciado;
        this.local = origem.local;
        this.situacaoContexto = origem.situacaoContexto;
        this.momento = origem.momento;

        this.familias.putAll(origem.familias);
        this.participantes.putAll(origem.participantes);
        this.objetos.putAll(origem.objetos);

        this.idPossuidorInicial = origem.idPossuidorInicial;
        this.itensIniciais.putAll(origem.itensIniciais);
        this.idPossuidorFinal = origem.idPossuidorFinal;
        this.itensFinais.putAll(origem.itensFinais);

        this.transformacoes.addAll(origem.transformacoes);

        this.papelDaIncognita = origem.papelDaIncognita;
        this.perguntaDaIncognita = origem.perguntaDaIncognita;
    }

    /**
     * Cria um rascunho preenchido a partir de uma situacao de dominio existente.
     *
     * <p>Permite editar uma situacao carregada do repositorio mantendo o modelo de
     * dominio imutavel: nenhuma regra e recalculada durante a copia.
     */
    public static RascunhoDeSituacao aPartirDe(SituacaoProblema situacao) {
        if (situacao == null) {
            throw new IllegalArgumentException("situacao nao pode ser nula");
        }
        RascunhoDeSituacao rascunho = new RascunhoDeSituacao();
        rascunho.setId(situacao.getId());
        rascunho.setEnunciado(situacao.getEnunciado());

        ContextoNarrativo contexto = situacao.getContexto();
        if (contexto != null) {
            rascunho.setContexto(
                    contexto.getLocal(),
                    contexto.getSituacao(),
                    contexto.getMomento());
        }

        CatalogoDaSituacao catalogo = situacao.getCatalogo();
        if (catalogo != null) {
            for (ParticipanteNarrativo participante : catalogo.getParticipantes()) {
                ReferenciaVisualParticipante ref = participante.getReferenciaVisual();
                String chaveVisual = ref != null && ref.estaDefinida() ? ref.getChave() : null;
                rascunho.adicionarParticipante(
                        participante.getId(),
                        participante.getNomeExibido(),
                        chaveVisual);
            }
            for (ObjetoContado objeto : catalogo.getObjetos()) {
                Map<String, String> caracteristicas = new LinkedHashMap<>();
                for (CaracteristicaObjeto carac : objeto.getCaracteristicas()) {
                    caracteristicas.put(carac.getDimensao(), carac.getValor());
                }
                ReferenciaVisual ref = objeto.getReferenciaVisual();
                String chaveVisual = ref != null && ref.estaDefinida() ? ref.getChave() : null;
                rascunho.adicionarObjeto(
                        objeto.getId(),
                        objeto.getFamilia().getId(),
                        objeto.getFamilia().getNomeConceitual(),
                        caracteristicas,
                        objeto.getRealizacaoTextual().getSingular(),
                        objeto.getRealizacaoTextual().getPlural(),
                        chaveVisual);
            }
        }

        EstadoDePosse estadoInicial = situacao.getEstadoInicial();
        if (estadoInicial != null) {
            rascunho.setPossuidorInicial(estadoInicial.getPossuidor().getId());
            for (Quantidade item : estadoInicial.getInventario().getItens()) {
                rascunho.definirItemInicial(item.getObjeto().getId(), item.getValor());
            }
        }

        for (TransformacaoNarrativa transformacao : situacao.getTransformacoes()) {
            AcaoNarrativa acao = transformacao.getAcao();
            rascunho.adicionarTransformacao(new TransformacaoRascunhada(
                    transformacao.getOrdem(),
                    transformacao.getMarcador().getRotulo(),
                    transformacao.getAtor().getId(),
                    transformacao.getDestinatario() != null
                            ? transformacao.getDestinatario().getId() : null,
                    transformacao.getQuantidade().getObjeto().getId(),
                    transformacao.getQuantidade().getValor(),
                    acao.getId(),
                    acao.getVerbo(),
                    acao.getParticipio(),
                    acao.getSentido(),
                    acao.exigeDestinatario()));
        }

        EstadoDePosse estadoFinal = situacao.getEstadoFinalCurado();
        if (estadoFinal != null) {
            rascunho.setPossuidorFinal(estadoFinal.getPossuidor().getId());
            for (Quantidade item : estadoFinal.getInventario().getItens()) {
                rascunho.definirItemFinal(item.getObjeto().getId(), item.getValor());
            }
        }

        IncognitaQuantitativa incognita = situacao.getIncognita();
        if (incognita != null) {
            rascunho.setIncognita(
                    incognita.getPapelOriginal(),
                    incognita.getDescricaoDaPergunta());
        }
        return rascunho;
    }

    /** Devolve o rascunho ao estado inicial, sem nada informado. */
    public void limpar() {
        id = "";
        enunciado = "";
        local = "";
        situacaoContexto = "";
        momento = "";
        familias.clear();
        participantes.clear();
        objetos.clear();
        idPossuidorInicial = null;
        itensIniciais.clear();
        idPossuidorFinal = null;
        itensFinais.clear();
        transformacoes.clear();
        papelDaIncognita = null;
        perguntaDaIncognita = "";
    }

    // ------------------------------------------------------------------
    // Construcao dos objetos de dominio
    // ------------------------------------------------------------------

    /**
     * Monta a SituacaoProblema a partir do que foi informado.
     *
     * <p>Todo dado vira objeto de dominio aqui, e as invariantes sao verificadas
     * pelos construtores das classes responsaveis. Qualquer incoerencia sobe como
     * excecao, e a tela a exibe sem interpretar.
     *
     * <p>Note o que este metodo <strong>nao</strong> faz: nao calcula o estado
     * final, nao infere a incognita pela ausencia de valor, nao ordena as
     * transformacoes por ordem de digitacao. A ordem vem do marcador que o
     * curador informou.
     */
    public SituacaoProblema montarSituacao() {
        CatalogoDaSituacao.Construtor catalogo = CatalogoDaSituacao.construtor();
        for (ParticipanteNarrativo participante : participantes.values()) {
            catalogo.comParticipante(participante);
        }
        for (ObjetoContado objeto : objetos.values()) {
            catalogo.comObjeto(objeto);
        }

        ParticipanteNarrativo possuidorInicial = exigirParticipante(
                idPossuidorInicial, "estado inicial");
        EstadoDePosse estadoInicial = new EstadoDePosse(
                possuidorInicial,
                montarInventario(itensIniciais, "estado inicial"),
                MarcadorTemporal.de(1, "no comeco"));

        SituacaoProblema.Construtor construtor =
                SituacaoProblema.construtor(id, enunciado)
                        .comCatalogo(catalogo.construir())
                        .comEstadoInicial(estadoInicial)
                        .comContexto(new ContextoNarrativo(
                                vazioComoNulo(local),
                                vazioComoNulo(situacaoContexto),
                                vazioComoNulo(momento)));

        for (TransformacaoRascunhada rascunho : transformacoes) {
            construtor.comTransformacao(montarTransformacao(rascunho));
        }

        if (possuiEstadoFinalCurado()) {
            ParticipanteNarrativo possuidorFinal = exigirParticipante(
                    idPossuidorFinal, "estado final curado");
            construtor.comEstadoFinalCurado(new EstadoDePosse(
                    possuidorFinal,
                    montarInventario(itensFinais, "estado final curado"),
                    MarcadorTemporal.de(proximaOrdemLivre(), "no fim")));
        }

        if (papelDaIncognita != null) {
            construtor.comIncognita(IncognitaQuantitativa.designar(
                    papelDaIncognita, perguntaDaIncognita));
        }
        return construtor.construir();
    }

    private TransformacaoNarrativa montarTransformacao(TransformacaoRascunhada rascunho) {
        String elemento = "transformacao de ordem " + rascunho.ordem;
        ParticipanteNarrativo ator = exigirParticipante(rascunho.idAtor, elemento + " (ator)");
        ParticipanteNarrativo destinatario = rascunho.idDestinatario == null
                ? null
                : exigirParticipante(rascunho.idDestinatario, elemento + " (destinatario)");

        ObjetoContado objeto = objetos.get(rascunho.idObjeto);
        if (objeto == null) {
            throw new IllegalArgumentException(
                    elemento + ": objeto contado nao cadastrado: " + rascunho.idObjeto);
        }
        AcaoNarrativa acao = rascunho.exigeDestinatario
                ? AcaoNarrativa.transferencia(
                        rascunho.idAcao, rascunho.verbo, rascunho.participio, rascunho.sentido)
                : AcaoNarrativa.semDestinatario(
                        rascunho.idAcao, rascunho.verbo, rascunho.participio, rascunho.sentido);

        return new TransformacaoNarrativa(
                ator,
                acao,
                destinatario,
                objeto.contar(rascunho.quantidade),
                MarcadorTemporal.de(rascunho.ordem, rascunho.rotuloTemporal));
    }

    private Inventario montarInventario(Map<String, Integer> itens, String elemento) {
        Inventario inventario = Inventario.vazio();
        for (Map.Entry<String, Integer> entrada : itens.entrySet()) {
            ObjetoContado objeto = objetos.get(entrada.getKey());
            if (objeto == null) {
                throw new IllegalArgumentException(
                        elemento + ": objeto contado nao cadastrado: " + entrada.getKey());
            }
            Quantidade quantidade = objeto.contar(entrada.getValue());
            inventario = inventario.adicionar(quantidade);
        }
        return inventario;
    }

    private ParticipanteNarrativo exigirParticipante(String idParticipante, String elemento) {
        if (idParticipante == null) {
            throw new IllegalArgumentException(elemento + ": possuidor nao informado");
        }
        ParticipanteNarrativo participante = participantes.get(idParticipante);
        if (participante == null) {
            throw new IllegalArgumentException(
                    elemento + ": participante nao cadastrado: " + idParticipante);
        }
        return participante;
    }

    /** Ordem seguinte a maior ordem curada, para situar o estado final no tempo. */
    private int proximaOrdemLivre() {
        int maior = 1;
        for (TransformacaoRascunhada transformacao : transformacoes) {
            maior = Math.max(maior, transformacao.ordem);
        }
        return maior + 1;
    }

    private static String vazioComoNulo(String valor) {
        return valor == null || valor.isBlank() ? null : valor;
    }

    /**
     * Dados de uma transformacao enquanto o curador a edita.
     *
     * <p>Guarda ids em vez de objetos porque um participante pode ser removido do
     * catalogo enquanto a transformacao ainda esta na lista; a resolucao acontece
     * apenas na montagem, quando o conjunto ja esta estavel.
     */
    public static final class TransformacaoRascunhada {

        public final int ordem;
        public final String rotuloTemporal;
        public final String idAtor;
        public final String idDestinatario;
        public final String idObjeto;
        public final int quantidade;
        public final String idAcao;
        public final String verbo;
        public final String participio;
        public final SentidoTransformacao sentido;
        public final boolean exigeDestinatario;

        public TransformacaoRascunhada(int ordem,
                                       String rotuloTemporal,
                                       String idAtor,
                                       String idDestinatario,
                                       String idObjeto,
                                       int quantidade,
                                       String idAcao,
                                       String verbo,
                                       String participio,
                                       SentidoTransformacao sentido,
                                       boolean exigeDestinatario) {
            this.ordem = ordem;
            this.rotuloTemporal = rotuloTemporal;
            this.idAtor = idAtor;
            this.idDestinatario = idDestinatario;
            this.idObjeto = idObjeto;
            this.quantidade = quantidade;
            this.idAcao = idAcao;
            this.verbo = verbo;
            this.participio = participio;
            this.sentido = sentido;
            this.exigeDestinatario = exigeDestinatario;
        }

        /** Descricao para a lista da tela, sem consultar o dominio. */
        public String descrever() {
            StringBuilder texto = new StringBuilder();
            texto.append(ordem).append(". [").append(rotuloTemporal).append("] ")
                    .append(idAtor).append(" ").append(verbo).append(" ")
                    .append(quantidade).append(" ").append(idObjeto);
            if (idDestinatario != null) {
                texto.append(" -> ").append(idDestinatario);
            }
            texto.append(" (").append(sentido.getNomeExibido()).append(")");
            return texto.toString();
        }
    }
}
