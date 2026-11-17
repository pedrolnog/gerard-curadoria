package br.edu.univasf.gerard_curador.dominio.estrutural;

import br.edu.univasf.gerard_curador.dominio.validacao.TipoDiagnostico;

import br.edu.univasf.gerard_curador.dominio.quantitativo.Numero;
import br.edu.univasf.gerard_curador.dominio.quantitativo.Quantidade;
import br.edu.univasf.gerard_curador.dominio.validacao.ResultadoValidacao;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Coordena e valida regras que envolvem varios papeis.
 *
 * <p>Responde a pergunta 4 da apresentacao final: quem valida a compatibilidade
 * entre grandeza, unidade e os varios papeis da relacao.
 *
 * <p>A justificativa esta na secao 6.4 da especificacao: a regra que coordena
 * varios papeis nao pertence a um papel isolado. A quantidade que ocupa o papel
 * ESTADO_INICIAL sabe recusar operacoes com unidades incompativeis, mas nao tem
 * como saber que deveria se relacionar com o ESTADO_FINAL por meio de duas
 * transformacoes. Esse conhecimento e sobre o <em>arranjo</em>, e mora aqui.
 *
 * <p>Classe abstrata com subclasses por tipo de arranjo. Cada subclasse conhece
 * a equacao que rege seus papeis:
 * <ul>
 *   <li>{@link ComposicaoDeTransformacoes}: estado inicial, transformacoes e
 *       estado final;
 *   <li>{@link ComposicaoParteTodo}: partes e todo;
 *   <li>{@link ComparacaoQuantitativa}: referente, referido e valor relativo.
 * </ul>
 *
 * <p>Responde tambem a pergunta 10: adicionar outra categoria de estrutura
 * aditiva significa escrever uma nova subclasse, sem tocar nas existentes nem no
 * nucleo quantitativo.
 *
 * <p>Regra inegociavel em todas elas: a relacao <strong>diagnostica</strong> a
 * divergencia entre o valor calculado e o valor curado, e nunca sobrescreve o
 * dado humano.
 */
public abstract class RelacaoEstrutural {

    private final String id;
    private final Map<PapelQuantitativo, Quantidade> papeis;

    protected RelacaoEstrutural(String id, Map<PapelQuantitativo, Quantidade> papeis) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("relacao estrutural exige id");
        }
        Map<PapelQuantitativo, Quantidade> copia = new LinkedHashMap<>();
        if (papeis != null) {
            for (Map.Entry<PapelQuantitativo, Quantidade> entrada : papeis.entrySet()) {
                if (entrada.getKey() != null && entrada.getValue() != null) {
                    copia.put(entrada.getKey(), entrada.getValue());
                }
            }
        }
        this.id = id;
        this.papeis = Collections.unmodifiableMap(copia);
    }

    public String getId() {
        return id;
    }

    /** Nome do arranjo, para relatorios e para o campo "categoria" do JSON. */
    public abstract String getCategoria();

    /**
     * Verifica a coerencia do arranjo e devolve os diagnosticos encontrados.
     *
     * <p><b>Template Method:</b> o esqueleto do algoritmo e fixo e {@code final}
     * — primeiro a regra comum a toda relacao (medidas compativeis entre os
     * papeis), depois a regra especifica de cada categoria, que a subclasse
     * fornece em {@link #validarRegrasDaCategoria}. Nenhuma subclasse consegue
     * "esquecer" a verificacao comum, e nenhuma altera as quantidades recebidas.
     */
    public final ResultadoValidacao validar() {
        return validarRegrasDaCategoria(validarCompatibilidadeDeMedidas());
    }

    /**
     * Passo variavel do Template Method: a equacao propria da categoria
     * (Ei + T1 + T2 = Ef, soma das partes = todo, referido = referente +/- diferenca).
     *
     * @param parcial diagnosticos ja produzidos pela verificacao comum
     * @return os diagnosticos acumulados
     */
    protected abstract ResultadoValidacao validarRegrasDaCategoria(ResultadoValidacao parcial);

    /**
     * Calcula o numero que ocupa o papel desconhecido, usando a equacao da
     * categoria. Cada subclasse resolve a sua; quem chama nao precisa saber
     * qual e (ligacao dinamica).
     *
     * @return o numero do papel pedido, ou null quando os dados nao bastam
     */
    public abstract Numero resolver(PapelQuantitativo papelDesconhecido);

    public Map<PapelQuantitativo, Quantidade> getPapeis() {
        return papeis;
    }

    public Quantidade quantidadeDoPapel(PapelQuantitativo papel) {
        return papeis.get(papel);
    }

    public boolean possuiPapel(PapelQuantitativo papel) {
        return papeis.containsKey(papel);
    }

    /**
     * Confere se todos os papeis presentes compartilham grandeza e unidade.
     *
     * <p>Verificacao comum a qualquer arranjo: nao se compoe reais com
     * quilogramas, seja numa transformacao, numa composicao ou numa comparacao.
     * Cada subclasse chama esta rotina antes de aplicar sua propria equacao.
     */
    protected ResultadoValidacao validarCompatibilidadeDeMedidas() {
        ResultadoValidacao resultado = ResultadoValidacao.semOcorrencias();
        Quantidade referencia = null;
        PapelQuantitativo papelReferencia = null;
        for (Map.Entry<PapelQuantitativo, Quantidade> entrada : papeis.entrySet()) {
            Quantidade atual = entrada.getValue();
            if (referencia == null) {
                referencia = atual;
                papelReferencia = entrada.getKey();
                continue;
            }
            if (!referencia.possuiMesmaMedidaQue(atual)) {
                resultado = resultado.acrescentar(
                        br.edu.univasf.gerard_curador.dominio.validacao.TipoDiagnostico.GRANDEZAS_OU_UNIDADES_INCOMPATIVEIS,
                        getId(),
                        "papel " + papelReferencia.getNomeExibido()
                                + " e papel " + entrada.getKey().getNomeExibido()
                                + ": " + referencia.descreverIncompatibilidadeCom(atual));
            }
        }
        return resultado;
    }

    @Override
    public String toString() {
        return getCategoria() + " (" + id + ")";
    }
}
