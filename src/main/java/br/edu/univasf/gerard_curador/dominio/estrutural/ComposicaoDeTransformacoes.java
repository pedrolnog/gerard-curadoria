package br.edu.univasf.gerard_curador.dominio.estrutural;

import br.edu.univasf.gerard_curador.dominio.narrativo.SentidoTransformacao;
import br.edu.univasf.gerard_curador.dominio.quantitativo.Numero;
import br.edu.univasf.gerard_curador.dominio.quantitativo.Quantidade;
import br.edu.univasf.gerard_curador.dominio.validacao.ResultadoValidacao;
import br.edu.univasf.gerard_curador.dominio.validacao.TipoDiagnostico;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Arranjo de estado inicial, duas transformacoes ordenadas e estado final.
 *
 * <p>E a relacao que rege a situacao das rosas e, generalizando, toda situacao
 * do tipo "tinha X, aconteceu A, aconteceu B, ficou com Y". Cobre tres das
 * variacoes da matriz de generalizacao conforme qual papel seja a incognita:
 * estado final desconhecido, estado inicial desconhecido ou transformacao
 * desconhecida.
 *
 * <p>A equacao verificada, com o sinal de cada transformacao dado pelo seu
 * sentido curado:
 *
 * <pre>
 *   estado_inicial + (sinal1 * transformacao1) + (sinal2 * transformacao2) = estado_final
 * </pre>
 *
 * <p>Nas rosas: 5 - 1 - 1 = 3.
 *
 * <p>Tambem verifica a transformacao resultante quando o curador a informa: nas
 * situacoes de duas partidas, o enunciado declara o saldo total ("ao final das
 * duas partidas ele tinha ganhado 22"), e esse saldo precisa bater com a
 * composicao das duas parcelas. E o que o caso de teste 14 exige.
 *
 * <p>Ponto central da responsabilidade humana: quando a conta nao fecha, esta
 * classe registra ESTADO_CALCULADO_DIVERGENTE informando os dois valores. Ela
 * nao decide qual esta certo, nao ajusta o estado final e nao adivinha o que o
 * curador quis dizer.
 */
public final class ComposicaoDeTransformacoes extends RelacaoEstrutural {

    private final SentidoTransformacao sentidoPrimeira;
    private final SentidoTransformacao sentidoSegunda;
    private final SentidoTransformacao sentidoResultante;

    private ComposicaoDeTransformacoes(String id,
                                       Map<PapelQuantitativo, Quantidade> papeis,
                                       SentidoTransformacao sentidoPrimeira,
                                       SentidoTransformacao sentidoSegunda,
                                       SentidoTransformacao sentidoResultante) {
        super(id, papeis);
        this.sentidoPrimeira = sentidoPrimeira;
        this.sentidoSegunda = sentidoSegunda;
        this.sentidoResultante = sentidoResultante;
    }

    /** Montagem incremental: o curador informa apenas os papeis que a situacao declara. */
    public static Construtor construtor(String id) {
        return new Construtor(id);
    }

    @Override
    public String getCategoria() {
        return "COMPOSICAO_TRANSFORMACOES";
    }

    public SentidoTransformacao getSentidoPrimeira() {
        return sentidoPrimeira;
    }

    public SentidoTransformacao getSentidoSegunda() {
        return sentidoSegunda;
    }

    public SentidoTransformacao getSentidoResultante() {
        return sentidoResultante;
    }

    @Override
    protected ResultadoValidacao validarRegrasDaCategoria(ResultadoValidacao resultado) {
        resultado = validarComposicaoDasParcelas(resultado);
        resultado = validarPercursoDosEstados(resultado);
        return resultado;
    }

    /**
     * Confere a transformacao resultante contra a soma das duas parcelas.
     *
     * <p>So executa quando os tres papeis foram curados: a situacao "Joao ganhou
     * 12 na segunda partida e 22 ao todo" declara resultante e segunda, e pede a
     * primeira. Nesse caso nao ha o que conferir, apenas o que resolver.
     */
    private ResultadoValidacao validarComposicaoDasParcelas(ResultadoValidacao resultado) {
        Quantidade primeira = quantidadeDoPapel(PapelQuantitativo.TRANSFORMACAO_PRIMEIRA);
        Quantidade segunda = quantidadeDoPapel(PapelQuantitativo.TRANSFORMACAO_SEGUNDA);
        Quantidade resultante = quantidadeDoPapel(PapelQuantitativo.TRANSFORMACAO_RESULTANTE);
        if (primeira == null || segunda == null || resultante == null) {
            return resultado;
        }
        int efeitoParcelas = efeitoDe(primeira, sentidoPrimeira) + efeitoDe(segunda, sentidoSegunda);
        int efeitoResultante = efeitoDe(resultante, sentidoResultante);
        if (efeitoParcelas != efeitoResultante) {
            return resultado.acrescentar(
                    TipoDiagnostico.ESTADO_CALCULADO_DIVERGENTE,
                    "transformacao resultante",
                    "composicao das parcelas produz saldo " + efeitoParcelas
                            + ", enquanto a transformacao resultante curada declara "
                            + efeitoResultante
                            + "; o valor curado foi preservado");
        }
        return resultado;
    }

    /**
     * Confere o percurso do estado inicial ao estado final.
     *
     * <p>Usa as parcelas quando existem; na falta delas, cai para a resultante.
     * Se o estado inicial ou o final nao foram curados, nao ha percurso a
     * conferir: e justamente a situacao em que um deles e a incognita.
     */
    private ResultadoValidacao validarPercursoDosEstados(ResultadoValidacao resultado) {
        Quantidade inicial = quantidadeDoPapel(PapelQuantitativo.ESTADO_INICIAL);
        Quantidade finalCurado = quantidadeDoPapel(PapelQuantitativo.ESTADO_FINAL);
        if (inicial == null || finalCurado == null) {
            return resultado;
        }
        Integer efeitoTotal = calcularEfeitoTotal();
        if (efeitoTotal == null) {
            return resultado;
        }
        int calculado = inicial.getValor() + efeitoTotal;
        if (calculado < 0) {
            resultado = resultado.acrescentar(
                    TipoDiagnostico.QUANTIDADE_INDISPONIVEL_PARA_RETIRADA,
                    "estado final",
                    "as transformacoes retiram mais do que o estado inicial possui: "
                            + inicial.getValor() + " com saldo " + efeitoTotal);
        }
        if (calculado != finalCurado.getValor()) {
            resultado = resultado.acrescentar(
                    TipoDiagnostico.ESTADO_CALCULADO_DIVERGENTE,
                    "estado final curado",
                    "calculado " + calculado + " a partir do estado inicial "
                            + inicial.getValor() + " com saldo " + efeitoTotal
                            + ", mas o curador informou " + finalCurado.getValor()
                            + "; o valor curado foi preservado");
        }
        return resultado;
    }

    /** Saldo das transformacoes, ou null quando nao ha parcelas nem resultante curadas. */
    private Integer calcularEfeitoTotal() {
        Quantidade primeira = quantidadeDoPapel(PapelQuantitativo.TRANSFORMACAO_PRIMEIRA);
        Quantidade segunda = quantidadeDoPapel(PapelQuantitativo.TRANSFORMACAO_SEGUNDA);
        if (primeira != null && segunda != null) {
            return efeitoDe(primeira, sentidoPrimeira) + efeitoDe(segunda, sentidoSegunda);
        }
        if (primeira != null && segunda == null) {
            return efeitoDe(primeira, sentidoPrimeira);
        }
        Quantidade resultante = quantidadeDoPapel(PapelQuantitativo.TRANSFORMACAO_RESULTANTE);
        if (resultante != null) {
            return efeitoDe(resultante, sentidoResultante);
        }
        return null;
    }

    private static int efeitoDe(Quantidade quantidade, SentidoTransformacao sentido) {
        SentidoTransformacao efetivo = sentido == null ? SentidoTransformacao.ACRESCIMO : sentido;
        return quantidade.getValor() * efetivo.getFator();
    }

    /**
     * Resolve o papel desconhecido a partir dos demais.
     *
     * <p>Resolver e diferente de corrigir: aqui a relacao calcula um valor que o
     * curador declarou desconhecido, e nao substitui um valor que ele informou.
     * A incognita continua sabendo qual papel era o desconhecido.
     *
     * @return o numero que ocupa o papel pedido, ou null quando os dados curados
     *         nao permitem determina-lo.
     */
    @Override
    public Numero resolver(PapelQuantitativo papelDesconhecido) {
        if (papelDesconhecido == null || possuiPapel(papelDesconhecido)) {
            return null;
        }
        Quantidade inicial = quantidadeDoPapel(PapelQuantitativo.ESTADO_INICIAL);
        Quantidade finalCurado = quantidadeDoPapel(PapelQuantitativo.ESTADO_FINAL);
        Quantidade primeira = quantidadeDoPapel(PapelQuantitativo.TRANSFORMACAO_PRIMEIRA);
        Quantidade segunda = quantidadeDoPapel(PapelQuantitativo.TRANSFORMACAO_SEGUNDA);
        Quantidade resultante = quantidadeDoPapel(PapelQuantitativo.TRANSFORMACAO_RESULTANTE);

        switch (papelDesconhecido) {
            case ESTADO_FINAL: {
                Integer efeito = calcularEfeitoTotal();
                if (inicial == null || efeito == null) {
                    return null;
                }
                return Numero.de(inicial.getValor() + efeito);
            }
            case ESTADO_INICIAL: {
                Integer efeito = calcularEfeitoTotal();
                if (finalCurado == null || efeito == null) {
                    return null;
                }
                return Numero.de(finalCurado.getValor() - efeito);
            }
            case TRANSFORMACAO_RESULTANTE: {
                if (primeira != null && segunda != null) {
                    return Numero.de(efeitoDe(primeira, sentidoPrimeira)
                            + efeitoDe(segunda, sentidoSegunda));
                }
                if (inicial != null && finalCurado != null) {
                    return Numero.de(finalCurado.getValor() - inicial.getValor());
                }
                return null;
            }
            case TRANSFORMACAO_PRIMEIRA: {
                if (resultante != null && segunda != null) {
                    return Numero.de(efeitoDe(resultante, sentidoResultante)
                            - efeitoDe(segunda, sentidoSegunda));
                }
                if (inicial != null && finalCurado != null && segunda != null) {
                    return Numero.de(finalCurado.getValor() - inicial.getValor()
                            - efeitoDe(segunda, sentidoSegunda));
                }
                return null;
            }
            case TRANSFORMACAO_SEGUNDA: {
                if (resultante != null && primeira != null) {
                    return Numero.de(efeitoDe(resultante, sentidoResultante)
                            - efeitoDe(primeira, sentidoPrimeira));
                }
                if (inicial != null && finalCurado != null && primeira != null) {
                    return Numero.de(finalCurado.getValor() - inicial.getValor()
                            - efeitoDe(primeira, sentidoPrimeira));
                }
                return null;
            }
            default:
                return null;
        }
    }

    /** Montagem passo a passo, para que a situacao declare apenas os papeis que possui. */
    public static final class Construtor {

        private final String id;
        private final Map<PapelQuantitativo, Quantidade> papeis = new LinkedHashMap<>();
        private SentidoTransformacao sentidoPrimeira = SentidoTransformacao.ACRESCIMO;
        private SentidoTransformacao sentidoSegunda = SentidoTransformacao.ACRESCIMO;
        private SentidoTransformacao sentidoResultante = SentidoTransformacao.ACRESCIMO;

        private Construtor(String id) {
            this.id = id;
        }

        public Construtor comEstadoInicial(Quantidade quantidade) {
            papeis.put(PapelQuantitativo.ESTADO_INICIAL, quantidade);
            return this;
        }

        public Construtor comEstadoIntermediario(Quantidade quantidade) {
            papeis.put(PapelQuantitativo.ESTADO_INTERMEDIARIO, quantidade);
            return this;
        }

        public Construtor comEstadoFinal(Quantidade quantidade) {
            papeis.put(PapelQuantitativo.ESTADO_FINAL, quantidade);
            return this;
        }

        public Construtor comPrimeiraTransformacao(Quantidade quantidade,
                                                   SentidoTransformacao sentido) {
            papeis.put(PapelQuantitativo.TRANSFORMACAO_PRIMEIRA, quantidade);
            this.sentidoPrimeira = sentido;
            return this;
        }

        public Construtor comSegundaTransformacao(Quantidade quantidade,
                                                  SentidoTransformacao sentido) {
            papeis.put(PapelQuantitativo.TRANSFORMACAO_SEGUNDA, quantidade);
            this.sentidoSegunda = sentido;
            return this;
        }

        public Construtor comTransformacaoResultante(Quantidade quantidade,
                                                     SentidoTransformacao sentido) {
            papeis.put(PapelQuantitativo.TRANSFORMACAO_RESULTANTE, quantidade);
            this.sentidoResultante = sentido;
            return this;
        }

        public ComposicaoDeTransformacoes construir() {
            return new ComposicaoDeTransformacoes(
                    id, papeis, sentidoPrimeira, sentidoSegunda, sentidoResultante);
        }
    }
}
