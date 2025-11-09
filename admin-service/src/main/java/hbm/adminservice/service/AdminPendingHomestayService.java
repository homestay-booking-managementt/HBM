package hbm.adminservice.service;

import hbm.adminservice.dto.HomestayDTO;
import hbm.adminservice.dto.HomestayImageDTO;
import hbm.adminservice.entity.Homestay;
import hbm.adminservice.entity.HomestayImage;
import hbm.adminservice.repository.HomestayImageRepository;
import hbm.adminservice.repository.HomestayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminPendingHomestayService {
    
    @Autowired
    private HomestayRepository homestayRepository;
    
    @Autowired
    private HomestayImageRepository homestayImageRepository;
    
    /**
     * Lấy danh sách homestay chờ duyệt (status = 1)
     */
    public List<HomestayDTO> getPendingHomestays() {
        List<Homestay> homestays = homestayRepository.findPendingHomestays();
        return homestays.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Chuyển đổi Entity sang DTO (bao gồm ảnh)
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
}
