package com.example;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@PluginDescriptor(
		name = "Cowbell Teleport",
		description = "Replace teleport animations with the cowbell amulet teleport animation",
		tags = {"animation", "teleport"}
)
public class ExamplePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ExampleConfig config;

	// Simple flag to know when a teleport is in progress
	private boolean teleporting = false;

	private static final Set<Integer> TELEPORT_SOUND_IDS = Set.of(
			SoundEffectID.TELEPORT_VWOOP,
			197, // Ancient teleport group sound
			965
	);

	@Override
	protected void startUp()
	{
		log.debug("Cowbell Teleport started");
		teleporting = false;
	}

	@Override
	protected void shutDown()
	{
		log.debug("Cowbell Teleport stopped");
		teleporting = false;
	}

	// Flag to schedule the arrival effect
	private boolean scheduleArrivalEffect = false;

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (!config.enabled())
			return;

		if (event.getActor() != client.getLocalPlayer())
			return;

		Player player = client.getLocalPlayer();
		int animationId = player.getAnimation();

		// ---- Arrival Detection ----
		if (teleporting && animationId == -1)
		{
			teleporting = false;

			// Replay cowbell animation
			player.setAnimation(config.cowbellAnimationId());

			// Schedule graphic + sound for next tick
			if (config.showTeleportGraphic())
			{
				scheduleArrivalEffect = true;
			}

			log.debug("Scheduled arrival milk splash and sound");
			return;
		}

		// Prevent overriding our own animation
		if (animationId == config.cowbellAnimationId())
			return;

		// ---- Teleport Start Detection ----
		if (!AnimationConstants.isTeleportAnimation(animationId))
			return;

		if (!shouldOverride(animationId))
			return;

		teleporting = true;

		// Replace animation
		player.setAnimation(config.cowbellAnimationId());

		// Show teleport graphic immediately at start
		if (config.showTeleportGraphic())
		{
			player.setGraphic(config.cowbellGraphicId());
		}

		log.debug("Replaced teleport animation {}", animationId);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (!scheduleArrivalEffect)
			return;

		Player player = client.getLocalPlayer();
		if (player == null)
			return;

		// Play cowbell graphic
		player.setGraphic(config.cowbellGraphicId());

		// Play cowbell arrival sound
		client.playSoundEffect(11286);

		scheduleArrivalEffect = false;
		log.debug("Played cowbell graphic and sound on arrival tick");
	}


	private boolean shouldOverride(int animationId)
	{
		if (AnimationConstants.isModernTeleport(animationId) && config.overrideModern())
			return true;

		if (AnimationConstants.isAncientTeleport(animationId) && config.overrideAncient())
			return true;

		if (AnimationConstants.isArceuusTeleport(animationId) && config.overrideArceuus())
			return true;

		if (AnimationConstants.isLunarTeleport(animationId) && config.overrideLunar())
			return true;

		return AnimationConstants.isTabTeleport(animationId) && config.overrideTabs();
	}

	@Subscribe
	public void onSoundEffectPlayed(SoundEffectPlayed event)
	{
		if (!config.enabled() || !config.muteTeleportSound())
			return;

		if (event.getSource() != client.getLocalPlayer())
			return;

		if (TELEPORT_SOUND_IDS.contains(event.getSoundId()))
		{
			event.consume();
			log.debug("Muted teleport sound {}", event.getSoundId());
		}
	}

	@Subscribe
	public void onAreaSoundEffectPlayed(AreaSoundEffectPlayed event)
	{
		if (!config.enabled() || !config.muteTeleportSound())
			return;

		if (TELEPORT_SOUND_IDS.contains(event.getSoundId()))
		{
			event.consume();
			log.debug("Muted area teleport sound {}", event.getSoundId());
		}
	}

	@Provides
	ExampleConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ExampleConfig.class);
	}
}
