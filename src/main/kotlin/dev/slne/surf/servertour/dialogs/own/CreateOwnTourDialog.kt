@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.servertour.config.config
import dev.slne.surf.servertour.dialogs.serverTourDialog
import dev.slne.surf.servertour.entry.EntryManager
import dev.slne.surf.servertour.entry.EntryMember
import dev.slne.surf.servertour.entry.EntryStatus
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.servertour.plugin
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.core.api.messages.adventure.appendNewline
import io.papermc.paper.registry.data.dialog.DialogBase
import java.util.*

fun createOwnTourDialog() = dialog {
    base {
        title { primary("Einreichung erstellen") }
        afterAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE)

        input {
            text("name") {
                label { text("Name") }
                maxLength(64)
                width(600)
            }

            text("description") {
                label { text("Beschreibung") }
                width(600)
                maxLength(Int.MAX_VALUE)
                multiline(Int.MAX_VALUE, 300)
            }
        }
    }

    type {
        confirmation(confirmCreateButton(), cancelCreateButton())
    }
}

private fun cancelCreateButton() = actionButton {
    label { text("Abbrechen") }
    tooltip { info("Abbrechen und zurück zur Einreichung") }

    action {
        playerCallback { player ->
            player.showDialog(serverTourDialog(player.uniqueId))
        }
    }
}

private fun confirmCreateButton() = actionButton {
    label { success("Erstellen") }
    tooltip { info("Erstellt die Einreichung") }

    action {
        customPlayerClick { response, player ->
            val name = response.getText("name") ?: "Fehler"
            val description = response.getText("description") ?: "Fehler"

            plugin.launch {
                val entry = TourEntry(
                    uuid = UUID.randomUUID(),
                    name = name,
                    description = description,
                    owner = EntryMember(
                        uuid = player.uniqueId
                    ),
                    location = player.location,
                    status = EntryStatus.DRAFT,
                    server = config.serverName
                )

                EntryManager.create(entry)

                player.showDialog(createSuccessNotice(entry))
            }
        }
    }
}

private fun createSuccessNotice(entry: TourEntry) = dialog {
    base {
        title { primary("Einreichung erstellt") }
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                success("Die Einreichung wurde erfolgreich erstellt!")
                appendNewline(2)

                variableKey("Name: ")
                appendNewline()
                variableValue(entry.name)
                appendNewline(2)

                variableKey("Beschreibung: ")
                appendNewline()
                variableValue(entry.description.ifBlank { "Keine Beschreibung vorhanden" })
            }
        }
    }

    type {
        notice {
            label { text("Zur Einreichung") }
            tooltip { info("Zurück zur Einreichung") }

            action {
                playerCallback { it.showDialog(ownTourDialog(entry, entry.isDraft())) }
            }
        }
    }
}