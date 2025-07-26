package vn.vnrailway.dao;

import java.util.List;

import vn.vnrailway.model.Message;
import vn.vnrailway.model.MessageSummary;

public interface MessageDAO {
    List<Message> getChatMessages(int userId);

    List<MessageSummary> getChatSummariesWithPagination(int offset, int limit);

    void saveMessage(int userId, Integer staffId, String content, String senderType);

    List<Message> getNewMessages(int userId, int lastMessageId);

    List<Message> getChatMessagesByUserId(int userId);

    int getTotalChatSummariesCount();

    List<Message> getMessagesAfter(int userId, int lastMessageId);
}
