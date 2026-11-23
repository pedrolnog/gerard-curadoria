package br.edu.univasf.gerard_curador.dominio.narrativo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Participante Narrativo da história")
class ParticipanteNarrativoTest {

    @Test
    @DisplayName("Igualdade de participantes é definida exclusivamente pela sua identidade (id)")
    void igualdadePorIdentidade() {
        ParticipanteNarrativo vovo1 = new ParticipanteNarrativo("vovo", "Vovó");
        ParticipanteNarrativo vovo2 = new ParticipanteNarrativo("vovo", "Vovó Maria");
        ParticipanteNarrativo outraPessoaMesmoNome = new ParticipanteNarrativo("outra_vovo", "Vovó");

        assertEquals(vovo1, vovo2);
        assertEquals(vovo1.hashCode(), vovo2.hashCode());
        assertNotEquals(vovo1, outraPessoaMesmoNome);
        assertNotEquals(vovo1, null);
        assertNotEquals(vovo1, "vovo");
    }

    @Test
    @DisplayName("Cria participante com referência visual padrão quando não informada")
    void participanteSemReferenciaVisualUsaNaoDefinida() {
        ParticipanteNarrativo vovo = new ParticipanteNarrativo("vovo", "Vovó");

        assertNotNull(vovo.getReferenciaVisual());
        assertFalse(vovo.getReferenciaVisual().estaDefinida());
        assertEquals("indefinida", vovo.getReferenciaVisual().getChave());
    }

    @Test
    @DisplayName("Cria participante com referência visual explícita")
    void participanteComReferenciaVisualExplicita() {
        ReferenciaVisualParticipante ref = ReferenciaVisualParticipante.de("avatar_vovo");
        ParticipanteNarrativo vovo = new ParticipanteNarrativo("vovo", "Vovó", ref);

        assertEquals(ref, vovo.getReferenciaVisual());
        assertTrue(vovo.getReferenciaVisual().estaDefinida());
        assertEquals("avatar_vovo", vovo.getReferenciaVisual().getChave());
    }

    @Test
    @DisplayName("Getters retornam os atributos curados e toString devolve o nome exibido")
    void atributosEToString() {
        ParticipanteNarrativo netinha = new ParticipanteNarrativo("netinha", "Netinha");

        assertEquals("netinha", netinha.getId());
        assertEquals("Netinha", netinha.getNomeExibido());
        assertEquals("Netinha", netinha.toString());
    }

    @Test
    @DisplayName("Rejeita id nulo ou em branco")
    void validaIdObrigatorio() {
        assertThrows(IllegalArgumentException.class, () -> new ParticipanteNarrativo(null, "Vovó"));
        assertThrows(IllegalArgumentException.class, () -> new ParticipanteNarrativo("", "Vovó"));
        assertThrows(IllegalArgumentException.class, () -> new ParticipanteNarrativo("   ", "Vovó"));
    }

    @Test
    @DisplayName("Rejeita nome exibido nulo ou em branco")
    void validaNomeExibidoObrigatorio() {
        assertThrows(IllegalArgumentException.class, () -> new ParticipanteNarrativo("vovo", null));
        assertThrows(IllegalArgumentException.class, () -> new ParticipanteNarrativo("vovo", ""));
        assertThrows(IllegalArgumentException.class, () -> new ParticipanteNarrativo("vovo", "   "));
    }

    @Test
    @DisplayName("setNomeExibido altera o nome e rejeita valores nulos ou em branco preservando o anterior")
    void setNomeExibidoAlteraERejeitaInvalido() {
        ParticipanteNarrativo vovo = new ParticipanteNarrativo("vovo", "Vovó");

        vovo.setNomeExibido("Vovó Maria");
        assertEquals("Vovó Maria", vovo.getNomeExibido());

        assertThrows(IllegalArgumentException.class, () -> vovo.setNomeExibido(null));
        assertEquals("Vovó Maria", vovo.getNomeExibido());

        assertThrows(IllegalArgumentException.class, () -> vovo.setNomeExibido("   "));
        assertEquals("Vovó Maria", vovo.getNomeExibido());
    }

    @Test
    @DisplayName("setReferenciaVisual normaliza nulo para referência visual não definida")
    void setReferenciaVisualNormalizaNulo() {
        ReferenciaVisualParticipante ref = ReferenciaVisualParticipante.de("avatar_vovo");
        ParticipanteNarrativo vovo = new ParticipanteNarrativo("vovo", "Vovó", ref);

        assertEquals(ref, vovo.getReferenciaVisual());

        vovo.setReferenciaVisual(null);
        assertEquals(ReferenciaVisualParticipante.naoDefinida(), vovo.getReferenciaVisual());
    }

    @Test
    @DisplayName("Identidade é imutável: dois participantes com mesmo id continuam iguais após mudar o nome de um deles")
    void igualdadePreservadaAposAlterarNome() {
        ParticipanteNarrativo vovo1 = new ParticipanteNarrativo("vovo", "Vovó");
        ParticipanteNarrativo vovo2 = new ParticipanteNarrativo("vovo", "Vovó");

        assertEquals(vovo1, vovo2);

        vovo1.setNomeExibido("Vovó Maria");
        assertEquals(vovo1, vovo2);
        assertEquals(vovo1.hashCode(), vovo2.hashCode());
    }
}
