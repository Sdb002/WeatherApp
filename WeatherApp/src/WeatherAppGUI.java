import org.json.simple.JSONObject;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class WeatherAppGUI extends JFrame {
    private JSONObject weatherData;
    private JLabel temperatureText, weatherConditionDesc, weatherConditionImage;
    private JLabel humidityText, windspeedText;
    private boolean isDarkMode = false;
    private DefaultListModel<String> suggestionListModel;
    private JList<String> suggestionList;
    private JScrollPane suggestionScrollPane;
    private JTextField searchTextField;
    private JButton darkModeButton, forecastButton;

    public WeatherAppGUI() {
        super("Weather App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 650);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        addGuiComponents();
        applyTheme();
    }

    private void applyTheme() {
        Color backgroundColor = isDarkMode ? new Color(30, 30, 30) : Color.WHITE;
        Color textColor = isDarkMode ? Color.WHITE : Color.BLACK;
        Color inputFieldColor = isDarkMode ? new Color(60, 60, 60) : Color.LIGHT_GRAY;
        Color buttonColor = isDarkMode ? new Color(50, 50, 50) : new Color(220, 220, 220);

        getContentPane().setBackground(backgroundColor);
        searchTextField.setBackground(inputFieldColor);
        searchTextField.setForeground(textColor);
        searchTextField.setBorder(BorderFactory.createLineBorder(isDarkMode ? Color.WHITE : Color.GRAY));

        temperatureText.setForeground(textColor);
        weatherConditionDesc.setForeground(textColor);
        humidityText.setForeground(textColor);
        windspeedText.setForeground(textColor);

        // Use the same Darkmode.png asset but invert colors in dark mode
        ImageIcon icon = resizeImage("src/assets/Darkmode.png", 40, 40);
        if (isDarkMode) {
            icon = invertIconColors(icon);
        }
        darkModeButton.setIcon(icon);
        darkModeButton.setBackground(backgroundColor);

        forecastButton.setBackground(buttonColor);
        forecastButton.setForeground(textColor);

        repaint();
    }

    private ImageIcon invertIconColors(ImageIcon icon) {
        BufferedImage img = new BufferedImage(
                icon.getIconWidth(),
                icon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g = img.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int rgba = img.getRGB(x, y);
                Color col = new Color(rgba, true);
                col = new Color(255 - col.getRed(), 255 - col.getGreen(), 255 - col.getBlue(), col.getAlpha());
                img.setRGB(x, y, col.getRGB());
            }
        }
        return new ImageIcon(img);
    }

    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        applyTheme();
    }

    private void addGuiComponents() {
        searchTextField = new JTextField();
        searchTextField.setBounds(15, 15, 351, 45);
        searchTextField.setFont(new Font("Arial", Font.PLAIN, 24));
        searchTextField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        searchTextField.setBackground(Color.LIGHT_GRAY);
        add(searchTextField);

        suggestionListModel = new DefaultListModel<>();
        suggestionList = new JList<>(suggestionListModel);
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!suggestionList.isSelectionEmpty()) {
                    searchTextField.setText(suggestionList.getSelectedValue());
                    suggestionScrollPane.setVisible(false);
                }
            }
        });

        suggestionScrollPane = new JScrollPane(suggestionList);
        suggestionScrollPane.setBounds(15, 60, 351, 100);
        suggestionScrollPane.setVisible(false);
        add(suggestionScrollPane);

        searchTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                SwingUtilities.invokeLater(() -> {
                    String query = searchTextField.getText().trim();
                    if (query.isEmpty()) {
                        suggestionScrollPane.setVisible(false);
                        return;
                    }
                    List<String> suggestions = WeatherApp.getLocationSuggestions(query);
                    suggestionListModel.clear();
                    if (!suggestions.isEmpty()) {
                        suggestions.forEach(suggestionListModel::addElement);
                        suggestionScrollPane.setVisible(true);
                    } else {
                        suggestionScrollPane.setVisible(false);
                    }
                });
            }
        });

        JButton searchButton = new JButton(resizeImage("src/assets/search.png", 40, 40));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.setContentAreaFilled(false);
        searchButton.setBorderPainted(false);
        searchButton.setFocusPainted(false);
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(e -> {
            String userInput = searchTextField.getText().trim();
            if (!userInput.isEmpty()) {
                weatherData = WeatherApp.getWeatherData(userInput);
                updateWeatherDisplay();
            }
        });
        add(searchButton);

        weatherConditionImage = new JLabel(resizeImage("src/assets/cloudy.png", 200, 200));
        weatherConditionImage.setBounds(125, 125, 200, 200);
        add(weatherConditionImage);

        temperatureText = new JLabel("10°C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Arial", Font.BOLD, 48));
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Arial", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        JLabel humidityImage = new JLabel(resizeImage("src/assets/humidity.png", 50, 50));
        humidityImage.setBounds(15, 500, 50, 50);
        add(humidityImage);

        humidityText = new JLabel("<html><b>Humidity:</b> 100%</html>");
        humidityText.setBounds(70, 500, 120, 55);
        humidityText.setFont(new Font("Arial", Font.PLAIN, 16));
        add(humidityText);

        JLabel windspeedImage = new JLabel(resizeImage("src/assets/windspeed.png", 50, 50));
        windspeedImage.setBounds(220, 500, 50, 50);
        add(windspeedImage);

        windspeedText = new JLabel("<html><b>Windspeed:</b> 0 km/h</html>");
        windspeedText.setBounds(280, 500, 120, 55);
        windspeedText.setFont(new Font("Arial", Font.PLAIN, 16));
        add(windspeedText);

        darkModeButton = new JButton();
        darkModeButton.setBounds(390, 550, 45, 45);
        darkModeButton.setContentAreaFilled(false);
        darkModeButton.setBorderPainted(false);
        darkModeButton.setFocusPainted(false);
        darkModeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        darkModeButton.addActionListener(e -> toggleDarkMode());
        add(darkModeButton);

        forecastButton = new JButton("5-Day Forecast");
        forecastButton.setBounds(15, 560, 180, 45);
        forecastButton.setFont(new Font("Arial", Font.BOLD, 14));
        forecastButton.setBackground(new Color(220, 220, 220));
        forecastButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        forecastButton.addActionListener(e -> {
            String location = searchTextField.getText().trim();
            if (!location.isEmpty()) {
                new WeatherForecastGUI(location).setDarkMode(isDarkMode);
            } else {
                JOptionPane.showMessageDialog(this, "Please search for a location first!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(forecastButton);
    }

    private void updateWeatherDisplay() {
        if (weatherData == null) {
            JOptionPane.showMessageDialog(this, "Failed to get weather data", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (weatherData.get("temperature") != null) {
            double temp = ((Number) weatherData.get("temperature")).doubleValue();
            temperatureText.setText(String.format("%.1f°C", temp));
        }

        String condition = (String) weatherData.get("weather_condition");
        weatherConditionDesc.setText(condition != null ? condition : "N/A");

        if (weatherData.get("humidity") != null) {
            humidityText.setText("<html><b>Humidity:</b> " + weatherData.get("humidity") + "%</html>");
        }

        if (weatherData.get("wind_speed") != null) {
            windspeedText.setText("<html><b>Windspeed:</b> " + weatherData.get("wind_speed") + " km/h</html>");
        } else {
            windspeedText.setText("<html><b>Windspeed:</b> 0 km/h</html>");
        }

        if (condition != null) {
            String lowerCondition = condition.toLowerCase();
            String imagePath = "src/assets/";

            if (lowerCondition.contains("clear")) imagePath += "clear.png";
            else if (lowerCondition.contains("cloud")) imagePath += "cloudy.png";
            else if (lowerCondition.contains("rain")) imagePath += "rain.png";
            else if (lowerCondition.contains("thunder") || lowerCondition.contains("storm")) imagePath += "ts.png";
            else if (lowerCondition.contains("snow")) imagePath += "snow.png";
            else imagePath += "others.png";

            weatherConditionImage.setIcon(resizeImage(imagePath, 200, 200));
        }
    }

    private ImageIcon resizeImage(String resourcePath, int width, int height) {
        try {
            BufferedImage image = ImageIO.read(new File(resourcePath));
            if (image != null) {
                Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
        } catch (IOException e) {
            System.err.println("Error loading image: " + resourcePath);
            e.printStackTrace();
        }
        return new ImageIcon(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WeatherAppGUI app = new WeatherAppGUI();
            app.setVisible(true);
        });
    }
}