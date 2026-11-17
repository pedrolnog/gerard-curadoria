package br.edu.univasf.gerard_curador.dominio.excecao;

/**
 * Raiz das excecoes que sinalizam a tentativa de criar um estado que o dominio
 * proibe (uma invariante quebrada).
 *
 * <p>E abstrata porque ninguem lanca uma "violacao generica": cada subclasse
 * nomeia a regra violada, e quem trata pode capturar a familia inteira
 * (polimorfismo de excecoes) ou apenas o caso especifico.
 *
 * <p>Estende {@link IllegalArgumentException} porque, do ponto de vista de quem
 * chama, o argumento recebido e que torna a operacao impossivel.
 *
 * <p>Excecoes interrompem uma operacao aritmetica invalida. Problemas de
 * curadoria (dados incoerentes informados pelo curador) NAO usam excecoes:
 * viram diagnosticos em {@code ResultadoValidacao}, sem interromper nada.
 */
public abstract class ViolacaoDeDominioException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    protected ViolacaoDeDominioException(String mensagem) {
        super(mensagem);
    }
}
