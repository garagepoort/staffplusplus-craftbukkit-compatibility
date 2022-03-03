package be.garagepoort.staffplusplus.craftbukkit.common.json.rayzr;

import org.bukkit.entity.Player;

public interface JsonSender {
    void send(JSONMessage jsonMessage, Player... players);
}
