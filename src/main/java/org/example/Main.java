package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

public class Main {

    private static final String API_KEY = "24815ec5ac005193b8b1dc7f"; // Replace with your API key
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the base currency (e.g., USD): ");
        String baseCurrency = scanner.nextLine().toUpperCase();

        System.out.print("Enter the target currency (e.g., EUR): ");
        String targetCurrency = scanner.nextLine().toUpperCase();

        System.out.print("Enter the amount to convert: ");
        double amount = scanner.nextDouble();

        try {
            double exchangeRate = fetchExchangeRate(baseCurrency, targetCurrency);
            if (exchangeRate != -1) {
                double convertedAmount = amount * exchangeRate;
                System.out.printf("Converted Amount: %.2f %s%n", convertedAmount, targetCurrency);
            } else {
                System.out.println("Failed to fetch exchange rate.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        scanner.close();
    }

    private static double fetchExchangeRate(String baseCurrency, String targetCurrency) throws Exception {
        URL url = new URL(API_URL + baseCurrency);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        conn.disconnect();

        JSONObject jsonResponse = new JSONObject(content.toString());
        if (jsonResponse.has("conversion_rates")) {
            JSONObject rates = jsonResponse.getJSONObject("conversion_rates");
            if (rates.has(targetCurrency)) {
                return rates.getDouble(targetCurrency);
            } else {
                System.out.println("Target currency not found in response.");
                return -1;
            }
        } else {
            System.out.println("Error in response: " + jsonResponse.getString("error-type"));
            return -1;
        }
    }
}
