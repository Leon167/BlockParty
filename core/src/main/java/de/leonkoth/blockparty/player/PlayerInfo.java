package de.leonkoth.blockparty.player;

import de.leonkoth.blockparty.BlockParty;
import de.leonkoth.blockparty.arena.Arena;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Leon on 15.03.2018.
 * Project Blockparty2
 * © 2019 - Leon Koth
 */

public class PlayerInfo {

    @Getter
    private static List<PlayerInfo> allPlayerInfos = new ArrayList<>();

    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    private int wins;

    @Setter
    @Getter
    private int points;

    @Setter
    @Getter
    private int gamesPlayed;

    @Setter
    @Getter
    private UUID uuid;

    @Setter
    @Getter
    private PlayerState playerState;

    @Setter
    @Getter
    private PlayerData playerData;

    @Setter
    @Getter
    private Arena currentArena;

    @Setter
    @Getter
    private Inventory inventory;

    @Setter
    @Getter
    private Scoreboard scoreboard;

    @Setter
    @Getter
    private int id;

    public PlayerInfo(String name, UUID uuid, int wins, int points, int gamesPlayed) {
        this.name = name;
        this.uuid = uuid;
        this.playerState = PlayerState.DEFAULT;
        this.wins = wins;
        this.points = points;
        this.gamesPlayed = gamesPlayed;
        this.id = getNextId();
        allPlayerInfos.add(this);
    }

    public PlayerInfo(int id, String name, UUID uuid, int wins, int points, int gamesPlayed) {
        this.name = name;
        this.uuid = uuid;
        this.playerState = PlayerState.DEFAULT;
        this.wins = wins;
        this.points = points;
        this.gamesPlayed = gamesPlayed;
        this.id = id;
        allPlayerInfos.add(this);
    }

    public PlayerInfo() {
        this.playerState = PlayerState.DEFAULT;
        this.id = getNextId();
        allPlayerInfos.add(this);
    }

    public void updateStats()
    {
        BlockParty.getInstance().getPlayerInfoManager().load(this);
    }

    private static int getNextId() {
        int i = 1;
        boolean eq;
        while (i < Integer.MAX_VALUE) {
            eq = false;
            for (PlayerInfo pi : allPlayerInfos) {
                if (pi.getId() == i) {
                    eq = true;
                    break;
                }
            }
            if (!eq) {
                return i;
            }
            i++;
        }
        //throw new IDOverFlowException("No IDs available");
        return -1;
    }

    public static PlayerInfo getFromPlayer(Player player) {
        for (PlayerInfo playerInfo : BlockParty.getInstance().getPlayers()) {
            if (playerInfo.getUuid().equals(player.getUniqueId())) {
                return playerInfo;
            }
        }

        return null;
    }

    public static PlayerInfo getFromPlayer(String player) {
        for (PlayerInfo playerInfo : BlockParty.getInstance().getPlayers()) {
            if (playerInfo.getName().equalsIgnoreCase(player)) {
                return playerInfo;
            }
        }

        return null;
    }

    public static boolean isInArena(PlayerInfo player) {
        return player != null && player.isInArena();
    }

    public static boolean isInArena(Player player) {
        return isInArena(getFromPlayer(player));
    }

    public static boolean isInArena(String player) {
        return isInArena(getFromPlayer(player));
    }

    public boolean isInArena() {
        return currentArena != null && playerState != PlayerState.DEFAULT;
    }

    @Override
    public String toString() {
        return "PlayerInfo -> " + name + ":" + uuid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (obj instanceof PlayerInfo) {
            PlayerInfo playerInfo = (PlayerInfo) obj;
            return uuid.equals(playerInfo.getUuid());
        } else {
            return super.equals(obj);
        }
    }

    public Player asPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void removePoints(int points) {
        this.points -= points;
    }

    public void addWins(int wins) {
        this.wins += wins;
    }

    public void addGamesPlayed(int gamesplayed) {
        this.gamesPlayed += gamesplayed;
    }

    public void removeWins(int wins) {
        this.wins -= wins;
    }

}
