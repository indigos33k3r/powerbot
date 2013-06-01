package org.powerbot.script.randoms;

import org.powerbot.script.Manifest;
import org.powerbot.script.PollingScript;
import org.powerbot.script.methods.Game;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Item;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Widget;
import org.powerbot.util.Tracker;

@Manifest(name = "Spin ticket destroyer", authors = {"Timer"}, description = "Claims or destroys spin tickets")
public class TicketDestroy extends PollingScript implements RandomEvent {
	private static final int[] ITEM_IDS = {24154, 24155};

	@Override
	public int poll() {
		if (!world.game.isLoggedIn() || world.game.getCurrentTab() != Game.TAB_INVENTORY) return 600;
		final Player player;
		if ((player = world.players.getLocal()) == null ||
				player.isInCombat() || player.getAnimation() != -1 || player.getInteracting() != null) return 600;
		final Item item = world.inventory.getItem(ITEM_IDS);
		if (item != null) {
			Tracker.getInstance().trackPage("randoms/TicketDestroy/", "");
			final Component child = item.getComponent();
			if (child != null) {
				if (((world.settings.get(1448) & 0xFF00) >>> 8) < (child.getItemId() == ITEM_IDS[0] ? 10 : 9)) {
					child.interact("Claim spin");
				}
				if (child.interact("Destroy")) {
					final Timer timer = new Timer(Random.nextInt(4000, 6000));
					while (timer.isRunning()) {
						final Widget widget = world.widgets.get(1183);
						if (widget != null && widget.isValid()) {
							for (final Component c : widget.getComponents()) {
								final String s;
								if (c.isVisible() && (s = c.getTooltip()) != null && s.trim().equalsIgnoreCase("destroy")) {
									if (c.interact("Destroy")) {
										final Timer t = new Timer(Random.nextInt(1500, 2000));
										while (t.isRunning() && child.getItemId() != -1) sleep(100, 250);
									}
								}
							}
						}
					}
				}
			}
		}
		return 600;
	}
}
