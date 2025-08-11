package dev.slne.surf.servertour

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.database.DatabaseManager
import dev.slne.surf.servertour.commands.serverTourCommand
import dev.slne.surf.servertour.database.tables.EntryTable
import dev.slne.surf.servertour.database.tables.MemberTable
import dev.slne.surf.servertour.database.tables.PoiTable
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.io.path.div

val plugin get() = JavaPlugin.getPlugin(SurfServerTour::class.java)

class SurfServerTour : SuspendingJavaPlugin() {

    private lateinit var databaseManager: DatabaseManager

    override suspend fun onLoadAsync() {
        databaseManager = DatabaseManager(
            configDirectory = dataPath,
            storageDirectory = dataPath / "storage"
        )

        databaseManager.databaseProvider.connect()

        newSuspendedTransaction {
            SchemaUtils.create(
                EntryTable,
                MemberTable,
                PoiTable,
            )
        }
    }

    override suspend fun onEnableAsync() {
        serverTourCommand()
    }

    override suspend fun onDisableAsync() {
        databaseManager.databaseProvider.disconnect()
    }

}