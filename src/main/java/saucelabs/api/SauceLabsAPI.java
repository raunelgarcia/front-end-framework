package saucelabs.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public interface SauceLabsAPI {
  @GET
  @Path("/v1/storage/files")
  @Produces(MediaType.APPLICATION_JSON)
  Response getV1StorageFiles(
      @HeaderParam("Authorization") String authorization,
      @QueryParam("q") String appId,
      @QueryParam("kind") String kind,
      @QueryParam("per_page") Integer perPage);

  @GET
  @Path("v1/rdc/devices")
  @Produces(MediaType.APPLICATION_JSON)
  Response getVerifyDeviceExists(@HeaderParam("Authorization") String authorization);
}
