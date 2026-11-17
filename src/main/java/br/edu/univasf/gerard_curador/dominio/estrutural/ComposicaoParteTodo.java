package br.edu.univasf.gerard_curador.dominio.estrutural;

import br.edu.univasf.gerard_curador.dominio.quantitativo.Numero;
import br.edu.univasf.gerard_curador.dominio.quantitativo.Quantidade;
import br.edu.univasf.gerard_curador.dominio.validacao.ResultadoValidacao;
import br.edu.univasf.gerard_curador.dominio.validacao.TipoDiagnostico;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Arranjo de partes que compoem um todo, sem transformacao no tempo.
 *
 * <p>Cobre a variacao "parte desconhecida" da matriz de generalizacao: "Paulo
 * tem 17 bolas; 11 sao azuis. Quantas sao vermelhas?". Tambem cobre as
 * composicoes diretas do documento historico, como "6 bolas de vidro e 8 de aco,
 * quantas ao todo?".
 *
 * <p>Distingue-se da composicao de transformacoes por uma diferenca conceitual
 * que importa ao dominio: aqui nao ha tempo. As bolas azuis e vermelhas de Paulo
 * coexistem; nenhuma virou a outra. Modelar isso como transformacao produziria
 * uma narrativa falsa, com eventos que o enunciado nao descreve.
 *
 * <p>Equacao verificada: a soma das partes e igual ao todo. Aceita qualquer
 * numero de partes, atendendo a situacoes como as quatro cores de borrachas da
 * Marina.
 *
 * <p>Como as demais relacoes: diagnostica a divergencia, nunca corrige o valor
 * curado.
 */
public final class ComposicaoParteTodo extends RelacaoEstrutural {

    /**
     * As partes ficam numa lista propria porque o mapa de papeis, indexado por
     * PapelQuantitativo, comporta uma unica entrada PARTE. Quatro cores de
     * borracha sao quatro partes distintas do mesmo todo.
     */
    /**
     * As partes ficam numa lista propria porque o mapa de papeis da superclasse
     * comporta uma unica quantidade por papel. O todo NAO e duplicado aqui: vive
     * so no mapa herdado e e lido por {@link #getTodo()}.
     */
    private final List<Quantidade> partes;

    private ComposicaoParteTodo(String id, List<Quantidade> partes, Quantidade todo) {
        super(id, montarPapeis(partes, todo));
        this.partes = Collections.unmodifiableList(new ArrayList<>(partes));
    }

    /**
     * Registra a primeira parte e o todo no mapa herdado.
     *
     * <p>O mapa serve a consulta generica por papel e a validacao de medidas; a
     * lista completa de partes permanece acessivel por getPartes().
     */
    private static Map<PapelQuantitativo, Quantidade> montarPapeis(List<Quantidade> partes,
                                                                   Quantidade todo) {
        Map<PapelQuantitativo, Quantidade> papeis = new LinkedHashMap<>();
        if (partes != null && !partes.isEmpty()) {
            papeis.put(PapelQuantitativo.PARTE, partes.get(0));
        }
        if (todo != null) {
            papeis.put(PapelQuantitativo.TODO, todo);
        }
        return papeis;
    }

    public static Construtor construtor(String id) {
        return new Construtor(id);
    }

    @Override
    public String getCategoria() {
        return "COMPOSICAO_PARTE_TODO";
    }

    public List<Quantidade> getPartes() {
        return new ArrayList<>(partes);
    }

    public Quantidade getTodo() {
        return quantidadeDoPapel(PapelQuantitativo.TODO);
    }

    @Override
    protected ResultadoValidacao validarRegrasDaCategoria(ResultadoValidacao resultado) {
        resultado = validarMedidasDasPartes(resultado);
        if (getTodo() == null || partes.isEmpty()) {
            return resultado;
        }
        int somaDasPartes = 0;
        for (Quantidade parte : partes) {
            somaDasPartes += parte.getValor();
        }
        if (somaDasPartes != getTodo().getValor()) {
            resultado = resultado.acrescentar(
                    TipoDiagnostico.ESTADO_CALCULADO_DIVERGENTE,
                    "todo curado",
                    "as partes somam " + somaDasPartes
                            + ", mas o todo curado declara " + getTodo().getValor()
                            + "; o valor curado foi preservado");
        }
        return resultado;
    }

    /** Todas as partes precisam compartilhar grandeza e unidade entre si e com o todo. */
    private ResultadoValidacao validarMedidasDasPartes(ResultadoValidacao resultado) {
        Quantidade referencia = getTodo() != null ? getTodo() : (partes.isEmpty() ? null : partes.get(0));
        if (referencia == null) {
            return resultado;
        }
        for (Quantidade parte : partes) {
            if (!referencia.possuiMesmaMedidaQue(parte)) {
                resultado = resultado.acrescentar(
                        TipoDiagnostico.GRANDEZAS_OU_UNIDADES_INCOMPATIVEIS,
                        getId(),
                        "parte " + parte.descrever() + ": "
                                + referencia.descreverIncompatibilidadeCom(parte));
            }
        }
        return resultado;
    }

    /**
     * Determina o todo a partir das partes, ou a parte que falta a partir do todo.
     *
     * <p>Para resolver uma parte ausente, todas as demais precisam estar curadas:
     * com duas partes desconhecidas o sistema so poderia adivinhar, e adivinhar e
     * o que este projeto proibe.
     *
     * @param quantidadeDePartesEsperadas total de partes que a situacao declara,
     *                                    usado para saber se falta exatamente uma.
     */
    public Numero resolverParteAusente(int quantidadeDePartesEsperadas) {
        if (getTodo() == null || partes.size() != quantidadeDePartesEsperadas - 1) {
            return null;
        }
        int somaConhecida = 0;
        for (Quantidade parte : partes) {
            somaConhecida += parte.getValor();
        }
        return Numero.de(getTodo().getValor() - somaConhecida);
    }

    /** Soma das partes curadas, quando o todo e a incognita. */
    /** Resolve o papel pedido: o todo pela soma das partes, ou a parte que falta. */
    @Override
    public Numero resolver(PapelQuantitativo papelDesconhecido) {
        if (papelDesconhecido == null) {
            return null;
        }
        return switch (papelDesconhecido) {
            case TODO -> resolverTodo();
            case PARTE -> resolverParteAusente(partes.size() + 1);
            default -> null;
        };
    }

    public Numero resolverTodo() {
        if (partes.isEmpty()) {
            return null;
        }
        int soma = 0;
        for (Quantidade parte : partes) {
            soma += parte.getValor();
        }
        return Numero.de(soma);
    }

    public static final class Construtor {

        private final String id;
        private final List<Quantidade> partes = new ArrayList<>();
        private Quantidade todo;

        private Construtor(String id) {
            this.id = id;
        }

        public Construtor comParte(Quantidade parte) {
            if (parte != null) {
                partes.add(parte);
            }
            return this;
        }

        public Construtor comTodo(Quantidade todo) {
            this.todo = todo;
            return this;
        }

        public ComposicaoParteTodo construir() {
            return new ComposicaoParteTodo(id, partes, todo);
        }
    }
}
