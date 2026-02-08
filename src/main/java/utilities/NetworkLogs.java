package utilities;

import static utilities.LocalEnviroment.isWindows;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;

public class NetworkLogs {
  private static final String LOG_FILTER = "okhttp.OkHttpClient";

  public static void getNetworkLogs() {
    if (LocalEnviroment.isAndroid() && !LocalEnviroment.isSaucelabs()) getAndroidLogs();
    // TODO create logs from other platforms
  }

  private static void getAndroidLogs() {
    String[] command =
        isWindows()
            ? new String[] {"cmd.exe", "/c", "adb logcat -d | findstr " + LOG_FILTER}
            : new String[] {"bash", "-c", "adb logcat -d | grep " + LOG_FILTER};

    ProcessBuilder processBuilder = new ProcessBuilder(command);
    processBuilder.redirectErrorStream(true);

    try {
      Process process = processBuilder.start();
      String output;

      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
        output = reader.lines().collect(Collectors.joining(System.lineSeparator()));
      }

      int exitCode = process.waitFor();
      if (exitCode != 0) {
        Logger.errorMessage("Failed to read Android network logs. Exit code: " + exitCode);
      }

      Path logsFolder = Path.of("network-logs");
      Files.createDirectories(logsFolder);

      Path outputPath = logsFolder.resolve("logs_" + System.currentTimeMillis() + ".txt");
      Files.writeString(
          outputPath,
          output,
          StandardCharsets.UTF_8,
          StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING,
          StandardOpenOption.WRITE);

      ExtentReport.attachTextFileToReport(outputPath.toFile());
    } catch (IOException e) {
      Logger.errorMessage("Error getting Android network logs: " + e.getMessage());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      Logger.errorMessage("Thread interrupted while getting Android network logs: " + e.getMessage());
    }
  }

  public static void clearLogs() {
    if (LocalEnviroment.isAndroid())
      JSExecutor.runCommand(Constants.NETWORK_LOG_CLEAN_COMMAND_ANDROID);
    // TODO clear logs from other platforms
  }
}
