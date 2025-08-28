@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kyori.adventure.nbt.*
import net.kyori.adventure.nbt.BinaryTagIO.Compression.GZIP
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.io.File
import java.io.IOException
import java.util.*

private const val PLAYER_DATA_FOLDER = "playerdata"
private const val PLAYER_GAME_MODE_FILE = "playerGameType"

private fun getPlayerFile(uuid: UUID): File? {
    for (world in Bukkit.getWorlds()) {
        val worldFolder = world.worldFolder
        if (!worldFolder.isDirectory()) {
            continue
        }

        val playerDataFolder = File(worldFolder, PLAYER_DATA_FOLDER)
        if (!playerDataFolder.isDirectory()) {
            continue
        }

        val playerFile = File(playerDataFolder, "$uuid.dat")
        if (playerFile.exists()) {
            return playerFile
        }
    }
    return null
}

suspend fun Player.setOfflineLocation(location: Location) {
    this.uniqueId.setOfflineLocation(location)
}

suspend fun UUID.setOfflineLocation(location: Location) = withContext(Dispatchers.IO) {
    val dataFile: File = getPlayerFile(this@setOfflineLocation) ?: return@withContext
    val rawTag: CompoundBinaryTag
    try {
        rawTag = BinaryTagIO.unlimitedReader()
            .read(dataFile.toPath(), GZIP)
    } catch (e: IOException) {
        error("Failed to read player data file for UUID $this: ${e.message}")
    }

    val builder = CompoundBinaryTag.builder().put(rawTag)
    val posTag = ListBinaryTag.builder()
    val rotTag = ListBinaryTag.builder()

    posTag.add(DoubleBinaryTag.doubleBinaryTag(location.x))
    posTag.add(DoubleBinaryTag.doubleBinaryTag(location.y))
    posTag.add(DoubleBinaryTag.doubleBinaryTag(location.z))

    rotTag.add(FloatBinaryTag.floatBinaryTag(location.yaw))
    rotTag.add(FloatBinaryTag.floatBinaryTag(location.pitch))

    builder.put("Pos", posTag.build())
    builder.put("Rotation", rotTag.build())

    val worldUUIDLeast: Long = location.getWorld().uid.leastSignificantBits
    val worldUUIDMost: Long = location.getWorld().uid.mostSignificantBits

    builder.putLong("WorldUUIDLeast", worldUUIDLeast)
    builder.putLong("WorldUUIDMost", worldUUIDMost)

    BinaryTagIO.writer()
        .write(builder.build(), dataFile.toPath(), GZIP)
}

private suspend fun setGameModeInFile(gameMode: GameMode, dataFile: File) = withContext(
    Dispatchers.IO
) {
    val rawTag = BinaryTagIO.unlimitedReader().read(dataFile.toPath(), GZIP)
    val builder = CompoundBinaryTag.builder().put(rawTag)

    builder.put(PLAYER_GAME_MODE_FILE, IntBinaryTag.intBinaryTag(gameMode.value))

    BinaryTagIO.writer().write(builder.build(), dataFile.toPath(), GZIP)
}

suspend fun OfflinePlayer.setOfflineGameMode(gameMode: GameMode) {
    val playerFile = getPlayerFile(this.uniqueId) ?: return

    try {
        setGameModeInFile(gameMode, playerFile)
    } catch (e: IOException) {
        error("Failed to set game mode for offline player ${this.name}: ${e.message}")
    }
}