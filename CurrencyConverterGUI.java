import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CurrencyConverterGUI extends JFrame {
    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/USD";

    private JComboBox<String> baseCurrencyComboBox;
    private JComboBox<String> targetCurrencyComboBox;
    private JTextField amountTextField;
    private JLabel resultLabel;

    public CurrencyConverterGUI() {
        setTitle("Currency Converter");
        setSize(1000, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createUI();

        // Fetch exchange rates from the API
        try {
            JSONObject exchangeRates = getExchangeRates();
            setupCurrencyComboBoxes(exchangeRates);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to fetch exchange rates.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void createUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel baseCurrencyLabel = new JLabel("Base Currency:");
        baseCurrencyComboBox = new JComboBox<>();
        JLabel targetCurrencyLabel = new JLabel("Target Currency:");
        targetCurrencyComboBox = new JComboBox<>();
        JLabel amountLabel = new JLabel("Amount:");
        amountTextField = new JTextField(9);
        JLabel resultTextLabel = new JLabel("Result:");
        resultLabel = new JLabel();

        JButton convertButton = new JButton("Convert");
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertCurrency();
            }
        });

        panel.add(baseCurrencyLabel);
        panel.add(baseCurrencyComboBox);
        panel.add(targetCurrencyLabel);
        panel.add(targetCurrencyComboBox);
        panel.add(amountLabel);
        panel.add(amountTextField);
        panel.add(resultTextLabel);
        panel.add(resultLabel);
        panel.add(convertButton);

        getContentPane().add(panel);
    }

    private void setupCurrencyComboBoxes(JSONObject exchangeRates) {
        JSONObject rates = exchangeRates.getJSONObject("rates");

        for (String currency : rates.keySet()) {
            baseCurrencyComboBox.addItem(currency);
            targetCurrencyComboBox.addItem(currency);
        }
    }

    private void convertCurrency() {
        try {
            String baseCurrency = (String) baseCurrencyComboBox.getSelectedItem();
            String targetCurrency = (String) targetCurrencyComboBox.getSelectedItem();
            double amount = Double.parseDouble(amountTextField.getText());

            JSONObject exchangeRates = getExchangeRates();
            JSONObject rates = exchangeRates.getJSONObject("rates");

            double baseRate = rates.getDouble(baseCurrency);
            double targetRate = rates.getDouble(targetCurrency);

            double convertedAmount = amount * (targetRate / baseRate);

            resultLabel.setText(String.format("%.2f %s is equal to %.2f %s",
                    amount, baseCurrency, convertedAmount, targetCurrency));

        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input or failed to perform conversion.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private JSONObject getExchangeRates() throws IOException {
        try {
            URI uri = new URI(API_URL);
            HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            return new JSONObject(response.body());
        } catch (Exception e) {
            throw new IOException("Failed to fetch exchange rates.", e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CurrencyConverterGUI converterGUI = new CurrencyConverterGUI();
            converterGUI.setVisible(true);
        });
    }
}
