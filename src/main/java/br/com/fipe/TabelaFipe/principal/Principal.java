package br.com.fipe.TabelaFipe.principal;

import br.com.fipe.TabelaFipe.model.Dados;
import br.com.fipe.TabelaFipe.model.Modelos;
import br.com.fipe.TabelaFipe.model.Veiculo;
import br.com.fipe.TabelaFipe.service.ConsumoApi;
import br.com.fipe.TabelaFipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner scanner = new Scanner(System.in);

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1";

    private ConsumoApi consumoApi = new ConsumoApi();

    private ConverteDados converteDados = new ConverteDados();

    public void exibMenu(){

        var menu = """
                *** Opções ***
                Carro
                Moto
                Caminhão
                
                Digite a opção desejada:
                """;

        System.out.println(menu);
        var opcao = scanner.nextLine();
        String endereco;

        if(opcao.toLowerCase().contains("carr")){
            endereco = URL_BASE + "/carros/marcas";
        }else if(opcao.toLowerCase().contains("mot")){
            endereco = URL_BASE + "/motos/marcas";
        } else if (opcao.toLowerCase().contains("cam")){
            endereco = URL_BASE + "/caminhoes/marcas";
        } else {
            System.out.println("Opção inválida");
            return;
        }

        var json = consumoApi.obterDados(endereco);

        var marcas = converteDados.obterDadosLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("Digite o código da marca desejada:");
        var codigoMarca = scanner.nextLine();

        endereco = endereco + "/" + codigoMarca + "/modelos";

        json = consumoApi.obterDados(endereco);
        var modeloLista = converteDados.obterDados(json, Modelos.class);

        System.out.println("\nModelos dessa marca:");
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\nDigite um trecho do nome do carro a ser buscado");
        var nomeVeiculo = scanner.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\nModelos filtrados");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("Digite por favor o código do modelo para buscar os valores de avaliação: ");
        var codigoModelo = scanner.nextLine();

        endereco = endereco + "/" + codigoModelo + "/anos";
        json = consumoApi.obterDados(endereco);
        List<Dados> anos = converteDados.obterDadosLista(json, Dados.class);
        List<Veiculo> veiculos = new ArrayList<>();

        for (int i = 0; i < anos.size(); i++) {
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumoApi.obterDados(enderecoAnos);
            Veiculo veiculo = converteDados.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("\nTodos os veículos filtrados com avaliações por ano: ");
        veiculos.forEach(System.out::println);


    }
}
