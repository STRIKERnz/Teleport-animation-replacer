package com.example;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("cowbell")
public interface ExampleConfig extends Config
{

	@ConfigItem(
		keyName = "overrideNormalandJewellery",
		name = "Normal Teleports and Jewellery",
		description = "Replace normal spellbook teleport animations and Jewellery (varrock, lumbridge, etc.)"
	)
	default boolean overrideNormal()
	{
		return true;
	}

	@ConfigItem(
		keyName = "overrideAncient",
		name = "Ancient Spellbook Teleports",
		description = "Replace ancient spellbook teleport animations"
	)
	default boolean overrideAncient()
	{
		return true;
	}

	@ConfigItem(
		keyName = "overrideArceuus",
		name = "Arceuus Teleports",
		description = "Replace Arceuus spellbook teleport animations"
	)
	default boolean overrideArceuus()
	{
		return true;
	}

	@ConfigItem(
		keyName = "overrideLunar",
		name = "Lunar Teleports",
		description = "Replace Lunar spellbook teleport animations"
	)
	default boolean overrideLunar()
	{
		return true;
	}
	
	@ConfigItem(
		keyName = "overrideTabs",
		name = "Teleport Tabs",
		description = "Replace teleport tab animations"
	)
	default boolean overrideTabs()
	{
		return true;
	}

	@ConfigItem(
		keyName = "overrideScrolls",
		name = "Teleport Scrolls",
		description = "Replace teleport scroll animations"
	)
	default boolean overrideScrolls() {return true;}

	@ConfigItem(
		keyName = "muteTeleportSound",
		name = "Mute Teleport Sound",
		description = "Suppress the teleport sound when the plugin replaces the animation"
	)
	default boolean muteTeleportSound()
	{
		return true;
	}

	@ConfigItem(
		keyName = "overrideAnimationType",
		name = "Override With",
		description = "Select which teleport animation to use when overriding teleports"
	)
	default TeleportAnimation teleportAnimation()
	{
		return TeleportAnimation.COWBELL;
	}

	@ConfigItem(
		keyName = "overrideEctophial",
		name = "Ectophial Teleports",
		description = "Replace Ectophial teleport animations"
	)
	default boolean overrideEctophial()
	{
		return true;
	}
}
