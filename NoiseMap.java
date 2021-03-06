package com.mojang.minecraft.level;

import java.util.Random;

public class NoiseMap {
  Random random = new Random();
  
  int seed = this.random.nextInt();
  
  int levels = 0;
  
  int fuzz = 16;
  
  public NoiseMap(int levels) {
    this.levels = levels;
  }
  
  public int[] read(int width, int height) {
    Random random = new Random();
    int[] tmp = new int[width * height];
    int level = this.levels;
    int step = width >> level;
    int y;
    for (y = 0; y < height; y += step) {
      for (int x = 0; x < width; x += step)
        tmp[x + y * width] = (random.nextInt(256) - 128) * this.fuzz; 
    } 
    for (step = width >> level; step > 1; step /= 2) {
      int val = 256 * (step << level);
      int ss = step / 2;
      int i;
      for (i = 0; i < height; i += step) {
        for (int x = 0; x < width; x += step) {
          int ul = tmp[(x + 0) % width + (i + 0) % height * width];
          int ur = tmp[(x + step) % width + (i + 0) % height * width];
          int dl = tmp[(x + 0) % width + (i + step) % height * width];
          int dr = tmp[(x + step) % width + (i + step) % height * width];
          int m = (ul + dl + ur + dr) / 4 + random.nextInt(val * 2) - val;
          tmp[x + ss + (i + ss) * width] = m;
        } 
      } 
      for (i = 0; i < height; i += step) {
        for (int x = 0; x < width; x += step) {
          int c = tmp[x + i * width];
          int r = tmp[(x + step) % width + i * width];
          int d = tmp[x + (i + step) % width * width];
          int mu = tmp[(x + ss & width - 1) + (i + ss - step & height - 1) * width];
          int ml = tmp[(x + ss - step & width - 1) + (i + ss & height - 1) * width];
          int m = tmp[(x + ss) % width + (i + ss) % height * width];
          int u = (c + r + m + mu) / 4 + random.nextInt(val * 2) - val;
          int l = (c + d + m + ml) / 4 + random.nextInt(val * 2) - val;
          tmp[x + ss + i * width] = u;
          tmp[x + (i + ss) * width] = l;
        } 
      } 
    } 
    int[] result = new int[width * height];
    for (y = 0; y < height; y++) {
      for (int x = 0; x < width; x++)
        result[x + y * width] = tmp[x % width + y % height * width] / 512 + 128; 
    } 
    return result;
  }
}
