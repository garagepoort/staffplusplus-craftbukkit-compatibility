package be.garagepoort.staffplusplus.craftbukkit.api;

import be.garagepoort.staffplusplus.craftbukkit.common.IProtocol;
import net.shortninja.staffplus.server.compatibility.v1_12_R1.Protocol_v1_12_R1;
import net.shortninja.staffplus.server.compatibility.v1_13_R1.Protocol_v1_13_R1;
import net.shortninja.staffplus.server.compatibility.v1_13_R2.Protocol_v1_13_R2;
import net.shortninja.staffplus.server.compatibility.v1_14_R1.Protocol_v1_14_R1;
import net.shortninja.staffplus.server.compatibility.v1_14_R2.Protocol_v1_14_R2;
import net.shortninja.staffplus.server.compatibility.v1_1x.*;
import org.bukkit.Bukkit;

public class ProtocolFactory {

    public static IProtocol getProtocol() {
        final String version = Bukkit.getBukkitVersion();
        switch (version) {
            case "1.12.1-R0.1-SNAPSHOT":
                return new Protocol_v1_12_R1();
            case "1.13.1-R0.1-SNAPSHOT":
                return new Protocol_v1_13_R1();
            case "1.13.2-R0.1-SNAPSHOT":
                return new Protocol_v1_13_R2();
            case "1.14.1-R0.1-SNAPSHOT":
            case "1.14.2-R0.1-SNAPSHOT":
                return new Protocol_v1_14_R1();
            case "1.14.3-R0.1-SNAPSHOT":
            case "1.14.4-R0.1-SNAPSHOT":
                return new Protocol_v1_14_R2();
            case "1.15.1-R0.1-SNAPSHOT":
                return new Protocol_v1_15_R1();
            case "1.16.1-R0.1-SNAPSHOT":
                return new Protocol_v1_16_R1();
            case "1.16.3-R0.1-SNAPSHOT":
                return new Protocol_v1_16_R2();
            case "1.16.4-R0.1-SNAPSHOT":
                return new Protocol_v1_16_R3();
            case "1.17-R0.1-SNAPSHOT":
                return new Protocol_v1_17_R0();
            case "1.18-R0.1-SNAPSHOT":
                return new Protocol_v1_18_R0();
            case "1.18.2-R0.1-SNAPSHOT":
                return new Protocol_v1_18_R1();
            default:
                throw new RuntimeException("No suitable protocol version found for: " + version + ". Are you sure this version of minecraft is supported?");
        }
    }
}
