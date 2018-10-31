package de.leonkoth.blockparty.listener;

import de.leonkoth.blockparty.BlockParty;
import de.leonkoth.blockparty.arena.Arena;
import de.leonkoth.blockparty.player.PlayerInfo;
import de.leonkoth.blockparty.player.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Leon on 15.03.2018.
 * Project Blockparty2
 * © 2016 - Leon Koth
 */
public class PlayerJoinListener implements Listener {

    private BlockParty blockParty;

    public PlayerJoinListener(BlockParty blockParty) {
        this.blockParty = blockParty;

        Bukkit.getPluginManager().registerEvents(this, blockParty.getPlugin());
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PlayerInfo playerInfo = PlayerInfo.getFromPlayer(player);
        if (playerInfo == null) {
            blockParty.getPlayers().add((playerInfo = new PlayerInfo(player.getName(), player.getUniqueId(), 0, 0)));
        } else {
            playerInfo.setPlayerState(PlayerState.DEFAULT);
            playerInfo.setCurrentArena(null);
        }
        blockParty.getPlayerInfoManager().createPlayerInfo(playerInfo);

        Arena arena = Arena.getByName(blockParty.getDefaultArena());
        if (blockParty.isBungee() && arena != null) {
            String error = arena.addPlayer(player).getCancelMessage();
            if (error != null) {
                player.kickPlayer(error);
            }
        }
    }

}
