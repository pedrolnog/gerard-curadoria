package br.edu.univasf.gerard_curador.dominio.excecao;

/**
 * Tentativa de operar quantidades de grandezas, unidades ou objetos diferentes,
 * como somar rosas com bonecas ou reais com quilogramas (secao 10 e caso 15).
 */
public final class QuantidadeIncompativelException extends ViolacaoDeDominioException {

    private static final long serialVersionUID = 1L;

    public QuantidadeIncompativelException(String motivo) {
        super(motivo);
    }
}
