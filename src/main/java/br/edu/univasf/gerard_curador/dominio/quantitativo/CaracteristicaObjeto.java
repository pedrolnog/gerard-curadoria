package br.edu.univasf.gerard_curador.dominio.quantitativo;

import java.util.Objects;

/**
 * Propriedade semanticamente relevante de um objeto contado.
 *
 * <p>Cor, material ou colecao sao caracteristicas: pares de dimensao e valor,
 * como cor=branca ou material=vidro. A modelagem por par nomeado responde a
 * pergunta 3 da apresentacao final: criar RosaBranca e RosaAmarela como
 * subclasses exigiria uma classe nova a cada enunciado curado, enquanto a
 * caracteristica permite descrever qualquer variacao com o mesmo codigo.
 *
 * <p>A distincao entre caracteristica de dominio e escolha visual (pergunta 2)
 * fica registrada aqui: uma caracteristica existe quando o enunciado a usa para
 * distinguir ou totalizar objetos. A cor das rosas e caracteristica porque as
 * transformacoes agem sobre uma cor especifica. Se a cor so importasse ao
 * desenho, ela pertenceria a ReferenciaVisual.
 */
public final class CaracteristicaObjeto {

    private final String dimensao;
    private final String valor;

    public CaracteristicaObjeto(String dimensao, String valor) {
        if (dimensao == null || dimensao.isBlank()) {
            throw new IllegalArgumentException("caracteristica exige dimensao");
        }
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("caracteristica exige valor");
        }
        this.dimensao = dimensao;
        this.valor = valor;
    }

    public static CaracteristicaObjeto de(String dimensao, String valor) {
        return new CaracteristicaObjeto(dimensao, valor);
    }

    public String getDimensao() {
        return dimensao;
    }

    public String getValor() {
        return valor;
    }

    /** Verdadeiro quando as duas descrevem a mesma dimensao, ainda que com valores diferentes. */
    public boolean descreveMesmaDimensaoQue(CaracteristicaObjeto outra) {
        return outra != null && dimensao.equals(outra.dimensao);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CaracteristicaObjeto)) {
            return false;
        }
        CaracteristicaObjeto outra = (CaracteristicaObjeto) obj;
        return dimensao.equals(outra.dimensao) && valor.equals(outra.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dimensao, valor);
    }

    @Override
    public String toString() {
        return dimensao + "=" + valor;
    }
}
