package org.mooner.sethome.api;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.mooner.sethome.Home;
import org.mooner.sethome.SetHome;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;

import static org.mooner.sethome.MoonerUtils.chat;
import static org.mooner.sethome.MoonerUtils.loadConfig;
import static org.mooner.sethome.SetHome.dbPath;

public class SetHomeAPI {
    public static final String CONNECTION = "jdbc:sqlite:" + dbPath + "setHome-"+SetHome.serverType+".db";
    private static int maxHomes;
    private static boolean disableHome;

    public static boolean isDisableHome() {
        return disableHome;
    }

    public static void reload() {
        File f = new File(dbPath, "config.yml");
        if(!f.exists()) {
            try {
                f.createNewFile();
                InputStream i = SetHome.plugin.getClass().getResourceAsStream("/config.yml");
                OutputStream o = Files.newOutputStream(f.toPath());

                int length;
                byte[] buffer = new byte[1024];

                while (i != null && (length = i.read(buffer)) > 0) o.write(buffer, 0, length);
                o.flush();
                o.close();
                if(i != null) i.close();
                SetHome.plugin.getLogger().info("성공적으로 config.yml을(를) 생성했습니다.");
            } catch (IOException e) {
                e.printStackTrace();
                SetHome.plugin.getLogger().warning("config.yml을(를) 생성하지 못했습니다.");
            }
        }

        FileConfiguration config = loadConfig(dbPath, "config.yml");
        maxHomes = config.getInt("maxHomes."+SetHome.serverType.getTag(), 5);
        disableHome = config.getBoolean("disableHome."+SetHome.serverType.getTag(), false);
    }

