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
        final String version = Bukkit.getServer().getClass().getPackage().getName();
        final String formattedVersion = version.substring(version.lastIndexOf('.') + 1);
        switch (formattedVersion) {
            case "v1_12_R1":
                return new Protocol_v1_12_R1();
            case "v1_13_R1":
                return new Protocol_v1_13_R1();
            case "v1_13_R2":
                return new Protocol_v1_13_R2();
            case "v1_14_R1":
            case "v1_14_R2":
                return new Protocol_v1_14_R1();
            case "v1_14_R3":
            case "v1_14_R4":
                return new Protocol_v1_14_R2();
            case "v1_15_R1":
                return new Protocol_v1_15_R1();
            case "v1_16_R1":
                return new Protocol_v1_16_R1();
            case "v1_16_R2":
                return new Protocol_v1_16_R2();
            case "v1_16_R3":
                return new Protocol_v1_16_R3();
            case "v1_16_R4":
                return new Protocol_v1_16_R4();
            default:
                throw new RuntimeException("No suitable protocal version found. Are you sure this version of minecraft is supported?");
        }
    }
}
