package br.edu.univasf.gerard_curador.persistencia.dto;

import java.util.List;

/**
 * DTO anemico raiz para transporte JSON de uma situacao-problema.
 *
 * <p>Diferente do dominio rico ({@code SituacaoProblema}), apenas espelha a estrutura do
 * arquivo JSON sem regras de validacao, calculo ou curadoria; o dominio nao importa Gson.
 */
public final class SituacaoDto {

    private String id;
    private String enunciado;
    private String categoria;
    private String escopo;
    private List<ParticipanteDto> participantes;
    private List<ObjetoContadoDto> objetosContados;
    private ContextoDto contexto;
    private IncognitaDto incognita;
    private EstadoDto estadoInicial;
    private List<TransformacaoDto> transformacoes;
    private EstadoDto estadoFinalCurado;
    private RelacaoDto relacao;
    private List<AmbiguidadeDto> ambiguidades;
    private String status;
    private String revisor;
    private String motivoRecusa;

    public SituacaoDto() {
    }

    public SituacaoDto(String id,
                       String enunciado,
                       String categoria,
                       String escopo,
                       List<ParticipanteDto> participantes,
                       List<ObjetoContadoDto> objetosContados,
                       ContextoDto contexto,
                       IncognitaDto incognita,
                       EstadoDto estadoInicial,
                       List<TransformacaoDto> transformacoes,
                       EstadoDto estadoFinalCurado,
                       RelacaoDto relacao,
                       List<AmbiguidadeDto> ambiguidades,
                       String status,
                       String revisor,
                       String motivoRecusa) {
        this.id = id;
        this.enunciado = enunciado;
        this.categoria = categoria;
        this.escopo = escopo;
        this.participantes = participantes;
        this.objetosContados = objetosContados;
        this.contexto = contexto;
        this.incognita = incognita;
        this.estadoInicial = estadoInicial;
        this.transformacoes = transformacoes;
        this.estadoFinalCurado = estadoFinalCurado;
        this.relacao = relacao;
        this.ambiguidades = ambiguidades;
        this.status = status;
        this.revisor = revisor;
        this.motivoRecusa = motivoRecusa;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getEscopo() {
        return escopo;
    }

    public void setEscopo(String escopo) {
        this.escopo = escopo;
    }

    public List<ParticipanteDto> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<ParticipanteDto> participantes) {
        this.participantes = participantes;
    }

    public List<ObjetoContadoDto> getObjetosContados() {
        return objetosContados;
    }

    public void setObjetosContados(List<ObjetoContadoDto> objetosContados) {
        this.objetosContados = objetosContados;
    }

    public ContextoDto getContexto() {
        return contexto;
    }

    public void setContexto(ContextoDto contexto) {
        this.contexto = contexto;
    }

    public IncognitaDto getIncognita() {
        return incognita;
    }

    public void setIncognita(IncognitaDto incognita) {
        this.incognita = incognita;
    }

    public EstadoDto getEstadoInicial() {
        return estadoInicial;
    }

    public void setEstadoInicial(EstadoDto estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

    public List<TransformacaoDto> getTransformacoes() {
        return transformacoes;
    }

    public void setTransformacoes(List<TransformacaoDto> transformacoes) {
        this.transformacoes = transformacoes;
    }

    public EstadoDto getEstadoFinalCurado() {
        return estadoFinalCurado;
    }

    public void setEstadoFinalCurado(EstadoDto estadoFinalCurado) {
        this.estadoFinalCurado = estadoFinalCurado;
    }

    public RelacaoDto getRelacao() {
        return relacao;
    }

    public void setRelacao(RelacaoDto relacao) {
        this.relacao = relacao;
    }

    public List<AmbiguidadeDto> getAmbiguidades() {
        return ambiguidades;
    }

    public void setAmbiguidades(List<AmbiguidadeDto> ambiguidades) {
        this.ambiguidades = ambiguidades;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRevisor() {
        return revisor;
    }

    public void setRevisor(String revisor) {
        this.revisor = revisor;
    }

    public String getMotivoRecusa() {
        return motivoRecusa;
    }

    public void setMotivoRecusa(String motivoRecusa) {
        this.motivoRecusa = motivoRecusa;
    }
}
