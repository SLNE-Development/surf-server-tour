@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own.review.submitted

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

suspend fun createViewSubmittedToursDialog(): Dialog {
    val submittedTours = EntryManager.listEntries()
        .filter { it.status == EntryStatus.PENDING }
        .sortedBy {
            it.name
        }
        .map {
            it to createSubmittedTourDialog(it)
        }
        .toObjectList()
    return dialog {
        base {
            title { primary("Einreichungen") }
            afterAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE)

            if (submittedTours.isEmpty()) {
                body {
                    plainMessage(400) {
                        error("Es wurden keine Einreichungen gefunden")
                    }
                }
            }
        }

        type {
            if (submittedTours.isEmpty()) {
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

                    submittedTours.forEach {
                        action {
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