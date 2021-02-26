package com.zjq.chat.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjq.chat.pojo.User;
import com.zjq.chat.util.Json;
import org.springframework.stereotype.Controller;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ServerEndpoint("/chat/{room}/{name}/{uid}")
@Controller
public class MyWebSocket implements Json {

    private static final Logger logger = Logger.getLogger(String.valueOf(MyWebSocket.class));

    /**
     * 存放所有用户session（当前在线用户）
     */
    private static Map<String, List<Session>> list = new ConcurrentHashMap<>();

    //随机头像存储源
    String[] head_images = new String[]{
            "christian.jpg",
            "daniel.jpg",
            "elliot.jpg",
            "helen.jpg",
            "jenny.jpg",
            "lena.png",
            "lindsay.png",
            "mark.png",
            "matt.jpg",
            "molly.png",
            "steve.jpg",
            "stevie.jpg",
            "tom.jpg",
            "veronika.jpg"};


    @OnOpen
    public void onOpen(Session session, @PathParam("name") String name, @PathParam("room") String room, @PathParam("uid") String uid) throws IOException {
        logger.info("用户已登录：" + name);
        String header = head_images[new Random().nextInt(head_images.length)];
        User user = new User(header, name, room, uid);
        session.getUserProperties().put("user", user);

        if (list.containsKey(room)) {
            list.get(room).add(session);

            List<User> userslist = list.get(room).stream().map(item -> (User) item.getUserProperties().get("user")).collect(Collectors.toList());

            StringBuilder userListString = new StringBuilder("欢迎加入聊天室 ");
            for (User item : userslist) {
                if (!item.getUid().equals(uid)) {
                    userListString.append("成员: ").append(item.getName()).append(" ");
                }
            }

            // 有成员才输出
            if (userListString.length() != 8) {
                onMessage(session, userListString.toString());
            }
        } else {
            list.put(room, new ArrayList<Session>() {{
                add(session);
            }});
        }
        freshUsers(room);
    }

    /**
     * 刷新列表
     */
    public void freshUsers(String room) throws IOException {
        List<Object> userslist = list.get(room).stream().map(user -> user.getUserProperties().get("user")).collect(Collectors.toList());
        Map<String, Object> messages = new HashMap<>();
        messages.put("type", "update_user");
        messages.put("users", userslist);
        String msg = toJson(messages);
        for (Session session : list.get(room)) {
            session.getBasicRemote().sendText(msg);
        }
    }


    @OnClose
    public void onClose(Session session) throws IOException {
        User user = (User) session.getUserProperties().get("user");

        Iterator<Session> it = list.get(user.getRoomId()).iterator();
        while (it.hasNext()) {
            Session res = it.next();
            User user1 = (User) res.getUserProperties().get("user");

            if (user.getUid().equals(user1.getUid())) {
                it.remove();
            }
        }

        freshUsers(user.getRoomId());
    }


    @OnError
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }


    @OnMessage
    public void onMessage(Session session, String msg) throws IOException {
        User user = (User) session.getUserProperties().get("user");

        Map<String, Object> message = new HashMap<>();
        message.put("type", "normal_msg");
        message.put("msg", msg);
        message.put("user", user);
        // 接收消息
        for (Session s : list.get(user.getRoomId())) {
            s.getBasicRemote().sendText(toJson(message));
        }
    }

    /***
     *  将传回前端消息转换为json格式
     * @param map
     * @return
     * @throws JsonProcessingException
     */
    @Override
    public String toJson(Map<String, ?> map) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(map);
    }
}
