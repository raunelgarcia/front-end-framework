package main_file;

import saucelabs.client.SauceLabsClient;
import saucelabs.service.SauceLabsService;
import utilities.DriverConfiguration;
import utilities.SaucelabsDriverConfiguration;

public class Main {

  public static void main(String[] args) {
    DriverConfiguration.getDriver();
    DriverConfiguration.quitDriver();
  }
}
