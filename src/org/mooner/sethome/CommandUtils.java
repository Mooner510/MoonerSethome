package org.mooner.sethome;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.mooner.sethome.MoonerUtils.*;
import static org.mooner.sethome.main.*;

public class CommandUtils {
    public static void addHome(Player p, String name) {
        config = loadConfig(dataPath, "homes.yml");
        if (homes(p) < 2) {
            if (name.contains(".")) {
                p.sendMessage(chat("&c홈 이름에는 . 문자를 입력할 수 없습니다."));
            } else if (name.equalsIgnoreCase("__backsInfo__") || name.equalsIgnoreCase("__deathInfo__")) {
                p.sendMessage(chat("&c해당 이름으로는 홈을 설정할 수 없습니다."));
            } else {
                config.set(p.getUniqueId() + "." + name + ".world", p.getWorld().getName());
                config.set(p.getUniqueId() + "." + name + ".x", p.getLocation().getX());
                config.set(p.getUniqueId() + "." + name + ".y", p.getLocation().getY());
                config.set(p.getUniqueId() + "." + name + ".z", p.getLocation().getZ());
                config.set(p.getUniqueId() + "." + name + ".yaw", p.getLocation().getYaw());
                config.set(p.getUniqueId() + "." + name + ".pitch", p.getLocation().getPitch());
                config.set(p.getUniqueId() + "." + name + ".timestamp", getTime());
                p.sendMessage(chat("&a현재 위치를 &6" + name + " &a이라는 홈으로 설정했습니다."));
                p.sendMessage(chat("&b/home " + name + "&a을 사용하여 여기로 이동할 수 있습니다."));
            }
            try {
                config.save(new File(dataPath, "homes.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            p.sendMessage(chat("&c홈은 최대 2개까지 지정할 수 있습니다."));
        }
    }

    public static void removeHome(Player p, String name) {
        config = loadConfig(dataPath, "homes.yml");
        if (config.isSet(p.getUniqueId() + "." + name)) {
            ConfigurationSection s = config.getConfigurationSection(p.getUniqueId() + "." + name);
            p.sendMessage(chat("&6" + name + " &a 홈을 제거했습니다."));
            p.sendMessage(chat("&e" + s.getString("world") + "&7월드의 &e" + Math.round(s.getDouble("x")) + ", " + Math.round(s.getDouble("y")) + ", " + Math.round(s.getDouble("z")) + "&7좌표에 있던 홈이었습니다."));
            config.set(p.getUniqueId() + "." + name, null);
            try {
                config.save(new File(dataPath, "homes.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            p.sendMessage(chat("&c홈 &6" + name + "&c은(는) 존재하지 않습니다."));
        }
    }

    public static int homes(Player p) {
        if(!config.isSet(p.getUniqueId().toString())) return 0;
        int a = config.getConfigurationSection(p.getUniqueId().toString()).getKeys(false).size();
        if(config.isSet(p.getUniqueId() + ".__backsInfo__")) a--;
        if(config.isSet(p.getUniqueId() + ".__deathInfo__")) a--;
        return a;
    }

    public static ArrayList<String> homeList(Player p) {
        if(!config.isSet(p.getUniqueId().toString())) return new ArrayList<>();
        ArrayList<String> homes = new ArrayList<>(config.getConfigurationSection(p.getUniqueId().toString()).getKeys(false));
        homes.remove("__backsInfo__");
        homes.remove("__deathInfo__");
        return homes;
    }

    public static void teleportHome(Player p, String name) {
        if (name.equalsIgnoreCase("__backsInfo__") || name.equalsIgnoreCase("__deathInfo__")) {
            p.sendMessage(chat("&c해당 이름의 홈으로는 이동할 수 없습니다."));
        } else if (config.isSet(p.getUniqueId() + "." + name)) {
            ConfigurationSection s = config.getConfigurationSection(p.getUniqueId() + "." + name);
            Location loc = new Location(
                    Bukkit.getWorld(s.getString("world")), s.getDouble("x"), s.getDouble("y"), s.getDouble("z"),
                    (float) s.getDouble("yaw"), (float) s.getDouble("pitch")
            );
            backHere(p);
            p.teleport(loc);
            p.sendMessage(chat("&a홈 &6" + name + "&a으로 이동했습니다."));
            p.sendMessage(chat("&b/back&7으로 이전 위치로 돌아갈 수 있습니다."));
        } else {
            p.sendMessage(chat("&c홈 &6" + name + "&c은(는) 존재하지 않습니다."));
        }
    }

    public static void back(Player p) {
        if (config.isSet(p.getUniqueId() + ".__backsInfo__")) {
            ConfigurationSection s = config.getConfigurationSection(p.getUniqueId() + ".__backsInfo__");
            Location loc = new Location(
                    Bukkit.getWorld(s.getString("world")), s.getDouble("x"), s.getDouble("y"), s.getDouble("z"),
                    (float) s.getDouble("yaw"), (float) s.getDouble("pitch")
            );
            backHere(p);
            p.teleport(loc);
            p.sendMessage(chat("&a이전 장소로 이동했습니다."));
            p.sendMessage(chat("&b/back&7으로 이전 위치로 돌아갈 수 있습니다."));
        } else {
            p.sendMessage(chat("&c마지막으로 있었던 이전 위치가 없습니다."));
        }
    }

    public static void backHere(Player p) {
        config.set(p.getUniqueId() + ".__backsInfo__.world", p.getWorld().getName());
        config.set(p.getUniqueId() + ".__backsInfo__.x", p.getLocation().getX());
        config.set(p.getUniqueId() + ".__backsInfo__.y", p.getLocation().getY());
        config.set(p.getUniqueId() + ".__backsInfo__.z", p.getLocation().getZ());
        config.set(p.getUniqueId() + ".__backsInfo__.yaw", p.getLocation().getYaw());
        config.set(p.getUniqueId() + ".__backsInfo__.pitch", p.getLocation().getPitch());
        config.set(p.getUniqueId() + ".__backsInfo__.timestamp", getTime());
        try {
            config.save(new File(dataPath, "homes.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void death(Player p) {
        if (config.isSet(p.getUniqueId() + ".__deathInfo__")) {
            ConfigurationSection s = config.getConfigurationSection(p.getUniqueId() + ".__deathInfo__");
            Location loc = new Location(
                    Bukkit.getWorld(s.getString("world")), s.getDouble("x"), s.getDouble("y"), s.getDouble("z"),
                    (float) s.getDouble("yaw"), (float) s.getDouble("pitch")
            );
            p.teleport(loc);
            removeDeath(p);
            p.sendMessage(chat("&a마지막으로 죽은 장소로 이동했습니다."));
            p.sendMessage(chat("&e다시 죽기 전까지 이 명령어는 사용할 수 없습니다."));
        } else {
            p.sendMessage(chat("&c다시 죽기 전까지 이 명령어를 사용할 수 없습니다."));
        }
    }

    public static void deathHere(Player p) {
        config.set(p.getUniqueId() + ".__deathInfo__.world", p.getWorld().getName());
        config.set(p.getUniqueId() + ".__deathInfo__.x", p.getLocation().getX());
        config.set(p.getUniqueId() + ".__deathInfo__.y", p.getLocation().getY());
        config.set(p.getUniqueId() + ".__deathInfo__.z", p.getLocation().getZ());
        config.set(p.getUniqueId() + ".__deathInfo__.yaw", p.getLocation().getYaw());
        config.set(p.getUniqueId() + ".__deathInfo__.pitch", p.getLocation().getPitch());
        config.set(p.getUniqueId() + ".__deathInfo__.timestamp", getTime());
        try {
            config.save(new File(dataPath, "homes.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeDeath(Player p) {
        config.set(p.getUniqueId() + ".__deathInfo__", null);
        try {
            config.save(new File(dataPath, "homes.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean runCommand(CommandSender sender, Command cmd, String[] arg) {
        if(!(sender instanceof Player)) return false;
        Player p = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("sethome")) {
            if(arg.length == 0) {
                p.sendMessage(chat("&6사용법: &c/sethome <홈이름>"));
            } else if(arg.length == 1) {
                addHome(p, arg[0]);
            } else {
                p.sendMessage(chat("&c홈 이름에는 띄어쓰기를 할 수 없습니다."));
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("home")) {
            if(arg.length == 0) {
                p.sendMessage(chat("&b" + p.getName() + "&6님의 홈 목록 &7(" + homes(p) + "/2)&8:"));
                if(homes(p) <= 0) {
                    p.sendMessage(chat(" &c 설정된 홈이 없습니다."));
                } else {
                    for (String key : config.getConfigurationSection(p.getUniqueId().toString()).getKeys(false)) {
                        p.sendMessage(chat(" &8- &e" + key + " &d(월드: " + config.getString(p.getUniqueId() + "." + key + ".world") + ")"));
                    }
                }
            } else if(arg.length == 1) {
                teleportHome(p, arg[0]);
            } else {
                p.sendMessage(chat("&c홈 이름에는 띄어쓰기를 할 수 없습니다."));
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("back")) {
            back(p);
            return true;
        } else if (cmd.getName().equalsIgnoreCase("revive")) {
            death(p);
            return true;
        } else if (cmd.getName().equalsIgnoreCase("delhome") || cmd.getName().equalsIgnoreCase("removehome")) {
            if (arg.length == 0) {
                p.sendMessage(chat("&b" + p.getName() + "&6님의 홈 목록 &7(" + homes(p) + "/2)&8:"));
                if (homes(p) <= 0) {
                    p.sendMessage(chat(" &c 설정된 홈이 없습니다."));
                } else {
                    for (String key : config.getConfigurationSection(p.getUniqueId().toString()).getKeys(false)) {
                        p.sendMessage(chat(" &8- &e" + key + " &d(월드: " + config.getString(p.getUniqueId() + "." + key + ".world") + ")"));
                    }
                }
            } else if (arg.length == 1) {
                removeHome(p, arg[0]);
            } else {
                p.sendMessage(chat("&c홈 이름에는 띄어쓰기를 할 수 없습니다."));
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("w") || cmd.getName().equalsIgnoreCase("tell") || cmd.getName().equalsIgnoreCase("msg")) {
            if(arg.length == 0) {
                p.sendMessage(chat("&c플레이어를 입력해 주세요."));
                p.sendMessage(chat("&6사용법: &7/" + cmd.getName().toLowerCase() + " <플레이어> <메시지>"));
            } else if(arg[0].equalsIgnoreCase(p.getName())) {
                p.sendMessage(chat("&6자기 자신에게 메시지를 보내려 하지 마세요!"));
            } else if(arg.length == 1) {
                p.sendMessage(chat("&c메시지를 입력해 주세요."));
                p.sendMessage(chat("&6사용법: &7/" + cmd.getName().toLowerCase() + " <플레이어> <메시지>"));
            } else {
                String message = String.join(" ", Arrays.asList(arg).subList(1, arg.length));
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if(player.getName().equalsIgnoreCase(arg[0])) {
                        p.sendMessage(chat("&dto &7" + player.getName() + ": ") + message);
                        player.sendMessage(chat("&dfrom &7" + p.getName() + ": ") + message);
                        if(!whisper.containsKey(player.getUniqueId())) {
                            player.sendMessage(chat("&b/r <메시지>&7로 마지막으로 대화해준 상대에게 바로 메시지를 보낼 수 있습니다."));
                            player.sendMessage(chat("&7이 메시지는 서버 시작 후, 한 번만 보내집니다."));
                        }
                        whisper.put(player.getUniqueId(), p.getUniqueId());
                        return true;
                    }
                }
                p.sendMessage(chat("&6" + arg[0] + "&c님은 온라인이 아니거나 서버에 접속한 적이 없습니다."));
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("r")) {
            if(whisper.containsKey(p.getUniqueId())) {
                String message = String.join(" ", arg);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if(player.getUniqueId().equals(whisper.get(p.getUniqueId()))) {
                        p.performCommand("w " + player.getName() + " " + message);
                        return true;
                    }
                }
                p.sendMessage(chat("&6" + arg[0] + "&c님은 온라인이 아니거나 서버에 접속한 적이 없습니다."));
            } else {
                p.sendMessage(chat("&c마지막으로 대화해준 상대가 없습니다."));
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("tpa")) {
            if(!tpa.containsKey(p.getUniqueId()) || (long) tpa.get(p.getUniqueId())[1] < getTime()) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if(player.getName().equalsIgnoreCase(arg[0])) {
                        p.sendMessage(chat("&b[TPA] &6" + player.getName() + "&a님에게 TPA를 보냈습니다."));
                        player.sendMessage(chat("&b[TPA] &b/tpcancel&a로 이 TPA를 취소할 수 있습니다."));
                        p.sendMessage(chat("&b[TPA] &660초 &a후에 알림 없이 자동으로 취소됩니다."));
                        player.sendMessage(chat("&b[TPA] &6" + p.getName() + "&a님이 TPA를 보냈습니다."));
                        player.sendMessage(chat("&b[TPA] &b/tpaccept&a로 이 TPA를 수락할 수 있습니다."));
                        player.sendMessage(chat("&b[TPA] &660초 &a후에 알림 없이 자동으로 취소됩니다."));
                        tpa.put(p.getUniqueId(), new Object[]{player.getUniqueId(), getTime() + 60000});
                        return true;
                    }
                }
                p.sendMessage(chat("&6" + arg[0] + "&c님은 온라인이 아니거나 서버에 접속한 적이 없습니다."));
            } else {
                p.sendMessage(chat("&b[TPA] &c이미 누군가에게 TPA를 보냈습니다. 취소하시려면 /tpcancel을 입력하세요."));
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("tpaccept")) {
            for (UUID uuid : tpa.keySet()) {
                if((long) tpa.get(uuid)[1] >= getTime() && tpa.get(uuid)[0].equals(p.getUniqueId())) {
                    Player player = Bukkit.getPlayer(uuid);
                    if(player.isOnline()) {
                        tpa.put(uuid, new Object[]{player.getUniqueId(), getTime()});
                        player.sendMessage(chat("&b[TPA] &6" + player.getName() + "&a님이 TPA를 수락했습니다."));
                        p.sendMessage(chat("&b[TPA] &6" + player.getName() + "&a님의 TPA를 수락했습니다."));
                        backHere(player);
                        player.teleport(p.getLocation());
                    } else {
                        p.sendMessage(chat("&6" + arg[0] + "&c님은 온라인이 아니거나 서버에 접속한 적이 없습니다."));
                    }
                }
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("tpcancel")) {
            if(tpa.containsKey(p.getUniqueId())) {
                Player player = Bukkit.getPlayer((UUID) tpa.get(p.getUniqueId())[0]);
                tpa.remove(player.getUniqueId());
                p.sendMessage(chat("&b[TPA] &6" + player.getName() + "&7님에게 보낸 TPA를 취소했습니다."));
                if(player.isOnline()) {
                    player.sendMessage(chat("&b[TPA] &6" + player.getName() + "&7님이 TPA를 취소했습니다."));
                }
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("discordsyntax")) {
            boolean b = settingConfig.getBoolean("syntax", false);
            settingConfig.set("syntax", !b);
            if(!b) p.sendMessage(chat("&aDiscord syntax chat is enabled!"));
            else p.sendMessage(chat("&cDiscord syntax chat is disabled!"));
            save();
            return true;
        }
        return false;
    }

    public static List<String> runTabComplete(CommandSender sender, Command cmd, String[] arg) {
        if(!(sender instanceof Player)) return Collections.emptyList();
        Player p = (Player) sender;
        ArrayList<String> list = new ArrayList<>();
        if (cmd.getName().equalsIgnoreCase("sethome") || cmd.getName().equalsIgnoreCase("home") || cmd.getName().equalsIgnoreCase("delhome") || cmd.getName().equalsIgnoreCase("removehome")) {
            if(arg.length == 1) {
                for(String types: homeList(p)) {
                    if(types.toLowerCase().startsWith(arg[0].toLowerCase())) {
                        list.add(types);
                    }
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("w") || cmd.getName().equalsIgnoreCase("tell") || cmd.getName().equalsIgnoreCase("msg")) {
            if(arg.length == 1) {
                for(Player player: Bukkit.getOnlinePlayers()) {
                    if(player.getName().toLowerCase().startsWith(arg[0].toLowerCase())) {
                        list.add(player.getName());
                    }
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("tpa")) {
            if(arg.length == 1) {
                for(Player player: Bukkit.getOnlinePlayers()) {
                    if(player.getName().toLowerCase().startsWith(arg[0].toLowerCase())) {
                        list.add(player.getName());
                    }
                }
            }
        }
        return list;
    }
}
