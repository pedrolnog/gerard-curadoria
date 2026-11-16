package br.edu.univasf.gerard_curador.persistencia;

/**
 * Excecao lancada quando um documento JSON e invalido ou omite uma referencia obrigatoria.
 *
 * <p>Pertence exclusivamente a camada de persistencia: o dominio nao conhece JSON nem Gson.
 */
public class ErroDeLeituraJson extends RuntimeException {

    /**
     * Cria o erro informando qual campo ou referencia falhou na leitura.
     *
     * @param mensagem descricao do campo ou referencia invalida
     */
    public ErroDeLeituraJson(String mensagem) {
        super(mensagem);
    }

    /**
     * Cria o erro preservando a causa original da falha de analise.
     *
     * @param mensagem descricao do campo ou referencia invalida
     * @param causa    excecao original
     */
    public ErroDeLeituraJson(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
