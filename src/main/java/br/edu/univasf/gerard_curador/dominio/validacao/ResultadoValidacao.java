package br.edu.univasf.gerard_curador.dominio.validacao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Diagnosticos estruturados, sem corrigir os dados humanos.
 *
 * <p>Atende diretamente a boa pratica da especificacao: evitar retornar apenas
 * true ou false quando o chamador precisa explicar qual regra foi violada. Um
 * booleano informaria que a situacao das rosas esta invalida; este objeto informa
 * que o estado final curado diz 3 rosas enquanto o calculo produz 4, deixando ao
 * curador a decisao sobre qual dos dois esta errado.
 *
 * <p>Acumula diagnosticos em vez de interromper no primeiro: uma situacao mal
 * curada costuma ter varios problemas, e mostra-los todos de uma vez economiza
 * rodadas de correcao.
 *
 * <p>Imutavel na leitura: getDiagnosticos devolve copia, e acrescentar produz um
 * novo resultado. Quem recebe um ResultadoValidacao nao consegue apagar um
 * problema relatado.
 */
public final class ResultadoValidacao {

    private final List<Diagnostico> diagnosticos;

    private ResultadoValidacao(List<Diagnostico> diagnosticos) {
        this.diagnosticos = Collections.unmodifiableList(diagnosticos);
    }

    /** Resultado sem nenhuma ocorrencia: a estrutura passou em todas as verificacoes. */
    public static ResultadoValidacao semOcorrencias() {
        return new ResultadoValidacao(new ArrayList<>());
    }

    public static ResultadoValidacao com(List<Diagnostico> diagnosticos) {
        return new ResultadoValidacao(
                diagnosticos == null ? new ArrayList<>() : new ArrayList<>(diagnosticos));
    }

    /** Devolve um novo resultado com o diagnostico acrescentado. */
    public ResultadoValidacao acrescentar(Diagnostico diagnostico) {
        if (diagnostico == null) {
            return this;
        }
        List<Diagnostico> novos = new ArrayList<>(diagnosticos);
        novos.add(diagnostico);
        return new ResultadoValidacao(novos);
    }

    public ResultadoValidacao acrescentar(TipoDiagnostico tipo, String elemento, String detalhe) {
        return acrescentar(new Diagnostico(tipo, elemento, detalhe));
    }

    /** Funde dois resultados, preservando a ordem de deteccao. */
    public ResultadoValidacao combinarCom(ResultadoValidacao outro) {
        if (outro == null || outro.diagnosticos.isEmpty()) {
            return this;
        }
        List<Diagnostico> novos = new ArrayList<>(diagnosticos);
        novos.addAll(outro.diagnosticos);
        return new ResultadoValidacao(novos);
    }

    public List<Diagnostico> getDiagnosticos() {
        return new ArrayList<>(diagnosticos);
    }

    public List<Diagnostico> getErros() {
        return filtrarPor(true);
    }

    public List<Diagnostico> getAvisos() {
        return filtrarPor(false);
    }

    private List<Diagnostico> filtrarPor(boolean querErros) {
        List<Diagnostico> selecionados = new ArrayList<>();
        for (Diagnostico diagnostico : diagnosticos) {
            if (diagnostico.ehErro() == querErros) {
                selecionados.add(diagnostico);
            }
        }
        return selecionados;
    }

    /**
     * Verdadeiro quando nenhum erro foi detectado.
     *
     * <p>Avisos nao impedem a validade: uma referencia visual ausente merece
     * atencao, mas nao invalida a estrutura da situacao.
     */
    public boolean ehValido() {
        return getErros().isEmpty();
    }

    public boolean possuiOcorrencias() {
        return !diagnosticos.isEmpty();
    }

    public boolean possuiAvisos() {
        return !getAvisos().isEmpty();
    }

    /** Verdadeiro quando algum diagnostico do tipo informado foi registrado. */
    public boolean contem(TipoDiagnostico tipo) {
        for (Diagnostico diagnostico : diagnosticos) {
            if (diagnostico.ehDoTipo(tipo)) {
                return true;
            }
        }
        return false;
    }

    public int quantidadeDeOcorrencias() {
        return diagnosticos.size();
    }

    /** Relatorio legivel, uma ocorrencia por linha. */
    public String descrever() {
        if (diagnosticos.isEmpty()) {
            return "nenhuma ocorrencia";
        }
        StringBuilder texto = new StringBuilder();
        for (Diagnostico diagnostico : diagnosticos) {
            if (texto.length() > 0) {
                texto.append(System.lineSeparator());
            }
            texto.append(diagnostico.descrever());
        }
        return texto.toString();
    }

    @Override
    public String toString() {
        return descrever();
    }
}
