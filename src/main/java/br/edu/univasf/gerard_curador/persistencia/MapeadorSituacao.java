package br.edu.univasf.gerard_curador.persistencia;

import br.edu.univasf.gerard_curador.dominio.estrutural.ComparacaoQuantitativa;
import br.edu.univasf.gerard_curador.dominio.estrutural.ComposicaoDeTransformacoes;
import br.edu.univasf.gerard_curador.dominio.estrutural.ComposicaoParteTodo;
import br.edu.univasf.gerard_curador.dominio.estrutural.IncognitaQuantitativa;
import br.edu.univasf.gerard_curador.dominio.estrutural.PapelQuantitativo;
import br.edu.univasf.gerard_curador.dominio.estrutural.RelacaoEstrutural;
import br.edu.univasf.gerard_curador.dominio.narrativo.AcaoNarrativa;
import br.edu.univasf.gerard_curador.dominio.narrativo.ContextoNarrativo;
import br.edu.univasf.gerard_curador.dominio.narrativo.EstadoDePosse;
import br.edu.univasf.gerard_curador.dominio.narrativo.Inventario;
import br.edu.univasf.gerard_curador.dominio.narrativo.MarcadorTemporal;
import br.edu.univasf.gerard_curador.dominio.narrativo.ParticipanteNarrativo;
import br.edu.univasf.gerard_curador.dominio.narrativo.ReferenciaVisualParticipante;
import br.edu.univasf.gerard_curador.dominio.narrativo.SentidoTransformacao;
import br.edu.univasf.gerard_curador.dominio.narrativo.TransformacaoNarrativa;
import br.edu.univasf.gerard_curador.dominio.quantitativo.CaracteristicaObjeto;
import br.edu.univasf.gerard_curador.dominio.quantitativo.FamiliaObjeto;
import br.edu.univasf.gerard_curador.dominio.quantitativo.GrandezaQuantitativa;
import br.edu.univasf.gerard_curador.dominio.quantitativo.Numero;
import br.edu.univasf.gerard_curador.dominio.quantitativo.ObjetoContado;
import br.edu.univasf.gerard_curador.dominio.quantitativo.Quantidade;
import br.edu.univasf.gerard_curador.dominio.quantitativo.RealizacaoTextualObjeto;
import br.edu.univasf.gerard_curador.dominio.quantitativo.ReferenciaVisual;
import br.edu.univasf.gerard_curador.dominio.quantitativo.UnidadeMedida;
import br.edu.univasf.gerard_curador.dominio.situacao.AmbiguidadeDeclarada;
import br.edu.univasf.gerard_curador.dominio.situacao.CatalogoDaSituacao;
import br.edu.univasf.gerard_curador.dominio.situacao.EscopoDaSituacao;
import br.edu.univasf.gerard_curador.dominio.situacao.SituacaoProblema;
import br.edu.univasf.gerard_curador.dominio.situacao.StatusCuradoria;
import br.edu.univasf.gerard_curador.persistencia.dto.AcaoDto;
import br.edu.univasf.gerard_curador.persistencia.dto.AmbiguidadeDto;
import br.edu.univasf.gerard_curador.persistencia.dto.ContextoDto;
import br.edu.univasf.gerard_curador.persistencia.dto.EstadoDto;
import br.edu.univasf.gerard_curador.persistencia.dto.IncognitaDto;
import br.edu.univasf.gerard_curador.persistencia.dto.ObjetoContadoDto;
import br.edu.univasf.gerard_curador.persistencia.dto.ParticipanteDto;
import br.edu.univasf.gerard_curador.persistencia.dto.QuantidadeDto;
import br.edu.univasf.gerard_curador.persistencia.dto.RealizacaoTextualDto;
import br.edu.univasf.gerard_curador.persistencia.dto.RelacaoDto;
import br.edu.univasf.gerard_curador.persistencia.dto.SituacaoDto;
import br.edu.univasf.gerard_curador.persistencia.dto.TransformacaoDto;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Converte entre o modelo rico do dominio e os DTOs anemicos da persistencia.
 *
 * <p>Usa exclusivamente a API publica do dominio e nao recalcula nenhum valor:
 * o estado final curado e lido exatamente como esta no DTO para que eventuais
 * divergencias sejam preservadas e apontadas pela validacao do dominio.
 */
public final class MapeadorSituacao {

