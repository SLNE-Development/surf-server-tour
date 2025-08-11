@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.view

import dev.slne.surf.servertour.dialogs.own.ownTourDialog
import dev.slne.surf.servertour.entry.EntryMember
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.surfapi.bukkit.api.builder.ItemStack
import dev.slne.surf.surfapi.bukkit.api.builder.meta
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import org.bukkit.Material
import org.bukkit.inventory.meta.SkullMeta

fun viewMembersDialog(entry: TourEntry) = dialog {
    base {
        body {
            entry.members.forEach { member ->
                item {
                    item(memberHead(member))
                    simpleDescription(400) {
                        text(member.offlinePlayer.name ?: "Unbekannt")
                    }
                }
            }
        }
    }

    type {
        notice {
            label { text("Zurück") }
            tooltip { info("Zurück zur Einreichung") }

            action {
                playerCallback {
                    it.showDialog(ownTourDialog(entry))
                }
            }
        }
    }
}

private fun memberHead(member: EntryMember) = ItemStack(Material.PLAYER_HEAD) {
    meta<SkullMeta> {
        owningPlayer = member.offlinePlayer
    }
}