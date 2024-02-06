package dino0131v1;

import battlecode.common.*;

public strictfp class MapLocationMultiSet {
    String data;
    
    MapLocationMultiSet() {
        data = new String();
    }

    void add(MapLocation loc) {
        data += loc.toString();
    }

    int count(MapLocation loc) {
        int i = 0;
        String loc_s = loc.toString();
        int c = 0;
        while ((i = data.indexOf(loc_s, i) + 1) > 0) {
            c++;
        }
        return c;
    }

    boolean contains(MapLocation loc) {
        return data.contains(loc.toString());
    }

    void clear() {
        data = new String();
    }
}
