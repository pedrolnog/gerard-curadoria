# gerard-curadoria

Curador de Histórias Aditivas — projeto da disciplina de Programação Orientada a Objetos (UNIVASF).

## Requisitos

- JDK 17 ou superior
- Maven 3.9+

## Comandos

```sh
mvn test           # compila e roda os testes automatizados (JUnit 5)
mvn exec:java      # demonstração em console
mvn javafx:run     # tela de curadoria (JavaFX)
```

## Dependências e fronteiras

| Biblioteca | Onde pode ser usada |
|---|---|
| JUnit 5 | somente em `src/test` |
| Gson | somente no pacote `persistencia` |
| JavaFX | somente no pacote `interfacegrafica` |

O pacote `dominio` não importa nenhuma delas (critério de aceitação da seção 10 do enunciado).