    /**
     * Converte uma situacao do dominio rico para seu DTO anemico serializavel.
     *
     * @param situacao objeto de dominio
     * @return DTO equivalente
     */
    public SituacaoDto paraDto(SituacaoProblema situacao) {
        if (situacao == null) {
            throw new IllegalArgumentException("mapeamento exige situacao");
        }
        String categoria = situacao.getRelacaoEstrutural() == null
                ? "NAO_DECLARADA"
                : situacao.getRelacaoEstrutural().getCategoria();

        return new SituacaoDto(
                situacao.getId(),
                situacao.getEnunciado(),
                categoria,
                situacao.getEscopo().name(),
                mapearParticipantesParaDto(situacao.getCatalogo()),
                mapearObjetosParaDto(situacao.getCatalogo()),
                mapearContextoParaDto(situacao.getContexto()),
                mapearIncognitaParaDto(situacao.getIncognita()),
                situacao.getEstadoInicial() == null
                        ? null
                        : mapearEstadoParaDto(situacao.getEstadoInicial()),
                mapearTransformacoesParaDto(situacao.getTransformacoes()),
                situacao.getEstadoFinalCurado() == null
                        ? null
                        : mapearEstadoParaDto(situacao.getEstadoFinalCurado()),
                mapearRelacaoParaDto(situacao.getRelacaoEstrutural()),
                mapearAmbiguidadesParaDto(situacao.getAmbiguidades()),
                situacao.getStatus().name(),
                situacao.getRevisor(),
                situacao.getMotivoDaRecusa());
    }

    /**
     * Reconstroi uma situacao do dominio rico a partir de um DTO anemico.
     *
     * <p>Monta primeiro o catalogo de participantes e objetos e resolve todas as
     * referencias por identificador, lancando {@link ErroDeLeituraJson} quando
     * alguma referencia obrigatoria nao existe.
     *
     * @param dto DTO lido do JSON
     * @return situacao de dominio reconstruida sem recalculo
     */
    public SituacaoProblema paraDominio(SituacaoDto dto) {
        if (dto == null) {
            throw new ErroDeLeituraJson("documento JSON vazio ou nulo");
        }
        String id = exigirTexto(dto.getId(), "id");
        String enunciado = exigirTexto(dto.getEnunciado(), "enunciado");
        EscopoDaSituacao escopo = lerEscopo(dto.getEscopo());

        CatalogoDaSituacao catalogo = lerCatalogo(dto);

        EstadoDePosse estadoInicial = null;
        if (dto.getEstadoInicial() != null) {
            estadoInicial = lerEstado(dto.getEstadoInicial(), catalogo, "estado_inicial");
        } else if (escopo.permiteModeloAditivo()) {
            throw new ErroDeLeituraJson("campo obrigatorio ausente: estado_inicial");
        }

        EstadoDePosse estadoFinal = dto.getEstadoFinalCurado() == null
                ? null
                : lerEstado(dto.getEstadoFinalCurado(), catalogo, "estado_final_curado");

        SituacaoProblema.Construtor construtor = SituacaoProblema.construtor(id, enunciado)
                .comCatalogo(catalogo)
                .comEstadoInicial(estadoInicial)
                .comEstadoFinalCurado(estadoFinal)
                .comContexto(lerContexto(dto.getContexto()))
                .comIncognita(lerIncognita(dto.getIncognita(), catalogo))
                .comRelacaoEstrutural(lerRelacao(dto.getRelacao(), catalogo, id));

        if (!escopo.permiteModeloAditivo()) {
            construtor.rotuladaForaDoEscopo();
        }
        if (dto.getAmbiguidades() != null) {
            for (AmbiguidadeDto ambiguidade : dto.getAmbiguidades()) {
                if (ambiguidade == null) {
                    throw new ErroDeLeituraJson("elemento nulo em ambiguidades");
                }
                construtor.comAmbiguidade(
                        exigirTexto(ambiguidade.getTrecho(), "ambiguidades.trecho"),
                        exigirTexto(ambiguidade.getMotivo(), "ambiguidades.motivo"));
            }
        }
        for (TransformacaoNarrativa transformacao : lerTransformacoes(dto.getTransformacoes(), catalogo)) {
            construtor.comTransformacao(transformacao);
        }

        SituacaoProblema situacao = construtor.construir();
        return aplicarStatus(situacao, dto.getStatus(), dto.getRevisor(), dto.getMotivoRecusa());
    }

