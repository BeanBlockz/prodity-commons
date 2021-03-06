package io.prodity.commons.bungee.inject.impl;

import io.prodity.commons.bungee.inject.BungeeInjectedPlugin;
import io.prodity.commons.bungee.plugin.ProdityBungeePlugin;
import io.prodity.commons.bungee.threads.BungeeThreads;
import io.prodity.commons.inject.bind.PluginBinder;
import io.prodity.commons.inject.impl.Platform;
import io.prodity.commons.inject.impl.ProdityInjectionResolver;
import io.prodity.commons.plugin.ProdityPlugin;
import io.prodity.commons.threads.Threads;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.TypeLiteral;

import javax.inject.Inject;
import java.util.logging.Logger;

public class DefaultPluginBinder extends PluginBinder {

	private final BungeeInjectedPlugin plugin;

	public DefaultPluginBinder(BungeeInjectedPlugin plugin) {
		super(plugin);
		this.plugin = plugin;
	}

	@Override
	protected void configure() {
		this.bind(this.plugin).named(this.plugin.getName())
				.to(Plugin.class)
				.to(ProdityBungeePlugin.class)
				.to(BungeeInjectedPlugin.class)
				.to(ProdityPlugin.class);
		this.bind(this.plugin.getLogger()).to(Logger.class);
		this.bind(ProxyServer.getInstance()).to(ProxyServer.class);
		this.bind(ProxyServer.getInstance().getPluginManager()).to(PluginManager.class);
		this.bind(ProxyServer.getInstance().getScheduler()).to(TaskScheduler.class);
		this.bind(BungeePlatform.class).to(Platform.class);
		this.bind(ProdityInjectionResolver.class).ranked(10).to(new TypeLiteral<InjectionResolver<Inject>>() {
		});
		this.bind(BungeeThreads.class).to(Threads.class);
	}
}
