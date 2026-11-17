package br.edu.univasf.gerard_curador.representacao.quadrinhos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Um quadro do roteiro: numero, legenda, cena e baloes.
 *
 * <p>Unidade da linguagem dos quadrinhos. Corresponde as quatro linhas da tabela
 * "conteudo minimo" da especificacao: estado inicial, primeira acao, segunda acao
 * e estado final com a pergunta.
 *
 * <p>Nao valida a operacao matematica: quando o gerador lhe entrega a legenda
 * "Vovo permanece com 1 rosa branca e 2 amarelas", o quadro a exibe sem conferir
 * a subtracao. Essa conferencia ja aconteceu no dominio, e repeti-la aqui
 * duplicaria a regra em duas camadas.
 */
public final class Quadro {

    private final int numero;
    private final String titulo;
    private final String legenda;
    private final DescritorCena cena;
    private final List<Balao> baloes;

    private Quadro(int numero, String titulo, String legenda,
                   DescritorCena cena, List<Balao> baloes) {
        if (numero < 1) {
            throw new IllegalArgumentException("quadro exige numero a partir de 1");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("quadro exige titulo");
        }
        this.numero = numero;
        this.titulo = titulo;
        this.legenda = legenda;
        this.cena = cena;
        this.baloes = Collections.unmodifiableList(new ArrayList<>(baloes));
    }

    public static Construtor construtor(int numero, String titulo) {
        return new Construtor(numero, titulo);
    }

    public int getNumero() {
        return numero;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getLegenda() {
        return legenda;
    }

    public DescritorCena getCena() {
        return cena;
    }

    public List<Balao> getBaloes() {
        return new ArrayList<>(baloes);
    }

    public boolean possuiLegenda() {
        return legenda != null && !legenda.isBlank();
    }

    public boolean possuiCena() {
        return cena != null;
    }

    /** Verdadeiro quando o quadro contem a pergunta feita ao leitor. */
    public boolean possuiPergunta() {
        for (Balao balao : baloes) {
            if (balao.getTipo() == Balao.Tipo.PERGUNTA) {
                return true;
            }
        }
        return false;
    }

    public String descrever() {
        StringBuilder texto = new StringBuilder();
        texto.append("Quadro ").append(numero).append(" - ").append(titulo);
        if (possuiLegenda()) {
            texto.append(System.lineSeparator()).append("  legenda: ").append(legenda);
        }
        if (possuiCena()) {
            texto.append(System.lineSeparator()).append("  cena: ").append(cena.descrever());
        }
        for (Balao balao : baloes) {
            texto.append(System.lineSeparator()).append("  ").append(balao.descrever());
        }
        return texto.toString();
    }

    @Override
    public String toString() {
        return descrever();
    }

    public static final class Construtor {

        private final int numero;
        private final String titulo;
        private String legenda;
        private DescritorCena cena;
        private final List<Balao> baloes = new ArrayList<>();

        private Construtor(int numero, String titulo) {
            this.numero = numero;
            this.titulo = titulo;
        }

        public Construtor comLegenda(String legenda) {
            this.legenda = legenda;
            return this;
        }

        public Construtor comCena(DescritorCena cena) {
            this.cena = cena;
            return this;
        }

        public Construtor comBalao(Balao balao) {
            if (balao != null) {
                baloes.add(balao);
            }
            return this;
        }

        public Quadro construir() {
            return new Quadro(numero, titulo, legenda, cena, baloes);
        }
    }
}