    // ------------------------------------------------------------------
    // Dominio -> DTO
    // ------------------------------------------------------------------

    private List<ParticipanteDto> mapearParticipantesParaDto(CatalogoDaSituacao catalogo) {
        List<ParticipanteDto> lista = new ArrayList<>();
        for (ParticipanteNarrativo participante : catalogo.getParticipantes()) {
            lista.add(new ParticipanteDto(
                    participante.getId(),
                    participante.getNomeExibido(),
                    participante.getReferenciaVisual().getChave()));
        }
        return lista;
    }

    private List<ObjetoContadoDto> mapearObjetosParaDto(CatalogoDaSituacao catalogo) {
        List<ObjetoContadoDto> lista = new ArrayList<>();
        for (ObjetoContado objeto : catalogo.getObjetos()) {
            Map<String, String> caracteristicas = new LinkedHashMap<>();
            for (CaracteristicaObjeto caracteristica : objeto.getCaracteristicas()) {
                caracteristicas.put(caracteristica.getDimensao(), caracteristica.getValor());
            }
            RealizacaoTextualObjeto rt = objeto.getRealizacaoTextual();
            lista.add(new ObjetoContadoDto(
                    objeto.getId(),
                    objeto.getFamilia().getId(),
                    objeto.getFamilia().getNomeConceitual(),
                    caracteristicas,
                    new RealizacaoTextualDto(rt.getSingular(), rt.getPlural(), rt.getIdioma()),
                    objeto.getReferenciaVisual().getChave()));
        }
        return lista;
    }

    private ContextoDto mapearContextoParaDto(ContextoNarrativo contexto) {
        return new ContextoDto(contexto.getLocal(), contexto.getSituacao(), contexto.getMomento());
    }

    private IncognitaDto mapearIncognitaParaDto(IncognitaQuantitativa incognita) {
        if (incognita == null) {
            return null;
        }
        return new IncognitaDto(
                incognita.getPapelOriginal().name(),
                incognita.getDescricaoDaPergunta(),
                incognita.estaResolvida(),
                incognita.estaResolvida() ? incognita.getValorResolvido().getValor() : null,
                incognita.estaResolvida() ? incognita.getValorResolvido().getObjeto().getId() : null);
    }

    private EstadoDto mapearEstadoParaDto(EstadoDePosse estado) {
        List<QuantidadeDto> itens = new ArrayList<>();
        for (Quantidade quantidade : estado.getInventario().getItens()) {
            itens.add(mapearQuantidadeParaDto(quantidade));
        }
        return new EstadoDto(
                estado.getPossuidor().getId(),
                estado.getMomento().getOrdem(),
                estado.getMomento().getRotulo(),
                itens);
    }

    private QuantidadeDto mapearQuantidadeParaDto(Quantidade quantidade) {
        return new QuantidadeDto(
                quantidade.getObjeto().getId(),
                quantidade.getValor(),
                quantidade.getGrandeza().name(),
                quantidade.getUnidade().name());
    }

    private List<TransformacaoDto> mapearTransformacoesParaDto(
            List<TransformacaoNarrativa> transformacoes) {
        List<TransformacaoDto> lista = new ArrayList<>();
        for (TransformacaoNarrativa transformacao : transformacoes) {
            AcaoNarrativa acao = transformacao.getAcao();
            AcaoDto acaoDto = new AcaoDto(
                    acao.getId(),
                    acao.getVerbo(),
                    acao.getParticipio(),
                    acao.getSentido().name(),
                    acao.exigeDestinatario(),
                    acao.getPreposicaoDoDestinatario());
            lista.add(new TransformacaoDto(
                    transformacao.getOrdem(),
                    transformacao.getMarcador().getRotulo(),
                    transformacao.getAtor().getId(),
                    transformacao.possuiDestinatario()
                            ? transformacao.getDestinatario().getId()
                            : null,
                    acaoDto,
                    mapearQuantidadeParaDto(transformacao.getQuantidade())));
        }
        return lista;
    }

    private List<AmbiguidadeDto> mapearAmbiguidadesParaDto(List<AmbiguidadeDeclarada> ambiguidades) {
        List<AmbiguidadeDto> lista = new ArrayList<>();
        for (AmbiguidadeDeclarada ambiguidade : ambiguidades) {
            lista.add(new AmbiguidadeDto(ambiguidade.getTrecho(), ambiguidade.getMotivo()));
        }
        return lista;
    }

