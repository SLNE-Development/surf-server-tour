@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.servertour.dialogs.listOwnToursDialog
import dev.slne.surf.servertour.dialogs.own.information.descriptionOwnTourEntryDialog
import dev.slne.surf.servertour.dialogs.own.information.renameOwnTourEntryDialog
import dev.slne.surf.servertour.dialogs.own.member.addMemberToOwnTourDialog
import dev.slne.surf.servertour.dialogs.own.member.ownTourMembersDialog
import dev.slne.surf.servertour.dialogs.own.poi.createPoiDialog
import dev.slne.surf.servertour.dialogs.own.poi.ownTourPoisDialog
import dev.slne.surf.servertour.entry.EntryManager
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.servertour.plugin
import dev.slne.surf.servertour.view.viewManager
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.core.api.messages.adventure.appendNewline
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.DialogBase
import net.kyori.adventure.text.Component

fun buildOwnTourTitle(entry: TourEntry, vararg subs: Component) = buildText {
    primary("Eigene Einreichungen")
    spacer(" - ")
    append(entry)

    subs.forEach { sub ->
        spacer(" - ")
        append(sub)
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

    variableKey("Aktiver Status: ")
    appendNewline()
    append(entry.status)
}

fun ownTourDialog(
    entry: TourEntry,
    editable: Boolean
): Dialog = dialog {
    base {
        title(buildOwnTourTitle(entry))
        externalTitle { text(entry.name) }
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

            if (editable) {
                action(renameButton(entry, editable))
                action(descriptionButton(entry, editable))
            }

            action(listMembersButton(entry, editable))
            if (editable) {
                action(addMemberButton(entry, editable))
            }

            action(listPoisButton(entry, editable))

            if (editable) {
                action(addPoiButton(entry, editable))

                action(changeLocationButton(entry, editable))
                action(submitButton(entry, editable))
            }

            action(removeEntryButton(entry, editable))
            action(viewButton(entry))

            exitAction(backButton())
        }
    }
}

private fun viewButton(entry: TourEntry) = actionButton {
    label { text("Tour ansehen") }
    tooltip { info("Teleportiert dich für 5 Sekunden zu deiner Tour") }

    action {
        playerCallback {
            plugin.launch {
                viewManager.viewTour(it, entry)
            }
        }
    }
}

private fun submitButton(entry: TourEntry, editable: Boolean) = actionButton {
    label { success("Einreichen") }
    tooltip { info("Reicht die Einreichung ein") }

    action {
        playerCallback { it.showDialog(createSubmitOwnTourDialog(entry, editable)) }
    }
}

private fun backButton() = actionButton {
    label { text("Zurück") }
    tooltip { info("Zurück zu den Einreichungen") }

    action {
        playerCallback {
            plugin.launch {
                it.showDialog(listOwnToursDialog(it.uniqueId))
            }
        }
    }
}

private fun removeEntryButton(entry: TourEntry, editable: Boolean) = actionButton {
    label { error("Entfernen") }
    tooltip { error("Entfernt die Einreichung") }

    action {
        playerCallback { it.showDialog(removeOwnTourDialog(entry, editable)) }
    }
}

private fun changeLocationButton(entry: TourEntry, editable: Boolean) = actionButton {
    label { text("Position ändern") }
    tooltip { info("Ändert die Position der Einreichung") }

    action {
        playerCallback { player ->
            plugin.launch {
                EntryManager.updateEntry(entry) {
                    it.location = player.location
                }

                player.showDialog(createChangedLocationNotice(entry, editable))
            }
        }
    }
}

private fun createChangedLocationNotice(entry: TourEntry, editable: Boolean) = dialog {
    base {
        title(
            buildOwnTourTitle(
                entry,
                buildText { spacer("Position geändert") }
            )
        )
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                success("Die Position der Einreichung ")
                append(entry)
                success(" wurde erfolgreich geändert")
            }
        }

        type {
            notice {
                action {
                    label { text("Zurück") }
                    tooltip { info("Zurück zu der Einreichung") }
                    playerCallback { it.showDialog(ownTourDialog(entry, editable)) }
                }
            }
        }
    }
}

private fun listPoisButton(entry: TourEntry, editable: Boolean) = actionButton {
    label { text("POIs") }
    tooltip { info("Zeige die POIs der Einreichung an") }

    action {
        playerCallback { it.showDialog(ownTourPoisDialog(entry, editable)) }
    }
}

private fun addPoiButton(entry: TourEntry, editable: Boolean) = actionButton {
    label { text("POI hinzufügen") }
    tooltip { info("Füge einen POI zu deiner Einreichung hinzu") }

    action {
        playerCallback { it.showDialog(createPoiDialog(entry, editable)) }
    }
}

private fun listMembersButton(entry: TourEntry, editable: Boolean) = actionButton {
    label { text("Mitglieder") }
    tooltip { info("Zeige die Mitglieder der Einreichung an") }

    action {
        playerCallback { it.showDialog(ownTourMembersDialog(entry, editable)) }
    }
}

private fun addMemberButton(entry: TourEntry, editable: Boolean) = actionButton {
    label { text("Mitglied hinzufügen") }
    tooltip { info("Füge ein Mitglied zu deiner Einreichung hinzu") }

    action {
        playerCallback { it.showDialog(addMemberToOwnTourDialog(entry, editable)) }
    }
}

private fun renameButton(entry: TourEntry, editable: Boolean) = actionButton {
    label { text("Name") }
    tooltip { info("Benenne deine Einreichung um") }

    action {
        playerCallback { it.showDialog(renameOwnTourEntryDialog(entry, editable)) }
    }
}

private fun descriptionButton(entry: TourEntry, editable: Boolean) = actionButton {
    label { text("Beschreibung") }
    tooltip { info("Ändere die Beschreibung der Einreichung") }

    action {
        playerCallback { it.showDialog(descriptionOwnTourEntryDialog(entry, editable)) }
    }
}