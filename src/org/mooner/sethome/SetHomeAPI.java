package org.mooner.sethome;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

import static org.mooner.sethome.Main.dataPath;
import static org.mooner.sethome.MoonerUtils.chat;

public class SetHomeAPI {
    public static final String CONNECTION = "jdbc:sqlite:" + dataPath + "DB/setHome.db";

    public static void loadAPI() {
        new File(dataPath+"DB/").mkdirs();
        File db = new File(dataPath + "DB/", "setHome.db");
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
            Main.plugin.getLogger().info("성공적으로 SetHome DB를 생성했습니다.");
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
            Main.plugin.getLogger().info("성공적으로 Back DB를 생성했습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addHome(Player p, String name) {
        if (getHomeCount(p) < 2) {
            Location location = p.getLocation().clone();
            Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, () -> {
                try(
                        Connection c = DriverManager.getConnection(CONNECTION);
                        PreparedStatement s = c.prepareStatement("INSERT INTO Homes (player, name, x, y, z, yaw, pitch, world) VALUES(?, ?, ?, ?, ?, ?, ?, ?)")
                ) {
                    s.setString(1, p.getUniqueId().toString());
                    s.setString(2, name);
                    s.setDouble(3, location.getX());
                    s.setDouble(4, location.getY());
                    s.setDouble(5, location.getZ());
                    s.setFloat(6, location.getYaw());
                    s.setFloat(7, location.getPitch());
                    s.setString(8, location.getWorld().getName());
                    s.executeUpdate();
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
                p.sendMessage(chat("&6" + name + " &a 홈을 제거했습니다."));
//            p.sendMessage(chat("&e" + s.getString("world") + "&7월드의 &e" + Math.round(s.getDouble("x")) + ", " + Math.round(s.getDouble("y")) + ", " + Math.round(s.getDouble("z")) + "&7좌표에 있던 홈이었습니다."));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            p.sendMessage(chat("&c홈 &6" + name + "&c은(는) 존재하지 않습니다..."));
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
            backHere(p);
            home.teleport(p);
            p.sendMessage(chat("&a홈 &6" + name + "&a으로 이동했습니다."));
            p.sendMessage(chat("&b/back&7으로 이전 위치로 돌아갈 수 있습니다."));
        } else {
            p.sendMessage(chat("&c홈 &6" + name + "&c은(는) 존재하지 않습니다..."));
        }
    }

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
        Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, () -> {
            try(
                    Connection c = DriverManager.getConnection(CONNECTION);
                    PreparedStatement s = c.prepareStatement("INSERT INTO Back (player, x, y, z, yaw, pitch, world) VALUES(?, ?, ?, ?, ?, ?, ?)")
            ) {
                s.setString(1, p.getUniqueId().toString());
                s.setDouble(2, location.getX());
                s.setDouble(3, location.getY());
                s.setDouble(4, location.getZ());
                s.setFloat(5, location.getYaw());
                s.setFloat(6, location.getPitch());
                s.setString(7, location.getWorld().getName());
                s.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Nullable
    public static Location getBack(Player p) {
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("SELECT * from Homes where player=?")
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
