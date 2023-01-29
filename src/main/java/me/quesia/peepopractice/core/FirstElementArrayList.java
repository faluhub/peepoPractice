package me.quesia.peepopractice.core;

import me.quesia.peepopractice.PeepoPractice;
import net.minecraft.structure.StructurePiece;

import java.util.ArrayList;

public class FirstElementArrayList<E> extends ArrayList<E> {
    @Override
    public boolean add(E e) {
        if (this.isEmpty() && PeepoPractice.CURRENT_ORIENTATION != null) {
            if (e instanceof StructurePiece) {
                PeepoPractice.log(PeepoPractice.CURRENT_ORIENTATION.getName());
                ((StructurePiece) e).setOrientation(PeepoPractice.CURRENT_ORIENTATION);
            }
            PeepoPractice.CURRENT_ORIENTATION = null;
        }
        return super.add(e);
    }
}
