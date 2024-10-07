package moe.seiua.skymatrix.render

import cn.seiua.skymatrix.event.events.WorldRenderEvent
import cn.seiua.skymatrix.render.GetColor
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d
import java.awt.Color

abstract class AbstractRenderObj {

    protected lateinit var position: Vec3d;
    protected var isEntity: Boolean = false;
    protected lateinit var entity: Entity;
    protected var color: GetColor = GetColor { Color(0f, 0f, 0f) };
    protected var text: (renderObj: AbstractRenderObj) -> String = {
        val p = it.getPos()
        "x: ${p.x.toInt()} y:${p.y.toInt()} z:${p.z.toInt()}";
    };
    protected var renderPos: Boolean = false;
    protected var renderText: Boolean = false;
    protected var renderArrow: Boolean = false;


    fun withPosition(position: Vec3d): AbstractRenderObj {
        this.position = position;
        isEntity = false;
        return this;
    }

    fun witEntity(entity: Entity): AbstractRenderObj {
        this.entity = entity;
        isEntity = true;
        return this;
    }

    fun withColor(color: GetColor): AbstractRenderObj {
        this.color = color;
        return this;
    }

    fun withColor(r: Float, g: Float, b: Float): AbstractRenderObj {
        this.color = GetColor {
            return@GetColor Color(r, g, b);
        }
        return this;
    }

    fun withColor(r: Int, g: Int, b: Int): AbstractRenderObj {
        withColor(r / 255f, g / 255f, b / 255f);
        return this;
    }

    fun withColor(r: Float, g: Float, b: Float, a: Float): AbstractRenderObj {
        this.color = GetColor {
            return@GetColor Color(r, g, b, a);
        }
        return this;
    }

    fun setRenderPos(renderPos: Boolean): AbstractRenderObj {
        this.renderPos = renderPos;
        return this;
    }

    fun setRenderText(renderText: Boolean): AbstractRenderObj {
        this.renderText = renderText;
        return this;
    }

    fun setRenderArrow(renderArrow: Boolean): AbstractRenderObj {
        this.renderArrow = renderArrow;
        return this;
    }

    fun withText(text: (renderObj: AbstractRenderObj) -> String): AbstractRenderObj {
        this.text = text;
        return this;
    }

    fun getPos(): Vec3d {
        if (isEntity) {
            return entity.getLeashPos(WorldRenderEvent.getInstance().tickDelta);
        }
        return position;
    }

    abstract fun render3D();
    abstract fun render2D();

}