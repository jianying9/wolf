package com.wolf.analysis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jianying9
 */
public final class State {

    //深度
    private final char ch;
    //下一个节点状态
    private Map<Character, State> nextStateMap = new HashMap<Character, State>(0, 1);
    //
    private State fail;
    //
    private String output;
    
    public State(char ch) {
        this.ch = ch;
    }
    
    public State getFail() {
        return this.fail;
    }
    
    void setFail(State fail) {
        this.fail = fail;
    }
    
    public String getOutput() {
        return this.output;
    }
    
    void setOutput(String output) {
        this.output = output;
    }
    
    public State get(char ch) {
        return this.nextStateMap.get(ch);
    }
    
    void put(char ch, State state) {
        this.nextStateMap.put(ch, state);
    }
    
    Set<Character> nextKeys() {
        return this.nextStateMap.keySet();
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("this:").append(this.ch)
                .append(",output:").append(this.output)
                .append(",fail:{").append(this.fail)
                .append("},next:[");
        Set<Character> nextSet = this.nextKeys();
        if (nextSet.isEmpty() == false) {
            for (Character nextCh : nextSet) {
                builder.append(nextCh).append(',');
            }
            builder.setLength(builder.length() - 1);
        }
        builder.append(']');
        return builder.toString();
    }
}
