package com.example.demo.dto;
import com.example.demo.model.ChatMedia;
import com.example.demo.model.ChatMedia.FileType;

public class ChatMediaDTO {

    private Long mediaId;
    private String fileUrl;
    private ChatMedia.FileType fileType;

    public ChatMediaDTO() {}

    public ChatMediaDTO(Long mediaId, FileType fileType, String fileUrl) {
        this.mediaId = mediaId;
        this.fileType = fileType;
        this.fileUrl = fileUrl;
    }

    public Long getMediaId() { return mediaId; }
    public void setMediaId(Long mediaId) { this.mediaId = mediaId; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public FileType getFileType() { return fileType; }
    public void setFileType(FileType fileType) { this.fileType = fileType; }

}
