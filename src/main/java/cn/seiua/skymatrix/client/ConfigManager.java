package cn.seiua.skymatrix.client;

import cn.seiua.skymatrix.client.component.*;
import cn.seiua.skymatrix.client.module.ModuleManager;
import cn.seiua.skymatrix.config.*;
import cn.seiua.skymatrix.utils.ReflectUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sun.jna.Native;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.*;

@Component
public class ConfigManager {

    @Use
    public List<Object> components;

    public static final String VERSION = "1.0";
    @Use
    public List<ExtraConfig> extraConfigs;
    @Use
    public LocalConfigStore localConfigStore;
    public static final String MODULE = "module";
    @Use
    public ModuleManager moduleManager;
    public static final String COMMON = "common";
    private Store store;
    private String uuid;
    public static final String CATEGORY = "category";
    public Store store1 = (Store) Native.loadLibrary(Store.class);
    public Map<String, Map<String, Map<String, Object>>> configs;

    private static final Logger logger = LoggerFactory.getLogger("ConfigManager");
    private List<Object> configObjs;
    private Map<String, Profile> profiles;
    private Profile current;

    public Object getConfigByKey(String cate, String gname, String cname) {
        return null;

    }

    private void putConfig(String cate, String gname, String cname, Object value) {
        if (configs.get(cate) == null) configs.put(cate, new HashMap<>());
        if (configs.get(cate).get(gname) == null) configs.get(cate).put(gname, new HashMap<>());
        configs.get(cate)
                .get(gname).put(cname, value);


    }

    private List<Run> callbacks = new ArrayList<>();

    public String reloadProfiles() {

        String uuid = null;
        String[] uuids = store.loadUUIDs();
        for (String u : uuids) {
            try {
                String json = new String(store.load(u), "utf8");
                Profile profile = JSON.parseObject(json, Profile.class);
                if (profile.isSelected()) {
                    uuid = profile.getUuid();
                    this.current = profile;
                } else {
                    profile.setSelected(false);
                }
                profiles.put(profile.getUuid(), profile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (uuid == null) {
            this.current = new Profile("default", UUID.randomUUID().toString(), VERSION);
            this.current.setSelected(true);
            uuid = this.current.getUuid();
            this.profiles.put(current.getUuid(), current);
        }
        return uuid;
    }

    public void switchProfile(String uuid) {
        if (current.getUuid() != uuid) {
            if (this.profiles.containsKey(uuid)) {
                Profile newp = this.profiles.get(uuid);
                current.setSelected(false);
                newp.setSelected(true);
                this.saveProfiles();
                this.current = newp;
            }
        }
    }

    public void saveProfiles() {
        for (Profile profile : profiles.values()) {

            byte[] data = new byte[0];
            try {
                data = JSON.toJSONString(profile, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.DisableCircularReferenceDetect).getBytes("utf8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            this.store.write(data, profile.getUuid());
        }
    }

    public List<Object> getConfigsByClass(Class c) {
        List<Object> temp = new ArrayList<>();
        for (Object o : this.configObjs) {
            if (o.getClass() == c) {
                temp.add(o);
            }
        }
        return temp;
    }

    @Init(level = 9999999)
    public void handle() {
        JSON.DEFAULT_GENERATE_FEATURE = SerializerFeature.DisableCircularReferenceDetect.getMask();
        profiles = new HashMap<>();
        configs = new HashMap<>();
        configObjs = new ArrayList<>();
        switchStore(localConfigStore);
        reloadProfiles();
        String cate = null;
        String gname = null;
        String cname = null;
        for (Object o : components) {
            Class target = o.getClass();
            Category category = (Category) target.getAnnotation(Category.class);
            SModule module = (SModule) target.getAnnotation(SModule.class);
            Config config = (Config) target.getAnnotation(Config.class);
            if (config != null || module != null) {
                cate = COMMON;
                if (config != null) {
                    gname = config.name();
                }
                if (module != null) {
                    cate = module.category();
                    gname = module.name();
                }

                for (Field field : target.getDeclaredFields()) {
                    field.setAccessible(true);
                    Value value = (Value) field.getAnnotation(Value.class);
                    if (value != null) {
                        cname = value.name();

                        try {
                            putConfig(cate, gname, cname, field.get(o));
                            configObjs.add(field.get(o));
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        bindConfigFromProfile();
        writeToProfile();
        saveProfiles();
        for (Run run : callbacks) {
            run.run();
        }
    }

    private void bindConfigFromProfile() {
        JSONObject jo = current.getConfig();
        if (jo == null) return;
        for (String cate : jo.keySet()) {

            for (String gname : jo.getJSONObject(cate).keySet()) {
                if (jo.getJSONObject(cate).getJSONObject(gname) != null) {
                    for (String cname : jo.getJSONObject(cate).getJSONObject(gname).keySet()) {
                        if (this.configs.containsKey(cate)) {
                            if (this.configs.get(cate).containsKey(gname)) {

                                Object config = this.configs.get(cate).get(gname).get(cname);
                                if (config == null) continue;
                                Object object = jo.getJSONObject(cate).getJSONObject(gname).getObject(cname, config.getClass());
                                ReflectUtils.copyData(config, object);
                                if (config instanceof ConfigInit) {
                                    ConfigInit configInit = (ConfigInit) config;
                                    configInit.init();
                                }
                            }
                        }
                    }
                }

            }
        }

    }

    public void addCallBack(Run run) {
        if (run != null) {
            this.callbacks.add(run);
        }

    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Profile getCurrent() {
        return current;
    }

    public void setCurrent(Profile current) {
        this.current = current;
    }

    public void writeToProfile() {
        this.current.setConfig(JSON.parseObject(JSON.toJSONString(configs)));
    }

    public void switchStore(Store store) {
        uuid = null;
        this.store = store;

    }

}
