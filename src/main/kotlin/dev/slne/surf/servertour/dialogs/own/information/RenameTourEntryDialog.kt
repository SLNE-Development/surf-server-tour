@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own.information

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.servertour.dialogs.own.buildOwnTourTitle
import dev.slne.surf.servertour.dialogs.own.ownTourDialog
import dev.slne.surf.servertour.entry.EntryManager
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.servertour.plugin
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.core.api.messages.adventure.appendNewline
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import io.papermc.paper.registry.data.dialog.DialogBase

fun renameOwnTourEntryDialog(entry: TourEntry, editable: Boolean) = dialog {
    base {
        title(buildOwnTourTitle(entry, buildText { spacer("Name ändern") }))
        afterAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE)

        input {
            text("name") {
                label { text("Name") }
                maxLength(64)
                initial(entry.name)
                width(600)
            }
        }
    }

    type {
        confirmation(confirmRenameButton(entry, editable), cancelRenameButton(entry, editable))
    }
}

private fun confirmRenameButton(entry: TourEntry, editable: Boolean) = actionButton {
    label { success("Speichern") }
    tooltip { info("Speichern und zurück zur Einreichung") }

    action {
        customClick { response, audience ->
            val newName = response.getText("name") ?: "Fehler"

            plugin.launch {
                val oldName = entry.name

                EntryManager.updateEntry(entry) {
                    it.name = newName
                }

                audience.showDialog(confirmNotice(entry, oldName, editable))
            }
        }
    }
}

private fun confirmNotice(entry: TourEntry, oldName: String, editable: Boolean) = dialog {
    base {
        title(
            buildOwnTourTitle(
                entry,
                buildText { spacer("Einreichung umbenannt") }
            )
        )
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                success("Die Einreichung wurde erfolgreich umbenannt")
                appendNewline(2)

                variableKey("Alter Name: ")
                appendNewline()
                variableValue(oldName)
                appendNewline(2)

                variableKey("Neuer Name: ")
                appendNewline()
                variableValue(entry.name)
            }
        }
    }

    type {
        notice(backToEntryButton(entry, editable))
    }
}

private fun backToEntryButton(entry: TourEntry, editable: Boolean) = actionButton {
    label { text("Zurück zur Einreichung") }
    tooltip { info("Zurück zur Einreichung") }

    action {
        playerCallback {
            it.showDialog(ownTourDialog(entry, editable))
        }
    }
}

private fun cancelRenameButton(entry: TourEntry, editable: Boolean) = actionButton {
    label { text("Abbrechen") }
    tooltip { info("Abbrechen und zurück zur Einreichung") }

    action {
        playerCallback {
            it.showDialog(ownTourDialog(entry, editable))
        }
    }
}