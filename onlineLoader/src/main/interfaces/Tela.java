package main.interfaces;

public interface Tela extends tickRender {
    public boolean clicou(int x, int y);

    public boolean cliquedireito(int x, int y);

    public boolean trocar_pagina(int x, int y, int prRodinha);

    public boolean interagivelAgora();
}
