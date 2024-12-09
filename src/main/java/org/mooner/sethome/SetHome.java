package org.mooner.sethome;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.mooner.moonerbungeeapi.api.BungeeAPI;
import org.mooner.moonerbungeeapi.api.ServerType;
import org.mooner.sethome.api.SetHomeAPI;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.mooner.sethome.MoonerUtils.chat;

public class SetHome extends JavaPlugin implements Listener {

    public static SetHome plugin;
    public static ServerType serverType;
    public static HashMap<UUID, UUID> whisper = new HashMap<>();
    public static HashMap<UUID, Object[]> tpa = new HashMap<>();

    public static String dataPath = "plugins/MoonerSethome/";
    public static String dbPath = "../db/Sethome/";

    @Deprecated
    public void onChat(AsyncPlayerChatEvent e) {
//        if(settingConfig.getBoolean("syntax", false)) {
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

    @Override
    public void onEnable() {
        plugin = this;
        serverType = BungeeAPI.getServerType(Bukkit.getPort());
        Bukkit.getConsoleSender().sendMessage(chat("&bPlugin Enabled! &7- &dMoonerSetHome"));
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        SetHomeAPI.loadAPI();
        SetHomeAPI.reload();
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(chat("&bPlugin Disabled! &7- &dMoonerSetHome"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
        return CommandUtils.runCommand(sender, cmd, label, arg);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] arg) {
        return CommandUtils.runTabComplete(sender, cmd, arg);
    }
}