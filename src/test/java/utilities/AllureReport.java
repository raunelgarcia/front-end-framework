package utilities;

import io.qameta.allure.Allure;

public class AllureReport {

  public static void fillReportInfo() {
    Allure.addDescription("Browser: " + LocalEnviroment.getBrowser());
  }
}
