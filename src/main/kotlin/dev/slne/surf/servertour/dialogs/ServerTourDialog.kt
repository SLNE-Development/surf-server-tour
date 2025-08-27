@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.servertour.dialogs.own.createOwnTourDialog
import dev.slne.surf.servertour.dialogs.own.review.showcase.createShowcaseTourDialog
import dev.slne.surf.servertour.dialogs.own.review.submitted.createViewSubmittedToursDialog
import dev.slne.surf.servertour.plugin
import dev.slne.surf.servertour.utils.ServerTourPermissionRegistry
import dev.slne.surf.servertour.utils.hasPermission
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase
import java.util.*

fun serverTourDialog(owner: UUID) = dialog {
    base {
        title { primary("Server Tour") }
        afterAction(DialogBase.DialogAfterAction.NONE)
    }

    type {
        multiAction {
            columns(2)

            action(listOwnTours(owner))
            action(createOwnTourButton())

            if (owner.hasPermission(ServerTourPermissionRegistry.REVIEWER)) {
                action(createViewSubmittedToursButton())
            }

            if (owner.hasPermission(ServerTourPermissionRegistry.SHOWCASE)) {
                action(createShowcaseTourButton())
            }
        }
    }
}

private fun createOwnTourButton(): ActionButton = actionButton {
    label { text("Einreichung erstellen") }
    tooltip { info("Erstellt eine neue Einreichung") }

    action {
        playerCallback { player ->
            player.showDialog(createOwnTourDialog())
        }
    }
}

private fun listOwnTours(owner: UUID): ActionButton = actionButton {
    label { info("Eigene Einreichungen") }

    action {
        playerCallback {
            plugin.launch {
                it.showDialog(listOwnToursDialog(owner))
            }
        }
    }
}

private fun createShowcaseTourButton(): ActionButton = actionButton {
    label { variableValue("Einreichungen ansehen") }
    tooltip { info("Sieh dir alle Einreichungen an") }

    action {
        playerCallback { player ->
            plugin.launch {
                player.showDialog(createShowcaseTourDialog())
            }
        }
    }
}

private fun createViewSubmittedToursButton(): ActionButton = actionButton {
    label { variableValue("Einreichungen ansehen") }
    tooltip { info("Sieh dir alle Einreichungen an, welche auf eine Überprüfung warten") }

    action {
        playerCallback { player ->
            plugin.launch {
                player.showDialog(createViewSubmittedToursDialog())
            }
        }
    }
}