package org.mooner.sethome;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Home {
    private final int id;
    private final UUID player;
    private final String name;
    private final Location loc;

    public Home(int id, String player, String name, double x, double y, double z, float yaw, float pitch, String world) {
        this.id = id;
        this.player = UUID.fromString(player);
        this.name = name;
        this.loc = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public void teleport(Player p) {
        p.teleport(loc);
    }

    public int getId() {
        return id;
    }

    public Location getLoc() {
        return loc;
    }

    public String getName() {
        return name;
    }

    public UUID getPlayer() {
        return player;
    }
}
