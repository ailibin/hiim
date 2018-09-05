package com.aiitec.hiim.im.entity;

/**
 * @author afb
 * @date 2018/1/4
 * 对聊天界面的相关操作的实体
 */

public class OperationContent {

    private String content;

    public OperationContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
