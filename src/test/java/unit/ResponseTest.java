package unit;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.ws.rs.core.GenericType;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import saucelabs.api.Response;

class ResponseTest {

  @Test
  void buildWithOptionalClassShouldReadTypedPayload() {
    jakarta.ws.rs.core.Response jaxRsResponse =
        jakarta.ws.rs.core.Response.status(201).entity("ok").build();

    Response<String> response = Response.build(jaxRsResponse, Optional.of(String.class));

    assertEquals(201, response.getStatus());
    assertEquals("ok", response.getPayload());
    assertNotNull(response.getHeader());
  }

  @Test
  void buildWithEmptyOptionalShouldUseRawEntity() {
    jakarta.ws.rs.core.Response jaxRsResponse =
        jakarta.ws.rs.core.Response.status(200).entity(99).build();

    Response<Integer> response = Response.build(jaxRsResponse, Optional.empty());

    assertEquals(200, response.getStatus());
    assertEquals(99, response.getPayload());
  }

  @Test
  void buildWithGenericTypeShouldReadGenericPayload() {
    jakarta.ws.rs.core.Response jaxRsResponse =
        jakarta.ws.rs.core.Response.status(200).entity(List.of("a", "b")).build();

    Response<List<String>> response =
        Response.build(jaxRsResponse, new GenericType<List<String>>() {});

    assertEquals(200, response.getStatus());
    assertEquals(List.of("a", "b"), response.getPayload());
  }
}
