package utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class NetworkLogs {
  public static void getAndroidLogs() {
    try {
      ProcessBuilder processBuilder =
          new ProcessBuilder("cmd.exe", "/c", "adb logcat -d | findstr okhttp.OkHttpClient");
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

      process.waitFor();
      reader.close();

    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
