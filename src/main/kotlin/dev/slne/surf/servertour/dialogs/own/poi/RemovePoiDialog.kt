@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own.poi

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.servertour.dialogs.own.buildOwnTourTitle
import dev.slne.surf.servertour.entry.EntryManager
import dev.slne.surf.servertour.entry.Poi
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

fun removePoiDialog(
    entry: TourEntry,
    poi: Poi,
    editable: Boolean
): Dialog = dialog {
    base {
        title(
            buildOwnTourTitle(
                entry,
                buildText { spacer("POIs") },
                poi.asComponent(),
                buildText { spacer("POI entfernen") })
        )
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
            action(acceptRemovePoiButton(entry, poi, editable))
            action(cancelRemovePoiButton(entry, poi, editable))
        }
    }
}

private fun backButton(entry: TourEntry, editable: Boolean): ActionButton = actionButton {
    label { text("Zurück") }
    tooltip {
        info("Zurück zu der Liste der POIs")
    }

    action {
        playerCallback {
            it.showDialog(createTourPoIsDialog(entry, editable))
        }
    }
}

private fun acceptRemovePoiButton(entry: TourEntry, poi: Poi, editable: Boolean) = actionButton {
    label { error("Entfernen") }
    tooltip { info("Entfernt den POI") }

    action {
        playerCallback {
            plugin.launch {
                EntryManager.removePoi(entry, poi)
                it.showDialog(removePoiSuccessNotice(entry, poi, editable))
            }
        }
    }
}

private fun removePoiSuccessNotice(entry: TourEntry, poi: Poi, editable: Boolean) = dialog {
    base {
        title(
            buildOwnTourTitle(
                entry,
                buildText { spacer("POIs") },
                poi.asComponent(),
                buildText { spacer("POI entfernt") })
        )
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                success("Der POI ")
                append(poi)
                success(" wurde erfolgreich entfernt")
            }
        }

        type {
            notice(backButton(entry, editable))
        }
    }
}

private fun cancelRemovePoiButton(entry: TourEntry, poi: Poi, editable: Boolean) = actionButton {
    label { text("Abbrechen") }
    tooltip { info("Abbrechen und zurück zum POI") }

    action {
        playerCallback {
            it.showDialog(onePoiDialog(entry, poi, editable))
        }
    }
}