    private RelacaoDto mapearRelacaoParaDto(RelacaoEstrutural relacao) {
        if (relacao == null) {
            return null;
        }
        Map<String, QuantidadeDto> papeis = new LinkedHashMap<>();
        for (Map.Entry<PapelQuantitativo, Quantidade> entrada : relacao.getPapeis().entrySet()) {
            papeis.put(entrada.getKey().name(), mapearQuantidadeParaDto(entrada.getValue()));
        }

        String sentidoPrimeira = null;
        String sentidoSegunda = null;
        String sentidoResultante = null;
        String participanteReferente = null;
        String participanteReferido = null;
        String direcao = null;
        List<QuantidadeDto> partes = null;
        QuantidadeDto todo = null;

        if (relacao instanceof ComposicaoDeTransformacoes comp) {
            sentidoPrimeira = comp.getSentidoPrimeira() == null
                    ? null : comp.getSentidoPrimeira().name();
            sentidoSegunda = comp.getSentidoSegunda() == null
                    ? null : comp.getSentidoSegunda().name();
            sentidoResultante = comp.getSentidoResultante() == null
                    ? null : comp.getSentidoResultante().name();
        } else if (relacao instanceof ComparacaoQuantitativa comp) {
            participanteReferente = comp.getParticipanteReferente() == null
                    ? null : comp.getParticipanteReferente().getId();
            participanteReferido = comp.getParticipanteReferido() == null
                    ? null : comp.getParticipanteReferido().getId();
            direcao = comp.getDirecao().name();
        } else if (relacao instanceof ComposicaoParteTodo pt) {
            partes = new ArrayList<>();
            for (Quantidade parte : pt.getPartes()) {
                partes.add(mapearQuantidadeParaDto(parte));
            }
            todo = pt.getTodo() == null ? null : mapearQuantidadeParaDto(pt.getTodo());
        }

        return new RelacaoDto(
                relacao.getId(),
                relacao.getCategoria(),
                papeis,
                sentidoPrimeira,
                sentidoSegunda,
                sentidoResultante,
                participanteReferente,
                participanteReferido,
                direcao,
                partes,
                todo);
    }

    // ------------------------------------------------------------------
    // DTO -> Dominio
    // ------------------------------------------------------------------

    private CatalogoDaSituacao lerCatalogo(SituacaoDto dto) {
        if (dto.getParticipantes() == null) {
            throw new ErroDeLeituraJson("campo obrigatorio ausente: participantes");
        }
        if (dto.getObjetosContados() == null) {
            throw new ErroDeLeituraJson("campo obrigatorio ausente: objetos_contados");
        }
        CatalogoDaSituacao.Construtor construtor = CatalogoDaSituacao.construtor();
        try {
            for (ParticipanteDto p : dto.getParticipantes()) {
                if (p == null) {
                    throw new ErroDeLeituraJson("participante nulo em participantes");
                }
                String id = exigirTexto(p.getId(), "participantes.id");
                String nome = exigirTexto(p.getNome(), "participantes.nome");
                ReferenciaVisualParticipante refVisual = (p.getReferenciaVisual() == null
                        || p.getReferenciaVisual().isBlank())
                        ? ReferenciaVisualParticipante.naoDefinida()
                        : ReferenciaVisualParticipante.de(p.getReferenciaVisual());
                construtor.comParticipante(new ParticipanteNarrativo(id, nome, refVisual));
            }

            Map<String, FamiliaObjeto> familias = new LinkedHashMap<>();
            for (ObjetoContadoDto obj : dto.getObjetosContados()) {
                if (obj == null) {
                    throw new ErroDeLeituraJson("objeto nulo em objetos_contados");
                }
                String id = exigirTexto(obj.getId(), "objetos_contados.id");
                String idFamilia = exigirTexto(obj.getFamilia(), "objetos_contados.familia");
                String nomeFamilia = (obj.getFamiliaNome() == null || obj.getFamiliaNome().isBlank())
                        ? idFamilia
                        : obj.getFamiliaNome();
                FamiliaObjeto familia = familias.computeIfAbsent(
                        idFamilia, chave -> new FamiliaObjeto(chave, nomeFamilia));

                Set<CaracteristicaObjeto> caracteristicas = new LinkedHashSet<>();
                if (obj.getCaracteristicas() != null) {
                    for (Map.Entry<String, String> entrada : obj.getCaracteristicas().entrySet()) {
                        caracteristicas.add(CaracteristicaObjeto.de(
                                exigirTexto(entrada.getKey(), "caracteristicas.dimensao"),
                                exigirTexto(entrada.getValue(), "caracteristicas.valor")));
                    }
                }

                RealizacaoTextualDto rtDto = obj.getRealizacaoTextual();
                if (rtDto == null) {
                    throw new ErroDeLeituraJson(
                            "campo obrigatorio ausente: objetos_contados.realizacao_textual");
                }
                RealizacaoTextualObjeto realizacaoTextual = new RealizacaoTextualObjeto(
                        exigirTexto(rtDto.getSingular(), "realizacao_textual.singular"),
                        exigirTexto(rtDto.getPlural(), "realizacao_textual.plural"),
                        exigirTexto(rtDto.getIdioma(), "realizacao_textual.idioma"));

                ReferenciaVisual refVisual = (obj.getReferenciaVisual() == null
                        || obj.getReferenciaVisual().isBlank())
                        ? ReferenciaVisual.naoDefinida()
                        : ReferenciaVisual.de(obj.getReferenciaVisual());

                construtor.comObjeto(new ObjetoContado(
                        id, familia, caracteristicas, realizacaoTextual, refVisual));
            }
            return construtor.construir();
        } catch (IllegalArgumentException erro) {
            throw new ErroDeLeituraJson("catalogo invalido: " + erro.getMessage(), erro);
        }
    }

