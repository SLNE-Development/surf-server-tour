@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs

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
            action(listOwnTours(owner))
        }
    }
}

private fun listOwnTours(owner: UUID): ActionButton = actionButton {
    label { text("Eigene Einreichungen") }
    action {
        playerCallback {
            it.showDialog(listOwnToursDialog(owner))
        }
    }
}