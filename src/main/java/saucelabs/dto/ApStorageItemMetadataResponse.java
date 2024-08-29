package saucelabs.dto;

import lombok.Data;

@Data
public class ApStorageItemMetadataResponse {
  private String identifier;
  private String name;
  private String version;
  private String short_version;
  private Boolean is_simulator;
  private String min_os;
}