    private ContextoNarrativo lerContexto(ContextoDto dto) {
        if (dto == null) {
            return ContextoNarrativo.naoInformado();
        }
        return new ContextoNarrativo(dto.getLocal(), dto.getSituacao(), dto.getMomento());
    }

    private IncognitaQuantitativa lerIncognita(IncognitaDto dto, CatalogoDaSituacao catalogo) {
        if (dto == null) {
            return null;
        }
        PapelQuantitativo papel = lerEnumerado(
                PapelQuantitativo.class,
                exigirTexto(dto.getPapel(), "incognita.papel"),
                "papel na incognita");
        IncognitaQuantitativa incognita = IncognitaQuantitativa.designar(
                papel, exigirTexto(dto.getPergunta(), "incognita.pergunta"));

        if (Boolean.TRUE.equals(dto.getResolvida())) {
            String idObjeto = exigirTexto(dto.getObjetoResolvido(), "incognita.objeto_resolvido");
            ObjetoContado objeto = exigirObjeto(catalogo, idObjeto, "incognita.objeto_resolvido");
            Integer valor = exigirInteiro(dto.getValorResolvido(), "incognita.valor_resolvido");
            incognita.resolver(objeto.contar(valor));
        }
        return incognita;
    }

    private EstadoDePosse lerEstado(EstadoDto dto, CatalogoDaSituacao catalogo, String contexto) {
        String idPossuidor = exigirTexto(dto.getPossuidor(), contexto + ".possuidor");
        ParticipanteNarrativo possuidor = exigirParticipante(
                catalogo, idPossuidor, contexto + ".possuidor");
        int ordem = exigirInteiro(dto.getOrdemTemporal(), contexto + ".ordem_temporal");
        String rotulo = exigirTexto(dto.getRotuloTemporal(), contexto + ".rotulo_temporal");
        if (dto.getItens() == null) {
            throw new ErroDeLeituraJson("campo obrigatorio ausente: " + contexto + ".itens");
        }

        MarcadorTemporal momento;
        try {
            momento = new MarcadorTemporal(ordem, rotulo);
        } catch (IllegalArgumentException erro) {
            throw new ErroDeLeituraJson(contexto + ": " + erro.getMessage(), erro);
        }

        Inventario inventario = Inventario.vazio();
        for (QuantidadeDto item : dto.getItens()) {
            inventario = inventario.adicionar(lerQuantidade(item, catalogo, contexto + ".itens"));
        }
        return new EstadoDePosse(possuidor, inventario, momento);
    }

