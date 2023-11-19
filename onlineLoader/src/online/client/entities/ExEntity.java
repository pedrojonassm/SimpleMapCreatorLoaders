package online.client.entities;

import entities.Entity;
import main.OnlineMapLoader;

public class ExEntity {
    private int x;
    private int y;
    private int z;

    private Integer identificadorServidor;

    public ExEntity() {
        x = OnlineMapLoader.aConfig.getPlayerX() - 128;
        y = OnlineMapLoader.aConfig.getPlayerY();
        z = OnlineMapLoader.aConfig.getPlayerZ();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public Integer getIdentificadorServidor() {
        return identificadorServidor;
    }

    public void setIdentificadorServidor(Integer identificadorServidor) {
        this.identificadorServidor = identificadorServidor;
    }

    public void update(Entity prEntity) {
        x = prEntity.getX();
        y = prEntity.getY();
        z = prEntity.getZ();
    }

}
