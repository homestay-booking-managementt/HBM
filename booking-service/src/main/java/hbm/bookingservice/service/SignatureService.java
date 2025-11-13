package hbm.bookingservice.service;

import java.util.Map;

public interface SignatureService {

    boolean isValidSignature(Map<String, Object> payload, String secretKey, String accessKey);
}
