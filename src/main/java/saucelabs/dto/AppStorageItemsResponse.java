package saucelabs.dto;

import lombok.Data;

@Data
public class AppStorageItemsResponse {
  private String id;
  private String name;
  private String kind;
  private AppStorageItemMetadataResponse metadata;
}
