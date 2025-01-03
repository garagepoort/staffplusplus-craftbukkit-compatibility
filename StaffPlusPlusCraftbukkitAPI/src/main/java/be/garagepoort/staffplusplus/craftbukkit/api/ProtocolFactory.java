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
        final String versionWithoutSnapshot = version.replaceAll("-SNAPSHOT", "");
        switch (versionWithoutSnapshot) {
            case "1.12.1-R0.1":
            case "1.12.2-R0.1":
                return new Protocol_v1_12_R1();
            case "1.13.1-R0.1":
                return new Protocol_v1_13_R1();
            case "1.13.2-R0.1":
                return new Protocol_v1_13_R2();
            case "1.14.1-R0.1":
            case "1.14.2-R0.1":
                return new Protocol_v1_14_R1();
            case "1.14.3-R0.1":
            case "1.14.4-R0.1":
                return new Protocol_v1_14_R2();
            case "1.15.1-R0.1":
            case "1.15.2-R0.1":
                return new Protocol_v1_15_R1();
            case "1.16.1-R0.1":
                return new Protocol_v1_16_R1();
            case "1.16.3-R0.1":
                return new Protocol_v1_16_R2();
            case "1.16.4-R0.1":
                return new Protocol_v1_16_R3();
            case "1.17-R0.1":
                return new Protocol_v1_17_R0();
            case "1.18-R0.1":
                return new Protocol_v1_18_R0();
            case "1.18.2-R0.1":
                return new Protocol_v1_18_R1();
            case "1.19-R0.1":
                return new Protocol_v1_19_R0();
            case "1.19.1-R0.1":
            case "1.19.2-R0.1":
                return new Protocol_v1_19_R1();
            case "1.19.3-R0.1":
                return new Protocol_v1_19_R2();
            case "1.19.4-R0.1":
                return new Protocol_v1_19_R3();
            case "1.20.1-R0.1":
            case "1.20-R0.1":
                return new Protocol_v1_20_R0();
            case "1.20.2-R0.1":
                return new Protocol_v1_20_R2();
            case "1.20.4-R0.1":
                return new Protocol_v1_20_R3();
            case "1.20.6-R0.1":
                return new Protocol_v1_20_R4();
            case "1.21-R0.1":
            case "1.21.1-R0.1":
                return new Protocol_v1_21_R0();
            case "1.21.2-R0.1":
            case "1.21.3-R0.1":
                return new Protocol_v1_21_R1();
            case "1.21.4-R0.1":
                return new Protocol_v1_21_R2();
            default:
                throw new RuntimeException("No suitable protocol version found for: " + version + ". Are you sure this version of minecraft is supported?");
        }
    }
}
