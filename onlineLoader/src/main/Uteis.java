package main;

import world.Camera;
import world.World;

public class Uteis {

    public static double distancia(int x1, int x2, int y1, int y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public static double modulo(double prValor) {

        return Math.sqrt(prValor * prValor);
    }

    public static int log(int prValor, int prLogaritmo) {
        int k = 0;
        while (prValor != 0 && prValor % prLogaritmo == 0) {
            k++;
            prValor = prValor / prLogaritmo;
        }
        return k;
    }

    public static int[] calcularPosicaoComAltura(int prPos) {
        int[] retorno = { 0, 0, 0 };
        retorno[0] = (int) ((prPos % (World.WIDTH * World.HIGH)) / World.HIGH) * OnlineMapLoader.TileSize;
        retorno[1] = (int) (prPos / World.HEIGHT / World.HIGH) * OnlineMapLoader.TileSize;
        retorno[2] = (prPos % World.HIGH);
        return retorno;
    }

    public static int[] calcularPosicaoSemAlturaRelativoACamera(int prPos) {
        int[] retorno = calcularPosicaoComAltura(prPos);
        retorno[0] -= Camera.x;
        retorno[1] -= Camera.y;
        return retorno;
    }

    public static int[] calcularPosicaoComAlturaRelativoACamera(int prPos) {
        int[] retorno = calcularPosicaoSemAlturaRelativoACamera(prPos);
        int lSubtract = (prPos % World.HIGH) * OnlineMapLoader.TileSize;
        retorno[0] -= lSubtract;
        retorno[1] -= lSubtract;
        return retorno;
    }

    public static int[] posToXYZ(int prPos) {
        int[] retorno = { 0, 0, 0 };
        retorno[0] = (int) ((prPos % (World.WIDTH * World.HIGH)) / World.HIGH);
        retorno[1] = (int) (prPos / World.HEIGHT / World.HIGH);
        retorno[2] = (prPos % World.HIGH);
        return retorno;
    }

    public static boolean isEnumValueValid(Integer lValue, Class prEnum) {
        return lValue != null && prEnum.isEnum() && lValue >= 0 && lValue < prEnum.getEnumConstants().length;
    }

}
