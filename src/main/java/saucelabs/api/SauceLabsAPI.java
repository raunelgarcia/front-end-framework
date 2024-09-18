package saucelabs.api;

import jakarta.ws.rs.*;
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
  Response getV1RdcDevices(@HeaderParam("Authorization") String authorization);

  @GET
  @Path("/rest/v1/info/platforms/{automation_api}")
  @Produces(MediaType.APPLICATION_JSON)
  Response getV1InfoPlatformsAutomationApi(
      @HeaderParam("Authorization") String authorization,
      @PathParam("automation_api") String automation_api);
}
