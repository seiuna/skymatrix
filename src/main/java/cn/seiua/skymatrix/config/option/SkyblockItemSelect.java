package cn.seiua.skymatrix.config.option;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.gui.UIComponent;
import cn.seiua.skymatrix.gui.ui.UI;
import cn.seiua.skymatrix.gui.ui.UISkyblockItemSelect;
import cn.seiua.skymatrix.utils.OptionInfo;
import cn.seiua.skymatrix.utils.SkyBlockUtils;
import com.alibaba.fastjson.annotation.JSONField;
import net.minecraft.item.ItemStack;

import java.io.Serializable;

public class SkyblockItemSelect implements Serializable, UIComponent {


    @JSONField(alternateNames = "uuid")
    private String uuid;

    @JSONField(alternateNames = "autoSelect")
    private boolean autoSelect;

    private transient Selector selector;
    private transient Filter filter;


    public SkyblockItemSelect(String uuid, boolean autoSelect, Selector selector, Filter filter) {
        this.uuid = uuid;
        this.autoSelect = autoSelect;
        this.selector = selector;
        this.filter = filter;
    }

    public boolean switchTo() {
        int slot = this.slot();
        if (slot == -1) {

            return false;
        }
        SkyMatrix.mc.player.getInventory().selectedSlot = slot;
        return true;
    }

    public Selector getSelector() {
        return selector;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public String getUuid() {
        if (SkyMatrix.mc.player != null && this.autoSelect) {
            return this.selector.select();
        }
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isAutoSelect() {
        return autoSelect;
    }

    public void setAutoSelect(boolean autoSelect) {
        this.autoSelect = autoSelect;
    }


    public int slot() {
        int i = 0;
        for (ItemStack itemStack : SkyMatrix.mc.player.getInventory().main) {
            if (i == 9) {
                break;
            }
            String uuid = SkyBlockUtils.getItemUuid(itemStack);
            String id = SkyBlockUtils.getItemId(itemStack);

            if (uuid != "none") {
                if (uuid.equals(this.getUuid())) {

                    return i;
                }

            }
            i++;
        }
        return -1;
    }

    @Override
    public UI build(String module, String category, String name, Signs sign) {
        OptionInfo<SkyblockItemSelect> optionInfo = new OptionInfo<>(this, category + "." + name, name, module, category, sign);
        UISkyblockItemSelect uiSkyblockItemSelect = new UISkyblockItemSelect(optionInfo);
        return uiSkyblockItemSelect;
    }

}
