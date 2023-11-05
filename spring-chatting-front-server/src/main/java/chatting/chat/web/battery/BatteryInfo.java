package chatting.chat.web.battery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.stereotype.Component;

public class BatteryInfo {

    public static String getBatteryStatus() throws IOException, InterruptedException {
        String osName = System.getProperty("os.name").toLowerCase();
        String command;

        if (osName.contains("win")) {
            // Windows 운영 체제인 경우
            command = "WMIC PATH Win32_Battery Get EstimatedChargeRemaining";
        } else if (osName.contains("mac")) {
            // MacOS 운영 체제인 경우
            command = "pmset -g batt";
        } else {
            // 다른 운영 체제인 경우 (명령어를 지정해야 함)
            command = "YOUR_CUSTOM_COMMAND_HERE";
        }

        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        StringBuilder output = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        return "Battery Status:\n" + output.toString();
    }
}
