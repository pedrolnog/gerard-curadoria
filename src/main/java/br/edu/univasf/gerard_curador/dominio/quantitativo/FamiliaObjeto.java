package br.edu.univasf.gerard_curador.dominio.quantitativo;

import java.util.Objects;

/**
 * Identidade conceitual comum as variacoes do que e contado.
 *
 * <p>Entidade mutavel por setters validados; o id nao muda (e a identidade
 * usada em equals/hashCode).
 *
 * <p>"Rosa" e a familia de rosa_branca e rosa_amarela. E a familia que autoriza
 * a pergunta "com quantas rosas a Vovo ficou?" quando o inventario guarda duas
 * variacoes distintas: sem esse conceito, totalizar brancas com amarelas seria
 * uma soma arbitraria feita pela interface.
 *
 * <p>Nao e uma enumeracao porque o conjunto de familias e aberto: cada situacao
 * curada pode introduzir figurinha, bola, fofolete ou chocolate sem alterar o
 * codigo-fonte. E uma classe de valor com identidade propria.
 */
public final class FamiliaObjeto {

    private final String id;
    private String nomeConceitual;

    public FamiliaObjeto(String id, String nomeConceitual) {
        this.id = validarId(id);
        this.nomeConceitual = validarNomeConceitual(nomeConceitual);
    }

    private static String validarId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("familia exige id");
        }
        return id.trim().toLowerCase();
    }

    private static String validarNomeConceitual(String nomeConceitual) {
        if (nomeConceitual == null || nomeConceitual.isBlank()) {
            throw new IllegalArgumentException("familia exige nome conceitual");
        }
        return nomeConceitual.trim();
    }

    public String getId() {
        return id;
    }

    public String getNomeConceitual() {
        return nomeConceitual;
    }

    /**
     * Altera o nome conceitual da familia.
     */
    public void setNomeConceitual(String nomeConceitual) {
        this.nomeConceitual = validarNomeConceitual(nomeConceitual);
    }

    /**
     * A identidade da familia e o id curado, nao o nome exibido.
     *
     * <p>Duas familias com o mesmo id sao a mesma familia ainda que o nome tenha
     * sido escrito em idiomas diferentes.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FamiliaObjeto)) {
            return false;
        }
        return id.equals(((FamiliaObjeto) obj).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nomeConceitual;
    }
}
