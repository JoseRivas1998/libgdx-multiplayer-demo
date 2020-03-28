package com.tcg.libgdxmultiplayerdemo.gamestates;

public enum GameStateType {
    PLAY(PlayState.class);
    public final Class<? extends AbstractGameState> stateClass;

    GameStateType(Class<? extends AbstractGameState> stateClass) {
        this.stateClass = stateClass;
    }
}
