package com.example.demo.model;
import java.time.LocalDateTime;

public class ChatMedia {
    public enum FileType { PHOTO, VIDEO }

    private Long chat_media_id;
    private Long chat_id;
    private FileType file_type;
    private String file_url;

    public Long getChat_media_id() { return chat_media_id; }
    public void setChat_media_id(Long chat_media_id) { this.chat_media_id = chat_media_id; }

    public Long getChat_id() { return chat_id; }
    public void setChat_id(Long chat_id) { this.chat_id = chat_id; }

    public FileType getFile_type() { return file_type; }
    public void setFile_type(FileType file_type) { this.file_type = file_type; }

    public String getFile_url() { return file_url; }
    public void setFile_url(String file_url) { this.file_url = file_url; }
}
