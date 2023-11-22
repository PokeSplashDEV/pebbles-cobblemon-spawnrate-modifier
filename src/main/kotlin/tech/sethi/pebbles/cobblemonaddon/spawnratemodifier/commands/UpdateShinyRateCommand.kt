package tech.sethi.pebbles.cobblemonaddon.spawnratemodifier.commands

import com.cobblemon.mod.common.Cobblemon
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.context.CommandContext
import kotlinx.coroutines.*
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.pokesplash.legendaryspawns.LegendarySpawns
import java.time.Duration
import java.time.Instant

object UpdateShinyRateCommand {
    private var shinyRateEndTime: Instant? = null
    private var shinyRateResetJob: Job? = null
    private var queue: ArrayList<Pair<Float, Float>>? = arrayListOf()

    private fun createTimer(rate: Float,
                            duration: Float,
        context: CommandContext<ServerCommandSource>) {

        LegendarySpawns.announcer.isAnnounceShinies = true;

        Cobblemon.config.shinyRate = rate;

        // Store the end time for the current shiny rate
        shinyRateEndTime = Instant.now().plus(Duration.ofMinutes(duration.toLong()))

        // Schedule a new timer task to reset the shiny rate after the specified duration
        shinyRateResetJob = GlobalScope.launch {
            delay((duration * 60 * 1000).toLong())
            Cobblemon.config.shinyRate = 8192f
            shinyRateEndTime = null
            // Broadcast
            for (player in context.source.server.playerManager.playerList) {
                player.sendMessage(Text.literal("[Booster]").formatted(Formatting.LIGHT_PURPLE)
                    .append(Text.literal(" The Shiny Boost has ended!")
                        .formatted(Formatting.GREEN)))
            }

            if (queue!!.size > 0) {
                val nextRate = queue!![0];
                createTimer(nextRate.first, nextRate.second, context)
                queue!!.removeAt(0);
            }

            LegendarySpawns.announcer.isAnnounceShinies = false;
        }

        // Broadcast
        for (player in context.source.server.playerManager.playerList) {
            player.sendMessage(Text.literal("[Booster]").formatted(Formatting.LIGHT_PURPLE)
                .append(Text.literal(" A Shiny Boost has begun for $duration minutes!")
                    .formatted(Formatting.GREEN)))
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("setshinyrate").requires { it.hasPermissionLevel(2) }
            .then(CommandManager.argument("rate", FloatArgumentType.floatArg(0f))
                .then(CommandManager.argument("duration", FloatArgumentType.floatArg(0f)).executes { context ->

                    val rate = FloatArgumentType.getFloat(context, "rate")
                    val duration = FloatArgumentType.getFloat(context, "duration")

                    if (shinyRateEndTime != null) {
                        queue?.add(Pair(rate, duration))
                        return@executes 0
                    }

                    if (duration == 0f) {
                        context.source.sendFeedback(
                            {
                                Text.literal("Shiny rate updated to $rate").formatted(
                                    Formatting.GREEN
                                )
                            }, true
                        )
                    } else {
                        context.source.sendFeedback(
                            {
                                Text.literal("Shiny rate updated to $rate for $duration minutes").formatted(
                                    Formatting.GREEN
                                )
                            }, true
                        )
                        val server = context.source.server
                        server.sendMessage(
                            Text.literal("Shiny rate is $rate for $duration minutes").formatted(
                                Formatting.GREEN
                            )
                        )
                        val player = context.source.player
                        if (player != null) {
                            player.sendMessage(
                                Text.literal("Shiny rate is $rate for $duration minutes").formatted(
                                    Formatting.GREEN
                                )
                            )
                        }

                    }

                    createTimer(rate, duration, context)

                    1
                }).executes { context ->
                    val rate = FloatArgumentType.getFloat(context, "rate")
                    Cobblemon.config.shinyRate = rate
                    context.source.sendFeedback(
                        {
                            Text.literal("Shiny rate updated to $rate").formatted(
                                Formatting.GREEN
                            )
                        }, true
                    )

                    1
                }))

        dispatcher.register(CommandManager.literal("addshinytime").requires { it.hasPermissionLevel(2) }
            .then(CommandManager.argument("duration", FloatArgumentType.floatArg(0f)).executes { context ->
                if (shinyRateEndTime == null) {
                    context.source.sendFeedback(
                        { Text.literal("There's no active shiny rate boost to extend.").formatted(Formatting.RED) }, false
                    )
                    return@executes 0
                }

                val duration = FloatArgumentType.getFloat(context, "duration")
                shinyRateEndTime = shinyRateEndTime?.plus(Duration.ofMinutes(duration.toLong()))

                // Cancel the previous timer task and schedule a new one with the updated end time
                shinyRateResetJob?.cancel()
                shinyRateResetJob = GlobalScope.launch {
                    delay(Duration.between(Instant.now(), shinyRateEndTime).toMillis())
                    Cobblemon.config.shinyRate = 8192f
                    shinyRateEndTime = null
                }

                context.source.sendFeedback(
                    {
                        Text.literal("Shiny rate boost extended by $duration minutes").formatted(
                            Formatting.GREEN
                        )
                    }, true
                )
                1
            }))

        dispatcher.register(CommandManager.literal("getshinyrate").requires { it.hasPermissionLevel(0) }
            .executes { context ->
                val shinyRate = Cobblemon.config.shinyRate
                val remainingTime = if (shinyRateEndTime != null) {
                    Duration.between(Instant.now(), shinyRateEndTime).toMinutes()
                } else {
                    0
                }
                val remaingSeconds = if (shinyRateEndTime != null) {
                    Duration.between(Instant.now(), shinyRateEndTime).seconds % 60
                } else {
                    0
                }

                if (remainingTime == 0L) {
                    context.source.sendFeedback(
                        { Text.literal("Current shiny rate: $shinyRate").formatted(Formatting.YELLOW) }, true
                    )
                } else {
                    context.source.sendFeedback(
                        {
                            Text.literal("Current shiny rate: $shinyRate for $remainingTime minutes $remaingSeconds seconds")
                                .formatted(
                                    Formatting.YELLOW
                                )
                        }, true
                    )
                }

                1
            })

                dispatcher.register(CommandManager.literal("defaultshinyrate").requires { it.hasPermissionLevel(2) }
            .executes { context ->
                shinyRateResetJob?.cancel()
                Cobblemon.config.shinyRate = 8192f
                shinyRateEndTime = null
                context.source.sendFeedback(
                    { Text.literal("Shiny rate reset to default").formatted(Formatting.GREEN) }, true
                )
                1
            })
    }
}
