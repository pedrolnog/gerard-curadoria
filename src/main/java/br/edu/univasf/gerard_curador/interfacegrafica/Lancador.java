package br.edu.univasf.gerard_curador.interfacegrafica;

import javafx.application.Application;

/**
 * Ponto de entrada pelo classpath para execucao sem module-path.
 */
public final class Lancador {

    private Lancador() {
    }

    public static void main(String[] args) {
        Application.launch(CuradorApplication.class, args);
    }
}