    private Quantidade lerQuantidade(QuantidadeDto dto,
                                     CatalogoDaSituacao catalogo,
                                     String contexto) {
        if (dto == null) {
            throw new ErroDeLeituraJson("quantidade nula em " + contexto);
        }
        String idObjeto = exigirTexto(dto.getObjeto(), contexto + ".objeto");
        ObjetoContado objeto = exigirObjeto(catalogo, idObjeto, contexto);
        int numero = exigirInteiro(dto.getNumero(), contexto + ".numero");
        GrandezaQuantitativa grandeza = lerEnumerado(
                GrandezaQuantitativa.class,
                exigirTexto(dto.getGrandeza(), contexto + ".grandeza"),
                "grandeza");
        UnidadeMedida unidade = lerEnumerado(
                UnidadeMedida.class,
                exigirTexto(dto.getUnidade(), contexto + ".unidade"),
                "unidade");
        try {
            return objeto.quantificar(Numero.de(numero), grandeza, unidade);
        } catch (IllegalArgumentException erro) {
            throw new ErroDeLeituraJson(contexto + ": " + erro.getMessage(), erro);
        }
    }

    private List<TransformacaoNarrativa> lerTransformacoes(List<TransformacaoDto> lista,
                                                           CatalogoDaSituacao catalogo) {
        List<TransformacaoNarrativa> transformacoes = new ArrayList<>();
        if (lista == null) {
            return transformacoes;
        }
        for (TransformacaoDto item : lista) {
            if (item == null) {
                throw new ErroDeLeituraJson("transformacao nula em transformacoes");
            }
            int ordem = exigirInteiro(item.getOrdem(), "transformacoes.ordem");
            String elemento = "transformacao " + ordem;
            String marcador = exigirTexto(item.getMarcador(), elemento + ".marcador");

            ParticipanteNarrativo ator = exigirParticipante(
                    catalogo, exigirTexto(item.getAtor(), elemento + ".ator"), elemento + ".ator");
            ParticipanteNarrativo destinatario = (item.getDestinatario() == null
                    || item.getDestinatario().isBlank())
                    ? null
                    : exigirParticipante(catalogo, item.getDestinatario(), elemento + ".destinatario");

            AcaoDto acaoDto = item.getAcao();
            if (acaoDto == null) {
                throw new ErroDeLeituraJson("campo obrigatorio ausente: " + elemento + ".acao");
            }
            SentidoTransformacao sentido = lerEnumerado(
                    SentidoTransformacao.class,
                    exigirTexto(acaoDto.getSentido(), elemento + ".acao.sentido"),
                    "sentido");
            if (acaoDto.getExigeDestinatario() == null) {
                throw new ErroDeLeituraJson(
                        "campo obrigatorio ausente: " + elemento + ".acao.exige_destinatario");
            }
            boolean exigeDestinatario = acaoDto.getExigeDestinatario();
            String idAcao = exigirTexto(acaoDto.getId(), elemento + ".acao.id");
            String verbo = exigirTexto(acaoDto.getVerbo(), elemento + ".acao.verbo");
            String participio = exigirTexto(acaoDto.getParticipio(), elemento + ".acao.participio");
            String preposicao = acaoDto.getPreposicaoDestinatario();

            try {
                AcaoNarrativa acao = (preposicao == null || preposicao.isBlank())
                        ? new AcaoNarrativa(idAcao, verbo, participio, sentido, exigeDestinatario)
                        : new AcaoNarrativa(idAcao, verbo, participio, sentido, exigeDestinatario,
                                preposicao);

                Quantidade quantidade = lerQuantidade(
                        item.getQuantidade(), catalogo, elemento + ".quantidade");
                transformacoes.add(new TransformacaoNarrativa(
                        ator, acao, destinatario, quantidade,
                        new MarcadorTemporal(ordem, marcador)));
            } catch (IllegalArgumentException erro) {
                throw new ErroDeLeituraJson(elemento + ": " + erro.getMessage(), erro);
            }
        }
        return transformacoes;
    }

    private RelacaoEstrutural lerRelacao(RelacaoDto dto,
                                         CatalogoDaSituacao catalogo,
                                         String idSituacao) {
        if (dto == null) {
            return null;
        }
        String categoria = exigirTexto(dto.getCategoria(), "relacao.categoria");
        String idRelacao = (dto.getId() == null || dto.getId().isBlank()) ? idSituacao : dto.getId();
        Map<String, QuantidadeDto> papeis = dto.getPapeis();

        return switch (categoria) {
            case "COMPOSICAO_TRANSFORMACOES" -> lerComposicaoTransformacoes(dto, papeis, catalogo, idRelacao);
            case "COMPARACAO" -> lerComparacao(dto, papeis, catalogo, idRelacao);
            case "COMPOSICAO_PARTE_TODO" -> lerParteTodo(dto, papeis, catalogo, idRelacao);
            default -> throw new ErroDeLeituraJson(
                    "categoria de relacao desconhecida: " + categoria);
        };
    }

