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

    private final String trecho;
    private final String motivo;

    public AmbiguidadeDeclarada(String trecho, String motivo) {
        if (trecho == null || trecho.isBlank()) {
            throw new IllegalArgumentException("ambiguidade exige o trecho do enunciado");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("ambiguidade exige o motivo");
        }
        this.trecho = trecho;
        this.motivo = motivo;
    }

    public String getTrecho() {
        return trecho;
    }

    public String getMotivo() {
        return motivo;
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
