import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Comparator;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import org.json.simple.JSONObject;

public class WeatherForecastGUI extends JFrame {
    private JTabbedPane tabbedPane;
    private boolean isDarkMode;

    public WeatherForecastGUI(String location) {
        super("5-Day Forecast - " + location);
        this.isDarkMode = false;

        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        add(tabbedPane, BorderLayout.CENTER);

        applyTheme();
        fetchAndOrganizeForecastData(location);

        setVisible(true);
    }

    public void setDarkMode(boolean isDarkMode) {
        this.isDarkMode = isDarkMode;
        applyTheme();
    }

    private void applyTheme() {
        Color bgColor = isDarkMode ? new Color(45, 45, 48) : Color.WHITE;
        Color textColor = isDarkMode ? Color.WHITE : Color.BLACK;
        Color tabBg = isDarkMode ? new Color(60, 63, 65) : new Color(240, 240, 240);

        getContentPane().setBackground(bgColor);
        tabbedPane.setBackground(tabBg);
        tabbedPane.setForeground(textColor);

        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            Component comp = tabbedPane.getComponentAt(i);
            if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                scrollPane.getViewport().setBackground(bgColor);

                if (scrollPane.getViewport().getView() instanceof JTextArea) {
                    JTextArea textArea = (JTextArea) scrollPane.getViewport().getView();
                    textArea.setBackground(bgColor);
                    textArea.setForeground(textColor);
                    textArea.setCaretColor(textColor);
                }
            }
        }
    }

    private void fetchAndOrganizeForecastData(String location) {
        java.util.List<JSONObject> forecastList = WeatherForecast.get5DayForecast(location);

        if (forecastList == null || forecastList.isEmpty()) {
            JTextArea noDataArea = createTextArea();
            noDataArea.setText("No weather data available for " + location);
            tabbedPane.addTab("No Data", new JScrollPane(noDataArea));
            return;
        }

        // Organize data by date with proper sorting
        TreeMap<Date, java.util.List<JSONObject>> dailyForecasts = new TreeMap<>();
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd-MM");

        for (JSONObject forecast : forecastList) {
            try {
                String dateTime = (String) forecast.get("date");
                Date date = parseFormat.parse(dateTime);
                // Normalize to midnight for grouping by day
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                Date normalizedDate = cal.getTime();

                if (!dailyForecasts.containsKey(normalizedDate)) {
                    dailyForecasts.put(normalizedDate, new ArrayList<JSONObject>());
                }
                dailyForecasts.get(normalizedDate).add(forecast);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Create tabs for each day in chronological order
        int dayCount = 0;
        for (Map.Entry<Date, java.util.List<JSONObject>> entry : dailyForecasts.entrySet()) {
            if (dayCount >= 5) break;

            String formattedDate = displayFormat.format(entry.getKey());
            java.util.List<JSONObject> dayData = entry.getValue();

            // Sort the day's forecasts by time
            dayData.sort(new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject o1, JSONObject o2) {
                    try {
                        Date d1 = parseFormat.parse((String) o1.get("date"));
                        Date d2 = parseFormat.parse((String) o2.get("date"));
                        return d1.compareTo(d2);
                    } catch (Exception e) {
                        return 0;
                    }
                }
            });

            JTextArea dayForecastArea = createTextArea();
            StringBuilder forecastText = new StringBuilder();
            forecastText.append("Date: ").append(formattedDate).append("\n\n");

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            for (JSONObject forecast : dayData) {
                try {
                    Date time = parseFormat.parse((String) forecast.get("date"));
                    forecastText.append(timeFormat.format(time)).append(": ")
                            .append(forecast.get("temperature")).append("°C, ")
                            .append(forecast.get("weather_condition")).append("\n");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            dayForecastArea.setText(forecastText.toString());
            JScrollPane scrollPane = new JScrollPane(dayForecastArea);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());

            tabbedPane.addTab(formattedDate, scrollPane);
            dayCount++;
        }
    }

    private JTextArea createTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setMargin(new Insets(10, 10, 10, 10));
        return textArea;
    }
}