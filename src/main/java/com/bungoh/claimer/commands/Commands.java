package com.bungoh.claimer.commands;

import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.bukkit.parsers.OfflinePlayerArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.context.CommandContext;
import com.bungoh.claimer.claims.Claim;
import com.bungoh.claimer.claims.ClaimManager;
import com.bungoh.claimer.claims.WorldClaimManager;
import com.bungoh.claimer.text.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Commands {

    private BukkitCommandManager<CommandSender> manager;

    public Commands(BukkitCommandManager<CommandSender> manager) {
        this.manager = manager;
    }

    public void initCommands() {
        manager.command(
                manager.commandBuilder("claimer", "c")
                        .literal("claim")
                        .argument(IntegerArgument.<CommandSender>newBuilder("radius")
                                .withMin(1)
                                .asOptionalWithDefault(1)
                                .build())
                        .senderType(Player.class)
                        .handler(this::claimCommand)
        );

        manager.command(
                manager.commandBuilder("claimer", "c")
                        .literal("unclaim")
                        .senderType(Player.class)
                        .handler(this::unclaimCommand)
        );

        manager.command(
                manager.commandBuilder("claimer", "c")
                        .literal("info")
                        .senderType(Player.class)
                        .handler(this::infoCommand)
        );

        manager.command(
                manager.commandBuilder("claimer", "c")
                        .literal("trust")
                        .argument(OfflinePlayerArgument.<CommandSender>newBuilder("player")
                                .build())
                        .senderType(Player.class)
                        .handler(this::trustCommand)
        );

        manager.command(
                manager.commandBuilder("claimer", "c")
                        .literal("untrust")
                        .argument(OfflinePlayerArgument.<CommandSender>newBuilder("player")
                                .build())
                        .senderType(Player.class)
                        .handler(this::untrustCommand)
        );
    }

    private void claimCommand(final @NonNull CommandContext<CommandSender> ctx) {
        Player p = (Player) ctx.getSender();
        WorldClaimManager wcm = ClaimManager.getInstance().getWorldClaimManager(p.getWorld());
        Optional<Claim> optClaim = wcm.getClaim(p.getChunk().getChunkKey());
        if (optClaim.isPresent()) {
            Message.prefixedText("This chunk is already &aclaimed&r!").to(p);
        } else {
            Claim c = new Claim(p.getChunk().getChunkKey(), p.getWorld().getName(), p.getUniqueId());
            wcm.addClaim(c);
            Message.prefixedText("You just &aclaimed &rthis chunk!").to(p);
        }
    }

    private void unclaimCommand(final @NonNull CommandContext<CommandSender> ctx) {
        Player p = (Player) ctx.getSender();
        WorldClaimManager wcm = ClaimManager.getInstance().getWorldClaimManager(p.getWorld());
        Optional<Claim> optClaim = wcm.getClaim(p.getChunk().getChunkKey());
        optClaim.ifPresentOrElse(claim -> {
            if (!claim.getOwner().equals(p.getUniqueId())) {
                Message.prefixedText("&cThis is not your claim! You can't unclaim it!").to(p);
                return;
            }
            wcm.removeClaim(claim);
            Message.prefixedText("You just &aunclaimed &rthis chunk!").to(p);
        }, () -> Message.prefixedText("This chunk is not &aclaimed!").to(p));
    }

    private void infoCommand(final @NonNull CommandContext<CommandSender> ctx) {
        Player p = (Player) ctx.getSender();
        WorldClaimManager wcm = ClaimManager.getInstance().getWorldClaimManager(p.getWorld());
        Optional<Claim> optClaim = wcm.getClaim(p.getChunk().getChunkKey());
        optClaim.ifPresentOrElse(claim -> {
            Chunk chunk = p.getWorld().getChunkAt(claim.getKey());
            String trustedMembers = claim.getTrusted().stream()
                    .map(Bukkit::getOfflinePlayer)
                    .map(OfflinePlayer::getName)
                    .collect(Collectors.joining(" + "));
            Message.text("&7&l==============================").to(p);
            Message.text("&a&lClaim: &r[" + chunk.getX() + ", " + chunk.getZ() + "]").to(p);
            Message.text("&a&lWorld: &r" + p.getWorld().getName()).to(p);
            Message.text("&a&lOwner: &r" + Optional.ofNullable(Bukkit.getOfflinePlayer(claim.getOwner()).getName()).orElse("None")).to(p);
            Message.text("&a&lTrusted Members: &r" + (trustedMembers.isEmpty() ? "None" : trustedMembers)).to(p);
            Message.text("&7&l==============================").to(p);
        }, () -> Message.prefixedText("You are not in a &aclaim&r!").to(p));
    }

    private void trustCommand(final @NonNull CommandContext<CommandSender> ctx) {
        Player p = (Player) ctx.getSender();
        WorldClaimManager wcm = ClaimManager.getInstance().getWorldClaimManager(p.getWorld());
        Optional<Claim> optClaim = wcm.getClaim(p.getChunk().getChunkKey());
        OfflinePlayer op = ctx.get("player");
        if (p.getUniqueId().equals(op.getUniqueId())) {
            Message.prefixedText("You can't &atrust &ryourself!").to(p);
            return;
        }
        optClaim.ifPresentOrElse(claim -> {
            if (!claim.getOwner().equals(p.getUniqueId())) {
                Message.prefixedText("This is not your &aclaim&r!").to(p);
                return;
            }
            if (claim.addTrustedMember(op.getUniqueId())) {
                Message.prefixedText("You just added &a" + op.getName() + "&r as a trusted member to this claim!").to(p);
            } else {
                Message.prefixedText("&a" + op.getName() + " &ris already a trusted member of this claim!").to(p);
            }
        }, () -> Message.prefixedText("You are not in a &aclaim&r!").to(p));
    }

    private void untrustCommand(final @NonNull CommandContext<CommandSender> ctx) {
        Player p = (Player) ctx.getSender();
        WorldClaimManager wcm = ClaimManager.getInstance().getWorldClaimManager(p.getWorld());
        Optional<Claim> optClaim = wcm.getClaim(p.getChunk().getChunkKey());

        optClaim.ifPresentOrElse(claim -> {
            OfflinePlayer op = ctx.get("player");
            if (!claim.getOwner().equals(p.getUniqueId())) {
                Message.prefixedText("This is not your &aclaim&r!").to(p);
                return;
            }
            if (claim.removeTrustedMember(op.getUniqueId())) {
                Message.prefixedText("You just removed &a" + op.getName() + " &ras a trusted member from this claim!").to(p);
            } else {
                Message.prefixedText("&a" + op.getName() + " &ris not a trusted member of this claim!").to(p);
            }
        }, () -> Message.prefixedText("You are not in a &aclaim&r!").to(p));
    }

}
