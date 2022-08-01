package be.garagepoort.staffplusplus.craftbukkit.api;

import be.garagepoort.staffplusplus.craftbukkit.common.json.rayzr.JsonSender;
import net.shortninja.staffplus.server.compatibility.v1_1x.JsonSender_v1_17_R0;
import net.shortninja.staffplus.server.compatibility.v1_1x.JsonSender_v1_18_R0;
import net.shortninja.staffplus.server.compatibility.v1_1x.JsonSender_v1_18_R1;
import net.shortninja.staffplus.server.compatibility.v1_1x.JsonSender_v1_19_R0;
import net.shortninja.staffplus.server.compatibility.v1_1x.JsonSender_v1_19_R1;
import org.bukkit.Bukkit;

public class JsonSenderFactory {

    public static JsonSender getSender() {
        final String version = Bukkit.getBukkitVersion();
        switch (version) {
            case "1.17-R0.1-SNAPSHOT":
                return new JsonSender_v1_17_R0();
            case "1.18-R0.1-SNAPSHOT":
                return new JsonSender_v1_18_R0();
            case "1.18.2-R0.1-SNAPSHOT":
                return new JsonSender_v1_18_R1();
            case "1.19-R0.1-SNAPSHOT":
                return new JsonSender_v1_19_R0();
            case "1.19.1-R0.1-SNAPSHOT":
                return new JsonSender_v1_19_R1();
            default:
                throw new RuntimeException("No suitable protocol version found for: " + version + ". Are you sure this version of minecraft is supported?");
        }
    }
}
