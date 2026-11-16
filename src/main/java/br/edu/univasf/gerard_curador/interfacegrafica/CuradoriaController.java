package br.edu.univasf.gerard_curador.interfacegrafica;

import br.edu.univasf.gerard_curador.app.SituacoesCuradas;
import br.edu.univasf.gerard_curador.dominio.estrutural.PapelQuantitativo;
import br.edu.univasf.gerard_curador.dominio.narrativo.AcaoNarrativa;
import br.edu.univasf.gerard_curador.dominio.narrativo.ParticipanteNarrativo;
import br.edu.univasf.gerard_curador.dominio.narrativo.ReferenciaVisualParticipante;
import br.edu.univasf.gerard_curador.dominio.quantitativo.CaracteristicaObjeto;
import br.edu.univasf.gerard_curador.dominio.quantitativo.ObjetoContado;
import br.edu.univasf.gerard_curador.dominio.quantitativo.ReferenciaVisual;
import br.edu.univasf.gerard_curador.dominio.repositorio.Repositorio;
import br.edu.univasf.gerard_curador.dominio.situacao.SituacaoProblema;
import br.edu.univasf.gerard_curador.dominio.situacao.StatusCuradoria;
import br.edu.univasf.gerard_curador.dominio.validacao.Diagnostico;
import br.edu.univasf.gerard_curador.dominio.validacao.ResultadoValidacao;
import br.edu.univasf.gerard_curador.persistencia.RepositorioSituacaoJson;
import br.edu.univasf.gerard_curador.representacao.quadrinhos.GeradorRoteiroQuadrinhos;
import br.edu.univasf.gerard_curador.representacao.quadrinhos.RenderizadorDeRoteiro;
import br.edu.univasf.gerard_curador.representacao.quadrinhos.RenderizadorHtml;
import br.edu.univasf.gerard_curador.representacao.quadrinhos.RenderizadorTextual;
import br.edu.univasf.gerard_curador.representacao.quadrinhos.RoteiroQuadrinhos;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.web.WebView;
import javafx.util.StringConverter;

/**
 * Controller JavaFX da tela de curadoria de situacoes-problema aditivas.
 *
 * <p>A tela apenas coleta dados no {@link RascunhoDeSituacao} e apresenta os
 * resultados produzidos pelo dominio, sem decidir regras nem recalcular valores.
 */
public final class CuradoriaController {

    private final Repositorio<SituacaoProblema, String> repositorio =
            new RepositorioSituacaoJson(Path.of("dados"));

    private final RascunhoDeSituacao rascunho = new RascunhoDeSituacao();
    private final GeradorRoteiroQuadrinhos geradorRoteiro = new GeradorRoteiroQuadrinhos();
    private final RenderizadorDeRoteiro renderizadorHtml = new RenderizadorHtml();
    private final RenderizadorDeRoteiro renderizadorTextual = new RenderizadorTextual();

    private SituacaoProblema situacaoAtual;
    private boolean atualizandoInterface;
    private boolean rascunhoModificado;

    @FXML
    private ListView<SituacaoProblema> listaSituacoes;

    @FXML
    private TabPane abasPrincipais;
    @FXML
    private Tab abaCatalogo;
    @FXML
    private Tab abaEstadoInicial;
    @FXML
    private Tab abaTransformacoes;
    @FXML
    private Tab abaIncognitaEstadoFinal;
    @FXML
    private Tab abaRevisao;
    @FXML
    private Tab abaRoteiro;

    // Aba 1: Identificacao e contexto
    @FXML
    private TextField campoId;
    @FXML
    private TextArea campoEnunciado;
    @FXML
    private TextField campoLocal;
    @FXML
    private TextField campoSituacaoContexto;
    @FXML
    private TextField campoMomento;

    // Aba 1: Participantes
    @FXML
    private TableView<ParticipanteNarrativo> tabelaParticipantes;
    @FXML
    private TableColumn<ParticipanteNarrativo, String> colParticipanteId;
    @FXML
    private TableColumn<ParticipanteNarrativo, String> colParticipanteNome;
    @FXML
    private TableColumn<ParticipanteNarrativo, String> colParticipanteChave;
    @FXML
    private TextField campoParticipanteId;
    @FXML
    private TextField campoParticipanteNome;
    @FXML
    private TextField campoParticipanteChave;

    // Aba 1: Objetos contados
    @FXML
    private TableView<ObjetoContado> tabelaObjetos;
    @FXML
    private TableColumn<ObjetoContado, String> colObjetoId;
    @FXML
    private TableColumn<ObjetoContado, String> colObjetoFamilia;
    @FXML
    private TableColumn<ObjetoContado, String> colObjetoCaracteristicas;
    @FXML
    private TableColumn<ObjetoContado, String> colObjetoRealizacao;
    @FXML
    private TableColumn<ObjetoContado, String> colObjetoChave;
    @FXML
    private TextField campoObjetoId;
    @FXML
    private TextField campoObjetoFamilia;
    @FXML
    private TextField campoObjetoDimensao;
    @FXML
    private TextField campoObjetoValor;
    @FXML
    private TextField campoObjetoSingular;
    @FXML
    private TextField campoObjetoPlural;
    @FXML
    private TextField campoObjetoChave;

    // Aba 2: Estado inicial
    @FXML
    private ComboBox<ParticipanteNarrativo> comboPossuidorInicial;
    @FXML
    private ComboBox<ObjetoContado> comboObjetoInicial;
    @FXML
    private Spinner<Integer> spinnerQuantidadeInicial;
    @FXML
    private TableView<ItemInventarioLinha> tabelaItensIniciais;
    @FXML
    private TableColumn<ItemInventarioLinha, String> colInicialObjeto;
    @FXML
    private TableColumn<ItemInventarioLinha, Integer> colInicialQuantidade;

