package de.treinke.minecraftguilds.objects;

public class Claim {
    public int x = 0;
    public int z = 0;
    public String guild = "";
    public String dim = "";

    public Claim(String g,String dim, int x, int z) {
        this.guild = g;
        this.dim = dim;
        this.x = x;
        this.z = z;
    }
}