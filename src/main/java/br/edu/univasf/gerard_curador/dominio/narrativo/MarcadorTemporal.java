package br.edu.univasf.gerard_curador.dominio.narrativo;

import java.util.Objects;

/**
 * Ordem ou referencia temporal explicitamente curada.
 *
 * <p>Detem a fronteira "ordem e referencia temporal". Guarda duas coisas
 * relacionadas mas distintas:
 * <ul>
 *   <li>a <strong>ordem</strong>, um inteiro que estabelece a sequencia dos
 *       eventos;
 *   <li>o <strong>rotulo</strong> curado, texto como "ontem", "hoje", "primeira
 *       partida" ou "segunda partida".
 * </ul>
 *
 * <p>Os dois sao necessarios porque o rotulo nao e ordenavel por si: "ontem"
 * vem antes de "hoje" por conhecimento de mundo, nao por comparacao de strings.
 * A ordem vem do curador, atendendo ao criterio "a ordem temporal vem do dado
 * curado, nao da posicao na tela".
 *
 * <p>Nas situacoes de duas partidas, "segunda partida" pode ser enunciada antes
 * da primeira no texto do problema ("Na segunda ele ganhou 12 bolas. Ao final
 * das duas partidas..."). O marcador preserva a ordem real dos eventos,
 * independentemente da ordem em que o enunciado os menciona (caso de teste 17).
 */
public final class MarcadorTemporal implements Comparable<MarcadorTemporal> {

    private final int ordem;
    private final String rotulo;

    public MarcadorTemporal(int ordem, String rotulo) {
        if (ordem < 1) {
            throw new IllegalArgumentException("ordem temporal deve comecar em 1, recebida: " + ordem);
        }
        if (rotulo == null || rotulo.isBlank()) {
            throw new IllegalArgumentException("marcador temporal exige rotulo curado");
        }
        this.ordem = ordem;
        this.rotulo = rotulo;
    }

    /** Marcador cujo rotulo e apenas a posicao, quando o enunciado nao nomeia o momento. */
    public static MarcadorTemporal daOrdem(int ordem) {
        return new MarcadorTemporal(ordem, "momento " + ordem);
    }

    public static MarcadorTemporal de(int ordem, String rotulo) {
        return new MarcadorTemporal(ordem, rotulo);
    }

    public int getOrdem() {
        return ordem;
    }

    public String getRotulo() {
        return rotulo;
    }

    public boolean ehAnteriorA(MarcadorTemporal outro) {
        return outro != null && ordem < outro.ordem;
    }

    public boolean ehPosteriorA(MarcadorTemporal outro) {
        return outro != null && ordem > outro.ordem;
    }

    /**
     * Verdadeiro quando dois marcadores disputam a mesma posicao temporal.
     *
     * <p>Usada pela situacao para diagnosticar "ordem temporal repetida" (caso de
     * teste 7): duas transformacoes nao podem ocupar o mesmo instante, porque
     * entao a sequencia seria ambigua.
     */
    public boolean ocupaMesmaOrdemQue(MarcadorTemporal outro) {
        return outro != null && ordem == outro.ordem;
    }

    @Override
    public int compareTo(MarcadorTemporal outro) {
        if (outro == null) {
            throw new IllegalArgumentException("comparacao temporal exige outro marcador");
        }
        return Integer.compare(ordem, outro.ordem);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MarcadorTemporal)) {
            return false;
        }
        MarcadorTemporal outro = (MarcadorTemporal) obj;
        return ordem == outro.ordem && rotulo.equals(outro.rotulo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ordem, rotulo);
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
