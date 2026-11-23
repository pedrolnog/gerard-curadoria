package br.edu.univasf.gerard_curador.dominio.situacao;

import java.util.Objects;

/**
 * Trecho do enunciado que admite mais de uma leitura, registrado pelo curador.
 *
 * <p>Exemplo: "Vovo deu 4 rosas para as netinhas" nao diz quantas cada uma
 * recebeu. O sistema nunca escolhe uma leitura sozinho (caso de teste 18): a
 * ambiguidade vira diagnostico e bloqueia a promocao ate o curador decidir.
 */
public final class AmbiguidadeDeclarada {

    private String trecho;
    private String motivo;

    public AmbiguidadeDeclarada(String trecho, String motivo) {
        validarTrecho(trecho);
        validarMotivo(motivo);
        this.trecho = trecho;
        this.motivo = motivo;
    }

    private static void validarTrecho(String trecho) {
        if (trecho == null || trecho.isBlank()) {
            throw new IllegalArgumentException("ambiguidade exige o trecho do enunciado");
        }
    }

    private static void validarMotivo(String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("ambiguidade exige o motivo");
        }
    }

    public String getTrecho() {
        return trecho;
    }

    /** Define o trecho do enunciado com ambiguidade. */
    public void setTrecho(String trecho) {
        validarTrecho(trecho);
        this.trecho = trecho;
    }

    public String getMotivo() {
        return motivo;
    }

    /** Define o motivo da ambiguidade. */
    public void setMotivo(String motivo) {
        validarMotivo(motivo);
        this.motivo = motivo;
    }

    public String descrever() {
        return "\"" + trecho + "\": " + motivo;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AmbiguidadeDeclarada outra)) {
            return false;
        }
        return trecho.equals(outra.trecho) && motivo.equals(outra.motivo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trecho, motivo);
    }

    @Override
    public String toString() {
        return descrever();
    }
}
