package tech.sethi.pebbles.cobblemonaddon.spawnratemodifier

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.fabricmc.loader.impl.FabricLoaderImpl
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*


object Queue {
    private var queue: ArrayList<Pair<Float, Float>> = arrayListOf()

    fun getNext(): Pair<Float, Float>? {
        if (queue.isEmpty()) {
            return null
        } else {
            return queue.get(0);
        }

    }

    fun add(item: Pair<Float, Float>) {
        queue.add(item)
        write()
    }

    fun remove(item: Pair<Float, Float>) {
        queue.remove(item)
        write()
    }

    private fun write() {
        try {

            val file = getFile()

            val gson = GsonBuilder().setPrettyPrinting().create()

            val type = object : TypeToken<ArrayList<Pair<Float, Float>>?>() {}.type

            val writer = FileWriter(file)
            writer.write(gson.toJson(queue, type))
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    public fun read() {
        try {

            val file = getFile()

            if (!file.exists()) {
                return
            }

            val reader = Scanner(file)

            var data = ""

            while (reader.hasNextLine()) {
                data += reader.nextLine()
            }
            reader.close()

            val gson = GsonBuilder().setPrettyPrinting().create()

            val type = object : TypeToken<ArrayList<Pair<Float, Float>>?>() {}.type

            val fileQueue = gson.fromJson<ArrayList<Pair<Float, Float>>?>(data, type)

            this.queue = fileQueue;

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun getFile(): File {
        val path = Paths.get(FabricLoaderImpl.INSTANCE.configDir.toString() +
                "/shinybooster/config.json")
        val file = path.toFile()


        // If the path doesn't exist, create it.
        if (!Files.exists(path.getParent())) {
            file.getParentFile().mkdirs()
        }

        return file
    }
}