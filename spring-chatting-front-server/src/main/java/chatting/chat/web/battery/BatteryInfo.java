package chatting.chat.web.battery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * BatteryInfo class
 */
@Slf4j
public class BatteryInfo {

    /**
     * Check battery percentage
     * <p> It can be run only in Mac || Linux || Window OS </p>
     * <p> If it isn't one of above OS, return {@code Optional.empty()}</p>
     * @return Optional< String >
     * @throws IOException
     */
    public static Optional<String> getBatteryStatus() throws IOException {
        String osName = System.getProperty("os.name").toLowerCase();
        log.trace("osName: {}", osName);
        int batteryPercentage = 100;


        if (osName.contains("win")) {
            Process process = Runtime.getRuntime().exec("WMIC PATH Win32_Battery Get EstimatedChargeRemaining");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                // 배터리 정보를 파싱하여 배터리 잔량을 추출합니다.
                try {
                    batteryPercentage = Integer.parseInt(line.trim());
                    System.out.println("Battery Percentage: " + batteryPercentage);
                } catch (NumberFormatException e) {
                    // 숫자로 파싱할 수 없는 경우 무시합니다.
                }
            }
        } else if (osName.contains("mac")) {
            Process process = Runtime.getRuntime().exec("pmset -g batt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("InternalBattery")) {
                    // 배터리 정보를 파싱하여 배터리 잔량을 추출합니다.
                    String[] parts = line.split("\\s+");
                    String batteryPercentageString = parts[1];
                    batteryPercentage = Integer.parseInt(batteryPercentageString.replace("%;", ""));
                    System.out.println("Battery Percentage: " + batteryPercentageString);
                }
            }
        } else if (osName.contains("linux")) {
            // read battery capacity

            Process process = Runtime.getRuntime().exec("upower -i /org/freedesktop/UPower/devices/battery_BAT0");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("percentage")) {
                    // 배터리 정보를 파싱하여 배터리 잔량을 추출합니다.
                    String[] parts = line.split(":");
                    String batteryPercentageString = parts[1].trim();
                    batteryPercentage = Integer.parseInt(batteryPercentageString.replace("%", ""));
                    System.out.println("Battery Percentage: " + batteryPercentageString);
                }
            }
        } else {
            log.trace("Your OS " + osName + " is not support");
            return Optional.empty();
        }
        return Optional.of(String.valueOf(batteryPercentage));
    }
}
