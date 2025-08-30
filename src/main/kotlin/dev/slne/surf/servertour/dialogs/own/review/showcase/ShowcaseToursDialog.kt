@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own.review.showcase

import dev.slne.surf.servertour.dialogs.SERVER_TOUR_LAST_VIEWED
import dev.slne.surf.servertour.dialogs.own.review.submitted.createSubmittedTourDialog
import dev.slne.surf.servertour.dialogs.serverTourDialog
import dev.slne.surf.servertour.entry.EntryManager
import dev.slne.surf.servertour.entry.EntryStatus
import dev.slne.surf.servertour.utils.appendEmDash
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.core.api.messages.adventure.appendNewline
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
                    columns(2)

                    acceptedTours.forEach {
                        action {
                            width(200)
                            label { variableKey(it.first.name) }
                            tooltip {
                                appendEmDash()
                                variableKey("Name: ")
                                variableValue(it.first.name)
                                appendNewline()
                                appendEmDash()
                                variableKey("Status: ")
                                variableValue(
                                    it.first.status.name.lowercase()
                                        .replaceFirstChar { char -> char.uppercase() })
                                appendNewline()
                                appendEmDash()
                                variableKey("Besitzer: ")
                                variableValue(
                                    it.first.owner.offlinePlayer.name
                                        ?: it.first.owner.uuid.toString()
                                )
                                appendNewline(2)
                                spacer("Klicke, um die Einreichung anzusehen.")
                            }

                            action {
                                playerCallback { player ->
                                    player.showDialog(it.second)
                                    SERVER_TOUR_LAST_VIEWED[player.uniqueId] = it.first
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