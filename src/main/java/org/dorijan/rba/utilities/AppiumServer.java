package org.dorijan.rba.utilities;

import java.io.IOException;


public class AppiumServer {

    private Process appiumProcess;

    /**
     * Starts a local Appium Server using the system-installed "appium" command.
     * Runs with chromedriver autodownload enabled.
     */
    public void start() {
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/c",
                    "appium --allow-insecure=\"*:chromedriver_autodownload\""
            );

            // Hide Appium server output from terminal
            builder.redirectOutput(ProcessBuilder.Redirect.to(new java.io.File("NUL")));
            builder.redirectError(ProcessBuilder.Redirect.to(new java.io.File("NUL")));

            appiumProcess = builder.start();

            // Wait for the server port to open
            waitForPort(4723, 15000);

        } catch (Exception e) {
            throw new RuntimeException("Failed to start Appium server", e);
        }
    }

    /**
     * Stops the Appium Server process.
     */
    public void stop() {
        if (appiumProcess != null) {
            appiumProcess.destroy();
        }
    }

    private void waitForPort(int port, int timeoutMs) throws Exception {
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < timeoutMs) {
            try {
                new java.net.Socket("127.0.0.1", port).close();
                return; // port is open
            } catch (IOException ignored) {
                Thread.sleep(300);
            }
        }
        throw new RuntimeException("Appium server did not open port " + port + " in time");
    }
}
