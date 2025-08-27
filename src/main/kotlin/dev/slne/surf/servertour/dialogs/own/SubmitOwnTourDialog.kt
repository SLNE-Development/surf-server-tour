@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.servertour.plugin
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.core.api.messages.adventure.appendNewline
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickOpensUrl
import io.papermc.paper.registry.data.dialog.DialogBase
import net.kyori.adventure.text.format.TextDecoration

fun createSubmitOwnTourDialog(
    entry: TourEntry,
    editable: Boolean
) = dialog {
    base {
        title(buildOwnTourTitle(entry, buildText { spacer("Einreichen") }))
        afterAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE)

        body {
            plainMessage(400) {
                success("Möchtest du die Einreichung ")
                append(entry)
                success(" wirklich einreichen?")
                appendNewline(2)

                error("Achtung: ", TextDecoration.BOLD)
                info("Sobald du die Einreichung abgeschickt hast, kann sie nicht mehr bearbeitet werden.")
                appendNewline(2)

                info("Die Einreichung wird im Anschluss von einem Teammitglied geprüft und gegebenenfalls freigeschaltet, sollte sie den ")
                append {
                    variableValue("Anforderungen und Richtlinien")

                    hoverEvent(buildText {
                        info("Hier klicken, um die Anforderungen und Richtlinien zu sehen")
                    })

                    clickOpensUrl("https://server.castcrafter.de") // TODO: Replace with actual URL
                }
                info(" entsprechen.")
            }
        }
    }

    type {
        confirmation(
            submitButton(entry),
            cancelSubmitButton(entry, editable)
        )
    }
}

private fun submitButton(entry: TourEntry) = actionButton {
    label { success("Einreichung abschicken") }
    tooltip { info("Reicht die Einreichung ein") }

    action {
        playerCallback {
            plugin.launch {
                entry.submit()

                it.showDialog(entrySubmittedNotice(entry))
            }
        }
    }
}

private fun cancelSubmitButton(entry: TourEntry, editable: Boolean) = actionButton {
    label { text("Abbrechen") }
    tooltip { info("Abbrechen und zurück zur Einreichung") }

    action {
        playerCallback { it.showDialog(ownTourDialog(entry, editable)) }
    }
}

private fun entrySubmittedNotice(entry: TourEntry) = dialog {
    base {
        title(
            buildOwnTourTitle(
                entry,
                buildText { spacer("Einreichung abgeschickt") }
            )
        )
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                success("Die Einreichung ")
                append(entry)
                success(" wurde erfolgreich eingereicht")
            }
        }

        type {
            notice {
                action {
                    label { text("Zurück") }
                    tooltip { info("Zurück zu der Einreichung") }

                    playerCallback { it.showDialog(ownTourDialog(entry, entry.isDraft())) }
                }
            }
        }
    }
}