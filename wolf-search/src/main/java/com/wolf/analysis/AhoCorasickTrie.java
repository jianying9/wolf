package com.wolf.analysis;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 *
 * @author jianying9
 */
public final class AhoCorasickTrie {

    private final State root = new State(Character.MAX_VALUE);

    public void add(String word) {
        word = word.trim().toLowerCase();
        char[] chars = word.toCharArray();
        State thisState = this.root;
        State nextState;
        for (int index = 0; index < chars.length; index++) {
            nextState = thisState.get(chars[index]);
            if (nextState == null) {
                nextState = new State(chars[index]);
                thisState.put(chars[index], nextState);
            }
            thisState = nextState;
        }
        thisState.setOutput(word);
    }

    public void initFail() {
        Set<Character> nextStateKeys = this.root.nextKeys();
        Queue<State> queue = new LinkedList<State>();
        State state;
        //dept=1的状态的fail指向root
        for (Character ch : nextStateKeys) {
            state = this.root.get(ch);
            state.setFail(this.root);
            queue.add(state);
        }
        //
        State fail;
        State thisState;
        while (queue.isEmpty() == false) {
            state = queue.poll();
            nextStateKeys = state.nextKeys();
            for (Character ch : nextStateKeys) {
                thisState = state.get(ch);
                fail = state.getFail();
                while (fail.get(ch) == null && fail.getFail() != null) {
                    //如果fail不是root，且fail的nextStates中没有key为ch的state,则继续下一个fail
                    fail = fail.getFail();
                }
                if (fail.get(ch) != null) {
                    //fail的nextStates中存在和thisState相同的key的state,则thisState的fail指向state
                    thisState.setFail(fail.get(ch));
                } else {
                    //fail是root，且root的nextStates中不存在和thisState相同的key的state,则thisState的fail指向root
                    thisState.setFail(this.root);
                }
                //
                queue.add(thisState);
            }
        }
    }

    public Set<String> analyze(String text) {
        text = text.trim().toLowerCase();
        State state = this.root;
        char[] chars = text.toCharArray();
        Set<String> resultSet = new HashSet<String>(4, 1);
        char ch;
        String output;
        State fail;
        for (int index = 0; index < chars.length; index++) {
            ch = chars[index];
            while (state.get(ch) == null && state.getFail() != null) {
                state = state.getFail();
            }
            if (state.get(ch) != null) {
                state = state.get(ch);
                output = state.getOutput();
                if (output != null) {
                    resultSet.add(output);
                }
                fail = state.getFail();
                while (fail != null) {
                    output = fail.getOutput();
                    if (output != null) {
                        resultSet.add(output);
                    }
                    fail = fail.getFail();
                }
            }
        }
        return resultSet;
    }
}
