package be.garagepoort.staffplusplus.craftbukkit.api.exceptions;

public class UnsupportedVersionException extends RuntimeException {
    
    public UnsupportedVersionException(String version) {
        super("Unsupported minecraft protocol version " + version + ", please check if an update is available");
    }
    
    public UnsupportedVersionException(String version, Throwable e) {
        super("Unsupported minecraft protocol version " + version + ", please check if an update is available", e);
    }
}