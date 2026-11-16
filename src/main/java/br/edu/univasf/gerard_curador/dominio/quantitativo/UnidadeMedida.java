package br.edu.univasf.gerard_curador.dominio.quantitativo;

/**
 * Unidade compativel com uma grandeza.
 *
 * <p>Cada unidade nasce vinculada a grandeza que a torna significativa. Esse
 * vinculo e declarado na propria enumeracao, e nao verificado por um "if"
 * espalhado pela aplicacao: o objeto carrega consigo o conhecimento sobre o
 * qual tem autoridade.
 *
 * <p>Consequencia pratica: nao existe caminho no codigo que produza uma
 * UnidadeMedida sem grandeza, nem que associe REAL a MASSA.
 */
public enum UnidadeMedida {

    /** Unidade de contagem: uma rosa, uma figurinha, uma bola. */
    UNIDADE(GrandezaQuantitativa.CONTAGEM, "unidade", "unidades"),

    /** Real brasileiro, para as situacoes de compra e troco. */
    REAL(GrandezaQuantitativa.VALOR_MONETARIO, "real", "reais"),

    /** Centavo de real. */
    CENTAVO(GrandezaQuantitativa.VALOR_MONETARIO, "centavo", "centavos"),

    /** Quilograma, para a situacao do arroz na dispensa. */
    QUILOGRAMA(GrandezaQuantitativa.MASSA, "quilograma", "quilogramas"),

    /** Grama. */
    GRAMA(GrandezaQuantitativa.MASSA, "grama", "gramas");

    private final GrandezaQuantitativa grandeza;
    private final String singular;
    private final String plural;

    UnidadeMedida(GrandezaQuantitativa grandeza, String singular, String plural) {
        this.grandeza = grandeza;
        this.singular = singular;
        this.plural = plural;
    }

    public GrandezaQuantitativa getGrandeza() {
        return grandeza;
    }

    public String getSingular() {
        return singular;
    }

    public String getPlural() {
        return plural;
    }

    /**
     * Flexao de numero da unidade.
     *
     * <p>A unidade sabe sua propria flexao porque singular e plural sao
     * propriedades dela. A escolha de <em>quando</em> exibir a unidade e da
     * realizacao textual; a forma correta da palavra e daqui.
     */
    public String flexionar(Numero numero) {
        if (numero == null) {
            throw new IllegalArgumentException("flexao exige um numero");
        }
        return numero.getValor() == 1 ? singular : plural;
    }

    /** Duas unidades operam juntas apenas quando sao identicas. */
    public boolean ehCompativelCom(UnidadeMedida outra) {
        return this == outra;
    }

    /** Sobrecarga: verdadeiro quando esta unidade mede a grandeza informada. */
    public boolean ehCompativelCom(GrandezaQuantitativa outraGrandeza) {
        return grandeza == outraGrandeza;
    }
}
