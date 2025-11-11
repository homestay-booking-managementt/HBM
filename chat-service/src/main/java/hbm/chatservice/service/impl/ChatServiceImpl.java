//package hbm.chatservice.service.impl;
//
//import hbm.chatservice.dto.ChatMessageDto;
//import hbm.chatservice.entity.ChatSession;
//import hbm.chatservice.repository.ChatMessageRepository;
//import hbm.chatservice.repository.ChatSessionRepository;
//import hbm.chatservice.service.ChatService;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//// package hbm.bookingservice.service.impl
//
//@Service
//@RequiredArgsConstructor
//public class ChatServiceImpl implements ChatService {
//
//    private final ChatSessionRepository chatSessionRepository;
//    private final ChatMessageRepository chatMessageRepository;
//    private final HomestayRepository homestayRepository; // Cần dùng để tìm hostId
//    private final ChatMapper chatMapper;
//    private final UserMapper userMapper; // Cần dùng để map thông tin user
//
//    // Hàm hỗ trợ tìm Session
//    @Transactional
//    public ChatSession getOrCreateSession(Long customerId, Long hostId) {
//        // 1. Tìm session ACTIVE
//        return chatSessionRepository.findByCustomerIdAndHostIdAndStatus(
//                        customerId, hostId, SessionStatus.ACTIVE.name())
//                .orElseGet(() -> {
//                    // 2. Tạo Session mới nếu không tồn tại
//                    ChatSession newSession = new ChatSession();
//                    newSession.setCustomerId(customerId);
//                    newSession.setHostId(hostId);
//                    newSession.setInitiatedBy(InitiatedBy.CUSTOMER);
//                    newSession.setStatus(SessionStatus.ACTIVE);
//                    newSession.setStartedAt(LocalDateTime.now());
//                    newSession.setIsWithBot(false);
//                    return chatSessionRepository.save(newSession);
//                });
//    }
//
//    @Override
//    @Transactional
//    public ChatMessageDto saveAndProcessMessage(Long senderId, ChatMessageRequestDto dto) {
//
//        // 1. Tìm Homestay để lấy Host ID
//        Homestay homestay = homestayRepository.findById(dto.getHomestayId())
//                .orElseThrow(() -> new IllegalArgumentException("Homestay not found."));
//
//        Long hostId = homestay.getUserId();
//
//        // Xác định vai trò của Sender
//        boolean isSenderHost = hostId.equals(senderId);
//
//        // Lấy Session: Giả định Customer luôn là người khởi tạo phiên ban đầu
//        Long customerId = isSenderHost ? homestayRepository.getCustomerIdForHomestay(dto.getHomestayId()) : senderId;
//
//        ChatSession session = getOrCreateSession(customerId, hostId);
//
//        // 2. Kiểm tra quyền (Security Check)
//        if (!session.getHostId().equals(senderId) && !session.getCustomerId().equals(senderId)) {
//            throw new AccessForbiddenException("Sender is not authorized for this chat session.");
//        }
//
//        // 3. Tạo Entity tin nhắn và lưu
//        ChatMessage message = chatMapper.toEntity(dto);
//        message.setSessionId(session.getId());
//        message.setSenderId(senderId);
//        message.setCreatedAt(LocalDateTime.now());
//
//        ChatMessage savedMessage = chatMessageRepository.save(message);
//
//        // 4. Cập nhật trạng thái Session (last_message_at)
//        session.setLastMessageAt(savedMessage.getCreatedAt());
//        chatSessionRepository.save(session);
//
//        return chatMapper.toDto(savedMessage);
//    }
//
//    @Override
//    public Long getRecipientId(Long sessionId, Long senderId) {
//        ChatSession session = chatSessionRepository.findById(sessionId)
//                .orElseThrow(() -> new IllegalArgumentException("Chat session not found."));
//
//        if (session.getHostId().equals(senderId)) {
//            return session.getCustomerId();
//        } else if (session.getCustomerId().equals(senderId)) {
//            return session.getHostId();
//        }
//        throw new AccessForbiddenException("Sender is not part of this session.");
//    }
//
//    @Override
//    @Transactional
//    public List<ChatMessageDto> getMessageHistory(Long sessionId, Long userId) {
//        // 1. Kiểm tra User có thuộc Session không
//        ChatSession session = chatSessionRepository.findById(sessionId)
//                .orElseThrow(() -> new IllegalArgumentException("Session not found."));
//
//        if (!session.getHostId().equals(userId) && !session.getCustomerId().equals(userId)) {
//            throw new AccessForbiddenException("You are not authorized to view this chat history.");
//        }
//
//        // 2. Đánh dấu tin nhắn là đã đọc
//        chatMessageRepository.markMessagesAsRead(sessionId, userId);
//
//        // 3. Trả về lịch sử
//        return chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId)
//                .stream()
//                .map(chatMapper::toDto)
//                .toList();
//    }
//
//    // ... các hàm khác (getSessionsForUser, etc.)
//}
