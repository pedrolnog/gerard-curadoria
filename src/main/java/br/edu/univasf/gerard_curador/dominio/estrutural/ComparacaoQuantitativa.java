package br.edu.univasf.gerard_curador.dominio.estrutural;

import br.edu.univasf.gerard_curador.dominio.narrativo.ParticipanteNarrativo;
import br.edu.univasf.gerard_curador.dominio.quantitativo.Numero;
import br.edu.univasf.gerard_curador.dominio.quantitativo.Quantidade;
import br.edu.univasf.gerard_curador.dominio.validacao.ResultadoValidacao;
import br.edu.univasf.gerard_curador.dominio.validacao.TipoDiagnostico;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Arranjo de referente, referido e valor relativo.
 *
 * <p>Cobre a variacao "comparacao" da matriz de generalizacao: "Jose tem 8 bolas
 * a mais que Paulo, que possui 6". O documento historico traz varias, inclusive
 * na direcao inversa ("Jammes tem 12 CDs a menos que Gisele").
 *
 * <p>O vocabulario e o de Vergnaud, e a especificacao o exige explicitamente:
 * <ul>
 *   <li><strong>referente</strong>: o termo tomado como base (Paulo, com 6);
 *   <li><strong>referido</strong>: o termo comparado (Jose);
 *   <li><strong>valor relativo</strong>: a diferenca declarada (8 a mais).
 * </ul>
 *
 * <p>A direcao da comparacao e dado curado, nao inferencia: "a mais" e "a menos"
 * invertem a equacao, e ler isso do enunciado seria a extracao automatica de
 * papeis que o escopo proibe.
 *
 * <p>Equacao verificada: referido = referente + (sinal * valor relativo).
 *
 * <p>Diferenca conceitual em relacao aos outros arranjos: o valor relativo nao e
 * uma posse. Ninguem possui "8 bolas a mais"; e uma relacao entre duas posses.
 * Por isso esta quantidade nunca entra num inventario, e a classe guarda os dois
 * participantes comparados como referencias explicitas.
 */
public final class ComparacaoQuantitativa extends RelacaoEstrutural {

    /** Direcao curada da comparacao. */
    public enum Direcao {

        /** O referido possui mais que o referente: "Jose tem 8 a mais que Paulo". */
        A_MAIS("a mais", 1),

        /** O referido possui menos que o referente: "Jammes tem 12 a menos que Gisele". */
        A_MENOS("a menos", -1);

        private final String nomeExibido;
        private final int fator;

        Direcao(String nomeExibido, int fator) {
            this.nomeExibido = nomeExibido;
            this.fator = fator;
        }

        public String getNomeExibido() {
            return nomeExibido;
        }

        public int getFator() {
            return fator;
        }

        public Direcao inverter() {
            return this == A_MAIS ? A_MENOS : A_MAIS;
        }
    }

    private final ParticipanteNarrativo participanteReferente;
    private final ParticipanteNarrativo participanteReferido;
    private final Direcao direcao;

    private ComparacaoQuantitativa(String id,
                                   Map<PapelQuantitativo, Quantidade> papeis,
                                   ParticipanteNarrativo participanteReferente,
                                   ParticipanteNarrativo participanteReferido,
                                   Direcao direcao) {
        super(id, papeis);
        if (direcao == null) {
            throw new IllegalArgumentException("comparacao exige direcao curada");
        }
        this.participanteReferente = participanteReferente;
        this.participanteReferido = participanteReferido;
        this.direcao = direcao;
    }

    public static Construtor construtor(String id) {
        return new Construtor(id);
    }

    @Override
    public String getCategoria() {
        return "COMPARACAO";
    }

    public ParticipanteNarrativo getParticipanteReferente() {
        return participanteReferente;
    }

    public ParticipanteNarrativo getParticipanteReferido() {
        return participanteReferido;
    }

    public Direcao getDirecao() {
        return direcao;
    }

