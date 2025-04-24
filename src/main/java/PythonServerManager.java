import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class PythonServerManager {
    private static Process pythonServerProcess;
    private static boolean started = false;

    public static void start() {
        if (started) return;
        started = true;

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "/usr/local/bin/python3.10", "./python/call.py"
            );
            pb.redirectErrorStream(true);
            try {
                pythonServerProcess = pb.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Log output
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(pythonServerProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[Python Server] " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (pythonServerProcess != null && pythonServerProcess.isAlive()) {
                    pythonServerProcess.destroy();
                    System.out.println("Python server shut down.");
                }
            }));

            Thread.sleep(2000); // wait for server

        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to start Python server", e);
        }
    }

    public static void waitForServer(String url) {
        HttpClient client = HttpClient.newHttpClient();
        int retries = 0;

        while (retries < 20) { // Wait max ~10s
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(1))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    System.out.println("✅ Python server is ready.");
                    return;
                }

            } catch (Exception ignored) {
                // server probably not ready yet
            }

            retries++;
            try {
                Thread.sleep(500); // wait 500ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        throw new RuntimeException("❌ Timeout waiting for Python server to start.");
    }

}
