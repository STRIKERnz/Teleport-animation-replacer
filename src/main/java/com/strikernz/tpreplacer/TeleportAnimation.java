package com.strikernz.tpreplacer;

public enum TeleportAnimation
{
	NONE(-1, "None"),
	COWBELL(AnimationConstants.COWBELL_TELEPORT, "Cowbell Amulet"),
	STANDARD(AnimationConstants.STANDARD_AND_JEWELLERY_TELEPORT, "Standard / Jewellery"),
	ANCIENT(AnimationConstants.ANCIENT_TELEPORT, "Ancient"),
	ARCEUUS(AnimationConstants.ARCEUUS_TELEPORT, "Arceuus"),
	LUNAR(AnimationConstants.LUNAR_TELEPORT, "Lunar"),
	TAB(AnimationConstants.TAB_TELEPORT, "Tab"),
	SCROLL(AnimationConstants.TELEPORT_SCROLLS, "Scroll"),
	ECTOPHIAL(AnimationConstants.ECTOPHIAL_TELEPORT, "Ectophial"),
	ARDOUGNE(AnimationConstants.ARDOUGNE_TELEPORT, "Ardougne Cape");

	private final int animationId;
	private final String name;

	TeleportAnimation(int animationId, String name)
	{
		this.animationId = animationId;
		this.name = name;
	}

	public int getAnimationId()
	{
		return animationId;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
