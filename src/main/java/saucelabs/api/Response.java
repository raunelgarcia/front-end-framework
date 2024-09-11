package saucelabs.api;

import jakarta.ws.rs.core.GenericType;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 *
 * <pre>
 *     Response represent an HTTP response with status code, headers, and payload.
 * </pre>
 *
 * @param <K> the payload
 */
public class Response<K> {

  private final int status;

  private final Map<String, List<String>> header;

  private K payload;

  public Response(int status, Map<String, List<String>> header) {
    this.status = status;
    this.header = header;
  }

  public Response(int status, Map<String, List<String>> header, K payload) {
    this(status, header);
    this.payload = payload;
  }

  public static <K> Response<K> build(jakarta.ws.rs.core.Response response, Optional<Class> clazz) {
    if (clazz.isPresent()) {
      return new Response(
          response.getStatus(), response.getHeaders(), response.readEntity(clazz.get()));
    } else {
      return new Response(response.getStatus(), response.getHeaders(), response.getEntity());
    }
  }

  public static <K> Response<K> build(
      jakarta.ws.rs.core.Response response, GenericType<K> genericType) {
    return new Response(
        response.getStatus(), response.getHeaders(), response.readEntity(genericType));
  }

  public int getStatus() {
    return status;
  }

  public Map<String, List<String>> getHeader() {
    return header;
  }

  public K getPayload() {
    return payload;
  }


}
