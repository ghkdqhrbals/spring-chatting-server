package chatting.chat.web.global;

import chatting.chat.web.battery.BatteryInfo;
import com.example.commondto.format.DateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;

@Slf4j
public class CommonModel {
    public static void addCommonModel(Model model) {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = currentTime.format(formatter);
        Locale currentLocale = Locale.getDefault();
        String country = currentLocale.getDisplayCountry();

        model.addAttribute("currentTime", formattedTime);
        model.addAttribute("country", country);
    }

}
