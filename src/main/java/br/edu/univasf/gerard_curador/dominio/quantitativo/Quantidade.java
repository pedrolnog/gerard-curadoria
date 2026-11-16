package br.edu.univasf.gerard_curador.dominio.quantitativo;

public final class Quantidade {
    private final Numero numero;
    private final ObjetoContado objeto;
    private final GrandezaQuantitativa grandeza;
    private final UnidadeMedida unidade;

    public Quantidade(Numero numero, ObjetoContado objeto, GrandezaQuantitativa grandeza, UnidadeMedida unidade) {
        this.numero = numero;
        this.objeto = objeto;
        this.grandeza = grandeza;
        this.unidade = unidade;
    }

    public Numero getNumero() {
        return numero;
    }

    public void exigirCompatibilidadeCom(Quantidade outra) {
        if (outra == null || grandeza != outra.grandeza || unidade != outra.unidade
                || !objeto.pertenceAMesmaFamilia(outra.objeto)) {
            throw new IllegalArgumentException("Quantidades incompativeis.");
        }
    }

    public Quantidade somar(Quantidade outra) {
        exigirCompatibilidadeCom(outra);
        return new Quantidade(numero.somar(outra.numero), objeto, grandeza, unidade);
    }


    public Quantidade subtrair(Quantidade outra) {
        exigirCompatibilidadeCom(outra);
        Numero resultado = numero.subtrair(outra.numero);
        if (resultado.ehNegativo()) {
            throw new IllegalArgumentException("Erro: Quantidade não pode ser negativa. (" + resultado.getValor() + ").");
        }
        return new Quantidade(resultado, objeto, grandeza, unidade);
    }
}