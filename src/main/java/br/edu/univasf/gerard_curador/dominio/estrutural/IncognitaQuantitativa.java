package br.edu.univasf.gerard_curador.dominio.estrutural;

import br.edu.univasf.gerard_curador.dominio.quantitativo.Quantidade;

/**
 * Designacao estavel do papel originalmente desconhecido.
 *
 * <p>Responde a pergunta 5 da apresentacao final: por que a incognita nao pode
 * ser recuperada procurando um valor vazio.
 *
 * <p>Um sistema que deduz a incognita pela ausencia de valor comete dois erros.
 * O primeiro e temporal: assim que a situacao e resolvida, todos os papeis tem
 * valor, e a informacao "este era o desconhecido" desaparece. A situacao das
 * rosas resolvida ficaria indistinguivel de uma situacao onde tudo sempre foi
 * conhecido, e o valor pedagogico do enunciado se perderia. O segundo e de
 * ambiguidade: uma situacao pode ter varios papeis sem valor curado sem que
 * todos sejam a pergunta feita ao aluno.
 *
 * <p>Por isso a incognita e uma designacao explicita, informada pelo curador, que
 * permanece apos receber um valor. O metodo {@link #resolver} devolve uma nova
 * incognita que continua sabendo qual papel era desconhecido, agora com a
 * resposta anexada (criterios "a incognita original permanece identificada apos
 * receber um valor" e caso de teste 13).
 *
 * <p>Imutavel: resolver nao apaga a designacao, cria uma versao resolvida dela.
 */
public final class IncognitaQuantitativa {

    private final PapelQuantitativo papelOriginal;
    private final String descricaoDaPergunta;
    private final Quantidade valorResolvido;

    private IncognitaQuantitativa(PapelQuantitativo papelOriginal,
                                  String descricaoDaPergunta,
                                  Quantidade valorResolvido) {
        if (papelOriginal == null) {
            throw new IllegalArgumentException("incognita exige papel original designado");
        }
        if (descricaoDaPergunta == null || descricaoDaPergunta.isBlank()) {
            throw new IllegalArgumentException("incognita exige descricao da pergunta curada");
        }
        this.papelOriginal = papelOriginal;
        this.descricaoDaPergunta = descricaoDaPergunta;
        this.valorResolvido = valorResolvido;
    }

    /** Designa a incognita antes de qualquer resolucao. */
    public static IncognitaQuantitativa designar(PapelQuantitativo papelOriginal,
                                                 String descricaoDaPergunta) {
        return new IncognitaQuantitativa(papelOriginal, descricaoDaPergunta, null);
    }

    public PapelQuantitativo getPapelOriginal() {
        return papelOriginal;
    }

    public String getDescricaoDaPergunta() {
        return descricaoDaPergunta;
    }

    public Quantidade getValorResolvido() {
        return valorResolvido;
    }

    public boolean estaResolvida() {
        return valorResolvido != null;
    }

    /**
     * Anexa a resposta preservando a designacao original.
     *
     * <p>O papel continua sendo consultavel depois disso: e exatamente o ponto
     * da classe.
     */
    public IncognitaQuantitativa resolver(Quantidade valor) {
        if (valor == null) {
            throw new IllegalArgumentException("resolucao exige quantidade");
        }
        if (estaResolvida()) {
            throw new IllegalStateException(
                    "incognita de " + papelOriginal.getNomeExibido() + " ja foi resolvida");
        }
        return new IncognitaQuantitativa(papelOriginal, descricaoDaPergunta, valor);
    }

    /** Verdadeiro quando o papel consultado e o que foi originalmente desconhecido. */
    public boolean designa(PapelQuantitativo papel) {
        return papelOriginal == papel;
    }

    /**
     * Chave estavel do papel, no formato usado pela persistencia.
     *
     * <p>Espelha o campo "incognita": "papel.transformacaoFinal" do exemplo JSON
     * da especificacao.
     */
    public String chaveDoPapel() {
        return "papel." + papelOriginal.name();
    }

    /** "Qual e o total do estado final?" */
    public String descrever() {
        return descricaoDaPergunta;
    }

    /** Descricao que sempre revela o papel, mesmo apos resolvida. */
    public String descreverComPapel() {
        String base = descricaoDaPergunta + " [papel original: " + papelOriginal.getNomeExibido() + "]";
        return estaResolvida() ? base + " = " + valorResolvido.descrever() : base;
    }

    @Override
    public String toString() {
        return descreverComPapel();
    }
}
