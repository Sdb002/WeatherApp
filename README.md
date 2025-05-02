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

The application includes two OpenWeatherMap API keys with automatic failover:
- Primary key: `4fb1829475d68b06bfd95d8ae9f138b4`
- Backup key: `9cac7fed4068cbe8d2862b53574c445f`

Note: These are free-tier API keys with usage limits. For production use, consider obtaining your own API keys from [OpenWeatherMap](https://openweathermap.org/).

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
