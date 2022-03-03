package be.garagepoort.staffplusplus.craftbukkit.api;

import be.garagepoort.staffplusplus.craftbukkit.common.IProtocol;
import be.garagepoort.staffplusplus.craftbukkit.common.json.rayzr.JsonSender;
import net.shortninja.staffplus.server.compatibility.v1_12_R1.Protocol_v1_12_R1;
import net.shortninja.staffplus.server.compatibility.v1_13_R1.Protocol_v1_13_R1;
import net.shortninja.staffplus.server.compatibility.v1_13_R2.Protocol_v1_13_R2;
import net.shortninja.staffplus.server.compatibility.v1_14_R1.Protocol_v1_14_R1;
import net.shortninja.staffplus.server.compatibility.v1_14_R2.Protocol_v1_14_R2;
import net.shortninja.staffplus.server.compatibility.v1_1x.JsonSender_v1_17_R0;
import net.shortninja.staffplus.server.compatibility.v1_1x.JsonSender_v1_18_R0;
import net.shortninja.staffplus.server.compatibility.v1_1x.JsonSender_v1_18_R1;
import net.shortninja.staffplus.server.compatibility.v1_1x.Protocol_v1_15_R1;
import net.shortninja.staffplus.server.compatibility.v1_1x.Protocol_v1_16_R1;
import net.shortninja.staffplus.server.compatibility.v1_1x.Protocol_v1_16_R2;
import net.shortninja.staffplus.server.compatibility.v1_1x.Protocol_v1_16_R3;
import net.shortninja.staffplus.server.compatibility.v1_1x.Protocol_v1_17_R0;
import net.shortninja.staffplus.server.compatibility.v1_1x.Protocol_v1_18_R0;
import net.shortninja.staffplus.server.compatibility.v1_1x.Protocol_v1_18_R1;
import org.bukkit.Bukkit;

public class JsonSenderFactory {

    public static JsonSender getSender() {
        final String version = Bukkit.getServer().getClass().getPackage().getName();
        final String formattedVersion = version.substring(version.lastIndexOf('.') + 1);
        switch (formattedVersion) {
            case "v1_17_R1":
                return new JsonSender_v1_17_R0();
            case "v1_18_R1":
                return new JsonSender_v1_18_R0();
            case "v1_18_R2":
                return new JsonSender_v1_18_R1();
            default:
                throw new RuntimeException("No suitable jsonsender version found. Are you sure this version of minecraft is supported?");
        }
    }
}
