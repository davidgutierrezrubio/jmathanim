package com.jmathanim.Utils;

import com.jmathanim.jmathanim.Dependable;

import java.util.List;

public final class DependableUtils {

    private DependableUtils() {}

    /**
     * Returns the maximum version number among the given dependables.
     */
    public static long maxVersion(List<Dependable> deps) {
        long max = 0;
        for (int i = 0, n = deps.size(); i < n; i++) {
            long v = deps.get(i).getVersion();
            if (v > max) max = v;
        }
        return max;
    }

    /** Specialized version for two dependables. */
    public static long maxVersion(Dependable a, Dependable b) {
        long va = a.getVersion();
        long vb = b.getVersion();
        return (va > vb) ? va : vb;
    }

    /** Specialized version for three dependables. */
    public static long maxVersion(Dependable a, Dependable b, Dependable c) {
        long va = a.getVersion();
        long vb = b.getVersion();
        long vc = c.getVersion();
        return Math.max(va, Math.max(vb, vc));
    }

    /** Specialized version for four dependables. */
    public static long maxVersion(Dependable a, Dependable b, Dependable c, Dependable d) {
        long va = a.getVersion();
        long vb = b.getVersion();
        long vc = c.getVersion();
        long vd = d.getVersion();
        return Math.max(Math.max(va, vb), Math.max(vc, vd));
    }
}
