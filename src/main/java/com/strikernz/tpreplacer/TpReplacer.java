package com.strikernz.tpreplacer;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.SoundEffectID;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.SoundEffectPlayed;
import net.runelite.api.events.AreaSoundEffectPlayed;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.Set;

@PluginDescriptor(
		name = "Cowbell Teleport",
		description = "Replace teleport animations with the cowbell amulet teleport animation",
		tags = {"animation", "teleport"}
)
public class TpReplacer extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private TpreplacerConfig config;

	private boolean teleporting = false;

	private static final Set<Integer> TELEPORT_SOUND_IDS = Set.of(
			SoundEffectID.TELEPORT_VWOOP,
			197,
			965
	);

	// All sounds that the plugin may play for overrides (useful to avoid double-ups and to mute originals)
	private static final Set<Integer> OVERRIDE_SOUND_IDS;

	static
	{
		java.util.Set<Integer> tmp = new java.util.HashSet<>();
		// Add values; duplicates are tolerated by HashSet
		tmp.add(AnimationConstants.COWBELL_ARRIVAL_SOUND);
		tmp.add(AnimationConstants.STANDARD_TELEPORT_SOUND);
		tmp.add(AnimationConstants.ANCIENT_TELEPORT_SOUND);
		tmp.add(AnimationConstants.ARCEUUS_TELEPORT_SOUND);
		tmp.add(AnimationConstants.LUNAR_TELEPORT_SOUND);
		tmp.add(AnimationConstants.TAB_TELEPORT_SOUND);
		tmp.add(AnimationConstants.TELEPORT_SCROLLS_SOUND);
		tmp.add(AnimationConstants.ECTOPHIAL_TELEPORT_SOUND);
		tmp.add(AnimationConstants.ARDOUGNE_TELEPORT_SOUND);
		OVERRIDE_SOUND_IDS = java.util.Collections.unmodifiableSet(tmp);
	}

	// Track last played sound to avoid playing the same sound multiple times in the same tick
	private int lastPlayedSoundId = -1;
	private int lastPlayedSoundTick = -1;

	// Map: originalSoundId -> expireTick (inclusive). We remove expired entries each tick.
	private final java.util.Map<Integer, Integer> mutedSoundUntilTick = new java.util.HashMap<>();

	@Override
	protected void startUp()
	{
		teleporting = false;
	}

	@Override
	protected void shutDown()
	{
		teleporting = false;
	}

	private int arrivalSoundTicksRemaining = -1;

	private static final int ARRIVAL_SOUND_DELAY_TICKS = 2;

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (event.getActor() != client.getLocalPlayer())
			return;

		Player player = client.getLocalPlayer();
		int animationId = player.getAnimation();

		// Handle the special cowbell flow where we wait for animation -1 to set arrival
		handleCowbellArrival(player, animationId);

		// If selected override is NONE, do nothing
		TeleportAnimation selected = config.teleportAnimation();
		if (selected == TeleportAnimation.NONE)
			return;

		// If the player is already playing the selected animation, nothing to do
		if (animationId == selected.getAnimationId())
			return;

		// Only react to known teleport animations
		if (!AnimationConstants.isTeleportAnimation(animationId))
			return;

		// Respect granular config toggles as before
		if (!shouldOverride(animationId))
			return;

		// Figure out the original sound for this teleport so we can mute it if needed
		int originalSound = getOriginalSoundForTeleport(animationId);

		// For cowbell we need the previous behavior (some teleports require special handling)
		if (selected == TeleportAnimation.COWBELL)
		{
			teleporting = true;

			// If muting is enabled, mark the original sound to be muted for the next tick
			if (originalSound != -1 && config.muteTeleportSound())
			{
				mutedSoundUntilTick.put(originalSound, client.getTickCount() + 1);
			}

			player.setAnimation(AnimationConstants.COWBELL_TELEPORT);
			player.setGraphic(AnimationConstants.COWBELL_TELEPORT_GRAPHIC);
			return;
		}

		// For other chosen animations, simply set the animation/graphic once.
		player.setAnimation(selected.getAnimationId());

		// If the chosen animation has associated graphics, set as needed and mark original sound to mute
		if (selected == TeleportAnimation.SCROLL)
		{
			if (AnimationConstants.TELEPORT_SCROLLS_GRAPHIC != -1)
			{
				player.setGraphic(AnimationConstants.TELEPORT_SCROLLS_GRAPHIC);
			}
			if (originalSound != -1 && config.muteTeleportSound())
			{
				mutedSoundUntilTick.put(originalSound, client.getTickCount() + 1);
			}
			if (AnimationConstants.TELEPORT_SCROLLS_SOUND != -1)
			{
				playSoundOnce(AnimationConstants.TELEPORT_SCROLLS_SOUND);
			}
		}
		else if (selected == TeleportAnimation.ECTOPHIAL)
		{
			if (AnimationConstants.ECTOPHIAL_TELEPORT_GRAPHIC != -1)
			{
				player.setGraphic(AnimationConstants.ECTOPHIAL_TELEPORT_GRAPHIC);
			}
			if (originalSound != -1 && config.muteTeleportSound())
			{
				mutedSoundUntilTick.put(originalSound, client.getTickCount() + 1);
			}
			if (AnimationConstants.ECTOPHIAL_TELEPORT_SOUND != -1)
			{
				playSoundOnce(AnimationConstants.ECTOPHIAL_TELEPORT_SOUND);
			}
		}
		else if (selected == TeleportAnimation.STANDARD)
		{
			if (AnimationConstants.STANDARD_TELEPORT_GRAPHIC != -1)
			{
				player.setGraphic(AnimationConstants.STANDARD_TELEPORT_GRAPHIC);
			}
			if (originalSound != -1 && config.muteTeleportSound())
			{
				mutedSoundUntilTick.put(originalSound, client.getTickCount() + 1);
			}
			if (AnimationConstants.STANDARD_TELEPORT_SOUND != -1)
			{
				playSoundOnce(AnimationConstants.STANDARD_TELEPORT_SOUND);
			}
		}
		else if (selected == TeleportAnimation.ANCIENT)
		{
			if (AnimationConstants.ANCIENT_TELEPORT_GRAPHIC != -1)
			{
				player.setGraphic(AnimationConstants.ANCIENT_TELEPORT_GRAPHIC);
			}
			if (originalSound != -1 && config.muteTeleportSound())
			{
				mutedSoundUntilTick.put(originalSound, client.getTickCount() + 1);
			}
			if (AnimationConstants.ANCIENT_TELEPORT_SOUND != -1)
			{
				playSoundOnce(AnimationConstants.ANCIENT_TELEPORT_SOUND);
			}
		}
		else if (selected == TeleportAnimation.ARCEUUS)
		{
			if (AnimationConstants.ARCEUUS_TELEPORT_GRAPHIC != -1)
			{
				player.setGraphic(AnimationConstants.ARCEUUS_TELEPORT_GRAPHIC);
			}
			if (originalSound != -1 && config.muteTeleportSound())
			{
				mutedSoundUntilTick.put(originalSound, client.getTickCount() + 1);
			}
			if (AnimationConstants.ARCEUUS_TELEPORT_SOUND != -1)
			{
				playSoundOnce(AnimationConstants.ARCEUUS_TELEPORT_SOUND);
			}
		}
		else if (selected == TeleportAnimation.LUNAR)
		{
			if (AnimationConstants.LUNAR_TELEPORT_GRAPHIC != -1)
			{
				player.setGraphic(AnimationConstants.LUNAR_TELEPORT_GRAPHIC);
			}
			if (originalSound != -1 && config.muteTeleportSound())
			{
				mutedSoundUntilTick.put(originalSound, client.getTickCount() + 1);
			}
			if (AnimationConstants.LUNAR_TELEPORT_SOUND != -1)
			{
				playSoundOnce(AnimationConstants.LUNAR_TELEPORT_SOUND);
			}
		}
		else if (selected == TeleportAnimation.TAB)
		{
			if (AnimationConstants.TAB_TELEPORT_GRAPHIC != -1)
			{
				player.setGraphic(AnimationConstants.TAB_TELEPORT_GRAPHIC);
			}
			if (originalSound != -1 && config.muteTeleportSound())
			{
				mutedSoundUntilTick.put(originalSound, client.getTickCount() + 1);
			}
			if (AnimationConstants.TAB_TELEPORT_SOUND != -1)
			{
				playSoundOnce(AnimationConstants.TAB_TELEPORT_SOUND);
			}
		}
		else if (selected == TeleportAnimation.ARDOUGNE)
		{
			if (AnimationConstants.ARDOUGNE_TELEPORT_GRAPHIC != -1)
			{
				player.setGraphic(AnimationConstants.ARDOUGNE_TELEPORT_GRAPHIC);
			}
			if (originalSound != -1 && config.muteTeleportSound())
			{
				mutedSoundUntilTick.put(originalSound, client.getTickCount() + 1);
			}
			if (AnimationConstants.ARDOUGNE_TELEPORT_SOUND != -1)
			{
				playSoundOnce(AnimationConstants.ARDOUGNE_TELEPORT_SOUND);
			}
		}
	}

	private void playSoundOnce(int soundId)
	{
		if (soundId == -1)
			return;

		int tick = client.getTickCount();
		if (soundId == lastPlayedSoundId && tick == lastPlayedSoundTick)
			return;

		// Record before playing so synchronous SoundEffectPlayed events aren't consumed
		lastPlayedSoundId = soundId;
		lastPlayedSoundTick = tick;

		client.playSoundEffect(soundId);
	}

	private int getOriginalSoundForTeleport(int animationId)
	{
		if (AnimationConstants.isModernTeleport(animationId))
			return AnimationConstants.STANDARD_TELEPORT_SOUND;
		if (AnimationConstants.isAncientTeleport(animationId))
			return AnimationConstants.ANCIENT_TELEPORT_SOUND;
		if (AnimationConstants.isArceuusTeleport(animationId))
			return AnimationConstants.ARCEUUS_TELEPORT_SOUND;
		if (AnimationConstants.isLunarTeleport(animationId))
			return AnimationConstants.LUNAR_TELEPORT_SOUND;
		if (AnimationConstants.isTabTeleport(animationId))
			return AnimationConstants.TAB_TELEPORT_SOUND;
		if (AnimationConstants.isTeleportScroll(animationId))
			return AnimationConstants.TELEPORT_SCROLLS_SOUND;
		if (AnimationConstants.isEctophialTeleport(animationId))
			return AnimationConstants.ECTOPHIAL_TELEPORT_SOUND;
		if (AnimationConstants.isArdougneTeleport(animationId))
			return AnimationConstants.ARDOUGNE_TELEPORT_SOUND;
		return -1;
	}

	private void handleCowbellArrival(Player player, int animationId)
	{
		if (teleporting && animationId == -1)
		{
			teleporting = false;

			player.setAnimation(AnimationConstants.COWBELL_TELEPORT);
			player.setGraphic(AnimationConstants.COWBELL_TELEPORT_GRAPHIC);

			arrivalSoundTicksRemaining = ARRIVAL_SOUND_DELAY_TICKS;
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		Player player = client.getLocalPlayer();
		if (player == null)
			return;

		// expire muted sound entries older than current tick
		int tick = client.getTickCount();
		mutedSoundUntilTick.values().removeIf(expire -> expire < tick);

		if (arrivalSoundTicksRemaining >= 0)
		{
			if (arrivalSoundTicksRemaining == 0)
			{
				// always play the cowbell arrival sound for the override regardless of mute setting
				if (AnimationConstants.COWBELL_ARRIVAL_SOUND != -1)
				{
					playSoundOnce(AnimationConstants.COWBELL_ARRIVAL_SOUND);
				}
				arrivalSoundTicksRemaining = -1;
			}
			else
			{
				arrivalSoundTicksRemaining--;
			}
		}
	}

	private boolean shouldOverride(int animationId)
	{
		if (AnimationConstants.isModernTeleport(animationId) && config.overrideNormal())
			return true;

		if (AnimationConstants.isAncientTeleport(animationId) && config.overrideAncient())
			return true;

		if (AnimationConstants.isArceuusTeleport(animationId) && config.overrideArceuus())
			return true;

		if (AnimationConstants.isLunarTeleport(animationId) && config.overrideLunar())
			return true;

		if (AnimationConstants.isTeleportScroll(animationId) && config.overrideScrolls())
			return true;

		if (AnimationConstants.isEctophialTeleport(animationId) && config.overrideEctophial())
			return true;

		if (AnimationConstants.isArdougneTeleport(animationId) && config.overrideArdougne())
			return true;

		return AnimationConstants.isTabTeleport(animationId) && config.overrideTabs();
	}

	@Subscribe
	public void onSoundEffectPlayed(SoundEffectPlayed event)
	{
		if (!config.muteTeleportSound())
			return;

		if (event.getSource() != client.getLocalPlayer())
			return;

		int soundId = event.getSoundId();
		int tick = client.getTickCount();

		// If this is the same sound the plugin just played this tick, don't consume it
		if (soundId == lastPlayedSoundId && tick == lastPlayedSoundTick)
			return;

		if (mutedSoundUntilTick.containsKey(soundId) && mutedSoundUntilTick.get(soundId) >= tick)
		{
			event.consume();
		}
	}

	@Subscribe
	public void onAreaSoundEffectPlayed(AreaSoundEffectPlayed event)
	{
		if (!config.muteTeleportSound())
			return;

		int soundId = event.getSoundId();
		int tick = client.getTickCount();

		// If this is the same sound the plugin just played this tick, don't consume it
		if (soundId == lastPlayedSoundId && tick == lastPlayedSoundTick)
			return;

		if (mutedSoundUntilTick.containsKey(soundId) && mutedSoundUntilTick.get(soundId) >= tick)
		{
			event.consume();
		}
	}

	@Provides
	TpreplacerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TpreplacerConfig.class);
	}
}