    // Aba 3: Transformacoes
    @FXML
    private Spinner<Integer> spinnerOrdemTransformacao;
    @FXML
    private TextField campoRotuloTemporal;
    @FXML
    private ComboBox<ParticipanteNarrativo> comboAtor;
    @FXML
    private ComboBox<ParticipanteNarrativo> comboDestinatario;
    @FXML
    private ComboBox<AcaoNarrativa> comboAcao;
    @FXML
    private ComboBox<ObjetoContado> comboObjetoTransformacao;
    @FXML
    private Spinner<Integer> spinnerQuantidadeTransformacao;
    @FXML
    private TableView<TransformacaoLinha> tabelaTransformacoes;
    @FXML
    private TableColumn<TransformacaoLinha, Integer> colTransfOrdem;
    @FXML
    private TableColumn<TransformacaoLinha, String> colTransfRotulo;
    @FXML
    private TableColumn<TransformacaoLinha, String> colTransfAtor;
    @FXML
    private TableColumn<TransformacaoLinha, String> colTransfAcao;
    @FXML
    private TableColumn<TransformacaoLinha, String> colTransfDestinatario;
    @FXML
    private TableColumn<TransformacaoLinha, String> colTransfObjeto;
    @FXML
    private TableColumn<TransformacaoLinha, Integer> colTransfQuantidade;

    // Aba 4: Incognita e estado final
    @FXML
    private ComboBox<PapelQuantitativo> comboPapelIncognita;
    @FXML
    private TextField campoPerguntaIncognita;
    @FXML
    private ComboBox<ParticipanteNarrativo> comboPossuidorFinal;
    @FXML
    private ComboBox<ObjetoContado> comboObjetoFinal;
    @FXML
    private Spinner<Integer> spinnerQuantidadeFinal;
    @FXML
    private TableView<ItemInventarioLinha> tabelaItensFinais;
    @FXML
    private TableColumn<ItemInventarioLinha, String> colFinalObjeto;
    @FXML
    private TableColumn<ItemInventarioLinha, Integer> colFinalQuantidade;

    // Aba 5: Revisao
    @FXML
    private Label rotuloResumoValidacao;
    @FXML
    private Label rotuloRevisaoObservacoes;
    @FXML
    private ListView<Diagnostico> listaDiagnosticos;

    // Aba 6: Roteiro
    @FXML
    private ToggleButton toggleFormatoRoteiro;
    @FXML
    private WebView webViewRoteiro;
    @FXML
    private TextArea textAreaRoteiro;

    // Rodape
    @FXML
    private Label rotuloStatus;
    @FXML
    private Label rotuloStatusCuradoria;

    @FXML
    public void initialize() {
        configurarListaLateral();
        configurarTabelas();
        configurarCombosESpinners();
        configurarOuvintesDeEdicao();
        configurarListaDiagnosticos();

        limparResultadosValidacao();
        atualizarBarraStatus("Pronto. Selecione uma situação ou crie uma nova.", null);

        try {
            recarregarListaESelecionar(null);
        } catch (RuntimeException erro) {
            registrarErro("Falha ao listar situações do repositório", erro);
        }
    }

    // ------------------------------------------------------------------
    // Configuracao inicial dos componentes
    // ------------------------------------------------------------------

    private void configurarListaLateral() {
        listaSituacoes.setCellFactory(lista -> new CelulaSituacao());
        listaSituacoes.setPlaceholder(new Label("Nenhuma situação gravada em dados/."));
        listaSituacoes.getSelectionModel().selectedItemProperty().addListener(
                (obs, antiga, selecionada) -> {
                    if (atualizandoInterface || selecionada == null) {
                        return;
                    }
                    carregarSituacaoSelecionada(selecionada);
                });
    }

    private void configurarTabelas() {
        tabelaParticipantes.setPlaceholder(new Label("Nenhum participante cadastrado."));
        colParticipanteId.setCellValueFactory(
                celula -> new SimpleStringProperty(celula.getValue().getId()));
        colParticipanteNome.setCellValueFactory(
                celula -> new SimpleStringProperty(celula.getValue().getNomeExibido()));
        colParticipanteChave.setCellValueFactory(celula -> {
            ReferenciaVisualParticipante ref = celula.getValue().getReferenciaVisual();
            return new SimpleStringProperty(
                    ref != null && ref.estaDefinida() ? ref.getChave() : "—");
        });

        tabelaObjetos.setPlaceholder(new Label("Nenhum objeto contado cadastrado."));
        colObjetoId.setCellValueFactory(
                celula -> new SimpleStringProperty(celula.getValue().getId()));
        colObjetoFamilia.setCellValueFactory(
                celula -> new SimpleStringProperty(celula.getValue().getFamilia().getNomeConceitual()));
        colObjetoCaracteristicas.setCellValueFactory(
                celula -> new SimpleStringProperty(formatarCaracteristicas(celula.getValue())));
        colObjetoRealizacao.setCellValueFactory(celula -> new SimpleStringProperty(
                celula.getValue().getRealizacaoTextual().getSingular()
                        + " / "
                        + celula.getValue().getRealizacaoTextual().getPlural()));
        colObjetoChave.setCellValueFactory(celula -> {
            ReferenciaVisual ref = celula.getValue().getReferenciaVisual();
            return new SimpleStringProperty(
                    ref != null && ref.estaDefinida() ? ref.getChave() : "—");
        });

        tabelaItensIniciais.setPlaceholder(new Label("Nenhum item no estado inicial."));
        colInicialObjeto.setCellValueFactory(
                celula -> new SimpleStringProperty(celula.getValue().nomeObjeto()));
        colInicialQuantidade.setCellValueFactory(
                celula -> new SimpleIntegerProperty(celula.getValue().quantidade()).asObject());

        tabelaItensFinais.setPlaceholder(new Label("Nenhum item no estado final curado."));
        colFinalObjeto.setCellValueFactory(
                celula -> new SimpleStringProperty(celula.getValue().nomeObjeto()));
        colFinalQuantidade.setCellValueFactory(
                celula -> new SimpleIntegerProperty(celula.getValue().quantidade()).asObject());

        tabelaTransformacoes.setPlaceholder(new Label("Nenhuma transformação cadastrada."));
        colTransfOrdem.setCellValueFactory(
                celula -> new SimpleIntegerProperty(celula.getValue().dados().ordem).asObject());
        colTransfRotulo.setCellValueFactory(
                celula -> new SimpleStringProperty(celula.getValue().dados().rotuloTemporal));
        colTransfAtor.setCellValueFactory(
                celula -> new SimpleStringProperty(nomeParticipanteOuId(celula.getValue().dados().idAtor)));
        colTransfAcao.setCellValueFactory(celula -> new SimpleStringProperty(
                celula.getValue().dados().verbo
                        + " (" + celula.getValue().dados().sentido.getNomeExibido() + ")"));
        colTransfDestinatario.setCellValueFactory(celula -> {
            String id = celula.getValue().dados().idDestinatario;
            return new SimpleStringProperty(id == null ? "—" : nomeParticipanteOuId(id));
        });
        colTransfObjeto.setCellValueFactory(
                celula -> new SimpleStringProperty(nomeObjetoOuId(celula.getValue().dados().idObjeto)));
        colTransfQuantidade.setCellValueFactory(
                celula -> new SimpleIntegerProperty(celula.getValue().dados().quantidade).asObject());
    }

