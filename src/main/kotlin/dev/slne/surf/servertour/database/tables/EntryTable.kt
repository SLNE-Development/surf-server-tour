package dev.slne.surf.servertour.database.tables

object EntryTable : BaseTable("servertour_entries") {

    val owner = uuid("owner")

}