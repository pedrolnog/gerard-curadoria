package br.edu.univasf.gerard_curador.interfacegrafica;

import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Aplicacao JavaFX da tela de curadoria de situacoes-problema aditivas.
 */
public final class CuradorApplication extends Application {

    private static final String TITULO = "Curador de Histórias Aditivas";
    private static final double LARGURA_INICIAL = 1280;
    private static final double ALTURA_INICIAL = 800;
    private static final double LARGURA_MINIMA = 1024;
    private static final double ALTURA_MINIMA = 680;

    @Override
    public void start(Stage stage) throws IOException {
        URL fxmlUrl = getClass().getResource(
                "/br/edu/univasf/gerard_curador/interfacegrafica/curadoria.fxml");
        if (fxmlUrl == null) {
            throw new IllegalStateException("Recurso FXML nao encontrado: curadoria.fxml");
        }
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent raiz = loader.load();

        Scene cena = new Scene(raiz, LARGURA_INICIAL, ALTURA_INICIAL);
        URL cssUrl = getClass().getResource(
                "/br/edu/univasf/gerard_curador/interfacegrafica/curadoria.css");
        if (cssUrl != null) {
            cena.getStylesheets().add(cssUrl.toExternalForm());
        }

        stage.setTitle(TITULO);
        stage.setScene(cena);
        stage.setMinWidth(LARGURA_MINIMA);
        stage.setMinHeight(ALTURA_MINIMA);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
