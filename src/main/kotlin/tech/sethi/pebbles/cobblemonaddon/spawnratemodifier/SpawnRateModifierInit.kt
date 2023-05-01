package tech.sethi.pebbles.cobblemonaddon.spawnratemodifier

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import tech.sethi.pebbles.cobblemonaddon.spawnratemodifier.commands.UpdateShinyRateCommand

object SpawnRateModifierInit : ModInitializer {
    override fun onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycleEvents.ServerStarted { server ->

//            UpdatePokemonSpawnCommand().register(server.commandManager.dispatcher)
            UpdateShinyRateCommand.register(server.commandManager.dispatcher)
        })
        println("Cobblemon Addon Mod initialized.")
    }
}
