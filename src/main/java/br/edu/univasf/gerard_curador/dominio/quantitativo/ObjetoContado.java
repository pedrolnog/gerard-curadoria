package br.edu.univasf.gerard_curador.dominio.quantitativo;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Identidade daquilo que e contado: sua familia e suas caracteristicas.
 *
 * <p>Responde a pergunta 1 da apresentacao final. Uma String "rosa branca" nao
 * sabe que pertence a familia rosa, nao sabe que difere de "rosa amarela"
 * apenas na cor, e aceitaria ser somada a "boneca" sem reclamar. ObjetoContado
 * sabe as tres coisas, e por isso e a autoridade sobre a identidade do que se
 * conta.
 *
 * <p>Imutavel, com o conjunto de caracteristicas defensivamente copiado e
 * exposto apenas como visao nao modificavel: um chamador nao consegue acrescentar
 * uma cor a uma rosa ja catalogada.
 */
public final class ObjetoContado {

    private final String id;
    private final FamiliaObjeto familia;
    private final Set<CaracteristicaObjeto> caracteristicas;
    private final RealizacaoTextualObjeto realizacaoTextual;
    private final ReferenciaVisual referenciaVisual;

    public ObjetoContado(String id,
                         FamiliaObjeto familia,
                         Set<CaracteristicaObjeto> caracteristicas,
                         RealizacaoTextualObjeto realizacaoTextual,
                         ReferenciaVisual referenciaVisual) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("objeto contado exige id");
        }
        if (familia == null) {
            throw new IllegalArgumentException("objeto contado exige familia");
        }
        if (realizacaoTextual == null) {
            throw new IllegalArgumentException("objeto contado exige realizacao textual");
        }
        this.id = id;
        this.familia = familia;
        this.caracteristicas = caracteristicas == null
                ? Collections.emptySet()
                : Collections.unmodifiableSet(new LinkedHashSet<>(caracteristicas));
        this.realizacaoTextual = realizacaoTextual;
        // A referencia visual e opcional: uma situacao pode ser curada antes de
        // alguem decidir como desenha-la. O dominio nao trava por falta de arte.
        this.referenciaVisual = referenciaVisual == null
                ? ReferenciaVisual.naoDefinida()
                : referenciaVisual;
    }

    public String getId() {
        return id;
    }

    public FamiliaObjeto getFamilia() {
        return familia;
    }

    public Set<CaracteristicaObjeto> getCaracteristicas() {
        return caracteristicas;
    }

    public RealizacaoTextualObjeto getRealizacaoTextual() {
        return realizacaoTextual;
    }

    public ReferenciaVisual getReferenciaVisual() {
        return referenciaVisual;
    }

    /**
     * Regra que autoriza a totalizacao por familia.
     *
     * <p>Rosa branca e rosa amarela respondem verdadeiro; rosa e boneca, falso.
     */
    public boolean pertenceAMesmaFamilia(ObjetoContado outro) {
        return outro != null && familia.equals(outro.familia);
    }

    public boolean pertenceAFamilia(FamiliaObjeto outraFamilia) {
        return familia.equals(outraFamilia);
    }

    /** Verdadeiro quando o objeto possui exatamente a caracteristica informada. */
    public boolean possuiCaracteristica(CaracteristicaObjeto caracteristica) {
        return caracteristicas.contains(caracteristica);
    }

    /**
     * Valor da caracteristica numa dada dimensao, quando existir.
     *
     * <p>Permite perguntar "qual a cor desta rosa?" sem que o chamador precise
     * varrer o conjunto por conta propria.
     */
    public String valorDaCaracteristica(String dimensao) {
        for (CaracteristicaObjeto caracteristica : caracteristicas) {
            if (caracteristica.getDimensao().equals(dimensao)) {
                return caracteristica.getValor();
            }
        }
        return null;
    }

    /**
     * O objeto contado produz a Quantidade que o refere.
     *
     * <p>Ponto de entrada preferencial: quantificar a partir do objeto deixa
     * explicito que uma quantidade sempre conta alguma coisa, nunca um numero
     * solto.
     */
    public Quantidade quantificar(Numero numero,
                                  GrandezaQuantitativa grandeza,
                                  UnidadeMedida unidade) {
        return new Quantidade(numero, this, grandeza, unidade);
    }

    /** Atalho para a grandeza mais comum nas situacoes curadas. */
    public Quantidade contar(int quantosItens) {
        return quantificar(Numero.de(quantosItens),
                GrandezaQuantitativa.CONTAGEM,
                UnidadeMedida.UNIDADE);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ObjetoContado)) {
            return false;
        }
        return id.equals(((ObjetoContado) obj).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }
}
