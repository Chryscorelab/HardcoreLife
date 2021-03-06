package hardcorelife.chryscorelab.helpers;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import hardcorelife.chryscorelab.Touchy;

import java.util.HashMap;
import java.util.UUID;

public class PlayerLife {

    private static Touchy touchy = Touchy.get();
    private static HashMap<UUID, Integer> lives = new HashMap<>();

    public static void initPlayer(Player player) {
        // Triggers the player data to be loaded from config.yml
        getLives(player);
    }

    public static void revivePlayer(Player player) {
        // Force-respawn a player without altering their life count
        // Mainly used when reviving a player who experiences permadeath
        player.setGameMode(GameMode.SURVIVAL);

        addLife(player);
        player.setHealth(0);

        // Re-enable movement
        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.1f);
    }

    public static int getLives(Player player) {
        // Get the current number of lives for a player
        // Will pull player data from the config, if not already loaded
        if (!lives.containsKey(getUUID(player))) {
            setLives(getUUID(player), touchy.getPlayerLivesConfig(getUUID(player)));
        }
        return lives.get(getUUID(player));
    }

    public static int getServerLives() {
        if (!lives.containsKey(getUUID())) {
            setLives(getUUID(), touchy.getPlayerLivesConfig(getUUID()));
        }
        return lives.get(getUUID());
    }

    public static int addServerLife() {
        // Adds a life to the server
        int finalLives = getServerLives() + 1;
        forceSetServerLives(finalLives);
        return finalLives;
    }

    public static void forceSetServerLives(int lifeCount) {
        if (lifeCount < 0) {
            lifeCount = 0;
        }
        getServerLives(); // Ensure life configs are loaded
        lives.put(getUUID(), lifeCount);
        touchy.savePlayerLifeConfig(getUUID(), lifeCount);
    }

    public static int addLife(Player player) {
        // Adds a life to a player
        int finalLives = getLives(player) + 1;
        setLives(getUUID(player), finalLives);
        return finalLives;
    }

    public static int removeLife(Player player) {
        // Removes a life from a player
        int finalLives = getLives(player) - 1;
        setLives(getUUID(player), finalLives);
        return finalLives;
    }

    public static void forceSetLives(Player player, int lifeCount) {
        // Force set life value
        setLives(getUUID(player), lifeCount);
    }

    private static void setLives(UUID uuid, int lifeCount) {
        // Updates the player's life count, then saves configs
        if (lifeCount < 0) {
            lifeCount = 0;
        }
        lives.put(uuid, lifeCount);
        touchy.savePlayerLifeConfig(uuid, lifeCount);
    }

    public static void saveLivesData() {
        // Saves lives object to disk
        touchy.saveHashmapData(lives);
    }

    public static void clearLivesData() {
        // Clears all in-memory life data. Will be reloaded from disk
        lives.clear();
    }

    public static HashMap<UUID, Integer> getLivesMap() {
        return lives;
    }

    private static UUID getUUID(Player player) {
        // Wrapper to handle when global lives are enabled
        if (touchy.globalLivesEnabled())
            return new UUID(0, 0);
        else
            return player.getUniqueId();
    }

    private static UUID getUUID() {
        return new UUID(0, 0);
    }

}
