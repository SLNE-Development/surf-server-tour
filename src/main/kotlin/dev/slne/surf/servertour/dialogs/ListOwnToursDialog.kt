@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs

import dev.slne.surf.servertour.dialogs.own.ownTourDialog
import dev.slne.surf.servertour.entry.EntryManager
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.core.api.util.toObjectList
import io.papermc.paper.registry.data.dialog.DialogBase
import java.util.*

fun listOwnToursDialog(owner: UUID) = dialog {
    val ownTours = EntryManager.listEntries(owner)
        .sortedBy {
            it.name
        }.map { it to ownTourDialog(it, it.isDraft()) }
        .toObjectList()

    base {
        title { primary("Eigene Einreichungen") }
        afterAction(DialogBase.DialogAfterAction.NONE)

        if (ownTours.isEmpty()) {
            body {
                plainMessage(400) {
                    info("Du hast noch keine Einreichungen erstellt")
                }
            }
        }
    }

    type {
        if (ownTours.isEmpty()) {
            notice {
                label { text("Zurück") }
                tooltip { info("Zurück zum Hauptmenü") }

                action {
                    playerCallback {
                        it.showDialog(serverTourDialog(owner))
                    }
                }
            }
        } else {
            multiAction {
                columns(3)

                ownTours.forEach {
                    action {
                        label { text(it.first.name) }
                        tooltip { info("Öffne Einreichung: ${it.first.name}") }

                        action {
                            playerCallback { player ->
                                player.showDialog(it.second)
                            }
                        }
                    }
                }

                exitAction {
                    label { text("Zurück") }
                    tooltip { info("Zurück zum Hauptmenü") }

                    action {
                        playerCallback {
                            it.showDialog(serverTourDialog(owner))
                        }
                    }
                }
            }
        }

    }
}