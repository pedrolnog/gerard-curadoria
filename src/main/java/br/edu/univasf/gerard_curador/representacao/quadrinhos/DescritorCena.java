package br.edu.univasf.gerard_curador.representacao.quadrinhos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Instrucoes visuais de um quadro: quem aparece e o que deve ser desenhado.
 *
 * <p>Pertence a representacao. Trabalha exclusivamente com as chaves abstratas
 * que o dominio forneceu (ReferenciaVisual e ReferenciaVisualParticipante), nunca
 * com caminhos de arquivo, cores ou coordenadas: essas sao decisoes do
 * renderizador da plataforma, uma camada abaixo desta.
 *
 * <p>A separacao permite que o mesmo roteiro alimente um renderizador Swing, uma
 * pagina HTML ou um gerador de PDF. Cada um resolve "rosa_branca" a sua maneira.
 */
public final class DescritorCena {

    private final List<String> personagensVisiveis;
    /** Chave visual do objeto -> quantas vezes desenha-lo no quadro. */
    private final Map<String, Integer> elementosVisuais;
    private final String ambientacao;

    private DescritorCena(List<String> personagensVisiveis,
                          Map<String, Integer> elementosVisuais,
                          String ambientacao) {
        this.personagensVisiveis = Collections.unmodifiableList(personagensVisiveis);
        this.elementosVisuais = Collections.unmodifiableMap(elementosVisuais);
        this.ambientacao = ambientacao;
    }

    public static Construtor construtor() {
        return new Construtor();
    }

    public List<String> getPersonagensVisiveis() {
        return new ArrayList<>(personagensVisiveis);
    }

    public Map<String, Integer> getElementosVisuais() {
        return new LinkedHashMap<>(elementosVisuais);
    }

    public String getAmbientacao() {
        return ambientacao;
    }

    public boolean possuiAmbientacao() {
        return ambientacao != null && !ambientacao.isBlank();
    }

    /** Total de elementos a desenhar, util para o renderizador dimensionar a cena. */
    public int totalDeElementos() {
        int total = 0;
        for (Integer quantidade : elementosVisuais.values()) {
            total += quantidade;
        }
        return total;
    }

    public String descrever() {
        StringBuilder texto = new StringBuilder();
        texto.append("personagens: ").append(String.join(", ", personagensVisiveis));
        if (!elementosVisuais.isEmpty()) {
            texto.append(" | elementos: ");
            boolean primeiro = true;
            for (Map.Entry<String, Integer> entrada : elementosVisuais.entrySet()) {
                if (!primeiro) {
                    texto.append(", ");
                }
                texto.append(entrada.getValue()).append("x ").append(entrada.getKey());
                primeiro = false;
            }
        }
        if (possuiAmbientacao()) {
            texto.append(" | ambientacao: ").append(ambientacao);
        }
        return texto.toString();
    }

    @Override
    public String toString() {
        return descrever();
    }

    public static final class Construtor {

        private final List<String> personagens = new ArrayList<>();
        private final Map<String, Integer> elementos = new LinkedHashMap<>();
        private String ambientacao;

        private Construtor() {
        }

        public Construtor comPersonagem(String chaveVisual) {
            if (chaveVisual != null && !chaveVisual.isBlank()
                    && !personagens.contains(chaveVisual)) {
                personagens.add(chaveVisual);
            }
            return this;
        }

        /** Acumula quando a mesma chave aparece mais de uma vez no quadro. */
        public Construtor comElemento(String chaveVisual, int quantidade) {
            if (chaveVisual == null || chaveVisual.isBlank() || quantidade <= 0) {
                return this;
            }
            elementos.merge(chaveVisual, quantidade, Integer::sum);
            return this;
        }

        public Construtor comAmbientacao(String ambientacao) {
            this.ambientacao = ambientacao;
            return this;
        }

        public DescritorCena construir() {
            return new DescritorCena(
                    new ArrayList<>(personagens), new LinkedHashMap<>(elementos), ambientacao);
        }
    }
}
