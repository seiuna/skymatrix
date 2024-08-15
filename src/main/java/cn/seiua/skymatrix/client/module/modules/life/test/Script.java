package cn.seiua.skymatrix.client.module.modules.life.test;

import cn.seiua.skymatrix.client.module.modules.life.test.scripts.RootScript;
import cn.seiua.skymatrix.event.events.ClientTickEvent;
import cn.seiua.skymatrix.event.events.WorldRenderEvent;

import java.util.LinkedList;

public abstract class Script {

    /**
     * 脚本的下一个脚本
     */
    protected Script next;
    /**
     * 脚本的上一个脚本
     */
    protected Script last;
    /**
     * 脚本的根脚本
     */
    protected RootScript root;
    /**
     * 脚本的索引
     */
    protected int index;
    /**
     * 当前脚本是否执行完毕
     */
    protected boolean finish;
    /**
     * 脚本的callback
     */
    protected ScriptCallBack callBack;

    public abstract void onTick(ClientTickEvent e);
    protected abstract void onRender(WorldRenderEvent e);

    /**
     * 将传入的脚本加入到执行队列中
     * @param script 追加的脚本
     * @return 返回传入的脚本
     */
     public Script append(Script script){
         this.next = script;
         this.next.last = this;
            this.next.root = this.root;
            this.next.index = this.index + 1;
            this.root.scripts.add(script);
            this.callBack = this.root.callBack;

         return script;
     }
    public RootScript build(){
        root.current=root.next;
        return root;
    }


}
