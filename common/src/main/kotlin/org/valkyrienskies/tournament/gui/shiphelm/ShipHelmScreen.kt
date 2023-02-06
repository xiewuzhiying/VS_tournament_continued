package org.valkyrienskies.Tournament.gui.shiphelm

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.valkyrienskies.Tournament.TournamentConfig
import org.valkyrienskies.Tournament.TournamentMod

class ShipHelmScreen(handler: ShipHelmScreenMenu, playerInventory: Inventory, text: Component) :
    AbstractContainerScreen<ShipHelmScreenMenu>(handler, playerInventory, text) {

    lateinit var assembleButton: ShipHelmButton
    lateinit var alignButton: ShipHelmButton
    lateinit var todoButton: ShipHelmButton

    init {
        titleLabelX = 120
    }

    override fun init() {
        super.init()
        val x = (width - imageWidth) / 2
        val y = (height - imageHeight) / 2

        assembleButton = addRenderableWidget(
            ShipHelmButton(x + BUTTON_1_X, y + BUTTON_1_Y, ASSEMBLE_TEXT, font) {
                // Send assemble or dissemble packet
                if (this.menu.assembled) {
                    assembleButton.active = TournamentConfig.SERVER.enableDisassembly
                    minecraft!!.gameMode!!.handleInventoryButtonClick(menu.containerId, 3)
                } else {
                    minecraft!!.gameMode!!.handleInventoryButtonClick(menu.containerId, 0)
                }
            }
        )



        alignButton = addRenderableWidget(
            ShipHelmButton(x + BUTTON_2_X, y + BUTTON_2_Y, ALIGN_TEXT, font) {
                minecraft!!.gameMode!!.handleInventoryButtonClick(menu.containerId, 1)
            }
        )

        todoButton = addRenderableWidget(
            ShipHelmButton(x + BUTTON_3_X, y + BUTTON_3_Y, TODO_TEXT, font) {
                minecraft!!.gameMode!!.handleInventoryButtonClick(menu.containerId, 3)
            }
        )
        todoButton.active = TournamentConfig.SERVER.enableDisassembly
    }

    override fun renderBg(matrixStack: PoseStack, partialTicks: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, TEXTURE)
        val x = (width - imageWidth) / 2
        val y = (height - imageHeight) / 2
        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight)
    }

    override fun renderLabels(matrixStack: PoseStack, i: Int, j: Int) {
        font.draw(matrixStack, title, titleLabelX.toFloat(), titleLabelY.toFloat(), 0x404040)

        if (this.menu.aligning) {
            alignButton.message = ALIGNING_TEXT
            alignButton.active = false
        } else {
            alignButton.message = ALIGN_TEXT
            alignButton.active = true
        }

        // TODO render stats
    }

    // mojank doesn't check mouse release for their widgets for some reason
    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        isDragging = false
        if (getChildAt(mouseX, mouseY).filter { it.mouseReleased(mouseX, mouseY, button) }.isPresent) return true

        return super.mouseReleased(mouseX, mouseY, button)
    }

    companion object { // TEXTURE DATA
        internal val TEXTURE = ResourceLocation(TournamentMod.MOD_ID, "textures/gui/ship_helm.png")

        private const val BUTTON_1_X = 10
        private const val BUTTON_1_Y = 73
        private const val BUTTON_2_X = 10
        private const val BUTTON_2_Y = 103
        private const val BUTTON_3_X = 10
        private const val BUTTON_3_Y = 133

        private val ASSEMBLE_TEXT = TranslatableComponent("gui.vs_tournament.assemble")
        private val DISSEMBLE_TEXT = TranslatableComponent("gui.vs_tournament.disassemble")
        private val ALIGN_TEXT = TranslatableComponent("gui.vs_tournament.align")
        private val ALIGNING_TEXT = TranslatableComponent("gui.vs_tournament.aligning")
        private val TODO_TEXT = TranslatableComponent("gui.vs_tournament.todo")
    }
}
