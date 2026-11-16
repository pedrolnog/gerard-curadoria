package br.edu.univasf.gerard_curador.dominio.narrativo;

import br.edu.univasf.gerard_curador.dominio.quantitativo.Quantidade;

/**
 * Direcao do efeito de uma transformacao sobre o inventario do possuidor.
 *
 * <p>Vocabulario fechado. Existe para que Quantidade jamais precise carregar
 * numero negativo: "deu 1 rosa" e uma quantidade positiva de 1 com sentido
 * RETIRADA, e nao uma quantidade de menos 1. Essa separacao mantem a invariante
 * "quantidade nunca e negativa" enquanto ainda permite representar perdas.
 *
 * <p>O sentido tambem e o que a relacao estrutural usa para compor duas
 * transformacoes numa transformacao resultante: ganhar 12 e depois perder 5
 * resulta em ganhar 7, e esse calculo depende de saber o sentido de cada parcela.
 */
public enum SentidoTransformacao {

    /** O possuidor passa a ter mais: ganhou, comprou, recebeu, chegaram. */
    ACRESCIMO("acrescimo", 1) {
        @Override
        public EstadoDePosse aplicar(EstadoDePosse estado, Quantidade quantidade, MarcadorTemporal momento) {
            return estado.acrescentar(quantidade, momento);
        }

        @Override
        public boolean podeSerAplicado(EstadoDePosse estado, Quantidade quantidade) {
            return true;
        }
    },

    /** O possuidor passa a ter menos: deu, gastou, perdeu, comeram, foram tiradas. */
    RETIRADA("retirada", -1) {
        @Override
        public EstadoDePosse aplicar(EstadoDePosse estado, Quantidade quantidade, MarcadorTemporal momento) {
            return estado.retirar(quantidade, momento);
        }

        @Override
        public boolean podeSerAplicado(EstadoDePosse estado, Quantidade quantidade) {
            return estado.comporta(quantidade);
        }
    };

    private final String nomeExibido;
    private final int fator;

    SentidoTransformacao(String nomeExibido, int fator) {
        this.nomeExibido = nomeExibido;
        this.fator = fator;
    }

    /**
     * Aplica o efeito deste sentido sobre um estado de posse, produzindo o
     * estado seguinte.
     *
     * <p>Metodo abstrato com um corpo por constante: quem aplica uma
     * transformacao nao pergunta "e acrescimo ou retirada?" — apenas delega ao
     * sentido, e a ligacao dinamica escolhe o comportamento (polimorfismo no
     * lugar de if/switch).
     */
    public abstract EstadoDePosse aplicar(EstadoDePosse estado, Quantidade quantidade, MarcadorTemporal momento);

    /** Consulta previa, para a validacao relatar o problema sem interromper. */
    public abstract boolean podeSerAplicado(EstadoDePosse estado, Quantidade quantidade);

    public String getNomeExibido() {
        return nomeExibido;
    }

    /**
     * Multiplicador do efeito: +1 para acrescimo, -1 para retirada.
     *
     * <p>Permite que a relacao estrutural componha transformacoes de sentidos
     * diferentes sem uma cadeia de condicionais.
     */
    public int getFator() {
        return fator;
    }

    public SentidoTransformacao inverter() {
        return this == ACRESCIMO ? RETIRADA : ACRESCIMO;
    }

    public boolean ehAcrescimo() {
        return this == ACRESCIMO;
    }

    public boolean ehRetirada() {
        return this == RETIRADA;
    }
}
