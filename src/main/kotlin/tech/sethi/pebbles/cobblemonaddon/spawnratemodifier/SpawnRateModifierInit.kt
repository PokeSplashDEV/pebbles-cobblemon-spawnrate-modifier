package tech.sethi.pebbles.cobblemonaddon.spawnratemodifier

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import tech.sethi.pebbles.cobblemonaddon.spawnratemodifier.commands.UpdateShinyRateCommand
import tech.sethi.pebbles.cobblemonaddon.spawnratemodifier.commands.UpdateShinyRateCommand.createTimer

object SpawnRateModifierInit : ModInitializer {
    override fun onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycleEvents.ServerStarted { server ->

//            UpdatePokemonSpawnCommand().register(server.commandManager.dispatcher)
            UpdateShinyRateCommand.register(server.commandManager.dispatcher)

            Queue.read()

            val nextRate = Queue.getNext()

            if (nextRate != null) {
                Queue.remove(nextRate)
                UpdateShinyRateCommand.createTimer(nextRate.first, nextRate.second, null)
            }
        })
        println("Cobblemon Addon Mod initialized.")
    }
}
