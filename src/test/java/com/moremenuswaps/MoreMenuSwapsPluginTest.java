package com.moremenuswaps;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class MoreMenuSwapsPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(MoreMenuSwapsPlugin.class);
		RuneLite.main(args);
	}
}