package com.example.shopuserservice.web.util.server;

import lombok.extern.slf4j.Slf4j;

import java.net.BindException;
import java.net.ServerSocket;

@Slf4j
public class PortChecker {

    /**
     * Checks if the given port is available.
     *
     * @param port the port to check
     * @return {@code true} if available, {@code false} otherwise
     */
    public static boolean isPortAvailable(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // Port is available
            log.trace("Port is available: {}", port);
            return true;
        } catch (BindException e) {
            // Port is in use
            log.error("Port is in use: {}", port);
            return false;
        } catch (Exception e) {
            // Some other exception occurred, might want to handle differently
            log.error("Port check error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Returns an available port.
     * If the given port is available, it returns the same port.
     * Otherwise, it checks the next port until an available one is found.
     *
     * @param port the starting port to check
     * @return an available port
     */
    public static int getAvailablePort(int port) {
        while (!isPortAvailable(port)) {
            port++;
        }
        return port;
    }
}
