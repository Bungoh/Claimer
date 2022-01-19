package com.bungoh.claimer.listeners;

import com.bungoh.claimer.claims.Claim;
import com.bungoh.claimer.claims.ClaimManager;
import com.bungoh.claimer.claims.WorldClaimManager;
import com.bungoh.claimer.text.Message;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Optional;

public class BlockBreakListener implements Listener {

    public BlockBreakListener() {

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Chunk c = event.getBlock().getChunk();
        WorldClaimManager wcm = ClaimManager.getInstance().getWorldClaimManager(c.getWorld());
        Optional<Claim> optClaim = wcm.getClaim(c.getChunkKey());
        optClaim.ifPresent(claim -> {
            Player player = event.getPlayer();
            if (!claim.canBreak(player.getUniqueId())) {
                event.setCancelled(true);
                Message.prefixedText("&cYou can't break blocks in this claim!").to(player);
            }
        });
    }

}
