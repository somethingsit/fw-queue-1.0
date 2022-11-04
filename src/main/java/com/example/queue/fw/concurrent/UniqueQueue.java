package com.example.queue.fw.concurrent;

import com.example.queue.fw.utils.DataUtil;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class UniqueQueue<T extends ConcurrentMessage> extends Queue<T>  {
    private ConcurrentHashMap<String, T> uniqueMap = new ConcurrentHashMap();

    public UniqueQueue() {
    }

    public boolean enqueue(T message) {
        if (DataUtil.isNullOrEmpty(message.getMessageId())) {
            throw new UniqueQueueItemMissIdException(message);
        } else if (this.uniqueMap.containsKey(message.getMessageId())) {
            throw new UniqueQueueItemException(message);
        } else {
            boolean success = super.enqueue(message);
            if (success) {
                this.uniqueMap.put(message.getMessageId(), message);
            }

            return success;
        }
    }

    public void complete(T message) {
        this.uniqueMap.remove(message.getMessageId());
    }

    public List<T> values() {
        Collection coll = this.uniqueMap.values();
        return (List)(coll instanceof List ? (List)coll : new ArrayList(coll));
    }

    public List<T> getAllRemain() {
        return Lists.newArrayList(this.uniqueMap.values());
    }

    public boolean contain(ConcurrentMessage message) {
        return this.uniqueMap.containsKey(message.getMessageId());
    }
}
