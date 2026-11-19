# Weather Application

A Java-based weather application that provides current weather conditions and a 5-day forecast for locations worldwide.

## Features

- **Current Weather Data**:
  - Temperature in Celsius
  - Weather conditions (e.g., sunny, cloudy, rainy)
  - Humidity percentage
  - Wind speed in km/h

- **5-Day Forecast**:
  - Daily temperature ranges
  - Weather conditions for each time period
  - Organized by day in a tabbed interface

- **User Interface**:
  - Dark/light mode toggle
  - Location search with auto-suggestions
  - Responsive design
  - Weather condition icons

- **API Features**:
  - Automatic API key rotation
  - Error handling and retry mechanism
  - Geolocation service integration

## Installation

1. Ensure you have Java JDK 11 or later installed
2. Clone this repository
3. Add the following dependencies to your project:
   - `org.json.simple` (for JSON parsing)
4. Create an `assets` folder in your `src` directory and add weather icons:
   - clear.png
   - cloudy.png
   - rain.png
   - ts.png (thunderstorm)
   - snow.png
   - others.png
   - search.png
   - Darkmode.png
   - humidity.png
   - windspeed.png

## Usage

1. Run the `AppLauncher.java` file
2. Enter a location name in the search field
3. View current weather data or click "5-Day Forecast" for extended predictions
4. Toggle dark mode using the moon/sun icon in the bottom right

## API Keys

The app needs a free [OpenWeatherMap API key](https://home.openweathermap.org/api_keys). No key is included in this repository. Provide yours in either of two ways:

- **Environment variables:** set `OWM_API_KEY` (and, optionally, `OWM_API_KEY_BACKUP`).
- **Config file:** copy `WeatherApp/config.properties.example` to `config.properties` in the same folder (the directory the app runs from in IntelliJ), and fill in your key. `config.properties` is git-ignored, so the key stays on your machine.

If a second key is configured, the app switches to it when the first is rejected or rate-limited.

## Technical Details

- Built with Java Swing for the GUI
- Uses OpenWeatherMap API for weather data
- Uses Open-Meteo Geocoding API for location data
- Implements responsive design principles
- Includes comprehensive error handling

## License

This project is open-source and available under the MIT License.

## Contributing

Contributions are welcome! Please fork the repository and submit pull requests.