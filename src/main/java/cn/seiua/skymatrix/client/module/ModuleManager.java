package cn.seiua.skymatrix.client.module;

import cn.seiua.skymatrix.client.*;
import cn.seiua.skymatrix.client.component.*;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.MapValueHolder;
import cn.seiua.skymatrix.message.Message;
import cn.seiua.skymatrix.message.MessageBuilder;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Event
@Component
@Config(name = "ModuleManager")
public class ModuleManager {
    @Use
    public List<Object> components;
    @Use
    public EventManager eventManager;
    @Use
    public Notification notification;
    @Use
    public ConfigManager configManager;

    private static final Logger logger = LoggerFactory.getLogger("ModuleManager");
    @Value(name = "state")
    public MapValueHolder<String, Boolean, Boolean> valueHolder = new MapValueHolder<String, Boolean, Boolean>(new HashMap<>());

    private Map<String, ModuleObj> modules;

    public static ModuleManager instance;

    private static Message message = MessageBuilder.build("Client");

    @Init(level = 999999)
    public void handle() {

        modules = new HashMap<>();
        for (Object o : components) {
            Class c = o.getClass();
            Annotation annotation = c.getAnnotation(SModule.class);
            Event event = (Event) c.getAnnotation(Event.class);
            if (annotation != null) {
                SModule module = (SModule) annotation;
                modules.put(getModuleName(module), new ModuleObj(o, false, module.name(), module.category()));
                valueHolder.value.put(getModuleName(module), false);
                logger.info("Module loaded: " + c.getName() + " " + getModuleName(module));
            }
        }
        instance = this;
        configManager.addCallBack(this::callBack);
    }

    public void callBack() {

        for (Object key : valueHolder.value.keySet()) {

            if (modules.containsKey(key)) {
                Object target = modules.get(key).getTarget();
                if (valueHolder.value.get(key)) {
                    eventManager.register(target.getClass());
                }
                SModule module = target.getClass().getAnnotation(SModule.class);
                Event event = target.getClass().getAnnotation(Event.class);
                if (module != null) {
                    if (module.disable()) {
                        valueHolder.value.put(this.getModuleName(module), false);
                        eventManager.unregister(target.getClass());
                    }
                    if (event != null && event.register()) {
                        valueHolder.value.put(this.getModuleName(module), true);
                        eventManager.register(target.getClass());
                    }


                }

            }


        }
    }


    public boolean isEnable(String moduleName) {
        if (!valueHolder.value.containsKey(moduleName)) {
            logger.warn("不存在的Modulename: " + moduleName);
            valueHolder.value.put(moduleName, false);
            return false;
        }
        return valueHolder.value.get(moduleName);
    }
    public void enable(Class c) {
        SModule sModule = (SModule) c.getAnnotation(SModule.class);
        String name = this.getModuleName(sModule);
        if (!this.isEnable(name)) {
            this.toggle(name);
        }
    }
    public void enable(Object c) {
        enable(c.getClass());
    }
    public void disable(Object c) {
        disable(c.getClass());
    }



    public void disable(Class c) {
        SModule sModule = (SModule) c.getAnnotation(SModule.class);
        String name = this.getModuleName(sModule);
        if (this.isEnable(name)) {
            this.toggle(name);
        }
    }
    public void toggle(String moduleName) {
        Object o = this.modules.get(moduleName).getTarget();
        boolean flag = false;
        boolean flag1 = false;
        IToggle toggle = null;
        if (o instanceof IToggle) {
            toggle = (IToggle) o;
            flag = true;
        }
        PreCheck preCheck = null;
        if (o instanceof PreCheck) {
            preCheck = (PreCheck) o;
            flag1 = true;
        }
        if (!valueHolder.value.containsKey(moduleName)) {
            valueHolder.value.put(moduleName, false);
            logger.warn("不存在的Modulename: " + moduleName);
        }
        try {
            if (!valueHolder.value.get(moduleName)) {
                if (flag1) preCheck.check();
                if (flag) toggle.enable();
                notification.push(new Notice("Module", "Enable " + this.modules.get(moduleName).getName(), NoticeType.INFO));
                eventManager.register(this.modules.get(moduleName).getTarget().getClass());
                valueHolder.value.put(moduleName, true);


            } else {
                if (flag) toggle.disable();
                notification.push(new Notice("Module", "Disable " + this.modules.get(moduleName).getName(), NoticeType.INFO));
                eventManager.unregister(this.modules.get(moduleName).getTarget().getClass());
                valueHolder.value.put(moduleName, false);
            }

        } catch (RuntimeException e) {
            if (e.getMessage() != null) {
                message.sendWarningMessage(Text.translatable(e.getMessage()).getString());
            }
            e.printStackTrace();

        }


    }

    public String getModuleName(SModule module) {
        return module.category() + "." + module.name();
    }


}
