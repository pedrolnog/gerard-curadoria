package br.edu.univasf.gerard_curador.representacao.quadrinhos;

import br.edu.univasf.gerard_curador.dominio.narrativo.EstadoDePosse;
import br.edu.univasf.gerard_curador.dominio.narrativo.TransformacaoNarrativa;
import br.edu.univasf.gerard_curador.dominio.quantitativo.Quantidade;
import br.edu.univasf.gerard_curador.dominio.situacao.EpisodioNarrativo;
import br.edu.univasf.gerard_curador.dominio.situacao.SequenciaNarrativa;
import java.util.ArrayList;
import java.util.List;

/**
 * Converte a sequencia narrativa em quadros; nao recalcula a matematica.
 *
 * <p>Esta e a classe que materializa a fronteira entre dominio e representacao.
 * Ela le episodios e produz quadros, e todo numero que aparece num balao veio de
 * um objeto do dominio ja validado. Nao ha nenhuma soma, subtracao ou comparacao
 * aritmetica neste arquivo, e isso e proposital: se houvesse, a mesma regra
 * existiria em dois lugares e poderiam divergir.
 *
 * <p>Responde a pergunta 9 da apresentacao final pelo outro lado: a situacao
 * produz a sequencia mas nao desenha os quadros, porque decidir que o estado
 * inicial merece um quadro proprio e que a pergunta vai no ultimo balao e
 * conhecimento de narrativa visual, nao de estruturas aditivas.
 *
 * <p>O caso de teste 12 se verifica aqui: trocar este gerador por outro (uma
 * narracao em audio, uma pagina HTML) nao exige tocar em nenhuma classe do
 * dominio.
 *
 * <p>Duas realizacoes sao oferecidas a partir do mesmo roteiro: texto simples e
 * HTML. Ambas leem o mesmo objeto, confirmando que a estrutura do roteiro
 * independe do formato final.
 */
public final class GeradorRoteiroQuadrinhos {

    /**
     * Gera o roteiro a partir da sequencia semantica.
     *
     * <p>Estrategia de divisao em quadros: um quadro por episodio, com a pergunta
     * incorporada ao quadro do estado final em vez de ganhar um quadro proprio.
     * A escolha e de linguagem visual, e mantem o roteiro das rosas nos quatro
     * quadros que a especificacao descreve.
     */
    public RoteiroQuadrinhos gerar(SequenciaNarrativa sequencia, String titulo) {
        if (sequencia == null) {
            throw new IllegalArgumentException("geracao exige sequencia narrativa");
        }
        List<Quadro> quadros = new ArrayList<>();
        String ambientacao = sequencia.getContexto().descrever();
        int numeroDoQuadro = 1;

        List<EpisodioNarrativo> episodios = sequencia.getEpisodios();
        for (int i = 0; i < episodios.size(); i++) {
            EpisodioNarrativo episodio = episodios.get(i);
            if (episodio.ehPergunta()) {
                // A pergunta acompanha o quadro anterior, sem quadro proprio.
                continue;
            }
            boolean ehUltimoEstado = episodio.ehEstado() && numeroDoQuadro > 1;
            EpisodioNarrativo pergunta = ehUltimoEstado ? sequencia.getPergunta() : null;

            quadros.add(episodio.ehEstado()
                    ? montarQuadroDeEstado(numeroDoQuadro, episodio, pergunta,
                            numeroDoQuadro == 1 ? ambientacao : null)
                    : montarQuadroDeEvento(numeroDoQuadro, episodio));
            numeroDoQuadro++;
        }
        return new RoteiroQuadrinhos(sequencia.getIdSituacao(), titulo, quadros);
    }

    /**
     * Quadro de estado: personagem, seus itens e a legenda descritiva.
     *
     * <p>Os elementos visuais vem das chaves abstratas do dominio, e a contagem
     * de cada um vem do numero ja calculado na quantidade. O gerador nao soma
     * nada: apenas repassa quantos desenhos o renderizador deve produzir.
     */
    private Quadro montarQuadroDeEstado(int numero,
                                        EpisodioNarrativo episodio,
                                        EpisodioNarrativo pergunta,
                                        String ambientacao) {
        EstadoDePosse estado = episodio.getEstado();
        DescritorCena.Construtor cena = DescritorCena.construtor()
                .comPersonagem(estado.getPossuidor().getReferenciaVisual().getChave())
                .comAmbientacao(ambientacao);

        for (Quantidade item : estado.getInventario().getItensPresentes()) {
            cena.comElemento(item.getObjeto().getReferenciaVisual().getChave(), item.getValor());
        }

        Quadro.Construtor construtor = Quadro.construtor(numero, tituloDoEstado(numero, pergunta))
                .comLegenda(estado.descrever())
                .comCena(cena.construir())
                .comBalao(Balao.narracao(estado.descrever()));

        if (pergunta != null) {
            construtor.comBalao(Balao.pergunta(pergunta.descrever()));
        }
        return construtor.construir();
    }

    /** Quadro de evento: ator, destinatario quando existe, e o objeto transferido. */
    private Quadro montarQuadroDeEvento(int numero, EpisodioNarrativo episodio) {
        TransformacaoNarrativa transformacao = episodio.getTransformacao();
        DescritorCena.Construtor cena = DescritorCena.construtor()
                .comPersonagem(transformacao.getAtor().getReferenciaVisual().getChave());

        if (transformacao.possuiDestinatario()) {
            cena.comPersonagem(transformacao.getDestinatario().getReferenciaVisual().getChave());
        }
        cena.comElemento(
                transformacao.getQuantidade().getObjeto().getReferenciaVisual().getChave(),
                transformacao.getQuantidade().getValor());

        return Quadro.construtor(numero, transformacao.getMarcador().getRotulo())
                .comLegenda(transformacao.descrever())
                .comCena(cena.construir())
                .comBalao(Balao.narracao(transformacao.descrever()))
                .construir();
    }

    private String tituloDoEstado(int numero, EpisodioNarrativo pergunta) {
        if (numero == 1) {
            return "Estado inicial";
        }
        return pergunta != null ? "Estado final e pergunta" : "Estado final";
    }
}
