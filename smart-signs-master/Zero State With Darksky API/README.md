# Dark Sky Android sample
This is a sample application based on [Weather Delta](https://play.google.com/store/apps/details?id=com.felkertech.n.weatherdelta&hl=en).

A lot of code has been cleaned up since there was a lot of weird UI coupling. More specifically,
this project is a public sample that shows how to use the [DarkSky API](https://darksky.net/dev) on
Android.

## Setup
1. Get [an API Key](https://darksky.net/dev) for DarkSky
    * Note: Make sure you do NOT commit private keys to public repositories
1. Import this project into Android Studio and sync with Gradle
1. Replace the `DARKSKY_API_KEY` in `WeatherSync.java` with your key
1. In your `MainActivity`, instantiate a new `WeatherSync` object and use one of the methods to get your weather data. Get the endpoint with `getUrl`
    * `getUrl(double latitude, double longitude)`
    * `getUrl(double latitude, double longitude, Calendar lookupDate)`
1. Download the data
    * This app has a caching system. You can call several different methods.
    * `startForcedDownload(String url)`
    * `startLazyDownload(String url)` may use cached results
    * There's also `startCachedDownload()`
1. At the end, you will get a callback from the `DownloadManager` possibly with results

This project also has helpful utilities like `Conversions` to handle metric/imperial units and `SettingsManager` to manage a variety of user-specified options.

**Note** This sample does not show how to get the user's location.

## License
This is licensed under the MIT License