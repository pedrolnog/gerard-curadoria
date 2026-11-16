package br.edu.univasf.gerard_curador.dominio.situacao;

import br.edu.univasf.gerard_curador.dominio.narrativo.ContextoNarrativo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sequencia semantica de estados, eventos e pergunta.
 *
 * <p>E a projecao que a situacao produz e que a representacao consome: o ponto
 * exato onde o dominio termina e a apresentacao comeca. Reproduz a estrutura
 * esperada pela especificacao:
 *
 * <pre>
 *   ESTADO:   Vovo possui 2 rosas brancas e 3 rosas amarelas.
 *   EVENTO:   Vovo da 1 rosa branca a netinha.
 *   EVENTO:   Vovo da 1 rosa amarela a netinha.
 *   ESTADO:   Vovo possui 1 rosa branca e 2 rosas amarelas.
 *   PERGUNTA: Qual e o total do estado final?
 * </pre>
 *
 * <p>Independente de midia: nao sabe o que e um quadro, um balao ou uma pagina.
 * Responde a pergunta 9 da apresentacao final: SituacaoProblema produz esta
 * sequencia mas nao desenha os quadros porque dividir conteudo em cenas e
 * conhecimento de quadrinhos, nao de estruturas aditivas. A mesma sequencia
 * alimenta um roteiro de HQ, uma narracao em audio ou uma pagina web sem que o
 * dominio saiba da existencia de nenhum deles.
 *
 * <p>A ordem dos episodios e a ordem curada das transformacoes, preservada na
 * construcao (caso de teste 10).
 */
public final class SequenciaNarrativa {

    private final String idSituacao;
    private final List<EpisodioNarrativo> episodios;
    private final ContextoNarrativo contexto;

    public SequenciaNarrativa(String idSituacao,
                              List<EpisodioNarrativo> episodios,
                              ContextoNarrativo contexto) {
        if (idSituacao == null || idSituacao.isBlank()) {
            throw new IllegalArgumentException("sequencia narrativa exige id da situacao");
        }
        this.idSituacao = idSituacao;
        this.episodios = Collections.unmodifiableList(
                new ArrayList<>(episodios == null ? new ArrayList<>() : episodios));
        this.contexto = contexto == null ? ContextoNarrativo.naoInformado() : contexto;
    }

    public String getIdSituacao() {
        return idSituacao;
    }

    public List<EpisodioNarrativo> getEpisodios() {
        return new ArrayList<>(episodios);
    }

    public ContextoNarrativo getContexto() {
        return contexto;
    }

    public int quantidadeDeEpisodios() {
        return episodios.size();
    }

    /** Episodios de um tipo especifico, na ordem em que ocorrem. */
    public List<EpisodioNarrativo> episodiosDoTipo(TipoEpisodio tipo) {
        List<EpisodioNarrativo> selecionados = new ArrayList<>();
        for (EpisodioNarrativo episodio : episodios) {
            if (episodio.getTipo() == tipo) {
                selecionados.add(episodio);
            }
        }
        return selecionados;
    }

    public List<EpisodioNarrativo> getEstados() {
        return episodiosDoTipo(TipoEpisodio.ESTADO);
    }

    public List<EpisodioNarrativo> getEventos() {
        return episodiosDoTipo(TipoEpisodio.EVENTO);
    }

    /** A pergunta da situacao, quando a sequencia a inclui. */
    public EpisodioNarrativo getPergunta() {
        for (EpisodioNarrativo episodio : episodios) {
            if (episodio.ehPergunta()) {
                return episodio;
            }
        }
        return null;
    }

    public EpisodioNarrativo getPrimeiroEpisodio() {
        return episodios.isEmpty() ? null : episodios.get(0);
    }

    public EpisodioNarrativo getUltimoEstado() {
        List<EpisodioNarrativo> estados = getEstados();
        return estados.isEmpty() ? null : estados.get(estados.size() - 1);
    }

    /**
     * Verdadeiro quando os eventos aparecem em ordem temporal nao decrescente.
     *
     * <p>Suporte ao caso de teste 10: a sequencia mantem a ordem das
     * transformacoes.
     */
    public boolean preservaOrdemTemporal() {
        int ordemAnterior = 0;
        for (EpisodioNarrativo episodio : episodios) {
            if (!episodio.ehEvento()) {
                continue;
            }
            int ordemAtual = episodio.getMomento().getOrdem();
            if (ordemAtual < ordemAnterior) {
                return false;
            }
            ordemAnterior = ordemAtual;
        }
        return true;
    }

    /** Sequencia completa, uma linha por episodio, com os rotulos semanticos. */
    public String descrever() {
        StringBuilder texto = new StringBuilder();
        for (EpisodioNarrativo episodio : episodios) {
            if (texto.length() > 0) {
                texto.append(System.lineSeparator());
            }
            texto.append(episodio.descreverComRotulo());
        }
        return texto.toString();
    }

    @Override
    public String toString() {
        return descrever();
    }
}
