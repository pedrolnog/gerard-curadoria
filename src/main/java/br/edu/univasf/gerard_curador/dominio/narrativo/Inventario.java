package br.edu.univasf.gerard_curador.dominio.narrativo;

import br.edu.univasf.gerard_curador.dominio.excecao.RetiradaImpossivelException;

import br.edu.univasf.gerard_curador.dominio.quantitativo.FamiliaObjeto;
import br.edu.univasf.gerard_curador.dominio.quantitativo.Numero;
import br.edu.univasf.gerard_curador.dominio.quantitativo.ObjetoContado;
import br.edu.univasf.gerard_curador.dominio.quantitativo.Quantidade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Quantidades possuidas: adiciona, retira e totaliza objetos da mesma familia.
 *
 * <p>Detem a autoridade sobre a fronteira "retirada e totalizacao". Responde a
 * pergunta 7 da apresentacao final: quem impede a retirada de uma quantidade
 * inexistente e o inventario, porque so ele conhece o conjunto do que se possui.
 * Quantidade sozinha sabe recusar um resultado negativo, mas nao sabe que o item
 * sequer existe na posse do participante.
 *
 * <p>Preserva a composicao do total, e nao apenas o numero 5: o inventario
 * inicial da avo guarda 2 rosas brancas e 3 rosas amarelas como entradas
 * separadas, e calcula o total por familia quando perguntado. E o que diferencia
 * este modelo do formulario plano, onde "estado_inicial = 5" perde a composicao.
 *
 * <p>Imutavel: adicionar e retirar devolvem novos inventarios. Assim o estado
 * inicial continua intacto depois de aplicadas as transformacoes, e a sequencia
 * narrativa pode exibir todos os momentos da historia.
 */
public final class Inventario {

    /** Chave: id do objeto contado. LinkedHashMap preserva a ordem curada. */
    private final Map<String, Quantidade> itens;

    private Inventario(Map<String, Quantidade> itens) {
        this.itens = Collections.unmodifiableMap(itens);
    }

    public static Inventario vazio() {
        return new Inventario(new LinkedHashMap<>());
    }

    /** Constroi um inventario a partir das quantidades curadas, na ordem informada. */
    public static Inventario com(Quantidade... quantidades) {
        Inventario inventario = vazio();
        for (Quantidade quantidade : quantidades) {
            inventario = inventario.adicionar(quantidade);
        }
        return inventario;
    }

    /**
     * Acrescenta uma quantidade, somando quando o objeto ja esta presente.
     *
     * <p>Quantidades de zero itens sao aceitas: representam explicitamente "a
     * avo ficou sem nenhuma rosa branca", informacao diferente de "a avo nunca
     * teve rosas brancas".
     */
    public Inventario adicionar(Quantidade quantidade) {
        if (quantidade == null) {
            throw new IllegalArgumentException("inventario exige quantidade para adicionar");
        }
        Map<String, Quantidade> novos = new LinkedHashMap<>(itens);
        String chave = quantidade.getObjeto().getId();
        Quantidade existente = novos.get(chave);
        novos.put(chave, existente == null ? quantidade : existente.somar(quantidade));
        return new Inventario(novos);
    }

    /**
     * Retira uma quantidade, recusando o que nao existe.
     *
     * <p>Duas recusas distintas, com diagnosticos distintos: o objeto nao consta
     * do inventario, ou consta em numero insuficiente. A excecao interrompe antes
     * de qualquer escrita, entao o inventario original permanece integro
     * (criterio "uma retirada impossivel produz diagnostico e nao corrompe o
     * estado").
     */
    public Inventario retirar(Quantidade quantidade) {
        if (quantidade == null) {
            throw new IllegalArgumentException("inventario exige quantidade para retirar");
        }
        String chave = quantidade.getObjeto().getId();
        Quantidade existente = itens.get(chave);
        if (existente == null) {
            throw new RetiradaImpossivelException(
                    "quantidade indisponivel para retirada: inventario nao possui " + chave);
        }
        Quantidade restante = existente.subtrair(quantidade);
        Map<String, Quantidade> novos = new LinkedHashMap<>(itens);
        novos.put(chave, restante);
        return new Inventario(novos);
    }

    /**
     * Verifica a viabilidade da retirada sem executa-la.
     *
     * <p>Permite que a validacao estrutural relate o problema em vez de
     * interromper o processamento das demais transformacoes.
     */
    public boolean comporta(Quantidade quantidade) {
        if (quantidade == null) {
            return false;
        }
        Quantidade existente = itens.get(quantidade.getObjeto().getId());
        return existente != null && existente.comportaRetiradaDe(quantidade);
    }

