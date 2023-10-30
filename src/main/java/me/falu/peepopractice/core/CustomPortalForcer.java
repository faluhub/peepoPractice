package me.falu.peepopractice.core;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

/**
 * Awful code copied from mojang.
 * Ignore the big huge ginormous duplicate.
 */
@SuppressWarnings("DuplicatedCode")
public class CustomPortalForcer {
    public static BlockPos getPortalPosition(BlockPos blockPos, ServerWorld world) {
        int ab;
        int aa;
        int y;
        int aev;
        int w;
        int v;
        int u;
        int t;
        double f;
        int s;
        double e;
        int r;
        double d = -1.0;
        int j = MathHelper.floor(blockPos.getX());
        int k = MathHelper.floor(blockPos.getY());
        int l = MathHelper.floor(blockPos.getZ());
        int m = j;
        int n = k;
        int o = l;
        int p = 0;
        int q = 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (r = j - 16; r <= j + 16; ++r) {
            e = (double) r + 0.5 - blockPos.getX();
            for (s = l - 16; s <= l + 16; ++s) {
                f = (double) s + 0.5 - blockPos.getZ();
                block2:
                for (t = world.getDimensionHeight() - 1; t >= 0; --t) {
                    if (!world.isAir(mutable.set(r, t, s))) { continue; }
                    while (t > 0 && world.isAir(mutable.set(r, t - 1, s))) {
                        --t;
                    }
                    for (u = q; u < q + 4; ++u) {
                        v = u % 2;
                        w = 1 - v;
                        if (u % 4 >= 2) {
                            v = -v;
                            w = -w;
                        }
                        for (aev = 0; aev < 3; ++aev) {
                            for (y = 0; y < 4; ++y) {
                                for (int z = -1; z < 4; ++z) {
                                    aa = r + (y - 1) * v + aev * w;
                                    ab = t + z;
                                    int ac = s + (y - 1) * w - aev * v;
                                    mutable.set(aa, ab, ac);
                                    if (z < 0 && !world.getBlockState(mutable).getMaterial().isSolid() || z >= 0 && !world.isAir(mutable)) {
                                        continue block2;
                                    }
                                }
                            }
                        }
                        double g = (double) t + 0.5 - blockPos.getY();
                        double h = e * e + g * g + f * f;
                        if (!(d < 0.0) && !(h < d)) { continue; }
                        d = h;
                        m = r;
                        n = t;
                        o = s;
                        p = u % 4;
                    }
                }
            }
        }
        if (d < 0.0) {
            for (r = j - 16; r <= j + 16; ++r) {
                e = (double) r + 0.5 - blockPos.getX();
                for (s = l - 16; s <= l + 16; ++s) {
                    f = (double) s + 0.5 - blockPos.getZ();
                    block10:
                    for (t = world.getDimensionHeight() - 1; t >= 0; --t) {
                        if (!world.isAir(mutable.set(r, t, s))) { continue; }
                        while (t > 0 && world.isAir(mutable.set(r, t - 1, s))) {
                            --t;
                        }
                        for (u = q; u < q + 2; ++u) {
                            v = u % 2;
                            w = 1 - v;
                            for (int x2 = 0; x2 < 4; ++x2) {
                                for (y = -1; y < 4; ++y) {
                                    int z = r + (x2 - 1) * v;
                                    aa = t + y;
                                    ab = s + (x2 - 1) * w;
                                    mutable.set(z, aa, ab);
                                    if (y < 0 && !world.getBlockState(mutable).getMaterial().isSolid() || y >= 0 && !world.isAir(mutable)) {
                                        continue block10;
                                    }
                                }
                            }
                            double g = (double) t + 0.5 - blockPos.getY();
                            double h = e * e + g * g + f * f;
                            if (!(d < 0.0) && !(h < d)) { continue; }
                            d = h;
                            m = r;
                            n = t;
                            o = s;
                            p = u % 2;
                        }
                    }
                }
            }
        }
        r = p;
        int ad = m;
        int ae = n;
        s = o;
        int af = r % 2;
        int ag = 1 - af;
        if (r % 4 >= 2) {
            af = -af;
            ag = -ag;
        }
        if (d < 0.0) {
            ae = MathHelper.clamp(n, 70, world.getDimensionHeight() - 10);
            for (t = -1; t <= 1; ++t) {
                for (u = 1; u < 3; ++u) {
                    for (v = -1; v < 3; ++v) {
                        w = ad + (u - 1) * af + t * ag;
                        aev = ae + v;
                        y = s + (u - 1) * ag - t * af;
                        mutable.set(w, aev, y);
                    }
                }
            }
        }
        for (t = -1; t < 3; ++t) {
            for (u = -1; u < 4; ++u) {
                if (t != -1 && t != 2 && u != -1 && u != 3) { continue; }
                mutable.set(ad + t * af, ae + u, s + t * ag);
            }
        }
        for (u = 0; u < 2; ++u) {
            for (v = 0; v < 3; ++v) {
                mutable.set(ad + u * af, ae + v, s + u * ag);
            }
        }
        return mutable.down(2);
    }

