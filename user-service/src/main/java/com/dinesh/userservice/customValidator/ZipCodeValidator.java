package com.dinesh.userservice.customValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class ZipCodeValidator implements ConstraintValidator<ZipCode, String> {

    private static final Logger LOG = LoggerFactory.getLogger(ZipCodeValidator.class);

    @Value("${MAPBOX_TOKEN}")
    private String accessToken;

    /**
     * Validates a given zip code by making a HTTP GET request to an external API.
     * Returns true if the zip code is valid and false otherwise.
     *
     * @param zipCode The zip code to be validated.
     * @param constraintValidatorContext The context object for the validation.
     * @return true if the zip code is valid, false otherwise.
     */
    @Override
    public boolean isValid(String zipCode, ConstraintValidatorContext constraintValidatorContext) {
        return validateZipCode(zipCode);
    }

    /**
     * Validates a given zip code by making an HTTP GET request to an external API.
     *
     * @param zipCode The zip code to be validated.
     * @return true if the zip code is valid (response code is HTTP_OK), false otherwise.
     */
    private boolean validateZipCode(String zipCode) {
        if(zipCode == null){
            return true;
        }
        OkHttpClient httpClient = new OkHttpClient();
        String apiUrl = "https://api.mapbox.com/geocoding/v5/mapbox.places/"+zipCode+".json?country=us&access_token="+accessToken;
        Request request = new Request.Builder().url(apiUrl).build();
        try (Response response = httpClient.newCall(request).execute()) {
            String jsonResponse = response.body().string();
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray features = jsonObject.getJSONArray("features");
            if (features.length() == 0) {
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            LOG.info(e.getMessage());
            return false;
        }
    }
}
