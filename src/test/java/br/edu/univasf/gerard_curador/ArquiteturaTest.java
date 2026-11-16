package br.edu.univasf.gerard_curador;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testes de regras arquiteturais do domínio")
class ArquiteturaTest {

    private static final Path DOMINIO_PATH =
            Path.of("src", "main", "java", "br", "edu", "univasf", "gerard_curador", "dominio");

    private static final List<String> IMPORTS_PROIBIDOS_UI_E_JSON = List.of(
            "javafx.",
            "javax.swing.",
            "java.awt.",
            "com.google.gson."
    );

    private static final List<String> IMPORTS_PROIBIDOS_CAMADAS_EXTERNAS = List.of(
            "br.edu.univasf.gerard_curador.persistencia",
            "br.edu.univasf.gerard_curador.representacao",
            "br.edu.univasf.gerard_curador.interfacegrafica",
            "br.edu.univasf.gerard_curador.app"
    );

    @Test
    @DisplayName("Domínio não deve importar bibliotecas de interface gráfica (JavaFX, Swing, AWT) nem Gson")
    void dominioNaoDeveImportarBibliotecasDeUiNemGson() throws IOException {
        List<String> arquivosCulpados = verificarViolacoesDeImports(IMPORTS_PROIBIDOS_UI_E_JSON);
        assertTrue(
                arquivosCulpados.isEmpty(),
                "Foram encontrados arquivos do domínio com imports de interface gráfica ou Gson:\n"
                        + String.join(System.lineSeparator(), arquivosCulpados)
        );
    }

    @Test
    @DisplayName("Domínio não deve importar persistência, representação, interface gráfica ou app")
    void dominioNaoDeveImportarCamadasExternas() throws IOException {
        List<String> arquivosCulpados = verificarViolacoesDeImports(IMPORTS_PROIBIDOS_CAMADAS_EXTERNAS);
        assertTrue(
                arquivosCulpados.isEmpty(),
                "Foram encontrados arquivos do domínio importando camadas externas proibidas (persistencia, representacao, interfacegrafica ou app):\n"
                        + String.join(System.lineSeparator(), arquivosCulpados)
        );
    }

    private List<String> verificarViolacoesDeImports(List<String> prefixosProibidos) throws IOException {
        assertTrue(
                Files.exists(DOMINIO_PATH),
                "O diretório de domínio deve existir no caminho relativo: " + DOMINIO_PATH
        );

        List<Path> arquivosJava;
        try (Stream<Path> stream = Files.walk(DOMINIO_PATH)) {
            arquivosJava = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".java"))
                    .toList();
        }

        List<String> violacoes = new ArrayList<>();
        for (Path arquivo : arquivosJava) {
            List<String> linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);
            for (int i = 0; i < linhas.size(); i++) {
                String linha = linhas.get(i).trim();
                if (linha.startsWith("import ")) {
                    String importAlvo = linha.substring("import ".length()).trim();
                    if (importAlvo.startsWith("static ")) {
                        importAlvo = importAlvo.substring("static ".length()).trim();
                    }
                    for (String proibido : prefixosProibidos) {
                        if (importAlvo.startsWith(proibido)) {
                            violacoes.add(String.format("%s (linha %d): %s",
                                    arquivo.toString(), i + 1, linha));
                        }
                    }
                }
            }
        }
        return violacoes;
    }
}