    private void configurarCombosESpinners() {
        StringConverter<ParticipanteNarrativo> conversorParticipante = new StringConverter<>() {
            @Override
            public String toString(ParticipanteNarrativo p) {
                return p == null ? "" : p.getNomeExibido() + " (" + p.getId() + ")";
            }

            @Override
            public ParticipanteNarrativo fromString(String s) {
                return null;
            }
        };
        comboPossuidorInicial.setConverter(conversorParticipante);
        comboPossuidorFinal.setConverter(conversorParticipante);
        comboAtor.setConverter(conversorParticipante);
        comboDestinatario.setConverter(conversorParticipante);

        StringConverter<ObjetoContado> conversorObjeto = new StringConverter<>() {
            @Override
            public String toString(ObjetoContado obj) {
                return obj == null ? "" : obj.getRealizacaoTextual().getSingular() + " (" + obj.getId() + ")";
            }

            @Override
            public ObjetoContado fromString(String s) {
                return null;
            }
        };
        comboObjetoInicial.setConverter(conversorObjeto);
        comboObjetoFinal.setConverter(conversorObjeto);
        comboObjetoTransformacao.setConverter(conversorObjeto);

        comboAcao.setConverter(new StringConverter<>() {
            @Override
            public String toString(AcaoNarrativa acao) {
                return acao == null ? "" : AcoesDisponiveis.rotularParaMenu(acao);
            }

            @Override
            public AcaoNarrativa fromString(String s) {
                return null;
            }
        });
        comboAcao.getItems().setAll(AcoesDisponiveis.listar());
        comboAcao.valueProperty().addListener((obs, antiga, nova) -> {
            boolean exigeDest = nova == null || nova.exigeDestinatario();
            comboDestinatario.setDisable(!exigeDest);
            if (!exigeDest) {
                comboDestinatario.setValue(null);
            }
        });

        comboPapelIncognita.setConverter(new StringConverter<>() {
            @Override
            public String toString(PapelQuantitativo papel) {
                return papel == null ? "" : papel.getNomeExibido();
            }

            @Override
            public PapelQuantitativo fromString(String s) {
                return null;
            }
        });
        comboPapelIncognita.getItems().setAll(PapelQuantitativo.values());

        spinnerQuantidadeInicial.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 999999, 1));
        spinnerQuantidadeFinal.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 999999, 1));
        spinnerOrdemTransformacao.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9999, 2));
        spinnerQuantidadeTransformacao.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999999, 1));
    }

    private void configurarOuvintesDeEdicao() {
        Runnable marcarModificado = () -> {
            if (!atualizandoInterface) {
                rascunhoModificado = true;
            }
        };

        campoId.textProperty().addListener((obs, a, n) -> marcarModificado.run());
        campoEnunciado.textProperty().addListener((obs, a, n) -> marcarModificado.run());
        campoLocal.textProperty().addListener((obs, a, n) -> marcarModificado.run());
        campoSituacaoContexto.textProperty().addListener((obs, a, n) -> marcarModificado.run());
        campoMomento.textProperty().addListener((obs, a, n) -> marcarModificado.run());
        campoPerguntaIncognita.textProperty().addListener((obs, a, n) -> {
            if (!atualizandoInterface) {
                rascunho.setIncognita(comboPapelIncognita.getValue(), n);
                rascunhoModificado = true;
            }
        });

        comboPapelIncognita.valueProperty().addListener((obs, a, papel) -> {
            if (!atualizandoInterface) {
                rascunho.setIncognita(papel, campoPerguntaIncognita.getText());
                rascunhoModificado = true;
            }
        });

        comboPossuidorInicial.valueProperty().addListener((obs, a, p) -> {
            if (!atualizandoInterface) {
                rascunho.setPossuidorInicial(p != null ? p.getId() : null);
                rascunhoModificado = true;
            }
        });

        comboPossuidorFinal.valueProperty().addListener((obs, a, p) -> {
            if (!atualizandoInterface) {
                rascunho.setPossuidorFinal(p != null ? p.getId() : null);
                rascunhoModificado = true;
            }
        });
    }

    private void configurarListaDiagnosticos() {
        listaDiagnosticos.setCellFactory(lista -> new CelulaDiagnostico());
        listaDiagnosticos.setPlaceholder(new Label("Nenhum diagnóstico registrado."));
    }

    // ------------------------------------------------------------------
    // Acoes da barra superior
    // ------------------------------------------------------------------

    @FXML
    private void aoClicarNovaSituacao() {
        atualizandoInterface = true;
        try {
            rascunho.limpar();
            situacaoAtual = null;
            rascunhoModificado = false;
            listaSituacoes.getSelectionModel().clearSelection();
            preencherInterfaceComRascunho();
            limparResultadosValidacao();
            abasPrincipais.getSelectionModel().select(abaCatalogo);
        } finally {
            atualizandoInterface = false;
        }
        atualizarBarraStatus("Nova situação iniciada.", null);
    }

    @FXML
    private void aoClicarCarregarExemplos() {
        try {
            repositorio.salvar(SituacoesCuradas.rosas());
            repositorio.salvar(SituacoesCuradas.figurinhasComEstadoInicialDesconhecido());
            repositorio.salvar(SituacoesCuradas.bilasComDuasPartidas());

            String idSelecionado = situacaoAtual != null ? situacaoAtual.getId() : null;
            recarregarListaESelecionar(idSelecionado);
            atualizarBarraStatus(
                    "Exemplos salvos no repositório e lista recarregada.",
                    situacaoAtual);
        } catch (RuntimeException erro) {
            registrarErro("Falha ao carregar situações de exemplo", erro);
        }
    }

    @FXML
    private void aoClicarSalvar() {
        recolherCamposTextoParaRascunho();
        try {
            SituacaoProblema montada = rascunho.montarSituacao();
            repositorio.salvar(montada);
            situacaoAtual = montada;
            rascunhoModificado = false;
            recarregarListaESelecionar(montada.getId());
            apresentarValidacaoERoteiro(montada);
            atualizarBarraStatus("Situação '" + montada.getId() + "' salva no repositório.", montada);
        } catch (RuntimeException erro) {
            registrarErro("Falha ao salvar situação", erro);
        }
    }

    @FXML
    private void aoClicarValidar() {
        recolherCamposTextoParaRascunho();
        try {
            SituacaoProblema montada = rascunho.montarSituacao();
            situacaoAtual = montada;
            rascunhoModificado = false;
            ResultadoValidacao resultado = apresentarValidacaoERoteiro(montada);
            abasPrincipais.getSelectionModel().select(abaRevisao);
            String mensagem = resultado.ehValido()
                    ? "Estrutura sem erros. Status permanece "
                            + montada.getStatus().getNomeExibido() + " até revisão humana."
                    : "Diagnósticos encontrados. Nenhum valor curado foi alterado.";
            atualizarBarraStatus(mensagem, montada);
        } catch (IllegalArgumentException | IllegalStateException erro) {
            situacaoAtual = null;
            limparResultadosValidacao();
            registrarErro("Montagem recusada pelo domínio", erro);
        }
    }

    @FXML
    private void aoClicarPromover() {
        try {
            SituacaoProblema alvo = obterSituacaoParaRevisao();
            TextInputDialog dialogo = new TextInputDialog();
            dialogo.setTitle("Promover situação");
            dialogo.setHeaderText("Promoção ao catálogo validado");
            dialogo.setContentText("Identificação do revisor:");
            Optional<String> resposta = dialogo.showAndWait();
            if (resposta.isEmpty()) {
                return;
            }
            SituacaoProblema promovida = alvo.promoverParaValidada(resposta.get().trim());
            repositorio.salvar(promovida);
            situacaoAtual = promovida;
            rascunhoModificado = false;
            recarregarListaESelecionar(promovida.getId());
            apresentarValidacaoERoteiro(promovida);
            atualizarBarraStatus(
                    "Situação '" + promovida.getId() + "' promovida para validada.",
                    promovida);
        } catch (RuntimeException erro) {
            registrarErro("Promoção recusada pelo domínio", erro);
        }
    }

    @FXML
    private void aoClicarRecusar() {
        try {
            SituacaoProblema alvo = obterSituacaoParaRevisao();

            TextInputDialog dialogoRevisor = new TextInputDialog();
            dialogoRevisor.setTitle("Recusar situação");
            dialogoRevisor.setHeaderText("Recusa por revisor humano");
            dialogoRevisor.setContentText("Identificação do revisor:");
            Optional<String> respRevisor = dialogoRevisor.showAndWait();
            if (respRevisor.isEmpty()) {
                return;
            }

            TextInputDialog dialogoMotivo = new TextInputDialog();
            dialogoMotivo.setTitle("Recusar situação");
            dialogoMotivo.setHeaderText("Registro do motivo da recusa");
            dialogoMotivo.setContentText("Motivo da recusa:");
            Optional<String> respMotivo = dialogoMotivo.showAndWait();
            if (respMotivo.isEmpty()) {
                return;
            }

            SituacaoProblema recusada = alvo.recusar(
                    respRevisor.get().trim(), respMotivo.get().trim());
            repositorio.salvar(recusada);
            situacaoAtual = recusada;
            rascunhoModificado = false;
            recarregarListaESelecionar(recusada.getId());
            apresentarValidacaoERoteiro(recusada);
            atualizarBarraStatus(
                    "Situação '" + recusada.getId() + "' recusada.",
                    recusada);
        } catch (RuntimeException erro) {
            registrarErro("Recusa rejeitada pelo domínio", erro);
        }
    }

    // ------------------------------------------------------------------
    // Aba 1: Catalogo
    // ------------------------------------------------------------------

    @FXML
    private void aoAdicionarParticipante() {
        try {
            rascunho.adicionarParticipante(
                    campoParticipanteId.getText(),
                    campoParticipanteNome.getText(),
                    campoParticipanteChave.getText());
            rascunhoModificado = true;
            campoParticipanteId.clear();
            campoParticipanteNome.clear();
            campoParticipanteChave.clear();
            tabelaParticipantes.getItems().setAll(rascunho.getParticipantes());
            atualizarCombosDoCatalogo();
            atualizarBarraStatus("Participante adicionado ao catálogo.", situacaoAtual);
        } catch (IllegalArgumentException | IllegalStateException erro) {
            registrarErro("Participante inválido", erro);
        }
    }

    @FXML
    private void aoRemoverParticipante() {
        ParticipanteNarrativo selecionado = tabelaParticipantes.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            atualizarBarraStatus("Selecione um participante na tabela para remover.", situacaoAtual);
            return;
        }
        rascunho.removerParticipante(selecionado.getId());
        rascunhoModificado = true;
        tabelaParticipantes.getItems().setAll(rascunho.getParticipantes());
        atualizarCombosDoCatalogo();
        atualizarTabelaTransformacoes();
        atualizarTabelaItensIniciais();
        atualizarTabelaItensFinais();
        atualizarBarraStatus("Participante '" + selecionado.getId() + "' removido.", situacaoAtual);
    }

    @FXML
    private void aoAdicionarObjeto() {
        try {
            String dimensao = campoObjetoDimensao.getText();
            String valor = campoObjetoValor.getText();
            Map<String, String> caracteristicas = new LinkedHashMap<>();
            if ((dimensao != null && !dimensao.isBlank()) || (valor != null && !valor.isBlank())) {
                caracteristicas.put(dimensao == null ? "" : dimensao, valor == null ? "" : valor);
            }
            String familia = campoObjetoFamilia.getText();
            String idFamilia = familia == null ? "" : familia.trim();
            rascunho.adicionarObjeto(
                    campoObjetoId.getText(),
                    idFamilia,
                    familia,
                    caracteristicas,
                    campoObjetoSingular.getText(),
                    campoObjetoPlural.getText(),
                    campoObjetoChave.getText());
            rascunhoModificado = true;
            campoObjetoId.clear();
            campoObjetoFamilia.clear();
            campoObjetoDimensao.clear();
            campoObjetoValor.clear();
            campoObjetoSingular.clear();
            campoObjetoPlural.clear();
            campoObjetoChave.clear();
            tabelaObjetos.getItems().setAll(rascunho.getObjetos());
            atualizarCombosDoCatalogo();
            atualizarBarraStatus("Objeto contado adicionado ao catálogo.", situacaoAtual);
        } catch (IllegalArgumentException | IllegalStateException erro) {
            registrarErro("Objeto contado inválido", erro);
        }
    }

    @FXML
    private void aoRemoverObjeto() {
        ObjetoContado selecionado = tabelaObjetos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            atualizarBarraStatus("Selecione um objeto na tabela para remover.", situacaoAtual);
            return;
        }
        rascunho.removerObjeto(selecionado.getId());
        rascunhoModificado = true;
        tabelaObjetos.getItems().setAll(rascunho.getObjetos());
        atualizarCombosDoCatalogo();
        atualizarTabelaItensIniciais();
        atualizarTabelaItensFinais();
        atualizarTabelaTransformacoes();
        atualizarBarraStatus("Objeto '" + selecionado.getId() + "' removido.", situacaoAtual);
    }

    // ------------------------------------------------------------------
    // Aba 2: Estado inicial
    // ------------------------------------------------------------------

    @FXML
    private void aoAdicionarItemInicial() {
        try {
            ObjetoContado objeto = comboObjetoInicial.getValue();
            if (objeto == null) {
                throw new IllegalArgumentException(
                        "Selecione um objeto do catálogo para o estado inicial.");
            }
            int quantidade = lerSpinnerInteiro(spinnerQuantidadeInicial);
            rascunho.definirItemInicial(objeto.getId(), quantidade);
            rascunhoModificado = true;
            atualizarTabelaItensIniciais();
            atualizarBarraStatus("Item definido no estado inicial.", situacaoAtual);
        } catch (IllegalArgumentException | IllegalStateException erro) {
            registrarErro("Item inicial inválido", erro);
        }
    }

    @FXML
    private void aoRemoverItemInicial() {
        ItemInventarioLinha selecionado = tabelaItensIniciais.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            atualizarBarraStatus("Selecione um item do estado inicial para remover.", situacaoAtual);
            return;
        }
        rascunho.removerItemInicial(selecionado.idObjeto());
        rascunhoModificado = true;
        atualizarTabelaItensIniciais();
        atualizarBarraStatus("Item removido do estado inicial.", situacaoAtual);
    }

    // ------------------------------------------------------------------
    // Aba 3: Transformacoes
    // ------------------------------------------------------------------

    @FXML
    private void aoAdicionarTransformacao() {
        try {
            int ordem = lerSpinnerInteiro(spinnerOrdemTransformacao);
            String rotulo = campoRotuloTemporal.getText() == null
                    ? "" : campoRotuloTemporal.getText().trim();
            ParticipanteNarrativo ator = comboAtor.getValue();
            if (ator == null) {
                throw new IllegalArgumentException("Selecione o ator da transformação.");
            }
            AcaoNarrativa acao = comboAcao.getValue();
            if (acao == null) {
                throw new IllegalArgumentException("Selecione a ação da transformação.");
            }
            ObjetoContado objeto = comboObjetoTransformacao.getValue();
            if (objeto == null) {
                throw new IllegalArgumentException("Selecione o objeto da transformação.");
            }
            ParticipanteNarrativo destinatario = comboDestinatario.getValue();
            int quantidade = lerSpinnerInteiro(spinnerQuantidadeTransformacao);

            RascunhoDeSituacao.TransformacaoRascunhada nova =
                    new RascunhoDeSituacao.TransformacaoRascunhada(
                            ordem,
                            rotulo,
                            ator.getId(),
                            destinatario != null ? destinatario.getId() : null,
                            objeto.getId(),
                            quantidade,
                            acao.getId(),
                            acao.getVerbo(),
                            acao.getParticipio(),
                            acao.getSentido(),
                            acao.exigeDestinatario());
            rascunho.adicionarTransformacao(nova);
            rascunhoModificado = true;
            campoRotuloTemporal.clear();
            spinnerOrdemTransformacao.getValueFactory().setValue(ordem + 1);
            atualizarTabelaTransformacoes();
            atualizarBarraStatus("Transformação adicionada (ordem " + ordem + ").", situacaoAtual);
        } catch (IllegalArgumentException | IllegalStateException erro) {
            registrarErro("Transformação inválida", erro);
        }
    }

    @FXML
    private void aoRemoverTransformacao() {
        TransformacaoLinha selecionada = tabelaTransformacoes.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            atualizarBarraStatus("Selecione uma transformação na tabela para remover.", situacaoAtual);
            return;
        }
        rascunho.removerTransformacao(selecionada.indiceNoRascunho());
        rascunhoModificado = true;
        atualizarTabelaTransformacoes();
        atualizarBarraStatus("Transformação removida.", situacaoAtual);
    }

    // ------------------------------------------------------------------
    // Aba 4: Incognita e estado final curado
    // ------------------------------------------------------------------

    @FXML
    private void aoLimparEstadoFinal() {
        atualizandoInterface = true;
        try {
            rascunho.setPossuidorFinal(null);
            for (String idObjeto : rascunho.getItensFinais().keySet()) {
                rascunho.removerItemFinal(idObjeto);
            }
            comboPossuidorFinal.setValue(null);
            rascunhoModificado = true;
            atualizarTabelaItensFinais();
        } finally {
            atualizandoInterface = false;
        }
        atualizarBarraStatus("Estado final curado limpo (opcional).", situacaoAtual);
    }

    @FXML
    private void aoAdicionarItemFinal() {
        try {
            ObjetoContado objeto = comboObjetoFinal.getValue();
            if (objeto == null) {
                throw new IllegalArgumentException(
                        "Selecione um objeto do catálogo para o estado final.");
            }
            int quantidade = lerSpinnerInteiro(spinnerQuantidadeFinal);
            rascunho.definirItemFinal(objeto.getId(), quantidade);
            rascunhoModificado = true;
            atualizarTabelaItensFinais();
            atualizarBarraStatus("Item definido no estado final curado.", situacaoAtual);
        } catch (IllegalArgumentException | IllegalStateException erro) {
            registrarErro("Item final inválido", erro);
        }
    }

    @FXML
    private void aoRemoverItemFinal() {
        ItemInventarioLinha selecionado = tabelaItensFinais.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            atualizarBarraStatus("Selecione um item do estado final para remover.", situacaoAtual);
            return;
        }
        rascunho.removerItemFinal(selecionado.idObjeto());
        rascunhoModificado = true;
        atualizarTabelaItensFinais();
        atualizarBarraStatus("Item removido do estado final curado.", situacaoAtual);
    }

    // ------------------------------------------------------------------
    // Aba 6: Roteiro
    // ------------------------------------------------------------------

    @FXML
    private void aoAlternarFormatoRoteiro() {
        boolean modoTexto = toggleFormatoRoteiro.isSelected();
        webViewRoteiro.setVisible(!modoTexto);
        webViewRoteiro.setManaged(!modoTexto);
        textAreaRoteiro.setVisible(modoTexto);
        textAreaRoteiro.setManaged(modoTexto);
        toggleFormatoRoteiro.setText(modoTexto ? "Alternar para HTML" : "Alternar para texto");
    }

    // ------------------------------------------------------------------
    // Sincronizacao entre dominio, rascunho e tela
    // ------------------------------------------------------------------

    private void carregarSituacaoSelecionada(SituacaoProblema selecionada) {
        try {
            rascunho.substituirPor(RascunhoDeSituacao.aPartirDe(selecionada));
            situacaoAtual = selecionada;
            rascunhoModificado = false;
            preencherInterfaceComRascunho();
            apresentarValidacaoERoteiro(selecionada);
            atualizarBarraStatus(
                    "Situação '" + selecionada.getId() + "' carregada e validada.",
                    selecionada);
        } catch (RuntimeException erro) {
            registrarErro("Falha ao carregar situação", erro);
        }
    }

    private void recarregarListaESelecionar(String idParaSelecionar) {
        atualizandoInterface = true;
        try {
            List<SituacaoProblema> todas = repositorio.listarTodos();
            listaSituacoes.getItems().setAll(todas);
            if (idParaSelecionar != null) {
                for (SituacaoProblema item : todas) {
                    if (idParaSelecionar.equals(item.getId())) {
                        listaSituacoes.getSelectionModel().select(item);
                        break;
                    }
                }
            }
        } finally {
            atualizandoInterface = false;
        }
    }

    private void preencherInterfaceComRascunho() {
        atualizandoInterface = true;
        try {
            campoId.setText(rascunho.getId());
            campoEnunciado.setText(rascunho.getEnunciado());
            campoLocal.setText(rascunho.getLocal());
            campoSituacaoContexto.setText(rascunho.getSituacaoContexto());
            campoMomento.setText(rascunho.getMomento());

            campoParticipanteId.clear();
            campoParticipanteNome.clear();
            campoParticipanteChave.clear();
            campoObjetoId.clear();
            campoObjetoFamilia.clear();
            campoObjetoDimensao.clear();
            campoObjetoValor.clear();
            campoObjetoSingular.clear();
            campoObjetoPlural.clear();
            campoObjetoChave.clear();
            campoRotuloTemporal.clear();

            tabelaParticipantes.getItems().setAll(rascunho.getParticipantes());
            tabelaObjetos.getItems().setAll(rascunho.getObjetos());

            atualizarCombosDoCatalogo();

            selecionarParticipantePorId(comboPossuidorInicial, rascunho.getIdPossuidorInicial());
            selecionarParticipantePorId(comboPossuidorFinal, rascunho.getIdPossuidorFinal());

            atualizarTabelaItensIniciais();
            atualizarTabelaItensFinais();
            atualizarTabelaTransformacoes();

            comboPapelIncognita.setValue(rascunho.getPapelDaIncognita());
            campoPerguntaIncognita.setText(rascunho.getPerguntaDaIncognita());

            int proximaOrdem = rascunho.getTransformacoes().stream()
                    .mapToInt(t -> t.ordem)
                    .max()
                    .orElse(1) + 1;
            spinnerOrdemTransformacao.getValueFactory().setValue(proximaOrdem);
        } finally {
            atualizandoInterface = false;
        }
    }

    private void recolherCamposTextoParaRascunho() {
        rascunho.setId(campoId.getText());
        rascunho.setEnunciado(campoEnunciado.getText());
        rascunho.setContexto(
                campoLocal.getText(),
                campoSituacaoContexto.getText(),
                campoMomento.getText());
        rascunho.setIncognita(comboPapelIncognita.getValue(), campoPerguntaIncognita.getText());
    }

    private void atualizarCombosDoCatalogo() {
        boolean estadoAnterior = atualizandoInterface;
        atualizandoInterface = true;
        try {
            List<ParticipanteNarrativo> participantes = rascunho.getParticipantes();
            List<ObjetoContado> objetos = rascunho.getObjetos();

            String idPosInicial = idParticipanteSelecionado(comboPossuidorInicial);
            String idPosFinal = idParticipanteSelecionado(comboPossuidorFinal);
            String idAtor = idParticipanteSelecionado(comboAtor);
            String idDestinatario = idParticipanteSelecionado(comboDestinatario);

            String idObjInicial = idObjetoSelecionado(comboObjetoInicial);
            String idObjFinal = idObjetoSelecionado(comboObjetoFinal);
            String idObjTransf = idObjetoSelecionado(comboObjetoTransformacao);

            comboPossuidorInicial.getItems().setAll(participantes);
            comboPossuidorFinal.getItems().setAll(participantes);
            comboAtor.getItems().setAll(participantes);
            comboDestinatario.getItems().setAll(participantes);

            comboObjetoInicial.getItems().setAll(objetos);
            comboObjetoFinal.getItems().setAll(objetos);
            comboObjetoTransformacao.getItems().setAll(objetos);

            selecionarParticipantePorId(comboPossuidorInicial,
                    idPosInicial != null ? idPosInicial : rascunho.getIdPossuidorInicial());
            selecionarParticipantePorId(comboPossuidorFinal,
                    idPosFinal != null ? idPosFinal : rascunho.getIdPossuidorFinal());
            selecionarParticipantePorId(comboAtor, idAtor);
            selecionarParticipantePorId(comboDestinatario, idDestinatario);

            selecionarObjetoPorId(comboObjetoInicial, idObjInicial);
            selecionarObjetoPorId(comboObjetoFinal, idObjFinal);
            selecionarObjetoPorId(comboObjetoTransformacao, idObjTransf);
        } finally {
            atualizandoInterface = estadoAnterior;
        }
    }

    private void atualizarTabelaItensIniciais() {
        tabelaItensIniciais.getItems().setAll(montarLinhasInventario(rascunho.getItensIniciais()));
    }

    private void atualizarTabelaItensFinais() {
        tabelaItensFinais.getItems().setAll(montarLinhasInventario(rascunho.getItensFinais()));
    }

    private List<ItemInventarioLinha> montarLinhasInventario(Map<String, Integer> itens) {
        List<ItemInventarioLinha> linhas = new ArrayList<>();
        for (Map.Entry<String, Integer> entrada : itens.entrySet()) {
            linhas.add(new ItemInventarioLinha(
                    entrada.getKey(),
                    nomeObjetoOuId(entrada.getKey()),
                    entrada.getValue()));
        }
        return linhas;
    }

    private void atualizarTabelaTransformacoes() {
        List<RascunhoDeSituacao.TransformacaoRascunhada> lista = rascunho.getTransformacoes();
        List<TransformacaoLinha> linhas = new ArrayList<>(lista.size());
        for (int i = 0; i < lista.size(); i++) {
            linhas.add(new TransformacaoLinha(i, lista.get(i)));
        }
        linhas.sort(Comparator.comparingInt(linha -> linha.dados().ordem));
        tabelaTransformacoes.getItems().setAll(linhas);
    }

    private ResultadoValidacao apresentarValidacaoERoteiro(SituacaoProblema situacao) {
        ResultadoValidacao resultado = situacao.validar();
        listaDiagnosticos.getItems().setAll(resultado.getDiagnosticos());

        int erros = resultado.getErros().size();
        int avisos = resultado.getAvisos().size();
        String textoErros = erros + (erros == 1 ? " erro" : " erros");
        String textoAvisos = avisos + (avisos == 1 ? " aviso" : " avisos");
        String sufixo = resultado.ehValido() ? " · Estrutura válida" : " · Pendências detectadas";
        rotuloResumoValidacao.setText(textoErros + " · " + textoAvisos + sufixo);

        rotuloResumoValidacao.getStyleClass().removeAll(
                "resumo-valido", "resumo-invalido", "resumo-neutro");
        rotuloResumoValidacao.getStyleClass().add(
                resultado.ehValido() ? "resumo-valido" : "resumo-invalido");

        String obs = situacao.getObservacoesDaRevisao();
        rotuloRevisaoObservacoes.setText(obs != null ? "Revisão humana: " + obs : "");
        rotuloRevisaoObservacoes.setVisible(obs != null);
        rotuloRevisaoObservacoes.setManaged(obs != null);

        try {
            RoteiroQuadrinhos roteiro = geradorRoteiro.gerar(
                    situacao.projetarSequenciaNarrativa(),
                    situacao.getId());
            webViewRoteiro.getEngine().loadContent(renderizadorHtml.renderizar(roteiro));
            textAreaRoteiro.setText(renderizadorTextual.renderizar(roteiro));
        } catch (IllegalStateException erro) {
            String mensagem = "Roteiro indisponível: " + erro.getMessage();
            webViewRoteiro.getEngine().loadContent(
                    "<html><body style='font-family:system-ui,sans-serif;padding:1.5rem;color:#475569;'>"
                            + "<p>" + escaparHtml(mensagem) + "</p></body></html>");
            textAreaRoteiro.setText(mensagem);
        }
        return resultado;
    }

    private void limparResultadosValidacao() {
        listaDiagnosticos.getItems().clear();
        rotuloResumoValidacao.setText("0 erros · 0 avisos · Aguardando validação");
        rotuloResumoValidacao.getStyleClass().removeAll(
                "resumo-valido", "resumo-invalido", "resumo-neutro");
        rotuloResumoValidacao.getStyleClass().add("resumo-neutro");
        rotuloRevisaoObservacoes.setText("");
        rotuloRevisaoObservacoes.setVisible(false);
        rotuloRevisaoObservacoes.setManaged(false);
        webViewRoteiro.getEngine().loadContent(
                "<html><body style='font-family:system-ui,sans-serif;padding:1.5rem;color:#64748b;'>"
                        + "<p>Valide uma situação para visualizar o roteiro em HTML.</p></body></html>");
        textAreaRoteiro.clear();
    }

    private SituacaoProblema obterSituacaoParaRevisao() {
        recolherCamposTextoParaRascunho();
        if (situacaoAtual != null && !rascunhoModificado) {
            return situacaoAtual;
        }
        return rascunho.montarSituacao();
    }

    private void atualizarBarraStatus(String mensagem, SituacaoProblema situacao) {
        rotuloStatus.setText(mensagem);
        StatusCuradoria status = situacao != null
                ? situacao.getStatus()
                : StatusCuradoria.CANDIDATA_NAO_CURADA;
        String textoBadge = status.getNomeExibido();
        if (situacao != null && situacao.getRevisor() != null && !situacao.getRevisor().isBlank()) {
            textoBadge = textoBadge + " · " + situacao.getRevisor();
        }
        rotuloStatusCuradoria.setText(textoBadge);
        rotuloStatusCuradoria.getStyleClass().removeAll(
                "badge-candidata", "badge-validada", "badge-recusada");
        rotuloStatusCuradoria.getStyleClass().add(switch (status) {
            case VALIDADA -> "badge-validada";
            case RECUSADA -> "badge-recusada";
            case CANDIDATA_NAO_CURADA -> "badge-candidata";
        });
    }

    private void registrarErro(String cabecalho, RuntimeException erro) {
        String mensagem = erro.getMessage() != null && !erro.getMessage().isBlank()
                ? erro.getMessage()
                : erro.getClass().getSimpleName();
        atualizarBarraStatus(cabecalho + ": " + mensagem, situacaoAtual);

        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Regra de domínio");
        alerta.setHeaderText(cabecalho);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    // ------------------------------------------------------------------
    // Auxiliares
    // ------------------------------------------------------------------

    private int lerSpinnerInteiro(Spinner<Integer> spinner) {
        SpinnerValueFactory<Integer> factory = spinner.getValueFactory();
        if (spinner.isEditable()) {
            String texto = spinner.getEditor().getText();
            if (texto != null && !texto.isBlank()) {
                try {
                    int valor = Integer.parseInt(texto.trim());
                    factory.setValue(valor);
                } catch (NumberFormatException erro) {
                    throw new IllegalArgumentException("Valor inteiro inválido: '" + texto + "'");
                }
            }
        }
        Integer valor = spinner.getValue();
        return valor != null ? valor : 0;
    }

    private String nomeParticipanteOuId(String idParticipante) {
        ParticipanteNarrativo p = rascunho.participantePorId(idParticipante);
        return p != null ? p.getNomeExibido() : idParticipante;
    }

    private String nomeObjetoOuId(String idObjeto) {
        ObjetoContado obj = rascunho.objetoPorId(idObjeto);
        return obj != null
                ? obj.getRealizacaoTextual().getSingular() + " (" + obj.getId() + ")"
                : idObjeto;
    }

    private static String formatarCaracteristicas(ObjetoContado objeto) {
        if (objeto.getCaracteristicas().isEmpty()) {
            return "—";
        }
        return objeto.getCaracteristicas().stream()
                .map(carac -> carac.getDimensao() + ": " + carac.getValor())
                .collect(Collectors.joining(", "));
    }

    private static String idParticipanteSelecionado(ComboBox<ParticipanteNarrativo> combo) {
        ParticipanteNarrativo selecionado = combo.getValue();
        return selecionado != null ? selecionado.getId() : null;
    }

    private static String idObjetoSelecionado(ComboBox<ObjetoContado> combo) {
        ObjetoContado selecionado = combo.getValue();
        return selecionado != null ? selecionado.getId() : null;
    }

    private static void selecionarParticipantePorId(ComboBox<ParticipanteNarrativo> combo, String id) {
        if (id == null) {
            combo.setValue(null);
            return;
        }
        for (ParticipanteNarrativo item : combo.getItems()) {
            if (id.equals(item.getId())) {
                combo.setValue(item);
                return;
            }
        }
        combo.setValue(null);
    }

    private static void selecionarObjetoPorId(ComboBox<ObjetoContado> combo, String id) {
        if (id == null) {
            combo.setValue(null);
            return;
        }
        for (ObjetoContado item : combo.getItems()) {
            if (id.equals(item.getId())) {
                combo.setValue(item);
                return;
            }
        }
        combo.setValue(null);
    }

    private static String escaparHtml(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    /** Linha exibida nas tabelas de inventario inicial e final. */
    public record ItemInventarioLinha(String idObjeto, String nomeObjeto, int quantidade) {
    }

    /** Linha exibida na tabela ordenada de transformacoes. */
    public record TransformacaoLinha(
            int indiceNoRascunho,
            RascunhoDeSituacao.TransformacaoRascunhada dados) {
    }
}
