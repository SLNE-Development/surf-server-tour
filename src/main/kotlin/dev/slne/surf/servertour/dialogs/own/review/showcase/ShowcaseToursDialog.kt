@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own.review.showcase

import dev.slne.surf.servertour.dialogs.own.review.submitted.createSubmittedTourDialog
import dev.slne.surf.servertour.dialogs.serverTourDialog
import dev.slne.surf.servertour.entry.EntryManager
import dev.slne.surf.servertour.entry.EntryStatus
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.core.api.util.toObjectList
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.DialogBase

suspend fun createShowcaseTourDialog(): Dialog {
    val acceptedTours = EntryManager.listEntries()
        .filter { it.status == EntryStatus.ACCEPTED }
        .sortedBy {
            it.name
        }
        .map {
            it to createSubmittedTourDialog(it, true)
        }
        .toObjectList()
    return dialog {
        base {
            title { primary("Einreichungen") }
            afterAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE)

            if (acceptedTours.isEmpty()) {
                body {
                    plainMessage(400) {
                        error("Es wurden keine Einreichungen gefunden")
                    }
                }
            }
        }

        type {
            if (acceptedTours.isEmpty()) {
                notice {
                    label { error("Zurück") }
                    tooltip { info("Zurück zum Hauptmenü") }

                    action {
                        playerCallback {
                            it.showDialog(serverTourDialog(it.uniqueId))
                        }
                    }
                }
            } else {
                multiAction {
                    columns(3)

                    acceptedTours.forEach {
                        action {
                            label { variableKey(it.first.name) }
                            tooltip { info("Einreichung ansehen: ${it.first.name}") }

                            action {
                                playerCallback { player ->
                                    player.showDialog(it.second)
                                }
                            }
                        }
                    }

                    exitAction {
                        label { error("Zurück") }
                        tooltip { info("Zurück zum Hauptmenü") }

                        action {
                            playerCallback {
                                it.showDialog(serverTourDialog(it.uniqueId))
                            }
                        }
                    }
                }
            }
        }
    }
}