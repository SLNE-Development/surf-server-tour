@file:Suppress("UnstableApiUsage")
@file:OptIn(NmsUseWithCaution::class)

package dev.slne.surf.servertour.dialogs.own.review.submitted

import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.bukkit.api.nms.NmsUseWithCaution

import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.DialogBase

fun createSubmittedTourPoIsDialog(entry: TourEntry, showcase: Boolean = false): Dialog = dialog {
    base {
        title { primary("PoIs der Einreichung") }
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                if (entry.members.isEmpty()) {
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
            notice(backButton(entry, showcase))
        } else {
            multiAction {
                columns(3)
                exitAction(backButton(entry, showcase))

                entry.poi.forEach {
                    action {
                        label { text(it.name) }
                        tooltip {
                            spacer("Klicke, um genauere Informationen zu diesem PoI anzusehen")
                        }
                        width(200)

                        action {
                            playerCallback { player ->
                                player.showDialog(createSubmittedTourPoIDialog(entry, it, showcase))
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun backButton(entry: TourEntry, showcase: Boolean) = actionButton {
    label { error("Zurück") }
    tooltip { info("Zurück zur Einreichung") }

    action {
        playerCallback { player ->
            player.showDialog(createSubmittedTourDialog(entry, showcase))
        }
    }
}


