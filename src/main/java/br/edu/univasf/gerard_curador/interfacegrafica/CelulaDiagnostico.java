package br.edu.univasf.gerard_curador.interfacegrafica;

import br.edu.univasf.gerard_curador.dominio.validacao.Diagnostico;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Celula personalizada para exibir um {@link Diagnostico} na lista de revisao.
 */
public final class CelulaDiagnostico extends ListCell<Diagnostico> {

    private final VBox cartao = new VBox(4);
    private final HBox cabecalho = new HBox(8);
    private final Label etiquetaSeveridade = new Label();
    private final Label rotuloTipo = new Label();
    private final Label rotuloElemento = new Label();
    private final Label rotuloDetalhe = new Label();

    public CelulaDiagnostico() {
        cartao.getStyleClass().add("celula-diagnostico");
        cabecalho.setAlignment(Pos.CENTER_LEFT);

        etiquetaSeveridade.getStyleClass().add("etiqueta-diagnostico");
        rotuloTipo.getStyleClass().add("diagnostico-tipo");
        rotuloTipo.setWrapText(true);

        rotuloElemento.getStyleClass().add("diagnostico-elemento");
        rotuloElemento.setWrapText(true);

        rotuloDetalhe.getStyleClass().add("diagnostico-detalhe");
        rotuloDetalhe.setWrapText(true);

        cabecalho.getChildren().addAll(etiquetaSeveridade, rotuloTipo);
        cartao.getChildren().addAll(cabecalho, rotuloElemento, rotuloDetalhe);

        listViewProperty().addListener((obs, antiga, nova) -> {
            if (nova != null) {
                cartao.maxWidthProperty().bind(nova.widthProperty().subtract(28));
            } else {
                cartao.maxWidthProperty().unbind();
            }
        });
    }

    @Override
    protected void updateItem(Diagnostico item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        etiquetaSeveridade.getStyleClass().removeAll("etiqueta-erro", "etiqueta-aviso");
        if (item.ehErro()) {
            etiquetaSeveridade.setText("ERRO");
            etiquetaSeveridade.getStyleClass().add("etiqueta-erro");
        } else {
            etiquetaSeveridade.setText("AVISO");
            etiquetaSeveridade.getStyleClass().add("etiqueta-aviso");
        }

        rotuloTipo.setText(item.getTipo().getNomeExibido());
        rotuloElemento.setText("Elemento: " + item.getElementoEnvolvido());
        rotuloDetalhe.setText(item.getDetalhe());

        setText(null);
        setGraphic(cartao);
    }
}
