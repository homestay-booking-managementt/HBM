package hbm.bookingservice.client;

import hbm.bookingservice.dto.payment.CreateMomoRequest;
import hbm.bookingservice.dto.payment.CreateMomoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "momo", url = "${momo.endpoint}")
public interface MomoClient {

    @PostMapping("/create")
    CreateMomoResponse createMomoQR(CreateMomoRequest request);
}
