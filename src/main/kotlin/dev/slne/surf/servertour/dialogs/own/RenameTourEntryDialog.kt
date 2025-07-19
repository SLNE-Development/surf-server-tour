@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.servertour.dialogs.ownTourDialog
import dev.slne.surf.servertour.entry.EntryManager
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.servertour.plugin
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import io.papermc.paper.registry.data.dialog.DialogBase

fun renameOwnTourEntryDialog(entry: TourEntry) = dialog {
    base {
        title { primary("${entry.name} umbenennen") }
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
        confirmation(confirmRenameButton(entry), cancelRenameButton(entry))
    }
}

private fun confirmRenameButton(entry: TourEntry) = actionButton {
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

                audience.showDialog(confirmNotice(entry, oldName))
            }
        }
    }
}

private fun confirmNotice(entry: TourEntry, oldName: String) = dialog {
    base {
        title { primary("Einreichung umbenennen") }
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                success("Die Einreichung wurde erfolgreich umbenannt")
            }
            plainMessage(400) {
                variableKey("Alter Name: ")
                variableValue(oldName)
            }
            plainMessage(400) {
                variableKey("Neuer Name: ")
                variableValue(entry.name)
            }
        }
    }

    type {
        notice(backToEntryButton(entry))
    }
}

private fun backToEntryButton(entry: TourEntry) = actionButton {
    label { text("Zurück zur Einreichung") }
    tooltip { info("Zurück zur Einreichung") }

    action {
        playerCallback {
            it.showDialog(ownTourDialog(entry))
        }
    }
}

private fun cancelRenameButton(entry: TourEntry) = actionButton {
    label { text("Abbrechen") }
    tooltip { info("Abbrechen und zurück zur Einreichung") }

    action {
        playerCallback {
            it.showDialog(ownTourDialog(entry))
        }
    }
}