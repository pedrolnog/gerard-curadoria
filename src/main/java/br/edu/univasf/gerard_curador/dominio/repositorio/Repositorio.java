package br.edu.univasf.gerard_curador.dominio.repositorio;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de armazenamento de objetos do dominio (inversao de dependencia).
 *
 * <p>A interface mora no dominio e nao sabe nada de JSON, arquivos ou bibliotecas:
 * quem depende dela (aplicacao, tela, testes) nao muda se a implementacao trocar
 * de JSON para banco de dados. A implementacao concreta fica na camada de
 * persistencia, que depende do dominio, e nunca o contrario.
 *
 * <p>Generica em {@code T} (o objeto guardado) e {@code ID} (sua identidade).
 *
 * @param <T>  tipo do objeto de dominio
 * @param <ID> tipo do identificador
 */
public interface Repositorio<T, ID> {

    /** Grava o objeto, substituindo a versao anterior de mesma identidade. */
    void salvar(T objeto);

    /** Recupera o objeto exatamente como foi gravado, sem recalcular nada. */
    Optional<T> buscarPorId(ID id);

    /** Todos os objetos gravados. */
    List<T> listarTodos();

    /** Remove o objeto; devolve falso quando ele nao existia. */
    boolean remover(ID id);
}
