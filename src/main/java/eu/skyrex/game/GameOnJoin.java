package eu.skyrex.game;

import eu.skyrex.Main;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.scoreboard.TeamManager;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.UUID;

public class GameOnJoin implements EventListener<PlayerSpawnEvent> {

    UUID resourcePackId = UUID.randomUUID();
    private final TeamManager manager = new TeamManager();
    private final Team team = manager.createBuilder("paintit")
            .seeInvisiblePlayers()
            .build();

    @Override
    public @NotNull Class<PlayerSpawnEvent> eventType() {
        return PlayerSpawnEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerSpawnEvent event) {
        if(!event.isFirstSpawn()) return Result.INVALID;

        event.getPlayer().addEffect(new Potion(PotionEffect.RESISTANCE, (byte) 127, Potion.INFINITE_DURATION));
        Main.getGameManager().addToSidebar(event.getPlayer());
        Main.getGameManager().sendMessageToAllPlayers("<yellow>"+event.getPlayer().getUsername() + " joined the game!");
        event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(
                "<blue>Welcome to PaintIT! This is a game like skribbl.io, but in Minecraft! You can draw, guess, and have fun!"));
        if(!Main.getGameManager().isGameStarted()) {
            event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(
                    "<green>The game has not started yet. You can start it by typing /game start"));
        } else {
            event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(
                    "<green>The game has already started, you just automatically join. Good luck!"));
        }

        event.getPlayer().sendPacket(team.createTeamsCreationPacket());
        event.getPlayer().setInvisible(true);
        team.addMember(((TextComponent)event.getPlayer().getName()).content());

        URI uri = URI.create("https://download.mc-packs.net/pack/419bbf0bfd629fcbbfa9aff111bab629a7f7e1d4.zip");
        ResourcePackInfo pack = ResourcePackInfo.resourcePackInfo(resourcePackId, uri, "419bbf0bfd629fcbbfa9aff111bab629a7f7e1d4");
        event.getPlayer().sendResourcePacks(ResourcePackRequest.resourcePackRequest().packs(pack));
        return Result.SUCCESS;
    }
}
