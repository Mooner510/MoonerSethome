package org.mooner.sethome.api;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mooner.sethome.SetHome;

import javax.annotation.Nullable;
import java.sql.*;

import static org.mooner.sethome.MoonerUtils.chat;
import static org.mooner.sethome.api.SetHomeAPI.CONNECTION;

public class TpaAPI {

    public static void back(Player p) {
        final Location home;
        if((home = getBack(p)) != null) {
            backHere(p);
            p.teleport(home);
            p.sendMessage(chat("&a이전 장소로 이동했습니다."));
            p.sendMessage(chat("&b/back&7으로 이전 위치로 돌아갈 수 있습니다."));
        } else {
            p.sendMessage(chat("&c마지막으로 있었던 이전 위치가 없습니다..."));
        }
    }

    public static void backHere(Player p) {
        final Location location = p.getLocation().clone();
        Bukkit.getScheduler().runTaskAsynchronously(SetHome.plugin, () -> {
            try(
                    Connection c = DriverManager.getConnection(CONNECTION);
                    PreparedStatement s2 = c.prepareStatement("UPDATE Back SET x=?, y=?, z=?, yaw=?, pitch=?, world=? where player=?");
                    PreparedStatement s = c.prepareStatement("INSERT INTO Back (player, x, y, z, yaw, pitch, world) VALUES(?, ?, ?, ?, ?, ?, ?)")
            ) {
                s2.setDouble(1, location.getX());
                s2.setDouble(2, location.getY());
                s2.setDouble(3, location.getZ());
                s2.setFloat(4, location.getYaw());
                s2.setFloat(5, location.getPitch());
                s2.setString(6, location.getWorld().getName());
                s2.setString(7, p.getUniqueId().toString());
                if (s2.executeUpdate() == 0) {
                    s.setString(1, p.getUniqueId().toString());
                    s.setDouble(2, location.getX());
                    s.setDouble(3, location.getY());
                    s.setDouble(4, location.getZ());
                    s.setFloat(5, location.getYaw());
                    s.setFloat(6, location.getPitch());
                    s.setString(7, location.getWorld().getName());
                    s.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Nullable
    public static Location getBack(Player p) {
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("SELECT * from Back where player=?")
        ) {
            s.setString(1, p.getUniqueId().toString());
            try (ResultSet r = s.executeQuery()) {
                if (r.next()) {
                    return new Location(Bukkit.getWorld(r.getString("world")), r.getDouble("x"), r.getDouble("y"), r.getDouble("z"), r.getFloat("yaw"), r.getFloat("pitch"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
