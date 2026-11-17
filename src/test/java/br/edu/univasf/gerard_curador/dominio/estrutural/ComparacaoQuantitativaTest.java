package br.edu.univasf.gerard_curador.dominio.estrutural;

import br.edu.univasf.gerard_curador.dominio.narrativo.ParticipanteNarrativo;
import br.edu.univasf.gerard_curador.dominio.quantitativo.*;
import br.edu.univasf.gerard_curador.dominio.validacao.ResultadoValidacao;
import br.edu.univasf.gerard_curador.dominio.validacao.TipoDiagnostico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Comparação Quantitativa")
class ComparacaoQuantitativaTest {

    private ParticipanteNarrativo paulo;
    private ParticipanteNarrativo jose;
    private ObjetoContado bola;
    private ObjetoContado dinheiro;

    @BeforeEach
    void setUp() {
        paulo = new ParticipanteNarrativo("paulo", "Paulo");
        jose = new ParticipanteNarrativo("jose", "José");
        bola = new ObjetoContado(
                "bola",
                new FamiliaObjeto("bola", "Bola"),
                Set.of(),
                RealizacaoTextualObjeto.emPortugues("bola", "bolas"),
                null
        );
        dinheiro = new ObjetoContado(
                "dinheiro",
                new FamiliaObjeto("dinheiro", "Dinheiro"),
                Set.of(),
                RealizacaoTextualObjeto.emPortugues("dinheiro", "dinheiro"),
                null
        );
    }

    @Test
    @DisplayName("Paulo 6, José 8 a mais resolve REFERIDO = 14")
    void paulo6Jose8AMaisResolveReferido14() {
        ComparacaoQuantitativa relacao = ComparacaoQuantitativa.construtor("comparacao-bolas")
                .comReferente(paulo, bola.contar(6))
                .comReferido(jose, null)
                .comValorRelativo(bola.contar(8), ComparacaoQuantitativa.Direcao.A_MAIS)
                .construir();

        Numero referido = relacao.resolver(PapelQuantitativo.REFERIDO);
        assertEquals(Numero.de(14), referido);
        assertEquals("COMPARACAO", relacao.getCategoria());
        assertEquals(ComparacaoQuantitativa.Direcao.A_MAIS, relacao.getDirecao());
        assertEquals(paulo, relacao.getParticipanteReferente());
        assertEquals(jose, relacao.getParticipanteReferido());
    }

    @Test
    @DisplayName("Referente e referido com o mesmo participante gera ENUNCIADO_AMBIGUO")
    void referenteEReferidoIguaisGeraEnunciadoAmbiguo() {
        ComparacaoQuantitativa relacao = ComparacaoQuantitativa.construtor("ambiguo")
                .comReferente(paulo, bola.contar(6))
                .comReferido(paulo, bola.contar(14))
                .comValorRelativo(bola.contar(8), ComparacaoQuantitativa.Direcao.A_MAIS)
                .construir();

        ResultadoValidacao resultado = relacao.validar();

        assertTrue(resultado.contem(TipoDiagnostico.ENUNCIADO_AMBIGUO));
        assertTrue(resultado.possuiAvisos());
    }

    @Test
    @DisplayName("Grandezas ou unidades incompatíveis entre papéis geram GRANDEZAS_OU_UNIDADES_INCOMPATIVEIS (Caso 15)")
    void grandezasOuUnidadesIncompativeisGeramDiagnostico() {
        Quantidade referenteBolas = bola.contar(6);
        Quantidade relativoReais = new Quantidade(
                Numero.de(8), dinheiro, GrandezaQuantitativa.VALOR_MONETARIO, UnidadeMedida.REAL);

        ComparacaoQuantitativa relacao = ComparacaoQuantitativa.construtor("incompativel")
                .comReferente(paulo, referenteBolas)
                .comReferido(jose, null)
                .comValorRelativo(relativoReais, ComparacaoQuantitativa.Direcao.A_MAIS)
                .construir();

        ResultadoValidacao resultado = relacao.validar();

        assertFalse(resultado.ehValido());
        assertTrue(resultado.contem(TipoDiagnostico.GRANDEZAS_OU_UNIDADES_INCOMPATIVEIS));
    }

    @Test
    @DisplayName("Referido curado divergente gera ESTADO_CALCULADO_DIVERGENTE sem alterar valor curado")
    void referidoDivergenteGeraDiagnostico() {
        ComparacaoQuantitativa relacao = ComparacaoQuantitativa.construtor("divergente")
                .comReferente(paulo, bola.contar(6))
                .comReferido(jose, bola.contar(20))
                .comValorRelativo(bola.contar(8), ComparacaoQuantitativa.Direcao.A_MAIS)
                .construir();

        ResultadoValidacao resultado = relacao.validar();

        assertFalse(resultado.ehValido());
        assertTrue(resultado.contem(TipoDiagnostico.ESTADO_CALCULADO_DIVERGENTE));
        assertEquals(20, relacao.quantidadeDoPapel(PapelQuantitativo.REFERIDO).getValor(),
                "Valor curado do referido deve ser preservado");
    }

    @Test
    @DisplayName("Resolve referente e valor relativo nas direções A_MAIS e A_MENOS")
    void resolverReferenteEValorRelativo() {
        // Referente desconhecido: José tem 14, 8 a mais que Paulo -> Paulo tem 6
        ComparacaoQuantitativa relacaoReferente = ComparacaoQuantitativa.construtor("desc-referente")
                .comReferente(paulo, null)
                .comReferido(jose, bola.contar(14))
                .comValorRelativo(bola.contar(8), ComparacaoQuantitativa.Direcao.A_MAIS)
                .construir();
        assertEquals(Numero.de(6), relacaoReferente.resolver(PapelQuantitativo.REFERENTE));

        // Valor relativo desconhecido: José tem 14, Paulo tem 6 -> 8 a mais
        ComparacaoQuantitativa relacaoRelativo = ComparacaoQuantitativa.construtor("desc-relativo")
                .comReferente(paulo, bola.contar(6))
                .comReferido(jose, bola.contar(14))
                .comValorRelativo(null, ComparacaoQuantitativa.Direcao.A_MAIS)
                .construir();
        assertEquals(Numero.de(8), relacaoRelativo.resolver(PapelQuantitativo.VALOR_RELATIVO));

        // Direção A_MENOS: Jammes tem 12 a menos que Gisele (18) -> Jammes tem 6
        ParticipanteNarrativo gisele = new ParticipanteNarrativo("gisele", "Gisele");
        ParticipanteNarrativo jammes = new ParticipanteNarrativo("jammes", "Jammes");
        ComparacaoQuantitativa relacaoAMenos = ComparacaoQuantitativa.construtor("a-menos")
                .comReferente(gisele, bola.contar(18))
                .comReferido(jammes, null)
                .comValorRelativo(bola.contar(12), ComparacaoQuantitativa.Direcao.A_MENOS)
                .construir();
        assertEquals(Numero.de(6), relacaoAMenos.resolver(PapelQuantitativo.REFERIDO));
    }
}
