package dev.slne.surf.servertour.utils

import org.bukkit.Bukkit
import java.util.UUID

fun UUID.hasPermission(permission: String) = Bukkit.getPlayer(this)?.hasPermission(permission) ?: false