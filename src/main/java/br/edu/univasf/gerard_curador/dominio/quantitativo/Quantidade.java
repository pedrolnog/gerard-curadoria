package br.edu.univasf.gerard_curador.dominio.quantitativo;

import br.edu.univasf.gerard_curador.dominio.excecao.QuantidadeIncompativelException;
import br.edu.univasf.gerard_curador.dominio.excecao.RetiradaImpossivelException;

import java.util.Objects;

/**
 * Associacao entre Numero, objeto contado, grandeza e unidade.
 *
 * <p>Esta e a classe que responde "somar rosas e bonecas e rejeitado pelo
 * dominio" e "somar reais e quilogramas e rejeitado pelo dominio". A verificacao
 * mora aqui, e nao na interface, porque a pergunta "estas duas quantidades podem
 * ser operadas?" e sobre a natureza das quantidades envolvidas.
 *
 * <p>Invariantes protegidas na construcao:
 * <ul>
 *   <li>numero, objeto, grandeza e unidade sao obrigatorios;
 *   <li>a unidade deve pertencer a grandeza declarada;
 *   <li>uma quantidade nunca nasce negativa: nao existe "menos duas rosas"
 *       dentro de um inventario. O <em>sentido</em> de uma transformacao e
 *       representado por SentidoTransformacao, nao pelo sinal do numero.
 * </ul>
 *
 * <p>Imutavel: somar e subtrair devolvem novas instancias, preservando os
 * estados anteriores.
 */
public final class Quantidade {

    private final Numero numero;
    private final ObjetoContado objeto;
    private final GrandezaQuantitativa grandeza;
    private final UnidadeMedida unidade;

    public Quantidade(Numero numero,
                      ObjetoContado objeto,
                      GrandezaQuantitativa grandeza,
                      UnidadeMedida unidade) {
        if (numero == null) {
            throw new IllegalArgumentException("quantidade exige numero");
        }
        if (objeto == null) {
            throw new IllegalArgumentException("quantidade exige objeto contado");
        }
        if (grandeza == null) {
            throw new IllegalArgumentException("quantidade exige grandeza");
        }
        if (unidade == null) {
            throw new IllegalArgumentException("quantidade exige unidade de medida");
        }
        if (!grandeza.aceitaUnidade(unidade)) {
            throw new IllegalArgumentException(
                    "unidade " + unidade + " nao pertence a grandeza " + grandeza);
        }
        if (numero.ehNegativo()) {
            throw new IllegalArgumentException("quantidade nao pode ser negativa: " + numero);
        }
        this.numero = numero;
        this.objeto = objeto;
        this.grandeza = grandeza;
        this.unidade = unidade;
    }

    public Numero getNumero() {
        return numero;
    }

    public ObjetoContado getObjeto() {
        return objeto;
    }

    public GrandezaQuantitativa getGrandeza() {
        return grandeza;
    }

    public UnidadeMedida getUnidade() {
        return unidade;
    }

    public int getValor() {
        return numero.getValor();
    }

    public Quantidade somar(Quantidade outra) {
        exigirCompatibilidadeCom(outra);
        return comNumero(numero.somar(outra.numero));
    }

    /**
     * Subtracao que recusa produzir um resultado impossivel.
     *
     * <p>Retirar tres rosas brancas quando existem duas lanca excecao em vez de
     * devolver menos um: o estado invalido nunca chega a existir, e por isso o
     * inventario de origem permanece intacto (caso de teste 5).
     */
    public Quantidade subtrair(Quantidade outra) {
        exigirCompatibilidadeCom(outra);
        Numero resultado = numero.subtrair(outra.numero);
        if (resultado.ehNegativo()) {
            throw new RetiradaImpossivelException(
                    "quantidade indisponivel para retirada: possui " + numero
                            + ", tentou retirar " + outra.numero
                            + " de " + objeto.getId());
        }
        return comNumero(resultado);
    }

    /**
     * Compatibilidade completa: mesmo objeto contado, mesma grandeza, mesma unidade.
     *
     * <p>Usada para operacoes que somam ou subtraem quantidades do mesmo item.
     * A totalizacao por familia, mais permissiva, e responsabilidade do
     * Inventario, que sabe agrupar variacoes.
     */
    public boolean ehCompativelCom(Quantidade outra) {
        return outra != null
                && objeto.equals(outra.objeto)
                && grandeza.ehCompativelCom(outra.grandeza)
                && unidade.ehCompativelCom(outra.unidade);
    }

    /**
     * Compatibilidade de medida, ignorando qual item e contado.
     *
     * <p>Duas rosas de cores diferentes sao mensuravelmente compativeis: ambas
     * sao contagens em unidades. E o que autoriza o Inventario a totalizar a
     * familia rosa (caso de teste 16).
     */
    public boolean possuiMesmaMedidaQue(Quantidade outra) {
        return outra != null
                && grandeza.ehCompativelCom(outra.grandeza)
                && unidade.ehCompativelCom(outra.unidade);
    }

    /** Verdadeiro quando esta quantidade cobre a retirada pretendida. */
    public boolean comportaRetiradaDe(Quantidade outra) {
        return ehCompativelCom(outra) && !numero.ehMenorQue(outra.numero);
    }

    public boolean ehZero() {
        return numero.ehZero();
    }

    /**
     * Diagnostica a incompatibilidade em linguagem do dominio.
     *
     * <p>Metodo separado da excecao porque a validacao estrutural precisa
     * <em>relatar</em> problemas sem interromper o fluxo, enquanto as operacoes
     * aritmeticas precisam <em>impedir</em> o estado invalido.
     */
    public String descreverIncompatibilidadeCom(Quantidade outra) {
        if (outra == null) {
            return "quantidade ausente na comparacao";
        }
        if (!grandeza.ehCompativelCom(outra.grandeza)) {
            return "grandezas incompativeis: " + grandeza.getNomeExibido()
                    + " e " + outra.grandeza.getNomeExibido();
        }
        if (!unidade.ehCompativelCom(outra.unidade)) {
            return "unidades incompativeis: " + unidade + " e " + outra.unidade;
        }
        if (!objeto.equals(outra.objeto)) {
            return "objetos contados distintos: " + objeto.getId() + " e " + outra.objeto.getId();
        }
        return null;
    }

    private void exigirCompatibilidadeCom(Quantidade outra) {
        String problema = descreverIncompatibilidadeCom(outra);
        if (problema != null) {
            throw new QuantidadeIncompativelException(problema);
        }
    }

    private Quantidade comNumero(Numero novoNumero) {
        return new Quantidade(novoNumero, objeto, grandeza, unidade);
    }

    /**
     * Descricao textual delegada a realizacao do objeto e a unidade.
     *
     * <p>Contagens dispensam a unidade ("2 rosas brancas", nao "2 unidades de
     * rosas brancas"); dinheiro e massa a exibem ("18 reais"). A regra de
     * <em>quando</em> exibir e de apresentacao, mas a forma da palavra vem dos
     * objetos que a possuem.
     */
    public String descrever() {
        if (grandeza == GrandezaQuantitativa.CONTAGEM) {
            return objeto.getRealizacaoTextual().descrever(numero);
        }
        return numero + " " + unidade.flexionar(numero)
                + " de " + objeto.getRealizacaoTextual().flexionar(numero);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Quantidade)) {
            return false;
        }
        Quantidade outra = (Quantidade) obj;
        return numero.equals(outra.numero)
                && objeto.equals(outra.objeto)
                && grandeza == outra.grandeza
                && unidade == outra.unidade;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero, objeto, grandeza, unidade);
    }

    @Override
    public String toString() {
        return descrever();
    }
}
