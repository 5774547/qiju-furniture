package com.qiju.furniture.module.contact.controller;

import com.qiju.furniture.common.result.Result;
import com.qiju.furniture.module.contact.dto.ContactSubmitDTO;
import com.qiju.furniture.module.contact.entity.Contact;
import com.qiju.furniture.module.contact.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 咨询Controller
 */
@Tag(name = "咨询管理")
@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @Operation(summary = "提交咨询")
    @PostMapping
    public Result<Void> submit(@Valid @RequestBody ContactSubmitDTO dto) {
        Contact contact = new Contact();
        contact.setName(dto.getName());
        contact.setEmail(dto.getEmail());
        contact.setPhone(dto.getPhone());
        contact.setMessage(dto.getMessage());
        contactService.save(contact);
        return Result.ok();
    }
}
