@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own.review.showcase

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.servertour.entry.EntryManager
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.servertour.plugin
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import io.papermc.paper.registry.data.dialog.DialogBase

suspend fun createSortShowcaseTourDialog(entry: TourEntry) = dialog {
    base {
        title { primary("Einreichungen") }
        afterAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE)

        body {
            plainMessage {
                info("Setze Nummern vor die Einreichungen, um diese zu sortieren.")
                appendNewline()
                info("Die Einträge werden aufsteigend sortiert.")
            }
        }

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
        confirmation(
            confirmSortButton(entry),
            cancelSortButton()
        )
    }
}

private fun confirmSortButton(
    entry: TourEntry
) = actionButton {
    label { success("Speichern") }
    tooltip { info("Speichern und zurück zum POI") }

    action {
        customClick { response, clicker ->
            val newName = response.getText("name") ?: run {
                clicker.sendText {
                    appendPrefix()
                    error("Der Name darf nicht leer sein.")
                }
                return@customClick
            }

            plugin.launch {
                val oldName = entry.name
                EntryManager.updateEntry(entry) {
                    it.name = newName
                }

                clicker.sendText {
                    appendPrefix()
                    success("Die Einreichung wurde von ")
                    variableValue(oldName)
                    success(" zu ")
                    variableValue(newName)
                    success(" umbenannt.")
                }

                clicker.showDialog(createSortShowcaseToursDialog())
            }
        }
    }
}

private fun cancelSortButton() = actionButton {
    label { text("Abbrechen") }
    tooltip { info("Abbrechen und zurück zur Übersicht") }

    action {
        playerCallback {
            plugin.launch {
                it.showDialog(createSortShowcaseToursDialog())
            }
        }
    }
}