package com.diboot.core.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 集合批次迭代
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/01/01
 */
public class BatchIterator<E> implements Iterator<List<E>> {
    /**
     * 每批次集合数量
     */
    private int batchSize;
    /***
     * 原始list
     */
    private List<E> originalList;
    private int index = 0;
    private List<E> result;
    private int total = 0;

    public BatchIterator(List<E> originalList, int batchSize) {
        if (batchSize <= 0) {
            return;
        }
        this.batchSize = batchSize;
        this.originalList = originalList;
        this.total = V.notEmpty(originalList)? originalList.size() : 0;
        result = new ArrayList<>(batchSize);
    }

    @Override
    public boolean hasNext() {
        return index < total;
    }

    @Override
    public List<E> next() {
        result.clear();
        for (int i = 0; i < batchSize && index < total; i++) {
            result.add(originalList.get(index++));
        }
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("BatchIterator.remove未实现!");
    }
}
