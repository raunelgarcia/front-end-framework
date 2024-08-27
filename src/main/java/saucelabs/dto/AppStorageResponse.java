package saucelabs.dto;

import java.util.List;
import lombok.Data;

@Data
public class AppStorageResponse {
  private List<AppStorageItemsResponse> items;
}
