@file:Suppress("UnstableApiUsage")
@file:OptIn(NmsUseWithCaution::class)

package dev.slne.surf.servertour.dialogs.own.review.submitted

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.servertour.dialogs.own.review.showcase.createShowcaseTourDialog
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.servertour.plugin
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.clearDialogs
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.bukkit.api.nms.NmsUseWithCaution
import dev.slne.surf.surfapi.core.api.messages.adventure.appendNewline
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import io.papermc.paper.registry.data.dialog.DialogBase

fun createSubmittedTourDialog(entry: TourEntry, showcase: Boolean = false) = dialog {
    base {
        title {
            primary("Tourübersicht für ")
            variableValue(entry.name)
        }
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                append(buildTourBody(entry))
            }
        }
    }

    type {
        multiAction {
            columns(2)
            exitAction(backButton(showcase))

            action(listMembersButton(entry, showcase))
            action(listPoIsButton(entry, showcase))

            if (!showcase) {
                action(acceptButton(entry))
                action(declineButton(entry))
            }

            action(teleportButton(entry))
        }
    }
}

private fun backButton(showcase: Boolean) = actionButton {
    label { error("Zurück") }
    tooltip { info("Zurück zu den Einreichungen") }

    action {
        playerCallback {
            plugin.launch {
                it.showDialog(if (showcase) createShowcaseTourDialog() else createViewSubmittedToursDialog())
            }
        }
    }
}

private fun teleportButton(entry: TourEntry) = actionButton {
    label { info("Teleportieren") }
    tooltip { info("Teleportiere dich zu dieser Tour") }

    action {
        playerCallback { player ->
            player.teleportAsync(entry.location)
            player.clearDialogs()
        }
    }
}

private fun acceptButton(entry: TourEntry) = actionButton {
    label { success("Annehmen") }
    tooltip { info("Klicke, um die Tour zu genehmigen") }

    action {
        playerCallback {
            plugin.launch {
                entry.accept()
            }
            it.showDialog(createEntryUpdatedNotice(true))
        }
    }
}

private fun declineButton(entry: TourEntry) = actionButton {
    label { error("Ablehnen") }
    tooltip { info("Klicke, um die Tour zu abzulehnen") }

    action {
        playerCallback {
            plugin.launch {
                entry.reject()
            }
            it.showDialog(createEntryUpdatedNotice(false))
        }
    }
}

private fun createEntryUpdatedNotice(accepted: Boolean, showcase: Boolean = false) = dialog {
    base {
        title { primary("Erfolgreich") }
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                if (accepted) {
                    success("Die Einreichung wurde angenommen.")
                } else {
                    error("Die Einreichung wurde abgelehnt.")
                }
            }
        }

        type {
            notice {
                action {
                    label { text("Zurück") }
                    tooltip { info("Zurück zu dne Einreichungen") }

                    playerCallback {
                        plugin.launch {
                            it.showDialog(if (showcase) createShowcaseTourDialog() else createViewSubmittedToursDialog())
                        }
                    }
                }
            }
        }
    }
}


private fun listMembersButton(entry: TourEntry, showcase: Boolean) = actionButton {
    label { text("Mitglieder") }
    tooltip { info("Zeige die Mitglieder der Einreichung an") }

    action {
        playerCallback {
            it.showDialog(createSubmittedTourMembersDialog(entry, showcase))
        }
    }
}

private fun listPoIsButton(entry: TourEntry, showcase: Boolean) = actionButton {
    label { text("PoIs") }
    tooltip { info("Zeige die PoIs der Einreichung an") }

    action {
        playerCallback {
            it.showDialog(createSubmittedTourPoIsDialog(entry, showcase))
        }
    }
}

fun buildTourBody(entry: TourEntry) = buildText {
    variableKey("Ersteller: ")
    val offlineOwner = entry.owner.offlinePlayer
    if (offlineOwner.hasPlayedBefore()) {
        variableValue(offlineOwner.name ?: "Unbekannt")
    } else {
        variableValue("Unbekannt")
    }
    appendNewline(2)

    val members = entry.members
    variableKey("Mitglieder ")
    variableValue("(${members.size})")
    appendNewline()
    if (members.isNotEmpty()) {
        members.joinToString(", ") {
            it.offlinePlayer.name ?: it.offlinePlayer.uniqueId.toString()
        }.let { variableValue(it) }
    } else {
        variableValue("Keine Mitglieder vorhanden")
    }
    appendNewline(2)

    val poIs = entry.poi
    variableKey("POIs ")
    variableValue("(${poIs.size})")
    appendNewline()
    if (poIs.isNotEmpty()) {
        variableValue(poIs.joinToString(", ") { it.name })
    } else {
        variableValue("Keine POIs vorhanden")
    }
    appendNewline(2)

    variableKey("Beschreibung:")
    appendNewline()
    variableValue(entry.description.ifBlank { "Keine Beschreibung vorhanden" })
    appendNewline(2)
}