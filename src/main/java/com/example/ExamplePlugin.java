package com.example;

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
public class ExamplePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ExampleConfig config;

	private boolean teleporting = false;

	private static final Set<Integer> TELEPORT_SOUND_IDS = Set.of(
			SoundEffectID.TELEPORT_VWOOP,
			197,
			965
	);

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

		// For cowbell we need the previous behavior (some teleports require special handling)
		if (selected == TeleportAnimation.COWBELL)
		{
			teleporting = true;

			player.setAnimation(AnimationConstants.COWBELL_TELEPORT);
			player.setGraphic(AnimationConstants.COWBELL_TELEPORT_GRAPHIC);
			return;
		}

		// For other chosen animations, simply set the animation/graphic once.
		player.setAnimation(selected.getAnimationId());
		// If the chosen animation has associated graphics, set as needed
		if (selected == TeleportAnimation.SCROLL)
		{
			player.setGraphic(AnimationConstants.TELEPORT_SCROLLS_GRAPHIC);
		}
		else if (selected == TeleportAnimation.ECTOPHIAL)
		{
			player.setGraphic(AnimationConstants.ECTOPHIAL_TELEPORT_GRAPHIC);
		}
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

		if (arrivalSoundTicksRemaining >= 0)
		{
			if (arrivalSoundTicksRemaining == 0)
			{
				client.playSoundEffect(11286);
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

		return AnimationConstants.isTabTeleport(animationId) && config.overrideTabs();
	}

	@Subscribe
	public void onSoundEffectPlayed(SoundEffectPlayed event)
	{
		if (!config.muteTeleportSound())
			return;

		if (event.getSource() != client.getLocalPlayer())
			return;

		if (TELEPORT_SOUND_IDS.contains(event.getSoundId()))
		{
			event.consume();
		}
	}

	@Subscribe
	public void onAreaSoundEffectPlayed(AreaSoundEffectPlayed event)
	{
		if (!config.muteTeleportSound())
			return;

		if (TELEPORT_SOUND_IDS.contains(event.getSoundId()))
		{
			event.consume();
		}
	}

	@Provides
	ExampleConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ExampleConfig.class);
	}
}