    public static BlockPos createPortal(BlockPos blockPos, ServerWorld world) {
        int ab;
        int aa;
        int y;
        int aev;
        int w;
        int v;
        int u;
        int t;
        double f;
        int s;
        double e;
        int r;
        double d = -1.0;
        int j = MathHelper.floor(blockPos.getX());
        int k = MathHelper.floor(blockPos.getY());
        int l = MathHelper.floor(blockPos.getZ());
        int m = j;
        int n = k;
        int o = l;
        int p = 0;
        int q = 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (r = j - 16; r <= j + 16; ++r) {
            e = (double) r + 0.5 - blockPos.getX();
            for (s = l - 16; s <= l + 16; ++s) {
                f = (double) s + 0.5 - blockPos.getZ();
                block2:
                for (t = world.getDimensionHeight() - 1; t >= 0; --t) {
                    if (!world.isAir(mutable.set(r, t, s))) { continue; }
                    while (t > 0 && world.isAir(mutable.set(r, t - 1, s))) {
                        --t;
                    }
                    for (u = q; u < q + 4; ++u) {
                        v = u % 2;
                        w = 1 - v;
                        if (u % 4 >= 2) {
                            v = -v;
                            w = -w;
                        }
                        for (aev = 0; aev < 3; ++aev) {
                            for (y = 0; y < 4; ++y) {
                                for (int z = -1; z < 4; ++z) {
                                    aa = r + (y - 1) * v + aev * w;
                                    ab = t + z;
                                    int ac = s + (y - 1) * w - aev * v;
                                    mutable.set(aa, ab, ac);
                                    if (z < 0 && !world.getBlockState(mutable).getMaterial().isSolid() || z >= 0 && !world.isAir(mutable)) {
                                        continue block2;
                                    }
                                }
                            }
                        }
                        double g = (double) t + 0.5 - blockPos.getY();
                        double h = e * e + g * g + f * f;
                        if (!(d < 0.0) && !(h < d)) { continue; }
                        d = h;
                        m = r;
                        n = t;
                        o = s;
                        p = u % 4;
                    }
                }
            }
        }
        if (d < 0.0) {
            for (r = j - 16; r <= j + 16; ++r) {
                e = (double) r + 0.5 - blockPos.getX();
                for (s = l - 16; s <= l + 16; ++s) {
                    f = (double) s + 0.5 - blockPos.getZ();
                    block10:
                    for (t = world.getDimensionHeight() - 1; t >= 0; --t) {
                        if (!world.isAir(mutable.set(r, t, s))) { continue; }
                        while (t > 0 && world.isAir(mutable.set(r, t - 1, s))) {
                            --t;
                        }
                        for (u = q; u < q + 2; ++u) {
                            v = u % 2;
                            w = 1 - v;
                            for (int x2 = 0; x2 < 4; ++x2) {
                                for (y = -1; y < 4; ++y) {
                                    int z = r + (x2 - 1) * v;
                                    aa = t + y;
                                    ab = s + (x2 - 1) * w;
                                    mutable.set(z, aa, ab);
                                    if (y < 0 && !world.getBlockState(mutable).getMaterial().isSolid() || y >= 0 && !world.isAir(mutable)) {
                                        continue block10;
                                    }
                                }
                            }
                            double g = (double) t + 0.5 - blockPos.getY();
                            double h = e * e + g * g + f * f;
                            if (!(d < 0.0) && !(h < d)) { continue; }
                            d = h;
                            m = r;
                            n = t;
                            o = s;
                            p = u % 2;
                        }
                    }
                }
            }
        }
        r = p;
        int ad = m;
        int ae = n;
        s = o;
        int af = r % 2;
        int ag = 1 - af;
        if (r % 4 >= 2) {
            af = -af;
            ag = -ag;
        }
        if (d < 0.0) {
            ae = MathHelper.clamp(n, 70, world.getDimensionHeight() - 10);
            for (t = -1; t <= 1; ++t) {
                for (u = 1; u < 3; ++u) {
                    for (v = -1; v < 3; ++v) {
                        w = ad + (u - 1) * af + t * ag;
                        aev = ae + v;
                        y = s + (u - 1) * ag - t * af;
                        boolean bl = v < 0;
                        mutable.set(w, aev, y);
                        world.setBlockState(mutable, bl ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
        for (t = -1; t < 3; ++t) {
            for (u = -1; u < 4; ++u) {
                if (t != -1 && t != 2 && u != -1 && u != 3) { continue; }
                mutable.set(ad + t * af, ae + u, s + t * ag);
                world.setBlockState(mutable, Blocks.OBSIDIAN.getDefaultState(), 3);
            }
        }
        BlockState blockState = Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, af == 0 ? Direction.Axis.Z : Direction.Axis.X);
        for (u = 0; u < 2; ++u) {
            for (v = 0; v < 3; ++v) {
                mutable.set(ad + u * af, ae + v, s + u * ag);
                world.setBlockState(mutable, blockState, 18);
            }
        }
        return mutable;
    }
}
