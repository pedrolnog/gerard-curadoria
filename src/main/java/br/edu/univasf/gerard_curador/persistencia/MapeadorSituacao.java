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
        String id = exigirTexto(dto.id(), "id");
        String enunciado = exigirTexto(dto.enunciado(), "enunciado");
        EscopoDaSituacao escopo = lerEscopo(dto.escopo());

        CatalogoDaSituacao catalogo = lerCatalogo(dto);

        EstadoDePosse estadoInicial = null;
        if (dto.estadoInicial() != null) {
            estadoInicial = lerEstado(dto.estadoInicial(), catalogo, "estado_inicial");
        } else if (escopo.permiteModeloAditivo()) {
            throw new ErroDeLeituraJson("campo obrigatorio ausente: estado_inicial");
        }

        EstadoDePosse estadoFinal = dto.estadoFinalCurado() == null
                ? null
                : lerEstado(dto.estadoFinalCurado(), catalogo, "estado_final_curado");

        SituacaoProblema.Construtor construtor = SituacaoProblema.construtor(id, enunciado)
                .comCatalogo(catalogo)
                .comEstadoInicial(estadoInicial)
                .comEstadoFinalCurado(estadoFinal)
                .comContexto(lerContexto(dto.contexto()))
                .comIncognita(lerIncognita(dto.incognita(), catalogo))
                .comRelacaoEstrutural(lerRelacao(dto.relacao(), catalogo, id));

        if (!escopo.permiteModeloAditivo()) {
            construtor.rotuladaForaDoEscopo();
        }
        if (dto.ambiguidades() != null) {
            for (AmbiguidadeDto ambiguidade : dto.ambiguidades()) {
                if (ambiguidade == null) {
                    throw new ErroDeLeituraJson("elemento nulo em ambiguidades");
                }
                construtor.comAmbiguidade(
                        exigirTexto(ambiguidade.trecho(), "ambiguidades.trecho"),
                        exigirTexto(ambiguidade.motivo(), "ambiguidades.motivo"));
            }
        }
        for (TransformacaoNarrativa transformacao : lerTransformacoes(dto.transformacoes(), catalogo)) {
            construtor.comTransformacao(transformacao);
        }

        SituacaoProblema situacao = construtor.construir();
        return aplicarStatus(situacao, dto.status(), dto.revisor(), dto.motivoRecusa());
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
        if (dto.participantes() == null) {
            throw new ErroDeLeituraJson("campo obrigatorio ausente: participantes");
        }
        if (dto.objetosContados() == null) {
            throw new ErroDeLeituraJson("campo obrigatorio ausente: objetos_contados");
        }
        CatalogoDaSituacao.Construtor construtor = CatalogoDaSituacao.construtor();
        try {
            for (ParticipanteDto p : dto.participantes()) {
                if (p == null) {
                    throw new ErroDeLeituraJson("participante nulo em participantes");
                }
                String id = exigirTexto(p.id(), "participantes.id");
                String nome = exigirTexto(p.nome(), "participantes.nome");
                ReferenciaVisualParticipante refVisual = (p.referenciaVisual() == null
                        || p.referenciaVisual().isBlank())
                        ? ReferenciaVisualParticipante.naoDefinida()
                        : ReferenciaVisualParticipante.de(p.referenciaVisual());
                construtor.comParticipante(new ParticipanteNarrativo(id, nome, refVisual));
            }

            Map<String, FamiliaObjeto> familias = new LinkedHashMap<>();
            for (ObjetoContadoDto obj : dto.objetosContados()) {
                if (obj == null) {
                    throw new ErroDeLeituraJson("objeto nulo em objetos_contados");
                }
                String id = exigirTexto(obj.id(), "objetos_contados.id");
                String idFamilia = exigirTexto(obj.familia(), "objetos_contados.familia");
                String nomeFamilia = (obj.familiaNome() == null || obj.familiaNome().isBlank())
                        ? idFamilia
                        : obj.familiaNome();
                FamiliaObjeto familia = familias.computeIfAbsent(
                        idFamilia, chave -> new FamiliaObjeto(chave, nomeFamilia));

                Set<CaracteristicaObjeto> caracteristicas = new LinkedHashSet<>();
                if (obj.caracteristicas() != null) {
                    for (Map.Entry<String, String> entrada : obj.caracteristicas().entrySet()) {
                        caracteristicas.add(CaracteristicaObjeto.de(
                                exigirTexto(entrada.getKey(), "caracteristicas.dimensao"),
                                exigirTexto(entrada.getValue(), "caracteristicas.valor")));
                    }
                }

                RealizacaoTextualDto rtDto = obj.realizacaoTextual();
                if (rtDto == null) {
                    throw new ErroDeLeituraJson(
                            "campo obrigatorio ausente: objetos_contados.realizacao_textual");
                }
                RealizacaoTextualObjeto realizacaoTextual = new RealizacaoTextualObjeto(
                        exigirTexto(rtDto.singular(), "realizacao_textual.singular"),
                        exigirTexto(rtDto.plural(), "realizacao_textual.plural"),
                        exigirTexto(rtDto.idioma(), "realizacao_textual.idioma"));

                ReferenciaVisual refVisual = (obj.referenciaVisual() == null
                        || obj.referenciaVisual().isBlank())
                        ? ReferenciaVisual.naoDefinida()
                        : ReferenciaVisual.de(obj.referenciaVisual());

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
        return new ContextoNarrativo(dto.local(), dto.situacao(), dto.momento());
    }

    private IncognitaQuantitativa lerIncognita(IncognitaDto dto, CatalogoDaSituacao catalogo) {
        if (dto == null) {
            return null;
        }
        PapelQuantitativo papel = lerEnumerado(
                PapelQuantitativo.class,
                exigirTexto(dto.papel(), "incognita.papel"),
                "papel na incognita");
        IncognitaQuantitativa incognita = IncognitaQuantitativa.designar(
                papel, exigirTexto(dto.pergunta(), "incognita.pergunta"));

        if (Boolean.TRUE.equals(dto.resolvida())) {
            String idObjeto = exigirTexto(dto.objetoResolvido(), "incognita.objeto_resolvido");
            ObjetoContado objeto = exigirObjeto(catalogo, idObjeto, "incognita.objeto_resolvido");
            Integer valor = exigirInteiro(dto.valorResolvido(), "incognita.valor_resolvido");
            incognita = incognita.resolver(objeto.contar(valor));
        }
        return incognita;
    }

    private EstadoDePosse lerEstado(EstadoDto dto, CatalogoDaSituacao catalogo, String contexto) {
        String idPossuidor = exigirTexto(dto.possuidor(), contexto + ".possuidor");
        ParticipanteNarrativo possuidor = exigirParticipante(
                catalogo, idPossuidor, contexto + ".possuidor");
        int ordem = exigirInteiro(dto.ordemTemporal(), contexto + ".ordem_temporal");
        String rotulo = exigirTexto(dto.rotuloTemporal(), contexto + ".rotulo_temporal");
        if (dto.itens() == null) {
            throw new ErroDeLeituraJson("campo obrigatorio ausente: " + contexto + ".itens");
        }

        MarcadorTemporal momento;
        try {
            momento = new MarcadorTemporal(ordem, rotulo);
        } catch (IllegalArgumentException erro) {
            throw new ErroDeLeituraJson(contexto + ": " + erro.getMessage(), erro);
        }

        Inventario inventario = Inventario.vazio();
        for (QuantidadeDto item : dto.itens()) {
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
        String idObjeto = exigirTexto(dto.objeto(), contexto + ".objeto");
        ObjetoContado objeto = exigirObjeto(catalogo, idObjeto, contexto);
        int numero = exigirInteiro(dto.numero(), contexto + ".numero");
        GrandezaQuantitativa grandeza = lerEnumerado(
                GrandezaQuantitativa.class,
                exigirTexto(dto.grandeza(), contexto + ".grandeza"),
                "grandeza");
        UnidadeMedida unidade = lerEnumerado(
                UnidadeMedida.class,
                exigirTexto(dto.unidade(), contexto + ".unidade"),
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
            int ordem = exigirInteiro(item.ordem(), "transformacoes.ordem");
            String elemento = "transformacao " + ordem;
            String marcador = exigirTexto(item.marcador(), elemento + ".marcador");

            ParticipanteNarrativo ator = exigirParticipante(
                    catalogo, exigirTexto(item.ator(), elemento + ".ator"), elemento + ".ator");
            ParticipanteNarrativo destinatario = (item.destinatario() == null
                    || item.destinatario().isBlank())
                    ? null
                    : exigirParticipante(catalogo, item.destinatario(), elemento + ".destinatario");

            AcaoDto acaoDto = item.acao();
            if (acaoDto == null) {
                throw new ErroDeLeituraJson("campo obrigatorio ausente: " + elemento + ".acao");
            }
            SentidoTransformacao sentido = lerEnumerado(
                    SentidoTransformacao.class,
                    exigirTexto(acaoDto.sentido(), elemento + ".acao.sentido"),
                    "sentido");
            if (acaoDto.exigeDestinatario() == null) {
                throw new ErroDeLeituraJson(
                        "campo obrigatorio ausente: " + elemento + ".acao.exige_destinatario");
            }
            boolean exigeDestinatario = acaoDto.exigeDestinatario();
            String idAcao = exigirTexto(acaoDto.id(), elemento + ".acao.id");
            String verbo = exigirTexto(acaoDto.verbo(), elemento + ".acao.verbo");
            String participio = exigirTexto(acaoDto.participio(), elemento + ".acao.participio");
            String preposicao = acaoDto.preposicaoDestinatario();

            try {
                AcaoNarrativa acao = (preposicao == null || preposicao.isBlank())
                        ? new AcaoNarrativa(idAcao, verbo, participio, sentido, exigeDestinatario)
                        : new AcaoNarrativa(idAcao, verbo, participio, sentido, exigeDestinatario,
                                preposicao);

                Quantidade quantidade = lerQuantidade(
                        item.quantidade(), catalogo, elemento + ".quantidade");
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
        String categoria = exigirTexto(dto.categoria(), "relacao.categoria");
        String idRelacao = (dto.id() == null || dto.id().isBlank()) ? idSituacao : dto.id();
        Map<String, QuantidadeDto> papeis = dto.papeis();

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
                            lerSentidoOuPadrao(dto.sentidoPrimeira(), "relacao.sentido_primeira"));
                    case TRANSFORMACAO_SEGUNDA -> construtor.comSegundaTransformacao(
                            quantidade,
                            lerSentidoOuPadrao(dto.sentidoSegunda(), "relacao.sentido_segunda"));
                    case TRANSFORMACAO_RESULTANTE -> construtor.comTransformacaoResultante(
                            quantidade,
                            lerSentidoOuPadrao(dto.sentidoResultante(), "relacao.sentido_resultante"));
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

        ParticipanteNarrativo referente = (dto.participanteReferente() == null
                || dto.participanteReferente().isBlank())
                ? null
                : exigirParticipante(catalogo, dto.participanteReferente(),
                        "relacao.participante_referente");
        ParticipanteNarrativo referido = (dto.participanteReferido() == null
                || dto.participanteReferido().isBlank())
                ? null
                : exigirParticipante(catalogo, dto.participanteReferido(),
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

        ComparacaoQuantitativa.Direcao direcao = (dto.direcao() == null || dto.direcao().isBlank())
                ? ComparacaoQuantitativa.Direcao.A_MAIS
                : lerEnumerado(ComparacaoQuantitativa.Direcao.class,
                        dto.direcao(), "relacao.direcao");

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
        if (dto.partes() != null) {
            for (QuantidadeDto parteDto : dto.partes()) {
                construtor.comParte(lerQuantidade(parteDto, catalogo, "relacao.partes"));
            }
        } else if (papeis != null && papeis.containsKey(PapelQuantitativo.PARTE.name())) {
            construtor.comParte(lerQuantidade(
                    papeis.get(PapelQuantitativo.PARTE.name()), catalogo, "relacao.papeis.PARTE"));
        }

        QuantidadeDto todoDto = dto.todo();
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
            return situacao.promoverParaValidada(revisor);
        }
        if (StatusCuradoria.RECUSADA.name().equals(status)) {
            return situacao.recusar(revisor,
                    motivoGravado == null || motivoGravado.isBlank()
                            ? "motivo registrado no arquivo" : motivoGravado);
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
