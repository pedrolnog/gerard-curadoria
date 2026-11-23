package br.edu.univasf.gerard_curador.dominio.quantitativo;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Identidade daquilo que e contado: sua familia e suas caracteristicas.
 *
 * <p>Entidade mutavel por setters validados; o id nao muda (e a identidade
 * usada em equals/hashCode). O conjunto de caracteristicas e defensivamente
 * copiado e exposto pelo getter apenas como visao nao modificavel.
 *
 * <p>Responde a pergunta 1 da apresentacao final. Uma String "rosa branca" nao
 * sabe que pertence a familia rosa, nao sabe que difere de "rosa amarela"
 * apenas na cor, e aceitaria ser somada a "boneca" sem reclamar. ObjetoContado
 * sabe as tres coisas, e por isso e a autoridade sobre a identidade do que se
 * conta.
 */
public final class ObjetoContado {

    private final String id;
    private FamiliaObjeto familia;
    private Set<CaracteristicaObjeto> caracteristicas;
    private RealizacaoTextualObjeto realizacaoTextual;
    private ReferenciaVisual referenciaVisual;

    public ObjetoContado(String id,
                         FamiliaObjeto familia,
                         Set<CaracteristicaObjeto> caracteristicas,
                         RealizacaoTextualObjeto realizacaoTextual,
                         ReferenciaVisual referenciaVisual) {
        this.id = validarId(id);
        this.familia = validarFamilia(familia);
        this.caracteristicas = copiarCaracteristicas(caracteristicas);
        this.realizacaoTextual = validarRealizacaoTextual(realizacaoTextual);
        // A referencia visual e opcional: uma situacao pode ser curada antes de
        // alguem decidir como desenha-la. O dominio nao trava por falta de arte.
        this.referenciaVisual = normalizarReferenciaVisual(referenciaVisual);
    }

    private static String validarId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("objeto contado exige id");
        }
        return id;
    }

    private static FamiliaObjeto validarFamilia(FamiliaObjeto familia) {
        if (familia == null) {
            throw new IllegalArgumentException("objeto contado exige familia");
        }
        return familia;
    }

    private static RealizacaoTextualObjeto validarRealizacaoTextual(RealizacaoTextualObjeto realizacaoTextual) {
        if (realizacaoTextual == null) {
            throw new IllegalArgumentException("objeto contado exige realizacao textual");
        }
        return realizacaoTextual;
    }

    private static ReferenciaVisual normalizarReferenciaVisual(ReferenciaVisual referenciaVisual) {
        return referenciaVisual == null
                ? ReferenciaVisual.naoDefinida()
                : referenciaVisual;
    }

    private static Set<CaracteristicaObjeto> copiarCaracteristicas(Set<CaracteristicaObjeto> caracteristicas) {
        return caracteristicas == null
                ? new LinkedHashSet<>()
                : new LinkedHashSet<>(caracteristicas);
    }

    public String getId() {
        return id;
    }

    public FamiliaObjeto getFamilia() {
        return familia;
    }

    /**
     * Altera a familia do objeto contado.
     */
    public void setFamilia(FamiliaObjeto familia) {
        this.familia = validarFamilia(familia);
    }

    public Set<CaracteristicaObjeto> getCaracteristicas() {
        return Collections.unmodifiableSet(caracteristicas);
    }

    /**
     * Altera o conjunto de caracteristicas do objeto contado.
     */
    public void setCaracteristicas(Set<CaracteristicaObjeto> caracteristicas) {
        this.caracteristicas = copiarCaracteristicas(caracteristicas);
    }

    /**
     * Adiciona uma caracteristica ao objeto contado.
     */
    public void adicionarCaracteristica(CaracteristicaObjeto caracteristica) {
        if (caracteristica == null) {
            throw new IllegalArgumentException("caracteristica nao pode ser nula");
        }
        this.caracteristicas.add(caracteristica);
    }

    /**
     * Remove uma caracteristica do objeto contado.
     */
    public void removerCaracteristica(CaracteristicaObjeto caracteristica) {
        if (caracteristica == null) {
            throw new IllegalArgumentException("caracteristica nao pode ser nula");
        }
        this.caracteristicas.remove(caracteristica);
    }

    public RealizacaoTextualObjeto getRealizacaoTextual() {
        return realizacaoTextual;
    }

    /**
     * Altera a realizacao textual do objeto contado.
     */
    public void setRealizacaoTextual(RealizacaoTextualObjeto realizacaoTextual) {
        this.realizacaoTextual = validarRealizacaoTextual(realizacaoTextual);
    }

    public ReferenciaVisual getReferenciaVisual() {
        return referenciaVisual;
    }

    /**
     * Altera a referencia visual do objeto contado.
     */
    public void setReferenciaVisual(ReferenciaVisual referenciaVisual) {
        this.referenciaVisual = normalizarReferenciaVisual(referenciaVisual);
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
