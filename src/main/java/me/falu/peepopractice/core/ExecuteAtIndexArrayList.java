package me.falu.peepopractice.core;

import java.util.ArrayList;

public class ExecuteAtIndexArrayList<E> extends ArrayList<E> {
    private final IndexExecutionTask<E> task;
    private final int index;

    public ExecuteAtIndexArrayList(IndexExecutionTask<E> task, int index) {
        this.task = task;
        this.index = index;
    }

    @Override
    public boolean add(E e) {
        if (this.size() == this.index) {
            this.task.execute(e);
        }
        return super.add(e);
    }

    @Override
    public void add(int index, E element) {
        if (index == this.index) {
            this.task.execute(element);
        }
        super.add(index, element);
    }

    public interface IndexExecutionTask<E> {
        void execute(E element);
    }
}
