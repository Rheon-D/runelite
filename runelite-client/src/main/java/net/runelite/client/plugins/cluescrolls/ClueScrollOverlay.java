/*
 * Copyright (c) 2016-2017, Seth <Sethtroll3@gmail.com>
 * Copyright (c) 2018, Lotto <https://github.com/devLotto>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.cluescrolls;

import com.google.common.base.MoreObjects;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Item;
import net.runelite.client.plugins.cluescrolls.clues.ClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.emote.ItemRequirement;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class ClueScrollOverlay extends Overlay
{
	public static final Color TITLED_CONTENT_COLOR = new Color(190, 190, 190);
	private static final Item[] EMPTY = new Item[0];

	private final ClueScrollPlugin plugin;
	private final PanelComponent panelComponent = new PanelComponent();


	@Inject
	public ClueScrollOverlay(ClueScrollPlugin plugin)
	{
		this.plugin = plugin;
		setPriority(OverlayPriority.LOW);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		ClueScroll clue = plugin.getClue();

		if (clue == null)
		{
			return null;
		}

		panelComponent.getChildren().clear();

		clue.makeOverlayHint(panelComponent, plugin);

		if (clue.itemRequirements().length > 0)
		{
			Item[] inventory = MoreObjects.firstNonNull(plugin.getInventoryItems(), EMPTY);
			Item[] equipment = MoreObjects.firstNonNull(plugin.getEquippedItems(), EMPTY);

			for (ItemRequirement requirement : clue.itemRequirements())
			{
				if (!requirement.fulfilledBy(plugin.getClient(), inventory) && !requirement.fulfilledBy(plugin.getClient(), equipment))
				{
					panelComponent.getChildren().add(LineComponent.builder()
						.leftColor(Color.RED)
						.left(String.format("Missing %s", requirement.getCollectiveName(plugin.getClient())))
						.build());
				}
			}
		}

		return panelComponent.render(graphics);
	}
}