    public static void loadAPI() {
        new File(dbPath).mkdirs();
        File db = new File(dbPath, "setHome-"+SetHome.serverType+".db");
        if(!db.exists()) {
            try {
                db.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS Homes (" +
                                "id INTEGER," +
                                "player TEXT NOT NULL," +
                                "name TEXT NOT NULL," +
                                "x REAL NOT NULL," +
                                "y REAL NOT NULL," +
                                "z REAL NOT NULL," +
                                "yaw REAL NOT NULL," +
                                "pitch REAL NOT NULL," +
                                "world TEXT NOT NULL," +
                                "PRIMARY KEY(id AUTOINCREMENT)" +
                                ")")
        ) {
            s.execute();
            SetHome.plugin.getLogger().info("성공적으로 SetHome DB를 생성했습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS Back (" +
                                "player TEXT NOT NULL," +
                                "x REAL NOT NULL," +
                                "y REAL NOT NULL," +
                                "z REAL NOT NULL," +
                                "yaw REAL NOT NULL," +
                                "pitch REAL NOT NULL," +
                                "world TEXT NOT NULL," +
                                "PRIMARY KEY(player)" +
                                ")")
        ) {
            s.execute();
            SetHome.plugin.getLogger().info("성공적으로 Back DB를 생성했습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addHome(Player p, String name) {
        if (getHomeCount(p) < maxHomes) {
            Location location = p.getLocation().clone();
            Bukkit.getScheduler().runTaskAsynchronously(SetHome.plugin, () -> {
                try(
                        Connection c = DriverManager.getConnection(CONNECTION);
                        PreparedStatement s2 = c.prepareStatement("UPDATE Homes SET x=?, y=?, z=?, yaw=?, pitch=?, world=? where player=? and name=?");
                        PreparedStatement s = c.prepareStatement("INSERT INTO Homes (player, name, x, y, z, yaw, pitch, world) VALUES(?, ?, ?, ?, ?, ?, ?, ?)")
                ) {
                    s2.setDouble(1, location.getX());
                    s2.setDouble(2, location.getY());
                    s2.setDouble(3, location.getZ());
                    s2.setFloat(4, location.getYaw());
                    s2.setFloat(5, location.getPitch());
                    s2.setString(6, location.getWorld().getName());
                    s2.setString(7, p.getUniqueId().toString());
                    s2.setString(8, name);
                    if(s2.executeUpdate() == 0) {
                        s.setString(1, p.getUniqueId().toString());
                        s.setString(2, name);
                        s.setDouble(3, location.getX());
                        s.setDouble(4, location.getY());
                        s.setDouble(5, location.getZ());
                        s.setFloat(6, location.getYaw());
                        s.setFloat(7, location.getPitch());
                        s.setString(8, location.getWorld().getName());
                        s.executeUpdate();
                    }
                    p.sendMessage(chat("&a현재 위치를 &6" + name + " &a이라는 홈으로 설정했습니다."));
                    p.sendMessage(chat("&b/home " + name + "&a을 사용하여 여기로 이동할 수 있습니다."));
                } catch (SQLException e) {
                    e.printStackTrace();
                    p.sendMessage(chat("&c홈을 생성하는 도중 오류가 발생했습니다. 어드민에게 신고해 주세요. &4(" + e.getMessage() + ")"));
                }
            });
        } else {
            p.sendMessage(chat("&c홈은 최대 2개까지 지정할 수 있습니다."));
        }
    }

    public static void removeHome(Player p, String name) {
        if(getHome(p, name) != null) {
            try (
                    Connection c = DriverManager.getConnection(CONNECTION);
                    PreparedStatement s = c.prepareStatement("DELETE From Homes where player=? and name=?")
            ) {
                s.setString(1, p.getUniqueId().toString());
                s.setString(2, name);
                s.execute();
                p.sendMessage(chat("&6" + name + "&a을(를) 제거했습니다."));
//            p.sendMessage(chat("&e" + s.getString("world") + "&7월드의 &e" + Math.round(s.getDouble("x")) + ", " + Math.round(s.getDouble("y")) + ", " + Math.round(s.getDouble("z")) + "&7좌표에 있던 홈이었습니다."));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            p.sendMessage(chat("&6" + name + "&c은(는) 존재하지 않습니다..."));
        }
    }

    public static int getHomeCount(Player p) {
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("SELECT COUNT(*) from Homes where player=?")
        ) {
            s.setString(1, p.getUniqueId().toString());
            try (ResultSet r = s.executeQuery()) {
                if (r.next()) {
                    return r.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static ArrayList<Home> getHomes(Player p) {
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("SELECT * from Homes where player=?")
        ) {
            final String player = p.getUniqueId().toString();
            s.setString(1, player);
            try (ResultSet r = s.executeQuery()) {
                ArrayList<Home> homes = new ArrayList<>();
                while (r.next()) {
                    homes.add(new Home(r.getInt("id"), player, r.getString("name"), r.getDouble("x"), r.getDouble("y"), r.getDouble("z"), r.getFloat("yaw"), r.getFloat("pitch"), r.getString("world")));
                }
                return homes;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Nullable
    public static Home getHome(Player p, String name) {
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("SELECT * from Homes where player=? and name=?")
        ) {
            final String player = p.getUniqueId().toString();
            s.setString(1, player);
            s.setString(2, name);
            try (ResultSet r = s.executeQuery()) {
                if (r.next()) {
                    return new Home(r.getInt("id"), player, name, r.getDouble("x"), r.getDouble("y"), r.getDouble("z"), r.getFloat("yaw"), r.getFloat("pitch"), r.getString("world"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void teleportHome(Player p, String name) {
        final Home home;
        if((home = getHome(p, name)) != null) {
            TpaAPI.backHere(p);
            home.teleport(p);
            p.sendMessage(chat("&6" + name + "&a(으)로 이동했습니다."));
            p.sendMessage(chat("&b/back&7으로 이전 위치로 돌아갈 수 있습니다."));
        } else {
            p.sendMessage(chat("&6" + name + "&c은(는) 존재하지 않습니다..."));
        }
    }

}
