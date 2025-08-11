package dev.slne.surf.servertour.utils

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object ServerTourPermissionRegistry : PermissionRegistry() {

    private const val PREFIX = "surf.servertour"
    private const val COMMAND_PREFIX = "$PREFIX.command"

    val BASE = create("$COMMAND_PREFIX.base")
}