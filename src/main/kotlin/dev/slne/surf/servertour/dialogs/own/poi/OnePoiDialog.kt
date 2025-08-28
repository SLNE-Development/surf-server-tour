@file:Suppress("UnstableApiUsage")
@file:OptIn(NmsUseWithCaution::class)

package dev.slne.surf.servertour.dialogs.own.poi

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.servertour.dialogs.own.buildOwnTourTitle
import dev.slne.surf.servertour.entry.EntryManager
import dev.slne.surf.servertour.entry.Poi
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.servertour.plugin
import dev.slne.surf.servertour.view.viewManager
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.clearDialogs
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.bukkit.api.nms.NmsUseWithCaution
import dev.slne.surf.surfapi.core.api.messages.adventure.appendNewline
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.DialogBase

fun buildPoiBody(entry: TourEntry, poi: Poi) = buildText {
    val owner = poi.owner

    variableKey("Besitzer: ")
    appendNewline()
    if (owner == null) {
        variableValue("Kein Besitzer gesetzt")
    } else {
        append(owner)
    }
    appendNewline(2)

    variableKey("Position: ")
    appendNewline()
    variableKey("X: ")
    variableValue(poi.location.x.toInt())
    spacer(", ")
    variableKey("Y: ")
    variableValue(poi.location.y.toInt())
    spacer(", ")
    variableKey("Z: ")
    variableValue(poi.location.z.toInt())
    spacer(" in ")
    variableKey("Welt: ")
    variableValue(poi.location.world?.name ?: "Unbekannt")
    appendNewline(2)

    variableKey("Beschreibung: ")
    appendNewline()
    variableValue(poi.description.ifBlank { "Keine Beschreibung vorhanden" })
}

fun onePoiDialog(
    entry: TourEntry,
    poi: Poi,
    editable: Boolean
): Dialog = dialog {
    base {
        title(
            buildOwnTourTitle(
                entry,
                buildText { spacer("POIs") },
                poi.asComponent()
            )
        )
        externalTitle {
            text(poi.name)
        }
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                append(buildPoiBody(entry, poi))
            }
        }
    }

    type {
        if (editable) {
            multiAction {
                columns(2)

                action(changeNameButton(entry, poi, editable))
                action(changeDescriptionButton(entry, poi, editable))
                action(changeLocationButton(entry, poi, editable))
                action(changeOwnerButton(entry, poi, editable))
                action(removePoiButton(entry, poi, editable))
                action(viewButton(poi))

                exitAction(backButton(entry, editable))
            }
        } else {
            multiAction {
                columns(2)
                backButton(entry, editable)
                action(viewButton(poi))
            }
        }
    }
}

private fun viewButton(poi: Poi) = actionButton {
    label { text("PoI Ansehen") }
    tooltip { info("Teleportiert dich für 5 Sekunden zu diesem PoI") }

    action {
        playerCallback {
            plugin.launch {
                it.clearDialogs()
                viewManager.viewPoi(it, poi)
            }
        }
    }
}

private fun backButton(entry: TourEntry, editable: Boolean) = actionButton {
    label { text("Zurück") }
    tooltip { info("Zurück zu den POIs") }

    action {
        playerCallback { it.showDialog(ownTourPoisDialog(entry, editable)) }
    }
}

private fun changeOwnerButton(
    entry: TourEntry,
    poi: Poi,
    editable: Boolean
) = actionButton {
    label { text("Besitzer ändern") }
    tooltip { info("Ändert den Besitzer des POI") }

    action {
        playerCallback { player ->
            player.showDialog(createChangePoiOwnerDialog(entry, poi, editable))
        }
    }
}

private fun changeLocationButton(
    entry: TourEntry,
    poi: Poi,
    editable: Boolean
) = actionButton {
    label { text("Position ändern") }
    tooltip { info("Ändert die Position des POI") }

    action {
        playerCallback { player ->
            plugin.launch {
                EntryManager.updatePoi(entry, poi) {
                    it.location = player.location
                }

                player.showDialog(createChangedLocationNotice(entry, poi, editable))
            }
        }
    }
}

private fun createChangedLocationNotice(
    entry: TourEntry,
    poi: Poi,
    editable: Boolean
) = dialog {
    base {
        title(
            buildOwnTourTitle(
                entry,
                buildText { spacer("POIs") },
                poi.asComponent(),
                buildText { spacer("Position geändert") }
            )
        )
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                success("Die Position des POI ")
                append(poi)
                success(" wurde erfolgreich geändert")
            }
        }

        type {
            notice {
                action {
                    label { text("Zurück") }
                    tooltip { info("Zurück zu dem POI") }
                    playerCallback { it.showDialog(onePoiDialog(entry, poi, editable)) }
                }
            }
        }
    }
}

private fun changeNameButton(
    entry: TourEntry,
    poi: Poi,
    editable: Boolean
) = actionButton {
    label { text("Name ändern") }
    tooltip { info("Ändert den Namen des POI") }

    action {
        playerCallback { it.showDialog(renamePoiDialog(entry, poi, editable)) }
    }
}

private fun changeDescriptionButton(
    entry: TourEntry,
    poi: Poi,
    editable: Boolean
) = actionButton {
    label { text("Beschreibung ändern") }
    tooltip { info("Ändert die Beschreibung des POI") }

    action {
        playerCallback { player ->
            player.showDialog(changePoiDescriptionDialog(entry, poi, editable))
        }
    }
}

private fun removePoiButton(
    entry: TourEntry,
    poi: Poi,
    editable: Boolean
) = actionButton {
    label { error("Entfernen") }
    tooltip { error("Entfernt den POI von der Einreichung") }

    action {
        playerCallback { player ->
            player.showDialog(removePoiDialog(entry, poi, editable))
        }
    }
}
