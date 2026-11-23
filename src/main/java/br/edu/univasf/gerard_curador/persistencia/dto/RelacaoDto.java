package br.edu.univasf.gerard_curador.persistencia.dto;

import java.util.List;
import java.util.Map;

/**
 * DTO anemico para transporte JSON da relacao estrutural da situacao.
 *
 * <p>Reune os campos aceitos pelos construtores de cada subclasse de RelacaoEstrutural.
 * Diferente do dominio rico, nao valida equacoes ou medidas; o dominio nao importa Gson.
 */
public final class RelacaoDto {

    private String id;
    private String categoria;
    private Map<String, QuantidadeDto> papeis;
    private String sentidoPrimeira;
    private String sentidoSegunda;
    private String sentidoResultante;
    private String participanteReferente;
    private String participanteReferido;
    private String direcao;
    private List<QuantidadeDto> partes;
    private QuantidadeDto todo;

    public RelacaoDto() {
    }

    public RelacaoDto(String id,
                      String categoria,
                      Map<String, QuantidadeDto> papeis,
                      String sentidoPrimeira,
                      String sentidoSegunda,
                      String sentidoResultante,
                      String participanteReferente,
                      String participanteReferido,
                      String direcao,
                      List<QuantidadeDto> partes,
                      QuantidadeDto todo) {
        this.id = id;
        this.categoria = categoria;
        this.papeis = papeis;
        this.sentidoPrimeira = sentidoPrimeira;
        this.sentidoSegunda = sentidoSegunda;
        this.sentidoResultante = sentidoResultante;
        this.participanteReferente = participanteReferente;
        this.participanteReferido = participanteReferido;
        this.direcao = direcao;
        this.partes = partes;
        this.todo = todo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Map<String, QuantidadeDto> getPapeis() {
        return papeis;
    }

    public void setPapeis(Map<String, QuantidadeDto> papeis) {
        this.papeis = papeis;
    }

    public String getSentidoPrimeira() {
        return sentidoPrimeira;
    }

    public void setSentidoPrimeira(String sentidoPrimeira) {
        this.sentidoPrimeira = sentidoPrimeira;
    }

    public String getSentidoSegunda() {
        return sentidoSegunda;
    }

    public void setSentidoSegunda(String sentidoSegunda) {
        this.sentidoSegunda = sentidoSegunda;
    }

    public String getSentidoResultante() {
        return sentidoResultante;
    }

    public void setSentidoResultante(String sentidoResultante) {
        this.sentidoResultante = sentidoResultante;
    }

    public String getParticipanteReferente() {
        return participanteReferente;
    }

    public void setParticipanteReferente(String participanteReferente) {
        this.participanteReferente = participanteReferente;
    }

    public String getParticipanteReferido() {
        return participanteReferido;
    }

    public void setParticipanteReferido(String participanteReferido) {
        this.participanteReferido = participanteReferido;
    }

    public String getDirecao() {
        return direcao;
    }

    public void setDirecao(String direcao) {
        this.direcao = direcao;
    }

    public List<QuantidadeDto> getPartes() {
        return partes;
    }

    public void setPartes(List<QuantidadeDto> partes) {
        this.partes = partes;
    }

    public QuantidadeDto getTodo() {
        return todo;
    }

    public void setTodo(QuantidadeDto todo) {
        this.todo = todo;
    }
}
