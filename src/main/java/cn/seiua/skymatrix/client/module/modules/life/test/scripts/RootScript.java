package cn.seiua.skymatrix.client.module.modules.life.test.scripts;

import cn.seiua.skymatrix.client.Client;
import cn.seiua.skymatrix.client.module.modules.life.test.Script;
import cn.seiua.skymatrix.client.module.modules.life.test.ScriptCallBack;
import cn.seiua.skymatrix.event.events.ClientTickEvent;
import cn.seiua.skymatrix.event.events.WorldRenderEvent;
import net.minecraft.text.Text;

import java.util.LinkedList;

public class RootScript extends Script {
    public LinkedList<Script> scripts;
    public Script current;
    public RootScript(ScriptCallBack callBack) {
        this.scripts = new LinkedList<>();
        this.scripts.add(this);
        this.index= 0;
        this.root = this;
        this.last=null;
        this.callBack=callBack;
        this.finish=true;
    }

    @Override
    public void onTick(ClientTickEvent e) {

    }

    @Override
    protected void onRender(WorldRenderEvent e) {

    }

    public void printLog(){
        Client.sendDebugMessage(Text.of("current:"+current.getClass().getSimpleName()+" index:"+index+" length:"+scripts.size()));
    }

}
