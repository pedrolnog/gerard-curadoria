package br.edu.univasf.gerard_curador.dominio.excecao;

/**
 * Tentativa de retirar mais do que existe, como retirar tres rosas brancas
 * quando existem duas (caso de teste 5). O estado de origem nao e alterado.
 */
public final class RetiradaImpossivelException extends ViolacaoDeDominioException {

    private static final long serialVersionUID = 1L;

    public RetiradaImpossivelException(String motivo) {
        super(motivo);
    }
}
