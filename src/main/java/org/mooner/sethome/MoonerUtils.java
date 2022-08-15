package org.mooner.sethome;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class MoonerUtils {
    public static String firstUpper(String str) {
        StringBuilder builder = new StringBuilder();
        for(String s: str.split(" ")) {
            builder.append(s.substring(0, 1).toUpperCase()).append(s.substring(1).toLowerCase()).append(" ");
        }
        return builder.toString();
    }

    /**
     * {@code 0.5} = 0.5%
     *
     * {@code 1} = 1%
     *
     * {@code 22} = 22%
     *
     * {@code 100} = 100%
    **/
    public static boolean chance(double chance) {
         return Math.random() * 100000 <= chance * 1000;
    }

    public static boolean canHold(Player p, ItemStack i) {
        for (ItemStack item : p.getInventory().getContents()) {
            if (item == null || item.getType().equals(Material.AIR)) return true;

            if (item.getItemMeta() != null) {
                if(item.getItemMeta().equals(i.getItemMeta()) && item.getType().equals(i.getType()) && item.getAmount() + i.getAmount() <= i.getMaxStackSize()) {
                    return true;
                }
            }

            if (item.getType().equals(i.getType()) && item.getAmount() + i.getAmount() <= i.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param d = String
     *
     *          ex. 3s / 3d / 5m2s
     *
     * @return = formatted duration
     */
    public static int durationFromString(String d) {
        int r = 0;
        try {
            if (d.contains("d")) {
                r += Integer.parseInt(d.substring(0, d.indexOf("d"))) * 60 * 60 * 24;
                if(d.length() > d.indexOf("d") + 1)
                    d = d.substring(d.indexOf("d") + 1);
            }

            if (d.contains("h")) {
                r += Integer.parseInt(d.substring(0, d.indexOf("h"))) * 60 * 60;
                if(d.length() > d.indexOf("h") + 1)
                    d = d.substring(d.indexOf("h") + 1);
            }

            if (d.contains("m")) {
                r += Integer.parseInt(d.substring(0, d.indexOf("m"))) * 60;
                if(d.length() > d.indexOf("m") + 1)
                    d = d.substring(d.indexOf("m") + 1);
            }

            if (d.contains("s")) {
                r += Integer.parseInt(d.substring(0, d.indexOf("s"))) * 60;
            }
        } catch (Exception e) {
            return 0;
        }
        return r;
    }

    /**
     * @param format = String Format
     * @return = Formatted String
     * @see SimpleDateFormat
     * @see SimpleDateFormat#format(Date)
     */
    public static String getDate(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.KOREA);
        return sdf.format(date);
    }

    /**
     * @param d = per second
     * @return = formatted duration
     */
    public static String duration(double d) {
        int second = (int) Math.floor(d);
        int minute = 0;
        int hour = 0;
        while(second >= 60) {
            second -= 60;
            minute++;
        }
        while(minute >= 60) {
            minute -= 60;
            hour++;
        }
        return ((hour > 0) ? toTimeFormatNumber(hour) + ":" : "") + ((minute > 0) ? toTimeFormatNumber(minute) + ":" : ((hour > 0)?"0":"")+"0:") + toTimeFormatNumber(second);
    }

    public static String toTimeFormatNumber(int d) {
        return (d < 10) ? "0" + d : d + "";
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static String replaceLast(String str, String regex, String replacement) {
        int regexIndexOf = str.lastIndexOf(regex);
        if(regexIndexOf == -1){
            return str;
        } else {
            return str.substring(0, regexIndexOf) + replacement + str.substring(regexIndexOf + regex.length());
        }
    }

    public static FileConfiguration loadConfig(String Path, String File) {
        FileInputStream stream = null;
        File f = new File(Path, File);

        try {
            stream = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        assert stream != null;
        return YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    public static long calcInt(String calc) {
        return Math.round(calcDouble(calc));
    }

    public static long calcInt(String calc, double value, int multi) {
        return Math.round(calcDouble(calc, value, multi));
    }

    public static double calcDouble(String calc) {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        try {
            return Double.parseDouble(engine.eval(calc).toString());
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static double calcDouble(String calc, double value, int multi) {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        calc = calc
                .replace("{a}", (value * multi) + "")
                .replace("{level}", multi+"")
                .replace("{value}", value+"");
        try {
            return Double.parseDouble(engine.eval(calc).toString());
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String chat(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String rome(long value) {
        StringBuilder s = new StringBuilder();
        if(value <= 0) return value + "";
        if(value >= 10000) return value + "";
        long v = value;
        while(v >= 9000) {
            s.append("FMF");
            v -= 9000;
        }
        while(v >= 5000) {
            s.append("F");
            v -= 5000;
        }
        while(v >= 4000) {
            s.append("MF");
            v -= 4000;
        }
        while(v >= 1000) {
            s.append("M");
            v -= 1000;
        }
        while(v >= 900) {
            s.append("CM");
            v -= 900;
        }
        while(v >= 500) {
            s.append("D");
            v -= 500;
        }
        while(v >= 400) {
            s.append("CD");
            v -= 400;
        }
        while(v >= 100) {
            s.append("C");
            v -= 100;
        }
        while(v >= 90) {
            s.append("XC");
            v -= 100;
        }
        while(v >= 50) {
            s.append("L");
            v -= 50;
        }
        while(v >= 40) {
            s.append("XL");
            v -= 40;
        }
        while(v >= 10) {
            s.append("X");
            v -= 10;
        }
        while(v >= 9) {
            s.append("IX");
            v -= 9;
        }
        while(v >= 5) {
            s.append("V");
            v -= 5;
        }
        while(v >= 4) {
            s.append("IV");
            v -= 4;
        }
        while(v >= 1) {
            s.append("I");
            v -= 1;
        }
        return s.toString();
    }

    public static String commaNumber(int number) {
        return NumberFormat.getInstance().format(number);
    }

    public static String commaNumber(double number) {
        return NumberFormat.getInstance().format(number);
    }

    public static String parseIfInt(double value, boolean comma) {
        if(comma) {
            if (Math.floor(value) == value) {
                return commaNumber(Math.floor(value));
            }
            return commaNumber(value);
        } else {
            if (Math.floor(value) == value) {
                return (int) Math.floor(value) + "";
            }
            return value + "";
        }
    }

    public static String parseString(double value) {
        BigDecimal b = BigDecimal.valueOf(value);
        b = b.setScale(0, 4);
        return parseIfInt(Double.parseDouble(b.toString()), false);
    }

    public static String parseString(double value, int amount) {
        BigDecimal b = BigDecimal.valueOf(value);
        b = b.setScale(amount, 4);
        return parseIfInt(Double.parseDouble(b.toString()), false);
    }

    public static String parseString(double value, boolean comma) {
        BigDecimal b = BigDecimal.valueOf(value);
        b = b.setScale(0, 4);
        return parseIfInt(Double.parseDouble(b.toString()), comma);
    }

    public static String parseString(double value, int amount, boolean comma) {
        BigDecimal b = BigDecimal.valueOf(value);
        b = b.setScale(amount, 4);
        return parseIfInt(Double.parseDouble(b.toString()), comma);
    }

    public static double parseDouble(double value) {
        BigDecimal b = BigDecimal.valueOf(value);
        b = b.setScale(0, 4);
        return Double.parseDouble(b.toString());
    }

    public static double parseDouble(double value, int amount) {
        BigDecimal b = BigDecimal.valueOf(value);
        b = b.setScale(amount, 4);
        return Double.parseDouble(b.toString());
    }

    public static double parseDouble(BigDecimal value) {
        return Double.parseDouble(value.toString());
    }

    public static double parseDouble(BigDecimal value, int amount) {
        value = value.setScale(amount, 4);
        return Double.parseDouble(value.toString());
    }

    public static String parseDoubleString(double value, int amount) {
        BigDecimal b = BigDecimal.valueOf(value);
        b = b.setScale(amount, 4);
        return String.valueOf(Double.parseDouble(b.toString()));
    }

    public static final ArrayList<String> listColor = new ArrayList<>(
            Arrays.asList(chat("&f"), chat("&e"), chat("&6"), chat("&c"), chat("&c"), chat("&f"), chat("&e"), chat("&6"), chat("&c"), chat("&c"))
    );

    public static String critMsg(String value) {
        int index = 0;
        StringBuilder b = new StringBuilder(chat("&f✧"));
        int size = listColor.size();
        for(String v: value.split("")) {
            if(index > size - 1) index = 0;
            b.append(listColor.get(index)).append(v);
            index++;
        }
        b.append(chat("&c✧"));
        return b.toString();
    }

    public static boolean onGround(Player p) {
        if(!p.isFlying()) {
            if(p.getLocation().getBlock().getType().equals(Material.AIR)) {
                if (p.getLocation().subtract(0, 0.005, 0).getBlock().getType().isSolid()) {
                    return true;
                } else return p.getLocation().add(0.3, -0.005, 0.3).getBlock().getType().isSolid() || p.getLocation().add(-0.3, -0.005, -0.3).getBlock().getType().isSolid();
            }
        }
        return false;
    }

    public static long getTimeSec() {
        return (long) Math.floor((double) System.currentTimeMillis() / 1000);
    }

    public static long getTime() {
        return System.currentTimeMillis();
    }

    public static void playSound(Location l, String s) {
        String[] soundStrings = s.split(":");
        if(soundStrings.length == 1) {
            playSound(l, soundStrings[0], 1, 1);
        } else if (soundStrings.length == 2) {
            playSound(l, soundStrings[0], 1, Double.parseDouble(soundStrings[1]));
        } else if (soundStrings.length == 3) {
            playSound(l, soundStrings[0], Double.parseDouble(soundStrings[2]), Double.parseDouble(soundStrings[1]));
        }
    }

    public static void playSound(Player p, String s) {
        String[] soundStrings = s.split(":");
        if(soundStrings.length == 1) {
            playSound(p, soundStrings[0], 1, 1);
        } else if (soundStrings.length == 2) {
            playSound(p, soundStrings[0], 1, Double.parseDouble(soundStrings[1]));
        } else if (soundStrings.length == 3) {
            playSound(p, soundStrings[0], Double.parseDouble(soundStrings[2]), Double.parseDouble(soundStrings[1]));
        }
    }

    public static void playSound(Location l, Sound s, double volume, double pitch) {
        Bukkit.getScheduler().runTaskAsynchronously(SetHome.plugin, () -> l.getWorld().playSound(l, s, (float) volume, (float) pitch));
    }

    public static void playSound(Location l, String s, double volume, double pitch) {
        Bukkit.getScheduler().runTaskAsynchronously(SetHome.plugin, () -> l.getWorld().playSound(l, Sound.valueOf(s), (float) volume, (float) pitch));
    }

    public static void playSound(Player p, Sound s, double volume, double pitch) {
        Bukkit.getScheduler().runTaskAsynchronously(SetHome.plugin, () -> p.playSound(p.getLocation(), s, (float) volume, (float) pitch));
    }

    public static void playSound(Player p, String s, double volume, double pitch) {
        Bukkit.getScheduler().runTaskAsynchronously(SetHome.plugin, () -> p.playSound(p.getLocation(), Sound.valueOf(s), (float) volume, (float) pitch));
    }
}
