@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own.poi

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.servertour.entry.EntryManager
import dev.slne.surf.servertour.entry.Poi
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.servertour.plugin
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase

fun removePoiDialog(entry: TourEntry, poi: Poi): Dialog = dialog {
    base {
        title { error("POI entfernen") }
        afterAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE)

        body {
            plainMessage(400) {
                error("Möchtest du den POI ")
                append(poi)
                error(" wirklich entfernen?")
            }
        }
    }

    type {
        multiAction {
            action(acceptRemovePoiButton(entry, poi))
            action(cancelRemovePoiButton(entry, poi))
        }
    }
}

private fun backButton(entry: TourEntry): ActionButton = actionButton {
    label { text("Zurück") }
    tooltip {
        info("Zurück zu zu der Liste der POIs")
    }

    action {
        playerCallback {
            it.showDialog(ownTourPoisDialog(entry))
        }
    }
}

private fun acceptRemovePoiButton(entry: TourEntry, poi: Poi) = actionButton {
    label { error("Entfernen") }
    tooltip { info("Entfernt den POI") }

    action {
        playerCallback {
            plugin.launch {
                EntryManager.removeMember(entry, poi.uuid)
                it.showDialog(removePoiSuccessNotice(entry, poi))
            }
        }
    }
}

private fun removePoiSuccessNotice(entry: TourEntry, poi: Poi) = dialog {
    base {
        title { primary("POI entfernt") }
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                success("Der POI ")
                append(poi)
                success(" wurde erfolgreich entfernt")
            }
        }

        type {
            notice(backButton(entry))
        }
    }
}

private fun cancelRemovePoiButton(entry: TourEntry, poi: Poi) = actionButton {
    label { text("Abbrechen") }
    tooltip { info("Abbrechen und zurück zum POI") }

    action {
        playerCallback {
            it.showDialog(onePoiDialog(entry, poi))
        }
    }
}