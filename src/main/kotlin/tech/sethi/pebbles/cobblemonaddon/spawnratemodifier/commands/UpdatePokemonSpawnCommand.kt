//package tech.sethi.pebbles.cobblemonaddon.spawnratemodifier.commands
//
//import com.cobblemon.mod.common.Cobblemon
//import com.cobblemon.mod.common.api.spawning.CobblemonWorldSpawnerManager
//import com.cobblemon.mod.common.api.spawning.SpawnBucket
//import com.cobblemon.mod.common.api.spawning.SpawnCause
//import com.cobblemon.mod.common.api.spawning.spawner.Spawner
//import com.cobblemon.mod.common.api.spawning.spawner.SpawningArea
//import com.cobblemon.mod.common.command.argument.SpawnBucketArgumentType
//import com.mojang.brigadier.Command
//import com.mojang.brigadier.CommandDispatcher
//import com.mojang.brigadier.context.CommandContext
//import net.minecraft.entity.Entity
//import net.minecraft.entity.player.PlayerEntity
//import net.minecraft.server.command.CommandManager
//import net.minecraft.server.command.ServerCommandSource
//import net.minecraft.server.network.ServerPlayerEntity
//import net.minecraft.text.Text
//import net.minecraft.util.math.MathHelper
//import net.minecraft.world.World
//class UpdatePokemonSpawnCommand {
//    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
//        dispatcher.register(CommandManager.literal("getallspawners").requires { it.hasPermissionLevel(2) }.executes { context ->
//            val player = context.source.player
//            if (player != null) {
//                getAllSpawners(context, player)
//            }
//            0
//        })
//    }
//
//    private fun getAllSpawners(context: CommandContext<ServerCommandSource>, player: ServerPlayerEntity): Int {
//
//        if (!Cobblemon.config.enableSpawning) {
//            return 0
//        }
//
//        val spawner = CobblemonWorldSpawnerManager.spawnersForPlayers[player.uuid] ?: return Command.SINGLE_SUCCESS
//        val bucket = SpawnBucketArgumentType.getSpawnBucket(context, "bucket")
//        val cause = SpawnCause(spawner, bucket, player)
//        val server = context.source.server
//
//
//        val slice = spawner.prospector.prospect(
//                spawner = spawner,
//                area = SpawningArea(
//                        cause = cause,
//                        world = player.world as net.minecraft.world.level.Level, // Use player.world without casting
//                        baseX = MathHelper.ceil(player.x - Cobblemon.config.worldSliceDiameter / 2F),
//                        baseY = MathHelper.ceil(player.y - Cobblemon.config.worldSliceHeight / 2F),
//                        baseZ = MathHelper.ceil(player.z - Cobblemon.config.worldSliceDiameter / 2F),
//                        length = Cobblemon.config.worldSliceDiameter,
//                        height = Cobblemon.config.worldSliceHeight,
//                        width = Cobblemon.config.worldSliceDiameter
//                )
//        )
//
//        server.sendMessage(Text.of("Spawners: " + spawner.toString()))
//        server.sendMessage(Text.of("Bucket: " + bucket.toString()))
//        server.sendMessage(Text.of("Cause: " + cause.toString()))
//
//        return Command.SINGLE_SUCCESS
//    }
//
//
//}