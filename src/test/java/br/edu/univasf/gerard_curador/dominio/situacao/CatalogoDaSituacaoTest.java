package br.edu.univasf.gerard_curador.dominio.situacao;

import br.edu.univasf.gerard_curador.dominio.narrativo.ParticipanteNarrativo;
import br.edu.univasf.gerard_curador.dominio.quantitativo.FamiliaObjeto;
import br.edu.univasf.gerard_curador.dominio.quantitativo.ObjetoContado;
import br.edu.univasf.gerard_curador.dominio.quantitativo.RealizacaoTextualObjeto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Catálogo de participantes e objetos da situação")
class CatalogoDaSituacaoTest {

    private final ParticipanteNarrativo vovo = SituacoesDeTeste.VOVO;
    private final ParticipanteNarrativo netinha = SituacoesDeTeste.NETINHA;
    private final ObjetoContado branca = SituacoesDeTeste.ROSA_BRANCA;
    private final ObjetoContado amarela = SituacoesDeTeste.ROSA_AMARELA;

    @Test
    @DisplayName("Cadastra e recupera participantes e objetos corretamente")
    void cadastraERecuperaParticipantesEObjetos() {
        CatalogoDaSituacao catalogo = CatalogoDaSituacao.construtor()
                .comParticipante(vovo)
                .comParticipante(netinha)
                .comObjeto(branca)
                .comObjeto(amarela)
                .construir();

        assertEquals(vovo, catalogo.participantePorId("vovo"));
        assertEquals(netinha, catalogo.participantePorId("netinha"));
        assertTrue(catalogo.contem(vovo));
        assertTrue(catalogo.contem(netinha));

        assertEquals(branca, catalogo.objetoPorId("rosa_branca"));
        assertEquals(amarela, catalogo.objetoPorId("rosa_amarela"));
        assertTrue(catalogo.contem(branca));
        assertTrue(catalogo.contem(amarela));

        assertEquals(2, catalogo.getParticipantes().size());
        assertEquals(2, catalogo.getObjetos().size());
    }

    @Test
    @DisplayName("Consulta por participante ou objeto inexistente devolve null ou false")
    void consultaInexistenteDevolveNullOuFalse() {
        CatalogoDaSituacao catalogo = CatalogoDaSituacao.construtor()
                .comParticipante(vovo)
                .comObjeto(branca)
                .construir();

        assertNull(catalogo.participantePorId("desconhecido"));
        assertNull(catalogo.participantePorId(null));
        assertFalse(catalogo.contem(netinha));
        assertFalse(catalogo.contem((ParticipanteNarrativo) null));

        assertNull(catalogo.objetoPorId("desconhecido"));
        assertNull(catalogo.objetoPorId(null));
        assertFalse(catalogo.contem(amarela));
        assertFalse(catalogo.contem((ObjetoContado) null));
    }

    @Test
    @DisplayName("Recusa cadastro de participante nulo ou repetido")
    void recusaParticipanteNuloOuRepetido() {
        assertThrows(IllegalArgumentException.class, () ->
                CatalogoDaSituacao.construtor().comParticipante(null));

        assertThrows(IllegalArgumentException.class, () ->
                CatalogoDaSituacao.construtor()
                        .comParticipante(vovo)
                        .comParticipante(new ParticipanteNarrativo("vovo", "Outra Vovó")));
    }

    @Test
    @DisplayName("Recusa cadastro de objeto contado nulo ou repetido")
    void recusaObjetoNuloOuRepetido() {
        assertThrows(IllegalArgumentException.class, () ->
                CatalogoDaSituacao.construtor().comObjeto(null));

        assertThrows(IllegalArgumentException.class, () ->
                CatalogoDaSituacao.construtor()
                        .comObjeto(branca)
                        .comObjeto(new ObjetoContado(
                                "rosa_branca",
                                SituacoesDeTeste.FAMILIA_ROSA,
                                Set.of(),
                                RealizacaoTextualObjeto.emPortugues("outra branca", "outras"),
                                null
                        )));
    }

    @Test
    @DisplayName("Filtra objetos por família e lista famílias distintas na ordem de cadastro")
    void filtraObjetosPorFamiliaEListaFamilias() {
        FamiliaObjeto familiaFigurinha = new FamiliaObjeto("figurinha", "Figurinha");
        ObjetoContado figurinha = new ObjetoContado(
                "fig_futebol",
                familiaFigurinha,
                Set.of(),
                RealizacaoTextualObjeto.emPortugues("figurinha de futebol", "figurinhas de futebol"),
                null
        );

        CatalogoDaSituacao catalogo = CatalogoDaSituacao.construtor()
                .comObjeto(branca)
                .comObjeto(amarela)
                .comObjeto(figurinha)
                .construir();

        List<ObjetoContado> rosas = catalogo.objetosDaFamilia(SituacoesDeTeste.FAMILIA_ROSA);
        assertEquals(2, rosas.size());
        assertTrue(rosas.contains(branca));
        assertTrue(rosas.contains(amarela));

        List<ObjetoContado> figurinhas = catalogo.objetosDaFamilia(familiaFigurinha);
        assertEquals(1, figurinhas.size());
        assertTrue(figurinhas.contains(figurinha));

        List<FamiliaObjeto> familias = catalogo.getFamilias();
        assertEquals(2, familias.size());
        assertEquals(SituacoesDeTeste.FAMILIA_ROSA, familias.get(0));
        assertEquals(familiaFigurinha, familias.get(1));
    }
}
