package br.edu.univasf.gerard_curador.persistencia;

import br.edu.univasf.gerard_curador.dominio.repositorio.Repositorio;
import br.edu.univasf.gerard_curador.dominio.situacao.SituacaoProblema;
import br.edu.univasf.gerard_curador.persistencia.dto.SituacaoDto;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Repositorio JSON de situacoes-problema usando Gson e DTOs anemicos.
 *
 * <p>Implementa o contrato {@link Repositorio} do dominio sem que o dominio
 * precise importar Gson ou conhecer arquivos: a persistencia converte o modelo
 * rico para DTOs anemicos na escrita e reconstroi os objetos na leitura sem
 * recalcular o estado final curado.
 */
public final class RepositorioSituacaoJson implements Repositorio<SituacaoProblema, String> {

    private final Path diretorio;
    private final Gson gson;
    private final MapeadorSituacao mapeador;

    /**
     * Cria o repositorio apontando para o diretorio informado, criando-o se nao existir.
     *
     * @param diretorio pasta onde os arquivos {@code <id>.json} serao gravados
     */
    public RepositorioSituacaoJson(Path diretorio) {
        if (diretorio == null) {
            throw new IllegalArgumentException("repositorio exige diretorio");
        }
        this.diretorio = diretorio;
        try {
            Files.createDirectories(diretorio);
        } catch (IOException erro) {
            throw new UncheckedIOException("falha ao criar diretorio " + diretorio, erro);
        }
        this.gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();
        this.mapeador = new MapeadorSituacao();
    }

    /**
     * Serializa a situacao para JSON formatado usando DTO anemico intermediario.
     *
     * @param situacao objeto do dominio rico
     * @return representacao JSON em UTF-8
     */
    public String serializar(SituacaoProblema situacao) {
        if (situacao == null) {
            throw new IllegalArgumentException("serializacao exige situacao");
        }
        return gson.toJson(mapeador.paraDto(situacao));
    }

    /**
     * Desserializa o texto JSON para o dominio sem recalcular valores curados.
     *
     * @param textoJson conteudo JSON
     * @return situacao reconstruida no dominio
     */
    public SituacaoProblema desserializar(String textoJson) {
        if (textoJson == null || textoJson.isBlank()) {
            throw new ErroDeLeituraJson("documento JSON vazio");
        }
        try {
            SituacaoDto dto = gson.fromJson(textoJson, SituacaoDto.class);
            if (dto == null) {
                throw new ErroDeLeituraJson("documento JSON vazio ou nulo");
            }
            return mapeador.paraDominio(dto);
        } catch (JsonParseException erro) {
            throw new ErroDeLeituraJson("JSON invalido: " + erro.getMessage(), erro);
        }
    }

    @Override
    public void salvar(SituacaoProblema situacao) {
        if (situacao == null) {
            throw new IllegalArgumentException("salvar exige situacao");
        }
        Path destino = arquivoDaSituacao(situacao.getId());
        try {
            Files.createDirectories(diretorio);
            Files.writeString(destino, serializar(situacao), StandardCharsets.UTF_8);
        } catch (IOException erro) {
            throw new UncheckedIOException("falha ao gravar " + destino, erro);
        }
    }

    @Override
    public Optional<SituacaoProblema> buscarPorId(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        Path arquivo = arquivoDaSituacao(id);
        if (!Files.exists(arquivo)) {
            return Optional.empty();
        }
        try {
            return Optional.of(desserializar(Files.readString(arquivo, StandardCharsets.UTF_8)));
        } catch (IOException erro) {
            throw new UncheckedIOException("falha ao ler " + arquivo, erro);
        }
    }

    @Override
    public List<SituacaoProblema> listarTodos() {
        List<SituacaoProblema> resultado = new ArrayList<>();
        try (Stream<Path> caminhos = Files.list(diretorio)) {
            List<Path> arquivos = caminhos
                    .filter(Files::isRegularFile)
                    .filter(caminho -> caminho.getFileName().toString().endsWith(".json"))
                    .sorted(Comparator.comparing(caminho -> caminho.getFileName().toString()))
                    .toList();
            for (Path arquivo : arquivos) {
                resultado.add(desserializar(Files.readString(arquivo, StandardCharsets.UTF_8)));
            }
            return resultado;
        } catch (IOException erro) {
            throw new UncheckedIOException("falha ao listar arquivos em " + diretorio, erro);
        }
    }

    @Override
    public boolean remover(String id) {
        if (id == null || id.isBlank()) {
            return false;
        }
        Path arquivo = arquivoDaSituacao(id);
        try {
            return Files.deleteIfExists(arquivo);
        } catch (IOException erro) {
            throw new UncheckedIOException("falha ao remover " + arquivo, erro);
        }
    }

    private Path arquivoDaSituacao(String id) {
        return diretorio.resolve(id + ".json");
    }
}
