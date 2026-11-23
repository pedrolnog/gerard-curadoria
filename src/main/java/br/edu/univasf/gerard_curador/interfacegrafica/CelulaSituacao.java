package br.edu.univasf.gerard_curador.interfacegrafica;

import br.edu.univasf.gerard_curador.dominio.situacao.SituacaoProblema;
import br.edu.univasf.gerard_curador.dominio.situacao.StatusCuradoria;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Celula personalizada para listar situacoes gravadas no repositorio.
 */
public final class CelulaSituacao extends ListCell<SituacaoProblema> {

    private static final int LIMITE_ENUNCIADO = 76;

    private final VBox cartao = new VBox(5);
    private final HBox cabecalho = new HBox(8);
    private final Label rotuloId = new Label();
    private final Region espacador = new Region();
    private final Label badgeStatus = new Label();
    private final Label rotuloResumo = new Label();

    public CelulaSituacao() {
        cartao.getStyleClass().add("celula-situacao");
        cabecalho.setAlignment(Pos.CENTER_LEFT);

        rotuloId.getStyleClass().add("situacao-id");
        HBox.setHgrow(espacador, Priority.ALWAYS);

        badgeStatus.getStyleClass().add("badge-status");
        badgeStatus.setMinWidth(Region.USE_PREF_SIZE);

        rotuloResumo.getStyleClass().add("situacao-resumo");
        rotuloResumo.setWrapText(true);

        // O selo vai para baixo do resumo para o id nunca ser cortado na lista.
        cabecalho.getChildren().addAll(rotuloId, espacador);
        cartao.getChildren().addAll(cabecalho, rotuloResumo, badgeStatus);

        listViewProperty().addListener((obs, antiga, nova) -> {
            if (nova != null) {
                cartao.maxWidthProperty().bind(nova.widthProperty().subtract(28));
            } else {
                cartao.maxWidthProperty().unbind();
            }
        });
    }

    @Override
    protected void updateItem(SituacaoProblema item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        rotuloId.setText(item.getId());
        rotuloResumo.setText(resumirEnunciado(item.getEnunciado()));

        StatusCuradoria status = item.getStatus();
        badgeStatus.setText(status.getNomeExibido());
        badgeStatus.getStyleClass().removeAll(
                "badge-candidata", "badge-validada", "badge-recusada");
        badgeStatus.getStyleClass().add(classeCssParaStatus(status));
        // A faixa colorida do cartao repete o status para leitura rapida da lista.
        cartao.getStyleClass().removeAll("celula-candidata", "celula-validada", "celula-recusada");
        cartao.getStyleClass().add(classeCssParaStatus(status).replace("badge-", "celula-"));

        setText(null);
        setGraphic(cartao);
    }

    private static String classeCssParaStatus(StatusCuradoria status) {
        return switch (status) {
            case VALIDADA -> "badge-validada";
            case RECUSADA -> "badge-recusada";
            case CANDIDATA_NAO_CURADA -> "badge-candidata";
        };
    }

    private static String resumirEnunciado(String enunciado) {
        if (enunciado == null || enunciado.isBlank()) {
            return "(sem enunciado)";
        }
        String limpo = enunciado.replaceAll("\\s+", " ").trim();
        if (limpo.length() <= LIMITE_ENUNCIADO) {
            return limpo;
        }
        return limpo.substring(0, LIMITE_ENUNCIADO) + "…";
    }
}
