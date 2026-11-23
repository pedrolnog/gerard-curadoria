package br.edu.univasf.gerard_curador.dominio.quantitativo;

import java.util.Objects;

/**
 * Formas linguisticas curadas de um objeto contado: singular, plural e idioma.
 *
 * <p>Entidade mutavel por setters validados.
 *
 * <p>Existe para manter a gramatica fora do objeto matematico, conforme a
 * fronteira obrigatoria "singular, plural e idioma -> RealizacaoTextualObjeto".
 * Sem ela, Quantidade precisaria decidir entre "1 rosas" e "1 rosa", assumindo
 * uma responsabilidade de lingua que nao lhe pertence.
 *
 * <p>As formas sao curadas, nao derivadas: o sistema nao acrescenta "s" ao
 * singular. Portugues tem plurais irregulares, e o projeto proibe inferencia
 * automatica sobre dados que o curador deve informar.
 */
public final class RealizacaoTextualObjeto {

    private String singular;
    private String plural;
    private String idioma;

    public RealizacaoTextualObjeto(String singular, String plural, String idioma) {
        this.singular = validarSingular(singular);
        this.plural = validarPlural(plural);
        this.idioma = validarIdioma(idioma);
    }

    private static String validarSingular(String singular) {
        if (singular == null || singular.isBlank()) {
            throw new IllegalArgumentException("realizacao textual exige singular");
        }
        return singular;
    }

    private static String validarPlural(String plural) {
        if (plural == null || plural.isBlank()) {
            throw new IllegalArgumentException("realizacao textual exige plural");
        }
        return plural;
    }

    private static String validarIdioma(String idioma) {
        if (idioma == null || idioma.isBlank()) {
            throw new IllegalArgumentException("realizacao textual exige idioma");
        }
        return idioma;
    }

    /** Fabrica para o idioma padrao das situacoes curadas na disciplina. */
    public static RealizacaoTextualObjeto emPortugues(String singular, String plural) {
        return new RealizacaoTextualObjeto(singular, plural, "pt-BR");
    }

    public String getSingular() {
        return singular;
    }

    /**
     * Altera a forma singular.
     */
    public void setSingular(String singular) {
        this.singular = validarSingular(singular);
    }

    public String getPlural() {
        return plural;
    }

    /**
     * Altera a forma plural.
     */
    public void setPlural(String plural) {
        this.plural = validarPlural(plural);
    }

    public String getIdioma() {
        return idioma;
    }

    /**
     * Altera o idioma.
     */
    public void setIdioma(String idioma) {
        this.idioma = validarIdioma(idioma);
    }

    /**
     * Escolhe a forma adequada ao numero.
     *
     * <p>Em portugues, zero leva plural ("nenhuma rosa" e uma construcao
     * diferente, decidida por quem escreve a frase, nao aqui).
     */
    public String flexionar(Numero numero) {
        if (numero == null) {
            throw new IllegalArgumentException("flexao exige um numero");
        }
        return numero.getValor() == 1 ? singular : plural;
    }

    /** Monta o sintagma completo: "2 rosas brancas", "1 rosa branca". */
    public String descrever(Numero numero) {
        return numero.getValor() + " " + flexionar(numero);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RealizacaoTextualObjeto)) {
            return false;
        }
        RealizacaoTextualObjeto outra = (RealizacaoTextualObjeto) obj;
        return singular.equals(outra.singular)
                && plural.equals(outra.plural)
                && idioma.equals(outra.idioma);
    }

    @Override
    public int hashCode() {
        return Objects.hash(singular, plural, idioma);
    }

    @Override
    public String toString() {
        return singular + "/" + plural + " (" + idioma + ")";
    }
}
