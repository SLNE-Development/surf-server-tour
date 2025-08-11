@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own.poi

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.servertour.dialogs.own.buildOwnTourTitle
import dev.slne.surf.servertour.entry.EntryManager
import dev.slne.surf.servertour.entry.EntryMember
import dev.slne.surf.servertour.entry.Poi
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.servertour.plugin
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import io.papermc.paper.registry.data.dialog.DialogBase

fun createChangePoiOwnerDialog(
    entry: TourEntry,
    poi: Poi,
    editable: Boolean
) = dialog {
    val buttons = buildDialogButtons(entry, poi, editable)

    base {
        title(
            buildOwnTourTitle(
                entry,
                buildText { spacer("POIs") },
                poi.asComponent(),
                buildText { spacer("Besitzer ändern") })
        )
        afterAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE)

        if (buttons.isEmpty()) {
            body {
                plainMessage(400) {
                    error("Es gibt keine Mitglieder, die als neuer Besitzer gesetzt werden können.")
                }
            }
        }
    }

    type {
        if (buttons.isEmpty()) {
            notice(backButton(entry, poi, editable))
        } else {
            multiAction {
                exitAction(backButton(entry, poi, editable))
                columns(3)

                buttons.forEach { action(it.second) }
            }
        }
    }
}

private fun backButton(
    entry: TourEntry,
    poi: Poi,
    editable: Boolean
) = actionButton {
    label { text("Zurück") }
    tooltip { info("Zurück zum POI") }

    action {
        playerCallback { it.showDialog(onePoiDialog(entry, poi, editable)) }
    }
}

private fun buildDialogButtons(
    entry: TourEntry,
    poi: Poi,
    editable: Boolean
) = entry.members
    .sortedBy { it.offlinePlayer.name }
    .map { member ->
        member to actionButton {
            val name = member.offlinePlayer.name ?: member.offlinePlayer.uniqueId.toString()

            label { text(name) }
            tooltip {
                info("Setze ")
                append(member)
                info(" als neuen Besitzer des POI ")
                append(poi)
            }
            width(200)

            action {
                playerCallback { player ->
                    plugin.launch {
                        EntryManager.updatePoi(entry, poi) {
                            it.owner = member
                        }

                        player.showDialog(
                            createChangePoiOwnerSuccessNotice(
                                entry,
                                poi,
                                member,
                                editable
                            )
                        )
                    }
                }
            }
        }
    }

private fun createChangePoiOwnerSuccessNotice(
    entry: TourEntry,
    poi: Poi,
    newOwner: EntryMember,
    editable: Boolean
) = dialog {
    base {
        title(
            buildOwnTourTitle(
                entry,
                buildText { spacer("POIs") },
                poi.asComponent(),
                buildText { spacer("Besitzer geändert") })
        )
        body {
            plainMessage(400) {
                success("Der POI ")
                append(poi)
                success(" wurde erfolgreich auf ")
                append(newOwner)
                success(" übertragen.")
            }
        }
    }

    type {
        notice {
            label { text("Zurück") }
            tooltip { info("Zurück zum POI") }

            action {
                playerCallback { it.showDialog(onePoiDialog(entry, poi, editable)) }
            }
        }
    }
}