package org.mooner.sethome;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.mooner.sethome.MoonerUtils.chat;
import static org.mooner.sethome.MoonerUtils.loadConfig;

public class main extends JavaPlugin implements Listener {

    public static main plugin;
    public static FileConfiguration config;
    public static FileConfiguration settingConfig;
    public static HashMap<UUID, UUID> whisper = new HashMap<>();
    public static HashMap<UUID, Object[]> tpa = new HashMap<>();

    public static String dataPath = "plugins/MoonerSethome/";

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        CommandUtils.deathHere(p);
    }

    @EventHandler
    public void onDeath(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        p.sendMessage(chat("&6/revive&7를 이용해 죽었던 장소로 이동할 수 있습니다."));
        p.sendMessage(chat("&c이 명령어를 사용하면 &6/back&7을 이용해 현재 이 위치로 돌아올 수 없습니다."));
    }

    @Deprecated
    public void onChat(AsyncPlayerChatEvent e) {
        if(settingConfig.getBoolean("syntax", false)) {
            String s = e.getMessage();
            Matcher matcher = Pattern.compile("\\*{2}([^*]+)\\*{2}").matcher(s);
            String v;
            while (matcher.find()) {
                v = matcher.group();
                s = s.replaceFirst(v.replace("*", "\\*"), chat("&l") + v.replace("*", "") + chat("&r"));
            }
            matcher = Pattern.compile("\\*([^*]+)\\*").matcher(s);
            while (matcher.find()) {
                v = matcher.group();
                s = s.replaceFirst(v.replace("*", "\\*"), chat("&o") + v.replace("*", "") + chat("&r"));
            }
            matcher = Pattern.compile("_([^*]+)_").matcher(s);
            while (matcher.find()) {
                v = matcher.group();
                s = s.replaceFirst(v, chat("&n") + v.replace("_", "") + chat("&r"));
            }
            matcher = Pattern.compile("~{2}([^*]+)~{2}").matcher(s);
            while (matcher.find()) {
                v = matcher.group();
                s = s.replaceFirst(v, chat("&m") + v.replace("~", "") + chat("&r"));
            }
            matcher = Pattern.compile("~{2}([^*]+)~{2}").matcher(s);
            while (matcher.find()) {
                v = matcher.group();
                s = s.replaceFirst(v, chat("&m") + v.replace("~", "") + chat("&r"));
            }
//        matcher = Pattern.compile("\\|{2}([^*]+)\\|{2}").matcher(s);
//        TextComponent c = null;
//        String b;
//        TextComponent a;
//        while (matcher.find()) {
//            if(c == null) c = new TextComponent("");
//            v = matcher.group();
//            b = s.substring(0, s.indexOf(v));
//            if(b.length() > 0) c.addExtra(new TextComponent(b));
//            a = new TextComponent(new String(new char[v.replace("|", "").length()]).replace("\0", "⬛"));
//            a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(v.replace("|", ""))));
//            a.setColor(ChatColor.DARK_GRAY);
//            c.addExtra(a);
//            s = s.replaceFirst(v, "");
////            c = new TextComponent(new String(new char[v.length()]).replace("\0", "⬛"));
////            c.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(v.replace("|", ""))));
////            c.setColor(ChatColor.BLACK);
////            s = s.replaceFirst(v, c.toPlainText());
//        }
//        if(c != null) {
//            e.setMessage("");
//            Bukkit.spigot().broadcast(c);
//        }
            e.setMessage(s);
        }
    }

    public static void update() {
        File f = new File(dataPath);
        if(!f.exists()) f.mkdir();
        File f2 = new File(dataPath, "homes.yml");
        try {
            f2.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File f3 = new File(dataPath, "config.yml");
        try {
            f3.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        config = loadConfig(dataPath, "homes.yml");
        settingConfig = loadConfig(dataPath, "config.yml");
        save();
    }

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(chat("&bPlugin Enabled! &7- &dMoonerSetHome"));
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        File f = new File(dataPath);
        if(!f.exists()) f.mkdir();
        File f2 = new File(dataPath, "homes.yml");
        try {
            f2.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File f3 = new File(dataPath, "config.yml");
        try {
            f3.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        config = loadConfig(dataPath, "homes.yml");
        settingConfig = loadConfig(dataPath, "config.yml");
    }

    public static void save() {
        try {
            settingConfig.save(new File(dataPath, "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(chat("&bPlugin Disabled! &7- &dMoonerSetHome"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
        return CommandUtils.runCommand(sender, cmd, arg);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] arg) {
        return CommandUtils.runTabComplete(sender, cmd, arg);
    }
}