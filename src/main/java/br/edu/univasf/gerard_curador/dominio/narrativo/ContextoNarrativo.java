package br.edu.univasf.gerard_curador.dominio.narrativo;

import java.util.Objects;

/**
 * Local, situacao e momento em que a historia acontece.
 *
 * <p>Entidade mutavel por setters validados.
 *
 * <p>Representa feira, jogo, colecao, ontem, hoje ou segunda partida sem inferir
 * pelo texto. A especificacao proibe deducao a partir do enunciado: o sistema
 * nao le "foi fazer compras na feira" e conclui que o local e a feira. O curador
 * declara.
 *
 * <p>Os tres campos respondem perguntas diferentes:
 * <ul>
 *   <li><strong>local</strong>: onde ("na feira", "em casa", "na escola");
 *   <li><strong>situacao</strong>: a moldura da atividade ("colecao de
 *       figurinhas", "jogo de bafo", "compras");
 *   <li><strong>momento</strong>: a referencia temporal geral da historia
 *       ("hoje", "mes passado"), distinta dos MarcadorTemporal que ordenam cada
 *       transformacao dentro dela.
 * </ul>
 *
 * <p>Todos sao opcionais: muitas situacoes historicas nao informam o local, e
 * inventar um seria exatamente a inferencia que o projeto proibe.
 */
public final class ContextoNarrativo {

    private String local;
    private String situacao;
    private String momento;

    public ContextoNarrativo(String local, String situacao, String momento) {
        this.local = normalizar(local);
        this.situacao = normalizar(situacao);
        this.momento = normalizar(momento);
    }

    /** Contexto ausente: a situacao curada nao informou local, moldura nem momento. */
    public static ContextoNarrativo naoInformado() {
        return new ContextoNarrativo(null, null, null);
    }

    private static String normalizar(String valor) {
        return valor == null || valor.isBlank() ? null : valor;
    }

    public String getLocal() {
        return local;
    }

    /**
     * Altera o local onde a historia acontece.
     */
    public void setLocal(String local) {
        this.local = normalizar(local);
    }

    public String getSituacao() {
        return situacao;
    }

    /**
     * Altera a situacao da atividade.
     */
    public void setSituacao(String situacao) {
        this.situacao = normalizar(situacao);
    }

    public String getMomento() {
        return momento;
    }

    /**
     * Altera o momento temporal da historia.
     */
    public void setMomento(String momento) {
        this.momento = normalizar(momento);
    }

    public boolean possuiLocal() {
        return local != null;
    }

    public boolean possuiSituacao() {
        return situacao != null;
    }

    public boolean possuiMomento() {
        return momento != null;
    }

    public boolean estaVazio() {
        return local == null && situacao == null && momento == null;
    }

    /**
     * Legenda de ambientacao para o primeiro quadro do roteiro.
     *
     * <p>Devolve null quando nada foi curado, para que o gerador de roteiro
     * simplesmente omita a legenda em vez de exibir texto inventado.
     */
    public String descrever() {
        if (estaVazio()) {
            return null;
        }
        StringBuilder texto = new StringBuilder();
        if (situacao != null) {
            texto.append(situacao);
        }
        if (local != null) {
            texto.append(texto.length() > 0 ? ", " : "").append(local);
        }
        if (momento != null) {
            texto.append(texto.length() > 0 ? ", " : "").append(momento);
        }
        return texto.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ContextoNarrativo)) {
            return false;
        }
        ContextoNarrativo outro = (ContextoNarrativo) obj;
        return Objects.equals(local, outro.local)
                && Objects.equals(situacao, outro.situacao)
                && Objects.equals(momento, outro.momento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(local, situacao, momento);
    }

    @Override
    public String toString() {
        String descricao = descrever();
        return descricao == null ? "contexto nao informado" : descricao;
    }
}
