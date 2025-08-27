@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own.member

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.servertour.dialogs.own.buildOwnTourTitle
import dev.slne.surf.servertour.entry.EntryManager
import dev.slne.surf.servertour.entry.EntryMember
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.servertour.plugin
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.core.api.messages.adventure.appendNewline
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase

fun changeMemberDescriptionDialog(
    entry: TourEntry,
    member: EntryMember,
    editable: Boolean
): Dialog = dialog {
    base {
        title(
            buildOwnTourTitle(
                entry,
                buildText { spacer("Mitglieder") },
                member.asComponent(),
                buildText { spacer("Beschreibung ändern") })
        )
        afterAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE)

        body {
            input {
                text("description") {
                    label { text("Neue Beschreibung") }
                    initial(member.description ?: "")
                    width(600)
                    maxLength(Int.MAX_VALUE)
                    multiline(Int.MAX_VALUE, 300)
                }
            }
        }
    }

    type {
        confirmation(
            acceptChangeDescriptionButton(entry, member, editable),
            cancelChangeDescriptionButton(entry, member, editable)
        )
    }
}

private fun acceptChangeDescriptionButton(
    entry: TourEntry,
    member: EntryMember,
    editable: Boolean
) = actionButton {
    label { success("Beschreibung ändern") }
    tooltip { info("Ändert die Beschreibung des Mitglieds") }

    action {
        customClick { response, audience ->
            val newDescription = response.getText("description") ?: return@customClick

            plugin.launch {
                val oldDescription = member.description

                EntryManager.updateMember(entry, member) {
                    it.description = newDescription
                }

                audience.showDialog(
                    changeMemberDescriptionSuccessNotice(
                        entry,
                        member,
                        oldDescription,
                        editable
                    )
                )
            }
        }
    }
}

private fun cancelChangeDescriptionButton(
    entry: TourEntry,
    member: EntryMember,
    editable: Boolean
) = actionButton {
    label { text("Abbrechen") }
    tooltip { info("Abbrechen und zurück zum Mitglied") }

    action {
        playerCallback { player ->
            player.showDialog(oneMemberDialog(entry, member, editable))
        }
    }
}

private fun changeMemberDescriptionSuccessNotice(
    entry: TourEntry,
    member: EntryMember,
    oldDescription: String?,
    editable: Boolean
) = dialog {
    base {
        title(
            buildOwnTourTitle(
                entry,
                buildText { spacer("Mitglieder") },
                member.asComponent(),
                buildText { spacer("Beschreibung geändert") })
        )
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                success("Die Beschreibung des Mitglieds wurde erfolgreich aktualisiert")
                appendNewline(2)

                variableKey("Alte Beschreibung: ")
                appendNewline()
                variableValue(oldDescription?.ifBlank { "Keine Beschreibung gesetzt" }
                    ?: "Keine Beschreibung gesetzt")
                appendNewline(2)

                variableKey("Neue Beschreibung: ")
                appendNewline()
                variableValue(member.description?.ifBlank { "Keine Beschreibung gesetzt" }
                    ?: "Keine Beschreibung gesetzt")
            }
        }

        type {
            notice(backButton(entry, member, editable))
        }
    }
}

private fun backButton(
    entry: TourEntry,
    member: EntryMember,
    editable: Boolean
): ActionButton = actionButton {
    label { text("Zurück") }
    tooltip { info("Zurück zum Mitglied") }

    action {
        playerCallback { player ->
            player.showDialog(oneMemberDialog(entry, member, editable))
        }
    }
}