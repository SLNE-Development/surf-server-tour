package dev.slne.surf.servertour.database.tables

object EntryTable : BaseTable("servertour_entries") {

    val server = varchar("server_name", 255)
    val owner = uuid("owner")

}