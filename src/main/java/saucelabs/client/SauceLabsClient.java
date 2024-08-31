package saucelabs.client;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import java.util.Optional;
import java.util.function.Supplier;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import saucelabs.api.Response;
import saucelabs.api.SauceLabsAPI;
import utilities.Constants;

@Configuration
public class SauceLabsClient {

  @Bean
  public SauceLabsAPI getAPI() {
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(Constants.SAUCELABS_API_URL);
    ResteasyWebTarget rtarget = (ResteasyWebTarget) target;
    return rtarget.proxy(SauceLabsAPI.class);
  }

  /**
   * Calls an API endpoint and returns a typed Response object.
   *
   * @param apiSupplier Supplier that executes the API call
   * @param clazz Optional class to handle entity type
   * @param genericType GenericType for complex types like collections
   * @param <T> The expected type of the response payload
   * @return A Response object wrapping the result of the API call
   */
  public <T> Response<T> call(
      Supplier<jakarta.ws.rs.core.Response> apiSupplier,
      Optional<Class> clazz,
      GenericType<T> genericType) {
    int status = 0;
    try {
      // Execute the API call
      jakarta.ws.rs.core.Response response = executeRestAPI(apiSupplier);
      status = response.getStatus();

      // Check if the response is HTML (for example, an error page)
      if (MediaType.TEXT_HTML.equals(response.getMediaType().toString())) {
        return Response.build(response, Optional.of(String.class));
      } else {
        // Wrap the response with the proper type handling
        return wrapResponse(response, clazz, genericType);
      }

    } catch (Exception e) {
      // Handle error and return a response representing the failure
      return handleErrorResponse(status, e);
    }
  }

  /**
   * Wraps the API response into a Response object depending on the type.
   *
   * @param response The raw API response
   * @param clazz Optional class type for the payload
   * @param genericType GenericType for handling complex payloads
   * @param <T> The expected type of the response payload
   * @return A Response object wrapping the API response
   */
  private <T> Response<T> wrapResponse(
      jakarta.ws.rs.core.Response response, Optional<Class> clazz, GenericType<T> genericType) {
    if (clazz.isPresent()) {
      // If class type is provided, use it to read the entity
      return Response.build(response, clazz);
    } else {
      // If generic type is provided (e.g., for complex types like lists), use it
      return Response.build(response, genericType);
    }
  }

  /**
   * Executes the API call provided by the Supplier.
   *
   * @param apiSupplier The Supplier function that calls the API
   * @return The raw HTTP response
   */
  private jakarta.ws.rs.core.Response executeRestAPI(
      Supplier<jakarta.ws.rs.core.Response> apiSupplier) {
    // Execute the supplier function that represents the API call
    return apiSupplier.get();
  }

  /**
   * Handles the creation of a Response in case of an error during the API call.
   *
   * @param status The HTTP status code (if available)
   * @param e The exception that occurred during the API call
   * @param <T> The expected type of the response payload
   * @return A Response object representing the error
   */
  private <T> Response<T> handleErrorResponse(int status, Exception e) {
    // Log the error (logging mechanism not implemented here, but can be added)
    e.printStackTrace(); // Log the stack trace for debugging

    // Return a response with the status code and error message as payload
    return new Response<>(status, null, (T) ("Error: " + e.getMessage()));
  }
}
