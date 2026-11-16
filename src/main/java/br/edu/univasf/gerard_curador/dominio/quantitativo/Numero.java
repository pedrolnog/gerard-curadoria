package br.edu.univasf.gerard_curador.dominio.quantitativo;

/**
 * Valor inteiro e operacoes numericas.
 *
 * <p>Responsabilidade unica desta classe: saber somar, subtrair, comparar e
 * classificar um valor inteiro. Ela nao sabe o que esta sendo contado, nem em
 * qual grandeza, nem qual papel ocupa na situacao. Essa ignorancia e
 * proposital: e o que permite reutilizar Numero em contagens, dinheiro e massa
 * sem contaminar a classe com conhecimento de dominio que pertence a outros
 * objetos.
 *
 * <p>Objeto imutavel: toda operacao devolve uma nova instancia. Isso impede que
 * um estado anterior seja corrompido quando alguem calcula sobre ele, atendendo
 * ao criterio de aceitacao "demonstrar que os estados anteriores nao sao
 * corrompidos".
 */
public final class Numero implements Comparable<Numero> {

    public static final Numero ZERO = new Numero(0);

    private final int valor;

    public Numero(int valor) {
        this.valor = valor;
    }

    /** Fabrica de leitura mais fluente nos testes e nas montagens de catalogo. */
    public static Numero de(int valor) {
        return new Numero(valor);
    }

    public int getValor() {
        return valor;
    }

    public Numero somar(Numero outro) {
        exigirNaoNulo(outro);
        return new Numero(valor + outro.valor);
    }

    public Numero subtrair(Numero outro) {
        exigirNaoNulo(outro);
        return new Numero(valor - outro.valor);
    }

    /**
     * Distancia absoluta entre dois numeros.
     *
     * <p>Usada pela relacao estrutural para descrever o tamanho de uma
     * divergencia entre o valor calculado e o valor curado, sem precisar decidir
     * qual dos dois esta correto.
     */
    public Numero diferencaAbsolutaPara(Numero outro) {
        exigirNaoNulo(outro);
        return new Numero(Math.abs(valor - outro.valor));
    }

    public boolean ehNegativo() {
        return valor < 0;
    }

    public boolean ehZero() {
        return valor == 0;
    }

    public boolean ehPositivo() {
        return valor > 0;
    }

    public boolean ehMenorQue(Numero outro) {
        exigirNaoNulo(outro);
        return valor < outro.valor;
    }

    public boolean ehMaiorQue(Numero outro) {
        exigirNaoNulo(outro);
        return valor > outro.valor;
    }

    private static void exigirNaoNulo(Numero outro) {
        if (outro == null) {
            throw new IllegalArgumentException("operacao numerica exige outro numero");
        }
    }

    @Override
    public int compareTo(Numero outro) {
        exigirNaoNulo(outro);
        return Integer.compare(valor, outro.valor);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Numero)) {
            return false;
        }
        return valor == ((Numero) obj).valor;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(valor);
    }

    @Override
    public String toString() {
        return Integer.toString(valor);
    }
}
