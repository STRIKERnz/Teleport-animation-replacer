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

		if (teleporting && animationId == -1)
		{
			teleporting = false;

			player.setAnimation(AnimationConstants.COWBELL_TELEPORT);
			player.setGraphic(AnimationConstants.COWBELL_TELEPORT_GRAPHIC);

			arrivalSoundTicksRemaining = ARRIVAL_SOUND_DELAY_TICKS;
			return;
		}

		if (animationId == AnimationConstants.COWBELL_TELEPORT)
			return;

		if (!AnimationConstants.isTeleportAnimation(animationId))
			return;

		if (!shouldOverride(animationId))
			return;

		teleporting = true;

		player.setAnimation(AnimationConstants.COWBELL_TELEPORT);
		player.setGraphic(AnimationConstants.COWBELL_TELEPORT_GRAPHIC);
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
