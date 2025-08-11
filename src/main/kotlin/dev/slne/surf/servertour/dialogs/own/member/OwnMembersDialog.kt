@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own.member

import dev.slne.surf.servertour.dialogs.own.buildOwnTourTitle
import dev.slne.surf.servertour.dialogs.own.ownTourDialog
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.DialogBase

fun ownTourMembersDialog(entry: TourEntry, editable: Boolean): Dialog = dialog {
    val members = buildDialogList(entry, editable)

    base {
        title(
            buildOwnTourTitle(
                entry,
                buildText { spacer("Mitglieder") }
            )
        )
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                variableKey("Mitglieder: ")
                variableValue(entry.members.size)
            }
        }
    }

    type {
        if (members.isEmpty()) {
            notice(backButton(entry, editable))
        } else {
            multiAction {
                columns(3)
                exitAction(backButton(entry, editable))

                members.forEach { member ->
                    action {
                        width(200)

                        label {
                            text(
                                member.first.offlinePlayer.name
                                    ?: member.first.offlinePlayer.uniqueId.toString()
                            )
                        }
                        tooltip { info("Öffne Mitglied: ${member.first.offlinePlayer.name}") }

                        action {
                            playerCallback { player -> player.showDialog(member.second) }
                        }
                    }
                }
            }
        }
    }
}

private fun backButton(entry: TourEntry, editable: Boolean) = actionButton {
    label { text("Zurück") }
    tooltip { info("Zurück zur Einreichung") }

    action {
        playerCallback { player ->
            player.showDialog(ownTourDialog(entry, editable))
        }
    }
}

private fun buildDialogList(entry: TourEntry, editable: Boolean) = entry.members
    .sortedBy { it.offlinePlayer.name }
    .map { it to oneMemberDialog(entry, it, editable) }