    private ComposicaoDeTransformacoes lerComposicaoTransformacoes(RelacaoDto dto,
                                                                   Map<String, QuantidadeDto> papeis,
                                                                   CatalogoDaSituacao catalogo,
                                                                   String idRelacao) {
        ComposicaoDeTransformacoes.Construtor construtor =
                ComposicaoDeTransformacoes.construtor(idRelacao);
        if (papeis != null) {
            for (Map.Entry<String, QuantidadeDto> entrada : papeis.entrySet()) {
                PapelQuantitativo papel = lerEnumerado(
                        PapelQuantitativo.class, entrada.getKey(), "papel na relacao");
                Quantidade quantidade = lerQuantidade(
                        entrada.getValue(), catalogo, "relacao.papeis." + entrada.getKey());
                switch (papel) {
                    case ESTADO_INICIAL -> construtor.comEstadoInicial(quantidade);
                    case ESTADO_INTERMEDIARIO -> construtor.comEstadoIntermediario(quantidade);
                    case ESTADO_FINAL -> construtor.comEstadoFinal(quantidade);
                    case TRANSFORMACAO_PRIMEIRA -> construtor.comPrimeiraTransformacao(
                            quantidade,
                            lerSentidoOuPadrao(dto.getSentidoPrimeira(), "relacao.sentido_primeira"));
                    case TRANSFORMACAO_SEGUNDA -> construtor.comSegundaTransformacao(
                            quantidade,
                            lerSentidoOuPadrao(dto.getSentidoSegunda(), "relacao.sentido_segunda"));
                    case TRANSFORMACAO_RESULTANTE -> construtor.comTransformacaoResultante(
                            quantidade,
                            lerSentidoOuPadrao(dto.getSentidoResultante(), "relacao.sentido_resultante"));
                    default -> throw new ErroDeLeituraJson(
                            "papel invalido para COMPOSICAO_TRANSFORMACOES: " + papel);
                }
            }
        }
        return construtor.construir();
    }

    private ComparacaoQuantitativa lerComparacao(RelacaoDto dto,
                                                 Map<String, QuantidadeDto> papeis,
                                                 CatalogoDaSituacao catalogo,
                                                 String idRelacao) {
        ComparacaoQuantitativa.Construtor construtor = ComparacaoQuantitativa.construtor(idRelacao);

        ParticipanteNarrativo referente = (dto.getParticipanteReferente() == null
                || dto.getParticipanteReferente().isBlank())
                ? null
                : exigirParticipante(catalogo, dto.getParticipanteReferente(),
                        "relacao.participante_referente");
        ParticipanteNarrativo referido = (dto.getParticipanteReferido() == null
                || dto.getParticipanteReferido().isBlank())
                ? null
                : exigirParticipante(catalogo, dto.getParticipanteReferido(),
                        "relacao.participante_referido");

        Quantidade qReferente = (papeis != null && papeis.containsKey(PapelQuantitativo.REFERENTE.name()))
                ? lerQuantidade(papeis.get(PapelQuantitativo.REFERENTE.name()),
                        catalogo, "relacao.papeis.REFERENTE")
                : null;
        Quantidade qReferido = (papeis != null && papeis.containsKey(PapelQuantitativo.REFERIDO.name()))
                ? lerQuantidade(papeis.get(PapelQuantitativo.REFERIDO.name()),
                        catalogo, "relacao.papeis.REFERIDO")
                : null;
        Quantidade qRelativo = (papeis != null
                && papeis.containsKey(PapelQuantitativo.VALOR_RELATIVO.name()))
                ? lerQuantidade(papeis.get(PapelQuantitativo.VALOR_RELATIVO.name()),
                        catalogo, "relacao.papeis.VALOR_RELATIVO")
                : null;

        ComparacaoQuantitativa.Direcao direcao = (dto.getDirecao() == null || dto.getDirecao().isBlank())
                ? ComparacaoQuantitativa.Direcao.A_MAIS
                : lerEnumerado(ComparacaoQuantitativa.Direcao.class,
                        dto.getDirecao(), "relacao.direcao");

        construtor.comReferente(referente, qReferente);
        construtor.comReferido(referido, qReferido);
        construtor.comValorRelativo(qRelativo, direcao);
        return construtor.construir();
    }

