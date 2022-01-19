package com.bungoh.claimer.claims;

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
     * Add Trusted Members to the {@link Claim}
     * @param trustedMembers Variable amount of Player UUID's who are trusted.
     */
    public void addTrustedMembers(UUID... trustedMembers) {
        Collections.addAll(trusted, trustedMembers);
    }

    /**
     * Add a Trusted Member to the {@link Claim}
     * @param trustedMember The Player UUID to be trusted.
     */
    public void addTrustedMember(UUID trustedMember) {
        trusted.add(trustedMember);
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
