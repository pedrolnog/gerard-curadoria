package br.edu.univasf.gerard_curador.dominio.situacao;

import br.edu.univasf.gerard_curador.dominio.narrativo.ParticipanteNarrativo;
import br.edu.univasf.gerard_curador.dominio.quantitativo.FamiliaObjeto;
import br.edu.univasf.gerard_curador.dominio.quantitativo.ObjetoContado;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Cadastro explicito dos participantes e objetos contados de uma situacao.
 *
 * <p>Existe para dar sentido ao criterio "todo objeto referido por uma
 * transformacao existe no catalogo" (caso de teste 8) e aos diagnosticos
 * PARTICIPANTE_INEXISTENTE e OBJETO_CONTADO_INEXISTENTE.
 *
 * <p>Sem um catalogo, uma transformacao poderia referir uma rosa roxa que nunca
 * foi declarada, e o erro so apareceria como um inventario vazio inesperado. Com
 * ele, a situacao verifica antes de processar: cada referencia usada nas
 * transformacoes e nos estados precisa constar do cadastro curado.
 *
 * <p>Corresponde aos blocos "participantes" e "objetos_contados" do JSON.
 */
public final class CatalogoDaSituacao {

    private final Map<String, ParticipanteNarrativo> participantes;
    private final Map<String, ObjetoContado> objetos;

    private CatalogoDaSituacao(Map<String, ParticipanteNarrativo> participantes,
                               Map<String, ObjetoContado> objetos) {
        this.participantes = new LinkedHashMap<>(participantes);
        this.objetos = new LinkedHashMap<>(objetos);
    }

    private static void validarNovoParticipante(Map<String, ParticipanteNarrativo> participantes,
                                               ParticipanteNarrativo participante) {
        if (participante == null) {
            throw new IllegalArgumentException("catalogo nao aceita participante nulo");
        }
        if (participantes.containsKey(participante.getId())) {
            throw new IllegalArgumentException(
                    "participante ja cadastrado: " + participante.getId());
        }
    }

    private static void validarNovoObjeto(Map<String, ObjetoContado> objetos,
                                         ObjetoContado objeto) {
        if (objeto == null) {
            throw new IllegalArgumentException("catalogo nao aceita objeto nulo");
        }
        if (objetos.containsKey(objeto.getId())) {
            throw new IllegalArgumentException("objeto ja cadastrado: " + objeto.getId());
        }
    }

    public static Construtor construtor() {
        return new Construtor();
    }

    public ParticipanteNarrativo participantePorId(String id) {
        return id == null ? null : participantes.get(id);
    }

    public ObjetoContado objetoPorId(String id) {
        return id == null ? null : objetos.get(id);
    }

    public boolean contem(ParticipanteNarrativo participante) {
        return participante != null && participantes.containsKey(participante.getId());
    }

    public boolean contem(ObjetoContado objeto) {
        return objeto != null && objetos.containsKey(objeto.getId());
    }

    public List<ParticipanteNarrativo> getParticipantes() {
        return new ArrayList<>(participantes.values());
    }

    /** Adiciona um participante ao catalogo. */
    public void adicionarParticipante(ParticipanteNarrativo participante) {
        validarNovoParticipante(participantes, participante);
        participantes.put(participante.getId(), participante);
    }

    /** Remove um participante do catalogo. */
    public void removerParticipante(ParticipanteNarrativo participante) {
        if (participante == null) {
            throw new IllegalArgumentException("catalogo nao aceita participante nulo");
        }
        if (!participantes.containsKey(participante.getId())) {
            throw new IllegalArgumentException("participante nao esta no catalogo: " + participante.getId());
        }
        participantes.remove(participante.getId());
    }

    public List<ObjetoContado> getObjetos() {
        return new ArrayList<>(objetos.values());
    }

    /** Adiciona um objeto contado ao catalogo. */
    public void adicionarObjeto(ObjetoContado objeto) {
        validarNovoObjeto(objetos, objeto);
        objetos.put(objeto.getId(), objeto);
    }

    /** Remove um objeto contado do catalogo. */
    public void removerObjeto(ObjetoContado objeto) {
        if (objeto == null) {
            throw new IllegalArgumentException("catalogo nao aceita objeto nulo");
        }
        if (!objetos.containsKey(objeto.getId())) {
            throw new IllegalArgumentException("objeto nao esta no catalogo: " + objeto.getId());
        }
        objetos.remove(objeto.getId());
    }

    /** Objetos de uma familia, para as totalizacoes por familia. */
    public List<ObjetoContado> objetosDaFamilia(FamiliaObjeto familia) {
        List<ObjetoContado> selecionados = new ArrayList<>();
        for (ObjetoContado objeto : objetos.values()) {
            if (objeto.pertenceAFamilia(familia)) {
                selecionados.add(objeto);
            }
        }
        return selecionados;
    }

    /** Familias distintas presentes no catalogo, na ordem de cadastro. */
    public List<FamiliaObjeto> getFamilias() {
        List<FamiliaObjeto> familias = new ArrayList<>();
        for (ObjetoContado objeto : objetos.values()) {
            if (!familias.contains(objeto.getFamilia())) {
                familias.add(objeto.getFamilia());
            }
        }
        return familias;
    }

    public static final class Construtor {

        private final Map<String, ParticipanteNarrativo> participantes = new LinkedHashMap<>();
        private final Map<String, ObjetoContado> objetos = new LinkedHashMap<>();

        private Construtor() {
        }

        public Construtor comParticipante(ParticipanteNarrativo participante) {
            validarNovoParticipante(participantes, participante);
            participantes.put(participante.getId(), participante);
            return this;
        }

        public Construtor comObjeto(ObjetoContado objeto) {
            validarNovoObjeto(objetos, objeto);
            objetos.put(objeto.getId(), objeto);
            return this;
        }

        public CatalogoDaSituacao construir() {
            return new CatalogoDaSituacao(
                    new LinkedHashMap<>(participantes), new LinkedHashMap<>(objetos));
        }
    }
}
