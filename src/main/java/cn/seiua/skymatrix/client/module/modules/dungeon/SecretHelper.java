package cn.seiua.skymatrix.client.module.modules.dungeon;

import cn.seiua.skymatrix.client.IToggle;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;

@Event
@Sign(sign = Signs.BETA)
@SModule(name = "secretHelper", category = "dungeon")
public class SecretHelper implements IToggle {

    @Override
    public void enable() {
        throw new RuntimeException("还没写");
    }
}
