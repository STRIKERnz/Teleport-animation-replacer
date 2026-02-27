package com.example;

import java.util.Set;

public final class AnimationConstants
{
	public static final int COWBELL_TELEPORT = 13811;
	public static final int COWBELL_TELEPORT_GRAPHIC = 3603;
	public static final int STANDARD_AND_JEWELLERY_TELEPORT = 714;
	public static final int ANCIENT_TELEPORT = 1979;
	public static final int ARCEUUS_TELEPORT = 3865;
	public static final int LUNAR_TELEPORT = 1816;
	public static final int TAB_TELEPORT = 4071;
	public static final int TELEPORT_SCROLLS = 3864;
	public static final int TELEPORT_SCROLLS_GRAPHIC = 111;
	public static final int ECTOPHIAL_TELEPORT = 878;
	public static final int ECTOPHIAL_TELEPORT_GRAPHIC = 1273;



	private static final Set<Integer> ALL_TELEPORTS = Set.of(
			STANDARD_AND_JEWELLERY_TELEPORT,
			ANCIENT_TELEPORT,
			ARCEUUS_TELEPORT,
			LUNAR_TELEPORT,
			TAB_TELEPORT,
			TELEPORT_SCROLLS,
			ECTOPHIAL_TELEPORT
	);

	private AnimationConstants() {}

	public static boolean isTeleportAnimation(int animationId)
	{
		return ALL_TELEPORTS.contains(animationId);
	}

	public static boolean isModernTeleport(int animationId)
	{
		return animationId == STANDARD_AND_JEWELLERY_TELEPORT;
	}

	public static boolean isAncientTeleport(int animationId)
	{
		return animationId == ANCIENT_TELEPORT;
	}

	public static boolean isArceuusTeleport(int animationId)
	{
		return animationId == ARCEUUS_TELEPORT;
	}

	public static boolean isLunarTeleport(int animationId)
	{
		return animationId == LUNAR_TELEPORT;
	}

	public static boolean isTabTeleport(int animationId)
	{
		return animationId == TAB_TELEPORT;
	}

	public static boolean isTeleportScroll(int animationId){ return animationId == TELEPORT_SCROLLS;}

	public static boolean isEctophialTeleport(int animationId){ return animationId == ECTOPHIAL_TELEPORT;}

}