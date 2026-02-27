package com.strikernz.tpreplacer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("cowbell")
public interface TpreplacerConfig extends Config
{
	@ConfigItem(
		keyName = "overrideAnimationType",
		name = "Override With",
		description = "Select which teleport animation to use when overriding teleports",
		position = 0
	)
	default TeleportAnimation teleportAnimation()
	{
		return TeleportAnimation.COWBELL;
	}

	@ConfigItem(
		keyName = "overrideNormalandJewellery",
		name = "Normal Teleports and Jewellery",
		description = "Replace normal spellbook teleport animations and Jewellery (varrock, lumbridge, etc.)",
		position = 1
	)
	default boolean overrideNormal()
	{
		return true;
	}

	@ConfigItem(
		keyName = "overrideAncient",
		name = "Ancient Spellbook Teleports",
		description = "Replace ancient spellbook teleport animations",
		position = 2
	)
	default boolean overrideAncient()
	{
		return true;
	}

	@ConfigItem(
		keyName = "overrideArceuus",
		name = "Arceuus Teleports",
		description = "Replace Arceuus spellbook teleport animations",
		position = 3
	)
	default boolean overrideArceuus()
	{
		return true;
	}

	@ConfigItem(
		keyName = "overrideLunar",
		name = "Lunar Teleports",
		description = "Replace Lunar spellbook teleport animations",
		position = 4
	)
	default boolean overrideLunar()
	{
		return true;
	}
	
	@ConfigItem(
		keyName = "overrideTabs",
		name = "Teleport Tabs",
		description = "Replace teleport tab animations",
		position = 5
	)
	default boolean overrideTabs()
	{
		return true;
	}

	@ConfigItem(
		keyName = "overrideScrolls",
		name = "Teleport Scrolls",
		description = "Replace teleport scroll animations",
		position = 6
	)
	default boolean overrideScrolls() {return true;}

	@ConfigItem(
		keyName = "muteTeleportSound",
		name = "Mute Teleport Sound",
		description = "Suppress the teleport sound when the plugin replaces the animation",
		position = 7
	)
	default boolean muteTeleportSound()
	{
		return true;
	}

	@ConfigItem(
		keyName = "overrideEctophial",
		name = "Ectophial Teleports",
		description = "Replace Ectophial teleport animations",
		position = 8
	)
	default boolean overrideEctophial()
	{
		return true;
	}

	@ConfigItem(
		keyName = "overrideArdougne",
		name = "Ardougne Cape Teleports",
		description = "Replace Ardougne cape teleport animations",
		position = 9
	)
	default boolean overrideArdougne()
	{
		return true;
	}
}
