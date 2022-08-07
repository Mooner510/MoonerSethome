package org.mooner.sethome;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

import static org.mooner.sethome.MoonerUtils.*;
import static org.mooner.sethome.Main.*;

public class CommandUtils {
    public static boolean runCommand(CommandSender sender, Command cmd, String[] arg) {
        if(!(sender instanceof Player)) return false;
        Player p = (Player) sender;
        switch (cmd.getName()) {
            case "sethome":
                if (arg.length == 0) {
                    p.sendMessage(chat("&6사용법: &c/sethome <홈 이름>"));
                } else if (arg.length == 1) {
                    SetHomeAPI.addHome(p, arg[0]);
                } else {
                    p.sendMessage(chat("&c홈 이름에는 띄어쓰기를 할 수 없습니다."));
                }
                return true;
            case "home":
                if (arg.length == 0) {
                    p.sendMessage(chat("&b" + p.getName() + "&6님의 홈 목록 &7(" + SetHomeAPI.getHomeCount(p) + "/2)&8:"));
                    if (SetHomeAPI.getHomeCount(p) <= 0) {
                        p.sendMessage(chat(" &c 설정된 홈이 없습니다."));
                    } else {
                        for (Home home : SetHomeAPI.getHomes(p)) {
                            p.sendMessage(chat(" &8- &e" + home.getName() + " &d(월드: " + home.getLoc().getWorld().getName() + ")"));
                        }
                    }
                } else if (arg.length == 1) {
                    SetHomeAPI.teleportHome(p, arg[0]);
                } else {
                    p.sendMessage(chat("&c홈 이름에는 띄어쓰기를 할 수 없습니다."));
                }
                return true;
            case "back":
                SetHomeAPI.back(p);
                return true;
//        } else if (cmd.getName().equalsIgnoreCase("revive")) {
//            death(p);
//            return true;
            case "removehome":
                if (arg.length == 0) {
                    p.sendMessage(chat("&b" + p.getName() + "&6님의 홈 목록 &7(" + SetHomeAPI.getHomeCount(p) + "/2)&8:"));
                    if (SetHomeAPI.getHomeCount(p) <= 0) {
                        p.sendMessage(chat(" &c 설정된 홈이 없습니다."));
                    } else {
                        for (Home home : SetHomeAPI.getHomes(p)) {
                            p.sendMessage(chat(" &8- &e" + home.getName() + " &d(월드: " + home.getLoc().getWorld().getName() + ")"));
                        }
                    }
                } else if (arg.length == 1) {
                    SetHomeAPI.removeHome(p, arg[0]);
                } else {
                    p.sendMessage(chat("&c홈 이름에는 띄어쓰기를 할 수 없습니다."));
                }
                return true;
            case "whisper":
                if (arg.length == 0) {
                    p.sendMessage(chat("&c플레이어를 입력해 주세요."));
                    p.sendMessage(chat("&6사용법: &7/" + cmd.getName() + " <플레이어> <메시지>"));
                } else if (arg[0].equalsIgnoreCase(p.getName())) {
                    p.sendMessage(chat("&6자기 자신에게 메시지를 보내려 하지 마세요!"));
                } else if (arg.length == 1) {
                    p.sendMessage(chat("&c메시지를 입력해 주세요."));
                    p.sendMessage(chat("&6사용법: &7/" + cmd.getName() + " <플레이어> <메시지>"));
                } else {
                    String message = String.join(" ", Arrays.asList(arg).subList(1, arg.length));
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getName().equalsIgnoreCase(arg[0])) {
                            p.sendMessage(chat("&6[&c나 &6-> " + player.getDisplayName() + "&6] &f") + message);
                            player.sendMessage(chat("&6[" + p.getDisplayName() + " &6-> &c나&6] &f") + message);
                            if (!whisper.containsKey(player.getUniqueId()))
                                player.sendMessage(chat("&b/r <메시지>&7로 마지막으로 메시지를 받은 상대에게 바로 답장을 할 수 있습니다."));
                            whisper.put(player.getUniqueId(), p.getUniqueId());
                            return true;
                        }
                    }
                    p.sendMessage(chat("&6" + arg[0] + "&c님은 온라인이 아니거나 서버에 접속한 적이 없습니다."));
                }
                return true;
            case "reply":
                if (whisper.containsKey(p.getUniqueId())) {
                    String message = String.join(" ", arg);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getUniqueId().equals(whisper.get(p.getUniqueId()))) {
                            p.performCommand("w " + player.getName() + " " + message);
                            return true;
                        }
                    }
                    p.sendMessage(chat("&6" + arg[0] + "&c님은 온라인이 아니거나 서버에 접속한 적이 없습니다."));
                } else {
                    p.sendMessage(chat("&c마지막으로 대화해준 상대가 없습니다."));
                }
                return true;
            case "tpa":
                if (!tpa.containsKey(p.getUniqueId()) || (long) tpa.get(p.getUniqueId())[1] < getTime()) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getName().equalsIgnoreCase(arg[0])) {
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
            case "tpaccept":
                for (UUID uuid : tpa.keySet()) {
                    if ((long) tpa.get(uuid)[1] >= getTime() && tpa.get(uuid)[0].equals(p.getUniqueId())) {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player.isOnline()) {
                            tpa.put(uuid, new Object[]{player.getUniqueId(), getTime()});
                            player.sendMessage(chat("&b[TPA] &6" + player.getName() + "&a님이 TPA를 수락했습니다."));
                            p.sendMessage(chat("&b[TPA] &6" + player.getName() + "&a님의 TPA를 수락했습니다."));
                            SetHomeAPI.backHere(player);
                            player.teleport(p.getLocation());
                        } else {
                            p.sendMessage(chat("&6" + arg[0] + "&c님은 온라인이 아니거나 서버에 접속한 적이 없습니다."));
                        }
                    }
                }
                return true;
            case "tpcancel":
                if (tpa.containsKey(p.getUniqueId())) {
                    Player player = Bukkit.getPlayer((UUID) tpa.get(p.getUniqueId())[0]);
                    tpa.remove(player.getUniqueId());
                    p.sendMessage(chat("&b[TPA] &6" + player.getName() + "&7님에게 보낸 TPA를 취소했습니다."));
                    if (player.isOnline()) {
                        player.sendMessage(chat("&b[TPA] &6" + player.getName() + "&7님이 TPA를 취소했습니다."));
                    }
                }
                return true;
//        } else if (cmd.getName().equalsIgnoreCase("discordsyntax")) {
//            boolean b = settingConfig.getBoolean("syntax", false);
//            settingConfig.set("syntax", !b);
//            if(!b) p.sendMessage(chat("&aDiscord syntax chat is enabled!"));
//            else p.sendMessage(chat("&cDiscord syntax chat is disabled!"));
//            save();
//            return true;
        }
        return false;
    }

    public static List<String> runTabComplete(CommandSender sender, Command cmd, String[] arg) {
        if(!(sender instanceof Player)) return Collections.emptyList();
        Player p = (Player) sender;
        ArrayList<String> list = new ArrayList<>();
        switch (cmd.getName()) {
            case "sethome":
            case "home":
            case "removehome":
                if (arg.length == 1) {
                    for (Home home : SetHomeAPI.getHomes(p)) {
                        if (home.getName().toLowerCase().startsWith(arg[0].toLowerCase())) {
                            list.add(home.getName());
                        }
                    }
                }
                break;
            case "whisper":
            case "tpa":
                if (arg.length == 1) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getName().toLowerCase().startsWith(arg[0].toLowerCase())) {
                            list.add(player.getName());
                        }
                    }
                }
                break;
        }
        return list;
    }
}
