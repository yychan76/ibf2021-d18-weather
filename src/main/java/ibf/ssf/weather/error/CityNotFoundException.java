package ibf.ssf.weather.error;

public class CityNotFoundException extends RuntimeException {

    public CityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
