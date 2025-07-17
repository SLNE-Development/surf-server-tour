package dev.slne.surf.servertour.utils

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object ServerTourPermissionRegistry : PermissionRegistry() {

    private val PREFIX = "surf.servertour"
    private val COMMAND_PREFIX = "$PREFIX.command"

    val BASE = "$COMMAND_PREFIX.base"
    val FAKE_CACHED_DATA = "$COMMAND_PREFIX.fake-cached-data"
}