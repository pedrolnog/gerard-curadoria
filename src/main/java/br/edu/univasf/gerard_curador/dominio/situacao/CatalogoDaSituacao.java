package br.edu.univasf.gerard_curador.dominio.situacao;

import br.edu.univasf.gerard_curador.dominio.narrativo.ParticipanteNarrativo;
import br.edu.univasf.gerard_curador.dominio.quantitativo.FamiliaObjeto;
import br.edu.univasf.gerard_curador.dominio.quantitativo.ObjetoContado;
import java.util.ArrayList;
import java.util.Collections;
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
        this.participantes = Collections.unmodifiableMap(participantes);
        this.objetos = Collections.unmodifiableMap(objetos);
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

    public List<ObjetoContado> getObjetos() {
        return new ArrayList<>(objetos.values());
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
            if (participante == null) {
                throw new IllegalArgumentException("catalogo nao aceita participante nulo");
            }
            if (participantes.containsKey(participante.getId())) {
                throw new IllegalArgumentException(
                        "participante ja cadastrado: " + participante.getId());
            }
            participantes.put(participante.getId(), participante);
            return this;
        }

        public Construtor comObjeto(ObjetoContado objeto) {
            if (objeto == null) {
                throw new IllegalArgumentException("catalogo nao aceita objeto nulo");
            }
            if (objetos.containsKey(objeto.getId())) {
                throw new IllegalArgumentException("objeto ja cadastrado: " + objeto.getId());
            }
            objetos.put(objeto.getId(), objeto);
            return this;
        }

        public CatalogoDaSituacao construir() {
            return new CatalogoDaSituacao(
                    new LinkedHashMap<>(participantes), new LinkedHashMap<>(objetos));
        }
    }
}
