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

    public boolean exigirCompatibilidadeCom(Quantidade quant2) {

    }

    public Quantidade somar(Quantidade quant2) {
        Numero resultado = numero.somar(quant2.numero);

        if (resultado.ehNegativo()) {

        }
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