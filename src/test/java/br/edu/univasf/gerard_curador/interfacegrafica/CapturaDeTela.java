package br.edu.univasf.gerard_curador.interfacegrafica;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.imageio.ImageIO;

/**
 * Utilitario de desenvolvimento (nao e teste automatizado): abre a tela, carrega
 * os exemplos, seleciona a primeira situacao e salva um PNG de cada aba.
 *
 * <p>Uso: mvn exec:java -Dexec.classpathScope=test
 *   -Dexec.mainClass=br.edu.univasf.gerard_curador.interfacegrafica.CapturaDeTela -Dexec.args=DIR
 */
public class CapturaDeTela extends Application {

    private static String diretorio = "capturas";

    public static void main(String[] args) {
        if (args.length > 0) {
            diretorio = args[0];
        }
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent raiz = FXMLLoader.load(getClass().getResource(
                "/br/edu/univasf/gerard_curador/interfacegrafica/curadoria.fxml"));
        Scene cena = new Scene(raiz, 1280, 800);
        cena.getStylesheets().add(getClass().getResource(
                "/br/edu/univasf/gerard_curador/interfacegrafica/curadoria.css").toExternalForm());
        stage.setScene(cena);
        stage.show();
        new File(diretorio).mkdirs();

        List<Runnable> passos = new ArrayList<>();
        passos.add(() -> salvar(cena, "00-inicial"));
        passos.add(() -> botao(raiz, "Carregar exemplos").fire());
        passos.add(() -> ((ListView<?>) raiz.lookup("#listaSituacoes")).getSelectionModel().select(0));
        TabPane abas = (TabPane) raiz.lookup("#abasPrincipais");
        for (int i = 0; i < abas.getTabs().size(); i++) {
            int indice = i;
            passos.add(() -> abas.getSelectionModel().select(indice));
            passos.add(() -> salvar(cena, String.format("%02d-aba-%d", indice + 1, indice + 1)));
        }
        passos.add(Platform::exit);
        executar(passos, 0);
    }

    private void executar(List<Runnable> passos, int i) {
        if (i >= passos.size()) {
            return;
        }
        PauseTransition pausa = new PauseTransition(Duration.millis(i == 0 ? 1500 : 700));
        pausa.setOnFinished(e -> {
            try {
                passos.get(i).run();
            } catch (RuntimeException erro) {
                System.err.println("passo " + i + " falhou: " + erro);
            }
            executar(passos, i + 1);
        });
        pausa.play();
    }

    private static Button botao(Parent raiz, String texto) {
        for (Node no : raiz.lookupAll(".button")) {
            if (no instanceof Button b && b.getText() != null && b.getText().startsWith(texto)) {
                return b;
            }
        }
        throw new IllegalStateException("botao nao encontrado: " + texto);
    }

    private static void salvar(Scene cena, String nome) {
        WritableImage imagem = cena.snapshot(null);
        int w = (int) imagem.getWidth();
        int h = (int) imagem.getHeight();
        BufferedImage saida = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        PixelReader leitor = imagem.getPixelReader();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                saida.setRGB(x, y, leitor.getArgb(x, y));
            }
        }
        try {
            ImageIO.write(saida, "png", new File(diretorio, nome + ".png"));
            System.out.println("captura: " + nome);
        } catch (java.io.IOException erro) {
            throw new java.io.UncheckedIOException(erro);
        }
    }
}