    private ComposicaoParteTodo lerParteTodo(RelacaoDto dto,
                                             Map<String, QuantidadeDto> papeis,
                                             CatalogoDaSituacao catalogo,
                                             String idRelacao) {
        ComposicaoParteTodo.Construtor construtor = ComposicaoParteTodo.construtor(idRelacao);
        if (dto.getPartes() != null) {
            for (QuantidadeDto parteDto : dto.getPartes()) {
                construtor.comParte(lerQuantidade(parteDto, catalogo, "relacao.partes"));
            }
        } else if (papeis != null && papeis.containsKey(PapelQuantitativo.PARTE.name())) {
            construtor.comParte(lerQuantidade(
                    papeis.get(PapelQuantitativo.PARTE.name()), catalogo, "relacao.papeis.PARTE"));
        }

        QuantidadeDto todoDto = dto.getTodo();
        if (todoDto == null && papeis != null) {
            todoDto = papeis.get(PapelQuantitativo.TODO.name());
        }
        if (todoDto != null) {
            construtor.comTodo(lerQuantidade(todoDto, catalogo, "relacao.todo"));
        }
        return construtor.construir();
    }

    private SituacaoProblema aplicarStatus(SituacaoProblema situacao,
                                           String status,
                                           String revisorGravado,
                                           String motivoGravado) {
        if (status == null || status.isBlank()
                || StatusCuradoria.CANDIDATA_NAO_CURADA.name().equals(status)) {
            return situacao;
        }
        String revisor = (revisorGravado == null || revisorGravado.isBlank())
                ? "revisor registrado no arquivo"
                : revisorGravado;
        if (StatusCuradoria.VALIDADA.name().equals(status)) {
            situacao.promoverParaValidada(revisor);
            return situacao;
        }
        if (StatusCuradoria.RECUSADA.name().equals(status)) {
            situacao.recusar(revisor,
                    motivoGravado == null || motivoGravado.isBlank()
                            ? "motivo registrado no arquivo" : motivoGravado);
            return situacao;
        }
        throw new ErroDeLeituraJson("status desconhecido: " + status);
    }

    private EscopoDaSituacao lerEscopo(String escopo) {
        if (escopo == null || escopo.isBlank()) {
            return EscopoDaSituacao.ADITIVO;
        }
        return lerEnumerado(EscopoDaSituacao.class, escopo, "escopo");
    }

    private SentidoTransformacao lerSentidoOuPadrao(String sentido, String campo) {
        if (sentido == null || sentido.isBlank()) {
            return SentidoTransformacao.ACRESCIMO;
        }
        return lerEnumerado(SentidoTransformacao.class, sentido, campo);
    }

    private ParticipanteNarrativo exigirParticipante(CatalogoDaSituacao catalogo,
                                                     String id,
                                                     String contexto) {
        ParticipanteNarrativo participante = catalogo.participantePorId(id);
        if (participante == null) {
            throw new ErroDeLeituraJson(
                    contexto + ": participante nao consta do catalogo: " + id);
        }
        return participante;
    }

    private ObjetoContado exigirObjeto(CatalogoDaSituacao catalogo,
                                       String id,
                                       String contexto) {
        ObjetoContado objeto = catalogo.objetoPorId(id);
        if (objeto == null) {
            throw new ErroDeLeituraJson(
                    contexto + ": objeto contado nao consta do catalogo: " + id);
        }
        return objeto;
    }

    private String exigirTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ErroDeLeituraJson("campo obrigatorio ausente ou vazio: " + campo);
        }
        return valor;
    }

    private int exigirInteiro(Integer valor, String campo) {
        if (valor == null) {
            throw new ErroDeLeituraJson("campo obrigatorio ausente: " + campo);
        }
        return valor;
    }

    private <T extends Enum<T>> T lerEnumerado(Class<T> tipo, String nome, String descricao) {
        try {
            return Enum.valueOf(tipo, nome);
        } catch (IllegalArgumentException erro) {
            throw new ErroDeLeituraJson(descricao + " desconhecido: " + nome, erro);
        }
    }
}
