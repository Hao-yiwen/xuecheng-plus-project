package com.xuecheng.content.model.dto.enums;

public enum MoveType {
    MOVEUP("moveup"),
    MOVEDOWN("movedown");

    private String moveType;

    MoveType(String moveupType) {
        this.moveType = moveupType;
    }

    public String getMoveType(){
        return this.moveType;
    }
}
