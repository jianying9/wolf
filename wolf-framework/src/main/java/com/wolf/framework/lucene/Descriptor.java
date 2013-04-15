package com.wolf.framework.lucene;

import org.apache.hadoop.fs.FSDataInputStream;

/**
 *
 * @author aladdin
 */
public class Descriptor {

    private final FSDataInputStream in;
    private long position = -1;

    public Descriptor(FSDataInputStream in) {
        this.in = in;
    }

    public FSDataInputStream getIn() {
        return in;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }
    
    public void addPosition(long position) {
        this.position += position;
    }
}
