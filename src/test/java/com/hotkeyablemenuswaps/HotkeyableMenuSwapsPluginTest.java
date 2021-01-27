package com.hotkeyablemenuswaps;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class HotkeyableMenuSwapsPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(HotkeyableMenuSwapsPlugin.class);
		RuneLite.main(args);
	}
}