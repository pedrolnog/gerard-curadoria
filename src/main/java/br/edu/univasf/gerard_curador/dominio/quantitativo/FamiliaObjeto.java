package br.edu.univasf.gerard_curador.dominio.quantitativo;
import java.util.Objects;

public class FamiliaObjeto {
    private final String id;
    private final String nomeExibicao;

    public FamiliaObjeto(String id, String nomeExibicao) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("O ID da família não pode ser nulo ou vazio.");
        }
        if (nomeExibicao == null || nomeExibicao.isBlank()) {
            throw new IllegalArgumentException("O nome de exibição não pode ser nulo ou vazio.");
        }

        this.id = id.trim().toLowerCase();
        this.nomeExibicao = nomeExibicao.trim();
    }

    public String getId() {
        return this.id;
    }

    public String getNomeExibicao() {
        return this.nomeExibicao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FamiliaObjeto that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public String toString() {
        return nomeExibicao + "(" + id + ")";
    }
}