    /** Quantidade possuida de um objeto especifico, ou null quando ausente. */
    public Quantidade quantidadeDe(ObjetoContado objeto) {
        return objeto == null ? null : itens.get(objeto.getId());
    }

    /** Numero possuido de um objeto especifico; zero quando ausente. */
    public Numero numeroDe(ObjetoContado objeto) {
        Quantidade quantidade = quantidadeDe(objeto);
        return quantidade == null ? Numero.ZERO : quantidade.getNumero();
    }

    public boolean contem(ObjetoContado objeto) {
        return objeto != null && itens.containsKey(objeto.getId());
    }

    /** Itens na ordem em que foram curados. */
    public List<Quantidade> getItens() {
        return new ArrayList<>(itens.values());
    }

    /** Itens com numero maior que zero: o que efetivamente se possui agora. */
    public List<Quantidade> getItensPresentes() {
        List<Quantidade> presentes = new ArrayList<>();
        for (Quantidade quantidade : itens.values()) {
            if (!quantidade.ehZero()) {
                presentes.add(quantidade);
            }
        }
        return presentes;
    }

    public boolean estaVazio() {
        return getItensPresentes().isEmpty();
    }

    /**
     * Totaliza as variacoes de uma mesma familia.
     *
     * <p>Aqui mora a resposta de "o inventario inicial totaliza cinco rosas": o
     * inventario percorre suas entradas, seleciona as da familia rosa e soma os
     * numeros. Objetos de outras familias sao ignorados, nunca somados por
     * engano.
     *
     * <p>Soma os numeros, e nao as quantidades, justamente porque as entradas
     * referem objetos contados diferentes: Quantidade.somar recusaria a operacao,
     * e com razao. A autoridade para agrupar variacoes e do inventario, que
     * primeiro confirma a compatibilidade de medida entre elas.
     */
    public Numero totalDaFamilia(FamiliaObjeto familia) {
        if (familia == null) {
            throw new IllegalArgumentException("totalizacao exige familia");
        }
        Numero total = Numero.ZERO;
        Quantidade referencia = null;
        for (Quantidade quantidade : itens.values()) {
            if (!quantidade.getObjeto().pertenceAFamilia(familia)) {
                continue;
            }
            if (referencia == null) {
                referencia = quantidade;
            } else if (!referencia.possuiMesmaMedidaQue(quantidade)) {
                throw new IllegalArgumentException(
                        "familia " + familia.getNomeConceitual()
                                + " possui itens em medidas incompativeis: "
                                + referencia.descreverIncompatibilidadeCom(quantidade));
            }
            total = total.somar(quantidade.getNumero());
        }
        return total;
    }

    /** Total geral de itens contados, independente de familia. */
    public Numero totalGeral() {
        Numero total = Numero.ZERO;
        for (Quantidade quantidade : itens.values()) {
            total = total.somar(quantidade.getNumero());
        }
        return total;
    }

    /** Familias presentes, na ordem curada, sem repeticao. */
    public List<FamiliaObjeto> familiasPresentes() {
        List<FamiliaObjeto> familias = new ArrayList<>();
        for (Quantidade quantidade : itens.values()) {
            FamiliaObjeto familia = quantidade.getObjeto().getFamilia();
            if (!familias.contains(familia)) {
                familias.add(familia);
            }
        }
        return familias;
    }

    /**
     * Compara dois inventarios item a item.
     *
     * <p>Usada pela relacao estrutural para confrontar o estado calculado com o
     * estado final curado. Compara apenas os itens presentes: um inventario que
     * registra "0 rosas brancas" equivale a outro que nao menciona rosas brancas,
     * porque ambos afirmam a mesma posse.
     */
    public boolean possuiMesmoConteudoQue(Inventario outro) {
        if (outro == null) {
            return false;
        }
        List<Quantidade> meus = getItensPresentes();
        List<Quantidade> seus = outro.getItensPresentes();
        if (meus.size() != seus.size()) {
            return false;
        }
        for (Quantidade minha : meus) {
            Quantidade dele = outro.itens.get(minha.getObjeto().getId());
            if (dele == null || !dele.equals(minha)) {
                return false;
            }
        }
        return true;
    }

    /** Descricao dos itens presentes: "2 rosas brancas e 3 rosas amarelas". */
    public String descrever() {
        List<Quantidade> presentes = getItensPresentes();
        if (presentes.isEmpty()) {
            return "nada";
        }
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < presentes.size(); i++) {
            if (i > 0) {
                texto.append(i == presentes.size() - 1 ? " e " : ", ");
            }
            texto.append(presentes.get(i).descrever());
        }
        return texto.toString();
    }

    @Override
    public String toString() {
        return descrever();
    }
}
