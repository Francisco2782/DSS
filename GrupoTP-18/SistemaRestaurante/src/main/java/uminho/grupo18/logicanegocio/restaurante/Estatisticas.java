package uminho.grupo18.logicanegocio.restaurante;

import uminho.grupo18.logicanegocio.cozinha.Ingrediente;
import uminho.grupo18.logicanegocio.pedidos.Proposta;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Estatisticas {

    private float faturacao = 0;
    private Map<Ingrediente, Integer> stock = new HashMap<>();
    private LocalTime tempoMedioEspera = LocalTime.of(0, 0);

    // Opção A: popularidade por frequência
    private Map<Proposta, Integer> popularidade = new HashMap<>();

    public Estatisticas() {
        // defaults já inicializados
    }

    public Estatisticas(float faturacao,
                        Map<Ingrediente, Integer> stock,
                        LocalTime tempoMedioEspera,
                        Map<Proposta, Integer> popularidade) {

        this.faturacao = faturacao;
        this.stock = new HashMap<>(stock);
        this.tempoMedioEspera = tempoMedioEspera;
        this.popularidade = new HashMap<>(popularidade);
    }

    public Estatisticas merge(List<Estatisticas> estatisticas) {
        Estatisticas resultado = new Estatisticas();

        long somaSegundos = 0;
        int n = 0;

        for (Estatisticas e : estatisticas) {
            if (e == null) continue;

            resultado.faturacao += e.faturacao;

            for (Map.Entry<Ingrediente, Integer> entry : e.stock.entrySet()) {
                resultado.stock.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }

            // combinar popularidade (frequência)
            for (Map.Entry<Proposta, Integer> entry : e.popularidade.entrySet()) {
                resultado.popularidade.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }

            if (e.tempoMedioEspera != null) {
                somaSegundos += e.tempoMedioEspera.toSecondOfDay();
                n++;
            }
        }

        if (n > 0) {
            resultado.tempoMedioEspera = LocalTime.ofSecondOfDay(somaSegundos / n);
        }

        return resultado;
    }

    public void addStockIngrediente(Ingrediente ingrediente, int quantidade) {
        stock.merge(ingrediente, quantidade, Integer::sum);
    }

    public void removerStockIngrediente(Ingrediente ingrediente, int quantidade) {
        stock.compute(ingrediente, (k, v) -> {
            int atual = (v == null) ? 0 : v;
            int novo = atual - quantidade;
            return Math.max(novo, 0);
        });
    }

    public int getStockIngrediente(Ingrediente ingrediente) {
        return stock.getOrDefault(ingrediente, 0);
    }

    public LocalTime getTempoMedioEspera() {
        return tempoMedioEspera;
    }

    public void setTempoMedioEspera(LocalTime tempoMedioEspera) {
        this.tempoMedioEspera = tempoMedioEspera;
    }

    public float getFaturacao() {
        return faturacao;
    }

    public void setFaturacao(float faturacao) {
        this.faturacao = faturacao;
    }

    // --------- Popularidade (frequência) ---------

    public void registarProposta(Proposta proposta) {
        if (proposta == null) return;
        popularidade.merge(proposta, 1, Integer::sum);
    }

    public int getPopularidade(Proposta proposta) {
        if (proposta == null) return 0;
        return popularidade.getOrDefault(proposta, 0);
    }

    public Map<Proposta, Integer> getPopularidadeOrdenada() {
        return popularidade.entrySet()
                .stream()
                .sorted(Map.Entry.<Proposta, Integer>comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }
}
