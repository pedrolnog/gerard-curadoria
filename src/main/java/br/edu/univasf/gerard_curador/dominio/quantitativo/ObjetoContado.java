package br.edu.univasf.gerard_curador.dominio.quantitativo;

import java.util.Set;

public final class ObjetoContado {
    private final String id;
    private final FamiliaObjeto familia;
    private final Set<CaracteristicaObjeto> caracteristicas;

    public ObjetoContado(String id, FamiliaObjeto familia, Set<CaracteristicaObjeto> caracteristicas) {
        this.id = id;
        this.familia = familia;
        this.caracteristicas = caracteristicas;
    }

    public boolean pertenceAMesmaFamilia(ObjetoContado outro) {
        return outro != null && familia.equals(outro.familia);
    }
    public Quantidade quantificar(Numero numero,
                                  GrandezaQuantitativa grandeza, UnidadeMedida unidade) {
        return new Quantidade(numero, this, grandeza, unidade);
    }
}
