@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.servertour.dialogs.listOwnToursDialog
import dev.slne.surf.servertour.entry.EntryManager
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

fun removeOwnTourDialog(entry: TourEntry, editable: Boolean): Dialog = dialog {
    base {
        title(
            buildOwnTourTitle(
                entry,
                buildText { spacer("Einreichung entfernen") }
            )
        )
        afterAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE)

        body {
            plainMessage(400) {
                error("Möchtest du die Einreichung ")
                append(entry)
                error(" wirklich entfernen?")
            }
        }
    }

    type {
        multiAction {
            action(acceptRemoveEntryButton(entry))
            action(cancelRemoveEntryButton(entry, editable))
        }
    }
}

private fun backButton(entry: TourEntry): ActionButton = actionButton {
    label { text("Zurück") }
    tooltip {
        info("Zurück zu der Liste der Einreichungen")
    }

    action {
        playerCallback {
            it.showDialog(listOwnToursDialog(entry.owner.uuid))
        }
    }
}

private fun acceptRemoveEntryButton(entry: TourEntry) = actionButton {
    label { error("Entfernen") }
    tooltip { info("Entfernt die Einreichung") }

    action {
        playerCallback {
            plugin.launch {
                EntryManager.delete(entry)
                it.showDialog(removeEntrySuccessNotice(entry))
            }
        }
    }
}

private fun removeEntrySuccessNotice(entry: TourEntry) = dialog {
    base {
        title(
            buildOwnTourTitle(
                entry,
                buildText { spacer("Einreichung entfernt") })
        )
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                success("Die Einreichung ")
                append(entry)
                success(" wurde erfolgreich entfernt")
            }
        }

        type {
            notice(backButton(entry))
        }
    }
}

private fun cancelRemoveEntryButton(entry: TourEntry, editable: Boolean) = actionButton {
    label { text("Abbrechen") }
    tooltip { info("Abbrechen und zurück zur Einreichung") }

    action {
        playerCallback {
            it.showDialog(ownTourDialog(entry, editable))
        }
    }
}