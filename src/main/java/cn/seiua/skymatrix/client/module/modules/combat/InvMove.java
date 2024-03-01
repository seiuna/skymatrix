package cn.seiua.skymatrix.client.module.modules.combat;

import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.KeyBind;
import cn.seiua.skymatrix.utils.ReflectUtils;

import java.util.Arrays;

@Event
@Sign(sign = Signs.FREE)
@SModule(name = "InvMove", category = "combat")
public class InvMove {

    @Value(name = "keyBind")
    public KeyBind keyBind = new KeyBind(Arrays.asList(), ReflectUtils.getModuleName(this));


}
