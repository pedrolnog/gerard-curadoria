package br.edu.univasf.gerard_curador.interfacegrafica;

import br.edu.univasf.gerard_curador.dominio.narrativo.AcaoNarrativa;
import br.edu.univasf.gerard_curador.dominio.narrativo.SentidoTransformacao;
import java.util.ArrayList;
import java.util.List;

/**
 * Acoes curadas que a tela oferece ao curador.
 *
 * <p>A diferenca em relacao a interface original e o ponto desta classe. La, o
 * curador escolhia um sinal numa caixa "Selecione o sinal...", desligada de
 * qualquer significado: o sistema sabia que havia uma subtracao, mas nao sabia
 * que alguem deu algo a alguem.
 *
 * <p>Aqui o curador escolhe uma <strong>acao</strong>, e o sentido vem junto
 * dela, porque dar sempre retira de quem da. O curador nao pode montar a
 * combinacao incoerente "deu, com sinal de acrescimo".
 *
 * <p>As acoes abaixo cobrem os verbos das situacoes historicas do documento de
 * 2009. A lista pertence a interface: e uma conveniencia de preenchimento, e
 * qualquer acao construida a mao continua valida para o dominio.
 */
public final class AcoesDisponiveis {

    private AcoesDisponiveis() {
    }

    /** Acoes curadas, na ordem em que aparecem no menu. */
    public static List<AcaoNarrativa> listar() {
        List<AcaoNarrativa> acoes = new ArrayList<>();

        // Transferencias: o que sai de um participante entra em outro.
        acoes.add(AcaoNarrativa.transferencia(
                "dar", "deu", "dada", SentidoTransformacao.RETIRADA));
        acoes.add(AcaoNarrativa.transferencia(
                "entregar", "entregou", "entregue", SentidoTransformacao.RETIRADA));
        acoes.add(AcaoNarrativa.transferencia(
                "emprestar", "emprestou", "emprestada", SentidoTransformacao.RETIRADA));
        acoes.add(AcaoNarrativa.transferencia(
                "perder_para", "perdeu", "perdida", SentidoTransformacao.RETIRADA));
        acoes.add(AcaoNarrativa.transferencia(
                "ganhar", "ganhou", "ganha", SentidoTransformacao.ACRESCIMO));
        acoes.add(AcaoNarrativa.transferencia(
                "receber", "recebeu", "recebida", SentidoTransformacao.ACRESCIMO));

        // Sem contraparte: alteram um inventario sem que outro receba.
        acoes.add(AcaoNarrativa.semDestinatario(
                "comprar", "comprou", "comprada", SentidoTransformacao.ACRESCIMO));
        acoes.add(AcaoNarrativa.semDestinatario(
                "encontrar", "encontrou", "encontrada", SentidoTransformacao.ACRESCIMO));
        acoes.add(AcaoNarrativa.semDestinatario(
                "comer", "comeu", "comida", SentidoTransformacao.RETIRADA));
        acoes.add(AcaoNarrativa.semDestinatario(
                "gastar", "gastou", "gasta", SentidoTransformacao.RETIRADA));
        acoes.add(AcaoNarrativa.semDestinatario(
                "perder", "perdeu", "perdida", SentidoTransformacao.RETIRADA));
        acoes.add(AcaoNarrativa.semDestinatario(
                "quebrar", "quebrou", "quebrada", SentidoTransformacao.RETIRADA));
        acoes.add(AcaoNarrativa.semDestinatario(
                "vender", "vendeu", "vendida", SentidoTransformacao.RETIRADA));
        acoes.add(AcaoNarrativa.semDestinatario(
                "retirar", "retirou", "retirada", SentidoTransformacao.RETIRADA));

        return acoes;
    }

    /**
     * Rotulo de menu que revela o sentido junto do verbo.
     *
     * <p>Assim o curador ve, no momento da escolha, que "deu" implica retirada.
     */
    public static String rotularParaMenu(AcaoNarrativa acao) {
        return acao.getVerbo()
                + " (" + acao.getSentido().getNomeExibido()
                + (acao.exigeDestinatario() ? ", com destinatario" : ", sem destinatario")
                + ")";
    }
}
