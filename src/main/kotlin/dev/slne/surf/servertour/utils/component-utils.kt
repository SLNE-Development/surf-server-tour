package dev.slne.surf.servertour.utils

import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder

fun SurfComponentBuilder.appendEmDash() = append(CommonComponents.EM_DASH).appendSpace()