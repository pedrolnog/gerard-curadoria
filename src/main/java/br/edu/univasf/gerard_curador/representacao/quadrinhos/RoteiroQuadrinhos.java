package br.edu.univasf.gerard_curador.representacao.quadrinhos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sequencia de quadros que realiza visualmente uma sequencia narrativa.
 *
 * <p>A especificacao pede de tres a cinco quadros. A situacao das rosas produz
 * quatro: estado inicial, primeira acao, segunda acao e estado final com a
 * pergunta.
 *
 * <p>Pertence inteiramente a representacao. Sabe contar quadros e organiza-los em
 * ordem; nao sabe somar rosas.
 */
public final class RoteiroQuadrinhos {

    private static final int MINIMO_DE_QUADROS = 3;
    private static final int MAXIMO_DE_QUADROS = 5;

    private final String idSituacao;
    private final String titulo;
    private final List<Quadro> quadros;

    public RoteiroQuadrinhos(String idSituacao, String titulo, List<Quadro> quadros) {
        if (idSituacao == null || idSituacao.isBlank()) {
            throw new IllegalArgumentException("roteiro exige id da situacao");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("roteiro exige titulo");
        }
        this.idSituacao = idSituacao;
        this.titulo = titulo;
        this.quadros = Collections.unmodifiableList(
                new ArrayList<>(quadros == null ? new ArrayList<>() : quadros));
    }

    public String getIdSituacao() {
        return idSituacao;
    }

    public String getTitulo() {
        return titulo;
    }

    public List<Quadro> getQuadros() {
        return new ArrayList<>(quadros);
    }

    public int quantidadeDeQuadros() {
        return quadros.size();
    }

    /** Verdadeiro quando o roteiro respeita a faixa de tres a cinco quadros. */
    public boolean respeitaFaixaDeQuadros() {
        return quadros.size() >= MINIMO_DE_QUADROS && quadros.size() <= MAXIMO_DE_QUADROS;
    }

    /** O quadro que contem a pergunta feita ao leitor. */
    public Quadro getQuadroComPergunta() {
        for (Quadro quadro : quadros) {
            if (quadro.possuiPergunta()) {
                return quadro;
            }
        }
        return null;
    }

    public Quadro getPrimeiroQuadro() {
        return quadros.isEmpty() ? null : quadros.get(0);
    }

    public Quadro getUltimoQuadro() {
        return quadros.isEmpty() ? null : quadros.get(quadros.size() - 1);
    }

    /** Roteiro completo em texto, um bloco por quadro. */
    public String descrever() {
        StringBuilder texto = new StringBuilder();
        texto.append("Roteiro: ").append(titulo).append(System.lineSeparator());
        texto.append("Situacao: ").append(idSituacao).append(System.lineSeparator());
        for (Quadro quadro : quadros) {
            texto.append(System.lineSeparator()).append(quadro.descrever())
                    .append(System.lineSeparator());
        }
        return texto.toString();
    }

    @Override
    public String toString() {
        return descrever();
    }
}
