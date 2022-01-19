package com.bungoh.claimer.claims;

import java.sql.Array;
import java.util.*;

public class Claim {

    private final long key;
    private final UUID owner;
    private final Set<UUID> trusted;
    private String worldName;

    public Claim(long key, String worldName, UUID owner) {
        this.key = key;
        this.worldName = worldName;
        this.owner = owner;
        trusted = new HashSet<>();
    }

    /**
     * Add a Trusted Member to the {@link Claim}
     * @param trustedMember The Player UUID to be trusted.
     * @return true if the {@link org.bukkit.entity.Player} was not already trusted.
     */
    public boolean addTrustedMember(UUID trustedMember) {
        return trusted.add(trustedMember);
    }

    /**
     * Remove a Trust Member from the {@link Claim}
     * @param trustedMember The Player UUID to be trusted.
     * @return true if the Trusted Member was in the Set.
     */
    public boolean removeTrustedMember(UUID trustedMember) {
        return trusted.remove(trustedMember);
    }

    /**
     * Specifies whether a Player can place blocks in this Claim.
     * @param uuid The UUID of the Player
     * @return true if the Player can place blocks in this Claim.
     */
    public boolean canPlace(UUID uuid) {
        return owner.equals(uuid) || trusted.contains(uuid);
    }

    /**
     * Specifies whether a Player can break blocks in this Claim.
     * @param uuid The UUID of the Player
     * @return true if the Player can break blocks in this Claim.
     */
    public boolean canBreak(UUID uuid) {
        return owner.equals(uuid) || trusted.contains(uuid);
    }

    public long getKey() {
        return key;
    }

    public UUID getOwner() {
        return owner;
    }

    public Set<UUID> getTrusted() {
        return trusted;
    }
}
