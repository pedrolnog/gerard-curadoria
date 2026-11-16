package br.edu.univasf.gerard_curador.dominio.validacao;

import java.util.Objects;

/**
 * Uma ocorrencia individual detectada pela validacao.
 *
 * <p>Carrega tres informacoes: <em>o que</em> foi violado (o tipo), <em>onde</em>
 * (o elemento envolvido) e <em>o detalhe</em> em linguagem do dominio. Assim o
 * curador humano recebe o suficiente para decidir, que e precisamente o que o
 * sistema nao faz por ele.
 *
 * <p>Note o que este objeto <strong>nao</strong> tem: nenhum metodo corrigir(),
 * nenhuma sugestao de valor substituto, nenhuma referencia mutavel ao dado
 * problematico. Diagnosticar e relatar; a correcao pertence ao curador.
 */
public final class Diagnostico {

    private final TipoDiagnostico tipo;
    private final String elementoEnvolvido;
    private final String detalhe;

    public Diagnostico(TipoDiagnostico tipo, String elementoEnvolvido, String detalhe) {
        if (tipo == null) {
            throw new IllegalArgumentException("diagnostico exige tipo");
        }
        this.tipo = tipo;
        this.elementoEnvolvido = elementoEnvolvido == null || elementoEnvolvido.isBlank()
                ? "situacao"
                : elementoEnvolvido;
        this.detalhe = detalhe == null || detalhe.isBlank() ? tipo.getNomeExibido() : detalhe;
    }

    public static Diagnostico de(TipoDiagnostico tipo, String elemento, String detalhe) {
        return new Diagnostico(tipo, elemento, detalhe);
    }

    public TipoDiagnostico getTipo() {
        return tipo;
    }

    public String getElementoEnvolvido() {
        return elementoEnvolvido;
    }

    public String getDetalhe() {
        return detalhe;
    }

    public boolean ehErro() {
        return tipo.ehErro();
    }

    public boolean ehAviso() {
        return tipo.ehAviso();
    }

    public boolean ehDoTipo(TipoDiagnostico outroTipo) {
        return tipo == outroTipo;
    }

    /** "[ERRO] estado calculado divergente ... (em: estado_final_curado): detalhe". */
    public String descrever() {
        return "[" + tipo.getSeveridade() + "] " + tipo.getNomeExibido()
                + " (em: " + elementoEnvolvido + "): " + detalhe;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Diagnostico)) {
            return false;
        }
        Diagnostico outro = (Diagnostico) obj;
        return tipo == outro.tipo
                && elementoEnvolvido.equals(outro.elementoEnvolvido)
                && detalhe.equals(outro.detalhe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipo, elementoEnvolvido, detalhe);
    }

    @Override
    public String toString() {
        return descrever();
    }
}
