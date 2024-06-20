package utilities;

import static utilities.LocalEnviroment.isWindows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class NetworkLogs {
  public static void getNetworkLogs() {
    if (LocalEnviroment.isAndroid()) getAndroidLogs();
    // TODO create logs from other platforms
  }

  private static void getAndroidLogs() {
    try {
      String[] command;
      if (isWindows()) {
        command = new String[] {"cmd.exe", "/c", "adb logcat -d | findstr okhttp.OkHttpClient"};
      } else {
        command = new String[] {"bash", "-c", "adb logcat -d | grep okhttp.OkHttpClient"};
      }
      ProcessBuilder processBuilder = new ProcessBuilder(command);
      processBuilder.redirectErrorStream(true);
      Process process = processBuilder.start();

      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      StringBuilder output = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        output.append(line).append("\n");
      }

      File logsFolder = new File("network-logs");
      if (!logsFolder.exists()) {
        logsFolder.mkdir();
      }
      String fileName = "logs_" + System.currentTimeMillis() + ".txt";
      File outputFile = new File(logsFolder, fileName);

      try (FileWriter writer = new FileWriter(outputFile)) {
        writer.write(output.toString());
      } catch (IOException e) {
        e.printStackTrace();
      }

      AllureReport.attachTextFileToAllureReport(outputFile);

      process.waitFor();
      reader.close();

    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static void clearLogs() {
    if (LocalEnviroment.isAndroid())
      JSExecutor.runCommand(Constants.NETWORK_LOG_CLEAN_COMMAND_ANDROID);
    // TODO clear logs from other platforms
  }
}
