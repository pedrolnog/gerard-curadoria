package br.edu.univasf.gerard_curador.representacao.quadrinhos;

/** Realizacao do roteiro como pagina HTML (tira de quadrinhos em grade). */
public final class RenderizadorHtml implements RenderizadorDeRoteiro {

    /**
     * Realizacao do mesmo roteiro em HTML.
     *
     * <p>Demonstra a independencia de midia exigida pelo projeto: as duas estrategias
     * partem do mesmo RoteiroQuadrinhos, sem consultar o dominio de novo.
     */
    @Override
    public String renderizar(RoteiroQuadrinhos roteiro) {
        exigirRoteiro(roteiro);
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>").append(System.lineSeparator());
        html.append("<html lang=\"pt-BR\">").append(System.lineSeparator());
        html.append("<head>").append(System.lineSeparator());
        html.append("  <meta charset=\"UTF-8\">").append(System.lineSeparator());
        html.append("  <title>").append(escapar(roteiro.getTitulo())).append("</title>")
                .append(System.lineSeparator());
        html.append("  <style>").append(System.lineSeparator());
        html.append("    body { font-family: system-ui, sans-serif; margin: 2rem; "
                + "background: #f6f4ef; color: #1d2d3f; }").append(System.lineSeparator());
        html.append("    h1 { font-size: 1.5rem; }").append(System.lineSeparator());
        html.append("    .tira { display: grid; gap: 1rem; "
                + "grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); }")
                .append(System.lineSeparator());
        html.append("    .quadro { border: 2px solid #1d2d3f; border-radius: 6px; "
                + "padding: 1rem; background: #fff; }").append(System.lineSeparator());
        html.append("    .quadro h2 { font-size: 0.95rem; text-transform: uppercase; "
                + "letter-spacing: 0.05em; margin: 0 0 0.5rem; color: #7a5c1e; }")
                .append(System.lineSeparator());
        html.append("    .legenda { font-style: italic; margin-bottom: 0.75rem; }")
                .append(System.lineSeparator());
        html.append("    .balao { border: 1px solid #1d2d3f; border-radius: 12px; "
                + "padding: 0.5rem 0.75rem; margin-bottom: 0.5rem; }")
                .append(System.lineSeparator());
        html.append("    .balao.pergunta { border-style: dashed; background: #fff8e5; "
                + "font-weight: 600; }").append(System.lineSeparator());
        html.append("    .cena { font-size: 0.8rem; color: #5a6b7d; }")
                .append(System.lineSeparator());
        html.append("  </style>").append(System.lineSeparator());
        html.append("</head>").append(System.lineSeparator());
        html.append("<body>").append(System.lineSeparator());
        html.append("<h1>").append(escapar(roteiro.getTitulo())).append("</h1>")
                .append(System.lineSeparator());
        html.append("<div class=\"tira\">").append(System.lineSeparator());

        for (Quadro quadro : roteiro.getQuadros()) {
            html.append("  <section class=\"quadro\">").append(System.lineSeparator());
            html.append("    <h2>").append(quadro.getNumero()).append(". ")
                    .append(escapar(quadro.getTitulo())).append("</h2>")
                    .append(System.lineSeparator());
            if (quadro.possuiLegenda()) {
                html.append("    <p class=\"legenda\">").append(escapar(quadro.getLegenda()))
                        .append("</p>").append(System.lineSeparator());
            }
            for (Balao balao : quadro.getBaloes()) {
                String classe = balao.getTipo() == Balao.Tipo.PERGUNTA
                        ? "balao pergunta" : "balao";
                html.append("    <p class=\"").append(classe).append("\">");
                if (balao.possuiEmissor()) {
                    html.append("<strong>").append(escapar(balao.getEmissor()))
                            .append(":</strong> ");
                }
                html.append(escapar(balao.getTexto())).append("</p>")
                        .append(System.lineSeparator());
            }
            if (quadro.possuiCena()) {
                html.append("    <p class=\"cena\">").append(escapar(quadro.getCena().descrever()))
                        .append("</p>").append(System.lineSeparator());
            }
            html.append("  </section>").append(System.lineSeparator());
        }

        html.append("</div>").append(System.lineSeparator());
        html.append("</body>").append(System.lineSeparator());
        html.append("</html>").append(System.lineSeparator());
        return html.toString();
    }

    /** Escapa os caracteres que quebrariam a marcacao. */
    private String escapar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    @Override
    public String getExtensao() {
        return "html";
    }
}
