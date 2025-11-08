package hbm.homestayservice.service;

import hbm.homestayservice.dto.CreateHomestayRequest;
import hbm.homestayservice.dto.HomestayDTO;
import hbm.homestayservice.dto.HomestayImageDTO;
import hbm.homestayservice.dto.HomestayPendingDTO;
import hbm.homestayservice.dto.UpdateHomestayRequest;
import hbm.homestayservice.dto.UpdateHomestayStatusRequest;
import hbm.homestayservice.entity.Homestay;
import hbm.homestayservice.entity.HomestayImage;
import hbm.homestayservice.entity.HomestayPending;
import hbm.homestayservice.repository.HomestayImageRepository;
import hbm.homestayservice.repository.HomestayPendingRepository;
import hbm.homestayservice.repository.HomestayRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HomestayService {
    
    @Autowired
    private HomestayRepository homestayRepository;
    
    @Autowired
    private HomestayPendingRepository homestayPendingRepository;
    
    @Autowired
    private HomestayImageRepository homestayImageRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * Lấy danh sách homestay công khai với các bộ lọc
     */
    public List<HomestayDTO> getPublicHomestays(String city, Short capacity, LocalDate checkIn, LocalDate checkOut) {
        try {
            List<Homestay> homestays = homestayRepository.findPublicHomestaysWithFilters(city, capacity, checkIn, checkOut);
            return homestays.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Fallback to simple query if complex query fails
            System.err.println("Complex query failed, using fallback: " + e.getMessage());
            e.printStackTrace();
            List<Homestay> homestays = homestayRepository.findAllPublicSimple();
            return homestays.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
    }
    
    /**
     * Tạo homestay mới với status = 1 (chờ duyệt)
     */
    @Transactional
    public HomestayDTO createHomestay(CreateHomestayRequest request) {
        // Validate dữ liệu
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên homestay không được để trống");
        }
        
        if (request.getBasePrice() == null || request.getBasePrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá phải lớn hơn 0");
        }
        
        // Tạo entity mới
        Homestay homestay = new Homestay();
        homestay.setUserId(request.getUserId());
        homestay.setName(request.getName());
        homestay.setDescription(request.getDescription());
        homestay.setAddress(request.getAddress());
        homestay.setCity(request.getCity());
        homestay.setLat(request.getLat());
        homestay.setLongitude(request.getLongitude());
        homestay.setCapacity(request.getCapacity() != null ? request.getCapacity() : 2);
        homestay.setNumRooms(request.getNumRooms() != null ? request.getNumRooms() : 1);
        homestay.setBathroomCount(request.getBathroomCount() != null ? request.getBathroomCount() : 1);
        homestay.setBasePrice(request.getBasePrice());
        homestay.setAmenities(request.getAmenities());
        
        // Set status = 1 (chờ duyệt)
        homestay.setStatus((byte) 1);
        
        // Không set created_at, updated_at, is_deleted
        // Các field này sẽ được database tự động set với DEFAULT values
        // Không set approved_by và approved_at (để null)
        homestay.setApprovedBy(null);
        homestay.setApprovedAt(null);
        
        // Lưu vào database
        Homestay savedHomestay = homestayRepository.save(homestay);
        
        // Lưu ảnh nếu có
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (CreateHomestayRequest.ImageRequest imageReq : request.getImages()) {
                HomestayImage image = new HomestayImage();
                image.setHomestayId(savedHomestay.getId());
                image.setUrl(imageReq.getUrl());
                image.setAlt(imageReq.getAlt());
                image.setIsPrimary(imageReq.getIsPrimary() != null ? imageReq.getIsPrimary() : false);
                homestayImageRepository.save(image);
            }
        }
        
        return convertToDTO(savedHomestay);
    }
    
    /**
     * Lấy danh sách homestay của chủ nhà hiện tại
     */
    public List<HomestayDTO> getMyHomestays(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID không được để trống");
        }
        
        List<Homestay> homestays = homestayRepository.findByUserId(userId);
        return homestays.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Chuyển đổi trạng thái homestay (chỉ chủ nhà mới được thay đổi homestay của mình)
     * Status: 2 = công khai, 3 = tạm ẩn, 4 = bị khóa
     */
    @Transactional
    public HomestayDTO updateHomestayStatus(Long homestayId, Long userId, UpdateHomestayStatusRequest request) {
        // Validate
        if (homestayId == null) {
            throw new IllegalArgumentException("Homestay ID không được để trống");
        }
        
        if (userId == null) {
            throw new IllegalArgumentException("User ID không được để trống");
        }
        
        if (request.getStatus() == null) {
            throw new IllegalArgumentException("Status không được để trống");
        }
        
        // Kiểm tra status hợp lệ (2, 3, 4)
        if (request.getStatus() < 2 || request.getStatus() > 4) {
            throw new IllegalArgumentException("Status không hợp lệ. Chỉ cho phép: 2 (công khai), 3 (tạm ẩn), 4 (bị khóa)");
        }
        
        // Tìm homestay
        Homestay homestay = homestayRepository.findById(homestayId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy homestay"));
        
        // Kiểm tra quyền sở hữu
        if (!homestay.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Bạn không có quyền chỉnh sửa homestay này");
        }
        
        // Kiểm tra homestay đã bị xóa chưa (null-safe check)
        if (Boolean.TRUE.equals(homestay.getIsDeleted())) {
            throw new IllegalArgumentException("Homestay đã bị xóa");
        }
        
        // Cập nhật status
        homestay.setStatus(request.getStatus());
        
        // Lưu thay đổi
        Homestay updatedHomestay = homestayRepository.save(homestay);
        
        return convertToDTO(updatedHomestay);
    }
    
    /**
     * Chuyển đổi Entity sang DTO
     */
    private HomestayDTO convertToDTO(Homestay homestay) {
        HomestayDTO dto = new HomestayDTO();
        dto.setId(homestay.getId());
        dto.setUserId(homestay.getUserId());
        dto.setName(homestay.getName());
        dto.setDescription(homestay.getDescription());
        dto.setAddress(homestay.getAddress());
        dto.setCity(homestay.getCity());
        dto.setLat(homestay.getLat());
        dto.setLongitude(homestay.getLongitude());
        dto.setCapacity(homestay.getCapacity());
        dto.setNumRooms(homestay.getNumRooms());
        dto.setBathroomCount(homestay.getBathroomCount());
        dto.setBasePrice(homestay.getBasePrice());
        dto.setAmenities(homestay.getAmenities());
        dto.setStatus(homestay.getStatus());
        dto.setCreatedAt(homestay.getCreatedAt());
        dto.setUpdatedAt(homestay.getUpdatedAt());
        
        // Lấy danh sách ảnh
        List<HomestayImage> images = homestayImageRepository.findByHomestayIdOrderByIsPrimaryDesc(homestay.getId());
        List<HomestayImageDTO> imageDTOs = images.stream()
                .map(this::convertImageToDTO)
                .collect(Collectors.toList());
        dto.setImages(imageDTOs);
        
        return dto;
    }
    
    /**
     * Chuyển đổi HomestayImage entity sang DTO
     */
    private HomestayImageDTO convertImageToDTO(HomestayImage image) {
        HomestayImageDTO dto = new HomestayImageDTO();
        dto.setId(image.getId());
        dto.setUrl(image.getUrl());
        dto.setAlt(image.getAlt());
        dto.setIsPrimary(image.getIsPrimary());
        dto.setCreatedAt(image.getCreatedAt());
        return dto;
    }
    
    /**
     * Host tạo yêu cầu cập nhật thông tin homestay
     * Tạo bản ghi trong homestay_pending với status = 'waiting'
     */
    @Transactional
    public HomestayPendingDTO requestUpdateHomestay(Long homestayId, Long userId, UpdateHomestayRequest request) {
        // Validate homestayId và userId
        if (homestayId == null || homestayId <= 0) {
            throw new IllegalArgumentException("ID homestay không hợp lệ");
        }
        
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("ID người dùng không hợp lệ");
        }
        
        // Tìm homestay
        Homestay homestay = homestayRepository.findById(homestayId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy homestay với ID: " + homestayId));
        
        // Kiểm tra người dùng có phải chủ homestay không
        if (!homestay.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Bạn không có quyền cập nhật homestay này");
        }
        
        // Kiểm tra homestay có bị xóa không
        if (Boolean.TRUE.equals(homestay.getIsDeleted())) {
            throw new IllegalArgumentException("Homestay đã bị xóa");
        }
        
        // Kiểm tra homestay có bị khóa không (status = 4)
        if (homestay.getStatus() != null && homestay.getStatus() == 4) {
            throw new IllegalArgumentException("Homestay đang bị khóa, không thể cập nhật");
        }
        
        // Chuyển request thành JSON
        String pendingDataJson;
        try {
            pendingDataJson = objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi khi chuyển đổi dữ liệu thành JSON", e);
        }
        
        // Tạo bản ghi homestay_pending
        HomestayPending pending = new HomestayPending();
        pending.setHomestayId(homestayId);
        pending.setPendingData(pendingDataJson);
        pending.setStatus("waiting");
        
        HomestayPending saved = homestayPendingRepository.save(pending);
        
        // Chuyển đổi sang DTO
        return convertPendingToDTO(saved);
    }
    
    /**
     * Chuyển đổi HomestayPending entity sang DTO
     */
    private HomestayPendingDTO convertPendingToDTO(HomestayPending pending) {
        HomestayPendingDTO dto = new HomestayPendingDTO();
        dto.setId(pending.getId());
        dto.setHomestayId(pending.getHomestayId());
        dto.setPendingData(pending.getPendingData());
        dto.setSubmittedAt(pending.getSubmittedAt());
        dto.setStatus(pending.getStatus());
        dto.setReviewedBy(pending.getReviewedBy());
        dto.setReviewedAt(pending.getReviewedAt());
        dto.setReason(pending.getReason());
        return dto;
    }
}