    @Override
    protected ResultadoValidacao validarRegrasDaCategoria(ResultadoValidacao resultado) {
        Quantidade referente = quantidadeDoPapel(PapelQuantitativo.REFERENTE);
        Quantidade referido = quantidadeDoPapel(PapelQuantitativo.REFERIDO);
        Quantidade relativo = quantidadeDoPapel(PapelQuantitativo.VALOR_RELATIVO);

        if (participanteReferente != null && participanteReferido != null
                && participanteReferente.equals(participanteReferido)) {
            resultado = resultado.acrescentar(
                    TipoDiagnostico.ENUNCIADO_AMBIGUO,
                    getId(),
                    "referente e referido sao o mesmo participante: "
                            + participanteReferente.getId());
        }

        if (referente == null || referido == null || relativo == null) {
            return resultado;
        }
        int calculado = referente.getValor() + direcao.getFator() * relativo.getValor();
        if (calculado != referido.getValor()) {
            resultado = resultado.acrescentar(
                    TipoDiagnostico.ESTADO_CALCULADO_DIVERGENTE,
                    "referido curado",
                    "referente " + referente.getValor() + " " + direcao.getNomeExibido()
                            + " " + relativo.getValor() + " resulta em " + calculado
                            + ", mas o curador informou " + referido.getValor()
                            + "; o valor curado foi preservado");
        }
        return resultado;
    }

    /**
     * Determina o papel desconhecido a partir dos outros dois.
     *
     * <p>Um valor relativo negativo e possivel e significativo: indica que a
     * direcao curada nao corresponde a realidade dos valores. O resultado e
     * devolvido como esta, para que o curador veja a inconsistencia em vez de
     * receber um valor silenciosamente ajustado.
     */
    @Override
    public Numero resolver(PapelQuantitativo papelDesconhecido) {
        if (papelDesconhecido == null || possuiPapel(papelDesconhecido)) {
            return null;
        }
        Quantidade referente = quantidadeDoPapel(PapelQuantitativo.REFERENTE);
        Quantidade referido = quantidadeDoPapel(PapelQuantitativo.REFERIDO);
        Quantidade relativo = quantidadeDoPapel(PapelQuantitativo.VALOR_RELATIVO);

        switch (papelDesconhecido) {
            case REFERIDO:
                if (referente == null || relativo == null) {
                    return null;
                }
                return Numero.de(referente.getValor() + direcao.getFator() * relativo.getValor());
            case REFERENTE:
                if (referido == null || relativo == null) {
                    return null;
                }
                return Numero.de(referido.getValor() - direcao.getFator() * relativo.getValor());
            case VALOR_RELATIVO:
                if (referente == null || referido == null) {
                    return null;
                }
                return Numero.de((referido.getValor() - referente.getValor()) * direcao.getFator());
            default:
                return null;
        }
    }

    public static final class Construtor {

        private final String id;
        private final Map<PapelQuantitativo, Quantidade> papeis = new LinkedHashMap<>();
        private ParticipanteNarrativo participanteReferente;
        private ParticipanteNarrativo participanteReferido;
        private Direcao direcao = Direcao.A_MAIS;

        private Construtor(String id) {
            this.id = id;
        }

        public Construtor comReferente(ParticipanteNarrativo participante, Quantidade quantidade) {
            this.participanteReferente = participante;
            if (quantidade != null) {
                papeis.put(PapelQuantitativo.REFERENTE, quantidade);
            }
            return this;
        }

        public Construtor comReferido(ParticipanteNarrativo participante, Quantidade quantidade) {
            this.participanteReferido = participante;
            if (quantidade != null) {
                papeis.put(PapelQuantitativo.REFERIDO, quantidade);
            }
            return this;
        }

        public Construtor comValorRelativo(Quantidade quantidade, Direcao direcao) {
            if (quantidade != null) {
                papeis.put(PapelQuantitativo.VALOR_RELATIVO, quantidade);
            }
            this.direcao = direcao;
            return this;
        }

        public ComparacaoQuantitativa construir() {
            return new ComparacaoQuantitativa(
                    id, papeis, participanteReferente, participanteReferido, direcao);
        }
    }
}
