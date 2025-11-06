package hbm.homestayservice.service;

import hbm.homestayservice.dto.CreateHomestayRequest;
import hbm.homestayservice.dto.HomestayDTO;
import hbm.homestayservice.entity.Homestay;
import hbm.homestayservice.repository.HomestayRepository;
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
    
    /**
     * Lấy danh sách homestay công khai với các bộ lọc
     */
    public List<HomestayDTO> getPublicHomestays(String city, Short capacity, LocalDate checkIn, LocalDate checkOut) {
        List<Homestay> homestays = homestayRepository.findPublicHomestaysWithFilters(city, capacity, checkIn, checkOut);
        return homestays.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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
        
        return convertToDTO(savedHomestay);
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
        return dto;
    }
}
