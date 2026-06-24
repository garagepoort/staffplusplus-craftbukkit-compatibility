package be.garagepoort.staffplusplus.craftbukkit.api;

import org.bukkit.Bukkit;

import java.lang.ReflectiveOperationException;
import java.lang.reflect.Method;

final class VersionUtil {
    private static final String SERVER_VERSION = parseServerVersion();
    
    public static String getMcVersion() {
        return SERVER_VERSION;
    }
    
    private static String parseServerVersion() {
        // Paper's implementation of Bukkit.getBukkitVersion() returns a different format than spigot
        // this method attempts to use paper API to get the server version if paper is found
        // otherwise fallbacks to parsing Bukkit.getBukkitVersion(), with the assumption that the server is not paper
        
        try {
            Class<?> clazz = Class.forName("io.papermc.paper.ServerBuildInfo");
            
            Method buildInfo = clazz.getMethod("buildInfo");
            Object info = buildInfo.invoke(null); // static
            
            Method minecraftVersionId = clazz.getMethod("minecraftVersionId");
            String version = (String) minecraftVersionId.invoke(info);
            
            return version + "-R0.1";
        } catch (ReflectiveOperationException ignored) {
        }
        
        String version = Bukkit.getBukkitVersion().replace("-SNAPSHOT", "");
        return version;
    }
}