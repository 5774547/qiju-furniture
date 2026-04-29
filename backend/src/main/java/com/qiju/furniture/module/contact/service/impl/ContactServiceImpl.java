package com.qiju.furniture.module.contact.service.impl;

import com.qiju.furniture.module.contact.entity.Contact;
import com.qiju.furniture.module.contact.mapper.ContactMapper;
import com.qiju.furniture.module.contact.service.ContactService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 咨询Service实现
 */
@Service
public class ContactServiceImpl extends ServiceImpl<ContactMapper, Contact> implements ContactService {
}
