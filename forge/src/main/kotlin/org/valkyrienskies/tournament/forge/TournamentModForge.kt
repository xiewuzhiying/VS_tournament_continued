package org.valkyrienskies.tournament.forge

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers
import net.minecraftforge.client.event.ModelEvent
import net.minecraftforge.event.TickEvent.ServerTickEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import org.valkyrienskies.tournament.TickScheduler
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.TournamentItems.TAB
import org.valkyrienskies.tournament.TournamentMod
import org.valkyrienskies.tournament.TournamentMod.init
import org.valkyrienskies.tournament.TournamentMod.initClient
import org.valkyrienskies.tournament.TournamentMod.initClientRenderers
import org.valkyrienskies.tournament.TournamentModels
import org.valkyrienskies.tournament.registry.CreativeTabs.create


@Mod(TournamentMod.MOD_ID)
class TournamentModForge {

    private var happendClientSetup = false

    init {
        getForgeBus().addListener { event: ServerTickEvent ->
            TickScheduler.tickServer(event.server)
        }

        getModBus().addListener { _: FMLCommonSetupEvent ->
            Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                TAB,
                create()
            )
            ValkyrienSkiesMod.vsCore.registerConfigLegacy("vs_tournament", TournamentConfig::class.java)
        }

        getModBus().addListener { event: FMLClientSetupEvent? ->
            clientSetup(
                event
            )
        }
        getModBus().addListener { event: ModelEvent.RegisterAdditional ->
            println("[Tournament] Registering models")
            TournamentModels.MODELS.forEach { rl ->
                println("[Tournament] Registering model $rl")
                event.register(rl)
            }
        }
        getModBus().addListener { event: RegisterRenderers ->
            entityRenderers(
                event
            )
        }

        init()
    }

    private fun clientSetup(event: FMLClientSetupEvent?) {
        if (happendClientSetup) {
            return
        }
        happendClientSetup = true
        initClient()
    }

    private fun entityRenderers(event: RegisterRenderers) {
        initClientRenderers(
            object : TournamentMod.ClientRenderers {
                override fun <T : BlockEntity> registerBlockEntityRenderer(
                    t: BlockEntityType<T>,
                    r: BlockEntityRendererProvider<T>
                ) = event.registerBlockEntityRenderer(t, r)
            }
        )
    }

    companion object {
        fun getModBus(): IEventBus = Bus.MOD.bus().get()
        fun getForgeBus(): IEventBus = Bus.FORGE.bus().get()
    }
}