package hbm.authservice.controller;

import hbm.authservice.entity.User;
import hbm.authservice.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping("/my-profile")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public User myProfile() {
        return userService.myProfile();
    }
}
