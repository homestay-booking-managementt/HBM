package hbm.homestayservice.service;

import hbm.homestayservice.dto.HomestayDTO;
import hbm.homestayservice.entity.Homestay;
import hbm.homestayservice.repository.HomestayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
