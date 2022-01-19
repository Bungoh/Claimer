package com.bungoh.claimer.text;

import com.bungoh.claimer.Claimer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Message {

    @Nullable String get();

    @Override
    @NotNull String toString();

    /**
     * Construct a MessageSender utility for this Message.
     * <p>
     * Translates colors.
     *
     * @return a new MessageSender
     */
    default MessageSender send() {
        return new MessageSender(() -> TextColors.translate(Message.this.toString()));
    }

    /**
     * Construct a MessageSender utility for this Message, applying the prefix.
     * <p>
     * Translates colors.
     *
     * @return a new MessageSender
     * @see Claimer.MainConfig#getPrefix()
     */
    default MessageSender prefixed() {
        return new MessageSender(() -> TextColors.translate(Claimer.MainConfig.getPrefix() + "&r " + Message.this.toString()));
    }

    /**
     * Construct a MessageSender utility for text with no color translation.
     *
     * @param text raw text
     * @return a new MessageSender
     */
    static MessageSender raw(@NotNull String text) {
        return new MessageSender(() -> text);
    }

    /**
     * Construct a MessageSender Utility for basic text.
     * <p>
     * Translates colors.
     *
     * @param text a string
     * @return a new MessageSender
     */
    static MessageSender text(@NotNull String text) {
        return new MessageSender(() -> TextColors.translate(text));
    }

    /**
     * Construct a MessageSender Utility for text, applying the prefix.
     * <p>
     * Translates colors.
     *
     * @param text a string
     * @return a new MessageSender
     * @see Claimer.MainConfig#getPrefix()
     */
    static MessageSender prefixedText(String text) {
        return new MessageSender(() -> TextColors.translate(Claimer.MainConfig.getPrefix() + "&r " + text));
    }

    /**
     * Wraps an outgoing message, providing various recipient types.
     */
    @SuppressWarnings("UnusedReturnValue")
    class MessageSender {

        private final Supplier<String> message;

        MessageSender(Supplier<String> message) {
            this.message = message;
        }

        /**
         * Send this message to a player.
         *
         * @param player a player
         * @return this wrapper
         */
        public MessageSender to(Player player) {
            player.sendMessage(message.get());
            return this;
        }

        /**
         * Send this message to the console.
         *
         * @return this wrapper
         */
        public MessageSender toConsole() {
            Bukkit.getConsoleSender().sendMessage(message.get());
            return this;
        }

        /**
         * Send this message to CommandSenders.
         *
         * @param recipients varargs of CommandSenders
         * @return this wrapper
         */
        public MessageSender toRecipients(CommandSender... recipients) {
            for (CommandSender sender : recipients) {
                sender.sendMessage(message.get());
            }
            return this;
        }

        /**
         * Send this message to a Consumer.
         * <p>
         * This method might be useful if you already have a method that
         * accepts strings to send on to players/participants.
         *
         * @param consumer a function that accepts a String
         * @return this wrapper
         */
        public MessageSender toConsumer(Consumer<String> consumer) {
            consumer.accept(message.get());
            return this;
        }
    }

}
