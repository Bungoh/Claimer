package com.bungoh.claimer.claims;

import com.bungoh.claimer.files.Config;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class WorldClaimManager {

    private final Map<Long, Claim> claims;
    private final String worldName;

    public WorldClaimManager(final @NotNull String worldName) {
        this.worldName = worldName;
        claims = new HashMap<>();
        initClaims();
    }

    private void initClaims() {
        Config.CACHE.getFile("data.yml")
                .modify(fc -> {
                    ConfigurationSection section = fc.getConfigurationSection(worldName);
                    if (section != null) {
                        Set<String> claimKeys = section.getKeys(false);
                        for (String key : claimKeys) {
                            // Get Essential Information to Create Claim Object
                            long l;
                            try {
                                l = Long.parseLong(key);
                            } catch (NumberFormatException e) {
                                Bukkit.getServer().getLogger()
                                        .warning("Claim " + key + " in world: " + worldName + " failed to load.");
                                continue;
                            }
                            String ownerString = section.getString(key + ".owner");
                            if (ownerString == null) {
                                Bukkit.getServer().getLogger()
                                        .warning("Claim " + key + " in world: " + worldName + " failed to load." +
                                                "No owner was supplied in the data file.");
                                continue;
                            }
                            UUID owner;
                            try {
                                owner = UUID.fromString(ownerString);
                            } catch (IllegalArgumentException e) {
                                Bukkit.getServer().getLogger()
                                        .warning("Claim " + key + " in world: " + worldName + " failed to load." +
                                                "Owner UUID was invalid.");
                                continue;
                            }
                            // Create Claim Object
                            Claim claim = new Claim(l, worldName, owner);

                            // Add Trusted Members
                            section.getStringList(key + ".trusted").stream()
                                    .map(s -> {
                                        try {
                                            return UUID.fromString(s);
                                        } catch (IllegalArgumentException e) {
                                            return null;
                                        }
                                    })
                                    .filter(Objects::nonNull)
                                    .forEach(claim::addTrustedMember);

                            claims.put(l, claim);
                        }
                    }
                });
    }

    /**
     * Updates the data.yml with the newly added {@link Claim}s
     */
    protected void updateDataFile() {
        Config data = Config.CACHE.getFile("data.yml");
        data.modify(fc -> {
            fc.set(worldName, null);
            for (Map.Entry<Long, Claim> entry : claims.entrySet()) {
                Claim claim = entry.getValue();
                long key = claim.getKey();
                // Set the owner
                fc.set(worldName + "." + key + ".owner", claim.getOwner().toString());
                // Set the trusted list
                List<String> trustedList = claim.getTrusted().stream()
                                .map(UUID::toString)
                                .collect(Collectors.toList());
                fc.set(worldName + "." + key + ".trusted", trustedList);
            }
        });
        data.save();
    }

    /**
     * Adds a {@link Claim} to the Claim Map. User should check if there is already a {@link Claim} at the location.
     * @param c The {@link Claim}
     */
    public void addClaim(final @NotNull Claim c) {
        claims.put(c.getKey(), c);
    }

    /**
     * Removes a {@link Claim} from the Claim Map. Users should check if this {@link Claim} is actually in the Map.
     * @param c The {@link Claim}
     */
    public void removeClaim(final @NotNull Claim c) {
        claims.remove(c.getKey());
    }

    /**
     * Checks if a {@link Chunk} is a {@link Claim} using the {@link Chunk}'s key.
     * @param c The {@link Chunk}
     * @return true if the {@link Chunk} is claimed.
     */
    public boolean isChunkClaimed(final @NotNull Chunk c) {
        return claims.containsKey(c.getChunkKey());
    }

    /**
     * Get an {@link Optional<Claim>} given a unique {@link Claim} key.
     * @param key The unique key of the {@link Claim}
     * @return an {@link Optional<Claim>}
     */
    public Optional<Claim> getClaim(final long key) {
        return Optional.ofNullable(claims.get(key));
    }

}
