package br.edu.univasf.gerard_curador.persistencia.dto;

import java.util.Map;

/**
 * DTO anemico para transporte JSON de um objeto contado do catalogo.
 *
 * <p>Diferente do dominio rico, apenas espelha campos serializaveis sem validar identidade ou familia;
 * o dominio nao importa Gson nem conhece esta representacao.
 */
public final class ObjetoContadoDto {

    private String id;
    private String familia;
    private String familiaNome;
    private Map<String, String> caracteristicas;
    private RealizacaoTextualDto realizacaoTextual;
    private String referenciaVisual;

    public ObjetoContadoDto() {
    }

    public ObjetoContadoDto(String id,
                            String familia,
                            String familiaNome,
                            Map<String, String> caracteristicas,
                            RealizacaoTextualDto realizacaoTextual,
                            String referenciaVisual) {
        this.id = id;
        this.familia = familia;
        this.familiaNome = familiaNome;
        this.caracteristicas = caracteristicas;
        this.realizacaoTextual = realizacaoTextual;
        this.referenciaVisual = referenciaVisual;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFamilia() {
        return familia;
    }

    public void setFamilia(String familia) {
        this.familia = familia;
    }

    public String getFamiliaNome() {
        return familiaNome;
    }

    public void setFamiliaNome(String familiaNome) {
        this.familiaNome = familiaNome;
    }

    public Map<String, String> getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(Map<String, String> caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    public RealizacaoTextualDto getRealizacaoTextual() {
        return realizacaoTextual;
    }

    public void setRealizacaoTextual(RealizacaoTextualDto realizacaoTextual) {
        this.realizacaoTextual = realizacaoTextual;
    }

    public String getReferenciaVisual() {
        return referenciaVisual;
    }

    public void setReferenciaVisual(String referenciaVisual) {
        this.referenciaVisual = referenciaVisual;
    }
}
