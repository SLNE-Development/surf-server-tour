@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own.member

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.servertour.dialogs.own.buildOwnTourTitle
import dev.slne.surf.servertour.dialogs.own.ownTourDialog
import dev.slne.surf.servertour.entry.EntryManager
import dev.slne.surf.servertour.entry.EntryMember
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.servertour.plugin
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase

fun addMemberToOwnTourDialog(entry: TourEntry, editable: Boolean): Dialog = dialog {
    base {
        title(buildOwnTourTitle(entry, buildText { spacer("Mitglied hinzufügen") }))
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            input {
                text("name") {
                    label { text("Name") }
                    width(400)
                    maxLength(16)
                }
            }
        }

        type {
            confirmation(addMemberButton(entry, editable), cancelAddMemberButton(entry, editable))
        }
    }
}

private fun backButton(entry: TourEntry, editable: Boolean): ActionButton = actionButton {
    label { text("Zurück") }
    tooltip { info("Zurück zur Einreichung") }

    action {
        playerCallback { player ->
            player.showDialog(ownTourDialog(entry, editable))
        }
    }
}

private fun addMemberButton(entry: TourEntry, editable: Boolean): ActionButton = actionButton {
    label { success("Hinzufügen") }
    tooltip { info("Mitglied hinzufügen") }

    action {
        customClick { response, audience ->
            val name = response.getText("name") ?: run {
                audience.showDialog(failedNameNotice(entry, editable))
                return@customClick
            }

            if (name.length < 3 || name.length > 16) {
                audience.showDialog(failedNameNotice(entry, editable))
                return@customClick
            }

            val offlinePlayer = server.getOfflinePlayer(name)
            if (!offlinePlayer.hasPlayedBefore()) {
                audience.showDialog(playerNotFoundNotice(entry, name, editable))
                return@customClick
            }

            if (entry.members.any { it.offlinePlayer.uniqueId == offlinePlayer.uniqueId }) {
                audience.showDialog(alreadyMemberNotice(entry, name, editable))
                return@customClick
            }

            plugin.launch {
                val member = EntryManager.addMember(entry, offlinePlayer.uniqueId)

                if (member == null) {
                    audience.showDialog(playerNotFoundNotice(entry, name, editable))
                    return@launch
                }

                audience.showDialog(memberAddedNotice(entry, member, editable))
            }
        }
    }
}

private fun cancelAddMemberButton(entry: TourEntry, editable: Boolean): ActionButton =
    actionButton {
        label { text("Abbrechen") }
        tooltip { info("Abbrechen und zurück zur Einreichung") }

        action {
            playerCallback { player ->
                player.showDialog(ownTourDialog(entry, editable))
            }
        }
    }

private fun backToInputButton(entry: TourEntry, editable: Boolean): ActionButton = actionButton {
    label { text("Zurück") }
    tooltip { info("Zurück zum Eingabefeld") }

    action {
        playerCallback { player ->
            player.showDialog(addMemberToOwnTourDialog(entry, editable))
        }
    }
}

private fun failedNameNotice(entry: TourEntry, editable: Boolean): Dialog = dialog {
    base {
        title(
            buildOwnTourTitle(
                entry,
                buildText { spacer("Fehler beim Hinzufügen eines Mitglieds") })
        )
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                error("Der Name muss zwischen ")
                variableValue("3")
                error(" und ")
                variableValue("16")
                error(" Zeichen lang sein.")
            }
        }
    }

    type {
        notice(backToInputButton(entry, editable))
    }
}

private fun playerNotFoundNotice(entry: TourEntry, name: String, editable: Boolean): Dialog =
    dialog {
        base {
            title(buildOwnTourTitle(entry, buildText { spacer("Spieler nicht gefunden") }))
            afterAction(DialogBase.DialogAfterAction.NONE)

            body {
                plainMessage(400) {
                    error("Der Spieler ")
                    variableValue(name)
                    error(" konnte nicht gefunden werden.")
                }
            }
        }

        type {
            notice(backToInputButton(entry, editable))
        }
    }

private fun alreadyMemberNotice(entry: TourEntry, name: String, editable: Boolean): Dialog =
    dialog {
        base {
            title(buildOwnTourTitle(entry, buildText { spacer("Spieler bereits Mitglied") }))
            afterAction(DialogBase.DialogAfterAction.NONE)

            body {
                plainMessage(400) {
                    error("Der Spieler ")
                    variableValue(name)
                    error(" ist bereits Mitglied der Einreichung.")
                }
            }
        }

        type {
            notice(backToInputButton(entry, editable))
        }
    }

private fun memberAddedNotice(entry: TourEntry, member: EntryMember, editable: Boolean): Dialog =
    dialog {
        base {
            title(buildOwnTourTitle(entry, buildText { spacer("Mitglied hinzugefügt") }))
            afterAction(DialogBase.DialogAfterAction.NONE)

            body {
                plainMessage(400) {
                    success("Das Mitglied ")
                    append(member)
                    success(" wurde erfolgreich hinzugefügt.")
                }
            }
        }

        type {
            notice(backButton(entry, editable))
        }
    }