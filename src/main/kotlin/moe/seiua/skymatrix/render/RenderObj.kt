package moe.seiua.skymatrix.render

import cn.seiua.skymatrix.SkyMatrix
import cn.seiua.skymatrix.event.events.HudRenderEvent
import cn.seiua.skymatrix.event.events.WorldRenderEvent
import cn.seiua.skymatrix.gui.ClickGui
import cn.seiua.skymatrix.gui.ui.UI
import cn.seiua.skymatrix.utils.MathUtils
import cn.seiua.skymatrix.utils.RenderUtilsV2
import com.mojang.blaze3d.platform.GlConst
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.util.Window
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector4f

class RenderObj : AbstractRenderObj() {
    /**
     * size 16*16
     */
    val ARROW: Identifier = Identifier.of("skymatrix", "textures/arrow.png")

    private var screenPos: Vector4f? = null
    override fun render3D() {
        val matrixStack = WorldRenderEvent.getInstance().matrixStack;
        val delta = WorldRenderEvent.getInstance().tickDelta;
        val window = SkyMatrix.mc.window;
        val pos = getPos();
        if (renderArrow) {
            updateScreenPos(matrixStack, position);
        }
        if (renderPos) {
            if (isEntity) {
                RenderUtilsV2.renderOutlineBlock(
                    matrixStack,
                    BlockPos(pos.x.toInt(), pos.y.toInt(), pos.z.toInt()), color.color
                );
            } else {

                RenderUtilsV2.renderOutlineBlock(
                    matrixStack,
                    BlockPos(pos.x.toInt(), pos.y.toInt(), pos.z.toInt()), color.color
                );
            }
        }
        if (renderText) {
            if (isEntity) {

            } else {
                RenderUtilsV2.renderText(
                    matrixStack,
                    ClickGui.fontRenderer28,
                    text.invoke(
                        this
                    ),
                    pos.add(0.0, 0.9, 0.0),
                    color.color,
                    delta
                )
            }
        }
    }

    override fun render2D() {
        if (renderArrow) {
            val matrixStack = HudRenderEvent.instance.context.matrices;
            if (screenPos == null) {
                return
            }
            val window = SkyMatrix.mc.window
            val center = Vec2f(window.width.toFloat() / 2, window.height.toFloat() / 2)
            var vec =
                Vec3d((screenPos!!.x - center.x).toDouble(), (screenPos!!.y - center.y).toDouble(), 0.0).normalize()
            if (screenPos!!.z >= 1) {
                vec = vec.multiply(-1.0)
            }
            val intersect = getBoundingIntersectPoint(vec, window)
            matrixStack.push()
            val ms = UI.getS()
            RenderSystem.enableBlend()
            RenderSystem.texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MAG_FILTER, GlConst.GL_LINEAR)
            val angle = (Math.toDegrees(
                StrictMath.atan2(
                    vec.y,
                    vec.x
                )
            )
                    + 90f)
            matrixStack.translate((intersect!!.x - 16) / ms, (intersect!!.y - 16) / ms, 0f)
            matrixStack.multiply(Quaternionf().rotationXYZ(0f, 0f, Math.toRadians(angle).toFloat()))
            HudRenderEvent.instance.context.drawTexture(ARROW, 0, 0, 0f, 0f, 16, 16, 16, 16)
            matrixStack.pop()
        }
    }


    private fun updateScreenPos(matrixStack: MatrixStack, pos: Vec3d) {
        val camera = WorldRenderEvent.getInstance().camera
        val cameraPos = camera.pos
        matrixStack.push()
        val screenPos = Vector4f(
            (pos.x - cameraPos.x).toFloat(),
            (pos.y - cameraPos.y).toFloat(),
            (pos.z - cameraPos.z).toFloat(),
            1.0f
        )
        val modelViewProjection = Matrix4f()
        matrixStack.peek().positionMatrix[modelViewProjection]
        modelViewProjection.mul(WorldRenderEvent.getInstance().projectionMatrix)
        modelViewProjection.mul(WorldRenderEvent.getInstance().positionMatrix)
        modelViewProjection.transform(screenPos)
        screenPos.x /= screenPos.w
        screenPos.y /= screenPos.w
        screenPos.z /= screenPos.w
        screenPos.x = (screenPos.x + 1.0f) * SkyMatrix.mc.window.framebufferWidth / 2.0f
        screenPos.y = (1.0f - screenPos.y) * SkyMatrix.mc.window.framebufferHeight / 2.0f
        screenPos.z = (1.0f + screenPos.z) / 2.0f
        matrixStack.peek()
        this.screenPos = screenPos
    }

    private fun getBoundingIntersectPoint(position: Vec3d, window: Window): Vec2f? {
        val ms = UI.getS()
        //将点落在屏幕边框位置设置四条边框的向量
        val screenBounds = arrayOf(
            Vec2f(0 + 40 * ms, 0 + 40 * ms),
            Vec2f(window.width - 40 * ms, 0f),
            Vec2f(window.width - 40 * ms, window.height - 50 * ms),
            Vec2f(0 + 40 * ms, window.height - 60 * ms)
        )
        val center = Vec2f(window.width.toFloat() / 2, window.height.toFloat() / 2)
        val pos = Vec3d((center.x + position.x.toFloat()).toDouble(), (center.y + position.y.toFloat()).toDouble(), 0.0)
        //计算每两个向量所构成的直线与pos的交点
        var closest: Vec2f? = null
        for (i in screenBounds.indices) {
            val v = screenBounds[i]
            val v2 = screenBounds[(i + 1) % screenBounds.size]
            val intersection = MathUtils.getIntersectionPoint(
                v, v2, center, Vec2f(
                    center.x,
                    center.y
                ).add(Vec2f(position.x.toFloat(), position.y.toFloat()).multiply(1600f))
            )
            if (intersection != null) {
                if (closest == null || Vec2f(intersection.x - center.x, intersection.y - center.y).length() < Vec2f(
                        closest.x - center.x,
                        closest.y - center.y
                    ).length()
                ) {
                    closest = intersection
                }
            }
        }
//        val v = MathUtils.getIntersectionPoint(Vec2f(closest!!.x, closest.y), Vec2f(pos.x.toFloat(), pos.y.toFloat()))

        return closest
    }
}