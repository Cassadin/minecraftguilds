package de.treinke.minecraftguilds.objects;

public class Claim {
    public int x = 0;
    public int z = 0;
    public String guild = "";
    public int dim = 0;

    public Claim(String g,int dim, int x, int z) {
        this.guild = g;
        this.dim = dim;
        this.x = x;
        this.z = z;
    }
}