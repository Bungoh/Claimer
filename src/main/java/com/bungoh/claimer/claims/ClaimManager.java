package com.bungoh.claimer.claims;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ClaimManager {

    private static ClaimManager instance;
    private Map<String, WorldClaimManager> claimManagers;

    private ClaimManager() {
        claimManagers = new HashMap<>();
        for (World w : Bukkit.getWorlds()) {
            claimManagers.put(w.getName(), new WorldClaimManager(w.getName()));
        }
    }

    /**
     * Update the data.yml file to account for all {@link Claim} changes made.
     */
    public void updateClaims() {
        for (Map.Entry<String, WorldClaimManager> e : claimManagers.entrySet()) {
            e.getValue().updateDataFile();
        }
    }

    public static synchronized @NotNull ClaimManager getInstance() {
        if (instance == null) {
            instance = new ClaimManager();
        }
        return instance;
    }

    public WorldClaimManager getWorldClaimManager(World world) {
        return claimManagers.get(world.getName());
    }
}
