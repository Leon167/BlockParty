package de.leonkoth.blockparty.arena;

import de.leonkoth.blockparty.BlockParty;
import de.leonkoth.blockparty.floor.Floor;
import de.leonkoth.blockparty.song.SongManager;
import de.leonkoth.blockparty.util.Bounds;
import de.leonkoth.blockparty.util.Size;
import de.leonkoth.utils.ui.IConfigUI;
import de.pauhull.utils.misc.YAMLLocation;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;
import java.util.List;

public class ArenaDataSet {

    @Setter
    @Getter
    @IConfigUI(title = "Test", description = {"Erste Zeile", "Und das ist die zweite"}, infoItem = Material.APPLE, onItem = Material.GREEN_DYE, offItem = Material.GRAY_DYE)
    int distanceToOutArea, timeToSearch, levelAmount, minPlayers, maxPlayers, lobbyCountdown;

    @Setter
    @Getter
    double timeReductionPerLevel, timeModifier;

    @Setter
    @Getter
    @IConfigUI(title = "Boolean Test", description = {"Erste Zeile", "Und das ist die zweite"}, infoItem = Material.APPLE, onItem = Material.GREEN_DYE, offItem = Material.GRAY_DYE, useVarNameAsTitle = true)
    boolean enabled, enableParticles, enableLightnings, autoRestart, autoKick, enableBoosts, enableFallingBlocks, useAutoGeneratedFloors, usePatternFloors,
            enableActionbarInfo, useNoteBlockSongs, useWebSongs, enableFireworksOnWin, timerResetOnPlayerJoin, allowJoinDuringGame, enableScoreboard, enableJoinMessage, enableSpectatorMode;

    @Setter
    @Getter
    @IConfigUI.UIInfo(suffix = " Config", leftItem = Material.APPLE, rightItem = Material.BONE, leftItemTitle = "Previous Page", rightItemTitle = "Next Page")
    String name;

    @Setter
    @Getter
    SongManager songManager;

    @Setter
    @Getter
    Floor floor;

    @Setter
    @Getter
    Location gameSpawn, lobbySpawn;

    @Setter
    @Getter
    SignList signs;

    public void loadAllValues(FileConfiguration configuration, Arena arena) {
        for (Field field : ArenaDataSet.class.getDeclaredFields()) {
            String path = "Settings." + getCapitalizedName(field.getName());
            Object value = load(configuration, path, field.getType(), arena);
            //Object castedValue = field.getType().cast(value);

            try {
                field.set(this, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void setAllValues(FileConfiguration configuration) {

        if (BlockParty.DEBUG)
            Bukkit.getConsoleSender().sendMessage("§c1");
        try {
            if (BlockParty.DEBUG)
                Bukkit.getConsoleSender().sendMessage("§d2");
            for (Field field : ArenaDataSet.class.getDeclaredFields()) {
                if (BlockParty.DEBUG)
                    Bukkit.getConsoleSender().sendMessage("§b3: " + field.getName());
                String path = "Settings." + getCapitalizedName(field.getName());
                Object value = field.get(this);
                set(configuration, path, value);

                if (BlockParty.DEBUG)
                    Bukkit.getConsoleSender().sendMessage("§a4: " + path);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Object load(FileConfiguration configuration, String path, Class<?> type, Arena arena) {
        if (!configuration.isSet(path)) {
            String fieldName = path.replace("Settings.", "");
            for (Field field : ArenaDataSet.class.getDeclaredFields()) {
                if (field.getName().equalsIgnoreCase(fieldName)){
                    try {
                        return field.get(arena.getData());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
            return null;
        }

        if (type == SongManager.class) {
            List<String> songs = configuration.getStringList(path);
            return new SongManager(arena, songs);
        } else if (type == Floor.class) {
            Location a = YAMLLocation.getLocation(path + ".A", configuration);
            Location b = YAMLLocation.getLocation(path + ".B", configuration);
            List<String> patterns = configuration.getStringList(path + ".Patterns");
            double width = configuration.getDouble(path + ".Width");
            double length = configuration.getDouble(path + ".Length");
            return new Floor(patterns, new Bounds(a, b), arena, new Size(width, 1, length));
        } else if (type == Location.class) {
            return YAMLLocation.getLocation(path, configuration);
        } else if (type == SignList.class) {
            List<String> locations = configuration.getStringList(path);
            return SignList.fromStringList(locations);
        } else {
            return configuration.get(path);
        }
    }

    public void set(FileConfiguration configuration, String path, Object object) {
        if (object instanceof SongManager) {
            SongManager songManager = (SongManager) object;
            configuration.set(path, songManager.getSongNames());
        } else if (object instanceof Floor) {
            Floor floor = (Floor) object;
            YAMLLocation.saveLocation(floor.getBounds().getA(), path + ".A", configuration);
            YAMLLocation.saveLocation(floor.getBounds().getB(), path + ".B", configuration);
            configuration.set(path + ".Patterns", floor.getPatternNames());
            configuration.set(path + ".Width", floor.getSize().getWidth());
            configuration.set(path + ".Length", floor.getSize().getLength());
        } else if (object instanceof Location) {
            Location location = (Location) object;
            YAMLLocation.saveLocation(location, path, configuration);
        } else if (object instanceof SignList) {
            SignList signList = (SignList) object;
            configuration.set(path, signList.toStringList());
        } else {
            configuration.set(path, object);
        }
    }

    private String getCapitalizedName(String name) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

}