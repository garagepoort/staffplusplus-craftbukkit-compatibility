package be.garagepoort.staffplusplus.craftbukkit.api;

import be.garagepoort.staffplusplus.craftbukkit.common.json.rayzr.JsonSender;
import net.shortninja.staffplus.server.compatibility.v1_1x.*;
import org.bukkit.Bukkit;

public class JsonSenderFactory {

    public static JsonSender getSender() {
        final String version = Bukkit.getBukkitVersion();
        final String versionWithoutSnapshot = version.replaceAll("-SNAPSHOT", "");

        switch (versionWithoutSnapshot) {
            case "1.17-R0.1":
                return new JsonSender_v1_17_R0();
            case "1.18-R0.1":
                return new JsonSender_v1_18_R0();
            case "1.18.2-R0.1":
                return new JsonSender_v1_18_R1();
            case "1.19-R0.1":
                return new JsonSender_v1_19_R0();
            case "1.19.1-R0.1":
            case "1.19.2-R0.1":
                return new JsonSender_v1_19_R1();
            case "1.19.3-R0.1":
                return new JsonSender_v1_19_R2();
            case "1.19.4-R0.1":
                return new JsonSender_v1_19_R3();
            case "1.20.1-R0.1":
            case "1.20-R0.1":
                return new JsonSender_v1_20_R0();
            case "1.20.2-R0.1":
                return new JsonSender_v1_20_R2();
            case "1.20.4-R0.1":
                return new JsonSender_v1_20_R3();
            case "1.20.6-R0.1":
                return new JsonSender_v1_20_R4();
            default:
                throw new RuntimeException("No suitable protocol version found for: " + version + ". Are you sure this version of minecraft is supported?");
        }
    }
}
