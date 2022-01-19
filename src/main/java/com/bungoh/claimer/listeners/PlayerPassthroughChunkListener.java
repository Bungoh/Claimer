package com.bungoh.claimer.listeners;

import com.bungoh.claimer.claims.Claim;
import com.bungoh.claimer.claims.ClaimManager;
import com.bungoh.claimer.claims.WorldClaimManager;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import java.util.Optional;

public class PlayerPassthroughChunkListener implements Listener {

    public PlayerPassthroughChunkListener() {

    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Chunk c1 = event.getFrom().getChunk();
        Chunk c2 = event.getTo().getChunk();
        if (c1.equals(c2) || !c1.getWorld().equals(c2.getWorld())) {
            return;
        }

        Player player = event.getPlayer();
        WorldClaimManager wcm = ClaimManager.getInstance().getWorldClaimManager(c1.getWorld());
        Optional<Claim> optClaim1 = wcm.getClaim(c1.getChunkKey());
        Optional<Claim> optClaim2 = wcm.getClaim(c2.getChunkKey());
        if (optClaim2.isPresent()) {
            //Check if optClaim1 is the same, if so return.
            if (optClaim1.isPresent() && optClaim1.get().getOwner().equals(optClaim2.get().getOwner())) {
                return;
            }
            optClaim2.get().sendWelcomeMessage(player);
        } else {
            optClaim1.ifPresent(claim -> claim.sendFarewellMessage(player));
        }
    }

}
