package br.edu.univasf.gerard_curador.representacao.quadrinhos;

import java.util.Objects;

/**
 * Elemento de fala ou pensamento dentro de um quadro.
 *
 * <p>Pertence ao sistema de representacao: conhece a sintaxe dos quadrinhos
 * (fala, pensamento, narracao), nao a aritmetica das estruturas aditivas.
 *
 * <p>O texto chega pronto do gerador, que o obteve dos objetos do dominio. Este
 * objeto nao calcula nem revalida nada; ele apenas sabe que um balao tem um tipo,
 * um emissor e um conteudo.
 */
public final class Balao {

    /** Tipos de balao que a linguagem dos quadrinhos distingue. */
    public enum Tipo {

        /** Contorno continuo: o personagem esta falando. */
        FALA("fala"),

        /** Contorno em nuvem: o personagem esta pensando. */
        PENSAMENTO("pensamento"),

        /** Caixa retangular: voz do narrador, fora da cena. */
        NARRACAO("narracao"),

        /** Balao da pergunta feita ao leitor, no quadro final. */
        PERGUNTA("pergunta");

        private final String nomeExibido;

        Tipo(String nomeExibido) {
            this.nomeExibido = nomeExibido;
        }

        public String getNomeExibido() {
            return nomeExibido;
        }
    }

    private final Tipo tipo;
    private final String emissor;
    private final String texto;

    public Balao(Tipo tipo, String emissor, String texto) {
        if (tipo == null) {
            throw new IllegalArgumentException("balao exige tipo");
        }
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("balao exige texto");
        }
        this.tipo = tipo;
        this.emissor = emissor;
        this.texto = texto;
    }

    /** Balao de narracao: sem emissor, porque o narrador nao esta na cena. */
    public static Balao narracao(String texto) {
        return new Balao(Tipo.NARRACAO, null, texto);
    }

    public static Balao fala(String emissor, String texto) {
        return new Balao(Tipo.FALA, emissor, texto);
    }

    public static Balao pergunta(String texto) {
        return new Balao(Tipo.PERGUNTA, null, texto);
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getEmissor() {
        return emissor;
    }

    public String getTexto() {
        return texto;
    }

    public boolean possuiEmissor() {
        return emissor != null && !emissor.isBlank();
    }

    public String descrever() {
        return possuiEmissor()
                ? "[" + tipo.getNomeExibido() + "] " + emissor + ": " + texto
                : "[" + tipo.getNomeExibido() + "] " + texto;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Balao)) {
            return false;
        }
        Balao outro = (Balao) obj;
        return tipo == outro.tipo
                && Objects.equals(emissor, outro.emissor)
                && texto.equals(outro.texto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipo, emissor, texto);
    }

    @Override
    public String toString() {
        return descrever();
    }
}
