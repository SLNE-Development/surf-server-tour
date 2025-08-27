@file:Suppress("UnstableApiUsage")
@file:OptIn(NmsUseWithCaution::class)

package dev.slne.surf.servertour.dialogs.own.review.submitted

import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.clearDialogs
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.bukkit.api.nms.NmsUseWithCaution
import dev.slne.surf.surfapi.core.api.messages.adventure.appendNewline
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.DialogBase

fun createSubmittedTourPoIsDialog(entry: TourEntry): Dialog = dialog {
    base {
        title { primary("PoIs der Einreichung") }
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                if(entry.members.isEmpty()) {
                    error("Keine PoIs")
                } else {
                    variableKey("PoIs: ")
                    variableValue(entry.poi.size)
                }
            }
        }
    }

    type {
        if (entry.poi.isEmpty()) {
            notice(backButton(entry))
        } else {
            multiAction {
                columns(3)
                exitAction(backButton(entry))

                entry.poi.forEach {
                    action {
                        label { text(it.name) }
                        tooltip {
                            variableKey("Besitzer: ")
                            variableValue(it.owner?.offlinePlayer?.name ?: "Nicht angegeben")
                            appendNewline()
                            variableKey("Beschreibung: ")
                            variableValue(it.description)
                            appendNewline(2)
                            spacer("Klicke, um dich zum PoI zu teleportieren")
                        }
                        width(200)

                        action {
                            playerCallback { player ->
                                player.teleportAsync(it.location)
                                player.clearDialogs()
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun backButton(entry: TourEntry) = actionButton {
    label { text("Zurück") }
    tooltip { info("Zurück zur Einreichung") }

    action {
        playerCallback { player ->
            player.showDialog(createSubmittedTourDialog(entry))
        }
    }
}


