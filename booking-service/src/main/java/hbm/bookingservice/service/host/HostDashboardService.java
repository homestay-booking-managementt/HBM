package hbm.bookingservice.service.host;

import hbm.bookingservice.dto.host.HostStatisticsDto;
import hbm.bookingservice.dto.host.RevenueStatisticsDto;
import hbm.bookingservice.dto.host.TopHomestayDto;

import java.util.List;

public interface HostDashboardService {
    
    /**
     * Lấy thống kê tổng quan cho host
     */
    HostStatisticsDto getHostStatistics(Long hostId);
    
    /**
     * Lấy thống kê doanh thu theo period
     */
    RevenueStatisticsDto getRevenueStatistics(Long hostId, String period);
    
    /**
     * Lấy top homestays theo doanh thu
     */
    List<TopHomestayDto> getTopHomestays(Long hostId, int limit);
}
