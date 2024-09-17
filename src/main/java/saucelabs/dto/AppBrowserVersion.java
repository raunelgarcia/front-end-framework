package saucelabs.dto;

import lombok.Data;

@Data
public class AppBrowserVersion {
  private String short_version;
  private String long_name;
  private String api_name;
  private String long_version;
  private String os;
}
