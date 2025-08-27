@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own.review.submitted

import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.DialogBase

fun createSubmittedTourMembersDialog(entry: TourEntry, showcase: Boolean = false): Dialog = dialog {
    base {
        title { primary("Mitglieder der Einreichung") }
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                if (entry.members.isEmpty()) {
                    error("Keine Mitglieder")
                } else {
                    variableKey("Mitglieder: ")
                    variableValue(entry.members.size)
                }
            }
        }
    }

    type {
        if (entry.members.isEmpty()) {
            notice(backButton(entry, showcase))
        } else {
            multiAction {
                columns(3)
                exitAction(backButton(entry, showcase))

                entry.members.forEach {
                    action {
                        width(200)

                        label {
                            text(
                                it.offlinePlayer.name
                                    ?: it.offlinePlayer.uniqueId.toString()
                            )
                        }
                        tooltip {
                            variableKey("Beschreibung:")
                            it.description?.let { desc ->
                                variableValue(desc)
                                return@tooltip
                            }

                            variableValue("Keine Beschreibung")
                        }
                    }
                }
            }
        }
    }
}

private fun backButton(entry: TourEntry, showcase: Boolean) = actionButton {
    label { text("Zurück") }
    tooltip { info("Zurück zur Einreichung") }

    action {
        playerCallback { player ->
            player.showDialog(createSubmittedTourDialog(entry, showcase))
        }
    }
}

