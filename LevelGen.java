package com.mojang.minecraft.level;

import com.mojang.minecraft.level.tile.Tile;
import java.util.Random;

public class LevelGen {
  private int width;
  
  private int height;
  
  private int depth;
  
  private Random random = new Random();
  
  public LevelGen(int width, int height, int depth) {
    this.width = width;
    this.height = height;
    this.depth = depth;
  }
  
  public byte[] generateMap() {
    int w = this.width;
    int h = this.height;
    int d = this.depth;
    int[] heightmap1 = (new NoiseMap(0)).read(w, h);
    int[] heightmap2 = (new NoiseMap(0)).read(w, h);
    int[] cf = (new NoiseMap(1)).read(w, h);
    int[] rockMap = (new NoiseMap(1)).read(w, h);
    byte[] blocks = new byte[this.width * this.height * this.depth];
    for (int x = 0; x < w; x++) {
      for (int y = 0; y < d; y++) {
        for (int z = 0; z < h; z++) {
          int dh1 = heightmap1[x + z * this.width];
          int dh2 = heightmap2[x + z * this.width];
          int cfh = cf[x + z * this.width];
          if (cfh < 128)
            dh2 = dh1; 
          int dh = dh1;
          if (dh2 > dh) {
            dh = dh2;
          } else {
            dh2 = dh1;
          } 
          dh = dh / 8 + d / 3;
          int rh = rockMap[x + z * this.width] / 8 + d / 3;
          if (rh > dh - 2)
            rh = dh - 2; 
          int j = (y * this.height + z) * this.width + x;
          int id = 0;
          if (y == dh)
            id = Tile.grass.id; 
          if (y < dh)
            id = Tile.dirt.id; 
          if (y <= rh)
            id = Tile.rock.id; 
          blocks[j] = (byte)id;
        } 
      } 
    } 
    int count = w * h * d / 256 / 64;
    for (int i = 0; i < count; i++) {
      float f1 = this.random.nextFloat() * w;
      float y = this.random.nextFloat() * d;
      float z = this.random.nextFloat() * h;
      int length = (int)(this.random.nextFloat() + this.random.nextFloat() * 150.0F);
      float dir1 = (float)(this.random.nextFloat() * Math.PI * 2.0D);
      float dira1 = 0.0F;
      float dir2 = (float)(this.random.nextFloat() * Math.PI * 2.0D);
      float dira2 = 0.0F;
      for (int l = 0; l < length; l++) {
        f1 = (float)(f1 + Math.sin(dir1) * Math.cos(dir2));
        z = (float)(z + Math.cos(dir1) * Math.cos(dir2));
        y = (float)(y + Math.sin(dir2));
        dir1 += dira1 * 0.2F;
        dira1 *= 0.9F;
        dira1 += this.random.nextFloat() - this.random.nextFloat();
        dir2 += dira2 * 0.5F;
        dir2 *= 0.5F;
        dira2 *= 0.9F;
        dira2 += this.random.nextFloat() - this.random.nextFloat();
        float size = (float)(Math.sin(l * Math.PI / length) * 2.5D + 1.0D);
        for (int xx = (int)(f1 - size); xx <= (int)(f1 + size); xx++) {
          for (int yy = (int)(y - size); yy <= (int)(y + size); yy++) {
            for (int zz = (int)(z - size); zz <= (int)(z + size); zz++) {
              float xd = xx - f1;
              float yd = yy - y;
              float zd = zz - z;
              float dd = xd * xd + yd * yd * 2.0F + zd * zd;
              if (dd < size * size && xx >= 1 && yy >= 1 && zz >= 1 && xx < this.width - 1 && yy < this.depth - 1 && zz < this.height - 1) {
                int ii = (yy * this.height + zz) * this.width + xx;
                if (blocks[ii] == Tile.rock.id)
                  blocks[ii] = 0; 
              } 
            } 
          } 
        } 
      } 
    } 
    return blocks;
  }
}
