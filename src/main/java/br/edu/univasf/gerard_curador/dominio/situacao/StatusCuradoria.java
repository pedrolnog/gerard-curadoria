package br.edu.univasf.gerard_curador.dominio.situacao;

/**
 * Estagio de revisao humana de uma situacao.
 *
 * <p>Implementa a regra da secao 8 da especificacao: o status
 * CANDIDATA_NAO_CURADA permanece ate que professor ou pesquisador revise a
 * estrutura, e a aplicacao pode apontar divergencias mas nao promove
 * automaticamente uma candidata ao catalogo validado.
 *
 * <p>Por isso a promocao exige um metodo chamado explicitamente com a identidade
 * de quem revisou: nao existe caminho pelo qual a validacao bem sucedida promova
 * a situacao sozinha. Validar e uma verificacao tecnica; curar e um ato humano.
 */
public enum StatusCuradoria {

    /** Estado inicial de toda situacao: estrutura montada, revisao humana pendente. */
    CANDIDATA_NAO_CURADA("candidata nao curada"),

    /** Revisada e aprovada por professor ou pesquisador. */
    VALIDADA("validada"),

    /** Revisada e recusada: fica registrada com o motivo, nao e apagada. */
    RECUSADA("recusada");

    private final String nomeExibido;

    StatusCuradoria(String nomeExibido) {
        this.nomeExibido = nomeExibido;
    }

    public String getNomeExibido() {
        return nomeExibido;
    }

    public boolean aguardaRevisao() {
        return this == CANDIDATA_NAO_CURADA;
    }

    public boolean foiRevisada() {
        return this != CANDIDATA_NAO_CURADA;
    }
}
