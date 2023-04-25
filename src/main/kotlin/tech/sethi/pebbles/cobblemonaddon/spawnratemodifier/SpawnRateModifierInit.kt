package tech.sethi.pebbles.cobblemonaddon.spawnratemodifier

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents

object SpawnRateModifierInit : ModInitializer {
    override fun onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycleEvents.ServerStarted {
            UpdateShinyRateCommand.register(it.commandManager.dispatcher)
        })
        println("Cobblemon Addon Mod initialized.")
    }
}
