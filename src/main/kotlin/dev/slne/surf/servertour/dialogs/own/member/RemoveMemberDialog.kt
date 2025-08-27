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
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase

fun removeMemberDialog(
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
                buildText { spacer("Mitglied entfernen") })
        )
        afterAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE)

        body {
            plainMessage(400) {
                error("Möchtest du das Mitglied ")
                variableValue(member.offlinePlayer.name ?: "Unbekannt")
                error(" wirklich entfernen?")
            }
        }
    }

    type {
        multiAction {
            action(acceptRemoveMemberButton(entry, member, editable))
            action(cancelRemoveMemberButton(entry, member, editable))

            exitAction(backButton(entry, editable))
        }
    }
}

private fun backButton(
    entry: TourEntry,
    editable: Boolean
): ActionButton = actionButton {
    label { text("Zurück") }
    tooltip { info("Zurück zu den Mitgliedern") }

    action {
        playerCallback { player ->
            player.showDialog(ownTourMembersDialog(entry, editable))
        }
    }
}

private fun acceptRemoveMemberButton(
    entry: TourEntry,
    member: EntryMember,
    editable: Boolean
) = actionButton {
    label { error("Entfernen") }
    tooltip { info("Entfernt das Mitglied") }

    action {
        customClick { response, audience ->
            plugin.launch {
                EntryManager.removeMember(entry, member.uuid)
                audience.showDialog(removeMemberSuccessNotice(entry, member, editable))
            }
        }
    }
}

private fun removeMemberSuccessNotice(
    entry: TourEntry,
    member: EntryMember,
    editable: Boolean
) = dialog {
    base {
        title(
            buildOwnTourTitle(
                entry,
                buildText { spacer("Mitglieder") },
                member.asComponent(),
                buildText { spacer("Mitglied entfernt") })
        )
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                success("Das Mitglied ")
                variableValue(member.offlinePlayer.name ?: "Unbekannt")
                success(" wurde erfolgreich entfernt")
            }
        }

        type {
            notice(backButton(entry, editable))
        }
    }
}

private fun cancelRemoveMemberButton(
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