package xyz.upperlevel.uppercore.gui.link;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.gui.link.impl.CommandLink;
import xyz.upperlevel.uppercore.gui.link.impl.ConsoleCommandLink;

public interface Link {
	Link EMPTY = new Link() {
		@Override
		public void run(Player player) {
		}

		@Override
		public Link and(Link after) {
			return after;
		}
	};

	/**
	 * Executed when the link is sync
	 *
	 * @param player
	 *            the player that executed this link
	 */
	void run(Player player);

	/**
	 * Joins this link with another one. The operation a.and(b) is different from
	 * b.and(a) only for the execution order
	 *
	 * @param after
	 *            the other link in the fusion (that will be executed after)
	 * @return a new link that runs both of the links
	 */
	default Link and(Link after) {
		return p -> {
			this.run(p);
			after.run(p);
		};
	}

	/**
	 * Creates a link that runs a commands with Console privileges
	 * (Bukkit.getConsoleSender())
	 *
	 * @param command
	 *            the commands to be runned (with &lt;player&gt; as the player's
	 *            name)
	 * @return a new link that executes the commands passed as argument with Console
	 *         privileges
	 */
	static Link consoleCommand(String command) {
		return new ConsoleCommandLink(command);
	}

	/**
	 * Creates a link that runs a commands with the executor privileges (the link
	 * executor)
	 *
	 * @param command
	 *            the commands to be runned (with &lt;player&gt; as the player's
	 *            name)
	 * @return a new link that executes the commands passed as argument with the
	 *         executor privileges
	 */
	static Link command(String command) {
		return new CommandLink(command);
	}

	/**
	 * Joins two or more links together maintaining the array's order as the
	 * execution order
	 *
	 * @param links
	 *            the links to be joined
	 * @return a new link that runs all of the links in th array's order
	 */
	static Link and(Link... links) {
		return p -> {
			for (Link link : links)
				link.run(p);
		};
	}
}
