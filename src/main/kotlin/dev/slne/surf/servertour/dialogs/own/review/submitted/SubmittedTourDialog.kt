@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own.review.submitted

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.servertour.dialogs.own.member.createTourMembersDialog
import dev.slne.surf.servertour.dialogs.own.poi.createTourPoIsDialog
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.servertour.plugin
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.core.api.messages.adventure.appendNewline
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import io.papermc.paper.registry.data.dialog.DialogBase

fun createSubmittedTourDialog(entry: TourEntry) = dialog {
    base {
        title {
            primary("Tourübersicht für ")
            variableValue(entry.name)
        }
        afterAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE)

        body {
            plainMessage(400) {
                append(buildTourBody(entry))
            }
        }
    }

    type {
        multiAction {
            columns(2)
            exitAction(backButton())

            action(listMembersButton(entry))
            action(listPoIsButton(entry))
        }
    }
}

private fun backButton() = actionButton {
    label { text("Zurück") }
    tooltip { info("Zurück zu den Einreichungen") }

    action {
        playerCallback {
            plugin.launch {
                it.showDialog(createViewSubmittedToursDialog())
            }
        }
    }
}

private fun listMembersButton(entry: TourEntry) = actionButton {
    label { text("Mitglieder") }
    tooltip { info("Zeige die Mitglieder der Einreichung an") }

    action {
        playerCallback { it.showDialog(createTourMembersDialog(entry, false)) }
    }
}

private fun listPoIsButton(entry: TourEntry) = actionButton {
    label { text("POIs") }
    tooltip { info("Zeige die POIs der Einreichung an") }

    action {
        playerCallback { it.showDialog(createTourPoIsDialog(entry, false)) }
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

    val pois = entry.poi
    variableKey("POIs ")
    variableValue("(${pois.size})")
    appendNewline()
    if (pois.isNotEmpty()) {
        variableValue(pois.joinToString(", ") { it.name })
    } else {
        variableValue("Keine POIs vorhanden")
    }
    appendNewline(2)

    variableKey("Beschreibung:")
    appendNewline()
    variableValue(entry.description.ifBlank { "Keine Beschreibung vorhanden" })
    appendNewline(2)
}