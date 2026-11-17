package br.edu.univasf.gerard_curador.dominio.validacao;

/**
 * Catalogo fechado dos diagnosticos que o dominio sabe emitir.
 *
 * <p>Corresponde a lista "diagnosticos estruturados sugeridos" da
 * especificacao. Ser uma enumeracao, e nao uma String livre, e o que torna o
 * diagnostico <em>estruturado</em>: o chamador pode reagir programaticamente a
 * um tipo especifico, e os testes podem afirmar que a violacao detectada foi
 * exatamente aquela esperada, sem comparar mensagens de texto.
 *
 * <p>Cada tipo carrega sua severidade natural. A distincao importa porque nem
 * todo diagnostico impede a situacao de existir: uma divergencia entre o estado
 * calculado e o curado e um ERRO que o curador precisa resolver, enquanto uma
 * referencia visual ausente e um AVISO que nao invalida a curadoria.
 */
public enum TipoDiagnostico {

    PARTICIPANTE_INEXISTENTE(
            "participante inexistente", Severidade.ERRO),

    OBJETO_CONTADO_INEXISTENTE(
            "objeto contado inexistente", Severidade.ERRO),

    TRANSFORMACAO_SEM_ATOR(
            "transformacao sem ator", Severidade.ERRO),

    TRANSFORMACAO_SEM_DESTINATARIO(
            "transformacao sem destinatario", Severidade.ERRO),

    TRANSFORMACAO_SEM_OBJETO(
            "transformacao sem objeto", Severidade.ERRO),

    ORDEM_TEMPORAL_REPETIDA(
            "ordem temporal repetida", Severidade.ERRO),

    QUANTIDADE_INDISPONIVEL_PARA_RETIRADA(
            "quantidade indisponivel para retirada", Severidade.ERRO),

    GRANDEZAS_OU_UNIDADES_INCOMPATIVEIS(
            "grandezas ou unidades incompativeis", Severidade.ERRO),

    INCOGNITA_ORIGINAL_NAO_DESIGNADA(
            "incognita original nao designada", Severidade.ERRO),

    /**
     * O caso central da responsabilidade humana: o sistema aponta, mas nao
     * sobrescreve o valor informado pelo curador.
     */
    ESTADO_CALCULADO_DIVERGENTE(
            "estado calculado divergente do estado final curado", Severidade.ERRO),

    QUANTIDADE_POR_DESTINATARIO_AMBIGUA(
            "quantidade por destinatario ambigua", Severidade.AVISO),

    PARTICIPANTE_INCLUIDO_NO_TOTAL_NAO_DEFINIDO(
            "participante incluido no total nao definido", Severidade.AVISO),

    PAPEL_DESCONHECIDO_NAO_INFORMADO(
            "papel desconhecido nao informado", Severidade.ERRO),

    /** Situacao multiplicativa que nao deve ser forcada ao modelo aditivo. */
    SITUACAO_FORA_DO_ESCOPO_ADITIVO(
            "situacao fora do escopo aditivo", Severidade.ERRO),

    /** Enunciado ambiguo: relatado, nunca resolvido por associacao automatica. */
    ENUNCIADO_AMBIGUO(
            "enunciado ambiguo", Severidade.AVISO),

    REFERENCIA_VISUAL_AUSENTE(
            "referencia visual ausente", Severidade.AVISO);

    /** Gravidade do diagnostico: erro impede a curadoria, aviso apenas alerta. */
    public enum Severidade {
        ERRO,
        AVISO
    }

    private final String nomeExibido;
    private final Severidade severidade;

    TipoDiagnostico(String nomeExibido, Severidade severidade) {
        this.nomeExibido = nomeExibido;
        this.severidade = severidade;
    }

    public String getNomeExibido() {
        return nomeExibido;
    }

    public Severidade getSeveridade() {
        return severidade;
    }

    public boolean ehErro() {
        return severidade == Severidade.ERRO;
    }

    public boolean ehAviso() {
        return severidade == Severidade.AVISO;
    }
}
