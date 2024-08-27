package saucelabs.dto;

import lombok.Data;

@Data
public class ApStorageItemMetadataResponse {
  private String identifier;
  private String name;
  private String version;
  private boolean isSimulator;
  private String minOs;
  private String targetOs;
}
