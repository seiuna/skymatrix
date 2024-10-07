package cn.seiua.skymatrix.client.module.modules.test

import cn.seiua.skymatrix.client.IToggle
import cn.seiua.skymatrix.client.component.Event
import cn.seiua.skymatrix.client.component.SModule
import cn.seiua.skymatrix.client.module.Sign
import cn.seiua.skymatrix.client.module.Signs
import cn.seiua.skymatrix.config.Value
import cn.seiua.skymatrix.config.option.KeyBind
import cn.seiua.skymatrix.event.EventTarget
import cn.seiua.skymatrix.event.events.ClientTickEvent
import cn.seiua.skymatrix.event.events.HudRenderEvent
import cn.seiua.skymatrix.event.events.WorldRenderEvent
import cn.seiua.skymatrix.utils.ReflectUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.seiua.skymatrix.loader.mainDispatcher
import moe.seiua.skymatrix.render.RenderObj
import net.minecraft.util.math.Vec3d

@Event
@Sign(sign = Signs.FREE)
@SModule(name = "TestRender23", category = "test")
class Avc : IToggle {
    @Value(name = "keyBind")
    var keyBind: KeyBind = KeyBind(mutableListOf(), ReflectUtils.getModuleName(this))

    val renderObj = RenderObj()

    @EventTarget
    fun onRender(event: ClientTickEvent) {
    }

    @EventTarget
    fun onRender3D(event: WorldRenderEvent) {
        renderObj.withPosition(Vec3d(0.0, 0.0, 0.0))
            .withColor(0.0f, 0.0f, 0.0f)
            .setRenderPos(true)
            .setRenderText(true)
            .setRenderArrow(true)
            .render3D()

    }

    @EventTarget
    fun render2D(event: HudRenderEvent) {
        renderObj.render2D()
    }

    override fun enable() {

        CoroutineScope(mainDispatcher).launch {
            CoroutineScope(Dispatchers.IO).launch {
                println("IO")
                println("IO")
                println("IO")
                println(Thread.currentThread().name)
            }
            println("enable")
            println("enable")
            println("enable")
            println("enable")
            println("enable")
            println("enable")
            println("enable")
            println(Thread.currentThread().name)
        }

    }

}