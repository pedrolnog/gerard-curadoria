package br.edu.univasf.gerard_curador.dominio.narrativo;

import br.edu.univasf.gerard_curador.dominio.quantitativo.FamiliaObjeto;

import br.edu.univasf.gerard_curador.dominio.quantitativo.Numero;
import br.edu.univasf.gerard_curador.dominio.quantitativo.ObjetoContado;
import br.edu.univasf.gerard_curador.dominio.quantitativo.Quantidade;

/**
 * Fotografia do inventario de um participante em um momento.
 *
 * <p>Une tres conhecimentos que juntos descrevem uma posse situada: quem possui,
 * o que possui e quando. Sem o marcador temporal, dois estados do mesmo
 * participante seriam indistinguiveis; sem o possuidor, o inventario flutuaria
 * sem dono.
 *
 * <p>Imutavel. Aplicar uma transformacao devolve um novo estado, e o anterior
 * permanece disponivel para a sequencia narrativa exibir. E o que permite
 * demonstrar, na apresentacao, que os estados anteriores nao sao corrompidos.
 *
 * <p>Nao valida a legitimidade da transformacao: quem decide se a retirada cabe
 * e o inventario, e quem coordena a sequencia e a situacao. Este objeto sabe
 * apenas produzir sua propria versao seguinte.
 */
public final class EstadoDePosse {

    private final ParticipanteNarrativo possuidor;
    private final Inventario inventario;
    private final MarcadorTemporal momento;

    public EstadoDePosse(ParticipanteNarrativo possuidor,
                         Inventario inventario,
                         MarcadorTemporal momento) {
        if (possuidor == null) {
            throw new IllegalArgumentException("estado de posse exige possuidor");
        }
        if (inventario == null) {
            throw new IllegalArgumentException("estado de posse exige inventario");
        }
        if (momento == null) {
            throw new IllegalArgumentException("estado de posse exige marcador temporal");
        }
        this.possuidor = possuidor;
        this.inventario = inventario;
        this.momento = momento;
    }

    public ParticipanteNarrativo getPossuidor() {
        return possuidor;
    }

    public Inventario getInventario() {
        return inventario;
    }

    public MarcadorTemporal getMomento() {
        return momento;
    }

    /**
     * Produz o estado seguinte com a quantidade acrescentada.
     *
     * <p>O novo estado recebe o marcador temporal informado: um estado e sempre
     * situado no tempo, e o momento do resultado nao e o mesmo do original.
     */
    public EstadoDePosse acrescentar(Quantidade quantidade, MarcadorTemporal novoMomento) {
        return new EstadoDePosse(possuidor, inventario.adicionar(quantidade), novoMomento);
    }

    /**
     * Produz o estado seguinte com a quantidade retirada.
     *
     * <p>Propaga a excecao do inventario quando a retirada e impossivel, e nesse
     * caso nenhum estado novo chega a existir: o estado atual permanece intacto.
     */
    public EstadoDePosse retirar(Quantidade quantidade, MarcadorTemporal novoMomento) {
        return new EstadoDePosse(possuidor, inventario.retirar(quantidade), novoMomento);
    }

    /** Consulta sem efeito colateral, para a validacao relatar em vez de interromper. */
    public boolean comporta(Quantidade quantidade) {
        return inventario.comporta(quantidade);
    }

    public boolean pertenceA(ParticipanteNarrativo participante) {
        return possuidor.equals(participante);
    }

    public Numero totalDaFamilia(FamiliaObjeto familia) {
        return inventario.totalDaFamilia(familia);
    }

    public Numero totalGeral() {
        return inventario.totalGeral();
    }

    public Quantidade quantidadeDe(ObjetoContado objeto) {
        return inventario.quantidadeDe(objeto);
    }

    /**
     * Compara o conteudo possuido, ignorando o momento.
     *
     * <p>A relacao estrutural usa esta comparacao para confrontar o estado
     * calculado com o estado final curado: os dois descrevem o mesmo instante da
     * historia por definicao, entao o que interessa e se a posse coincide.
     */
    public boolean possuiMesmoConteudoQue(EstadoDePosse outro) {
        return outro != null
                && possuidor.equals(outro.possuidor)
                && inventario.possuiMesmoConteudoQue(outro.inventario);
    }

    /** "Vovo possui 2 rosas brancas e 3 rosas amarelas". */
    public String descrever() {
        return possuidor.getNomeExibido() + " possui " + inventario.descrever();
    }

    @Override
    public String toString() {
        return descrever();
    }
}
