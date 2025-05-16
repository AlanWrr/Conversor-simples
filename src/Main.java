import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Logger;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/934f14a1c597695fc2044219/latest/USD";

    // Lista de moedas suportadas no menu
    private static final String[] MOEDAS_SUPORTADAS = {"BRL", "EUR", "GBP", "JPY", "CAD", "AUD"};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            String json = obterJsonDaApi();

            while (true) {
                System.out.println("\n==============================");
                System.out.println("     CONVERSOR DE MOEDAS");
                System.out.println("==============================");
                System.out.println("Moedas disponíveis:");
                for (int i = 0; i < MOEDAS_SUPORTADAS.length; i++) {
                    System.out.printf("%d. %s\n", i + 1, MOEDAS_SUPORTADAS[i]);
                }

                System.out.print("\nEscolha o número da moeda de destino: ");
                int escolha = scanner.nextInt();
                scanner.nextLine(); // limpar o buffer

                if (escolha < 1 || escolha > MOEDAS_SUPORTADAS.length) {
                    System.out.println("Opção inválida. Tente novamente.");
                    continue;
                }

                String moeda = MOEDAS_SUPORTADAS[escolha - 1];

                System.out.print("Digite o valor em USD: ");
                double valorUSD = scanner.nextDouble();
                scanner.nextLine();

                String busca = "\"" + moeda + "\":";
                int index = json.indexOf(busca);

                if (index == -1) {
                    System.out.println("Moeda não encontrada na resposta da API.");
                    continue;
                }

                int start = index + busca.length();
                int end = json.indexOf(",", start);
                if (end == -1) end = json.indexOf("}", start);

                String taxaStr = json.substring(start, end).trim();
                double taxa = Double.parseDouble(taxaStr);

                double convertido = valorUSD * taxa;
                System.out.printf("\nUSD %.2f equivale a %s %.2f\n", valorUSD, moeda, convertido);

                System.out.print("\nDeseja converter outro valor? (s/n): ");
                String resposta = scanner.nextLine();
                if (!resposta.equalsIgnoreCase("s")) break;
            }

            System.out.println("Programa encerrado.");
            scanner.close();

        } catch (Exception e) {
            logger.severe("Erro ao conectar ou processar dados da API: " + e.getMessage());
        }
    }

    private static String obterJsonDaApi() throws Exception {
        URL url = URI.create(API_URL).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder jsonBuilder = new StringBuilder();
        String linha;

        while ((linha = reader.readLine()) != null) {
            jsonBuilder.append(linha);
        }
        reader.close();

        return jsonBuilder.toString();
    }
}
