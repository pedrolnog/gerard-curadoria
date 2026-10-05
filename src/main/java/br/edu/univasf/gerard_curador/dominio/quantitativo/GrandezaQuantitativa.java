package br.edu.univasf.gerard_curador.dominio.quantitativo;

public enum GrandezaQuantitativa {
    CONTAGEM,
    VALOR_MONETARIO,
    MASSA,
    COMPRIMENTO,
    VOLUME;

    public boolean ehCompativelCom(UnidadeMedida unidade) {
        return unidade != null && unidade.getGrandeza() == this;
    }

    public UnidadeMedida getUnidadePadrao() {
        return switch (this) {
            case CONTAGEM -> UnidadeMedida.UNIDADE;
            case VALOR_MONETARIO -> UnidadeMedida.REAL;
            case MASSA -> UnidadeMedida.QUILOGRAMA;
        };
    }

}
