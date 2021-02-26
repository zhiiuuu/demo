package com.zjq.chat.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {
    private String header;

    private String name;
    private String roomId;
    private String uid;

    public User(String header, String name, String roomId, String id) {
        this.uid = id;
        this.header = header;
        this.name = name;
        this.roomId = roomId;
    }

}
