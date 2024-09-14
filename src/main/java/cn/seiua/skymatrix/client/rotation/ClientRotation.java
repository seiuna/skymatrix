package cn.seiua.skymatrix.client.rotation;

import cn.seiua.skymatrix.client.Run;
import cn.seiua.skymatrix.client.component.Component;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.Use;

@Component
@Event(register = true)
public class ClientRotation {

    @Use
    public SmoothRotation smoothRotation;
    @Use
    public RotationFuck rotationFuck;

    public ClientRotation() {
        new Thread(this::update).start();
    }

    private void update() {
        while (true) {
            try {
                Thread.sleep(2l);
                if (rotationFuck != null) {
                    rotationFuck.onTick();
                }
                if (smoothRotation != null) {
                    smoothRotation.onTick();
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void clientLook(Rotation rotation, int tick, Run callback) {
        rotationFuck.smoothLook(rotation, tick, callback);
    }

    public void serverLook(Rotation rotation, int tick, Run callback) {
        smoothRotation.smoothLook(rotation, tick, callback, false);
    }

    public void cancelClientLook() {
        rotationFuck.task = null;
    }

    public void cancelServerLook() {
        this.smoothRotation.running = false;
        smoothRotation.task = null;
    }

}
