package br.edu.univasf.gerard_curador.dominio.quantitativo;

public enum UnidadeMedida {
    UNIDADE(GrandezaQuantitativa.CONTAGEM),
    REAL(GrandezaQuantitativa.VALOR_MONETARIO),
    CENTAVO(GrandezaQuantitativa.VALOR_MONETARIO),
    QUILOGRAMA(GrandezaQuantitativa.MASSA),
    GRAMA(GrandezaQuantitativa.MASSA);

    private final GrandezaQuantitativa grandeza;

    UnidadeMedida(GrandezaQuantitativa grandeza) {
        this.grandeza = grandeza;
    }

    public GrandezaQuantitativa getGrandeza() {
        return grandeza;
    }

    public boolean ehCompativelCom(GrandezaQuantitativa outraGrandeza) {
        return this.grandeza == outraGrandeza;
    }
}
