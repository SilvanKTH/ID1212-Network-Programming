package kth.chara.client.startup;

import kth.chara.client.view.NonBlockingInterpreter;

/**
 * Begin with adding new players
 */

public class Main {
    public static void main(String[] args){
        new NonBlockingInterpreter().start();
    }
}
