@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own.member

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.servertour.entry.EntryManager
import dev.slne.surf.servertour.entry.EntryMember
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.servertour.plugin
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase

fun removeMemberDialog(entry: TourEntry, member: EntryMember): Dialog = dialog {
    base {
        title { error("Mitglied entfernen") }
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
            action(acceptRemoveMemberButton(entry, member))
            action(cancelRemoveMemberButton(entry, member))

            exitAction(backButton(entry))
        }
    }
}

private fun backButton(entry: TourEntry): ActionButton = actionButton {
    label { text("Zurück") }
    tooltip { info("Zurück zu den Mitgliedern") }

    action {
        playerCallback { player ->
            player.showDialog(ownTourMembersDialog(entry))
        }
    }
}

private fun acceptRemoveMemberButton(entry: TourEntry, member: EntryMember) = actionButton {
    label { error("Entfernen") }
    tooltip { info("Entfernt das Mitglied") }

    action {
        customClick { response, audience ->
            plugin.launch {
                EntryManager.removeMember(entry, member.uuid)
                audience.showDialog(removeMemberSuccessNotice(entry, member))
            }
        }
    }
}

private fun removeMemberSuccessNotice(entry: TourEntry, member: EntryMember) = dialog {
    base {
        title { primary("Mitglied entfernt") }
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                success("Das Mitglied ")
                variableValue(member.offlinePlayer.name ?: "Unbekannt")
                success(" wurde erfolgreich entfernt")
            }
        }

        type {
            notice(backButton(entry))
        }
    }
}

private fun cancelRemoveMemberButton(entry: TourEntry, member: EntryMember) = actionButton {
    label { text("Abbrechen") }
    tooltip { info("Abbrechen und zurück zum Mitglied") }

    action {
        playerCallback { player ->
            player.showDialog(oneMemberDialog(entry, member))
        }
    }
}